package app.vibegram.tools

import android.content.Context
import android.content.SharedPreferences
import app.vibegram.VibeConfig
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.ImageLoader
import org.telegram.messenger.Utilities
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Native-backed cache inspection, one-tap cleanup, RAM stats, and background auto-trim.
 */
object PerformanceOptimizer {

    private const val PREF_NAME = "vibegram_perf"
    private const val KEY_LAST_AUTO_TRIM = "last_auto_trim_ms"
    private val AUTO_TRIM_COOLDOWN_MS = TimeUnit.HOURS.toMillis(12)
    private val STARTUP_DELAY_MS = TimeUnit.SECONDS.toMillis(8)

    data class CacheStats(
        @JvmField val cacheAndTempBytes: Long,
        @JvmField val mediaBytes: Long,
        @JvmField val logsBytes: Long,
    ) {
        @JvmField
        val totalBytes: Long = cacheAndTempBytes + mediaBytes + logsBytes
    }

    data class MemoryStats(
        @JvmField val usedMb: Int,
        @JvmField val maxMb: Int,
    )

    fun interface StatsCallback {
        fun onResult(stats: CacheStats)
    }

    fun interface CleanCallback {
        fun onFinished(freedBytes: Long, remaining: CacheStats)
    }

    @JvmStatic
    fun readMemoryStats(): MemoryStats {
        val rt = Runtime.getRuntime()
        val usedBytes = (rt.totalMemory() - rt.freeMemory()).coerceAtLeast(0L)
        val maxBytes = rt.maxMemory().coerceAtLeast(1L)
        val mb = 1024L * 1024L
        return MemoryStats(
            usedMb = (usedBytes / mb).toInt(),
            maxMb = (maxBytes / mb).toInt(),
        )
    }

    @JvmStatic
    fun trimMemoryCaches() {
        try {
            ImageLoader.getInstance().clearMemory()
            System.gc()
        } catch (e: Throwable) {
            FileLog.e(e)
        }
    }

    @JvmStatic
    fun scanCacheAsync(callback: StatsCallback) {
        Utilities.cacheClearQueue.postRunnable {
            val stats = measureCacheBlocking()
            AndroidUtilities.runOnUIThread {
                callback.onResult(stats)
            }
        }
    }

    @JvmStatic
    fun cleanCacheAsync(callback: CleanCallback) {
        Utilities.cacheClearQueue.postRunnable {
            val before = measureCacheBlocking()
            val nowSec = System.currentTimeMillis() / 1000L + 60L
            clearTemporaryAndCacheDirs(nowSec)
            val after = measureCacheBlocking()
            val freed = (before.totalBytes - after.totalBytes).coerceAtLeast(0L)
            AndroidUtilities.runOnUIThread {
                try {
                    ImageLoader.getInstance().clearMemory()
                } catch (e: Throwable) {
                    FileLog.e(e)
                }
                callback.onFinished(freed, after)
            }
        }
    }

    @JvmStatic
    fun scheduleStartupAutoTrim() {
        val limitMb = VibeConfig.autoTrimCacheMb()
        if (limitMb <= 0) return
        val prefs = prefs() ?: return
        val now = System.currentTimeMillis()
        val last = prefs.getLong(KEY_LAST_AUTO_TRIM, 0L)
        if (Math.abs(now - last) < AUTO_TRIM_COOLDOWN_MS) return

        Utilities.cacheClearQueue.postRunnable({
            try {
                val thresholdBytes = limitMb.toLong() * 1024L * 1024L
                val current = measureCacheBlocking()
                prefs.edit().putLong(KEY_LAST_AUTO_TRIM, System.currentTimeMillis()).apply()
                if (current.cacheAndTempBytes + current.logsBytes > thresholdBytes) {
                    // Preserve very recent files (< 24h) while reclaiming older temp/cache & logs.
                    val cutoffSec = (System.currentTimeMillis() / 1000L) - TimeUnit.HOURS.toSeconds(24)
                    clearTemporaryAndCacheDirs(cutoffSec)
                }
            } catch (e: Throwable) {
                FileLog.e(e)
            }
        }, STARTUP_DELAY_MS)
    }

    private fun prefs(): SharedPreferences? {
        val ctx = ApplicationLoader.applicationContext ?: return null
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun measureCacheBlocking(): CacheStats {
        var cacheBytes = 0L
        var mediaBytes = 0L
        var logsBytes = 0L
        try {
            val cacheDir = FileLoader.checkDirectory(FileLoader.MEDIA_DIR_CACHE)
            if (cacheDir != null) {
                cacheBytes += safeDirSize(cacheDir, 5)
                cacheBytes += safeDirSize(cacheDir, 4)
                cacheBytes += safeDirSize(cacheDir, 3)
                cacheBytes += safeDirSize(File(cacheDir, "acache"), 0)
            }
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_IMAGE), 0)
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_IMAGE_PUBLIC), 0)
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_VIDEO), 0)
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_VIDEO_PUBLIC), 0)
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_DOCUMENT), 0)
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_FILES), 0)
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_AUDIO), 0)
            mediaBytes += safeDirSize(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_STORIES), 0)

            logsBytes += safeDirSize(AndroidUtilities.getLogsDir(), 1)
        } catch (e: Throwable) {
            FileLog.e(e)
        }
        return CacheStats(cacheBytes, mediaBytes, logsBytes)
    }

    private fun clearTemporaryAndCacheDirs(cutoffSec: Long) {
        try {
            val cacheDir = FileLoader.checkDirectory(FileLoader.MEDIA_DIR_CACHE)
            if (cacheDir != null) {
                safeClearDir(cacheDir, 4, cutoffSec)
                safeClearDir(cacheDir, 5, cutoffSec)
                safeClearDir(cacheDir, 3, cutoffSec)
                safeClearDir(File(cacheDir, "acache"), 0, cutoffSec)
            }
            safeClearDir(FileLoader.checkDirectory(FileLoader.MEDIA_DIR_STORIES), 0, cutoffSec)
            safeClearDir(AndroidUtilities.getLogsDir(), 0, cutoffSec)
        } catch (e: Throwable) {
            FileLog.e(e)
        }
    }

    private fun safeDirSize(dir: File?, docType: Int): Long {
        if (dir == null || !dir.exists()) return 0L
        return if (dir.isDirectory) {
            Utilities.getDirSize(dir.absolutePath, docType, false).coerceAtLeast(0L)
        } else if (dir.isFile) {
            dir.length().coerceAtLeast(0L)
        } else {
            0L
        }
    }

    private fun safeClearDir(dir: File?, docType: Int, cutoffSec: Long) {
        if (dir == null || !dir.exists() || !dir.isDirectory) return
        Utilities.clearDir(dir.absolutePath, docType, cutoffSec, false)
    }
}
