package com.TuDime.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.TuDime.NetworkOperation.IJSONParseListener;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.constants.ApiConstants;
import com.TuDime.ui.Model.CardsModel;
import com.TuDime.ui.adapter.Recycler_Ecards_Adapter;
import com.TuDime.Prefrences.SharedPrefsHelper;

/**
 * Created by mahak on 7/13/2017.
 */

public class EcardsHomePage extends BaseFragment implements IJSONParseListener {
    private View v;
    public static ViewPager pager;
    private Recycler_Ecards_Adapter adapter1;
    private ArrayList<CardsModel> list_images;
    private RecyclerView recyclerView;
    public final static int LOOPS = 1000;
    public static int FIRST_PAGE = 0;
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.ecards_home_page , container , false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity() , LinearLayoutManager.HORIZONTAL , false);
        recyclerView = (RecyclerView)v. findViewById(R.id.recycler_category);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(linearLayoutManager);
        hitEcardsApi();

        return v;
    }

    public void hitEcardsApi() {
        /*showDialog(getContext().getResources().getString(R.string.load));
        String url = ApiConstants.BASE_URL1 + "rule=cards";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           cancleDialog();
                            list_images = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("1")) {
                                JSONArray array = jsonObject.getJSONArray("message");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        CardsModel model = new CardsModel();
                                        model.setId(object.getString("id"));
                                        model.setCards_images(object.getString("images"));
                                        model.setCategories(object.getString("category"));
                                        list_images.add(model);
                                    }
                                adapter1 = new Recycler_Ecards_Adapter(getActivity(), list_images , recyclerView);
                                recyclerView.setAdapter(adapter1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                ((Ecards3D_Activity)getActivity()). removeProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("category" , "commoncard");
                if((CiaoPrefrences.getInstance(getActivity()).getEcardAppLang()).equals("0")){
                    if ((CiaoPrefrences.getInstance(getActivity()).getAppLang()).equals("es")) {
                        params.put("language", "spanish");
                    } else if ((CiaoPrefrences.getInstance(getActivity()).getAppLang()).equals("en")) {
                        params.put("language", "english");
                    } else if ((CiaoPrefrences.getInstance(getActivity()).getAppLang()).equals("hi")) {
                        params.put("language", "hindi");
                    }
                }else {
                    params.put("language" , CiaoPrefrences.getInstance(getActivity()).getEcardAppLang());
                }
                return params;
            }
        };
        ((BaseApplication) getActivity().getApplicationContext()).getVolleyManagerInstance().addToRequestQueue(request, "cards");*/

        showDialog(getContext().getResources().getString(R.string.load));
        String url = ApiConstants.BASE_URL1 + "rule=cards";
        JSONObject data;
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());
        Bundle params = new Bundle();
        params.putString("category" , "commoncard");
        if ((SharedPrefsHelper.getInstance().get_Language()).equals("es")) {
            params.putString("language", "spanish");
        } else if ((SharedPrefsHelper.getInstance().get_Language()).equals("en")) {
            params.putString("language", "english");
        } else if ((SharedPrefsHelper.getInstance().get_Language()).equals("hi")) {
            params.putString("language", "hindi");
        }
        data = new JSONObject();
        MyVolley.init(getActivity());
        try {
            mResponse.getResponse(Request.Method.POST, url,
                    485, this, params, false, false, data);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject jsonObject, int requestCode) {

        if (requestCode==485)

        {
            cancleDialog();

            try {
                cancleDialog();
                list_images = new ArrayList<>();
                if (jsonObject.getString("status").equals("1")) {
                    JSONArray array = jsonObject.getJSONArray("message");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        CardsModel model = new CardsModel();
                        model.setId(object.getString("id"));
                        model.setCards_images(object.getString("images"));
                        model.setCategories(object.getString("category"));
                        list_images.add(model);
                    }
                    adapter1 = new Recycler_Ecards_Adapter(getActivity(), list_images , recyclerView);
                    recyclerView.setAdapter(adapter1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }





        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {
    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
    }
}
