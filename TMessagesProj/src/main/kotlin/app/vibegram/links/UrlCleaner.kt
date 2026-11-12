package app.vibegram.links

import java.util.Locale

class UrlCleaner internal constructor(private val rules: List<AdGuardRule>) {

    private val domainBuckets: Map<String, List<AdGuardRule>>
    private val globalRules: List<AdGuardRule>

    init {
        val buckets = HashMap<String, MutableList<AdGuardRule>>()
        val globals = ArrayList<AdGuardRule>()
        for (rule in rules) {
            val matcher = rule.urlMatcher
            if (matcher is UrlMatcher.Host && matcher.hostRegex == null) {
                buckets.getOrPut(matcher.host.lowercase(Locale.ROOT)) { ArrayList(2) }.add(rule)
            } else if (rule.domainInclude.isNotEmpty()) {
                for (domain in rule.domainInclude) {
                    buckets.getOrPut(domain.lowercase(Locale.ROOT)) { ArrayList(2) }.add(rule)
                }
            } else {
                globals.add(rule)
            }
        }
        domainBuckets = buckets
        globalRules = globals
    }

    fun clean(url: String): String {
        if (url.indexOf('?') < 0) return url
        val parsed = ParsedUrl.parse(url) ?: return url

        val host = parsed.host.lowercase(Locale.ROOT)
        val pathAndQuery = parsed.pathAndQuery
        val applicable = collectApplicable(host, pathAndQuery, url) ?: return url

        var hasRemoval = false
        for (rule in applicable) {
            if (rule.exception) {
                if (rule.paramMatcher is ParamMatcher.All) return url
            } else {
                hasRemoval = true
            }
        }
        if (!hasRemoval) return url

        var droppedAny = false
        var stripDelim = false
        for (rule in applicable) {
            if (!rule.exception && rule.paramMatcher is ParamMatcher.All) {
                stripDelim = true
                break
            }
        }

        val kept = ArrayList<Pair<String, String?>>(parsed.params.size)
        for (param in parsed.params) {
            val (k, v) = param
            var shouldDrop = false
            for (rule in applicable) {
                if (!rule.exception && rule.paramMatcher.matches(k, v)) {
                    shouldDrop = true
                    break
                }
            }
            if (shouldDrop) {
                for (rule in applicable) {
                    if (rule.exception && rule.paramMatcher.matches(k, v)) {
                        shouldDrop = false
                        break
                    }
                }
            }
            if (shouldDrop) {
                droppedAny = true
            } else {
                kept.add(param)
            }
        }

        val stripEmptyQuery = parsed.hadQuery && kept.isEmpty() && stripDelim
        if (!droppedAny && !stripEmptyQuery) return url
        return parsed.rebuild(kept)
    }

    private fun collectApplicable(host: String, pathAndQuery: String, url: String): List<AdGuardRule>? {
        var matched: ArrayList<AdGuardRule>? = null
        for (rule in globalRules) {
            if (rule.matchesUrl(host, pathAndQuery, url)) {
                if (matched == null) matched = ArrayList(4)
                matched.add(rule)
            }
        }
        if (domainBuckets.isNotEmpty()) {
            var idx = 0
            while (idx >= 0 && idx < host.length) {
                val sub = if (idx == 0) host else host.substring(idx)
                val bucket = domainBuckets[sub]
                if (bucket != null) {
                    for (rule in bucket) {
                        if (rule.matchesUrl(host, pathAndQuery, url)) {
                            if (matched == null) matched = ArrayList(4)
                            if (matched.isEmpty() || !matched.contains(rule)) {
                                matched.add(rule)
                            }
                        }
                    }
                }
                val dot = host.indexOf('.', idx)
                idx = if (dot < 0) -1 else dot + 1
            }
        }
        return matched
    }

    companion object {
        fun fromAdGuardFilter(text: String): UrlCleaner {
            val rules = text.lineSequence().mapNotNull(AdGuardRuleParser::parse).toList()
            return UrlCleaner(rules)
        }
    }
}

private fun AdGuardRule.matchesUrl(host: String, pathAndQuery: String, fullUrl: String): Boolean {
    if (domainInclude.isNotEmpty() && domainInclude.none { hostMatches(host, it) }) return false
    if (domainExclude.any { hostMatches(host, it) }) return false
    return when (val m = urlMatcher) {
        is UrlMatcher.Any -> true
        is UrlMatcher.FullRegex -> m.regex.containsMatchIn(fullUrl)
        is UrlMatcher.Host -> {
            val hostOk = m.hostRegex?.matches(host) ?: hostMatches(host, m.host)
            hostOk && (m.pathRegex == null || m.pathRegex.containsMatchIn(pathAndQuery))
        }
    }
}

private fun hostMatches(host: String, rule: String): Boolean = host == rule || host.endsWith(".$rule")

internal class ParsedUrl private constructor(
    val host: String,
    val params: List<Pair<String, String?>>,
    val hadQuery: Boolean,
    private val scheme: String,
    private val authority: String,
    private val path: String,
    private val fragment: String?,
) {
    val pathAndQuery: String
        get() = if (!hadQuery) path else "$path?${rebuildQuery(params)}"

    fun rebuild(keptParams: List<Pair<String, String?>>): String = buildString {
        append(scheme).append("://").append(authority).append(path)
        if (keptParams.isNotEmpty()) append('?').append(rebuildQuery(keptParams))
        if (fragment != null) append('#').append(fragment)
    }

    companion object {
        fun parse(url: String): ParsedUrl? {
            val schemeEnd = url.indexOf("://")
            if (schemeEnd <= 0) return null
            val scheme = url.substring(0, schemeEnd)
            val afterScheme = url.substring(schemeEnd + 3)

            val fragIdx = afterScheme.indexOf('#')
            val beforeFrag = if (fragIdx < 0) afterScheme else afterScheme.substring(0, fragIdx)
            val fragment = if (fragIdx < 0) null else afterScheme.substring(fragIdx + 1)

            val qIdx = beforeFrag.indexOf('?')
            val authAndPath = if (qIdx < 0) beforeFrag else beforeFrag.substring(0, qIdx)
            val query = if (qIdx < 0) "" else beforeFrag.substring(qIdx + 1)

            val pathStart = authAndPath.indexOf('/')
            val authority = if (pathStart < 0) authAndPath else authAndPath.substring(0, pathStart)
            val path = if (pathStart < 0) "" else authAndPath.substring(pathStart)

            val hostPort = authority.substringAfterLast('@')
            val host = hostPort.substringBefore(':')
            if (host.isEmpty()) return null

            return ParsedUrl(
                host = host,
                params = if (query.isEmpty()) emptyList() else parseQuery(query),
                hadQuery = qIdx >= 0,
                scheme = scheme,
                authority = authority,
                path = path,
                fragment = fragment,
            )
        }

        private fun parseQuery(q: String): List<Pair<String, String?>> =
            q.split('&').mapNotNull { piece ->
                if (piece.isEmpty()) return@mapNotNull null
                val eq = piece.indexOf('=')
                if (eq < 0) piece to null
                else piece.substring(0, eq) to piece.substring(eq + 1)
            }

        private fun rebuildQuery(params: List<Pair<String, String?>>): String =
            params.joinToString("&") { (k, v) -> if (v == null) k else "$k=$v" }
    }
}
