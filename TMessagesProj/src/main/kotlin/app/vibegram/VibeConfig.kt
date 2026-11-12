package app.vibegram

import android.content.SharedPreferences
import app.vibegram.config.ConfigStore
import app.vibegram.proxy.ProxyDisableCondition
import tw.nekomimi.nekogram.NekoConfig
import tw.nekomimi.nekogram.config.ConfigItem

/**
 * Основные настройки VibeGram.
 */
object VibeConfig {

    private const val LEGACY_PROXY_VPN_KEY = "DisableProxyWhenVpnEnabled"

    private val sync = Any()
    private val configs = ArrayList<ConfigItem>()

    @JvmStatic
    fun getConfigTypes(): Map<String, Int> = ConfigStore.getTypes(configs)

    @Volatile
    private var configLoaded = false

    @JvmStatic
    fun getPreferences(): SharedPreferences = NekoConfig.getPreferences()

    // ---- Форматирование ----

    /** Относительное «был(а) в сети»: «5 минут назад» вместо времени. */
    @JvmField
    val relativeLastSeen = addConfig("VGRelativeLastSeen", ConfigItem.configTypeBool, false)

    // ---- Чаты ----

    /** Запятая после упоминания: «@user, » вместо «@user ». */
    @JvmField
    val addCommaAfterMention = addConfig("VGAddCommaAfterMention", ConfigItem.configTypeBool, true)

    // ---- Эмодзи ----

    /**
     * Загружать список эмодзи-паков из канала метаданных NagramX.
     *
     * Свой канал метаданных у нас не заведён, а паки живут в чужом, поэтому
     * по умолчанию выключено: без спроса клиент в чужой канал не ходит.
     */
    @JvmField
    val downloadEmojiPacks = addConfig("VGDownloadEmojiPacks", ConfigItem.configTypeBool, false)

    // ---- Статические геттеры для вызова из Java в горячих местах ----

    @JvmStatic
    fun downloadEmojiPacks(): Boolean = downloadEmojiPacks.Bool()

    @JvmStatic
    fun relativeLastSeen(): Boolean = relativeLastSeen.Bool()

    @JvmStatic
    fun addCommaAfterMention(): Boolean = addCommaAfterMention.Bool()

    // ---- Производительность и инструменты VibeGram ----

    /** Автоматическая очистка кэша при превышении порога в МБ (0 = выкл, 512 / 1024 / 2048). */
    @JvmField
    val autoTrimCacheMb = addConfig("VGAutoTrimCacheMb", ConfigItem.configTypeInt, 0)

    /** Показывать быстрые шаблоны ответов и заметки в меню чата. */
    @JvmField
    val showQuickToolsInChatMenu = addConfig("VGShowQuickToolsInChatMenu", ConfigItem.configTypeBool, true)

    /** Повышенная плавность: отключает лишнюю нагрузку при быстрой прокрутке и анимациях. */
    @JvmField
    val optimizeScrollCache = addConfig("VGOptimizeScrollCache", ConfigItem.configTypeBool, true)

    @JvmStatic
    fun autoTrimCacheMb(): Int = autoTrimCacheMb.Int()

    @JvmStatic
    fun showQuickToolsInChatMenu(): Boolean = showQuickToolsInChatMenu.Bool()

    @JvmStatic
    fun optimizeScrollCache(): Boolean = optimizeScrollCache.Bool()

    // ---- Прокси ----

    /**
     * Маска условий, при которых прокси выключается сам: [ProxyDisableCondition.flag].
     *
     * У NagramX на этом месте был булев `DisableProxyWhenVpnEnabled`; его значение
     * переносится в бит VPN в [migrateProxyConditions].
     */
    @JvmField
    val proxyDisableConditions = addConfig("VGProxyDisableConditions", ConfigItem.configTypeInt, 0)

    @JvmStatic
    fun isProxyDisabledOn(condition: ProxyDisableCondition): Boolean =
        (proxyDisableConditions.Int() and condition.flag) != 0

    @JvmStatic
    fun setProxyDisabledOn(condition: ProxyDisableCondition, disabled: Boolean) {
        val current = proxyDisableConditions.Int()
        val updated = if (disabled) current or condition.flag else current and condition.flag.inv()
        if (updated != current) {
            proxyDisableConditions.setConfigInt(updated)
        }
    }

    @JvmStatic
    fun hasProxyDisableConditions(): Boolean = proxyDisableConditions.Int() != 0

    private fun migrateProxyConditions(preferences: SharedPreferences) {
        if (!preferences.contains(LEGACY_PROXY_VPN_KEY)) return
        if (preferences.getBoolean(LEGACY_PROXY_VPN_KEY, false)) {
            val migrated = proxyDisableConditions.Int() or ProxyDisableCondition.VPN.flag
            proxyDisableConditions.value = migrated
            preferences.edit().putInt(proxyDisableConditions.key, migrated).apply()
        }
        preferences.edit().remove(LEGACY_PROXY_VPN_KEY).apply()
    }

    private fun addConfig(key: String, type: Int, defaultValue: Any?): ConfigItem {
        val item = ConfigItem(key, type, defaultValue)
        configs.add(item)
        return item
    }

    @JvmStatic
    fun init() {
        loadConfig(false)
    }

    @JvmStatic
    fun loadConfig(force: Boolean) {
        synchronized(sync) {
            if (configLoaded && !force) return
            val preferences = getPreferences()
            if (!ConfigStore.loadAll(configs, preferences)) return
            migrateProxyConditions(preferences)
            configLoaded = true
        }
    }

    /** Сбрасывает все настройки VibeGram к значениям по умолчанию. */
    @JvmStatic
    fun reset() {
        synchronized(sync) {
            ConfigStore.resetAll(configs, getPreferences())
        }
    }
}
