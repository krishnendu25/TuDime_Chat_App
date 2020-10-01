package obj.quickblox.sample.chat.java.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.Utils;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.Prefrences.CiaoPrefrences;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.db.QbUsersDbManager;
import obj.quickblox.sample.chat.java.services.LoginService;
import obj.quickblox.sample.chat.java.ui.Callback.Contact_chat_Refresh;
import obj.quickblox.sample.chat.java.ui.Callback.Search_Fragments;
import obj.quickblox.sample.chat.java.ui.Model.Contact_Model;
import obj.quickblox.sample.chat.java.ui.Model.Subscription_Model;
import obj.quickblox.sample.chat.java.ui.fragments.Calls_Fragment;
import obj.quickblox.sample.chat.java.ui.fragments.Chat_Fragment;
import obj.quickblox.sample.chat.java.ui.fragments.Contact_chat_Fragment;
import obj.quickblox.sample.chat.java.utils.Constant;
import obj.quickblox.sample.chat.java.utils.InternetConnection;
import obj.quickblox.sample.chat.java.utils.KeyboardUtils;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_profile;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_tudime_fetch_my_call_balence;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.update_user_profile;
import static obj.quickblox.sample.chat.java.ui.activity.SignUpActivity.hasPermissions;

public class DashBoard extends BaseActivity implements QBRTCClientSessionCallbacks, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //Dialog_Activity
    private static final String TAG = DashBoard.class.getSimpleName();
    public static String User_PhoneNo = "", User_Email = "";
    private static MyPagerAdapter adapterViewPager;
    ImageView image1, image_bg;
    @BindView(R.id.FloatingActionMenu)
    com.github.clans.fab.FloatingActionMenu FloatingActionMenu;
    int mSelectedTabPosition;
    FloatingActionButton add_contact, action_secret_chat, Refresh_contact, Refresh_chat;
    Search_Fragments search_fragments;
    TextView Subscrption_managment, call_directly, text_call_credit;
    ArrayList<Subscription_Model> Subscription_Model = new ArrayList<>();
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private AppBarLayout appbar;
    private Toolbar toolbar;
    private TextView total_credit, name_display;
    private ViewPager pager;
    private TabLayout tabs;
    private List<Contact_Model> Local_Contact = null;
    private QBUser qbUser;
    private String Account_Create = "";
    private String Global_FCM_TOKEN;
    private SearchView searchView;
    private TextView showAppVersion;

    public static void start(Context context) {
        Intent intent = new Intent(context, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_dash_board);
        ButterKnife.bind(this);
        Initialization();
        hideActionbar();
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                mSelectedTabPosition = position;
                Objects.requireNonNull(pager.getAdapter()).notifyDataSetChanged();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
                if (position == 2) {
                    action_secret_chat.setVisibility(View.GONE);
                    Refresh_chat.setVisibility(View.GONE);
                    Refresh_contact.setVisibility(View.VISIBLE);
                    FloatingActionMenu.setVisibility(View.VISIBLE);
                    add_contact.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    add_contact.setVisibility(View.GONE);
                    action_secret_chat.setVisibility(View.GONE);
                    Refresh_contact.setVisibility(View.GONE);
                    FloatingActionMenu.setVisibility(View.VISIBLE);
                    Refresh_chat.setVisibility(View.VISIBLE);

                } else if (position == 0) {
                    FloatingActionMenu.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });


    }

    private void startLoginService() {
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            LoginService.start(this, qbUser);
        }
    }

    private void Initialization() {
        try {
            FirebaseApp.initializeApp(this);
            try {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        Global_FCM_TOKEN = instanceIdResult.getToken();
                        try {
                            subscribeToPushNotifications(Global_FCM_TOKEN);
                        } catch (Exception e) {
                        }

                    }
                });

            } catch (Exception e) { }
        } catch (Exception e) { }

        add_contact = findViewById(R.id.add_contact);
        action_secret_chat = findViewById(R.id.action_secret_chat);
        Refresh_contact = findViewById(R.id.Refresh_contact);
        Refresh_chat = findViewById(R.id.Refresh_chat);
        qbUser = new QBUser();
        qbUser.setLogin(SharedPrefsHelper.getInstance().getQbUser().getLogin());
        qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        pager = (ViewPager) findViewById(R.id.pager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);
        Local_Contact = new ArrayList<>();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, this.getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        View v = navigationView.getHeaderView(0);
        v.findViewById(R.id.ll1);
        name_display = (TextView) v.findViewById(R.id.txt_anna);
        image1 = (ImageView) v.findViewById(R.id.image_drawer);
        image_bg = (ImageView) v.findViewById(R.id.imge_bg);
        v.findViewById(R.id.image_drawer).setOnClickListener(this);
        View vv = v.findViewById(R.id.listView1);
        vv.findViewById(R.id.linear_encounter123).setVisibility(View.VISIBLE);
        vv.findViewById(R.id.text_new_gp).setOnClickListener(this);
        vv.findViewById(R.id.text_new_bd).setOnClickListener(this);
        vv.findViewById(R.id.text_setting).setOnClickListener(this);
        vv.findViewById(R.id.sigout).setOnClickListener(this);
        vv.findViewById(R.id.text_encounters).setOnClickListener(this);
        vv.findViewById(R.id.txt_schedule_event).setOnClickListener(this);
        vv.findViewById(R.id.total_credit).setOnClickListener(this);
        vv.findViewById(R.id.text_ecards).setOnClickListener(this);
        showAppVersion =   vv.findViewById(R.id.showAppVersion);
        vv.findViewById(R.id.text_schedule_events).setOnClickListener(this);
        vv.findViewById(R.id.text_change_language).setOnClickListener(this);
        vv.findViewById(R.id.Subscrption_managment).setOnClickListener(this);
        vv.findViewById(R.id.Subscrption_managment).setSelected(true);
        vv.findViewById(R.id.call_directly).setOnClickListener(this);
        vv.findViewById(R.id.text_call_credit).setOnClickListener(this);
        vv.findViewById(R.id.text_feedback).setOnClickListener(this);
        total_credit = (TextView) vv.findViewById(R.id.total_credit);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapterViewPager);
        adapterViewPager.notifyDataSetChanged();
        pager.setCurrentItem(1);
        mSelectedTabPosition = 1;
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addContactIntent = new Intent(Contacts.Intents.Insert.ACTION, Contacts.People.CONTENT_URI);
                addContactIntent.putExtra(Contacts.Intents.Insert.NAME, "");
                startActivity(addContactIntent);
            }
        });
        Refresh_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact_chat_Refresh contact_chat_refresh = Chat_Fragment.newInstance();
                contact_chat_refresh.Reload();

            }
        });


        Refresh_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact_chat_Refresh contact_chat_refresh = Contact_chat_Fragment.newInstance();
                contact_chat_refresh.Reload();

            }
        });
        action_secret_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this, Secret_Chat.class);
                startActivity(intent);
            }
        });

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            showAppVersion.setText("Version "+packageInfo.versionName);

        }
        catch (PackageManager.NameNotFoundException e) {
            showAppVersion.setText("1.0.0.0 beta");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String string_find) {
                if (mSelectedTabPosition == 0) {
                    search_fragments = Calls_Fragment.newInstance();
                    search_fragments.filter(string_find);
                }
                if (mSelectedTabPosition == 1) {

                    search_fragments = Chat_Fragment.newInstance();
                    search_fragments.filter(string_find);
                }
                if (mSelectedTabPosition == 2) {
                    search_fragments = Contact_chat_Fragment.newInstance();
                    search_fragments.filter(string_find);
                }
                return true;
            }
        });


        return true;
    }

    protected void onResume() {
        super.onResume();
        try{
            if (InternetConnection.checkConnection(this)) {
                try {
                    LoginService.start(getApplicationContext(), sharedPrefsHelper.getQbUser());
                } catch (Exception e) {
                }
            }
        }catch (Exception e){}
       try{
           Update_Profile_Update(SharedPrefsHelper.getInstance().getUSERID()
                   , SharedPrefsHelper.getInstance().getUserName(),
                   SharedPrefsHelper.getInstance().getQbUser().getId().toString());
           Fetch_My_Call_Balence(SharedPrefsHelper.getInstance().getUSERID());
       }catch (Exception e){}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.Subscrption_managment:
                startActivity(new Intent(DashBoard.this, TuDime_Membership.class));
                break;
            case R.id.text_new_gp:
                startActivity(new Intent(DashBoard.this, UpdateProfileActivity.class));
                break;
            case R.id.text_new_bd:
                startActivity(new Intent(DashBoard.this, NotificationCenterActivity.class));
                break;
            case R.id.text_encounters:
                startActivity(new Intent(DashBoard.this, EncountersActivity.class));
                break;
            case R.id.text_schedule_events:
                startActivity(new Intent(DashBoard.this, ScheduleTaskActivity.class));
                break;
            case R.id.call_directly:
                startActivity(new Intent(DashBoard.this, TuDime_CAN.class));
                break;
            case R.id.text_change_language:
                Intent i2 = new Intent(DashBoard.this, SetLanguage.class);
                i2.putExtra("coming_from", "dashboard");
                startActivity(i2);
                break;
            case R.id.text_setting:
                startActivity(new Intent(DashBoard.this, Settings_Tudime.class));
                break;
            case R.id.text_feedback:
                startActivity(new Intent(DashBoard.this, FeedbackActivity.class));
                break;
            case R.id.text_call_credit:
                Intent i = new Intent(DashBoard.this, PapPallIntegration.class);
                startActivity(i);
                break;
            case R.id.image_drawer:
                startActivity(new Intent(DashBoard.this, UpdateProfileActivity.class));
                break;
            case R.id.txt_schedule_event:
                startActivity(new Intent(DashBoard.this, ScheduleTaskActivity.class));
                break;
            case R.id.sigout:
                try {
                    SharedPrefsHelper.clearAllData();
                    CiaoPrefrences.getInstance(this).clearPrefrences();
                    Intent ii = new Intent(getApplicationContext(), SetLanguage.class);
                    ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(ii);
                    QbUsersDbManager dbManager = QbUsersDbManager.getInstance(this);
                    dbManager.deleteall();
                    finish();
                } catch (Exception e) {   }
                break;
            case R.id.text_ecards:
                Intent intent = new Intent(DashBoard.this, Ecards3D_Activity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) { }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) { }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) { }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

        if (requestCode == 715) {
            hideProgressDialog();
            try {
                String Balence_Api;
                if (response.getString("status").equalsIgnoreCase("success")) {
                    JSONArray data = response.getJSONArray("data");

                    if (data.length() != 0) {
                        Balence_Api = data.getJSONObject(0).getString("plan_price");
                        if (Balence_Api.equalsIgnoreCase("")) {
                            Balence_Api = "0";
                        }
                        total_credit.setText("$" + Balence_Api);
                    } else {
                        Balence_Api = "0";
                        total_credit.setText("$" + Balence_Api);
                    }

                } else {
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }
        }


        if (requestCode == 584) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    Fetch_Profile_Update(SharedPrefsHelper.getInstance().getUSERID());
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }

        }
        if (requestCode == 447) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    Account_Create = response.getJSONArray("data").getJSONObject(0).getString("create_dt_timestamp");
                    Fetch_Membership_Details(SharedPrefsHelper.getInstance().getUSERID(), "");
                    if (response.getJSONArray("data").length() == 0) {
                        try {
                            Picasso.get().load(SharedPrefsHelper.getInstance().getQbUser().getWebsite()).placeholder(R.drawable.navigation_drawer_pro_pic)
                                    .into(image1);
                        } catch (Exception e) {
                            Picasso.get().load("").placeholder(R.drawable.navigation_drawer_pro_pic)
                                    .into(image1);
                        }
                    } else {
                        name_display.setText(response.getJSONArray("data").getJSONObject(0).getString("name"));

                        try {
                            Picasso.get().load(response.getJSONArray("data").getJSONObject(0).getString("Cover_pic_url")).placeholder(R.drawable.navigation_drawer_pro_pic)
                                    .into(image1);
                            Picasso.get().load(response.getJSONArray("data").getJSONObject(0).getString("pic1_url")).placeholder(R.drawable.navigation_drawer_cover_bg)
                                    .into(image_bg);
                        } catch (Exception e) {

                        }
                    }
                } else {
                    ToastUtils.shortToast("Oops...something went wrong...");
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }
        }

        if (requestCode == 897) {
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    Subscription_Model.clear();
                    JSONArray all_data = response.getJSONArray("data");

                    for (int i = 0; i < all_data.length(); i++) {
                        Subscription_Model sb = new Subscription_Model();
                        sb.setStart_time(all_data.getJSONObject(i).getString("start_time_unix_timestamp"));
                        sb.setEnd_time(all_data.getJSONObject(i).getString("end_time_unix_timestamp"));
                        Subscription_Model.add(sb);
                    }
                    Filter_Subscription(Account_Create);
                } else {
                    ToastUtils.shortToast("Oops...something went wrong...");
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }
        }
    }


    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }

    void Update_Profile_Update(String userid, String name, String QB_User_id) {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("userid", userid);
        parms.putString("name", name);
        if (SharedPrefsHelper.getInstance().getProfilePhotostatus() != null) {
            if (SharedPrefsHelper.getInstance().getProfilePhotostatus().equalsIgnoreCase("")) {
                parms.putString("privacy_status", getString(R.string.public_profile));
            }
        }
        parms.putString("QB_User_id", QB_User_id);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, update_user_profile,
                584, this, parms, false, false, Params_Object);
    }

    void Fetch_Profile_Update(String userid) {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid", userid);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, get_user_profile,
                447, this, parms, false, false, Params_Object);
    }

    private void Fetch_My_Call_Balence(String userid) {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid", userid);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, get_user_tudime_fetch_my_call_balence, 715, this, parms, false, false, Params_Object);
    }

    public void subscribeToPushNotifications(String registrationID) {
        try {
            QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
            subscription.setEnvironment(QBEnvironment.DEVELOPMENT);
            String deviceId = getCurrentDeviceId();
            subscription.setDeviceUdid(deviceId);
            subscription.setRegistrationID(registrationID);
            QBPushNotifications.createSubscription(subscription).performAsync(new QBEntityCallback<ArrayList<QBSubscription>>() {
                @Override
                public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                }
            });
        } catch (Exception e) {
        }
    }

    private String getCurrentDeviceId() {
        return Utils.generateDeviceId(this);
    }

    private void Filter_Subscription(String account_create) {

        String Acount_Create = Constant.Get_back_date(account_create);
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        Integer Day_Count = (int) Constant.getDateDiff(Acount_Create, timeStamp);



            if (Subscription_Model.size() > 0) {
                int count = 0;
                for (int i = 0; i < Subscription_Model.size(); i++) {

                    String Day_Count_v = String.valueOf(Constant.getDateDiff(timeStamp, Constant.Get_back_date(Subscription_Model.get(i).getEnd_time().toString())));
                    if (Integer.valueOf(Day_Count_v) > 0) {
                        count++;
                    }
                }
                if (count == 0) {
                    if (Integer.valueOf(Day_Count) <= 45 && Integer.valueOf(Day_Count) >= 0) {
                    } else {
                        Open_Subscrption_alert(DashBoard.this);
                    }
                }

            } else {
                if (Integer.valueOf(Day_Count) <= 45 && Integer.valueOf(Day_Count) >= 0) {
                } else {
                    Open_Subscrption_alert(DashBoard.this);
                }
            }
    }

    private void Open_Subscrption_alert(Activity mActivity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (this).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.no_subscription, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        TextView Application_Close = (TextView) dialogView.findViewById(R.id.Application_Close);
        TextView buy_membership = (TextView) dialogView.findViewById(R.id.buy_membership);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Application_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    alertDialog.dismiss();
            }
        });
        buy_membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                startActivity(new Intent(DashBoard.this, Buy_TuDime_Subscription.class));
            }
        });
        alertDialog.show();
    }
    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Calls_Fragment.newInstance();
                case 1:
                    return Chat_Fragment.newInstance();
                case 2:
                    return Contact_chat_Fragment.newInstance();
                default:
                    return null;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0) {
                return getResources().getString(R.string.CALLS);
            } else if (position == 1) {
                return getResources().getString(R.string.CHATS);
            } else {
                return getResources().getString(R.string.CONTACTS);
            }

        }
    }

}
