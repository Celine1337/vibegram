package tw.nekomimi.nekogram.helpers;

import static org.telegram.messenger.LocaleController.getString;
import static org.telegram.ui.ProfileActivity.sendLogs;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LiteModeSettingsActivity;
import org.telegram.ui.PrivacySettingsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import app.vibegram.ai.ui.AiSettingsActivity;
import app.vibegram.icons.IconPacksActivity;
import app.vibegram.pillstack.pills.weather.WeatherSettingsActivity;
import app.vibegram.settings.VibeCloudActivity;
import app.vibegram.settings.VibeGlyphActivity;
import app.vibegram.pillstack.PillStackSettingsActivity;
import app.vibegram.plugins.ui.PluginsActivity;
import app.vibegram.settings.VibeAppNavigationActivity;
import app.vibegram.settings.VibeAppearanceActivity;
import app.vibegram.settings.VibeMomentsActivity;
import app.vibegram.settings.VibeChatsActivity;
import app.vibegram.settings.VibeGeneralActivity;
import app.vibegram.settings.VibeOtherActivity;
import app.vibegram.settings.VibeSettingsActivity;
import app.vibegram.tools.VibeToolsActivity;
import tw.nekomimi.nekogram.settings.BaseNekoSettingsActivity;
import tw.nekomimi.nekogram.settings.BaseNekoXSettingsActivity;
import tw.nekomimi.nekogram.settings.NekoAboutActivity;
import tw.nekomimi.nekogram.settings.NekoEmojiSettingsActivity;
import tw.nekomimi.nekogram.settings.NekoPasscodeSettingsActivity;
import tw.nekomimi.nekogram.settings.NekoTranslatorSettingsActivity;

public class SettingsHelper {

    private static final String HOST_NAGRAM = "nasettings";
    private static final String HOST_VIBEGRAM = "vibegram";

    private static final Map<String, String> SEARCH_TITLE_ALIASES = new HashMap<>();
    private static final Map<String, String> TRANSLATOR_ROWS = new HashMap<>();
    private static final Map<String, String> NAGRAM_ROWS = new HashMap<>();
    private static final Map<String, String> MOVED_ROWS = new HashMap<>();

    static {
        SEARCH_TITLE_ALIASES.put("VGGeneral:botPrivacyAccount", "VGBotPrivacyAccount");
        SEARCH_TITLE_ALIASES.put("OEAyu:ayuGhost", "GhostMode");
        SEARCH_TITLE_ALIASES.put("OEAyu:ayuDisableAll", "VGGeneralAyuMomentsDisableAll");
        SEARCH_TITLE_ALIASES.put("OEAyu:ayuClearDatabase", "ClearMessageDatabase");
        SEARCH_TITLE_ALIASES.put("OEAppearance:appNavigation", "VGAppearanceNavigation");
        SEARCH_TITLE_ALIASES.put("OEChats:hideStories", "VGAppearanceStories");
        SEARCH_TITLE_ALIASES.put("OEChats:hideActionBarStatus", "VGAppearanceHideActionBarStatus");
        SEARCH_TITLE_ALIASES.put("OEChats:hideFloatingButton", "VGAppearanceHideFloatingButton");
        SEARCH_TITLE_ALIASES.put("OEChats:hideSearchBar", "VGAppearanceHideSearchBar");
        SEARCH_TITLE_ALIASES.put("OEChats:hideAllChats", "HideAllTab");
        SEARCH_TITLE_ALIASES.put("OEChats:hideShareButton", "HideShareButtonInChannel");
        SEARCH_TITLE_ALIASES.put("OEChats:hideAi", "VGAppearanceHideAi");
        SEARCH_TITLE_ALIASES.put("OEChats:hideAiEditor", "VGAppearanceHideAiEditor");
        SEARCH_TITLE_ALIASES.put("OEChats:hideAiSummary", "VGAppearanceHideAiSummary");
        SEARCH_TITLE_ALIASES.put("OEChats:hideAiIv", "VGAppearanceHideAiIv");
        SEARCH_TITLE_ALIASES.put("OEChats:hideSettingsSections", "VGAppearanceHideSettingsSections");
        SEARCH_TITLE_ALIASES.put("OEChats:disableGreeting", "VGChatsDisableGreetingSticker");
        SEARCH_TITLE_ALIASES.put("OEChats:hideKeyboardOnScroll", "HideKeyboardOnChatScroll");
        SEARCH_TITLE_ALIASES.put("OEChats:transcribeProvider", "PremiumPreviewVoiceToText");
        SEARCH_TITLE_ALIASES.put("VGGeneral:translator", "TranslatorSettings");
        SEARCH_TITLE_ALIASES.put("VGGeneral:translateInSheet", "VGChatsTranslateInSheet");
        SEARCH_TITLE_ALIASES.put("VGGeneral:glyph", "VGGlyphTitle");
        SEARCH_TITLE_ALIASES.put("VibeGram:channel", "ProfileChannel");
        SEARCH_TITLE_ALIASES.put("VGGeneral:downloadSpeed", "VGGeneralSpeedHeader");
        SEARCH_TITLE_ALIASES.put("OEAppearance:md3Styles", "VGAppearanceMaterialDesign3");
        SEARCH_TITLE_ALIASES.put("OEAppearance:md3Loading", "VGAppearanceNewLoadingStyle");
        SEARCH_TITLE_ALIASES.put("OEAppearance:md3Slider", "VGAppearanceSliderStyle");
        SEARCH_TITLE_ALIASES.put("OEAppearance:md3Switch", "VGAppearanceSwitchStyle");
        SEARCH_TITLE_ALIASES.put("OEAppearance:md3NavBar", "VGAppearanceNewNavigationBarStyle");
        SEARCH_TITLE_ALIASES.put("OEAppearance:md3ListItems", "VGAppearanceM3ListItems");
        SEARCH_TITLE_ALIASES.put("OEAppearance:iosStyles", "VGAppearanceIosDesign");
        SEARCH_TITLE_ALIASES.put("OEAppearance:iosNavBar", "VGAppearanceIosNavigationBarStyle");
        SEARCH_TITLE_ALIASES.put("OEChats:doubleTapReaction", "DoubleTapSetting");
        SEARCH_TITLE_ALIASES.put("OEChats:quickTransition", "VGChatsQuickTransitions");
        SEARCH_TITLE_ALIASES.put("OEChats:inlineMathResult", "VGChatsInlineMath");
        SEARCH_TITLE_ALIASES.put("OEChats:searchHashtagChat", "SearchHashtagDefaultPageChat");
        SEARCH_TITLE_ALIASES.put("OEChats:searchHashtagChannel", "SearchHashtagDefaultPageChannel");
        SEARCH_TITLE_ALIASES.put("OEChats:replaceEdited", "VGChatsReplaceEditedWithIcon");
        SEARCH_TITLE_ALIASES.put("OEChats:videoMessagesCamera", "CameraInVideoMessages");
        SEARCH_TITLE_ALIASES.put("OEChats:voskModels", "VoskModelsShort");
        MOVED_ROWS.put("chats:translateInSheet", "general");
        MOVED_ROWS.put("other:unlimitedPinnedDialogs", "general");
        MOVED_ROWS.put("other:glyph", "general");
        moveRows("chats", "menus", "bottomButton", "adminShortcuts", "chatMenu", "messageMenu",
                "groupedMessageMenu", "textStyle", "TextStyle", "mediaViewerMenu", "actionBarButtons",
                "defaultDeleteMenu", "DefaultDeleteMenu");
        moveRows("chats", "media", "cameraType", "extendedSettings", "videoMessagesCamera",
                "rememberLastUsedCamera", "zoomSlider", "staticZoom", "alwaysSendInHD", "hdrPhotos",
                "disableInstantCamera", "DisableInstantCamera", "doubleTapSeekDuration", "preferOriginalQuality",
                "videoPlayerDecoder", "VideoPlayerDecoder", "swipeToPip", "unmuteWithVolumeButtons",
                "showSmallGIF", "ShowSmallGIF", "dontAutoPlayNextVoice", "DontAutoPlayNextVoice",
                "disableProximityEvents", "DisableProximityEvents", "pauseOnMinimize", "transcribeProvider",
                "TranscribeProviderShort", "cloudflareCredentials", "CloudflareCredentials", "llmProviderGeminiKey",
                "LlmProviderGeminiKey", "transcribeProviderOpenAI", "TranscribeProviderOpenAI", "voskModels",
                "VoskModelsShort");
        moveRows("chats", "hiding", "disableTrending", "DisableTrending", "hideGroupSticker", "hideReactions",
                "disableGreeting", "hideSendAsPeer", "hideShareButton", "hideGiftButton", "hideSearchButton",
                "hideCameraTile", "premiumElements", "PremiumElements");
        moveRows("other", "media", "noiseSuppressAndVoiceEnhance", "NoiseSuppressAndVoiceEnhance",
                "enhancedVideoBitrate", "EnhancedVideoBitrate");
        moveRows("appearance", "hiding", "hideActionBarStatus", "hideStories", "HideStoriesFromHeader",
                "DisableStories", "hideFloatingButton", "hideSearchBar", "hideAllChats", "hideAi", "hideAiEditor",
                "hideAiSummary", "hideAiIv", "hideSettingsSections", "hidePremiumSection", "HidePremiumSection",
                "hideHelpSection", "HideHelpSection");
        TRANSLATOR_ROWS.put("translateButton", "showTranslate");
        TRANSLATOR_ROWS.put("translateChatButton", "TelegramUIAutoTranslate");
        TRANSLATOR_ROWS.put("translationProvider", "translationProvider");
        TRANSLATOR_ROWS.put("translateToLang", "TranslateTo");
        TRANSLATOR_ROWS.put("doNotTranslate", "DoNotTranslate");
        NAGRAM_ROWS.put("HidePremiumSection", "appearance");
        NAGRAM_ROWS.put("HideHelpSection", "appearance");
        NAGRAM_ROWS.put("HideStoriesFromHeader", "appearance");
        NAGRAM_ROWS.put("DisableStories", "appearance");
        NAGRAM_ROWS.put("MainTabsHideTitles", "navigation");
        NAGRAM_ROWS.put("MainTabsHideContacts", "navigation");
        NAGRAM_ROWS.put("MainTabsHideCallsSettings", "navigation");
        NAGRAM_ROWS.put("MainTabsHideProfile", "navigation");
        NAGRAM_ROWS.put("AlwaysShowDownloadIcon", "navigation");
        NAGRAM_ROWS.put("DisableSystemAccount", "privacy");
        NAGRAM_ROWS.put("PerformanceClass", "powersaving");
        NAGRAM_ROWS.put("VideoPlayerDecoder", "chats");
    }

    private static void moveRows(String from, String to, String... rows) {
        for (String row : rows) {
            MOVED_ROWS.put(from + ":" + row, to);
        }
    }

    private static final Set<String> VIBEGRAM_SCREENS = new HashSet<>(Arrays.asList(
            "settings", "general", "appearance", "chats", "plugins", "pillstack", "other",
            "ayumoments", "navigation", "menus", "media", "hiding"));

    public static boolean isDeepLink(String path) {
        if (path == null) {
            return false;
        }
        if (path.startsWith(HOST_NAGRAM + "/")) {
            return true;
        }
        if (!path.startsWith(HOST_VIBEGRAM + "/")) {
            return false;
        }
        return VIBEGRAM_SCREENS.contains(path.substring(HOST_VIBEGRAM.length() + 1));
    }

    public static String linkPathFor(String key) {
        if (key == null) {
            return null;
        }
        switch (key) {
            case "vibegram":
                return HOST_VIBEGRAM + "/settings";
            case "vibegram_general":
                return HOST_VIBEGRAM + "/general";
            case "vibegram_appearance":
                return HOST_VIBEGRAM + "/appearance";
            case "vibegram_chats":
                return HOST_VIBEGRAM + "/chats";
            case "vibegram_menus":
                return HOST_VIBEGRAM + "/menus";
            case "vibegram_media":
                return HOST_VIBEGRAM + "/media";
            case "vibegram_hiding":
                return HOST_VIBEGRAM + "/hiding";
            case "vibegram_other":
                return HOST_VIBEGRAM + "/other";
            case "vibegram_ayumoments":
                return HOST_VIBEGRAM + "/ayumoments";
            case "pillstack":
                return HOST_VIBEGRAM + "/pillstack";
            case "vibegram_navigation":
                return HOST_VIBEGRAM + "/navigation";
            case "vibegram_tools":
                return HOST_VIBEGRAM + "/tools";
            default:
                return HOST_NAGRAM + "/" + key;
        }
    }

    public static void processDeepLink(Activity activity, Uri uri, Callback callback, Runnable unknown) {
        if (uri == null) {
            unknown.run();
            return;
        }
        var segments = uri.getPathSegments();
        if (segments.isEmpty() || segments.size() > 2) {
            unknown.run();
            return;
        }
        final boolean vibegram = HOST_VIBEGRAM.equals(segments.get(0));
        if (!vibegram && !HOST_NAGRAM.equals(segments.get(0))) {
            unknown.run();
            return;
        }
        if (vibegram && segments.size() < 2) {
            unknown.run();
            return;
        }
        var row = uri.getQueryParameter("r");
        if (TextUtils.isEmpty(row)) {
            row = uri.getQueryParameter("row");
        }
        BaseFragment fragment;
        BaseNekoSettingsActivity neko_fragment = null;
        BaseNekoXSettingsActivity nekox_fragment = null;
        VibeAppNavigationActivity navigation_fragment = null;
        String screen = vibegram ? segments.get(1) : nagramScreen(segments.size() == 1 ? null : segments.get(1), row);
        if (screen == null && segments.size() == 2 && segments.get(1).startsWith("vibegram_")) {
            String base = segments.get(1).substring("vibegram_".length());
            if (row != null && MOVED_ROWS.containsKey(base + ":" + row)) {
                screen = base;
            }
        }
        if (screen != null && row != null) {
            String moved = MOVED_ROWS.get(screen + ":" + row);
            if (moved != null) {
                screen = moved;
            }
        }
        if (screen != null) {
            switch (screen) {
                case "settings":
                    fragment = neko_fragment = new VibeSettingsActivity();
                    break;
                case "general":
                    if (TRANSLATOR_ROWS.containsKey(row)) {
                        fragment = nekox_fragment = new NekoTranslatorSettingsActivity();
                        row = TRANSLATOR_ROWS.get(row);
                    } else {
                        fragment = neko_fragment = new VibeGeneralActivity();
                    }
                    break;
                case "appearance":
                    fragment = neko_fragment = "useSystemEmoji".equals(row)
                            ? new NekoEmojiSettingsActivity() : new VibeAppearanceActivity();
                    break;
                case "chats":
                    fragment = neko_fragment = new VibeChatsActivity();
                    break;
                case "menus":
                    fragment = neko_fragment = new VibeChatsActivity(VibeChatsActivity.SCREEN_MENUS);
                    break;
                case "media":
                    fragment = neko_fragment = new VibeChatsActivity(VibeChatsActivity.SCREEN_MEDIA);
                    break;
                case "hiding":
                    fragment = neko_fragment = new VibeChatsActivity(VibeChatsActivity.SCREEN_HIDING);
                    break;
                case "other":
                    fragment = neko_fragment = new VibeOtherActivity();
                    break;
                case "ayumoments":
                    fragment = neko_fragment = new VibeMomentsActivity();
                    break;
                case "pillstack":
                    fragment = neko_fragment = new PillStackSettingsActivity();
                    break;
                case "plugins":
                    fragment = new PluginsActivity();
                    break;
                case "navigation":
                    fragment = navigation_fragment = new VibeAppNavigationActivity();
                    break;
                case "tools":
                    fragment = neko_fragment = new VibeToolsActivity();
                    break;
                case "privacy":
                    fragment = new PrivacySettingsActivity();
                    break;
                case "powersaving":
                    fragment = new LiteModeSettingsActivity();
                    break;
                default:
                    unknown.run();
                    return;
            }
        } else if (PasscodeHelper.getSettingsKey().equals(segments.get(1))) {
            fragment = neko_fragment = new NekoPasscodeSettingsActivity();
        } else {
            switch (segments.get(1)) {
                case "about":
                    fragment = new NekoAboutActivity();
                    break;
                case "emoji":
                    fragment = neko_fragment = new NekoEmojiSettingsActivity();
                    break;
                case "translator":
                case "translate":
                case "t":
                    fragment = nekox_fragment = new NekoTranslatorSettingsActivity();
                    break;
                case "vibegram":
                    fragment = neko_fragment = new VibeSettingsActivity();
                    break;
                case "vibegram_general":
                    if (TRANSLATOR_ROWS.containsKey(row)) {
                        fragment = nekox_fragment = new NekoTranslatorSettingsActivity();
                        row = TRANSLATOR_ROWS.get(row);
                    } else {
                        fragment = neko_fragment = new VibeGeneralActivity();
                    }
                    break;
                case "vibegram_appearance":
                    fragment = neko_fragment = "useSystemEmoji".equals(row)
                            ? new NekoEmojiSettingsActivity() : new VibeAppearanceActivity();
                    break;
                case "vibegram_chats":
                    fragment = neko_fragment = new VibeChatsActivity();
                    break;
                case "vibegram_other":
                    fragment = neko_fragment = new VibeOtherActivity();
                    break;
                case "vibegram_ayumoments":
                    fragment = neko_fragment = new VibeMomentsActivity();
                    break;
                case "pillstack":
                    fragment = neko_fragment = new PillStackSettingsActivity();
                    break;
                case "pillstackweather":
                    fragment = neko_fragment = new WeatherSettingsActivity();
                    break;
                case "iconpacks":
                    fragment = neko_fragment = new IconPacksActivity();
                    break;
                case "vibegram_ai":
                    fragment = neko_fragment = new AiSettingsActivity();
                    break;
                case "vibegram_cloud":
                    fragment = neko_fragment = new VibeCloudActivity();
                    break;
                case "vibegram_glyph":
                    fragment = neko_fragment = new VibeGlyphActivity();
                    break;
                case "send_logs":
                    sendLogs(activity, false);
                    return;
                default:
                    unknown.run();
                    return;
            }
        }
        callback.presentFragment(fragment);
        var value = uri.getQueryParameter("v");
        if (TextUtils.isEmpty(value)) {
            value = uri.getQueryParameter("value");
        }
        if (!TextUtils.isEmpty(row)) {
            var rowFinal = row;
            if (neko_fragment != null) {
                BaseNekoSettingsActivity finalNeko_fragment = neko_fragment;
                AndroidUtilities.runOnUIThread(() -> finalNeko_fragment.scrollToRow(rowFinal, unknown));
            } else if (navigation_fragment != null) {
                VibeAppNavigationActivity finalNavigation_fragment = navigation_fragment;
                AndroidUtilities.runOnUIThread(() -> finalNavigation_fragment.scrollToRow(rowFinal, unknown));
            } else if (nekox_fragment != null) {
                BaseNekoXSettingsActivity finalNekoX_fragment = nekox_fragment;
                if (!TextUtils.isEmpty(value)) {
                    String finalValue = value;
                    AndroidUtilities.runOnUIThread(() -> finalNekoX_fragment.importToRow(rowFinal, finalValue, unknown));
                } else {
                    AndroidUtilities.runOnUIThread(() -> finalNekoX_fragment.scrollToRow(rowFinal, unknown));
                }
            }
        }
    }

    private static String nagramScreen(String segment, String row) {
        if (segment == null) {
            return "settings";
        }
        String fallback;
        switch (segment) {
            case "general":
            case "g":
                fallback = "general";
                break;
            case "chat":
            case "chats":
            case "c":
                fallback = "chats";
                break;
            case "experimental":
            case "e":
                fallback = "other";
                break;
            default:
                return null;
        }
        String moved = row == null ? null : NAGRAM_ROWS.get(row);
        return moved != null ? moved : fallback;
    }

    private static String rowTitle(BaseNekoSettingsActivity fragment, String key) {
        String title = getString(key);
        if (title != null && !title.isEmpty() && !title.equals(key)) {
            return title;
        }
        if (key.isEmpty()) {
            return null;
        }
        String capitalized = Character.toUpperCase(key.charAt(0)) + key.substring(1);
        String prefix = fragment.getSearchPrefix();
        if (prefix != null) {
            String alias = SEARCH_TITLE_ALIASES.get(prefix + ":" + key);
            if (alias != null) {
                title = resolved(alias);
                if (title != null) {
                    return title;
                }
            }
            title = resolved(prefix + capitalized);
            if (title != null) {
                return title;
            }
        }
        return resolved(capitalized);
    }

    private static String resolved(String name) {
        if (LocaleController.getStringResId(name) == 0) {
            return null;
        }
        String value = getString(name);
        return value == null || value.isEmpty() ? null : value;
    }

    public interface Callback {
        void presentFragment(BaseFragment fragment);
    }

    public static ArrayList<SettingsSearchResult> onCreateSearchArray(Callback callback) {
        ArrayList<SettingsSearchResult> items = new ArrayList<>();
        ArrayList<BaseNekoXSettingsActivity> fragments = new ArrayList<>();
        fragments.add(new NekoTranslatorSettingsActivity());

        ArrayList<BaseNekoSettingsActivity> vibegramFragments = new ArrayList<>();
        vibegramFragments.add(new VibeSettingsActivity());
        vibegramFragments.add(new VibeGeneralActivity());
        vibegramFragments.add(new VibeAppearanceActivity());
        vibegramFragments.add(new VibeChatsActivity());
        vibegramFragments.add(new VibeChatsActivity(VibeChatsActivity.SCREEN_MENUS));
        vibegramFragments.add(new VibeChatsActivity(VibeChatsActivity.SCREEN_MEDIA));
        vibegramFragments.add(new VibeChatsActivity(VibeChatsActivity.SCREEN_HIDING));
        vibegramFragments.add(new VibeOtherActivity());
        vibegramFragments.add(new VibeMomentsActivity());
        vibegramFragments.add(new PillStackSettingsActivity());
        vibegramFragments.add(new AiSettingsActivity());

        String e_title = getString(R.string.VibeGram);
        for (BaseNekoSettingsActivity fragment : vibegramFragments) {
            try {
                fragment.buildRowsForSearch();
            } catch (Exception e) {
                continue;
            }
            int uid = fragment.getSearchGuid();
            int drawable = fragment.getSearchIcon();
            String f_title = fragment.getSearchTitle();
            for (Map.Entry<Integer, String> entry : fragment.getSearchRows().entrySet()) {
                String key = entry.getValue();
                if (key == null || key.endsWith("Header") || key.equals(String.valueOf(entry.getKey()))) {
                    continue;
                }
                String title = rowTitle(fragment, key);
                if (title == null || title.isEmpty()) {
                    continue;
                }
                Runnable open = () -> {
                    callback.presentFragment(fragment);
                    AndroidUtilities.runOnUIThread(() -> fragment.scrollToRow(key, null));
                };
                items.add(new SettingsSearchResult(
                        uid + entry.getKey(), title, e_title, f_title, drawable, open));
            }
        }

        items.add(new SettingsSearchResult(29000, getString(R.string.VGAppearanceUseSystemEmoji), e_title,
                getString(R.string.EmojiSets), R.drawable.msg_emoji_smiles, () -> {
                    NekoEmojiSettingsActivity emoji = new NekoEmojiSettingsActivity();
                    callback.presentFragment(emoji);
                    AndroidUtilities.runOnUIThread(() -> emoji.scrollToRow("useSystemEmoji", null));
                }));

        String n_title = getString(R.string.Language);
        for (BaseNekoXSettingsActivity fragment: fragments) {
            int uid = fragment.getBaseGuid();
            int drawable = fragment.getDrawable();
            String f_title = fragment.getTitle();
            for (Map.Entry<Integer, String> entry : fragment.getRowMapReverse().entrySet()) {
                Integer i = entry.getKey();
                String key = entry.getValue();
                if (key.equals(String.valueOf(i))) {
                    continue;
                }
                int guid = uid + i;
                String title = getString(key);
                if (title == null || title.isEmpty()) {
                    continue;
                }
                Runnable open = () -> {
                    callback.presentFragment(fragment);
                    AndroidUtilities.runOnUIThread(() -> fragment.scrollToRow(key, null));
                };
                SettingsSearchResult result = new SettingsSearchResult(
                        guid, title, n_title, f_title, drawable, open
                );
                items.add(result);
            }
        }
        return items;
    }
}
