package com.TuDime.ui.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.CollectionsUtil;
import com.TuDime.R;
import com.TuDime.managers.DialogsManager;
import com.TuDime.ui.Callback.Language_Translator;
import com.TuDime.ui.Callback.QouteChatTrigger;
import com.TuDime.ui.activity.ChatActivity;
import com.TuDime.ui.activity.DashBoard;
import com.TuDime.ui.activity.Video_Player;
import com.TuDime.ui.activity.Webview;
import com.TuDime.ui.adapter.listeners.AttachClickListener;
import com.TuDime.ui.adapter.listeners.MessageLinkClickListener;
import com.TuDime.utils.Constant;
import com.TuDime.utils.LinkUtils;
import com.TuDime.utils.MessageTextClickMovement;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.TimeUtils;
import com.TuDime.utils.ToastUtils;
import com.TuDime.utils.UiUtils;
import com.TuDime.utils.chat.ChatHelper;
import com.TuDime.utils.qb.PaginationHistoryListener;
import com.TuDime.utils.qb.QbUsersHolder;
import com.quickblox.users.model.QBUser;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.TuDime.constants.AppConstants.PASS_URL;
import static com.TuDime.constants.AppConstants.VIDEO_URL;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private static final String TAG = ChatAdapter.class.getSimpleName();
    private final QBChatDialog chatDialog;
    private PaginationHistoryListener paginationListener;
    private int previousGetCount = 0;
    private static final int CUSTOM_VIEW_TYPE = -1;
    private static final int TYPE_TEXT_RIGHT = 1;
    private static final int TYPE_TEXT_LEFT = 2;
    private static final int TYPE_ATTACH_RIGHT = 3;
    private static final int TYPE_ATTACH_LEFT = 4;
    private static final int TYPE_ATTACH_RIGHT_VIDEO = 7;
    private static final int TYPE_ATTACH_LEFT_VIDEO = 6;
    private static final int TYPE_NOTIFICATION_CENTER = 5;
    String TrAnSlAtEdTeXt="";
    QouteChatTrigger qouteChatTrigger;
    //Message TextView click listener
    //
    private MessageLinkClickListener messageTextViewLinkClickListener;
    private boolean overrideOnClick;
    Language_Translator language_translator;
    private AttachClickListener attachImageClickListener;

    private SparseIntArray containerLayoutRes = new SparseIntArray() {
        {
            put(TYPE_TEXT_RIGHT, R.layout.list_item_text_right);
            put(TYPE_TEXT_LEFT, R.layout.list_item_text_left);
            put(TYPE_ATTACH_RIGHT, R.layout.list_item_attach_right);
            put(TYPE_ATTACH_LEFT, R.layout.list_item_attach_left);
            put(TYPE_NOTIFICATION_CENTER, R.layout.list_item_notif_center);
            put(TYPE_ATTACH_RIGHT_VIDEO, R.layout.list_item_attach_video_right);
            put(TYPE_ATTACH_LEFT_VIDEO, R.layout.list_item_attach_video_left);
        }
    };

    private MessageViewHolder viewHolder;
    private List<QBChatMessage> chatMessages;
    private LayoutInflater inflater;
    protected Context context;
    private String Language_String;

    public ChatAdapter(Context context, QBChatDialog chatDialog, List<QBChatMessage> chatMessages) {
        this.chatDialog = chatDialog;
        this.context = context;
        this.chatMessages = chatMessages;
        this.inflater = LayoutInflater.from(context);
        language_translator = (ChatActivity) context;
        qouteChatTrigger = (ChatActivity)context;
    }

    public void updateStatusDelivered(String messageID, Integer userId) {
        for (int position = 0; position < chatMessages.size(); position++) {
            QBChatMessage message = chatMessages.get(position);
            if (message.getId().equals(messageID)) {
                ArrayList<Integer> deliveredIds = new ArrayList<>();
                if (message.getDeliveredIds() != null) {
                    deliveredIds.addAll(message.getDeliveredIds());
                }
                deliveredIds.add(userId);
                message.setDeliveredIds(deliveredIds);
                notifyItemChanged(position);
            }
        }
    }

    public void updateStatusRead(String messageID, Integer userId) {
        for (int position = 0; position < chatMessages.size(); position++) {
            QBChatMessage message = chatMessages.get(position);
            if (message.getId().equals(messageID)) {
                ArrayList<Integer> readIds = new ArrayList<>();
                if (message.getReadIds() != null) {
                    readIds.addAll(message.getReadIds());
                }
                readIds.add(userId);
                message.setReadIds(readIds);
                notifyItemChanged(position);
            }
        }
    }

    public void setMessageTextViewLinkClickListener(MessageLinkClickListener textViewLinkClickListener, boolean overrideOnClick) {
        this.messageTextViewLinkClickListener = textViewLinkClickListener;
        this.overrideOnClick = overrideOnClick;
    }

    public void setAttachImageClickListener(AttachClickListener clickListener) {
        attachImageClickListener = clickListener;
    }

    public void removeAttachImageClickListener() {

        attachImageClickListener = null;
    }

    public void addToList(List<QBChatMessage> items) {
        if (Constant.isOnline(context))
        {
            chatMessages.addAll(0, items);
            notifyItemRangeInserted(0, items.size());
        }

    }

    public void addList(List<QBChatMessage> items) {
        if (Constant.isOnline(context))
        {
            chatMessages.clear();
            chatMessages.addAll(items);
            notifyDataSetChanged();
        }

    }

    public void add(QBChatMessage item) {
        this.chatMessages.add(item);
        this.notifyItemInserted(chatMessages.size() - 1);
    }

    public List<QBChatMessage> getList() {
        return chatMessages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ATTACH_RIGHT_VIDEO:
                viewHolder = new  VideoMessageHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false),R.id.msg_attach_status_message,R.id.video_play,R.id.msg_image_attach);
                return viewHolder;
            case TYPE_ATTACH_LEFT_VIDEO:
                viewHolder = new  VideoMessageHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false),R.id.msg_attach_status_message,R.id.video_play,R.id.msg_image_attach);
                return viewHolder;
            case TYPE_NOTIFICATION_CENTER:
                viewHolder = new NotificationHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_text_message,
                        R.id.msg_text_time_message);
                return viewHolder;
            case TYPE_TEXT_RIGHT:
                viewHolder = new TextMessageHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_text_message,
                        R.id.msg_text_time_message, R.id.msg_link_preview, R.id.msg_text_status_message);
                return viewHolder;
            case TYPE_TEXT_LEFT:
                viewHolder = new TextMessageHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_text_message,
                        R.id.msg_text_time_message, R.id.msg_link_preview);
                return viewHolder;
            case TYPE_ATTACH_RIGHT:
                viewHolder = new ImageAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_image_attach, R.id.msg_progressbar_attach,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach, R.id.msg_attach_status_message);
                return viewHolder;
            case TYPE_ATTACH_LEFT:
                viewHolder = new ImageAttachHolder(inflater.inflate(containerLayoutRes.get(viewType), parent, false), R.id.msg_image_attach, R.id.msg_progressbar_attach,
                        R.id.msg_text_time_attach, R.id.msg_signs_attach);
                return viewHolder;
            default:
                return onCreateCustomViewHolder(parent, viewType);
        }
    }

    @Override
    public void onViewRecycled(MessageViewHolder holder) {
        if (holder.getItemViewType() == TYPE_TEXT_LEFT || holder.getItemViewType() == TYPE_TEXT_RIGHT) {
            TextMessageHolder textMessageHolder = (TextMessageHolder) holder;

            if (textMessageHolder.linkPreviewLayout.getTag() != null) {
                textMessageHolder.linkPreviewLayout.setTag(null);
            }
        }
        if (containerLayoutRes.get(holder.getItemViewType()) != 0 && holder.avatar != null) {
            Glide.clear(holder.avatar);
        }
        super.onViewRecycled(holder);
    }

    private MessageViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        downloadMore(position);
        QBChatMessage chatMessage = getItem(position);
        if (isIncoming(chatMessage) && !isReadByCurrentUser(chatMessage)) {
            readMessage(chatMessage);
        }
        int valueType = getItemViewType(position);
        Log.d(TAG, "onBindViewHolder on position " + position);
        switch (valueType) {
            case TYPE_NOTIFICATION_CENTER:
                onBindViewNotificationHolder((NotificationHolder) holder, chatMessage, position);
                break;
            case TYPE_TEXT_RIGHT:
                onBindViewMsgRightHolder((TextMessageHolder) holder, chatMessage, position);
                break;
            case TYPE_TEXT_LEFT:
                onBindViewMsgLeftHolder((TextMessageHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_RIGHT:
                onBindViewAttachRightHolder((ImageAttachHolder) holder, chatMessage, position);
                break;
            case TYPE_ATTACH_LEFT:
                onBindViewAttachLeftHolder((ImageAttachHolder) holder, chatMessage, position);
                break;
            default:
                onBindViewCustomHolder(holder, chatMessage, position);
                break;
        }
    }

    private void onBindViewNotificationHolder(NotificationHolder holder, QBChatMessage chatMessage, int position) {
        holder.messageTextView.setText(Constant.base64Controller(chatMessage.getBody(),false));
        TrAnSlAtEdTeXt="";
        holder.messageTimeTextView.setText(getTime(chatMessage.getDateSent()));
    }

    private void onBindViewMsgRightHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position) {
        fillTextMessageHolder(holder, chatMessage, position, false);

        if (holder.getItemViewType() == TYPE_TEXT_RIGHT)

            holder.bubbleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String extension=getMimeType(Constant.base64Controller(chatMessage.getBody(),false));

                    if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store"))
                    {
                        if (extension.contains("html")||extension.contains("xml")||
                                extension.contains("json")||
                                extension.contains("txt")||extension.contains("pdf"))
                        { Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(Constant.base64Controller(chatMessage.getBody(),false)));
                            context.startActivity(intent); }

                        else  if (extension.contains("docx"))
                        {
                            Intent i = new Intent(context, Webview.class);
                            i.putExtra(PASS_URL,"http://docs.google.com/gview?embedded=true&url="+Constant.base64Controller(chatMessage.getBody(),false));
                            context.startActivity(i);
                        }

                        else  if (extension.contains("mp4")||extension.contains("m4a")||extension.contains("mov")||extension.contains("3gp")
                                ||extension.contains("m4v")||extension.contains("webm")||extension.contains("m3u8")||extension.contains("ts")||
                                extension.contains("f4v")||extension.contains("flv")||extension.contains("3gpp"))
                        {  Intent i = new Intent(context, Video_Player.class);
                            i.putExtra(VIDEO_URL,Constant.base64Controller(chatMessage.getBody(),false));
                            context.startActivity(i);}
                        else if (extension.contains("apk"))
                        {
                            Update(Constant.base64Controller(chatMessage.getBody(),false).trim().toString());
                        }else
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(Constant.base64Controller(chatMessage.getBody(),false)));
                            context.startActivity(intent);
                        }
                    }
                }
            });

            holder.bubbleFrame.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Set<String> messagesIds = new HashSet<String>();
                    try {
                        messagesIds.add(chatMessage.getId());
                        messagesIds.add(chatMessage.getDeliveredIds().toString());
                    } catch (Exception e) {

                    }
                    final PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.chat_sms_work, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int i = item.getItemId();

                            if (i == R.id.item1) {
                                Constant.setClipboard(context, Constant.base64Controller(chatMessage.getBody(),false));
                                TrAnSlAtEdTeXt = "";
                                ToastUtils.longToast(R.string.clipboard);
                            } else if (i == R.id.item2) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setTitle("WARNING");
                                builder1.setMessage("Are you REALLY sure you want to delete this chat? It will be unrecoverable if you continue");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                QBRestChatService.deleteMessages(messagesIds, true).performAsync(new QBEntityCallback<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid, Bundle bundle) {
                                                        chatMessages.remove(position);
                                                        notifyDataSetChanged();
                                                        // refresh_chat.Refresh();

                                                    }

                                                    @Override
                                                    public void onError(QBResponseException e) {

                                                    }
                                                });

                                                dialog.cancel();

                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            } else if (i == R.id.item3) {

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setTitle("WARNING");
                                builder1.setMessage("Are you REALLY sure you want to delete this chat? It will be unrecoverable if you continue");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                QBRestChatService.deleteMessages(messagesIds, false).performAsync(new QBEntityCallback<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid, Bundle bundle) {
                                                        chatMessages.remove(position);
                                                        notifyDataSetChanged();
                                                        updateStatusDelivered(chatMessage.getId(),chatDialog.getUserId());

                                                    }

                                                    @Override
                                                    public void onError(QBResponseException e) {

                                                    }
                                                });

                                                dialog.cancel();

                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();


                            } else if (i == R.id.item4) {

                                Show_Edit_Message(position,context, chatDialog.getDialogId(), chatMessage.getId(), Constant.base64Controller(chatMessage.getBody(),false));
                            } else if (i == R.id.item_for)
                            {
                                try {
                                    SharedPrefsHelper.getInstance().set_FORWARD(Constant.base64Controller(chatMessage.getBody(),false));
                                    Intent intent = new Intent(context, DashBoard.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else if (i == R.id.item_quotes){
                                String sms = Constant.base64Controller(chatMessage.getBody(),false);
                                qouteChatTrigger.openQuoteChat(sms);
                                QBUser user = QbUsersHolder.getInstance().getUserById(chatMessage.getSenderId());
                                String userName = user.getFullName();
                                String quotes = "";
                                String New_quotes = userName+": "+"''"+sms+"''";
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("name",getSenderName(chatMessage));
                                    jsonObject.put("sms",Constant.base64Controller(chatMessage.getBody(),false));
                                    qouteChatTrigger.openQuoteChat(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                SharedPrefsHelper.getInstance().setQuotes(New_quotes);
                            }
                            else {
                                return onMenuItemClick(item);
                            }
                            return true;
                        }
                    });

                    popup.show();



                    return false;
                }
            });

    }

    private void Show_Edit_Message(int position,Context context, String dialogId, String messagesIds, String body)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_send_message, null);
        dialogBuilder.setView(dialogView);
        TextView close_dialog = dialogView.findViewById(R.id.close_dialog);
        EditText msg_box = dialogView.findViewById(R.id.msg_box);
        ProgressBar progress_chat  = dialogView.findViewById(R.id.progress_chat);
        progress_chat.setVisibility(View.GONE);
        msg_box.setText(body);
        Button resend_msg = dialogView.findViewById(R.id.resend_msg);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        resend_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_chat.setVisibility(View.VISIBLE);
                if (msg_box.getText().toString().equalsIgnoreCase(""))
                {progress_chat.setVisibility(View.GONE);
                ToastUtils.shortToast(R.string.Enter_Your_Message);
                return;
                }else
                {
                    String ChatBODY = Constant.base64Controller(msg_box.getText().toString().trim(),true);
                    QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                    messageUpdateBuilder.updateText(ChatBODY+"\n"+"---Update Message---") //updates message's text
                            .markDelivered()     //mark message as delivered on server
                            .markRead();         //mark message as read on server

                    QBRestChatService.updateMessage(messagesIds, dialogId, messageUpdateBuilder).performAsync(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle)
                        {
                            chatMessages.get(position).setBody(ChatBODY+"\n"+"---Update Message---");
                            notifyDataSetChanged();
                            progress_chat.setVisibility(View.GONE);
                            ToastUtils.shortToast(R.string.Update_Message_Successful);
                            alertDialog.dismiss();
                            notifyItemChanged(position);

                        }

                        @Override
                        public void onError(QBResponseException e) {
                            alertDialog.dismiss();
                            progress_chat.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void onBindViewMsgLeftHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position) {
        holder.messageTimeTextView.setVisibility(View.GONE);
        setOpponentsName(holder, chatMessage, false);
        TextView customMessageTimeTextView = holder.itemView.findViewById(R.id.custom_msg_text_time_message);
        customMessageTimeTextView.setText(getTime(chatMessage.getDateSent()));
        fillTextMessageHolder(holder, chatMessage, position, true);

        if (holder.getItemViewType() == TYPE_TEXT_LEFT)


            holder.bubbleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String extension=getMimeType(Constant.base64Controller(chatMessage.getBody(),false));

                if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store"))
                {
                    if (extension.contains("html")||extension.contains("xml")||
                            extension.contains("json")||
                            extension.contains("txt")||extension.contains("pdf"))
                    { Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Constant.base64Controller(chatMessage.getBody(),false)));
                        context.startActivity(intent); }

                  else  if (extension.contains("docx"))
                    {
                        Intent i = new Intent(context, Webview.class);
                        i.putExtra(PASS_URL,"http://docs.google.com/gview?embedded=true&url="+Constant.base64Controller(chatMessage.getBody(),false));
                        context.startActivity(i);
                    }

                    else  if (extension.contains("mp4")||extension.contains("m4a")||extension.contains("mov")||extension.contains("3gp")
                            ||extension.contains("m4v")||extension.contains("webm")||extension.contains("m3u8")||extension.contains("ts")||
                            extension.contains("f4v")||extension.contains("flv")||extension.contains("3gpp"))
                    {  Intent i = new Intent(context, Video_Player.class);
                        i.putExtra(VIDEO_URL,Constant.base64Controller(chatMessage.getBody(),false));
                        context.startActivity(i);}
                    else if (extension.contains("apk"))
                    {
                        Update(Constant.base64Controller(chatMessage.getBody(),false).trim().toString());
                    }else
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(Constant.base64Controller(chatMessage.getBody(),false)));
                        context.startActivity(intent);
                    }
                }
                }
            });

            holder.bubbleFrame.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Set<String> messagesIds = new HashSet<String>();
                    try {
                        messagesIds.add(chatMessage.getId());
                        messagesIds.add(chatMessage.getDeliveredIds().toString());
                    } catch (Exception e) {

                    }
                    final PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.opponent_chat_work, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int i = item.getItemId();

                            if (i == R.id.item1) {
                                Constant.setClipboard(context,Constant.base64Controller(chatMessage.getBody(),false));
                                TrAnSlAtEdTeXt = "";
                                ToastUtils.longToast(R.string.clipboard);
                            }  else if (i == R.id.TRANSLATE)
                            {
                                language_translator.Language_Translator(Constant.base64Controller(chatMessage.getBody(),false),
                                        SharedPrefsHelper.getInstance().get_Language(), String.valueOf(position));
                            }else if (i == R.id.item_for)
                            {
                                try {
                                    SharedPrefsHelper.getInstance().set_FORWARD(Constant.base64Controller(chatMessage.getBody(),false));
                                    Intent intent = new Intent(context, DashBoard.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else if (i == R.id.item3) {

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setTitle("WARNING");
                                builder1.setMessage("Are you REALLY sure you want to delete this chat? It will be unrecoverable if you continue");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                QBRestChatService.deleteMessages(messagesIds, false).performAsync(new QBEntityCallback<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid, Bundle bundle) {
                                                        chatMessages.remove(position);
                                                        notifyDataSetChanged();
                                                        updateStatusDelivered(chatMessage.getId(),chatDialog.getUserId());

                                                    }
                                                    @Override
                                                    public void onError(QBResponseException e) {

                                                    }
                                                });
                                                dialog.cancel();

                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();



                            }else if (i == R.id.item_quotes){
                                String sms = Constant.base64Controller(chatMessage.getBody(),false);
                                qouteChatTrigger.openQuoteChat(sms);
                                QBUser user = QbUsersHolder.getInstance().getUserById(chatMessage.getSenderId());
                                String userName = user.getFullName();
                                String quotes = "";
                                String New_quotes = userName+": "+"''"+sms+"''";
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("name",getSenderName(chatMessage));
                                    jsonObject.put("sms",Constant.base64Controller(chatMessage.getBody(),false));
                                    qouteChatTrigger.openQuoteChat(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                SharedPrefsHelper.getInstance().setQuotes(New_quotes);
                            } else {
                                return onMenuItemClick(item);
                            }
                            return true;
                        }
                    });

                    popup.show();



                    return false;
                }
            });







    }

    private void onBindViewAttachRightHolder(ImageAttachHolder holder, QBChatMessage chatMessage, int position) {
        fillAttachmentHolder(holder, chatMessage, position, false);
    }

    private void onBindViewAttachLeftHolder(ImageAttachHolder holder, QBChatMessage chatMessage, int position) {
        setOpponentsName(holder, chatMessage, true);
        fillAttachmentHolder(holder, chatMessage, position, true);
        setItemAttachClickListener(getAttachListenerByType(position), holder, getAttachment(position), position);
    }

    private void onBindViewCustomHolder(MessageViewHolder holder, QBChatMessage chatMessage, int position) {
    }

    private void fillAttachmentHolder(ImageAttachHolder holder, QBChatMessage chatMessage, int position, boolean isLeftMessage) {
        setDateSentAttach(holder, chatMessage);
        displayAttachment(holder, position);

      /*  if (isLeftMessage==false)
        {*/
            holder.bubbleFrame.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Set<String> messagesIds = new HashSet<String>();
                    try{
                        messagesIds.add(chatMessage.getId());
                        messagesIds.add(chatMessage.getDeliveredIds().toString());
                    }catch (Exception e)
                    {

                    }
                    final PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.chat_sms_work_image, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int i = item.getItemId();
                         if (i == R.id.item2) {

                             try {
                                 AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                 builder1.setTitle("WARNING");
                                 builder1.setMessage("Are you REALLY sure you want to delete this chat? It will be unrecoverable if you continue");
                                 builder1.setCancelable(true);

                                 builder1.setPositiveButton(
                                         "Yes",
                                         new DialogInterface.OnClickListener() {
                                             public void onClick(DialogInterface dialog, int id) {
                                                 QBRestChatService.deleteMessages(messagesIds, true).performAsync(new QBEntityCallback<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid, Bundle bundle) {
                                                         chatMessages.remove(position);
                                                         notifyDataSetChanged();
                                                         updateStatusDelivered(chatMessage.getId(),chatDialog.getUserId());

                                                     }
                                                     @Override
                                                     public void onError(QBResponseException e) {

                                                     }
                                                 });
                                                 dialog.cancel();

                                             }
                                         });

                                 builder1.setNegativeButton(
                                         "No",
                                         new DialogInterface.OnClickListener() {
                                             public void onClick(DialogInterface dialog, int id) {
                                                 dialog.cancel();
                                             }
                                         });

                                 AlertDialog alert11 = builder1.create();
                                 alert11.show();

                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                         }else  if (i == R.id.item3) {
                             try {
                                 AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                 builder1.setTitle("WARNING");
                                 builder1.setMessage("Are you REALLY sure you want to delete this chat? It will be unrecoverable if you continue");
                                 builder1.setCancelable(true);

                                 builder1.setPositiveButton(
                                         "Yes",
                                         new DialogInterface.OnClickListener() {
                                             public void onClick(DialogInterface dialog, int id) {
                                                 QBRestChatService.deleteMessages(messagesIds, false).performAsync(new QBEntityCallback<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid, Bundle bundle) {
                                                         chatMessages.remove(position);
                                                         notifyDataSetChanged();

                                                     }
                                                     @Override
                                                     public void onError(QBResponseException e) {

                                                     }
                                                 });
                                                 dialog.cancel();

                                             }
                                         });

                                 builder1.setNegativeButton(
                                         "No",
                                         new DialogInterface.OnClickListener() {
                                             public void onClick(DialogInterface dialog, int id) {
                                                 dialog.cancel();
                                             }
                                         });

                                 AlertDialog alert11 = builder1.create();
                                 alert11.show();

                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                            }
                            else {
                                return onMenuItemClick(item);
                            }
                            return true;
                        }
                    });
                    popup.show();
                    return false;
                }
            });
        /*}*/


        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }

        setItemAttachClickListener(getAttachListenerByType(position), holder, getAttachment(position), position);

        if (!isLeftMessage) {
            boolean read = isRead(chatMessage);
            boolean delivered = isDelivered(chatMessage);
            if (read) {
                holder.attachStatusTextView.setBackground(context.getResources().getDrawable(R.drawable.green_tick));
            } else if (delivered) {
                holder.attachStatusTextView.setBackground(context.getResources().getDrawable(R.drawable.orange_tick));
            } else {
                holder.attachStatusTextView.setBackground(context.getResources().getDrawable(R.drawable.gray_tick));
            }
        }





    }

    private void fillTextMessageHolder(TextMessageHolder holder, QBChatMessage chatMessage, int position, boolean isLeftMessage) {
        holder.linkPreviewLayout.setVisibility(View.GONE);


        if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store"))
        {holder.messageTextView.setText(getFileNameFromURL(Constant.base64Controller(chatMessage.getBody(),false))); }else
        { holder.messageTextView.setText(Constant.base64Controller(chatMessage.getBody(),false)); }


        String extension=getMimeType(Constant.base64Controller(chatMessage.getBody(),false));


        if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".pdf") && Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pdf_ic, 0, 0, 0);


        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".html")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.html_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".json")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.json_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".xml")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.xml_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".docx")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.text_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".txt")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.text_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".apk")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.apk_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".mp3")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.audio_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".wav")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.audio_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".mp4")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".3gp")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".mov")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".avi")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".flv")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_ic, 0, 0, 0);
        }else  if (Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains(".m3u8")&& Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_ic, 0, 0, 0);
        }else if ( Constant.base64Controller(chatMessage.getBody(),false).toLowerCase().contains("file_store") )
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unknown_ic, 0, 0, 0);
        }else
        {
            holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (extension.contains("mp4")||extension.contains("m4a")||extension.contains("mov")||extension.contains("3gp")
                ||extension.contains("m4v")||extension.contains("webm")||extension.contains("m3u8")||extension.contains("ts")||
                extension.contains("f4v")||extension.contains("flv")||extension.contains("3gpp"))
        {       holder.messageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_ic, 0, 0, 0);}


        holder.messageTimeTextView.setText(getTime(chatMessage.getDateSent()));

        setMessageTextViewLinkClickListener(holder, position);

        int valueType = getItemViewType(position);
        String avatarUrl = obtainAvatarUrl(valueType, chatMessage);
        if (avatarUrl != null) {
            displayAvatarImage(avatarUrl, holder.avatar);
        }

        List<String> urlsList = LinkUtils.extractUrls(Constant.base64Controller(chatMessage.getBody(),false));
        TrAnSlAtEdTeXt="";
        if (urlsList.isEmpty()) {
            holder.messageTextView.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);
        } else {
            holder.messageTextView.setMaxWidth((int) context.getResources().getDimension(R.dimen.link_preview_width));
            holder.linkPreviewLayout.setTag(chatMessage.getId());
        }

        if (!isLeftMessage) {
            boolean read = isRead(chatMessage);
            boolean delivered = isDelivered(chatMessage);
            if (read) {
              //  holder.messageStatusTextView.setText(R.string.statuses_read);
                holder.messageStatusTextView.setBackground(context.getResources().getDrawable(R.drawable.green_tick));

            } else if (delivered) {
               // holder.messageStatusTextView.setText(R.string.statuses_delivered);
                holder.messageStatusTextView.setBackground(context.getResources().getDrawable(R.drawable.orange_tick));
            } else {
               // holder.messageStatusTextView.setText(R.string.statuses_sent);
                holder.messageStatusTextView.setBackground(context.getResources().getDrawable(R.drawable.gray_tick));
            }
        }
    }

    private void setOpponentsName(MessageViewHolder holder, QBChatMessage chatMessage, boolean isAttachment) {
        int viewId = isAttachment ? R.id.opponent_name_attach_view : R.id.opponent_name_text_view;

        TextView opponentNameTextView = holder.itemView.findViewById(viewId);
        opponentNameTextView.setTextColor(UiUtils.getRandomTextColorById(chatMessage.getSenderId()));
        opponentNameTextView.setText(getSenderName(chatMessage));
    }

    private String getSenderName(QBChatMessage chatMessage) {
        QBUser sender = QbUsersHolder.getInstance().getUserById(chatMessage.getSenderId());
        String fullName = "";
        if (sender != null && !TextUtils.isEmpty(sender.getFullName())) {
            fullName = sender.getFullName();
        }
        return fullName;
    }

    private void readMessage(QBChatMessage chatMessage) {
        try {
            chatDialog.readMessage(chatMessage);
        } catch (XMPPException | SmackException.NotConnectedException e) {
            Log.w(TAG, e);
        }
    }

    private boolean isReadByCurrentUser(QBChatMessage chatMessage) {
            Integer currentUserId = ChatHelper.getCurrentUser().getId();
            return !CollectionsUtil.isEmpty(chatMessage.getReadIds()) && chatMessage.getReadIds().contains(currentUserId);
    }

    private boolean isRead(QBChatMessage chatMessage) {
        boolean read = false;
        if (Constant.isOnline(context))
        {
            Integer recipientId = chatMessage.getRecipientId();
            Integer currentUserId = ChatHelper.getCurrentUser().getId();
            Collection<Integer> readIds = chatMessage.getReadIds();
            if (readIds == null) {
                return false;
            }
            if (recipientId != null && !recipientId.equals(currentUserId) && readIds.contains(recipientId)) {
                read = true;
            } else if (readIds.size() == 1 && readIds.contains(currentUserId)) {
                read = false;
            } else if (readIds.size() > 0) {
                read = true;
            }
            return read;
        }else
        {
            return read;
        }

    }

    private boolean isDelivered(QBChatMessage chatMessage) {
        boolean delivered = false;
        if (Constant.isOnline(context))
        {
            Integer recipientId = chatMessage.getRecipientId();
            Integer currentUserId = ChatHelper.getCurrentUser().getId();
            Collection<Integer> deliveredIds = chatMessage.getDeliveredIds();
            if (deliveredIds == null) {
                return false;
            }
            if (recipientId != null && !recipientId.equals(currentUserId) && deliveredIds.contains(recipientId)) {
                delivered = true;
            } else if (deliveredIds.size() == 1 && deliveredIds.contains(currentUserId)) {
                delivered = false;
            } else if (deliveredIds.size() > 0) {
                delivered = true;
            }return delivered;
        }else
        {
            return true;
        }




    }

    public void setPaginationHistoryListener(PaginationHistoryListener paginationListener) {
        this.paginationListener = paginationListener;
    }

    private void downloadMore(int position) {
        if (position == 0) {
            if (getItemCount() != previousGetCount) {
                paginationListener.downloadMore();
                previousGetCount = getItemCount();
            }
        }
    }

    @Override
    public long getHeaderId(int position) {
        QBChatMessage chatMessage = getItem(position);
        return TimeUtils.getDateAsHeaderId(chatMessage.getDateSent() * 1000);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.view_chat_message_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;
        TextView dateTextView = view.findViewById(R.id.header_date_textview);

        QBChatMessage chatMessage = getItem(position);
        dateTextView.setText(getDate(chatMessage.getDateSent()));
        dateTextView.setTextColor(context.getResources().getColor(R.color.white));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dateTextView.getLayoutParams();
        lp.topMargin = 0;
        dateTextView.setLayoutParams(lp);
    }

    private void setMessageTextViewLinkClickListener(TextMessageHolder holder, int position) {
        if (messageTextViewLinkClickListener != null) {
            MessageTextClickMovement customClickMovement =
                    new MessageTextClickMovement(messageTextViewLinkClickListener, overrideOnClick, context);
            customClickMovement.setPositionInAdapter(position);

            holder.messageTextView.setMovementMethod(customClickMovement);
        }
    }

    private AttachClickListener getAttachListenerByType(int position) {
        QBAttachment attachment = getAttachment(position);
        if (QBAttachment.PHOTO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                QBAttachment.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
            return attachImageClickListener;
        }
        return null;
    }

    private void setDateSentAttach(ImageAttachHolder holder, QBChatMessage chatMessage) {
        holder.attachTimeTextView.setText(getTime(chatMessage.getDateSent()));
    }

    @Nullable
    private String obtainAvatarUrl(int valueType, QBChatMessage chatMessage) {
        return null;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    private QBChatMessage getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        QBChatMessage chatMessage = getItem(position);

       if (chatMessage.getProperty(DialogsManager.PROPERTY_NOTIFICATION_TYPE) != null) {
            return TYPE_NOTIFICATION_CENTER;
        }

        if (hasAttachments(chatMessage)) {
            QBAttachment attachment = getAttachment(position);

            if (QBAttachment.PHOTO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                    QBAttachment.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT : TYPE_ATTACH_RIGHT;
            }
            if (QBAttachment.VIDEO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                    QBAttachment.VIDEO_TYPE.equalsIgnoreCase(attachment.getType())) {
                return isIncoming(chatMessage) ? TYPE_ATTACH_LEFT_VIDEO : TYPE_ATTACH_RIGHT_VIDEO;
            }
        } else {
            return isIncoming(chatMessage) ? TYPE_TEXT_LEFT : TYPE_TEXT_RIGHT;
        }
        return customViewType(position);
    }

    private int customViewType(int position) {
        return CUSTOM_VIEW_TYPE;
    }

    private boolean isIncoming(QBChatMessage chatMessage) {

            QBUser currentUser = ChatHelper.getCurrentUser();
            return chatMessage.getSenderId() != null && !chatMessage.getSenderId().equals(currentUser.getId());
    }

    private boolean hasAttachments(QBChatMessage chatMessage) {
        Collection<QBAttachment> attachments = chatMessage.getAttachments();
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * @return string in "Hours:Minutes" format, i.e. <b>10:15</b>
     */
    private String getTime(long seconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
        return dateFormat.format(new Date(seconds * 1000));
    }

    /**
     * @return string in "Month Day" format, i.e. <b>APRIL 25</b>
     */
    public static String getDate(long milliseconds) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy ", Locale.getDefault());
        return dateFormat.format(new Date(milliseconds * 1000));
    }

    private void displayAttachment(MessageViewHolder holder, int position) {
        QBAttachment attachment = getAttachment(position);

        if (QBAttachment.PHOTO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                QBAttachment.IMAGE_TYPE.equalsIgnoreCase(attachment.getType())) {
            showPhotoAttach(holder, position);
        }

        if (QBAttachment.VIDEO_TYPE.equalsIgnoreCase(attachment.getType()) ||
                QBAttachment.VIDEO_TYPE.equalsIgnoreCase(attachment.getType())) {
            showPhotoAttach(holder, position);
        }
    }

    private void showPhotoAttach(MessageViewHolder holder, int position) {
        String imageUrl = getImageUrl(position);
        showImageByURL(holder, imageUrl, position);
    }
  /*  private void showVIDEOAttach(MessageViewHolder holder, int position) {
        String imageUrl = getImageUrl(position);
        showVIDEOurl(holder, imageUrl, position);
    }*/
    private String getImageUrl(int position) {
        QBAttachment attachment = getAttachment(position);
        return QBFile.getPrivateUrlForUID(attachment.getId());
    }

    private QBAttachment getAttachment(int position) {
        QBChatMessage chatMessage = getItem(position);
        return chatMessage.getAttachments().iterator().next();
    }

    private void showImageByURL(MessageViewHolder holder, String url, int position) {
        int preferredImageWidth = (int) context.getResources().getDimension(R.dimen.attach_image_width_preview);
        int preferredImageHeight = (int) context.getResources().getDimension(R.dimen.attach_image_height_preview);

        Glide.with(context)
                .load(url)
                .listener(this.<String, GlideDrawable>getRequestListener(holder, position))
                .override(preferredImageWidth, preferredImageHeight)
                .dontTransform()
                .error(R.drawable.ic_error)
                .into(((ImageAttachHolder) holder).attachImageView);
    }

    private RequestListener getRequestListener(MessageViewHolder holder, int position) {
        return new ImageLoadListener<>((ImageAttachHolder) holder);
    }

    private void displayAvatarImage(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder_user)
                .dontAnimate()
                .into(imageView);
    }

    private void setItemAttachClickListener(AttachClickListener listener, MessageViewHolder holder, QBAttachment qbAttachment, int position) {
        if (listener != null) {
            holder.bubbleFrame.setOnClickListener(new ItemClickListenerFilter(listener, qbAttachment, position));
        }
    }

    private static class NotificationHolder extends MessageViewHolder {
        private TextView messageTextView;
        private TextView messageTimeTextView;

        private NotificationHolder(View itemView, @IdRes int msgId, @IdRes int timeId) {
            super(itemView);
            messageTextView = itemView.findViewById(msgId);
            messageTimeTextView = itemView.findViewById(timeId);
        }
    }

    private static class TextMessageHolder extends MessageViewHolder {
        private View linkPreviewLayout;
        private TextView messageTextView;
        private TextView messageTimeTextView;
        private TextView messageStatusTextView;

        private TextMessageHolder(View itemView, @IdRes int msgId, @IdRes int timeId, @IdRes int linkPreviewLayoutId, @IdRes int statusId) {
            super(itemView);
            messageTextView = itemView.findViewById(msgId);
            messageTimeTextView = itemView.findViewById(timeId);
            linkPreviewLayout = itemView.findViewById(linkPreviewLayoutId);
            messageStatusTextView = itemView.findViewById(statusId);




        }

        private TextMessageHolder(View itemView, @IdRes int msgId, @IdRes int timeId, @IdRes int linkPreviewLayoutId) {
            super(itemView);
            messageTextView = itemView.findViewById(msgId);
            messageTimeTextView = itemView.findViewById(timeId);
            linkPreviewLayout = itemView.findViewById(linkPreviewLayoutId);
        }
    }

    private static class VideoMessageHolder extends MessageViewHolder {
        private ImageView video_play,msg_image_attach;
        private TextView msg_attach_status_message;
        private VideoMessageHolder(View itemView,@IdRes int timeId, @IdRes int video_play_t, @IdRes int msg_image_attach_) {
            super(itemView);

            msg_attach_status_message = itemView.findViewById(timeId);
            video_play = itemView.findViewById(video_play_t);
            msg_image_attach = itemView.findViewById(msg_image_attach_);

        }
       /* void showVIDEOurl(MessageViewHolder holder, String url, int position)
        {


        }*/
    }

    private static class ImageAttachHolder extends MessageViewHolder {
        private ImageView attachImageView;
        private ProgressBar attachProgressBar;
        private TextView attachTimeTextView;
        private TextView attachStatusTextView;

        private ImageAttachHolder(View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int timeId, @IdRes int signId) {
            super(itemView);
            attachImageView = itemView.findViewById(attachId);
            attachProgressBar = itemView.findViewById(progressBarId);
            attachTimeTextView = itemView.findViewById(timeId);
        }

        private ImageAttachHolder(View itemView, @IdRes int attachId, @IdRes int progressBarId, @IdRes int timeId, @IdRes int signId, @IdRes int statusId) {
            super(itemView);
            attachImageView = itemView.findViewById(attachId);
            attachProgressBar = itemView.findViewById(progressBarId);
            attachTimeTextView = itemView.findViewById(timeId);
            attachStatusTextView = itemView.findViewById(statusId);
        }
    }

    public abstract static class MessageViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public View bubbleFrame;

        private MessageViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.msg_image_avatar);
            bubbleFrame = itemView.findViewById(R.id.msg_bubble_background);
        }
    }

    protected static class ImageLoadListener<M, P> implements RequestListener<M, P> {
        private ImageAttachHolder holder;

        private ImageLoadListener(ImageAttachHolder holder) {
            this.holder = holder;
            holder.attachProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean onException(Exception e, M model, Target<P> target, boolean isFirstResource) {
            Log.e(TAG, "ImageLoadListener Exception= " + e);
            holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.attachProgressBar.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(P resource, M model, Target<P> target, boolean isFromMemoryCache, boolean isFirstResource) {
            holder.attachImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.attachProgressBar.setVisibility(View.GONE);
            return false;
        }
    }

    private class ItemClickListenerFilter implements View.OnClickListener {
        protected int position;
        private QBAttachment attachment;
        private AttachClickListener chatAttachClickListener;

        ItemClickListenerFilter(AttachClickListener attachClickListener, QBAttachment attachment, int position) {
            this.position = position;
            this.attachment = attachment;
            this.chatAttachClickListener = attachClickListener;
        }

        @Override
        public void onClick(View view) {
            chatAttachClickListener.onLinkClicked(attachment, position);
        }
    }

    public String getMimeType(String URL) {
        String extension="";
        if(URL.contains(".")) {
             extension = URL.substring(URL.lastIndexOf("."));
        }
        return extension.toLowerCase();
    }
    public void Update(final String apkurl) {
        try {
            new AsyncTask<Void, String, String>() {
                String result="";
                @Override
                protected String doInBackground(Void... params) {
                    try {
                        URL url = new URL(apkurl);
                        HttpURLConnection c = (HttpURLConnection) url
                                .openConnection();
                        c.setRequestMethod("GET");

                        c.connect();

                        String PATH = Environment.getExternalStorageDirectory()
                                + "/download/";
                        File file = new File(PATH);
                        file.mkdirs();
                        File outputFile = new File(file, "app.apk");
                        FileOutputStream fos = new FileOutputStream(outputFile);

                        InputStream is = c.getInputStream();

                        byte[] buffer = new byte[1024];
                        int len1 = 0;
                        while ((len1 = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len1);
                        }
                        fos.close();
                        is.close();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk")), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    } catch (IOException e) {
                        result="Update error! "+ e.getMessage();
                        e.printStackTrace();

                    }
                    return result;
                }

                protected void onPostExecute(String result) {

                    Toast.makeText(context, result,
                            Toast.LENGTH_LONG).show();
                };

            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String getFileNameFromURL(String url) {
        if (url == null) {
            return "";
        }
        try {
            URL resource = new URL(url);
            String host = resource.getHost();
            if (host.length() > 0 && url.endsWith(host)) {
                // handle ...example.com
                return "";
            }
        }
        catch(MalformedURLException e) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        // find end index for ?
        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        // find end index for #
        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        // calculate the end index
        int endIndex = Math.min(lastQMPos, lastHashPos);
        return url.substring(startIndex, endIndex);
    }






}