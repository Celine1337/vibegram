package app.vibegram.tools;

import static org.telegram.messenger.LocaleController.formatString;
import static org.telegram.messenger.LocaleController.getString;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;

import java.util.List;

import app.vibegram.VibeConfig;
import tw.nekomimi.nekogram.settings.BaseNekoSettingsActivity;
import tw.nekomimi.nekogram.ui.cells.HeaderCell;

/**
 * Vibe Tools: Quick Snippets, Private Chat Notes, and Performance/Cache optimization controls.
 */
public class VibeToolsActivity extends BaseNekoSettingsActivity {

    private static final int[] AUTO_TRIM_VALUES = {0, 512, 1024, 2048};
    private static final int[] AUTO_TRIM_LABELS = {
            R.string.VGToolsAutoTrimOff,
            R.string.VGToolsAutoTrim512,
            R.string.VGToolsAutoTrim1024,
            R.string.VGToolsAutoTrim2048,
    };

    private int productivityHeaderRow;
    private int snippetsRow;
    private int notesRow;
    private int showInChatMenuRow;
    private int productivityInfoRow;

    private int performanceHeaderRow;
    private int cacheFootprintRow;
    private int clearCacheRow;
    private int autoTrimRow;
    private int optimizeScrollRow;
    private int memoryUsageRow;
    private int performanceInfoRow;

    private PerformanceOptimizer.CacheStats cacheStats;
    private boolean scanningCache;

    @Override
    protected void updateRows() {
        super.updateRows();

        productivityHeaderRow = addRow("productivityHeader");
        snippetsRow = addRow("snippets");
        notesRow = addRow("notes");
        showInChatMenuRow = addRow("showInChatMenu");
        productivityInfoRow = addRow();

        performanceHeaderRow = addRow("performanceHeader");
        cacheFootprintRow = addRow("cacheFootprint");
        clearCacheRow = addRow("clearCache");
        autoTrimRow = addRow("autoTrim");
        optimizeScrollRow = addRow("optimizeScroll");
        memoryUsageRow = addRow("memoryUsage");
        performanceInfoRow = addRow();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCacheStats();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void refreshCacheStats() {
        if (scanningCache) {
            return;
        }
        scanningCache = true;
        PerformanceOptimizer.scanCacheAsync(stats -> {
            scanningCache = false;
            cacheStats = stats;
            if (listAdapter != null && cacheFootprintRow >= 0) {
                listAdapter.notifyItemChanged(cacheFootprintRow, PARTIAL);
            }
        });
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.VGToolsTitle);
    }

    @Override
    public int getSearchGuid() {
        return 24600;
    }

    @Override
    public int getSearchIcon() {
        return R.drawable.msg_instant;
    }

    @Override
    public String getSearchPrefix() {
        return "VibeTools";
    }

    @Override
    protected String getKey() {
        return "vibegram_tools";
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == snippetsRow) {
            QuickSnippetsSheet sheet = new QuickSnippetsSheet(this, null);
            sheet.setOnDismissListener(d -> {
                if (listAdapter != null && snippetsRow >= 0) {
                    listAdapter.notifyItemChanged(snippetsRow, PARTIAL);
                }
            });
            showDialog(sheet);
        } else if (position == notesRow) {
            showNotesBrowserDialog();
        } else if (position == showInChatMenuRow) {
            boolean enabled = VibeConfig.showQuickToolsInChatMenu.toggleConfigBool();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(enabled);
            }
        } else if (position == cacheFootprintRow) {
            refreshCacheStats();
        } else if (position == clearCacheRow) {
            confirmClearCache();
        } else if (position == autoTrimRow) {
            showAutoTrimSelector(view);
        } else if (position == optimizeScrollRow) {
            boolean enabled = VibeConfig.optimizeScrollCache.toggleConfigBool();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(enabled);
            }
        } else if (position == memoryUsageRow) {
            PerformanceOptimizer.trimMemoryCaches();
            if (listAdapter != null && memoryUsageRow >= 0) {
                listAdapter.notifyItemChanged(memoryUsageRow, PARTIAL);
            }
            BulletinFactory.of(this)
                    .createSimpleBulletin(R.raw.done, getString(R.string.VGToolsMemoryTrimmed))
                    .show();
        }
    }

    private void showNotesBrowserDialog() {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        final List<ChatNotesManager.ChatNote> notes = ChatNotesManager.list(currentAccount);
        if (notes.isEmpty()) {
            AlertDialog.Builder empty = new AlertDialog.Builder(activity, getResourceProvider());
            empty.setTitle(getString(R.string.VGToolsNotes));
            empty.setMessage(getString(R.string.VGToolsNotesEmpty));
            empty.setPositiveButton(getString(R.string.OK), null);
            showDialog(empty.create());
            return;
        }
        CharSequence[] labels = new CharSequence[notes.size()];
        for (int i = 0; i < notes.size(); i++) {
            ChatNotesManager.ChatNote note = notes.get(i);
            String preview = note.text.replace('\n', ' ');
            if (preview.length() > 42) {
                preview = preview.substring(0, 42) + "…";
            }
            labels[i] = note.chatTitle + " — " + preview;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, getResourceProvider());
        builder.setTitle(getString(R.string.VGToolsNotes));
        builder.setItems(labels, (dialog, which) -> {
            ChatNotesManager.ChatNote picked = notes.get(which);
            showDialog(new ChatNoteSheet(
                    VibeToolsActivity.this,
                    picked.account,
                    picked.dialogId,
                    picked.chatTitle,
                    () -> {
                        if (listAdapter != null && notesRow >= 0) {
                            listAdapter.notifyItemChanged(notesRow, PARTIAL);
                        }
                    }));
        });
        showDialog(builder.create());
    }

    private void confirmClearCache() {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, getResourceProvider());
        builder.setTitle(getString(R.string.VGToolsClearCacheNow));
        builder.setMessage(getString(R.string.VGToolsClearCacheConfirm));
        builder.setNegativeButton(getString(R.string.Cancel), null);
        builder.setPositiveButton(getString(R.string.Clear), (dialog, which) ->
                PerformanceOptimizer.cleanCacheAsync((freedBytes, remaining) -> {
                    cacheStats = remaining;
                    if (listAdapter != null && cacheFootprintRow >= 0) {
                        listAdapter.notifyItemChanged(cacheFootprintRow, PARTIAL);
                    }
                    BulletinFactory.of(this)
                            .createSimpleBulletin(R.raw.ic_delete, formatString(
                                    R.string.VGToolsCacheCleaned,
                                    AndroidUtilities.formatFileSize(freedBytes)))
                            .show();
                }));
        showDialog(builder.create());
    }

    private void showAutoTrimSelector(View rowView) {
        Activity activity = getParentActivity();
        if (activity == null) {
            return;
        }
        CharSequence[] options = new CharSequence[AUTO_TRIM_LABELS.length];
        for (int i = 0; i < AUTO_TRIM_LABELS.length; i++) {
            options[i] = getString(AUTO_TRIM_LABELS[i]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, getResourceProvider());
        builder.setTitle(getString(R.string.VGToolsAutoTrim));
        builder.setItems(options, (dialog, which) -> {
            VibeConfig.autoTrimCacheMb.setConfigInt(AUTO_TRIM_VALUES[which]);
            if (rowView instanceof TextSettingsCell) {
                ((TextSettingsCell) rowView).setTextAndValue(
                        getString(R.string.VGToolsAutoTrim), formatAutoTrimValue(), true);
            } else if (listAdapter != null && autoTrimRow >= 0) {
                listAdapter.notifyItemChanged(autoTrimRow, PARTIAL);
            }
        });
        showDialog(builder.create());
    }

    private String formatAutoTrimValue() {
        int value = VibeConfig.autoTrimCacheMb();
        for (int i = 0; i < AUTO_TRIM_VALUES.length; i++) {
            if (AUTO_TRIM_VALUES[i] == value) {
                return getString(AUTO_TRIM_LABELS[i]);
            }
        }
        return value + " MB";
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, boolean partial) {
            switch (holder.getItemViewType()) {
                case TYPE_HEADER: {
                    HeaderCell cell = (HeaderCell) holder.itemView;
                    if (position == productivityHeaderRow) {
                        cell.setText(getString(R.string.VGToolsProductivityHeader));
                    } else if (position == performanceHeaderRow) {
                        cell.setText(getString(R.string.VGToolsPerformanceHeader));
                    }
                    break;
                }
                case TYPE_SETTINGS: {
                    TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                    if (position == snippetsRow) {
                        cell.setTextAndValue(getString(R.string.VGToolsSnippets),
                                formatString(R.string.VGToolsSnippetsCount, QuickSnippetsManager.count()), true);
                    } else if (position == notesRow) {
                        cell.setTextAndValue(getString(R.string.VGToolsNotes),
                                formatString(R.string.VGToolsNotesCount, ChatNotesManager.count(currentAccount)), true);
                    } else if (position == cacheFootprintRow) {
                        String val = cacheStats == null
                                ? getString(R.string.VGToolsCacheCalculating)
                                : formatString(R.string.VGToolsCacheBreakdown,
                                        AndroidUtilities.formatFileSize(cacheStats.cacheAndTempBytes + cacheStats.mediaBytes),
                                        AndroidUtilities.formatFileSize(cacheStats.logsBytes));
                        cell.setTextAndValue(getString(R.string.VGToolsCacheSize), val, true);
                    } else if (position == autoTrimRow) {
                        cell.setTextAndValue(getString(R.string.VGToolsAutoTrim), formatAutoTrimValue(), true);
                    } else if (position == memoryUsageRow) {
                        PerformanceOptimizer.MemoryStats mem = PerformanceOptimizer.readMemoryStats();
                        cell.setTextAndValue(getString(R.string.VGToolsMemoryUsage),
                                formatString(R.string.VGToolsMemoryValue, mem.usedMb, mem.maxMb), false);
                    }
                    break;
                }
                case TYPE_TEXT: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == clearCacheRow) {
                        cell.setColors(Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_windowBackgroundWhiteBlackText);
                        cell.setTextAndIcon(getString(R.string.VGToolsClearCacheNow), R.drawable.msg_clearcache, true);
                    }
                    break;
                }
                case TYPE_CHECK: {
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == showInChatMenuRow) {
                        cell.setTextAndCheck(getString(R.string.VGToolsShowInChatMenu),
                                VibeConfig.showQuickToolsInChatMenu(), false);
                    } else if (position == optimizeScrollRow) {
                        cell.setTextAndValueAndCheck(getString(R.string.VGToolsOptimizeScroll),
                                getString(R.string.VGToolsOptimizeScrollInfo),
                                VibeConfig.optimizeScrollCache(), true, true);
                    }
                    break;
                }
                case TYPE_INFO_PRIVACY: {
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    boolean bottom = position == performanceInfoRow;
                    if (position == productivityInfoRow) {
                        cell.setText(getString(R.string.VGToolsShowInChatMenuInfo));
                    } else if (position == performanceInfoRow) {
                        cell.setText(getString(R.string.VGToolsAutoTrimInfo));
                    } else {
                        cell.setText(null);
                    }
                    cell.setBackground(Theme.getThemedDrawable(mContext,
                            bottom ? R.drawable.greydivider_bottom : R.drawable.greydivider,
                            Theme.key_windowBackgroundGrayShadow));
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == productivityHeaderRow || position == performanceHeaderRow) {
                return TYPE_HEADER;
            } else if (position == productivityInfoRow || position == performanceInfoRow) {
                return TYPE_INFO_PRIVACY;
            } else if (position == showInChatMenuRow || position == optimizeScrollRow) {
                return TYPE_CHECK;
            } else if (position == clearCacheRow) {
                return TYPE_TEXT;
            }
            return TYPE_SETTINGS;
        }
    }
}
