package app.vibegram.config

import android.content.SharedPreferences
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import tw.nekomimi.nekogram.config.ConfigItem

/**
 * Shared preference loader and reset helper for VibeGram config objects.
 */
internal object ConfigStore {

    fun getTypes(configs: List<ConfigItem>): Map<String, Int> =
        configs.associate { it.key to it.type }

    fun loadAll(configs: List<ConfigItem>, preferences: SharedPreferences): Boolean {
        if (ApplicationLoader.applicationContext == null) {
            return false
        }
        for (item in configs) {
            try {
                when (item.type) {
                    ConfigItem.configTypeBool ->
                        item.value = preferences.getBoolean(item.key, item.defaultValue as Boolean)

                    ConfigItem.configTypeInt ->
                        item.value = preferences.getInt(item.key, item.defaultValue as Int)

                    ConfigItem.configTypeLong ->
                        item.value = preferences.getLong(item.key, item.defaultValue as Long)

                    ConfigItem.configTypeFloat ->
                        item.value = preferences.getFloat(item.key, item.defaultValue as Float)

                    ConfigItem.configTypeString ->
                        item.value = preferences.getString(item.key, item.defaultValue as String?)
                }
            } catch (e: Exception) {
                FileLog.e(e)
            }
        }
        return true
    }

    fun resetAll(configs: List<ConfigItem>, preferences: SharedPreferences) {
        val editor = preferences.edit()
        for (item in configs) {
            editor.remove(item.key)
            item.value = item.defaultValue
        }
        editor.apply()
    }
}
