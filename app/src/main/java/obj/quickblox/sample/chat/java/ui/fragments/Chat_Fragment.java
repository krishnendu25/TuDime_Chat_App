package obj.quickblox.sample.chat.java.ui.fragments;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.services.QBPushManager;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import obj.quickblox.sample.chat.java.R;

import obj.quickblox.sample.chat.java.async.BaseAsyncTask;
import obj.quickblox.sample.chat.java.db.QbUsersDbManager;
import obj.quickblox.sample.chat.java.managers.DialogsManager;
import obj.quickblox.sample.chat.java.services.CallService;
import obj.quickblox.sample.chat.java.ui.Callback.Contact_chat_Refresh;
import obj.quickblox.sample.chat.java.ui.Callback.Popup_click_adapter;
import obj.quickblox.sample.chat.java.ui.Callback.Search_Fragments;
import obj.quickblox.sample.chat.java.ui.Model.Call_model;
import obj.quickblox.sample.chat.java.ui.Model.Contact_Model;
import obj.quickblox.sample.chat.java.ui.activity.Archive_Chat;
import obj.quickblox.sample.chat.java.ui.activity.CallActivity;
import obj.quickblox.sample.chat.java.ui.activity.ChatActivity;
import obj.quickblox.sample.chat.java.ui.activity.Choose_Sign_Up_type;
import obj.quickblox.sample.chat.java.ui.activity.SelectUsersActivity;
import obj.quickblox.sample.chat.java.ui.adapter.DialogsAdapter;
import obj.quickblox.sample.chat.java.ui.dialog.ProgressDialogFragment;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.Consts;
import obj.quickblox.sample.chat.java.utils.ErrorUtils;
import obj.quickblox.sample.chat.java.utils.FcmConsts;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;
import obj.quickblox.sample.chat.java.utils.qb.QbChatDialogMessageListenerImp;
import obj.quickblox.sample.chat.java.utils.qb.QbDialogHolder;
import obj.quickblox.sample.chat.java.utils.qb.QbDialogUtils;
import obj.quickblox.sample.chat.java.utils.qb.callback.QBPushSubscribeListenerImpl;
import obj.quickblox.sample.chat.java.utils.qb.callback.QbEntityCallbackImpl;

import static android.app.Activity.RESULT_OK;
import static obj.quickblox.sample.chat.java.util.AppSignatureHashHelper.TAG;

public class Chat_Fragment extends BaseFragment implements Contact_chat_Refresh,Search_Fragments, DialogsManager.ManagingDialogsCallbacks, Popup_click_adapter {

    private static final int REQUEST_SELECT_PEOPLE = 174;
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;
    private static final int PLAY_SERVICES_REQUEST_CODE = 9000;
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
    private TextView Select_Contact_view, Archive_Chat_Go;
    private List<QBChatDialog> filteredDataList;
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
        Select_Contact_view = view.findViewById(R.id.Select_Contact_view);
        Archive_Chat_Go = view.findViewById(R.id.Archive_Chat_Go);
        Shimmer_Effect = view.findViewById(R.id.Shimmer_Effect);

        systemMessagesListener = new SystemMessagesListener();
        dialogsManager = new DialogsManager();
        currentUser = ChatHelper.getCurrentUser();
        currentUser_qb = SharedPrefsHelper.getInstance().getQbUser();
        initUi(view);
        if (Constant.isOnline(getActivity()))
        {  if (!ChatHelper.getInstance().isLogged()) {
            restartApp(getActivity());
        }}else
        {updateDialogsAdapter();}
        if (QbDialogHolder.getInstance().getDialogs().size() > 0) {
            loadDialogsFromQb(true, true);
        } else {
            loadDialogsFromQb(false, true);
        }

        Archive_Chat_Go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Archive_Chat.class));
            }
        });


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
        if (Constant.isOnline(getActivity()) )
        {
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
            }catch (Exception e)
            {

            }
        }else
        {
            loadDialogsFromQb(true, true);
        }


    }

    private void checkPlayServicesAvailable() {
        try{
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_REQUEST_CODE).show();
                } else {

                //    getActivity().finish();
                }
            }
        }catch (Exception e){}

    }

    @Override
    public void onPause() {
        super.onPause();
        if (Constant.isOnline(getActivity()) )
        {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(pushBroadcastReceiver);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Constant.isOnline(getActivity()) )
        {
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
            if (Constant.isOnline(getActivity()) )
            {
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
            if (Constant.isOnline(getActivity()) )
            {
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

        try{
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
        }catch (Exception E)
        {  updateDialogsAdapter(); }

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
            }catch (Exception E) {   }

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
                try{
                    loadDialogsFromQb(true, true);
                    setOnRefreshListener.setRefreshing(false);
                }catch (Exception e)
                {
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
                ToastUtils.shortToast(getString(R.string.dialogs_cannot_delete_chat)+" "+ dialog.getName());

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

        if (Constant.isOnline(getActivity()) )
        {
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
                            new DialogJoinerAsyncTask(Chat_Fragment.this, dialogs, false).execute();

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
        Archive_Reload();
        if (Constant.isOnline(getActivity()) )
        {
            isProcessingResultInProgress = true;
            if (!silentUpdate) {
                show_dialog();
            }

            ChatHelper.getInstance().getDialogs(requestBuilder, new QBEntityCallback<ArrayList<QBChatDialog>>() {
                @Override
                public void onSuccess(ArrayList<QBChatDialog> dialogs, Bundle bundle) {
                    DialogJoinerAsyncTask dialogJoinerAsyncTask = new DialogJoinerAsyncTask(Chat_Fragment.this, dialogs, clearDialogHolder);
                    dialogJoinerAsyncTask.execute();
                }

                @Override
                public void onError(QBResponseException e) {
                    disableProgress();
                    //ToastUtils.shortToast(e.getMessage());
                }
            });
        }



    }

    private void Archive_Reload() {
        Cursor cursor = dbManager.get_QBChat_TABLE();
        if (cursor.getCount() != 0) {
            Archive_Chat_Go.setText(getResources().getString(R.string.Archived) +"(" + cursor.getCount() + ")");
        } else {
            Archive_Chat_Go.setText(getResources().getString(R.string.Archived) +"(" + "0" + ")");
        }
    }

    public void disableProgress() {
        isProcessingResultInProgress = false;
        hide_dialog();
        setOnRefreshListener.setRefreshing(false);
    }

    public void updateDialogsAdapter() {
        if (Constant.isOnline(getActivity()))
        {
            Collection<QBChatDialog> Backup_List = QbDialogHolder.getInstance().getDialogs().values();
            SharedPrefsHelper.getInstance().setQBChatDialog_DB(Backup_List);
            ArrayList<QBChatDialog> listDialogs = new ArrayList<>(Backup_List);
            ArrayList<String> temp_dialog = new ArrayList<>();
            try{
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
            }catch (Exception e)
            {
                Chat_Dialog = listDialogs;
                cancleDialog();
            }

        }else
        {
            Collection<QBChatDialog> Backup_List =SharedPrefsHelper.getInstance().getQBChatDialog_DB();
            ArrayList<QBChatDialog> listDialogs = new ArrayList<>(Backup_List);
            ArrayList<String> temp_dialog = new ArrayList<>();
            try{
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
            }catch (Exception e)
            {
                Chat_Dialog = listDialogs;
                cancleDialog();
            }

                if (dialogsAdapter!=null)
                {
                    dialogsAdapter.notifyDataSetChanged();
                }else
                {
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
                    Archive_Reload();
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
        if (Constant.isOnline(getActivity()) )
        {
            boolean isIncomingCall = SharedPrefsHelper.getInstance().get(Consts.EXTRA_IS_INCOMING_CALL, false);
            if (isCallServiceRunning(CallService.class)) {
                Log.d(TAG, "CallService is running now");
                CallActivity.start(getActivity(), isIncomingCall);
            }
        }
        Archive_Reload();
        clearAppNotifications();
        if (SharedPrefsHelper.getInstance().get_E_CARD_URL() != null) {
            if (SharedPrefsHelper.getInstance().get_E_CARD_URL().equalsIgnoreCase("")) {
                Select_Contact_view.setVisibility(View.GONE);

            } else {
                Select_Contact_view.setVisibility(View.VISIBLE);
                Select_Contact_view.setText(getResources().getString(R.string.Send_Ecard));
            }
        } else {
            Select_Contact_view.setVisibility(View.GONE);
        }

        if (SharedPrefsHelper.getInstance().get_FORWARD() != null) {
            if (SharedPrefsHelper.getInstance().get_FORWARD().equalsIgnoreCase("")) {
                Select_Contact_view.setVisibility(View.GONE);
            } else {
                Select_Contact_view.setVisibility(View.VISIBLE);
                Select_Contact_view.setText(getResources().getString(R.string.Forward_chat));
            }
        } else {
            Select_Contact_view.setVisibility(View.GONE);
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
        try{
            dialogsAdapter.setFilter(filter(Chat_Dialog, S));
        }catch (Exception e)
        {

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
            try{
                ChatHelper.getInstance().join(dialogs);
            }catch (Exception e){}

            return null;
        }

        @Override
        public void onResult(Void aVoid) {
            try{
                if (activityRef.get() != null) {
                    activityRef.get().disableProgress();
                    if (clearDialogHolder) {
                        QbDialogHolder.getInstance().clear();
                    }
                    QbDialogHolder.getInstance().addDialogs(dialogs);
                    activityRef.get().updateDialogsAdapter();
                }
            }catch (Exception e){

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