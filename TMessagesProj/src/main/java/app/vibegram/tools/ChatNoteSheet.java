package app.vibegram.tools;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.LocaleController.formatString;
import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

/**
 * BottomSheet editor for local per-chat private notes.
 */
public class ChatNoteSheet extends BottomSheet {

    private final Runnable onChanged;

    public ChatNoteSheet(BaseFragment fragment, int account, long dialogId, String chatTitle, Runnable onChanged) {
        super(fragment.getParentActivity(), true, fragment.getResourceProvider());
        this.onChanged = onChanged;
        fixNavigationBar();

        Context context = getContext();
        int accent = getThemedColor(Theme.key_featuredStickers_addButton);
        ChatNotesManager.ChatNote existing = ChatNotesManager.getNote(account, dialogId);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(8), dp(20), dp(18));

        View handle = new View(context);
        handle.setBackground(Theme.createRoundRectDrawable(dp(2),
                ColorUtils.setAlphaComponent(getThemedColor(Theme.key_dialogTextGray2), 0x66)));
        root.addView(handle, LayoutHelper.createLinear(34, 4, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 14));

        TextView titleView = new TextView(context);
        titleView.setText(getString(R.string.VGToolsChatNote));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        titleView.setTypeface(AndroidUtilities.bold());
        titleView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        root.addView(titleView);

        String displayTitle = TextUtils.isEmpty(chatTitle) ? String.valueOf(dialogId) : chatTitle;
        TextView subtitleView = new TextView(context);
        subtitleView.setText(existing != null && existing.updatedAt > 0
                ? displayTitle + " • " + formatString(R.string.VGToolsChatNoteUpdated,
                        LocaleController.formatDateTime(existing.updatedAt / 1000L, true))
                : displayTitle);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        subtitleView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
        subtitleView.setSingleLine(true);
        subtitleView.setEllipsize(TextUtils.TruncateAt.END);
        root.addView(subtitleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 3, 0, 14));

        final EditTextBoldCursor noteField = new EditTextBoldCursor(context);
        noteField.setHint(getString(R.string.VGToolsChatNoteHint));
        noteField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        noteField.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        noteField.setHintTextColor(getThemedColor(Theme.key_dialogTextGray2));
        noteField.setCursorColor(accent);
        noteField.setCursorSize(dp(18));
        noteField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        noteField.setMinLines(4);
        noteField.setMaxLines(9);
        noteField.setGravity(Gravity.TOP | Gravity.START);
        noteField.setBackground(Theme.createRoundRectDrawable(dp(14), getThemedColor(Theme.key_graySection)));
        noteField.setPadding(dp(14), dp(12), dp(14), dp(12));
        if (existing != null) {
            noteField.setText(existing.text);
            noteField.setSelection(existing.text.length());
        }
        root.addView(noteField, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 14));

        LinearLayout actions = new LinearLayout(context);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        if (existing != null) {
            TextView clearButton = new TextView(context);
            clearButton.setText(getString(R.string.Delete));
            clearButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            clearButton.setTypeface(AndroidUtilities.bold());
            clearButton.setGravity(Gravity.CENTER);
            clearButton.setTextColor(getThemedColor(Theme.key_text_RedRegular));
            clearButton.setPadding(dp(16), dp(12), dp(16), dp(12));
            clearButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                    dp(12), getThemedColor(Theme.key_graySection),
                    ColorUtils.setAlphaComponent(getThemedColor(Theme.key_text_RedRegular), 0x22)));
            clearButton.setOnClickListener(v -> {
                ChatNotesManager.deleteNote(account, dialogId);
                dismiss();
                if (this.onChanged != null) {
                    this.onChanged.run();
                }
                BulletinFactory.of(fragment)
                        .createSimpleBulletin(R.raw.ic_delete, getString(R.string.VGToolsChatNoteCleared))
                        .show();
            });
            actions.addView(clearButton, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, 46, 0, 0, 10, 0));
        }

        TextView saveButton = new TextView(context);
        saveButton.setText(getString(R.string.Save));
        saveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        saveButton.setTypeface(AndroidUtilities.bold());
        saveButton.setGravity(Gravity.CENTER);
        saveButton.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        saveButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                dp(12), accent, ColorUtils.setAlphaComponent(accent, 0xCC)));
        saveButton.setOnClickListener(v -> {
            String value = noteField.getText() == null ? "" : noteField.getText().toString();
            boolean saved = ChatNotesManager.saveNote(account, dialogId, displayTitle, value);
            dismiss();
            if (this.onChanged != null) {
                this.onChanged.run();
            }
            BulletinFactory.of(fragment)
                    .createSimpleBulletin(
                            saved ? R.raw.contact_check : R.raw.ic_delete,
                            getString(saved ? R.string.VGToolsChatNoteSaved : R.string.VGToolsChatNoteCleared))
                    .show();
        });
        actions.addView(saveButton, LayoutHelper.createLinear(0, 46, 1f));

        root.addView(actions, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        setCustomView(root);
    }
}
