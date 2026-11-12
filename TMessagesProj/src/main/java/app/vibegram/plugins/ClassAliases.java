package app.vibegram.plugins;

import java.util.HashMap;
import java.util.Map;

public final class ClassAliases {

    public static final String ROOT = "com.exteragram.messenger";

    public static final String MEDIA_ROOT = "com.google.android.exoplayer2";

    private static final String[] ROOT_PREFIXES = {ROOT + ".", MEDIA_ROOT + "."};

    private static final String[][] EXACT = {
            {"com.exteragram.messenger.utils.chats.ChatUtils", "com.exteragram.messenger.utils.chats.ChatUtils"},
            {"com.exteragram.messenger.utils.ChatUtils", "com.exteragram.messenger.utils.chats.ChatUtils"},
            {"com.exteragram.messenger.utils.text.LocaleUtils", "com.exteragram.messenger.utils.text.LocaleUtils"},
            {"com.exteragram.messenger.utils.LocaleUtils", "com.exteragram.messenger.utils.text.LocaleUtils"},
            {"com.exteragram.messenger.utils.AppUtils", "com.exteragram.messenger.utils.AppUtils"},
            {"com.exteragram.messenger.R", "org.telegram.messenger.R"},
            {"com.exteragram.messenger.utils.system.VibratorUtils", "com.exteragram.messenger.utils.system.VibratorUtils"},
            {"com.exteragram.messenger.ai.AiConfig", "app.vibegram.ai.AiConfig"},
            {"com.exteragram.messenger.ai.AiController", "com.exteragram.messenger.ai.AiController"},
            {"com.exteragram.messenger.ai.ui.ResponseAlert", "com.exteragram.messenger.ai.ui.ResponseAlert"},
            {"com.exteragram.messenger.ai.ui.GenerateFromMessageBottomSheet", "com.exteragram.messenger.ai.ui.GenerateFromMessageBottomSheet"},
            {"com.exteragram.messenger.plugins.ui.components.InstallPluginBottomSheet", "com.exteragram.messenger.plugins.ui.components.InstallPluginBottomSheet"},
            {"com.exteragram.messenger.utils.system.SystemUtils", "com.exteragram.messenger.utils.system.SystemUtils"},
            {"com.exteragram.messenger.utils.SystemUtils", "com.exteragram.messenger.utils.system.SystemUtils"},
            {"com.exteragram.messenger.preferences.MainPreferencesActivity", "app.vibegram.settings.VibeSettingsActivity"},
            {"com.exteragram.messenger.preferences.GeneralPreferencesActivity", "app.vibegram.settings.VibeGeneralActivity"},
            {"com.exteragram.messenger.preferences.AppearancePreferencesActivity", "app.vibegram.settings.VibeAppearanceActivity"},
            {"com.exteragram.messenger.preferences.ChatsPreferencesActivity", "app.vibegram.settings.VibeChatsActivity"},
            {"com.exteragram.messenger.preferences.OtherPreferencesActivity", "app.vibegram.settings.VibeOtherActivity"},
            {"com.exteragram.messenger.preferences.AppNavigationPreferencesActivity", "app.vibegram.settings.VibeAppNavigationActivity"},
            {"com.exteragram.messenger.preferences.BasePreferencesActivity", "com.exteragram.messenger.preferences.BasePreferencesActivity"},
            {"com.exteragram.messenger.preferences.components.AltSeekbar", "app.vibegram.appearance.AltSeekbar"},
            {"com.exteragram.messenger.utils.chats.MainMenuHelper", "app.vibegram.drawer.MainMenuHelper"},
            {"com.exteragram.messenger.icons.ui.IconPacksActivity", "app.vibegram.icons.IconPacksActivity"},
            {"com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ColoredBackground", "app.vibegram.pillstack.pills.ColoredBackground"},
            {"com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill", "app.vibegram.pillstack.pills.WeatherPill"},
            {"com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity", "app.vibegram.pillstack.PillStackSettingsActivity"},
            {"com.exteragram.messenger.pillstack.ui.PillStackLayout", "app.vibegram.pillstack.PillStackView"},
            {"com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPreferencesActivity", "app.vibegram.pillstack.pills.weather.WeatherSettingsActivity"},
            {"com.exteragram.messenger.preferences.appearance.AppearancePreferencesActivity", "app.vibegram.settings.VibeAppearanceActivity"},
            {"com.exteragram.messenger.nowplaying.ui.components.NowPlayingCard", "app.vibegram.components.ProfileMusicCard"},
            {"com.exteragram.messenger.nowplaying.NowPlayingController", "app.vibegram.nowplaying.NowPlayingController"},
            {"com.exteragram.messenger.proxy.ProxyController", "app.vibegram.proxy.ProxyController"},
            {"com.exteragram.messenger.utils.ui.UIUtil", "app.vibegram.utils.UIUtil"},
            {"com.exteragram.messenger.utils.ui.MainTabsUiHelper", "app.vibegram.appearance.MainTabsUiHelper"},
            {"com.google.android.exoplayer2.util.Consumer", "androidx.media3.common.util.Consumer"},
            {"com.google.android.exoplayer2.video.VideoSize", "androidx.media3.common.VideoSize"},
            {"com.google.android.exoplayer2.PlaybackException", "androidx.media3.common.PlaybackException"},
            {"com.google.android.exoplayer2.PlaybackParameters", "androidx.media3.common.PlaybackParameters"},
            {"com.google.android.exoplayer2.C", "androidx.media3.common.C"},
            {"org.telegram.ui.Gifts.ProfileGiftsContainer$SelectGiftsBottomSheet", "org.telegram.ui.Gifts.SelectGiftsBottomSheet"},
            {"org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode", "org.telegram.utils.glass.source.GlassSourceRenderNode"},
            {"org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor", "org.telegram.utils.glass.source.GlassSourceColor"},
            {"org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceWrapped", "org.telegram.utils.glass.source.GlassSourceWrapped"},
            {"org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap", "org.telegram.utils.glass.source.GlassSourceBitmap"},
            {"org.telegram.ui.Components.blur3.source.BlurredBackgroundSource", "org.telegram.utils.glass.source.GlassSource"},
    };

    private static final String[][] PREFIXES = {
            {"com.exteragram.messenger.ai.data.", "app.vibegram.ai.data."},
            {"com.exteragram.messenger.ai.network.", "app.vibegram.ai.network."},
            {"com.exteragram.messenger.pillstack.core.", "app.vibegram.pillstack."},
            {"com.exteragram.messenger.pillstack.ui.pills.", "app.vibegram.pillstack.pills."},
            {"com.exteragram.messenger.pillstack.ui.", "app.vibegram.pillstack."},
            {"com.exteragram.messenger.preferences.", "app.vibegram.settings."},
            {"com.exteragram.messenger.plugins.", "app.vibegram.plugins."},
            {"com.exteragram.messenger.icons.", "app.vibegram.icons."},
            {"com.exteragram.messenger.camera.", "app.vibegram.camera."},
            {"com.exteragram.messenger.backup.", "app.vibegram.backup."},
            {"com.exteragram.messenger.feed.", "app.vibegram.feed."},
            {"com.exteragram.messenger.drawer.", "app.vibegram.drawer."},
            {"com.exteragram.messenger.components.", "app.vibegram.components."},
            {"com.exteragram.messenger.utils.", "app.vibegram.utils."},
    };

    private static final Map<String, String> EXACT_MAP = buildExactMap();

    private ClassAliases() {
    }

    private static Map<String, String> buildExactMap() {
        Map<String, String> map = new HashMap<>(EXACT.length * 2);
        for (String[] pair : EXACT) {
            map.put(pair[0], pair[1]);
        }
        return map;
    }

    public static String resolve(String name) {
        if (name == null) {
            return null;
        }
        String exact = EXACT_MAP.get(name);
        if (exact != null) {
            return exact;
        }
        if (!underRoot(name)) {
            return name;
        }
        int dollar = name.indexOf('$');
        String outer = dollar < 0 ? name : name.substring(0, dollar);
        String nested = dollar < 0 ? "" : name.substring(dollar);
        exact = EXACT_MAP.get(outer);
        if (exact != null) {
            return exact + nested;
        }
        for (String[] pair : PREFIXES) {
            if (outer.startsWith(pair[0])) {
                return pair[1] + outer.substring(pair[0].length()) + nested;
            }
        }
        return name;
    }

    private static boolean underRoot(String name) {
        for (String prefix : ROOT_PREFIXES) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
