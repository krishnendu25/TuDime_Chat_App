package com.TuDime.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.TuDime.NetworkOperation.IJSONParseListener;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.Prefrences.CiaoPrefrences;
import com.TuDime.R;
import com.TuDime.constants.ApiConstants;
import com.TuDime.ui.Model.CardsModel;
import com.TuDime.ui.adapter.AlphaInAnimationAdapter;
import com.TuDime.ui.adapter.Ecards_categories_adapter;
import com.TuDime.ui.adapter.ScaleInAnimationAdapter;
import com.TuDime.Prefrences.SharedPrefsHelper;

public class Missing_fragment extends BaseFragment implements View.OnClickListener , IJSONParseListener {
    private static final int REQUEST_CODE_DOODLE = 10006;
    private View v;
    private RecyclerView recyclerView;
    private ArrayList<CardsModel> list_images;
    private Ecards_categories_adapter adapter1;
    private CiaoPrefrences prefrences;
    private int count = 0;
    private String language = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.categories_pages_layout, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.category);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(linearLayoutManager);

        list_images = new ArrayList<>();
        prefrences = CiaoPrefrences.getInstance(getActivity());
        v.findViewById(R.id.load_more).setOnClickListener(this);

        if ((prefrences.getEcardAppLang()).equals("0")) {
            if ((prefrences.getAppLang()).equals("es")) {
                language = "spanish";
            } else if ((prefrences.getAppLang()).equals("en")) {
                language = "english";
            } else if ((prefrences.getAppLang()).equals("hi")) {
                language = "hindi";
            }
        } else {
            language = prefrences.getEcardAppLang();
        }

        String category =  getArguments().getString("category");
        hitEcardsApi();
        /*if(language.equals("english")) {
            if (category.equals("love")) {
                if (prefrences.getLoveLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getLoveLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getLoveLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("birthday")) {
                if (prefrences.getBdayLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getBdayLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getBdayLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("friendship")) {
                if (prefrences.getFriendLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getFriendLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getFriendLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("someonespecial")) {
                if (prefrences.getSomeSpecLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getSomeSpecLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getSomeSpecLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("missing")) {
                if (prefrences.getMissLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getMissLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getMissLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("congrats")) {
                if (prefrences.getCongoLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getCongoLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getCongoLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("goodluck")) {
                if (prefrences.getLuckLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getLuckLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getLuckLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("getwell")) {
                if (prefrences.getWelleLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getWelleLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getWelleLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("inivitation")) {
                if (prefrences.getInviteLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getInviteLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getInviteLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("thank_you")) {
                if (prefrences.getThankLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getThankLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getThankLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("sorry")) {
                if (prefrences.getSorryLoadCount().equals("0")) {
                    hitEcardsApi(prefrences.getSorryLoadCount(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getSorryLoadCount()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            }
        }else if(language.equals("hindi")){
            if (category.equals("love")) {
                if (prefrences.getLoveLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getLoveLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getLoveLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("birthday")) {
                if (prefrences.getBdayLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getBdayLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getBdayLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("friendship")) {
                if (prefrences.getFriendLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getFriendLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getFriendLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("someonespecial")) {
                if (prefrences.getSomeSpecLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getSomeSpecLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getSomeSpecLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("missing")) {
                if (prefrences.getMissLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getMissLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getMissLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("congrats")) {
                if (prefrences.getCongoLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getCongoLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getCongoLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("goodluck")) {
                if (prefrences.getLuckLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getLuckLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getLuckLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("getwell")) {
                if (prefrences.getWelleLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getWelleLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getWelleLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("inivitation")) {
                if (prefrences.getInviteLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getInviteLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getInviteLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("thank_you")) {
                if (prefrences.getThankLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getThankLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getThankLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("sorry")) {
                if (prefrences.getSorryLoadCountHindi().equals("0")) {
                    hitEcardsApi(prefrences.getSorryLoadCountHindi(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getSorryLoadCountHindi()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            }
        }else if(language.equals("spanish")){
            if (category.equals("love")) {
                if (prefrences.getLoveLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getLoveLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getLoveLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("birthday")) {
                if (prefrences.getBdayLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getBdayLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getBdayLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("friendship")) {
                if (prefrences.getFriendLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getFriendLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getFriendLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("someonespecial")) {
                if (prefrences.getSomeSpecLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getSomeSpecLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getSomeSpecLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("missing")) {
                if (prefrences.getMissLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getMissLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getMissLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("congrats")) {
                if (prefrences.getCongoLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getCongoLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getCongoLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("goodluck")) {
                if (prefrences.getLuckLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getLuckLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getLuckLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("getwell")) {
                if (prefrences.getWelleLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getWelleLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getWelleLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("inivitation")) {
                if (prefrences.getInviteLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getInviteLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getInviteLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("thank_you")) {
                if (prefrences.getThankLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getThankLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getThankLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            } else if (category.equals("sorry")) {
                if (prefrences.getSorryLoadCountSpanish().equals("0")) {
                    hitEcardsApi(prefrences.getSorryLoadCountSpanish(), "0");
                } else {
                    for (int i = 0; i <= Integer.parseInt(prefrences.getSorryLoadCountSpanish()); i++) {
                        hitEcardsApi(String.valueOf(i), String.valueOf(i));
                    }
                }
            }
        }*/

        return v;
    }

    public void hitEcardsApi() {
       /* if(s.equals("0")) {
            ((Ecards3D_Activity) getActivity()).showProgressDialog();
        }
        String url = ApiConstants.BASE_URL1 + "rule=missing_cards";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(s.equals("0")) {
                                ((Ecards3D_Activity) getActivity()).removeProgressDialog();
                            }
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("1")) {
                                JSONArray array = jsonObject.getJSONArray("message");
                                if(array.length() == 0){
                                    if(s.equals("0")) {
                                        Toast.makeText(getActivity(), getString(R.string.no_more_ecards), Toast.LENGTH_SHORT).show();
                                    }
                                }else if(array.length() > 0) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        CardsModel model = new CardsModel();
                                        model.setId(object.getString("id"));
                                        model.setCards_images(object.getString("pics"));
                                        model.setCategories(object.getString("category"));
                                        list_images.add(model);
                                    }

                                    if (adapter1 == null) {
                                        adapter1 = new Ecards_categories_adapter(getActivity(), list_images, recyclerView);
                                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter1);
                                        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
                                        scaleAdapter.setDuration(1000);
                                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                                        recyclerView.setAdapter(scaleAdapter);
                                    } else {
//                                        adapter1.notifyDataSetChanged();
                                       int position =  adapter1.getItem_position();

                                        adapter1 = new Ecards_categories_adapter(getActivity(), list_images, recyclerView);
                                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter1);
                                        ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
                                        scaleAdapter.setDuration(1000);
                                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                                        recyclerView.setAdapter(scaleAdapter);
                                        recyclerView.smoothScrollToPosition(position);
                                    }
                                }
                            }else{
                                Toast.makeText(getActivity(), getString(R.string.no_more_ecards), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            if(s.equals("0")) {
                                ((Ecards3D_Activity) getActivity()).removeProgressDialog();
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    if (s.equals("0")) {
                        ((Ecards3D_Activity) getActivity()).removeProgressDialog();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                count  = Integer.parseInt(loveLoadCount);
                params.put("category", getArguments().getString("category"));
                params.put("counter", loveLoadCount);
                if ((prefrences.getEcardAppLang()).equals("0")) {
                    if ((prefrences.getAppLang()).equals("es")) {
                        params.put("language", "spanish");
                    } else if ((prefrences.getAppLang()).equals("en")) {
                        params.put("language", "english");
                    } else if ((prefrences.getAppLang()).equals("hi")) {
                        params.put("language", "hindi");
                    }
                } else {
                    params.put("language", prefrences.getEcardAppLang());
                }
                return params;
            }
        };
        ((BaseApplication) getActivity().getApplicationContext()).getVolleyManagerInstance().addToRequestQueue(request, "cards");*/


showDialog(getContext().getResources().getString(R.string.load));
        String url = ApiConstants.BASE_URL1 + "rule=missing_cards";
        JSONObject data;
        JSONRequestResponse mResponse = new JSONRequestResponse(getActivity());

        Bundle params = new Bundle();
        params.putString("category" , getArguments().getString("category"));
        params.putString("counter" ,"");
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
                    363, this, params, false, false, data);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onClick(View view) {
//        count++;
//        hitEcardsApi();
//    }
//}
        hitEcardsApi();
      /* String category =  getArguments().getString("category");
        if(category.equals("love")){
            hitEcardsApi(prefrences.getLoveLoadCountHindi(), "0");
            count++;
            if(language.equals("english")) {
                prefrences.setLoveLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getLoveLoadCount(), "0");
            }else if(language.equals("spanish")){
                prefrences.setLoveLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getLoveLoadCountSpanish(), "0");
            }else if(language.equals("hindi")){
                prefrences.setLoveLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getLoveLoadCountHindi(), "0");
            }
        }else if(category.equals("birthday")){
            count++;
            if(language.equals("english")) {
                prefrences.setBdayLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getBdayLoadCount(), "0");
            }else if(language.equals("spanish")){
                prefrences.setBdayLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getBdayLoadCountSpanish(), "0");
            }else if(language.equals("hindi")){
                prefrences.setBdayLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getBdayLoadCountHindi(), "0");
            }
        }else if(category.equals("friendship")){
            count++;
            if(language.equals("english")) {
                prefrences.setFriendLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getFriendLoadCount(), "0");
            }else if(language.equals("spanish")){
                prefrences.setFriendLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getFriendLoadCountSpanish(), "0");
            }else if(language.equals("hindi")){
                prefrences.setFriendLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getFriendLoadCountHindi(), "0");
            }
        }else if(category.equals("someonespecial")){
            count++;
            if(language.equals("english")) {
                prefrences.setSomeSpecLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getSomeSpecLoadCount(), "0");
            }else if(language.equals("spanish")){
                prefrences.setSomeSpecLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getSomeSpecLoadCountSpanish(), "0");
            }else if(language.equals("hindi")){
                prefrences.setSomeSpecLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getSomeSpecLoadCountHindi(), "0");
            }
        }else if(category.equals("missing")){
            count++;

            if(language.equals("english")) {
                prefrences.setMissLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getMissLoadCount() , "0");
            }else if(language.equals("spanish")){
                prefrences.setMissLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getMissLoadCountSpanish() , "0");
            }else if(language.equals("hindi")){
                prefrences.setMissLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getMissLoadCountHindi() , "0");
            }

        }else if(category.equals("congrats")){
            count++;

            if(language.equals("english")) {
                prefrences.setCongoLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getCongoLoadCount() , "0");
            }else if(language.equals("spanish")){
                prefrences.setCongoLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getCongoLoadCountSpanish() , "0");
            }else if(language.equals("hindi")){
                prefrences.setCongoLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getCongoLoadCountHindi() , "0");
            }
        }else if(category.equals("goodluck")){
            count++;

            if(language.equals("english")) {
                prefrences.setLuckLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getLuckLoadCount() , "0");
            }else if(language.equals("spanish")){
                prefrences.setLuckLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getLuckLoadCountSpanish() , "0");
            }else if(language.equals("hindi")){
                prefrences.setLuckLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getLuckLoadCountHindi() , "0");
            }
        }else if(category.equals("getwell")){
            count++;

            if(language.equals("english")) {
                prefrences.setWellLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getWelleLoadCount() , "0");
            }else if(language.equals("spanish")){
                prefrences.setWellLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getWelleLoadCountSpanish() , "0");
            }else if(language.equals("hindi")){
                prefrences.setWellLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getWelleLoadCountHindi() , "0");
            }
        }else if(category.equals("inivitation")){
            count++;
            if(language.equals("english")) {
                prefrences.setInviteLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getInviteLoadCount() , "0");
            }else if(language.equals("spanish")){
                prefrences.setInviteLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getInviteLoadCountSpanish() , "0");
            }else if(language.equals("hindi")){
                prefrences.setInviteLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getInviteLoadCountHindi() , "0");
            }
        }else if(category.equals("thank_you")){
            count++;

            if(language.equals("english")) {
                prefrences.setThankLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getThankLoadCount() , "0");
            }else if(language.equals("spanish")){
                prefrences.setThankLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getThankLoadCountSpanish() , "0");
            }else if(language.equals("hindi")){
                prefrences.setThankLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getThankLoadCountHindi() , "0");
            }
        }else if(category.equals("sorry")){
            count++;

            if(language.equals("english")) {
                prefrences.setSorryLoadCount(String.valueOf(count));
                hitEcardsApi(prefrences.getSorryLoadCount() , "0");
            }else if(language.equals("spanish")){
                prefrences.setSorryLoadCountSpanish(String.valueOf(count));
                hitEcardsApi(prefrences.getSorryLoadCountSpanish() , "0");
            }else if(language.equals("hindi")){
                prefrences.setSorryLoadCountHindi(String.valueOf(count));
                hitEcardsApi(prefrences.getSorryLoadCountHindi() , "0");
            }
        }*/
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
    }

    @Override
    public void SuccessResponse(JSONObject jsonObject, int requestCode)
    {
         if (requestCode==363)
         {
             cancleDialog();
             try {

                 if (jsonObject.getString("status").equals("1")) {
                     JSONArray array = jsonObject.getJSONArray("message");
                     if(array.length() == 0){

                             Toast.makeText(getActivity(), getString(R.string.no_more_ecards), Toast.LENGTH_SHORT).show();

                     }else if(array.length() > 0) {
                         for (int i = 0; i < array.length(); i++) {
                             JSONObject object = array.getJSONObject(i);
                             CardsModel model = new CardsModel();
                             model.setId(object.getString("id"));
                             model.setCards_images(object.getString("pics"));
                             model.setCategories(object.getString("category"));
                             list_images.add(model);
                         }

                         if (adapter1 == null) {
                             adapter1 = new Ecards_categories_adapter(getActivity(), list_images, recyclerView);
                             AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter1);
                             ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
                             scaleAdapter.setDuration(1000);
                             scaleAdapter.setInterpolator(new OvershootInterpolator());
                             recyclerView.setAdapter(scaleAdapter);
                         } else {
//                                        adapter1.notifyDataSetChanged();
                             int position =  adapter1.getItem_position();

                             adapter1 = new Ecards_categories_adapter(getActivity(), list_images, recyclerView);
                             AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter1);
                             ScaleInAnimationAdapter scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
                             scaleAdapter.setDuration(1000);
                             scaleAdapter.setInterpolator(new OvershootInterpolator());
                             recyclerView.setAdapter(scaleAdapter);
                             recyclerView.smoothScrollToPosition(position);
                         }
                     }
                 }else{
                     Toast.makeText(getActivity(), getString(R.string.no_more_ecards), Toast.LENGTH_SHORT).show();
                 }




             } catch (Exception e) {
                 if(e.equals("0")) {
                     Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                 }
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
