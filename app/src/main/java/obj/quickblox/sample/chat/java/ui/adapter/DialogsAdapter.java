package obj.quickblox.sample.chat.java.ui.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.db.QbUsersDbManager;
import obj.quickblox.sample.chat.java.ui.Callback.Popup_click_adapter;
import obj.quickblox.sample.chat.java.ui.Model.Contact_Model;
import obj.quickblox.sample.chat.java.ui.activity.ChatActivity;
import obj.quickblox.sample.chat.java.ui.activity.Chat_profile;
import obj.quickblox.sample.chat.java.ui.activity.Group_Details_View;
import obj.quickblox.sample.chat.java.ui.fragments.Chat_Fragment;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.ResourceUtils;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.UiUtils;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;
import obj.quickblox.sample.chat.java.utils.qb.QbDialogUtils;
import obj.quickblox.sample.chat.java.utils.qb.QbUsersHolder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static obj.quickblox.sample.chat.java.ui.activity.ChatActivity.EXTRA_DIALOG_ID;
import static obj.quickblox.sample.chat.java.ui.activity.Chat_profile.QB_User_Id;

public class DialogsAdapter extends BaseAdapter {
    private Context context;
    private List<QBChatDialog> selectedItems = new ArrayList<>();
    private List<QBChatDialog> dialogs;
    private int REQUEST_DIALOG_ID_FOR_UPDATE=165;
    private int OFFLINE_REQUEST_DIALOG_ID_FOR_UPDATE=143;
    Chat_Fragment chat_fragment;
    Popup_click_adapter popup_click_adapter;
    public DialogsAdapter(Context context, List<QBChatDialog> dialogs) {
        this.context = context;
        this.dialogs = dialogs;
      
    }
    public DialogsAdapter(Context context, List<QBChatDialog> dialogs, Chat_Fragment chat_fragment) {
        this.context = context;
        this.dialogs = dialogs;
        this.chat_fragment=chat_fragment;
        this.popup_click_adapter = chat_fragment;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_dialog, parent, false);

            holder = new ViewHolder();
            holder.rootLayout = (ViewGroup) convertView.findViewById(R.id.root);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.text_dialog_name);
            holder.lastMessageTextView = (TextView) convertView.findViewById(R.id.text_dialog_last_message);
            holder.dialogImageView = (ImageView) convertView.findViewById(R.id.image_dialog_icon);
            holder.unreadCounterTextView = (TextView) convertView.findViewById(R.id.text_dialog_unread_count);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        QBChatDialog dialog = getItem(position);
        if (dialog.getType().equals(QBDialogType.GROUP)) {
            try{
                Picasso.get().load(dialog.getPhoto()) .placeholder(R.drawable.group_icon).into( holder.dialogImageView);
            }catch (Exception e)
            {Picasso.get().load("") .placeholder(R.drawable.group_icon).into( holder.dialogImageView);}


        } else {

            holder.dialogImageView.setImageDrawable(null);
            try{
                if (QbDialogUtils.getDialoProfile(dialog).equalsIgnoreCase(""))
                {
                    holder.dialogImageView.setBackground(context.getResources().getDrawable(R.drawable.default_user_image));
                }else
                {
                    Picasso.get()
                            .load(QbDialogUtils.getDialoProfile(dialog))
                            .placeholder(R.drawable.default_user_image)
                            .into( holder.dialogImageView);
                }

            }catch (Exception e)
            {
                holder.dialogImageView.setBackground(context.getResources().getDrawable(R.drawable.default_user_image));
            }



        }
        holder.dialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (dialog.getType().equals(QBDialogType.GROUP))
                    {
                        Group_Details_View.Group_Dialog=getItem(position);
                        Intent intent = new Intent(context, Group_Details_View.class);
                        context.startActivity(intent);

                    }else
                    {
                        QBChatDialog selectedDialog = (QBChatDialog) dialogs.get(position);
                        QBUser user = QbUsersHolder.getInstance().getUserById(selectedDialog.getRecipientId());
                        Intent intent = new Intent(context, Chat_profile.class);
                        QB_User_Id = String.valueOf(selectedDialog.getRecipientId());
                        intent.putExtra("User_Name",user.getFullName());
                        intent.putExtra("User_Login",user.getLogin());
                        intent.putExtra("User_Image_url",user.getWebsite());
                        context.startActivity(intent);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        });

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isOnline(context))
                {
                    final QBChatDialog selectedDialog = (QBChatDialog) dialogs.get(position);
                    if (ChatHelper.getInstance().isLogged()) {
                        ChatActivity.startForResult((Activity) context, REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog);
                    } else {
                        ChatHelper.getInstance().loginToChat(ChatHelper.getCurrentUser(), new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                ChatActivity.startForResult((Activity) context, REQUEST_DIALOG_ID_FOR_UPDATE, selectedDialog);
                            }

                            @Override
                            public void onError(QBResponseException e) {
                            }
                        });
                    }
                }else
                {
                    ArrayList<QBChatMessage> qbChatMessages = new ArrayList<>();
                    for (int i=0 ; i<SharedPrefsHelper.getInstance().getQBChatMessage_Offline().size(); i++)
                    {
                        if (SharedPrefsHelper.getInstance().getQBChatMessage_Offline().get(i).size()>0)
                        {
                            if (SharedPrefsHelper.getInstance().getQBChatMessage_Offline().get(i).get(0).getDialogId().equalsIgnoreCase(dialogs.get(position).getDialogId()))
                            {
                                qbChatMessages = SharedPrefsHelper.getInstance().getQBChatMessage_Offline().get(i);
                                break;
                            }
                        }
                    }QBChatDialog selectedDialog = (QBChatDialog) dialogs.get(position);
                    Intent intent_c = new Intent(context,ChatActivity.class);
                    intent_c.putExtra("AllChat",qbChatMessages);
                    intent_c.putExtra(EXTRA_DIALOG_ID,selectedDialog);
                    intent_c.putExtra("USER_NAME",QbDialogUtils.getDialogName(dialog));
                    context.startActivity(intent_c);

                }

            }
        });

        View finalConvertView = convertView;
        holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup_click_adapter.onLong_Click(finalConvertView,position, dialogs.get(position),v);
                return true;
            }
        });






        holder.nameTextView.setText(QbDialogUtils.getDialogName(dialog));





        holder.lastMessageTextView.setText(prepareTextLastMessage(dialog));

        int unreadMessagesCount = getUnreadMsgCount(dialog);
        if (unreadMessagesCount == 0) {
            holder.unreadCounterTextView.setVisibility(View.GONE);
        } else {
            holder.unreadCounterTextView.setVisibility(View.VISIBLE);
            holder.unreadCounterTextView.setText(String.valueOf(unreadMessagesCount > 99 ? "99+" : unreadMessagesCount));
        }

        holder.rootLayout.setBackgroundColor(isItemSelected(position) ? ResourceUtils.getColor(R.color.selected_list_item_color) :
                ResourceUtils.getColor(android.R.color.transparent));


       /* ArrayList<QBChatDialog> textList = new ArrayList<>();
        textList.clear();
        ArrayList<QBChatDialog> textListd= SharedPrefsHelper.getInstance().Get_user_array();

        for (int i=0 ; i<textList.size() ; i++)
        {
            if (!dialogs.get(position).equals(textList.get(i)))
            {
                convertView.setVisibility(View.VISIBLE);
            }else {
                convertView.setVisibility(View.GONE);
            }

        }*/

        return convertView;
    }

    @Override
    public QBChatDialog getItem(int position) {
        return dialogs.get(position);
    }

    @Override
    public long getItemId(int id) {
        return (long) id;
    }

    @Override
    public int getCount() {
        return dialogs != null ? dialogs.size() : 0;
    }

    public List<QBChatDialog> getSelectedItems() {
        return selectedItems;
    }

    private boolean isItemSelected(Integer position) {
        return !selectedItems.isEmpty() && selectedItems.contains(getItem(position));
    }

    private int getUnreadMsgCount(QBChatDialog chatDialog) {
        Integer unreadMessageCount = chatDialog.getUnreadMessageCount();
        if (unreadMessageCount == null) {
            unreadMessageCount = 0;
        }
        return unreadMessageCount;
    }

    public void remove_item(int i)
    {
        dialogs.remove(i);
        notifyDataSetChanged();

    }

    private boolean isLastMessageAttachment(QBChatDialog dialog) {
        String lastMessage = dialog.getLastMessage();
        Integer lastMessageSenderId = dialog.getLastMessageUserId();
        return TextUtils.isEmpty(lastMessage) && lastMessageSenderId != null;
    }

    private String prepareTextLastMessage(QBChatDialog chatDialog) {
        if (isLastMessageAttachment(chatDialog)) {
            return context.getString(R.string.chat_attachment);
        } else {
            return chatDialog.getLastMessage();
        }
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void updateList(List<QBChatDialog> dialogs) {
        this.dialogs = dialogs;
        notifyDataSetChanged();
    }

    public void selectItem(QBChatDialog item) {
        if (selectedItems.contains(item)) {
            return;
        }
        selectedItems.add(item);
        notifyDataSetChanged();
    }

    public void toggleSelection(QBChatDialog item) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
        } else {
            selectedItems.add(item);
        }
        notifyDataSetChanged();
    }

    public void setFilter(List<QBChatDialog> filter)
    {
        this.dialogs = filter;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ViewGroup rootLayout;
        ImageView dialogImageView;
        TextView nameTextView;
        TextView lastMessageTextView;
        TextView unreadCounterTextView;
    }
}