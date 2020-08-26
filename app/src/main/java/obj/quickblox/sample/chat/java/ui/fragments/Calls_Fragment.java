package obj.quickblox.sample.chat.java.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.db.QbUsersDbManager;
import obj.quickblox.sample.chat.java.ui.Callback.Search_Fragments;
import obj.quickblox.sample.chat.java.ui.Model.Call_model;
import obj.quickblox.sample.chat.java.ui.adapter.Call_Adapter;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.qb.QbUsersHolder;

public class Calls_Fragment extends BaseFragment implements Search_Fragments {
    private static Calls_Fragment calls_fragment;
    ArrayList<Call_model> call_models;
    RecyclerView rcvContacts;
    Call_Adapter call_adapter;
    TextView txvEmptyView;
    private ArrayList<Call_model> filteredDataList;
    // newInstance constructor for creating fragment with arguments
    public static Calls_Fragment newInstance() {
        if (calls_fragment!=null)
        {
            return calls_fragment;
        }else
        {
            calls_fragment = new Calls_Fragment();
            return calls_fragment;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        call_models = new ArrayList<>();
        QbUsersDbManager dbManager = QbUsersDbManager.getInstance(getContext());
        Cursor cursor = dbManager.get_all_call(String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
        if (cursor.getCount() != 0) {
            call_models.clear();
            while (cursor.moveToNext()) {
                Call_model call_model = new Call_model();
                call_model.setDB_CALL_RECIPIENTID(cursor.getString(3));
                call_model.setDB_CALL_RECIPIENTNAME(cursor.getString(2));
                call_model.setDB_CALL_TYPE(cursor.getString(7));
                call_model.setDB_CALL_COUNT(cursor.getString(4));
                call_model.setDB_CALL_START_TIME(cursor.getString(6));
                call_model.setDB_CALL_QBUSER(cursor.getString(1));
                call_model.setCall_status(cursor.getString(5));
                call_models.add(call_model);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.call_fragment_layout, container, false);
        rcvContacts = view.findViewById(R.id.rcvContacts);
        rcvContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        txvEmptyView = view.findViewById(R.id.txvEmptyView);


        if (call_models.size() > 0) {
            txvEmptyView.setVisibility(View.GONE);
        } else {
            txvEmptyView.setVisibility(View.VISIBLE);
        }

        Collections.reverse(call_models);
        call_adapter = new Call_Adapter(getContext(), call_models);
        rcvContacts.setAdapter(call_adapter);
        call_adapter.notifyDataSetChanged();
        return view;

    }




    private ArrayList<Call_model> filter(ArrayList<Call_model> dataList, String newText) {
        newText=newText.toLowerCase();
        String text = null;
        filteredDataList=new ArrayList<Call_model>();
        for(Call_model dataFromDataList:dataList){

            if (newText.matches("^[0-9]*$"))
            {
                text= dataFromDataList.getDB_CALL_RECIPIENTNAME().toLowerCase();
            }else
            {
                text= dataFromDataList.getDB_CALL_RECIPIENTNAME().toLowerCase();
            }


            if(text.toLowerCase().contains(newText)){
                filteredDataList.add(dataFromDataList);
            }
        }

        return filteredDataList;
    }

    @Override
    public void filter(String S) {
        try{
            call_adapter.setFilter( filter(call_models, S));
        }catch (Exception e)
        {

        }


    }
}
