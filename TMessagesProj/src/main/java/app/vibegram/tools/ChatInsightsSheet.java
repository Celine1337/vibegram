package app.vibegram.tools;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.LocaleController.formatString;
import static org.telegram.messenger.LocaleController.getString;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

/**
 * Displays dialog diagnostics and single-pass loaded message statistics for a chat.
 */
public class ChatInsightsSheet extends BottomSheet {

    private final ChatActivity chatActivity;

    public ChatInsightsSheet(ChatActivity chatActivity) {
        super(chatActivity.getParentActivity(), false, chatActivity.getResourceProvider());
        this.chatActivity = chatActivity;
        fixNavigationBar();

        Context context = getContext();
        int accent = getThemedColor(Theme.key_featuredStickers_addButton);

        long dialogId = chatActivity.getDialogId();
        TLRPC.Chat chat = chatActivity.getCurrentChat();
        TLRPC.User user = chatActivity.getCurrentUser();
        TLRPC.EncryptedChat encryptedChat = chatActivity.getCurrentEncryptedChat();

        String chatTitle = resolveTitle(user, chat, dialogId);
        String peerType = resolveType(user, chat, encryptedChat);
        String username = resolveUsername(user, chat);
        int dcId = resolveDcId(user, chat);
        String flags = resolveFlags(user, chat, encryptedChat);
        String botApiId = formatBotApiId(dialogId, chat);
        Stats stats = analyzeMessages(chatActivity.messages);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(8), dp(20), dp(18));

        View handle = new View(context);
        handle.setBackground(Theme.createRoundRectDrawable(dp(2),
                ColorUtils.setAlphaComponent(getThemedColor(Theme.key_dialogTextGray2), 0x66)));
        root.addView(handle, LayoutHelper.createLinear(34, 4, Gravity.CENTER_HORIZONTAL, 0, 0, 0, 14));

        TextView titleView = new TextView(context);
        titleView.setText(getString(R.string.VGToolsInsights));
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        titleView.setTypeface(AndroidUtilities.bold());
        titleView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        root.addView(titleView);

        TextView subtitleView = new TextView(context);
        subtitleView.setText(chatTitle);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        subtitleView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
        subtitleView.setSingleLine(true);
        subtitleView.setEllipsize(TextUtils.TruncateAt.END);
        root.addView(subtitleView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 2, 0, 14));

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        card.setBackground(Theme.createRoundRectDrawable(dp(16), getThemedColor(Theme.key_graySection)));

        addRow(card, context, getString(R.string.VGToolsInsightsDialogId), String.valueOf(dialogId), true);
        if (!TextUtils.isEmpty(botApiId)) {
            addRow(card, context, getString(R.string.VGToolsInsightsBotApiId), botApiId, true);
        }
        addRow(card, context, getString(R.string.VGToolsInsightsType), peerType, false);
        if (!TextUtils.isEmpty(username)) {
            addRow(card, context, getString(R.string.VGToolsInsightsUsername), "@" + username, true);
        }
        if (dcId > 0) {
            addRow(card, context, getString(R.string.VGToolsInsightsDc), "DC" + dcId, false);
        }
        if (!TextUtils.isEmpty(flags)) {
            addRow(card, context, getString(R.string.VGToolsInsightsFlags), flags, false);
        }
        addRow(card, context, getString(R.string.VGToolsInsightsLoaded),
                formatString(R.string.VGToolsInsightsMessages, stats.total, stats.outgoing, stats.incoming), false);

        String mediaSummary = "Text " + stats.textOnly
                + " • Photos " + stats.photos
                + " • Videos " + stats.videos
                + " • Voice/Audio " + stats.audio
                + " • Files/GIF " + stats.docs
                + " • Stickers " + stats.stickers;
        addRow(card, context, getString(R.string.VGToolsInsightsMediaBreakdown), mediaSummary, false);

        root.addView(card, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 0, 0, 14));

        TextView copySummary = new TextView(context);
        copySummary.setText(getString(R.string.VGToolsInsightsCopySummary));
        copySummary.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        copySummary.setTypeface(AndroidUtilities.bold());
        copySummary.setGravity(Gravity.CENTER);
        copySummary.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        copySummary.setBackground(Theme.createSimpleSelectorRoundRectDrawable(
                dp(12), accent, ColorUtils.setAlphaComponent(accent, 0xCC)));
        copySummary.setOnClickListener(v -> {
            StringBuilder summary = new StringBuilder();
            summary.append(chatTitle).append('\n');
            summary.append("Dialog ID: ").append(dialogId).append('\n');
            if (!TextUtils.isEmpty(botApiId)) {
                summary.append("Bot API ID: ").append(botApiId).append('\n');
            }
            summary.append("Type: ").append(peerType).append('\n');
            if (!TextUtils.isEmpty(username)) {
                summary.append("Username: @").append(username).append('\n');
            }
            if (dcId > 0) {
                summary.append("DC: ").append(dcId).append('\n');
            }
            summary.append("Loaded: ").append(stats.total)
                    .append(" (out ").append(stats.outgoing).append(", in ").append(stats.incoming).append(')');
            AndroidUtilities.addToClipboard(summary.toString());
            BulletinFactory.of(getContainer(), resourcesProvider)
                    .createCopyBulletin(getString(R.string.VGToolsInsightsCopied))
                    .show();
        });
        root.addView(copySummary, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 46));

        setCustomView(root);
    }

    private void addRow(LinearLayout parent, Context context, String label, String value, boolean copyable) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dp(5), 0, dp(5));

        TextView labelView = new TextView(context);
        labelView.setText(label);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        labelView.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
        row.addView(labelView);

        TextView valueView = new TextView(context);
        valueView.setText(value);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        valueView.setTypeface(AndroidUtilities.bold());
        valueView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        row.addView(valueView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 0, 1, 0, 0));

        if (copyable) {
            row.setBackground(Theme.createSelectorDrawable(
                    ColorUtils.setAlphaComponent(getThemedColor(Theme.key_dialogTextBlack), 0x12), 2));
            row.setOnClickListener(v -> {
                AndroidUtilities.addToClipboard(value);
                BulletinFactory.of(getContainer(), resourcesProvider)
                        .createCopyBulletin(getString(R.string.VGToolsInsightsCopied))
                        .show();
            });
        }
        parent.addView(row, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
    }

    private static final class Stats {
        int total;
        int outgoing;
        int incoming;
        int textOnly;
        int photos;
        int videos;
        int audio;
        int docs;
        int stickers;
    }

    private static Stats analyzeMessages(ArrayList<MessageObject> messages) {
        Stats stats = new Stats();
        if (messages == null) {
            return stats;
        }
        for (int i = 0, size = messages.size(); i < size; i++) {
            MessageObject msg = messages.get(i);
            if (msg == null || msg.type == MessageObject.TYPE_DATE || msg.messageOwner instanceof TLRPC.TL_messageService) {
                continue;
            }
            stats.total++;
            if (msg.isOutOwner() || msg.isOut()) {
                stats.outgoing++;
            } else {
                stats.incoming++;
            }
            if (msg.isSticker()) {
                stats.stickers++;
            } else if (msg.isPhoto()) {
                stats.photos++;
            } else if (msg.isVideo() || msg.isRoundVideo()) {
                stats.videos++;
            } else if (msg.isVoice() || msg.isMusic()) {
                stats.audio++;
            } else if (msg.isGif() || msg.getDocument() != null) {
                stats.docs++;
            } else {
                stats.textOnly++;
            }
        }
        return stats;
    }

    private static String resolveTitle(TLRPC.User user, TLRPC.Chat chat, long dialogId) {
        if (user != null) {
            return UserObject.getUserName(user);
        }
        if (chat != null && !TextUtils.isEmpty(chat.title)) {
            return chat.title;
        }
        return String.valueOf(dialogId);
    }

    private static String resolveType(TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat != null) return "Secret Chat";
        if (user != null) {
            if (user.self) return "Saved Messages";
            return user.bot ? "Bot" : "Private User";
        }
        if (chat != null) {
            if (chat.forum) return "Forum Supergroup";
            if (ChatObject.isChannelAndNotMegaGroup(chat)) return "Channel";
            if (ChatObject.isMegagroup(chat)) return "Supergroup";
            return "Group";
        }
        return "Dialog";
    }

    private static String resolveUsername(TLRPC.User user, TLRPC.Chat chat) {
        if (user != null) return UserObject.getPublicUsername(user);
        if (chat != null) return ChatObject.getPublicUsername(chat);
        return null;
    }

    private static int resolveDcId(TLRPC.User user, TLRPC.Chat chat) {
        if (user != null && user.photo != null) return user.photo.dc_id;
        if (chat != null && chat.photo != null) return chat.photo.dc_id;
        return 0;
    }

    private static String formatBotApiId(long dialogId, TLRPC.Chat chat) {
        if (chat != null && ChatObject.isChannel(chat)) {
            return "-100" + Math.abs(dialogId);
        }
        return null;
    }

    private static String resolveFlags(TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat) {
        StringBuilder sb = new StringBuilder();
        if (encryptedChat != null) appendFlag(sb, "E2EE");
        if (user != null) {
            if (user.verified) appendFlag(sb, "Verified");
            if (user.premium) appendFlag(sb, "Premium");
            if (user.scam) appendFlag(sb, "Scam");
            if (user.fake) appendFlag(sb, "Fake");
            if (user.support) appendFlag(sb, "Support");
            if (user.restricted) appendFlag(sb, "Restricted");
        }
        if (chat != null) {
            if (chat.verified) appendFlag(sb, "Verified");
            if (chat.forum) appendFlag(sb, "Forum");
            if (chat.noforwards) appendFlag(sb, "Protected");
            if (chat.scam) appendFlag(sb, "Scam");
            if (chat.fake) appendFlag(sb, "Fake");
            if (chat.restricted) appendFlag(sb, "Restricted");
        }
        return sb.toString();
    }

    private static void appendFlag(StringBuilder sb, String flag) {
        if (sb.length() > 0) {
            sb.append(", ");
        }
        sb.append(flag);
    }
}
