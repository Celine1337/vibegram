package app.vibegram.tools

import org.json.JSONArray
import org.json.JSONObject
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.Utilities
import java.io.File
import java.util.concurrent.ConcurrentHashMap

/**
 * Local per-chat private notes scoped by Telegram account and dialog ID.
 * Cached in memory with asynchronous atomic disk writes.
 */
object ChatNotesManager {

    private const val FILE_NAME = "vibegram_chat_notes.json"
    private val loadLock = Any()

    @Volatile
    private var loaded = false
    private val notes = ConcurrentHashMap<String, ChatNote>()

    data class ChatNote(
        @JvmField val account: Int,
        @JvmField val dialogId: Long,
        @JvmField val chatTitle: String,
        @JvmField val text: String,
        @JvmField val updatedAt: Long,
    )

    private fun key(account: Int, dialogId: Long): String = "$account:$dialogId"

    private fun storageFile(): File? {
        val ctx = ApplicationLoader.applicationContext ?: return null
        return File(ctx.filesDir, FILE_NAME)
    }

    private fun ensureLoaded() {
        if (loaded) return
        synchronized(loadLock) {
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
                    val account = obj.optInt("account", 0)
                    val dialogId = obj.optLong("dialogId", 0L)
                    val text = obj.optString("text")
                    if (dialogId == 0L || text.isBlank()) continue
                    notes[key(account, dialogId)] = ChatNote(
                        account = account,
                        dialogId = dialogId,
                        chatTitle = obj.optString("chatTitle"),
                        text = text,
                        updatedAt = obj.optLong("updatedAt", 0L),
                    )
                }
            } catch (e: Exception) {
                FileLog.e(e)
            }
        }
    }

    private fun persistAsync() {
        val snapshot = ArrayList(notes.values)
        Utilities.globalQueue.postRunnable {
            val file = storageFile() ?: return@postRunnable
            try {
                val array = JSONArray()
                for (note in snapshot) {
                    val obj = JSONObject()
                    obj.put("account", note.account)
                    obj.put("dialogId", note.dialogId)
                    obj.put("chatTitle", note.chatTitle)
                    obj.put("text", note.text)
                    obj.put("updatedAt", note.updatedAt)
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
    fun hasNote(account: Int, dialogId: Long): Boolean {
        if (dialogId == 0L) return false
        ensureLoaded()
        return notes.containsKey(key(account, dialogId))
    }

    @JvmStatic
    fun getNote(account: Int, dialogId: Long): ChatNote? {
        if (dialogId == 0L) return null
        ensureLoaded()
        return notes[key(account, dialogId)]
    }

    @JvmStatic
    fun saveNote(account: Int, dialogId: Long, chatTitle: String?, rawText: String?): Boolean {
        if (dialogId == 0L) return false
        ensureLoaded()
        val k = key(account, dialogId)
        val trimmed = rawText?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            val removed = notes.remove(k) != null
            if (removed) persistAsync()
            return false
        }
        val title = chatTitle?.trim().takeUnless { it.isNullOrEmpty() } ?: dialogId.toString()
        notes[k] = ChatNote(
            account = account,
            dialogId = dialogId,
            chatTitle = title,
            text = trimmed,
            updatedAt = System.currentTimeMillis(),
        )
        persistAsync()
        return true
    }

    @JvmStatic
    fun deleteNote(account: Int, dialogId: Long): Boolean {
        ensureLoaded()
        val removed = notes.remove(key(account, dialogId)) != null
        if (removed) persistAsync()
        return removed
    }

    @JvmStatic
    fun count(account: Int): Int {
        ensureLoaded()
        var c = 0
        for (note in notes.values) {
            if (note.account == account) c++
        }
        return c
    }

    @JvmStatic
    fun list(account: Int): List<ChatNote> {
        ensureLoaded()
        return notes.values
            .filter { it.account == account }
            .sortedByDescending { it.updatedAt }
    }
}
