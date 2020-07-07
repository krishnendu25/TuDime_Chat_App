package obj.quickblox.sample.chat.java.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.adapter.FaqAdapter;

public class ExtraFaqActivity extends AppCompatActivity {
    private ExpandableListView elvFaq;
    private FaqAdapter adapterFaq;
    ArrayList<String> listDataHeader;
    HashMap<String, ArrayList<String>> listDataChild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_faq);
        Instantiation();
    }

    private void Instantiation()
    {
        elvFaq = (ExpandableListView) findViewById(R.id.elv_faq);
        prepareListData();
        adapterFaq = new FaqAdapter(getApplicationContext(), listDataHeader, listDataChild);
        elvFaq.setGroupIndicator(null);

        elvFaq.setAdapter(adapterFaq);
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, ArrayList<String>>();

        // Adding child data
        String ques1 = getResources().getString(R.string.ques1);
        String ques2 = getResources().getString(R.string.ques2);
        String ques3 = getResources().getString(R.string.ques3);
        String ques4 = getResources().getString(R.string.ques4);
        String ques5 = getResources().getString(R.string.ques5);
        String ques6 = getResources().getString(R.string.ques6);
        String ques7 = getResources().getString(R.string.ques7);
        String ques8 = getResources().getString(R.string.ques8);
        String ques9 = getResources().getString(R.string.ques9);
        String ques10 = getResources().getString(R.string.ques10);

        String ans1 = getResources().getString(R.string.ans1);
        String ans2 = getResources().getString(R.string.ans2);
        String ans3 = getResources().getString(R.string.ans3);
        String ans4 = getResources().getString(R.string.ans4);
        String ans5 = getResources().getString(R.string.ans5);
        String ans6 = getResources().getString(R.string.ans6);
        String ans7 = getResources().getString(R.string.ans7);
        String ans8 = getResources().getString(R.string.ans8);
        String ans9 = getResources().getString(R.string.ans9);
        String ans10 = getResources().getString(R.string.ans10);

        listDataHeader.add(ques1);
        listDataHeader.add(ques2);
        listDataHeader.add(ques3);
        listDataHeader.add(ques4);
        listDataHeader.add(ques5);
        listDataHeader.add(ques6);
        listDataHeader.add(ques7);
        listDataHeader.add(ques8);
        listDataHeader.add(ques9);
        listDataHeader.add(ques10);
        // Adding child data
        ArrayList<String> alfirst = new ArrayList<String>();
        alfirst.add(ans1);

        ArrayList<String> alSecond = new ArrayList<String>();
        alSecond.add(ans2);

        ArrayList<String> alThird = new ArrayList<String>();
        alThird.add(ans3);

        ArrayList<String> alFour = new ArrayList<String>();
        alFour.add(ans4);

        ArrayList<String> alFive = new ArrayList<String>();
        alFive.add(ans5);

        ArrayList<String> alSix = new ArrayList<String>();
        alSix.add(ans6);

        ArrayList<String> alSeven = new ArrayList<String>();
        alSeven.add(ans7);

        ArrayList<String> alEight = new ArrayList<String>();
        alEight.add(ans8);

        ArrayList<String> alNine = new ArrayList<String>();
        alNine.add(ans9);

        ArrayList<String> alTen = new ArrayList<String>();
        alTen.add(ans10);

        listDataChild.put(listDataHeader.get(0), alfirst); // Header, Child data
        listDataChild.put(listDataHeader.get(1), alSecond);
        listDataChild.put(listDataHeader.get(2), alThird);
        listDataChild.put(listDataHeader.get(3), alFour);
        listDataChild.put(listDataHeader.get(4), alFive);
        listDataChild.put(listDataHeader.get(5), alSix);
        listDataChild.put(listDataHeader.get(6), alSeven);
        listDataChild.put(listDataHeader.get(7), alEight);
        listDataChild.put(listDataHeader.get(8), alNine);
        listDataChild.put(listDataHeader.get(9), alTen);

    }
}
