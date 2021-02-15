package com.TuDime.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.StringRes;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.TuDime.R;
import com.TuDime.ui.Callback.Create_Group_Chat;
import com.TuDime.ui.activity.Add_Participants;
import com.TuDime.ui.activity.ChatActivity;
import com.TuDime.ui.fragments.Contact_chat_Fragment;
import com.TuDime.ui.fragments.Sercet_Chat_Contact;
import com.TuDime.utils.chat.ChatHelper;

import static com.TuDime.ui.activity.Group_Details_View.USER_ID_ADD_GROUP;
import static com.TuDime.ui.activity.Group_Details_View.qbUser_add;
import static com.TuDime.utils.ResourceUtils.getString;

public class CheckboxUsersAdapter extends UsersAdapter {

    Context context;
    private List<Integer> initiallySelectedUsers;
    private Set<QBUser> selectedUsers;
    private ProgressDialog progressDialog = null;
    private Create_Group_Chat create_group_chat=null;
    private Sercet_Chat_Contact sercet_chat_contact=null;
    private boolean IS_Serect_Chat=false;
    String Add_User_re="";
    Add_Participants activity;
    public CheckboxUsersAdapter(Context context, List<QBUser> users) {
        super(context, users);
        selectedUsers = new HashSet<>();
        this.context = context;
        this.selectedUsers.add(currentUser);
        this.initiallySelectedUsers = new ArrayList<>();

    }
    public CheckboxUsersAdapter(Context context, List<QBUser> users, Contact_chat_Fragment fragment) {
        super(context, users);
        selectedUsers = new HashSet<>();
        this.context = context;
        this.selectedUsers.add(currentUser);
        this.create_group_chat =fragment;
        this.initiallySelectedUsers = new ArrayList<>();

    }
    public CheckboxUsersAdapter(Context context, List<QBUser> users, Sercet_Chat_Contact fragment,boolean b) {
        super(context, users);
        selectedUsers = new HashSet<>();
        this.context = context;
        this.selectedUsers.add(currentUser);
        this.sercet_chat_contact =fragment;
        this.initiallySelectedUsers = new ArrayList<>();
        this.IS_Serect_Chat=b;
    }
    public CheckboxUsersAdapter(Context context, List<QBUser> users, String Add_User) {
        super(context, users);
        selectedUsers = new HashSet<>();
        this.context = context;
        this.selectedUsers.add(currentUser);
        this.Add_User_re=Add_User;
        this.activity=(Add_Participants)context;
        this.initiallySelectedUsers = new ArrayList<>();
    }


    public void addSelectedUsers(List<Integer> userIds) {
        for (QBUser user : userList) {
            for (Integer id : userIds) {
                if (user.getId().equals(id)) {
                    selectedUsers.add(user);
                    initiallySelectedUsers.add(user.getId());
                    break;
                }
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

         QBUser user = getItem(position);
         ViewHolder holder = (ViewHolder) view.getTag();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Add_User_re!=null)
                {
                    if (!Add_User_re.equalsIgnoreCase(""))
                    {
                        String id = user.getId().toString();
                        /*Intent resultIntent = new Intent();
                        Log.e("myResult",id);
                        resultIntent.putExtra("myResult",id);
                        activity.setResult(RESULT_OK, resultIntent);*/
                        USER_ID_ADD_GROUP=id;
                        qbUser_add=user;
                        Log.e("myResult",id);
                        activity.finish();
                    }else
                    {
                        if (sercet_chat_contact!=null)
                        {

                        }else
                        {
                            if (selectedUsers.size()>1)
                            { create_group_chat.Pass_List_do(selectedUsers);
                                view.setBackgroundColor(Color.parseColor("#75007AFF"));
                                selectedUsers.add(user);
                            }else
                            {
                                showProgressDialog(R.string.dlg_loading);
                                selectedUsers.clear();
                                selectedUsers.add(user);
                                ArrayList<QBUser> selectedUsersd = new ArrayList<>(selectedUsers);
                                String chatName = selectedUsersd.get(0).getFullName();
                                String finalChatName = chatName;
                                ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsersd, chatName,
                                        new QBEntityCallback<QBChatDialog>() {
                                            @Override
                                            public void onSuccess(QBChatDialog dialog, Bundle args) {
                                                hideProgressDialog();
                                                ArrayList<QBChatDialog> dialogs = new ArrayList<>();
                                                dialogs.add(dialog);
                                                dialogs.get(0).setName(finalChatName);
                                                /*  ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog, true);*/
                                                if (IS_Serect_Chat)
                                                {
                                                    Intent intent = new Intent(context, ChatActivity.class);
                                                    intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialog);
                                                    intent.putExtra(ChatActivity.IS_SERECT_CHAT, "true");
                                                    intent.putExtra(ChatActivity.EXTRA_IS_NEW_DIALOG, true);
                                                    context.startActivity(intent);
                                                }else
                                                {
                                                    Intent intent = new Intent(context, ChatActivity.class);
                                                    intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialog);
                                                    intent.putExtra(ChatActivity.IS_SERECT_CHAT, "false");
                                                    intent.putExtra(ChatActivity.EXTRA_IS_NEW_DIALOG, true);
                                                    context.startActivity(intent);
                                                }

                                            }

                                            @Override
                                            public void onError(QBResponseException error) {
                                                hideProgressDialog();
                                            }
                                        }
                                );

                            }
                        }
                    }
                    }
                }





        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (create_group_chat!=null)
                {
                    create_group_chat.Pass_List_do(selectedUsers);
                    view.setBackgroundColor(Color.parseColor("#75007AFF"));
                    selectedUsers.add(user);
                }




                return true;
            }
        });






        holder.userCheckBox.setVisibility(View.GONE);
        holder.userCheckBox.setChecked(selectedUsers.contains(user));

        return view;
    }

    public Set<QBUser> getSelectedUsers() {
        return selectedUsers;
    }

    @Override
    protected boolean isAvailableForSelection(QBUser user) {
        return super.isAvailableForSelection(user) && !initiallySelectedUsers.contains(user.getId());
    }


    protected void showProgressDialog(@StringRes Integer messageId) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            // Disable the back button
            DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            };
            progressDialog.setOnKeyListener(keyListener);
        }
        progressDialog.setMessage(getString(messageId));
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
