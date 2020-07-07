package obj.quickblox.sample.chat.java.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import obj.quickblox.sample.chat.java.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Secret_Chat_Dialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Secret_Chat_Dialog extends Fragment {
    SwipeRefreshLayout Serect_Chat_Refresh;
    ListView Serect_Chat_List;

    public Secret_Chat_Dialog() {
        // Required empty public constructor
    }


    public static Secret_Chat_Dialog newInstance() {
        Secret_Chat_Dialog fragment=null;
        if (fragment!=null)
        {
            return fragment;
        }else
        {
            fragment = new Secret_Chat_Dialog();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_secret_chat_dialog, container, false);

        Serect_Chat_Refresh = view.findViewById(R.id.Serect_Chat_Refresh);
        Serect_Chat_List = view.findViewById(R.id.Serect_Chat_List);

        Serect_Chat_Refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });



        return view;
    }
}
