package com.TuDime.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import com.TuDime.R;
import com.TuDime.ui.Callback.Popup_click_adapter;
import com.TuDime.ui.Model.Archive_Model;
import com.TuDime.ui.activity.Archive_Chat;
import com.TuDime.ui.activity.ChatActivity;
import com.TuDime.ui.activity.Chat_profile;
import com.TuDime.utils.chat.ChatHelper;
import com.TuDime.utils.qb.QbDialogUtils;
import com.TuDime.utils.qb.QbUsersHolder;

import static com.TuDime.ui.activity.Chat_profile.QB_User_Id;

/**
 * Created by KRISHNENDU MANNA on 11,May,2020
 */
public class Archive_Adapter extends RecyclerView.Adapter<Archive_Adapter.MyViewHolder> {

    ArrayList<Archive_Model> horizontalList;
    private Context context;
    Popup_click_adapter popup_click_adapter;
    public Archive_Adapter(Context context,  ArrayList<Archive_Model> horizontalList) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.popup_click_adapter = (Archive_Chat)context;
    }


    @Override
    public int getItemCount() {
        return horizontalList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ViewGroup rootLayout;
        ImageView dialogImageView;
        TextView nameTextView;
        TextView lastMessageTextView;
        TextView unreadCounterTextView;

        public MyViewHolder(View convertView) {
            super(convertView);
          rootLayout = (ViewGroup) convertView.findViewById(R.id.root);
          nameTextView = (TextView) convertView.findViewById(R.id.text_dialog_name);
          lastMessageTextView = (TextView) convertView.findViewById(R.id.text_dialog_last_message);
          dialogImageView = (ImageView) convertView.findViewById(R.id.image_dialog_icon);
          unreadCounterTextView = (TextView) convertView.findViewById(R.id.text_dialog_unread_count);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dialog, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        JSONObject jsonObject;
        String selectedDialog = horizontalList.get(position).getCHAT_DIALOG_ALL();
        Gson gson = new Gson();
        QBChatDialog dialog = gson.fromJson(selectedDialog , QBChatDialog.class);


        if (dialog.getType().equals(QBDialogType.GROUP)) {
            holder.nameTextView.setText(dialog.getName());
            holder.nameTextView.setText(QbDialogUtils.getDialogName(dialog));
        } else {
            Integer opponentId = dialog.getOccupants().get(1);
            QBUser user = QbUsersHolder.getInstance().getUserById(opponentId);
            try {
                holder.nameTextView.setText(user.getFullName());
            }catch (Exception e)
            {

            }

            holder.dialogImageView.setImageDrawable(null);
            try{
                if (user.getWebsite().equalsIgnoreCase(""))
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
        holder.lastMessageTextView.setText(prepareTextLastMessage(dialog));
        if (dialog.getType().equals(QBDialogType.GROUP)) {
            holder.dialogImageView.setImageResource(R.drawable.group_icon);
        }

        }
        holder.dialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    QBUser user = QbUsersHolder.getInstance().getUserById(dialog.getRecipientId());
                    Intent intent = new Intent(context, Chat_profile.class);
                    QB_User_Id = String.valueOf(dialog.getRecipientId());
                    intent.putExtra("User_Name",user.getFullName());
                    intent.putExtra("QB_User_Id",QB_User_Id);
                    intent.putExtra("User_Login",user.getLogin());
                    intent.putExtra("User_Image_url",user.getWebsite());
                    QB_User_Id = user.getId().toString();
                    context.startActivity(intent);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
        });
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChatHelper.getInstance().isLogged()) {
                    ChatActivity.startForResult((Activity) context, 165, dialog);
                } else {
                    ChatHelper.getInstance().loginToChat(ChatHelper.getCurrentUser(), new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                            ChatActivity.startForResult((Activity) context, 165, dialog);
                        }

                        @Override
                        public void onError(QBResponseException e) {
                        }
                    });
                }
            }
        });
        int unreadMessagesCount = getUnreadMsgCount(dialog);
        if (unreadMessagesCount == 0) {
            holder.unreadCounterTextView.setVisibility(View.GONE);
        } else {
            holder.unreadCounterTextView.setVisibility(View.VISIBLE);
            holder.unreadCounterTextView.setText(String.valueOf(unreadMessagesCount > 99 ? "99+" : unreadMessagesCount));
        }
        View finalConvertView = null;
        holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                popup_click_adapter.onLong_Click(finalConvertView,position, dialog,v);
                return true;
            }
        });


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
    private int getUnreadMsgCount(QBChatDialog chatDialog) {
        Integer unreadMessageCount = chatDialog.getUnreadMessageCount();
        if (unreadMessageCount == null) {
            unreadMessageCount = 0;
        }
        return unreadMessageCount;
    }

}
