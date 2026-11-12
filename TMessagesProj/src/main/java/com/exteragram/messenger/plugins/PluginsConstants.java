package com.exteragram.messenger.plugins;

/**
 * Шим строковых констант exteraGram. Значения — те же, что в
 * {@code com.exteragram.messenger.plugins.PluginsConstants} 12.9.0, а не наши
 * внутренние: плагин сравнивает их с тем, что приходит из SDK.
 */
public final class PluginsConstants {

    public static final String PYTHON = app.vibegram.plugins.PluginsConstants.PYTHON;
    public static final String SEND_MESSAGE_HOOK = app.vibegram.plugins.PluginsConstants.SEND_MESSAGE_HOOK;
    public static final String STRATEGY = app.vibegram.plugins.PluginsConstants.STRATEGY;
    public static final String PARAMS = app.vibegram.plugins.PluginsConstants.PARAMS;
    public static final String UPDATE = app.vibegram.plugins.PluginsConstants.UPDATE;
    public static final String UPDATES = app.vibegram.plugins.PluginsConstants.UPDATES;
    public static final String REQUEST = app.vibegram.plugins.PluginsConstants.REQUEST;
    public static final String RESPONSE = app.vibegram.plugins.PluginsConstants.RESPONSE;
    public static final String ERROR = app.vibegram.plugins.PluginsConstants.ERROR;
    public static final String PLUGINS = app.vibegram.plugins.PluginsConstants.PLUGINS;
    public static final String PLUGINS_EXT = app.vibegram.plugins.PluginsConstants.PLUGINS_EXT;
    public static final String PLUGINS_SDK = app.vibegram.plugins.PluginsConstants.PLUGINS_SDK;
    public static final String CREATE_SETTINGS = app.vibegram.plugins.PluginsConstants.CREATE_SETTINGS;
    public static final String APP_START = app.vibegram.plugins.PluginsConstants.APP_START;
    public static final String APP_STOP = app.vibegram.plugins.PluginsConstants.APP_STOP;
    public static final String APP_PAUSE = app.vibegram.plugins.PluginsConstants.APP_PAUSE;
    public static final String APP_RESUME = app.vibegram.plugins.PluginsConstants.APP_RESUME;
    public static final String ON_APP_EVENT = app.vibegram.plugins.PluginsConstants.ON_APP_EVENT;
    public static final String ON_PLUGIN_LOAD = app.vibegram.plugins.PluginsConstants.ON_PLUGIN_LOAD;
    public static final String ON_PLUGIN_UNLOAD = app.vibegram.plugins.PluginsConstants.ON_PLUGIN_UNLOAD;

    private PluginsConstants() {
    }

    public static final class Settings {
        public static final String TYPE = app.vibegram.plugins.PluginsConstants.Settings.TYPE;
        public static final String KEY = app.vibegram.plugins.PluginsConstants.Settings.KEY;
        public static final String TEXT = app.vibegram.plugins.PluginsConstants.Settings.TEXT;
        public static final String SUBTEXT = app.vibegram.plugins.PluginsConstants.Settings.SUBTEXT;
        public static final String ICON = app.vibegram.plugins.PluginsConstants.Settings.ICON;
        public static final String ACCENT = app.vibegram.plugins.PluginsConstants.Settings.ACCENT;
        public static final String RED = app.vibegram.plugins.PluginsConstants.Settings.RED;
        public static final String ON_CLICK = app.vibegram.plugins.PluginsConstants.Settings.ON_CLICK;
        public static final String DEFAULT = app.vibegram.plugins.PluginsConstants.Settings.DEFAULT;
        public static final String ITEMS = app.vibegram.plugins.PluginsConstants.Settings.ITEMS;
        public static final String HINT = app.vibegram.plugins.PluginsConstants.Settings.HINT;
        public static final String MULTILINE = app.vibegram.plugins.PluginsConstants.Settings.MULTILINE;
        public static final String MAX_LENGTH = app.vibegram.plugins.PluginsConstants.Settings.MAX_LENGTH;
        public static final String MASK = app.vibegram.plugins.PluginsConstants.Settings.MASK;
        public static final String ON_CHANGE = app.vibegram.plugins.PluginsConstants.Settings.ON_CHANGE;
        public static final String TYPE_SWITCH = app.vibegram.plugins.PluginsConstants.Settings.TYPE_SWITCH;
        public static final String TYPE_INPUT = app.vibegram.plugins.PluginsConstants.Settings.TYPE_INPUT;
        public static final String TYPE_SELECTOR = app.vibegram.plugins.PluginsConstants.Settings.TYPE_SELECTOR;
        public static final String TYPE_HEADER = app.vibegram.plugins.PluginsConstants.Settings.TYPE_HEADER;
        public static final String TYPE_DIVIDER = app.vibegram.plugins.PluginsConstants.Settings.TYPE_DIVIDER;
        public static final String TYPE_TEXT = app.vibegram.plugins.PluginsConstants.Settings.TYPE_TEXT;
        public static final String TYPE_EDIT_TEXT = app.vibegram.plugins.PluginsConstants.Settings.TYPE_EDIT_TEXT;
        public static final String TYPE_CUSTOM = app.vibegram.plugins.PluginsConstants.Settings.TYPE_CUSTOM;
        public static final String VIEW = app.vibegram.plugins.PluginsConstants.Settings.VIEW;
        public static final String ITEM = app.vibegram.plugins.PluginsConstants.Settings.ITEM;
        public static final String FACTORY = app.vibegram.plugins.PluginsConstants.Settings.FACTORY;
        public static final String FACTORY_ARGS = app.vibegram.plugins.PluginsConstants.Settings.FACTORY_ARGS;
        public static final String CREATE_SUB_FRAGMENT = app.vibegram.plugins.PluginsConstants.Settings.CREATE_SUB_FRAGMENT;
        public static final String ON_LONG_CLICK = app.vibegram.plugins.PluginsConstants.Settings.ON_LONG_CLICK;
        public static final String LINK_ALIAS = app.vibegram.plugins.PluginsConstants.Settings.LINK_ALIAS;

        private Settings() {
        }
    }

    public static final class Strategy {
        public static final String MODIFY = app.vibegram.plugins.PluginsConstants.Strategy.MODIFY;
        public static final String CANCEL = app.vibegram.plugins.PluginsConstants.Strategy.CANCEL;
        public static final String DEFAULT = app.vibegram.plugins.PluginsConstants.Strategy.DEFAULT;
        public static final String MODIFY_FINAL = app.vibegram.plugins.PluginsConstants.Strategy.MODIFY_FINAL;

        private Strategy() {
        }
    }

    public static final class Xposed {
        public static final String REPLACE_HOOKED_METHOD = app.vibegram.plugins.PluginsConstants.Xposed.REPLACE_HOOKED_METHOD;
        public static final String BEFORE_HOOKED_METHOD = app.vibegram.plugins.PluginsConstants.Xposed.BEFORE_HOOKED_METHOD;
        public static final String AFTER_HOOKED_METHOD = app.vibegram.plugins.PluginsConstants.Xposed.AFTER_HOOKED_METHOD;
        public static final String HOOK_FILTERS = app.vibegram.plugins.PluginsConstants.Xposed.HOOK_FILTERS;

        private Xposed() {
        }
    }

    public static final class DevServer {
        public static final String MODULE = app.vibegram.plugins.PluginsConstants.DevServer.MODULE;
        public static final String CLASS = app.vibegram.plugins.PluginsConstants.DevServer.CLASS;
        public static final String START_SERVER = app.vibegram.plugins.PluginsConstants.DevServer.START_SERVER;
        public static final String STOP_SERVER = app.vibegram.plugins.PluginsConstants.DevServer.STOP_SERVER;

        private DevServer() {
        }
    }

    public static final class MenuItemTypes {
        public static final String MESSAGE_CONTEXT_MENU = app.vibegram.plugins.PluginsConstants.MenuItemTypes.MESSAGE_CONTEXT_MENU;
        public static final String DRAWER_MENU = app.vibegram.plugins.PluginsConstants.MenuItemTypes.DRAWER_MENU;
        public static final String MAIN_MENU = app.vibegram.plugins.PluginsConstants.MenuItemTypes.MAIN_MENU;
        public static final String CHAT_ACTION_MENU = app.vibegram.plugins.PluginsConstants.MenuItemTypes.CHAT_ACTION_MENU;
        public static final String PROFILE_ACTION_MENU = app.vibegram.plugins.PluginsConstants.MenuItemTypes.PROFILE_ACTION_MENU;

        private MenuItemTypes() {
        }
    }

    public static final class MenuItemProperties {
        public static final String MENU_TYPE = app.vibegram.plugins.PluginsConstants.MenuItemProperties.MENU_TYPE;
        public static final String ITEM_ID = app.vibegram.plugins.PluginsConstants.MenuItemProperties.ITEM_ID;
        public static final String TEXT = app.vibegram.plugins.PluginsConstants.MenuItemProperties.TEXT;
        public static final String SUBTEXT = app.vibegram.plugins.PluginsConstants.MenuItemProperties.SUBTEXT;
        public static final String ICON = app.vibegram.plugins.PluginsConstants.MenuItemProperties.ICON;
        public static final String ON_CLICK = app.vibegram.plugins.PluginsConstants.MenuItemProperties.ON_CLICK;
        public static final String CONDITION = app.vibegram.plugins.PluginsConstants.MenuItemProperties.CONDITION;
        public static final String PRIORITY = app.vibegram.plugins.PluginsConstants.MenuItemProperties.PRIORITY;

        private MenuItemProperties() {
        }
    }

    public static final class HookFilterTypes {
        public static final String RESULT_IS_NULL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.RESULT_IS_NULL;
        public static final String RESULT_IS_TRUE = app.vibegram.plugins.PluginsConstants.HookFilterTypes.RESULT_IS_TRUE;
        public static final String RESULT_IS_FALSE = app.vibegram.plugins.PluginsConstants.HookFilterTypes.RESULT_IS_FALSE;
        public static final String RESULT_NOT_NULL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.RESULT_NOT_NULL;
        public static final String RESULT_IS_INSTANCE_OF = app.vibegram.plugins.PluginsConstants.HookFilterTypes.RESULT_IS_INSTANCE_OF;
        public static final String RESULT_EQUAL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.RESULT_EQUAL;
        public static final String RESULT_NOT_EQUAL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.RESULT_NOT_EQUAL;
        public static final String ARGUMENT_IS_NULL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.ARGUMENT_IS_NULL;
        public static final String ARGUMENT_IS_TRUE = app.vibegram.plugins.PluginsConstants.HookFilterTypes.ARGUMENT_IS_TRUE;
        public static final String ARGUMENT_IS_FALSE = app.vibegram.plugins.PluginsConstants.HookFilterTypes.ARGUMENT_IS_FALSE;
        public static final String ARGUMENT_NOT_NULL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.ARGUMENT_NOT_NULL;
        public static final String ARGUMENT_IS_INSTANCE_OF = app.vibegram.plugins.PluginsConstants.HookFilterTypes.ARGUMENT_IS_INSTANCE_OF;
        public static final String ARGUMENT_EQUAL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.ARGUMENT_EQUAL;
        public static final String ARGUMENT_NOT_EQUAL = app.vibegram.plugins.PluginsConstants.HookFilterTypes.ARGUMENT_NOT_EQUAL;
        public static final String CONDITION = app.vibegram.plugins.PluginsConstants.HookFilterTypes.CONDITION;
        public static final String OR = app.vibegram.plugins.PluginsConstants.HookFilterTypes.OR;

        private HookFilterTypes() {
        }
    }
}
