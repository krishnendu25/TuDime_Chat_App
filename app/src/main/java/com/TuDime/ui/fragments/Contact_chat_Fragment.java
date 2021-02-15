package com.TuDime.ui.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.TuDime.R;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.ui.Callback.Contact_chat_Refresh;
import com.TuDime.ui.Callback.Create_Group_Chat;
import com.TuDime.ui.Callback.Go_to_Chat;
import com.TuDime.ui.Callback.Search_Fragments;
import com.TuDime.ui.Model.Contact_Model;
import com.TuDime.ui.activity.ChatActivity;
import com.TuDime.ui.adapter.CheckboxUsersAdapter;
import com.TuDime.ui.adapter.Show_All_Contact_Adapter;
import com.TuDime.utils.ErrorUtils;
import com.TuDime.utils.PermissionsChecker;
import com.TuDime.utils.ToastUtils;
import com.TuDime.utils.chat.ChatHelper;
import com.TuDime.utils.qb.QbDialogHolder;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Contact_chat_Fragment extends BaseFragment implements Go_to_Chat,Create_Group_Chat, Contact_chat_Refresh,Search_Fragments {
    private static Contact_chat_Fragment fragmentThird;
    private ListView usersListView;
    private ProgressBar progressBar;
    private TextView txvEmptyView_frg,txvEmptyView_frg_1;
    private TextView Start_Chat_People;
    public static final String EXTRA_QB_USERS = "qb_users";
    public static final String EXTRA_CHAT_NAME = "chat_name";
    public static final int MINIMUM_CHAT_OCCUPANTS_SIZE = 2;
    private static final int REQUEST_DIALOG_ID_FOR_UPDATE = 165;
    public static final int PRIVATE_CHAT_OCCUPANTS_SIZE = 2;
    private static final int PER_PAGE_SIZE = 100;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc string updated_at";
    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    private static final String EXTRA_QB_DIALOG = "qb_dialog";
    private CheckboxUsersAdapter usersAdapter;
    private ArrayList<QBUser> users_QB;
    private long lastClickTime = 0l;
    private QBChatDialog qbChatDialog_Qb;
    private String chatName="";
    private boolean isProcessingResultInProgress;
    private QbUsersDbManager dbManager;
    private PermissionsChecker checker;
    ShimmerFrameLayout Shimmer_Effect;
    List<Contact_Model> Local_Contact;
    private ListView See_all_contact_without_Friend;
    private Show_All_Contact_Adapter all_contact_adapter;
    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    ArrayList<QBUser> users_s = new ArrayList<>();
    private ArrayList<QBUser> filteredDataList;
    private ArrayList<Contact_Model> temp_seach_local_contact;
    // newInstance constructor for creating fragment with arguments
    public static Contact_chat_Fragment newInstance() {
          if (fragmentThird!=null)
        {
            return fragmentThird;
        }else
        {
            fragmentThird = new Contact_chat_Fragment();
            return fragmentThird;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        try{

        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment_layout, container, false);
        Shimmer_Effect=view.findViewById(R.id.Shimmer_Effect);
        usersListView = (ListView)view.findViewById(R.id.list_select_users_fr);
        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        See_all_contact_without_Friend = (ListView)view.findViewById(R.id.See_all_contact_without_Friend);
        Start_Chat_People = (TextView)view.findViewById(R.id.Start_Chat_People);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_select_users_fr);
        txvEmptyView_frg = (TextView) view.findViewById(R.id.txvEmptyView_frg);
        txvEmptyView_frg_1= (TextView) view.findViewById(R.id.txvEmptyView_frg_1);
        See_all_contact_without_Friend.setEmptyView(txvEmptyView_frg_1);
        usersListView.setEmptyView(txvEmptyView_frg);
        QBChatDialog dialog = null;
        qbChatDialog_Qb = dialog;/*(QBChatDialog) getActivity().getIntent().getSerializableExtra(null);*/
        dbManager = QbUsersDbManager.getInstance(getActivity());
        checker = new PermissionsChecker(getActivity());

        loadUsersFromQb();



        Start_Chat_People.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                List<QBUser> users = new ArrayList<>(usersAdapter.getSelectedUsers());
                if (users.size() < MINIMUM_CHAT_OCCUPANTS_SIZE) {
                    ToastUtils.shortToast(R.string.select_users_choose_users);
                } else {
                    if (qbChatDialog_Qb == null && users.size() > PRIVATE_CHAT_OCCUPANTS_SIZE) {
                        showChatNameDialog();
                    } else {
                        passResultToCallerActivity();
                    }
                }


            }
        });








        return view;

    }

    private void showChatNameDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enter_chat_name, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextGroupName = dialogView.findViewById(R.id.edittext_dialog_name);

        dialogBuilder.setTitle(R.string.dialog_enter_chat_name);
        dialogBuilder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editTextGroupName.getText())) {
                    ToastUtils.shortToast(R.string.dialog_enter_chat_name);
                } else {
                    chatName = editTextGroupName.getText().toString();
                    passResultToCallerActivity();
                    usersAdapter.notifyDataSetChanged();
                    loadUsersFromQb();
                    Start_Chat_People.setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        });

        dialogBuilder.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.create().show();
    }

    private void passResultToCallerActivity() {
       /* Intent result = new Intent();
        ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());

        String chatName = data.getStringExtra(SelectUsersActivity.EXTRA_CHAT_NAME);


        result.putExtra(EXTRA_QB_USERS, selectedUsers);
*/



        /*if (!TextUtils.isEmpty(chatName)) {
            result.putExtra(EXTRA_CHAT_NAME, chatName);
        }
        getActivity().setResult(RESULT_OK, result);
        getActivity().finish();*/
        ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());

            if (isPrivateDialogExist(selectedUsers)) {
                selectedUsers.remove(ChatHelper.getCurrentUser());
                QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
                isProcessingResultInProgress = false;
                ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
            } else {

                showDialog("Creating chat…");

                if (chatName.equals(""))
                {
                    chatName= selectedUsers.get(0).getFullName();
                }

                ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers, chatName,
                        new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog dialog, Bundle args) {
                                cancleDialog();
                                ArrayList<QBChatDialog> dialogs = new ArrayList<>();
                                dialogs.add(dialog);
                                dialogs.get(0).setName(chatName);
                              /*  ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog, true);*/
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialog);
                                intent.putExtra(ChatActivity.EXTRA_IS_NEW_DIALOG, true);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(QBResponseException error) {
                                isProcessingResultInProgress = false;
                                cancleDialog();
                                showErrorSnackbar(R.string.dialogs_creation_error, error, null);
                            }
                        }
                );





            }



    }




    private boolean isPrivateDialogExist(ArrayList<QBUser> allSelectedUsers) {
        ArrayList<QBUser> selectedUsers = new ArrayList<>();
        selectedUsers.addAll(allSelectedUsers);
        selectedUsers.remove(ChatHelper.getCurrentUser());
        return selectedUsers.size() == 1 && QbDialogHolder.getInstance().hasPrivateDialogWithUser(selectedUsers.get(0));
    }


    private void loadUsersFromQb() {
        ArrayList<GenericQueryRule> rules = new ArrayList<>();
        rules.add(new GenericQueryRule(ORDER_RULE, ORDER_VALUE));

        QBPagedRequestBuilder qbPagedRequestBuilder = new QBPagedRequestBuilder();
        qbPagedRequestBuilder.setRules(rules);
        qbPagedRequestBuilder.setPerPage(PER_PAGE_SIZE);

        show_dialog();
        QBUsers.getUsers(qbPagedRequestBuilder, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                users_s.addAll(users);
                usersAdapter = new CheckboxUsersAdapter(getContext(), users, Contact_chat_Fragment.this);
                updateUsersAdapter();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_users_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadUsersFromQb();
                            }
                        });
                hide_dialog();
            }
        });
    }


    public ArrayList<QBUser> Search_people_to_fragment()
    {
        return users_s;
    }










  /*  private ArrayList<QBUser>  Set_Name_ToQb_User(ArrayList<QBUser> users)
    {
        ArrayList<QBUser> temp_list = new ArrayList<>();

        try{
            Local_Contact = getContactsForm_Phone(getContext());

        }catch (Exception e)
        {

        }

        for (int i=0 ; i<Local_Contact.size() ; i++)
        {
            boolean flag=false;
            int position=0;
            for(int j=0 ; j<users.size() ; j++)
            {

                if (Local_Contact.get(i).getContact_Number().length()>10)
                {
                    if (users.get(j).getLogin().equals(Local_Contact.get(i).getContact_Number().substring(Local_Contact.get(i).getContact_Number().length()-10)))
                    {
                        position=j;
                        flag=true;
                        break;
                    }
                }else
                {
                    if (users.get(j).getLogin().equals(Local_Contact.get(i).getContact_Number()))
                    {
                        position=j;
                        flag=true;
                        break;
                    }
                }


            }
            if (flag)
            {
                users.get(position).setFullName(Local_Contact.get(i).getContact_Name());
                temp_list.add(users.get(position));
                Local_Contact.remove(i);
            }


        }
        users_QB=temp_list;

        return users_QB;




        //MyString.Substring(MyString.Length-6)




    }*/

    private void getDialog() {
        String dialogID = qbChatDialog_Qb.getDialogId();
        ChatHelper.getInstance().getDialogById(dialogID, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                qbChatDialog_Qb = qbChatDialog;
                loadUsersFromDialog(qbChatDialog.getOccupants());
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_dialog_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadUsersFromQb();
                            }
                        });
                hide_dialog();
            }
        });
    }
    private void updateUsersAdapter() {
        if (qbChatDialog_Qb != null) {
            usersAdapter.addSelectedUsers(qbChatDialog_Qb.getOccupants());
        }
        usersListView.setAdapter(usersAdapter);
        Local_Contact = getContactsForm_Phone(getContext());
        all_contact_adapter = new Show_All_Contact_Adapter(getActivity(),Local_Contact);
        See_all_contact_without_Friend.setAdapter(all_contact_adapter);
        ListUtils.setDynamicHeight(usersListView);
        ListUtils.setDynamicHeight(See_all_contact_without_Friend);
        hide_dialog();
    }
     void showErrorSnackbar(@StringRes int resId, Exception e, View.OnClickListener clickListener) {
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null) {
            ErrorUtils.showSnackbar(rootView, resId, e,
                    R.string.dialog_retry, clickListener).show();
        }
    }
    private void loadUsersFromDialog(List<Integer> userIdsList) {
        QBUsers.getUsersByIDs(userIdsList, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                usersAdapter = new CheckboxUsersAdapter(getContext(), users_QB, Contact_chat_Fragment.this);
                for (QBUser user : qbUsers) {
                    usersAdapter.addUserToUserList(user);
                }
                updateUsersAdapter();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.select_users_get_users_dialog_error, e, null);
                hide_dialog();
            }
        });
    }



//Get_Phone_Number_Form_Ph
    public List<Contact_Model> getContactsForm_Phone(Context ctx) {
        List<Contact_Model> Temp_Local_Contact = new ArrayList<>();

        ContentResolver cr = ctx.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    Contact_Model info = new Contact_Model();
                    info.Contact_Name =cursor.getString(nameIndex);
                    info.Contact_Number = cursor.getString(numberIndex);
                    info.QB_user_id = "   ";
                    Temp_Local_Contact.add(info);
                }
            } finally {
                cursor.close();
            }
        }
        return Temp_Local_Contact;
    }

    @Override
    public void passtochat(ArrayList<QBUser> selectedUsers) {
      //  ArrayList<QBUser> selectedUsers = new ArrayList<>(usersAdapter.getSelectedUsers());

        if (isPrivateDialogExist(selectedUsers)) {
            selectedUsers.remove(ChatHelper.getCurrentUser());
            QBChatDialog existingPrivateDialog = QbDialogHolder.getInstance().getPrivateDialogWithUser(selectedUsers.get(0));
            isProcessingResultInProgress = false;
            ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, existingPrivateDialog);
        } else {

            showDialog("Creating chat…");

            if (chatName.equals(""))
            {
                chatName= selectedUsers.get(0).getFullName();
            }

            ChatHelper.getInstance().createDialogWithSelectedUsers(selectedUsers, chatName,
                    new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog dialog, Bundle args) {
                            cancleDialog();
                            ArrayList<QBChatDialog> dialogs = new ArrayList<>();
                            dialogs.add(dialog);
                            dialogs.get(0).setName(chatName);
                            /*  ChatActivity.startForResult(getActivity(), REQUEST_DIALOG_ID_FOR_UPDATE, dialog, true);*/
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialog);
                            intent.putExtra(ChatActivity.EXTRA_IS_NEW_DIALOG, true);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(QBResponseException error) {
                            isProcessingResultInProgress = false;
                            cancleDialog();
                            showErrorSnackbar(R.string.dialogs_creation_error, error, null);
                        }
                    }
            );





        }

    }

    @Override
    public void Pass_List_do(Set<QBUser> QBList)
    {
        if(QBList.size()>1)
        {
            Start_Chat_People.setVisibility(View.VISIBLE);
        }else
        {
            Start_Chat_People.setVisibility(View.GONE);
        }

    }

    @Override
    public void filter(String S)
    {
        try{
            usersAdapter.setFilter( filter(users_s, S));
            all_contact_adapter.setFilter( filter(Local_Contact, S,""));
        }catch (Exception e)
        {

        }
    }

    private List<Contact_Model> filter(List<Contact_Model> users_s, String newText, String s1)
    {
        newText=newText.toLowerCase();
        String text = null;
        temp_seach_local_contact=new ArrayList<Contact_Model>();
        for(Contact_Model dataFromDataList:users_s){

            if (newText.matches("^[0-9]*$")||newText.contains("@"))
            {
                text= dataFromDataList.Contact_Number;
            }else
            {
                text= dataFromDataList.Contact_Name.toLowerCase();
            }


            if(text.contains(newText)){
                temp_seach_local_contact.add(dataFromDataList);
            }
        }

        return temp_seach_local_contact;
    }

    private ArrayList<QBUser> filter(ArrayList<QBUser> dataList, String newText) {
        newText=newText.toLowerCase();
        String text = null;
        filteredDataList=new ArrayList<QBUser>();
        for(QBUser dataFromDataList:dataList){

            if (newText.matches("^[0-9]*$")||newText.contains("@"))
            {
                text= dataFromDataList.getLogin().toLowerCase();
            }else
            {
                text= dataFromDataList.getFullName().toLowerCase();
            }


            if(text.toLowerCase().contains(newText)){
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    @Override
    public void Reload()
    {  loadUsersFromQb();}


    /*public void doSearchQuery(String searchText) {
        if (usersAdapter != null) {
            usersAdapter.filter(searchText);
        }
    }*/

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight()+80;
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
    void show_dialog()
    {
        Shimmer_Effect.startShimmerAnimation();
        Shimmer_Effect.setVisibility(View.VISIBLE);
    }

    void hide_dialog()
    {
        Shimmer_Effect.stopShimmerAnimation();
        Shimmer_Effect.setVisibility(View.GONE);

    }
}