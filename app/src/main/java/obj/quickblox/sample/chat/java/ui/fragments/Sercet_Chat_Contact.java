package obj.quickblox.sample.chat.java.ui.fragments;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.db.QbUsersDbManager;
import obj.quickblox.sample.chat.java.ui.Callback.Go_to_Chat;
import obj.quickblox.sample.chat.java.ui.adapter.CheckboxUsersAdapter;
import obj.quickblox.sample.chat.java.ui.adapter.Show_All_Contact_Adapter;
import obj.quickblox.sample.chat.java.utils.ErrorUtils;
import obj.quickblox.sample.chat.java.utils.PermissionsChecker;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Sercet_Chat_Contact#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sercet_Chat_Contact extends BaseFragment implements Go_to_Chat {
    SwipeRefreshLayout Serect_Contact_Refresh;
    ListView Serect_Contact_List;
    private CheckboxUsersAdapter usersAdapter;
    private static final int PER_PAGE_SIZE = 100;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc string updated_at";
    private ProgressBar progressBar;
    ArrayList<QBUser> users_s = new ArrayList<>();
    private QBChatDialog qbChatDialog_Qb;
    private PermissionsChecker checker;



    public Sercet_Chat_Contact() {

    }

    public static Sercet_Chat_Contact newInstance() {
     Sercet_Chat_Contact fragment=null;
        if (fragment!=null)
        {
            return fragment;
        }else
        {
            fragment = new Sercet_Chat_Contact();
            return fragment;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sercet__chat__contact, container, false);
        Serect_Contact_Refresh = view.findViewById(R.id.Serect_Contact_Refresh);
        Serect_Contact_List = view.findViewById(R.id.Serect_Contact_List);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_select_users_fr);
        QBChatDialog dialog = null;
        qbChatDialog_Qb = dialog;/*(QBChatDialog) getActivity().getIntent().getSerializableExtra(null);*/
        checker = new PermissionsChecker(getActivity());
        Serect_Contact_Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        loadUsersFromQb();


        return view;
    }

    private void loadUsersFromQb() {
        ArrayList<GenericQueryRule> rules = new ArrayList<>();
        rules.add(new GenericQueryRule(ORDER_RULE, ORDER_VALUE));

        QBPagedRequestBuilder qbPagedRequestBuilder = new QBPagedRequestBuilder();
        qbPagedRequestBuilder.setRules(rules);
        qbPagedRequestBuilder.setPerPage(PER_PAGE_SIZE);

        progressBar.setVisibility(View.VISIBLE);
        users_s.clear();
        QBUsers.getUsers(qbPagedRequestBuilder, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                users_s.addAll(users);
                usersAdapter = new CheckboxUsersAdapter(getContext(), users,Sercet_Chat_Contact.this,true);
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
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateUsersAdapter()
    {
        if (qbChatDialog_Qb != null) {
            usersAdapter.addSelectedUsers(qbChatDialog_Qb.getOccupants());
        }
        Serect_Contact_List.setAdapter(usersAdapter);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void passtochat(ArrayList<QBUser> QBList) {

    }
    void showErrorSnackbar(@StringRes int resId, Exception e, View.OnClickListener clickListener) {
        View rootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null) {
            ErrorUtils.showSnackbar(rootView, resId, e,
                    R.string.dialog_retry, clickListener).show();
        }
    }
}
