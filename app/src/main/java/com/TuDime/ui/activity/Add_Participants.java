package com.TuDime.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.StringRes;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.VolleyError;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import com.TuDime.R;
import com.TuDime.ui.Callback.Go_to_Chat;
import com.TuDime.ui.adapter.CheckboxUsersAdapter;
import com.TuDime.utils.ErrorUtils;
import com.TuDime.utils.PermissionsChecker;

public class Add_Participants extends BaseActivity implements Go_to_Chat {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participants);
        ButterKnife.bind(this);
        actionBar.setTitle("Add Participants");
        actionBar.setDisplayHomeAsUpEnabled(true);
        Serect_Contact_Refresh = findViewById(R.id.Serect_Contact_Refresh);
        Serect_Contact_List = findViewById(R.id.Serect_Contact_List);
        progressBar = (ProgressBar)findViewById(R.id.progress_select_users_fr);
        QBChatDialog dialog = null;
        qbChatDialog_Qb = dialog;/*(QBChatDialog) getActivity().getIntent().getSerializableExtra(null);*/
        checker = new PermissionsChecker(this);
        Serect_Contact_Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        loadUsersFromQb();

    }


    private void loadUsersFromQb() {
        ArrayList<GenericQueryRule> rules = new ArrayList<>();
        rules.add(new GenericQueryRule(ORDER_RULE, ORDER_VALUE));

        QBPagedRequestBuilder qbPagedRequestBuilder = new QBPagedRequestBuilder();
        qbPagedRequestBuilder.setRules(rules);
        qbPagedRequestBuilder.setPerPage(PER_PAGE_SIZE);

        showProgressDialog(R.string.load);
        users_s.clear();
        QBUsers.getUsers(qbPagedRequestBuilder, null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                users_s.addAll(users);
                usersAdapter = new CheckboxUsersAdapter(Add_Participants.this, users_s,"Add_Participants");
                updateUsersAdapter();
                hideProgressDialog();
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
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
    public void showErrorSnackbar(@StringRes int resId, Exception e, View.OnClickListener clickListener) {
        View rootView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null) {
            ErrorUtils.showSnackbar(rootView, resId, e,
                    R.string.dialog_retry, clickListener).show();
        }
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }
}
