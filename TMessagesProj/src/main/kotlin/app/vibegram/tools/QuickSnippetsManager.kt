package app.vibegram.tools

import org.json.JSONArray
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.Utilities
import java.io.File
import java.util.Locale
import java.util.UUID

/**
 * Stores and retrieves reusable quick-reply message snippets in local JSON storage.
 */
object QuickSnippetsManager {

    private const val FILE_NAME = "vibegram_quick_snippets.json"
    private val lock = Any()

    @Volatile
    private var loaded = false
    private val items = ArrayList<Snippet>()

    data class Snippet(
        @JvmField val id: String,
        @JvmField val title: String,
        @JvmField val text: String,
        @JvmField val usageCount: Int,
        @JvmField val updatedAt: Long,
    )

    private fun storageFile(): File? {
        val ctx = ApplicationLoader.applicationContext ?: return null
        return File(ctx.filesDir, FILE_NAME)
    }

    private fun ensureLoadedLocked() {
        if (loaded) return
        loaded = true
        val file = storageFile() ?: return
        if (!file.exists()) return
        try {
            val raw = file.readText(Charsets.UTF_8)
            if (raw.isBlank()) return
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optString("id").ifEmpty { UUID.randomUUID().toString() }
                val title = obj.optString("title").trim()
                val text = obj.optString("text")
                if (title.isEmpty() || text.isEmpty()) continue
                items.add(
                    Snippet(
                        id = id,
                        title = title,
                        text = text,
                        usageCount = obj.optInt("usageCount", 0),
                        updatedAt = obj.optLong("updatedAt", 0L),
                    )
                )
            }
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    private fun schedulePersistLocked() {
        val snapshot = ArrayList(items)
        Utilities.globalQueue.postRunnable {
            val file = storageFile() ?: return@postRunnable
            try {
                val array = JSONArray()
                for (item in snapshot) {
                    val obj = JSONObject()
                    obj.put("id", item.id)
                    obj.put("title", item.title)
                    obj.put("text", item.text)
                    obj.put("usageCount", item.usageCount)
                    obj.put("updatedAt", item.updatedAt)
                    array.put(obj)
                }
                val tmp = File(file.parentFile, "$FILE_NAME.tmp")
                tmp.writeText(array.toString(), Charsets.UTF_8)
                if (!tmp.renameTo(file)) {
                    file.writeText(array.toString(), Charsets.UTF_8)
                    tmp.delete()
                }
            } catch (e: Exception) {
                FileLog.e(e)
            }
        }
    }

    @JvmStatic
    fun count(): Int {
        synchronized(lock) {
            ensureLoadedLocked()
            return items.size
        }
    }

    @JvmStatic
    fun list(query: String?): List<Snippet> {
        val normalizedQuery = query?.trim()?.lowercase(Locale.ROOT).orEmpty()
        val copy: List<Snippet>
        synchronized(lock) {
            ensureLoadedLocked()
            copy = ArrayList(items)
        }
        val filtered = if (normalizedQuery.isEmpty()) {
            copy
        } else {
            copy.filter {
                it.title.lowercase(Locale.ROOT).contains(normalizedQuery) ||
                    it.text.lowercase(Locale.ROOT).contains(normalizedQuery)
            }
        }
        return filtered.sortedWith(
            compareByDescending<Snippet> { it.usageCount }
                .thenByDescending { it.updatedAt }
                .thenBy { it.title.lowercase(Locale.ROOT) }
        )
    }

    @JvmStatic
    fun upsert(existingId: String?, rawTitle: String, rawText: String): Snippet? {
        val title = rawTitle.trim()
        val text = rawText.trim()
        if (title.isEmpty() || text.isEmpty()) return null
        val now = System.currentTimeMillis()
        synchronized(lock) {
            ensureLoadedLocked()
            if (!existingId.isNullOrEmpty()) {
                val idx = items.indexOfFirst { it.id == existingId }
                if (idx >= 0) {
                    val prev = items[idx]
                    val updated = prev.copy(title = title, text = text, updatedAt = now)
                    items[idx] = updated
                    schedulePersistLocked()
                    return updated
                }
            }
            val created = Snippet(
                id = UUID.randomUUID().toString(),
                title = title,
                text = text,
                usageCount = 0,
                updatedAt = now,
            )
            items.add(created)
            schedulePersistLocked()
            return created
        }
    }

    @JvmStatic
    fun markUsed(id: String) {
        synchronized(lock) {
            ensureLoadedLocked()
            val idx = items.indexOfFirst { it.id == id }
            if (idx >= 0) {
                val prev = items[idx]
                items[idx] = prev.copy(
                    usageCount = prev.usageCount + 1,
                    updatedAt = System.currentTimeMillis(),
                )
                schedulePersistLocked()
            }
        }
    }

    @JvmStatic
    fun delete(id: String): Boolean {
        synchronized(lock) {
            ensureLoadedLocked()
            val removed = items.removeAll { it.id == id }
            if (removed) {
                schedulePersistLocked()
            }
            return removed
        }
    }
}
