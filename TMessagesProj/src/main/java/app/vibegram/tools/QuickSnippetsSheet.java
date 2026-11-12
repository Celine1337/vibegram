package app.vibegram.tools;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.LocaleController.formatString;
import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

import java.util.List;

/**
 * BottomSheet for browsing, creating, searching, and inserting Quick Snippets.
 */
public class QuickSnippetsSheet extends BottomSheet {

    public interface SnippetSelectListener {
        void onSnippetSelected(String text);
    }

    private final BaseFragment fragment;
    private final SnippetSelectListener selectListener;
    private final int accent;
    private final EditTextBoldCursor searchField;
    private final LinearLayout listContainer;
    private final TextView emptyView;
    private String query = "";

    public QuickSnippetsSheet(BaseFragment fragment, SnippetSelectListener selectListener) {
        super(fragment.getParentActivity(), true, fragment.getResourceProvider());
        this.fragment = fragment;
        this.selectListener = selectListener;
        fixNavigationBar();

        Context context = getContext();
        accent = getThemedColor(Theme.key_featuredStickers_addButton);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(8), dp(18), dp(16));

        View handle = new View(context);
        handle.setBackground(Theme.createRoundRectDrawable(dp(2),
                ColorUtils.setAlphaComponent(getThemedColor(Theme.key_dialogTextGray2), 0x66)));
        root.addView(handle, LayoutHelper.createLinear(34, 4, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 14));

        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView titleView = new TextView(context);
        titleView.setText(getString(R.string.VGToolsSnippets));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        titleView.setTypeface(AndroidUtilities.bold());
        titleView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        header.addView(titleView, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f));

        TextView addButton = new TextView(context);
        addButton.setText("+ " + getString(R.string.VGToolsSnippetAdd));
        addButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        addButton.setTypeface(AndroidUtilities.bold());
        addButton.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        addButton.setPadding(dp(12), dp(7), dp(12), dp(7));
        addButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                dp(10), accent, ColorUtils.setAlphaComponent(accent, 0xCC)));
        addButton.setOnClickListener(v -> openEditorDialog(null));
        header.addView(addButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));
        root.addView(header, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 12));

        searchField = new EditTextBoldCursor(context);
        searchField.setHint(getString(R.string.VGToolsSnippetsSearchHint));
        searchField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        searchField.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        searchField.setHintTextColor(getThemedColor(Theme.key_dialogTextGray2));
        searchField.setCursorColor(accent);
        searchField.setCursorSize(dp(18));
        searchField.setSingleLine(true);
        searchField.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchField.setBackground(Theme.createRoundRectDrawable(dp(12), getThemedColor(Theme.key_graySection)));
        searchField.setPadding(dp(14), dp(10), dp(14), dp(10));
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s == null ? "" : s.toString();
                refreshList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        root.addView(searchField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 12));

        listContainer = new LinearLayout(context);
        listContainer.setOrientation(LinearLayout.VERTICAL);

        emptyView = new TextView(context);
        emptyView.setText(getString(R.string.VGToolsSnippetsEmpty));
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        emptyView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setPadding(dp(16), dp(28), dp(16), dp(28));
        root.addView(emptyView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        ScrollView scrollView = new ScrollView(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int maxH = (int) (AndroidUtilities.displaySize.y * 0.48f);
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxH, MeasureSpec.AT_MOST));
            }
        };
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(listContainer, new FrameLayout.LayoutParams(
                LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        root.addView(scrollView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        setCustomView(root);
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAllViews();
        List<QuickSnippetsManager.Snippet> items = QuickSnippetsManager.list(query);
        if (items.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        emptyView.setVisibility(View.GONE);
        Context context = getContext();
        int cardColor = getThemedColor(Theme.key_graySection);

        for (int i = 0; i < items.size(); i++) {
            final QuickSnippetsManager.Snippet item = items.get(i);
            LinearLayout card = new LinearLayout(context);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setPadding(dp(14), dp(11), dp(8), dp(11));
            card.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                    dp(14), cardColor, ColorUtils.setAlphaComponent(accent, 0x22)));

            LinearLayout textCol = new LinearLayout(context);
            textCol.setOrientation(LinearLayout.VERTICAL);

            TextView title = new TextView(context);
            title.setText(item.title);
            title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            title.setTypeface(AndroidUtilities.bold());
            title.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            title.setSingleLine(true);
            title.setEllipsize(TextUtils.TruncateAt.END);
            textCol.addView(title);

            TextView preview = new TextView(context);
            preview.setText(item.text);
            preview.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            preview.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
            preview.setMaxLines(2);
            preview.setEllipsize(TextUtils.TruncateAt.END);
            textCol.addView(preview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 0));

            card.addView(textCol, LayoutHelper.createLinear(0, LayoutHelper.WRAP_CONTENT, 1f, Gravity.CENTER_VERTICAL, 0, 0, 8, 0));

            ImageView editBtn = makeIconButton(context, R.drawable.msg_edit, getThemedColor(Theme.key_dialogTextGray2));
            editBtn.setOnClickListener(v -> openEditorDialog(item));
            card.addView(editBtn, LayoutHelper.createLinear(36, 36, Gravity.CENTER_VERTICAL));

            ImageView deleteBtn = makeIconButton(context, R.drawable.msg_delete, getThemedColor(Theme.key_text_RedRegular));
            deleteBtn.setOnClickListener(v -> {
                QuickSnippetsManager.delete(item.id);
                refreshList();
                BulletinFactory.of(getContainer(), resourcesProvider)
                        .createSimpleBulletin(R.raw.ic_delete, getString(R.string.VGToolsSnippetDeleted))
                        .show();
            });
            card.addView(deleteBtn, LayoutHelper.createLinear(36, 36, Gravity.CENTER_VERTICAL));

            card.setOnClickListener(v -> {
                QuickSnippetsManager.markUsed(item.id);
                if (selectListener != null) {
                    selectListener.onSnippetSelected(item.text);
                    dismiss();
                    if (fragment != null) {
                        BulletinFactory.of(fragment)
                                .createSimpleBulletin(R.raw.copy, getString(R.string.VGToolsSnippetInserted))
                                .show();
                    }
                } else {
                    AndroidUtilities.addToClipboard(item.text);
                    BulletinFactory.of(getContainer(), resourcesProvider)
                            .createCopyBulletin(getString(R.string.VGToolsSnippetCopied))
                            .show();
                }
            });

            int bottomMargin = i == items.size() - 1 ? 0 : 8;
            listContainer.addView(card, LayoutHelper.createLinear(
                    LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, bottomMargin));
        }
    }

    private ImageView makeIconButton(Context context, int resId, int tintColor) {
        ImageView btn = new ImageView(context);
        btn.setImageResource(resId);
        btn.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.MULTIPLY));
        btn.setScaleType(ImageView.ScaleType.CENTER);
        btn.setBackground(Theme.createSelectorDrawable(
                ColorUtils.setAlphaComponent(tintColor, 0x22), Theme.RIPPLE_MASK_CIRCLE_20DP));
        return btn;
    }

    private void openEditorDialog(QuickSnippetsManager.Snippet existing) {
        Context context = getContext();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(22), dp(8), dp(22), dp(4));

        final EditTextBoldCursor titleInput = new EditTextBoldCursor(context);
        titleInput.setHint(getString(R.string.VGToolsSnippetTitleHint));
        titleInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        titleInput.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        titleInput.setHintTextColor(getThemedColor(Theme.key_dialogTextGray2));
        titleInput.setSingleLine(true);
        if (existing != null) {
            titleInput.setText(existing.title);
            titleInput.setSelection(existing.title.length());
        }
        container.addView(titleInput, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 12));

        final EditTextBoldCursor bodyInput = new EditTextBoldCursor(context);
        bodyInput.setHint(getString(R.string.VGToolsSnippetBodyHint));
        bodyInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        bodyInput.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        bodyInput.setHintTextColor(getThemedColor(Theme.key_dialogTextGray2));
        bodyInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        bodyInput.setMinLines(3);
        bodyInput.setMaxLines(6);
        bodyInput.setGravity(Gravity.TOP | Gravity.START);
        if (existing != null) {
            bodyInput.setText(existing.text);
        }
        container.addView(bodyInput, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(getString(existing == null ? R.string.VGToolsSnippetAdd : R.string.VGToolsSnippetEdit));
        builder.setView(container);
        builder.setNegativeButton(getString(R.string.Cancel), null);
        builder.setPositiveButton(getString(R.string.Save), (dialog, which) -> {
            String t = titleInput.getText() == null ? "" : titleInput.getText().toString();
            String b = bodyInput.getText() == null ? "" : bodyInput.getText().toString();
            if (QuickSnippetsManager.upsert(existing == null ? null : existing.id, t, b) == null) {
                BulletinFactory.of(getContainer(), resourcesProvider)
                        .createErrorBulletin(getString(R.string.VGToolsSnippetEmptyError))
                        .show();
                return;
            }
            refreshList();
            BulletinFactory.of(getContainer(), resourcesProvider)
                    .createSimpleBulletin(R.raw.contact_check, getString(R.string.VGToolsSnippetSaved))
                    .show();
        });
        builder.show();
    }
}
