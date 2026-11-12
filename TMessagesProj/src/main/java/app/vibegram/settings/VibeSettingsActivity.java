package app.vibegram.settings;

import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.RecyclerListView;

import tw.nekomimi.nekogram.settings.BaseNekoSettingsActivity;
import tw.nekomimi.nekogram.ui.cells.HeaderCell;

/**
 * Корневой экран настроек VibeGram.
 */
public class VibeSettingsActivity extends BaseNekoSettingsActivity {

    private static final int MENU_CLOUD = 1;
    private static final int MENU_SEARCH = 2;

    private static final int TYPE_ABOUT = 100;

    private int aboutRow;

    private int categoriesHeaderRow;
    private int generalRow;
    private int appearanceRow;
    private int chatsRow;
    private int vibeToolsRow;
    private int pluginsRow;
    private int otherRow;
    private int categoriesDividerRow;

    private int linksHeaderRow;
    private int channelRow;
    private int sourceRow;
    private int linksDividerRow;

    @Override
    protected void updateRows() {
        super.updateRows();

        aboutRow = addRow("about");

        categoriesHeaderRow = addRow("categoriesHeader");
        generalRow = addRow("general");
        appearanceRow = addRow("appearance");
        chatsRow = addRow("chats");
        vibeToolsRow = addRow("vibeTools");
        pluginsRow = addRow("plugins");
        otherRow = addRow("other");
        categoriesDividerRow = addRow();

        linksHeaderRow = addRow("linksHeader");
        channelRow = addRow("channel");
        sourceRow = addRow("source");
        linksDividerRow = addRow();
    }

    /**
     * Прозрачный ActionBar поверх шапки; заголовок остаётся скрытым до прокрутки.
     */
    @Override
    public View createView(Context context) {
        View view = super.createView(context);
        getMessagesController().getContentSettings(null);
        if (actionBar != null && fragmentView instanceof android.widget.FrameLayout) {
            // 1:1 с MainPreferencesActivity.createView из 12.9.0.
            actionBar.setBackground(null);
            actionBar.setCastShadows(false);
            actionBar.setAddToContainer(false);
            if (actionBar.getTitleTextView() != null) {
                actionBar.getTitleTextView().setAlpha(0f);
            }
            ((android.widget.FrameLayout) fragmentView).addView(actionBar,
                    org.telegram.ui.Components.LayoutHelper.createFrame(
                            org.telegram.ui.Components.LayoutHelper.MATCH_PARENT,
                            org.telegram.ui.Components.LayoutHelper.WRAP_CONTENT,
                            android.view.Gravity.TOP));
        }
        if (actionBar != null) {
            final org.telegram.ui.ActionBar.ActionBarMenu menu = actionBar.createMenu();
            menu.addItem(MENU_SEARCH, R.drawable.outline_header_search, getResourceProvider());
            menu.addItem(MENU_CLOUD, R.drawable.cloud_sync, getResourceProvider());
            actionBar.setActionBarMenuOnItemClick(new org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    if (id == -1) {
                        finishFragment();
                    } else if (id == MENU_CLOUD) {
                        presentFragment(new VibeCloudActivity());
                    } else if (id == MENU_SEARCH) {
                        showSettingsSearchDialog();
                    }
                }
            });
        }
        return view;
    }

    @Override
    protected void setupAdaptiveBackground() {
    }

    @Override
    protected boolean needActionBarBlur() {
        return false;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        if (listView != null) {
            listView.setPadding(0, top, 0, bottom);
            listView.setClipToPadding(false);
        }
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.VibeGram);
    }

    @Override
    public int getSearchGuid() {
        return 24000;
    }

    @Override
    public int getSearchIcon() {
        return R.drawable.msg_settings_old;
    }

    @Override
    public String getSearchPrefix() {
        return "VibeGram";
    }

    @Override
    protected String getKey() {
        return "vibegram";
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == generalRow) {
            presentFragment(new VibeGeneralActivity());
        } else if (position == appearanceRow) {
            presentFragment(new VibeAppearanceActivity());
        } else if (position == chatsRow) {
            presentFragment(new VibeChatsActivity());
        } else if (position == vibeToolsRow) {
            presentFragment(new app.vibegram.tools.VibeToolsActivity());
        } else if (position == pluginsRow) {
            presentFragment(new app.vibegram.plugins.ui.PluginsActivity());
        } else if (position == otherRow) {
            presentFragment(new VibeOtherActivity());
        } else if (position == channelRow) {
            getMessagesController().openByUserName("vibegram", this, 1);
        } else if (position == sourceRow) {
            org.telegram.messenger.browser.Browser.openUrl(getParentActivity(),
                    "https://github.com/vibegram/vibegram");
        }
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ABOUT) {
                AboutHeaderCell view = new AboutHeaderCell(mContext);
                view.setOnLogoClickListener(v -> showDialog(new app.vibegram.updater.UpdaterSheet(VibeSettingsActivity.this)));
                view.setTag(RecyclerListView.TAG_NOT_SECTION);
                view.setLayoutParams(new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                return new RecyclerListView.Holder(view);
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean partial) {
            switch (holder.getItemViewType()) {
                case TYPE_HEADER: {
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (position == categoriesHeaderRow) {
                        cell.setText(getString(R.string.VibeGramCategories));
                    } else if (position == linksHeaderRow) {
                        cell.setText(getString(R.string.VibeGramLinks));
                    }
                    break;
                }
                case TYPE_TEXT: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == generalRow) {
                        cell.setTextAndIcon(getString(R.string.VibeGramGeneral), R.drawable.msg_media, true);
                    } else if (position == appearanceRow) {
                        cell.setTextAndIcon(getString(R.string.VibeGramAppearance), R.drawable.msg_theme, true);
                    } else if (position == chatsRow) {
                        cell.setTextAndIcon(getString(R.string.VibeGramChats), R.drawable.msg_discussion, true);
                    } else if (position == vibeToolsRow) {
                        cell.setTextAndIcon(getString(R.string.VGToolsTitle), R.drawable.msg_instant, true);
                    } else if (position == pluginsRow) {
                        cell.setTextAndIcon(getString(R.string.VibeGramPlugins), R.drawable.msg_plugins, true);
                    } else if (position == otherRow) {
                        cell.setTextAndIcon(getString(R.string.VibeGramOther), R.drawable.msg_fave, false);
                    } else if (position == channelRow) {
                        cell.setTextAndValueAndIcon(getString(R.string.ProfileChannel),
                                "@vibegram", R.drawable.msg_channel, true);
                    } else if (position == sourceRow) {
                        cell.setTextAndValueAndIcon(getString(R.string.VibeGramSource),
                                "GitHub", R.drawable.msg_language, false);
                    }
                    cell.setImageLeft(21);
                    cell.setOffsetFromImage(71);
                    break;
                }
                case TYPE_INFO_PRIVACY: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == linksDividerRow) {
                        cell.setText(null);
                        cell.setBackground(Theme.getThemedDrawable(mContext,
                                R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    }
                    break;
                }
            }
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == TYPE_TEXT;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == aboutRow) {
                return TYPE_ABOUT;
            } else if (position == categoriesHeaderRow || position == linksHeaderRow) {
                return TYPE_HEADER;
            } else if (position == categoriesDividerRow) {
                return TYPE_SHADOW;
            } else if (position == linksDividerRow) {
                return TYPE_INFO_PRIVACY;
            }
            return TYPE_TEXT;
        }
    }
}
