package obj.quickblox.sample.chat.java.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import obj.quickblox.sample.chat.java.R;

public class Tudime_Contact_Fragment extends Fragment {
    public Tudime_Contact_Fragment() {

    }
    public static Tudime_Contact_Fragment newInstance() {
        Tudime_Contact_Fragment fragment = new Tudime_Contact_Fragment();

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tudime__contact_, container, false);





    }
}
