package com.TuDime.ui.fragments;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.services.QBPushManager;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.TuDime.R;
import com.TuDime.async.BaseAsyncTask;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.managers.DialogsManager;
import com.TuDime.services.CallService;
import com.TuDime.ui.Callback.Contact_chat_Refresh;
import com.TuDime.ui.Callback.Popup_click_adapter;
import com.TuDime.ui.Callback.Search_Fragments;
import com.TuDime.ui.activity.Archive_Chat;
import com.TuDime.ui.activity.CallActivity;
import com.TuDime.ui.activity.ChatActivity;
import com.TuDime.ui.activity.SelectUsersActivity;
import com.TuDime.ui.adapter.DialogsAdapter;
import com.TuDime.ui.dialog.ProgressDialogFragment;
import com.TuDime.utils.Constant;
import com.TuDime.constants.Consts;
import com.TuDime.utils.ErrorUtils;
import com.TuDime.constants.FcmConsts;
import com.TuDime.utils.KeyboardUtils;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;
import com.TuDime.utils.chat.ChatHelper;
import com.TuDime.utils.qb.QbChatDialogMessageListenerImp;
import com.TuDime.utils.qb.QbDialogHolder;
import com.TuDime.utils.qb.QbDialogUtils;
import com.TuDime.utils.qb.callback.QBPushSubscribeListenerImpl;
import com.TuDime.utils.qb.callback.QbEntityCallbackImpl;

import static android.app.Activity.RESULT_OK;
import static com.TuDime.util.AppSignatureHashHelper.TAG;

public class Chat_Fragment extends BaseFragment implements Contact_chat_Refresh, Search_Fragments, DialogsManager.ManagingDialogsCallbacks, Popup_click_adapter {

    private static final int REQUEST_SELECT_PEOPLE = 174;
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;
    private static final int PLAY_SERVICES_REQUEST_CODE = 9000;
    public static ArrayList<ArrayList<QBChatMessage>> Full_Chat;
    private static Chat_Fragment fragmentSec;
    ShimmerFrameLayout Shimmer_Effect;
    QbUsersDbManager dbManager;
    List<QBChatDialog> Chat_Dialog;
    private ListView dialogsListView;
    private LinearLayout emptyHintLayout;
    private SwipeRefreshLayout setOnRefreshListener;
    private DialogsAdapter dialogsAdapter;
    private int skipRecords = 0;
    private QBRequestGetBuilder requestBuilder;
    private boolean isProcessingResultInProgress;
    private BroadcastReceiver pushBroadcastReceiver;
    private QBChatDialogMessageListener allDialogsMessagesListener;
    private SystemMessagesListener systemMessagesListener;
    private QBSystemMessagesManager systemMessagesManager;
    private QBIncomingMessagesManager incomingMessagesManager;
    private DialogsManager dialogsManager;
    private QBUser currentUser, currentUser_qb;
    private TextView Select_Contact_view, Select_For_view;
    private List<QBChatDialog> filteredDataList;
    private int Offline_Chat_Count = 0;
    private int globalVar = 0;

    public static Chat_Fragment newInstance() {

        if (fragmentSec != null) {
            return fragmentSec;
        } else {
            fragmentSec = new Chat_Fragment();
            return fragmentSec;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_fragment_layout, container, false);
        dbManager = QbUsersDbManager.getInstance(getContext());
        Select_For_view = view.findViewById(R.id.Select_For_view);
        Select_Contact_view = view.findViewById(R.id.Select_Contact_view);
        Shimmer_Effect = view.findViewById(R.id.Shimmer_Effect);
        Full_Chat = new ArrayList<>();
        systemMessagesListener = new SystemMessagesListener();
        dialogsManager = new DialogsManager();
        currentUser = ChatHelper.getCurrentUser();
        currentUser_qb = SharedPrefsHelper.getInstance().getQbUser();
        initUi(view);
        if (Constant.isOnline(getActivity())) {
            if (!ChatHelper.getInstance().isLogged()) {
                restartApp(getActivity());
            }
            try {
                //Load Offline Data.1st
                updateDialogsAdapter();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (QbDialogHolder.getInstance().getDialogs().size() > 0) {
                loadDialogsFromQb(true, true);
            } else {
                loadDialogsFromQb(true, true);
            }
        } else {
            updateDialogsAdapter();
        }




        dialogsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    setOnRefreshListener.setEnabled(true);
                } else setOnRefreshListener.setEnabled(false);
            }
        });

        return view;

    }

    @Override
    public void onResumeFinished() {
        if (Constant.isOnline(getActivity())) {
            try {
                if (ChatHelper.getInstance().isLogged()) {
                    checkPlayServicesAvailable();
                    registerQbChatListeners();
                    loadDialogsFromQb(true, true);
                } else {
                    showDialog("Loading");
                    ChatHelper.getInstance().loginToChat(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                            checkPlayServicesAvailable();
                            registerQbChatListeners();
                            loadDialogsFromQb(true, true);
                            cancleDialog();
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            cancleDialog();
                            //  getActivity().finish();
                        }
                    });
                }
            } catch (Exception e) {

            }
        } else {
            loadDialogsFromQb(true, true);
        }


    }

    private void checkPlayServicesAvailable() {
        try {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_REQUEST_CODE).show();
                } else {

                    //    getActivity().finish();
                }
            }
        } catch (Exception e) {
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (Constant.isOnline(getActivity())) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pushBroadcastReceiver);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Constant.isOnline(getActivity())) {
            unregisterQbChatListeners();
        }

    }

    private void registerQbChatListeners() {
        incomingMessagesManager = QBChatService.getInstance().getIncomingMessagesManager();
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();

        if (incomingMessagesManager != null) {
            incomingMessagesManager.addDialogMessageListener(allDialogsMessagesListener != null
                    ? allDialogsMessagesListener : (allDialogsMessagesListener = new AllDialogsMessageListener()));
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
                    ? systemMessagesListener : (systemMessagesListener = new SystemMessagesListener()));
        }

        dialogsManager.addManagingDialogsCallbackListener(this);

        pushBroadcastReceiver = new PushBroadcastReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(pushBroadcastReceiver,
                new IntentFilter(FcmConsts.ACTION_NEW_FCM_EVENT));
    }

    private void unregisterQbChatListeners() {
        if (incomingMessagesManager != null) {
            incomingMessagesManager.removeDialogMessageListrener(allDialogsMessagesListener);
        }

        if (systemMessagesManager != null) {
            systemMessagesManager.removeSystemMessageListener(systemMessagesListener);
        }

        dialogsManager.removeManagingDialogsCallbackListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult with ResultCode: " + resultCode + " RequestCode: " + requestCode);
        if (resultCode == RESULT_OK) {
            if (Constant.isOnline(getActivity())) {
                if (requestCode == REQUEST_SELECT_PEOPLE) {
                    ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data
                            .getSerializableExtra(SelectUsersActivity.EXTRA_QB_USERS);
                    String chatName = data.getStringExtra(SelectUsersActivity.EXTRA_CHAT_NAME);

                    if (isPrivateDialogExist(selectedUsers)) {
                        selectedUsers.remove(ChatHelper.getCurrentUser());
                        QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                        isProcessingResultInProgress = false;
                        ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
                    } else {
                        ProgressDialogFragment.show(getActivity().getSupportFragmentManager(), R.string.create_chat);
                        createDialog(selectedUsers, chatName);
                    }
                } else if (requestCode == REQUEST_DIALOG_ID_FOR_UPDATE) {
                    if (data != null) {
                        String dialogId = data.getStringExtra(ChatActivity.EXTRA_DIALOG_ID);
                        loadUpdatedDialog(dialogId);
                    } else {
                        isProcessingResultInProgress = false;
                        updateDialogsList();
                    }
                }
            }


        } else {
            if (Constant.isOnline(getActivity())) {
                updateDialogsAdapter();
            }

        }
    }

    private boolean isPrivateDialogExist(ArrayList<QBUser> allSelectedUsers) {
        ArrayList<QBUser> selectedUsers = new ArrayList<>();
        selectedUsers.addAll(allSelectedUsers);
        selectedUsers.remove(ChatHelper.getCurrentUser());
        return selectedUsers.size() == 1 && QbDialogHolder.getInstance().hasPrivateDialogWithUser(selectedUsers.get(0));
    }

    private void loadUpdatedDialog(String dialogId) {

        try {
            ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
                @Override
                public void onSuccess(QBChatDialog result, Bundle bundle) {
                    QbDialogHolder.getInstance().addDialog(result);
                    updateDialogsAdapter();
                    isProcessingResultInProgress = false;

                }

                @Override
                public void onError(QBResponseException e) {
                    isProcessingResultInProgress = false;
                }
            });
        } catch (Exception E) {
            updateDialogsAdapter();
        }

    }


    private void logout() {
        if (QBPushManager.getInstance().isSubscribedToPushes()) {
            QBPushManager.getInstance().addListener(new QBPushSubscribeListenerImpl() {
                @Override
                public void onSubscriptionDeleted(boolean success) {
                    Log.d(TAG, "Subscription Deleted");
                    logoutREST();
                    QBPushManager.getInstance().removeListener(this);
                }
            });
            try {
                SubscribeService.unSubscribeFromPushes(getContext());
            } catch (Exception E) {
            }

        } else {
            logoutREST();
        }
    }

    private void logoutREST() {
        Log.d(TAG, "SignOut");
        QBUsers.signOut().performAsync(null);
    }

    private void updateDialogsList() {
        requestBuilder.setSkip(skipRecords = 0);
        loadDialogsFromQb(true, true);
    }


    private void initUi(View view) {
        emptyHintLayout = view.findViewById(R.id.layout_chat_empty_fragments);
        dialogsListView = view.findViewById(R.id.list_dialogs_chats_fragments);
        setOnRefreshListener = view.findViewById(R.id.swipy_refresh_layout_fragments);
        Chat_Dialog = new ArrayList<>(QbDialogHolder.getInstance().getDialogs().values());
        dialogsAdapter = new DialogsAdapter(getActivity(), Chat_Dialog, Chat_Fragment.this);
        dialogsListView.setEmptyView(emptyHintLayout);
        dialogsListView.setAdapter(dialogsAdapter);
        requestBuilder = new QBRequestGetBuilder();

        setOnRefreshListener.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    loadDialogsFromQb(true, true);
                    setOnRefreshListener.setRefreshing(false);
                } catch (Exception e) {
                    setOnRefreshListener.setRefreshing(false);
                }

            }
        });


    }


    private void deleteSelectedDialogs() {
        final Collection<QBChatDialog> selectedDialogs = dialogsAdapter.getSelectedItems();
        List<QBChatDialog> dialogsToDelete = new ArrayList<>();
        for (QBChatDialog dialog : selectedDialogs) {
            if (dialog.getType().equals(QBDialogType.PUBLIC_GROUP)) {
                cancleDialog();
                ToastUtils.shortToast(getString(R.string.dialogs_cannot_delete_chat) + " " + dialog.getName());

            } else if (dialog.getType().equals(QBDialogType.GROUP)) {
                cancleDialog();
                dialogsToDelete.add(dialog);
                dialogsManager.sendMessageLeftUser(dialog);
                dialogsManager.sendSystemMessageLeftUser(systemMessagesManager, dialog);
                dialogsAdapter.notifyDataSetChanged();
            } else if (dialog.getType().equals(QBDialogType.PRIVATE)) {
                cancleDialog();
                dialogsToDelete.add(dialog);
                dialogsAdapter.notifyDataSetChanged();
            }
        }

        if (Constant.isOnline(getActivity())) {
            ChatHelper.getInstance().deleteDialogs(dialogsToDelete, new QBEntityCallback<ArrayList<String>>() {
                @Override
                public void onSuccess(ArrayList<String> dialogsIds, Bundle bundle) {

                    Log.d(TAG, "Dialogs Deleting Successful");
                    QbDialogHolder.getInstance().deleteDialogs(dialogsIds);
                    updateDialogsAdapter();

                }

                @Override
                public void onError(QBResponseException e) {
                    cancleDialog();
                    Log.d(TAG, "Deleting Dialogs Error: " + e.getMessage());
                    showErrorSnackbar(R.string.dialogs_deletion_error, e,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deleteSelectedDialogs();
                                }
                            });
                }
            });
        }

    }

    public void showErrorSnackbar(@StringRes int resId, Exception e, View.OnClickListener clickListener) {
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null) {
            ErrorUtils.showSnackbar(rootView, resId, e,
                    R.string.dialog_retry, clickListener).show();
        }
    }

    private void createDialog(final ArrayList<QBUser> selectedUsers, String chatName) {
        Log.d(TAG, "Creating Dialog");
        ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers, chatName,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        Log.d(TAG, "Creating Dialog Successful");
                        isProcessingResultInProgress = false;
                        dialogsManager.sendSystemMessageAboutCreatingDialog(systemMessagesManager, dialog);
                        ArrayList<QBChatDialog> dialogs = new ArrayList<>();
                        dialogs.add(dialog);
                        try {
                            try {
                                new DialogJoinerAsyncTask(Chat_Fragment.this, dialogs, false).execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog, true);
                        ProgressDialogFragment.hide(getActivity().getSupportFragmentManager());
                    }

                    @Override
                    public void onError(QBResponseException error) {
                        Log.d(TAG, "Creating Dialog Error: " + error.getMessage());
                        isProcessingResultInProgress = false;
                        ProgressDialogFragment.hide(getActivity().getSupportFragmentManager());
                        /* showErrorSnackbar(R.string.dialogs_creation_error, error, null);*/
                    }
                }
        );
    }

    private void loadDialogsFromQb(final boolean silentUpdate, final boolean clearDialogHolder) {
        if (Constant.isOnline(getActivity())) {
            isProcessingResultInProgress = true;
            if (!silentUpdate) {
                show_dialog();
            }

            ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
                @Override
                public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle bundle) {
                    try {
                        DialogJoinerAsyncTask dialogJoinerAsyncTask = new DialogJoinerAsyncTask(Chat_Fragment.this, dialogs, clearDialogHolder);
                        dialogJoinerAsyncTask.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(QBResponseException e) {
                    disableProgress();
                }
            });
        }


    }

   

    public void disableProgress() {
        isProcessingResultInProgress = false;
        hide_dialog();
        setOnRefreshListener.setRefreshing(false);
    }

    public void updateDialogsAdapter() {
        if (Constant.isOnline(getActivity())) {
            Collection<QBChatDialog> Backup_List = QbDialogHolder.getInstance().getDialogs().values();
            dbManager.insertChatJSon(Backup_List,String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()),
                  String.valueOf(System.currentTimeMillis()));


            ArrayList<QBChatDialog> listDialogs = new ArrayList<>(Backup_List);
            ArrayList<String> temp_dialog = new ArrayList<>();
            try {
                Cursor cursor = dbManager.get_QBChat(SharedPrefsHelper.getInstance().getQbUser().getLogin());
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        temp_dialog.add(cursor.getString(1));
                    }
                }
                for (int i = 0; i < temp_dialog.size(); i++) {
                    for (int j = 0; j < listDialogs.size(); j++) {
                        if (listDialogs.get(j).getDialogId().equalsIgnoreCase(temp_dialog.get(i).toString())) {
                            listDialogs.remove(j);
                        }
                    }
                }
                dialogsAdapter.updateList(listDialogs);
                Chat_Dialog = listDialogs;
                cancleDialog();
            } catch (Exception e) {
                Chat_Dialog = listDialogs;
                cancleDialog();
            }
            method1();
        } else {
            Collection<QBChatDialog> Backup_List = dbManager.getChatJSon(String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
          //  Collection<QBChatDialog> Backup_List = SharedPrefsHelper.getInstance().getQBChatDialog_DB();
            ArrayList<QBChatDialog> listDialogs = new ArrayList<>(Backup_List);
            ArrayList<String> temp_dialog = new ArrayList<>();
            try {
                Cursor cursor = dbManager.get_QBChat(SharedPrefsHelper.getInstance().getQbUser().getLogin());
                if (cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        temp_dialog.add(cursor.getString(1));
                    }
                }
                for (int i = 0; i < temp_dialog.size(); i++) {
                    for (int j = 0; j < listDialogs.size(); j++) {
                        if (listDialogs.get(j).getDialogId().equalsIgnoreCase(temp_dialog.get(i).toString())) {
                            listDialogs.remove(j);
                        }
                    }
                }
                dialogsAdapter.updateList(listDialogs);
                Chat_Dialog = listDialogs;
                cancleDialog();
            } catch (Exception e) {
                Chat_Dialog = listDialogs;
                cancleDialog();
            }

            if (dialogsAdapter != null) {
                dialogsAdapter.notifyDataSetChanged();
            } else {
                dialogsAdapter = new DialogsAdapter(getActivity(), Chat_Dialog, Chat_Fragment.this);
                dialogsListView.setAdapter(dialogsAdapter);
            }
        }
    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {
        updateDialogsAdapter();
    }

    @Override
    public void onLong_Click(View view, int Position, QBChatDialog selectedDialog, View fview) {
        final PopupMenu popup = new PopupMenu(getContext(), fview);
        popup.getMenuInflater().inflate(R.menu.chat_activity_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();

                if (i == R.id.item1) {
                    showDialog("Loading");
                    dialogsAdapter.selectItem(selectedDialog);
                    deleteSelectedDialogs();
                    dialogsAdapter.notifyDataSetChanged();
                } else if (i == R.id.item2) {

                    Gson gson = new Gson();
                    String jsonString = gson.toJson(selectedDialog);
                    boolean res = dbManager.insertDB_QBChat_TABLE(selectedDialog.getDialogId(), SharedPrefsHelper.getInstance().getQbUser().getLogin(), jsonString);
                    if (res) {
                        dialogsAdapter.remove_item(Position);
                        dialogsAdapter.notifyDataSetChanged();
                    }
                  
                } else {
                    return onMenuItemClick(item);
                }
                return true;
            }
        });

        popup.show();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Constant.isOnline(getActivity())) {
            boolean isIncomingCall = SharedPrefsHelper.getInstance().get(Consts.EXTRA_IS_INCOMING_CALL, false);
            if (isCallServiceRunning(CallService.class)) {
                CallActivity.start(getActivity(), isIncomingCall);
            }
            try {
                Collection<QBChatDialog> Backup_List = QbDialogHolder.getInstance().getDialogs().values();
                dbManager.insertChatJSon(Backup_List,String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()),
                        String.valueOf(System.currentTimeMillis()));
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
      
        clearAppNotifications();
        try {
            if (!SharedPrefsHelper.getInstance().get_E_CARD_URL().equalsIgnoreCase("")) {
                Select_Contact_view.setText(getString(R.string.Send_Ecard));
                Select_Contact_view.setVisibility(View.VISIBLE);
            } else {
                Select_Contact_view.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Select_Contact_view.setVisibility(View.GONE);
        }

        try {
            if (!SharedPrefsHelper.getInstance().get_FORWARD().equalsIgnoreCase("")) {
                Select_For_view.setText(getString(R.string.Forward_chat));
                Select_For_view.setVisibility(View.VISIBLE);
            } else {
                Select_For_view.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Select_For_view.setVisibility(View.GONE);
        }

    }

    private boolean isCallServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void clearAppNotifications() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    void show_dialog() {
        Shimmer_Effect.startShimmerAnimation();
        Shimmer_Effect.setVisibility(View.VISIBLE);
    }

    void hide_dialog() {
        Shimmer_Effect.stopShimmerAnimation();
        Shimmer_Effect.setVisibility(View.GONE);

    }

    @Override
    public void filter(String S) {
        try {
            dialogsAdapter.setFilter(filter(Chat_Dialog, S));
        } catch (Exception e) {

        }

    }

    private List<QBChatDialog> filter(List<QBChatDialog> dataList, String newText) {
        newText = newText.toLowerCase();
        String text = null;
        filteredDataList = new ArrayList<QBChatDialog>();
        for (QBChatDialog dataFromDataList : dataList) {
            text = QbDialogUtils.getDialogName(dataFromDataList).toLowerCase();

            if (text.toLowerCase().contains(newText)) {
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    @Override
    public void Reload() {
        loadDialogsFromQb(false, true);
    }

    private void method1() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(100);
        QBRestChatService.getDialogMessages(Chat_Dialog.get(globalVar), messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                Full_Chat.add(qbChatMessages);
                if (globalVar < Chat_Dialog.size() - 1) {
                    globalVar++;
                    method1();
                } else {
                    dbManager.insertMessageJSon(Full_Chat,String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()),
                            String.valueOf(System.currentTimeMillis()));
                   // SharedPrefsHelper.getInstance().setQBChatMessage_Offline(Full_Chat);
                }
            }

            @Override
            public void onError(QBResponseException e) {
            }
        });

    }

    public static class DialogJoinerAsyncTask extends BaseAsyncTask<Void, Void, Void> {
        private WeakReference<Chat_Fragment> activityRef;
        private ArrayList<QBChatDialog> dialogs;
        private boolean clearDialogHolder;

        public DialogJoinerAsyncTask(Chat_Fragment dialogsActivity, ArrayList<QBChatDialog> dialogs, boolean clearDialogHolder) {
            activityRef = new WeakReference<>(dialogsActivity);
            this.dialogs = dialogs;
            this.clearDialogHolder = clearDialogHolder;
        }

        @Override
        public Void performInBackground(Void... voids) throws Exception {
            try {
                ChatHelper.getInstance().join(dialogs);
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        public void onResult(Void aVoid) {
            try {
                if (activityRef.get() != null) {
                    activityRef.get().disableProgress();
                    if (clearDialogHolder) {
                        QbDialogHolder.getInstance().clear();
                    }
                    QbDialogHolder.getInstance().addDialogs(dialogs);
                    activityRef.get().updateDialogsAdapter();
                }
            } catch (Exception e) {

            }

        }

        @Override
        public void onException(Exception e) {
            super.onException(e);
            Log.d(TAG, "Error: " + e);
            // ToastUtils.shortToast("Error: " + e.getMessage());
        }
    }

    private class PushBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(FcmConsts.EXTRA_FCM_MESSAGE);
            Log.v(TAG, "Received broadcast " + intent.getAction() + " with data: " + message);
            requestBuilder.setSkip(skipRecords = 0);
            loadDialogsFromQb(true, true);
        }
    }

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            dialogsManager.onSystemMessageReceived(qbChatMessage);
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {

        }
    }

    private class AllDialogsMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(final String dialogId, final QBChatMessage qbChatMessage, Integer senderId) {
            Log.d(TAG, "Processing received Message: " + qbChatMessage.getBody());
            if (!senderId.equals(ChatHelper.getCurrentUser().getId())) {
                dialogsManager.onGlobalMessageReceived(dialogId, qbChatMessage);
            }
        }
    }
   
}