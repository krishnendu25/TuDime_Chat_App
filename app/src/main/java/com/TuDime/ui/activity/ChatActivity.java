package com.TuDime.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TuDime.EmojiKeyBoard.EmojiKeyboard;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.R;
import com.TuDime.constants.Consts;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.managers.DialogsManager;
import com.TuDime.services.CallService;
import com.TuDime.services.LoginService;
import com.TuDime.ui.Callback.ChatConstants;
import com.TuDime.ui.Callback.Language_Translator;
import com.TuDime.ui.Callback.QouteChatTrigger;
import com.TuDime.ui.Callback.send_contact;
import com.TuDime.ui.Model.Call_model;
import com.TuDime.ui.Model.Contact_Model;
import com.TuDime.ui.Model.SerectMessageModel;
import com.TuDime.ui.adapter.AttachmentPreviewAdapter;
import com.TuDime.ui.adapter.ChatAdapter;
import com.TuDime.ui.adapter.Contact_send_adapter;
import com.TuDime.ui.adapter.WallpaperGridAdapter;
import com.TuDime.ui.adapter.listeners.AttachClickListener;
import com.TuDime.ui.adapter.listeners.SetclickCallback;
import com.TuDime.ui.dialog.ProgressDialogFragment;
import com.TuDime.ui.widget.AttachmentPreviewAdapterView;
import com.TuDime.util.AppUtility;
import com.TuDime.utils.Constant;
import com.TuDime.utils.PermissionsChecker;
import com.TuDime.utils.PushNotificationSender;
import com.TuDime.utils.SystemPermissionHelper;
import com.TuDime.utils.ToastUtils;
import com.TuDime.utils.WebRtcSessionManager;
import com.TuDime.utils.chat.ChatHelper;
import com.TuDime.utils.imagepick.ImagePickHelper;
import com.TuDime.utils.imagepick.OnImagePickedListener;
import com.TuDime.utils.qb.PaginationHistoryListener;
import com.TuDime.utils.qb.QbChatDialogMessageListenerImp;
import com.TuDime.utils.qb.QbDialogHolder;
import com.TuDime.utils.qb.QbDialogUtils;
import com.TuDime.utils.qb.QbUsersHolder;
import com.TuDime.utils.qb.VerboseQbChatConnectionListener;
import com.TuDime.utils.views.DoodleActivity;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBMessageStatusesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.listeners.QBMessageStatusListener;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.listeners.QBSystemMessageListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.TuDime.constants.ApiConstants.Upload_File;
import static com.TuDime.constants.ApiConstants.detectlanguage;
import static com.TuDime.ui.activity.Chat_profile.QB_User_Id;

public class ChatActivity extends BaseActivity implements QouteChatTrigger, Language_Translator, AdapterView.OnItemSelectedListener, SetclickCallback, View.OnClickListener, send_contact, OnImagePickedListener, QBMessageStatusListener, DialogsManager.ManagingDialogsCallbacks {
    public static final String EXTRA_DIALOG_ID = "dialogId";
    public static final String IS_SERECT_CHAT = "false";
    public static final String EXTRA_IS_NEW_DIALOG = "isNewDialog";
    public static final String IS_IN_BACKGROUND = "is_in_background";
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int MAX_UPLOAD_FILES = 10, DOODLE_SEND = 5465, ECARD_SEND = 88124;
    private static final int REQUEST_CODE_ATTACHMENT = 721;
    private static final int REQUEST_CODE_SELECT_PEOPLE = 752;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc string created_at";
    private static final int LIMIT_PER_PAGE = 100;
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int PICKFILE_RESULT_CODE = 5547;
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    public static String USER_NAME = "";
    public static String Class_Name = "", SMS_VALUE = "";
    private static String CHAT_DIALOG_TYPE = "0";
    private final Handler handler = new Handler();
    protected List<QBChatMessage> messagesList;
    ArrayList<Contact_Model> Local_Contact = new ArrayList<>();
    LinearLayout CONTACT_VIEW;
    ArrayList<String> language_aaray = new ArrayList<>();
    TextView typing_view;
    @BindView(R.id.Do_voice_call)
    TextView DoVoiceCall;
    @BindView(R.id.Do_Video_Call)
    TextView DoVideoCall;
    @BindView(R.id.Call_View)
    CardView CallView;
    ShimmerFrameLayout Shimmer_Effect;
    Runnable updater;
    Handler timerHandler = null;
    TestAsync testAsk = null;
    @BindView(R.id.QouteChatName)
    TextView QouteChatName;
    LinearLayout chatBoxSubMenu;
    ImageView showChatSubMenu;
    LinearLayout QouteChatView;
    TextView QouteChatTV;
    TextView closeQouteChat;
    //Timer Popup
    ImageView close_popup_timer;
    TextView timer_dropdown;
    RelativeLayout chatTimerLayout;
    CardView saveTimerData;
    ArrayList<SerectMessageModel> serectChatID = new ArrayList<>();
    private EditText messageEditText;
    private ImageView speech_text, imgOptions;
    private LinearLayout attachmentPreviewContainerLayout;
    private ChatAdapter chatAdapter;
    private RecyclerView chatMessagesRecyclerView;
    private AttachmentPreviewAdapter attachmentPreviewAdapter;
    private ConnectionListener chatConnectionListener;
    private ImageAttachClickListener imageAttachClickListener;
    private QBMessageStatusesManager qbMessageStatusesManager;
    private DialogsManager dialogsManager;
    private SystemMessagesListener systemMessagesListener;
    private QBSystemMessagesManager systemMessagesManager;
    private QBChatDialog qbChatDialog;
    private ArrayList<QBChatMessage> unShownMessages;
    private int skipPagination = 0;
    private ChatMessageListener chatMessageListener;
    private boolean checkAdapterInit;
    private PermissionsChecker checker;
    private QBUser currentUser;
    private EmojiKeyboard emojiKeyboard;
    //Other_View
    private CardView OTHER_SHOW_View;
    private ImageButton doodle_Ib, location_Ib, gallery_Ib, Theam_Ib, ecardd_Ib, Contact_Ib, File_Ib, Video_Ib;
    //Theame
    private LinearLayout THEAME_VIEW;
    private GridView grdWallpapers;
    private TextView my_theme, txvDone, txvCancel, txvCancel_CON;
    private View layout_chat_container;
    private ListView contact_recyler;
    //ML KIT Translation
    private LinearLayout Translation_View;
    private Spinner spin_translate;
    private EditText text;
    private ImageView speech_to_text, dismiss;
    private Button tranlate_btn;
    private WallpaperGridAdapter wallpaperGridAdapter;
    private ImageView wallpaper_et;
    private QbUsersDbManager dbManager = QbUsersDbManager.getInstance(this);
    private QBRoster chatRoster = null;
    private String is_Serect;
    private View progressBar;
    private String SelectLang_Code = "";
    private int adapter_position;
    private Bitmap bitmap;
    private File destination;

    public static void startForResult(Activity activity, int code, QBChatDialog dialogId) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        activity.startActivityForResult(intent, code);
    }

    public static void startForResult(Activity activity, int code, QBChatDialog dialogId, boolean isNewDialog) {
        Intent intent = new Intent(activity, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DIALOG_ID, dialogId);
        intent.putExtra(ChatActivity.EXTRA_IS_NEW_DIALOG, isNewDialog);
        activity.startActivityForResult(intent, code);
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {//DocumentsContract.isDocumentUri(context.getApplicationContext(), uri))
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String SaveImagetoSDcard(String imagename, Bitmap img, Activity mActivity) {
        File mydir = new File(mActivity.getFilesDir() + "/" + System.currentTimeMillis() + "_Tudime/");
        if (!mydir.exists()) {
            mydir.mkdir();
        }
        File image = new File(mydir, imagename + ".png");

        boolean success = false;
        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            img.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {

        } catch (Exception e) {

        }
        if (success) {
            String finalimageurl = mydir.toString() + "/" + imagename + ".png";
            return finalimageurl;
        } else {
            return "";
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        SharedPrefsHelper.getInstance().delete(IS_IN_BACKGROUND);
        try {
            is_Serect = getIntent().getStringExtra(IS_SERECT_CHAT);
        } catch (Exception e) {
            is_Serect = "false";
        }
        Initialization();
        initViews();
        initMessagesRecyclerView();
        initChatConnectionListener();
        initChat();
        startLoginService();
        speech_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySpeechRecognizer(SPEECH_REQUEST_CODE);

            }
        });
        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEmojieKeyBoard();
            }
        });
        //Typing Status
        messageEditText.addTextChangedListener(new TextWatcher() {
            private final long DELAY = 2000; // milliseconds
            boolean isTyping = false;
            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isTyping) {
                    Log.d(TAG, "started typing");
                    try {
                        qbChatDialog.sendIsTypingNotification();
                    } catch (XMPPException | SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    isTyping = true;
                }
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       isTyping = false;
                                       Log.d(TAG, "stopped typing");
                                       try {
                                           qbChatDialog.sendStopTypingNotification();
                                       } catch (XMPPException | SmackException.NotConnectedException e) {
                                           e.printStackTrace();
                                       }
                                   }
                               },
                        DELAY);
            }
        });

        if (Constant.isOnline(this)) {
            QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
                @Override
                public void processUserIsTyping(String dialogId, Integer senderId) {
                    setActionbarSubTitle(getResources().getString(R.string.typing));
                }

                @Override
                public void processUserStopTyping(String dialogId, Integer senderId) {
                    setActionbarSubTitle("Online");
                }
            };
            qbChatDialog.addIsTypingListener(typingListener);
            //Online/ofline
            QBSubscriptionListener subscriptionListener = new QBSubscriptionListener() {
                @Override
                public void subscriptionRequested(int userId) {
                    try {
                        if (chatRoster != null)
                            chatRoster.confirmSubscription(userId);
                    } catch (SmackException.NotConnectedException e) {

                    } catch (SmackException.NotLoggedInException e) {

                    } catch (XMPPException e) {

                    } catch (SmackException.NoResponseException e) {

                    }
                }
            };
            chatRoster = QBChatService.getInstance().getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
            // to send subscription request
            try {
                chatRoster.subscribe(qbChatDialog.getRecipientId()); //getRecipientId is Opponent UserID
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
            QBRosterListener rosterListener = new QBRosterListener() {
                @Override
                public void entriesDeleted(Collection<Integer> userIds) {
                }

                @Override
                public void entriesAdded(Collection<Integer> userIds) {
                }

                @Override
                public void entriesUpdated(Collection<Integer> userIds) {
                }

                @Override
                public void presenceChanged(QBPresence presence1) {
                    try {
                        int userid = presence1.getUserId();
                        int recid = qbChatDialog.getRecipientId();//opponent   user id
                        if (userid == recid) {

                            if (presence1.getType() == QBPresence.Type.online)
                                //status.setText(getResources().getString(R.string.online));
                                setActionbarSubTitle("Online");
                            else {
                                setActionbarSubTitle("");
                                String lastseen = getlastseen();
                                if (lastseen.length() > 0) {
                                    setActionbarSubTitle(lastseen);
                                }
                            }
                        } else {
                        }
                    } catch (Exception e) {

                    }

                }
            };
            QBPresence presence = chatRoster.getPresence(qbChatDialog.getRecipientId());
            if (presence.getType() == QBPresence.Type.online) {
                rosterListener.presenceChanged(presence);
            } else {
                rosterListener.presenceChanged(presence);
            }
            updateTime();
        }
    }

    void updateTime() {
        Handler timerHandler = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                try {
                    if (testAsk == null) {
                        new TestAsync().execute();
                    } else {
                        testAsk.execute();
                    }
                    if (!SharedPrefsHelper.getInstance().get_Timer().equalsIgnoreCase("")){
                        if (serectChatID.size() > 0) {
                            if (SharedPrefsHelper.getInstance().get_SERCET_CHAT().equalsIgnoreCase("1")) {
                                for (int i = 0; i < serectChatID.size(); i++) {
                                    JSONObject jsonObject = new JSONObject(SharedPrefsHelper.getInstance().get_Timer());
                                    int time = Integer.parseInt(jsonObject.getString("time"));
                                    String previousTime =jsonObject.getString("fulltime");
                                    int differce = (int) AppUtility.DateTimeDifference(System.currentTimeMillis(), Long.parseLong(previousTime), AppUtility.TimeDifference.MINUTE);
                                    if (differce>time){
                                        deleteSms(serectChatID.get(i));
                                    }
                                    serectChatID.remove(i);
                                }
                            }
                        }
                    }



                } catch (Exception e) {
                }
                timerHandler.postDelayed(updater, 30000);
            }
        };
        timerHandler.post(updater);

        //SerectChat
        if (is_Serect != null) {
            if (is_Serect.equalsIgnoreCase("true")) {
                SharedPrefsHelper.getInstance().set_SERCET_CHAT("1");
                CHAT_DIALOG_TYPE = SharedPrefsHelper.getInstance().get_SERCET_CHAT();
                wallpaper_et.setBackgroundColor(getResources().getColor(R.color.black));
                skipPagination = 0;
                checkAdapterInit = false;
                joinGroupChat();
            }
        }


    }

    private void deleteSms(SerectMessageModel smsID) {
        Set<String> messagesIds = new HashSet<String>();
        try {
            messagesIds.add(smsID.getMessageID());
            messagesIds.add(smsID.getDeliveredIds().toString());
        } catch (Exception e) {

        }
        QBRestChatService.deleteMessages(messagesIds, true).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {


            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    private void Initialization() {
        testAsk = new TestAsync();
        chatTimerLayout = (RelativeLayout) findViewById(R.id.chatTimerLayout);
        close_popup_timer = (ImageView) findViewById(R.id.close_popup_timer);
        timer_dropdown = (TextView) findViewById(R.id.timer_dropdown);
        saveTimerData = (CardView) findViewById(R.id.saveTimerData);
        saveTimerData.setOnClickListener(this);
        timer_dropdown.setOnClickListener(this);
        close_popup_timer.setOnClickListener(this);
        SharedPrefsHelper.getInstance().set_SERCET_CHAT("0");
        CHAT_DIALOG_TYPE = SharedPrefsHelper.getInstance().get_SERCET_CHAT();
        wallpaper_et = findViewById(R.id.wallpaper_et);
        Shimmer_Effect = findViewById(R.id.Shimmer_Effect);
        my_theme = findViewById(R.id.my_theme);
        typing_view = findViewById(R.id.typing_view);
        Translation_View = (LinearLayout) findViewById(R.id.Translation_View);
        spin_translate = (Spinner) findViewById(R.id.spin_translate);
        text = (EditText) findViewById(R.id.text);
        speech_to_text = (ImageView) findViewById(R.id.speech_to_text);
        dismiss = (ImageView) findViewById(R.id.dismiss);
        tranlate_btn = (Button) findViewById(R.id.tranlate_btn);
        chatBoxSubMenu = (LinearLayout) findViewById(R.id.chatBoxSubMenu);
        showChatSubMenu = (ImageView) findViewById(R.id.showChatSubMenu);
        chatBoxSubMenu.setOnClickListener(this);
        showChatSubMenu.setOnClickListener(this);
        tranlate_btn.setOnClickListener(this);
        speech_to_text.setOnClickListener(this);
        dismiss.setOnClickListener(this);
        my_theme.setOnClickListener(this);
        contact_recyler = (ListView) findViewById(R.id.contact_recyler);
        CONTACT_VIEW = (LinearLayout) findViewById(R.id.CONTACT_VIEW);
        speech_text = (ImageView) findViewById(R.id.speech_text);
        imgOptions = (ImageView) findViewById(R.id.imgOptions);
        dialogsManager = new DialogsManager();
        dialogsManager.addManagingDialogsCallbackListener(this);
        QouteChatView = (LinearLayout) findViewById(R.id.QouteChatView);
        QouteChatTV = (TextView) findViewById(R.id.QouteChatTV);
        closeQouteChat = (TextView) findViewById(R.id.closeQouteChat);
        closeQouteChat.setOnClickListener(this);
        systemMessagesManager = QBChatService.getInstance().getSystemMessagesManager();
        systemMessagesListener = new SystemMessagesListener();
        try {
            qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
        } catch (Exception e) {
        }
        try {
            if (Constant.isOnline(this)) {

                qbChatDialog.initForChat(QBChatService.getInstance());
                chatMessageListener = new ChatMessageListener();
                qbChatDialog.addMessageListener(chatMessageListener);
            }
        } catch (Exception e) {
        }

        layout_chat_container = findViewById(R.id.layout_chat_container);
        layout_chat_container.setOnClickListener(this);
        emojiKeyboard = new EmojiKeyboard(this, layout_chat_container);
        checker = new PermissionsChecker(getApplicationContext());
        currentUser = SharedPrefsHelper.getInstance().getQbUser();
        OTHER_SHOW_View = (CardView) findViewById(R.id.OTHER_SHOW_View);
        OTHER_SHOW_View.setVisibility(View.GONE);
        doodle_Ib = (ImageButton) findViewById(R.id.doodle_Ib);
        location_Ib = (ImageButton) findViewById(R.id.location_Ib);
        Theam_Ib = (ImageButton) findViewById(R.id.Theam_Ib);
        ecardd_Ib = (ImageButton) findViewById(R.id.ecardd_Ib);
        Contact_Ib = (ImageButton) findViewById(R.id.Contact_Ib);
        File_Ib = (ImageButton) findViewById(R.id.File_Ib);
        Video_Ib = (ImageButton) findViewById(R.id.Video_Ib);
        doodle_Ib.setOnClickListener(this);
        location_Ib.setOnClickListener(this);
        Theam_Ib.setOnClickListener(this);
        ecardd_Ib.setOnClickListener(this);
        Contact_Ib.setOnClickListener(this);
        File_Ib.setOnClickListener(this);
        Video_Ib.setOnClickListener(this);

        //Theam
        THEAME_VIEW = (LinearLayout) findViewById(R.id.THEAME_VIEW);
        grdWallpapers = (GridView) findViewById(R.id.grdWallpapers);
        txvDone = (TextView) findViewById(R.id.txvDone);
        txvDone.setVisibility(View.GONE);
        txvCancel = (TextView) findViewById(R.id.txvCancel);
        txvCancel_CON = (TextView) findViewById(R.id.txvCancel_CON);
        txvCancel_CON.setOnClickListener(this);
        txvDone.setOnClickListener(this);
        txvCancel.setOnClickListener(this);
        new MyTask().execute("my string parameter");
        /*ArrayList<Integer> imgArr = new ArrayList<Integer>();
        for (int i = 0; i < 107; i++) {
            imgArr.add(getResources().getIdentifier("theme_thumb_" + (i + 1), "drawable", getPackageName()));
        }*/

        String[] tab_titles = getApplicationContext().getResources().getStringArray(R.array.theam_1);
        List<String> wordList = Arrays.asList(tab_titles);
        wallpaperGridAdapter = new WallpaperGridAdapter(this, wordList);
        grdWallpapers.setAdapter(wallpaperGridAdapter);

        language_aaray.add(getString(R.string.select_your_language));//0
        language_aaray.add(getString(R.string.eng));//1
        language_aaray.add(getString(R.string.spanish));//2
        language_aaray.add(getString(R.string.hindi));//3
        language_aaray.add(getString(R.string.Thai));//4
        language_aaray.add(getString(R.string.Ben));//5
        language_aaray.add(getString(R.string.Tel));//6
        language_aaray.add(getString(R.string.Mar));//7
        language_aaray.add(getString(R.string.Fre));//8
        language_aaray.add(getString(R.string.Chi));//9
        language_aaray.add(getString(R.string.Kor));//10
        language_aaray.add(getString(R.string.Ara));//11
        language_aaray.add(getString(R.string.Jap));//12
        language_aaray.add(getString(R.string.Mal));//13
        language_aaray.add(getString(R.string.Por));//14
        language_aaray.add(getString(R.string.Vie));//15
        language_aaray.add(getString(R.string.tl));//16
        language_aaray.add(getString(R.string.it));//17

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.lnaguage_select_textview, R.id.txt_selct_lang, language_aaray);
        spin_translate.setAdapter(adapter);
        spin_translate.setOnItemSelectedListener(this);

        if (Constant.isOnline(this)) {
            if (qbChatDialog.getType() != QBDialogType.GROUP) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            } else {
                setActionbarSubTitle(String.valueOf(qbChatDialog.getOccupants().size()) + "group members");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_popup_timer:
                chatTimerLayout.setVisibility(View.GONE);
                break;
            case R.id.timer_dropdown:
                PopupMenu popup = new PopupMenu(ChatActivity.this, view);
                popup.getMenuInflater()
                        .inflate(R.menu.activity_chat_timer, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        timer_dropdown.setText(item.getTitle().toString().trim());
                        return true;
                    }
                });
                popup.show();
                break;
            case R.id.saveTimerData:
                chatTimerLayout.setVisibility(View.GONE);
                saveTimeDataSave(timer_dropdown.getText().toString());
                break;
            case R.id.closeQouteChat:
                QouteChatView.setVisibility(View.GONE);
                QouteChatTV.setText("");
                SharedPrefsHelper.getInstance().setQuotes("");
                break;
            case R.id.showChatSubMenu:
                if (chatBoxSubMenu.getVisibility() == View.GONE) {
                    chatBoxSubMenu.setVisibility(View.VISIBLE);
                } else {
                    chatBoxSubMenu.setVisibility(View.GONE);
                }
                break;

            case R.id.my_theme:
                OTHER_SHOW_View.setVisibility(View.GONE);
                try {
                    selectImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tranlate_btn:
                if (SelectLang_Code.equalsIgnoreCase("")) {
                    ToastUtils.shortToast(R.string.Select_one_language);
                } else if (text.getText().toString().trim().isEmpty()) {
                    ToastUtils.shortToast("Enter Your Text");
                } else {
                    translateSend(text.getText().toString().trim(), SelectLang_Code);
                }

                break;
            case R.id.txvCancel_CON:
                OTHER_SHOW_View.setVisibility(View.GONE);
                if (CONTACT_VIEW.getVisibility() == View.VISIBLE) {
                    CONTACT_VIEW.setVisibility(View.GONE);
                } else {
                    CONTACT_VIEW.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.layout_chat_container:
                OTHER_SHOW_View.setVisibility(View.GONE);
                CONTACT_VIEW.setVisibility(View.GONE);
                THEAME_VIEW.setVisibility(View.GONE);
                break;
            case R.id.doodle_Ib:
                OTHER_SHOW_View.setVisibility(View.GONE);
                Intent intentDoodle = new Intent(this, DoodleActivity.class);
                startActivityForResult(intentDoodle, DOODLE_SEND);
                OTHER_SHOW_View.setVisibility(View.GONE);
                break;
            case R.id.location_Ib:
                Intent intenddt = new Intent(this, MapSendLocationActivity.class);
                startActivity(intenddt);
                OTHER_SHOW_View.setVisibility(View.GONE);
                break;
            case R.id.Theam_Ib:
                OTHER_SHOW_View.setVisibility(View.GONE);
                if (THEAME_VIEW.getVisibility() == View.GONE) {
                    THEAME_VIEW.setVisibility(View.VISIBLE);
                } else {
                    THEAME_VIEW.setVisibility(View.GONE);
                }
                break;
            case R.id.ecardd_Ib:
                OTHER_SHOW_View.setVisibility(View.GONE);
                Intent intentt = new Intent(this, Ecards3D_Activity.class);
                startActivityForResult(intentt, ECARD_SEND);
                break;
            case R.id.Contact_Ib:
                OTHER_SHOW_View.setVisibility(View.GONE);
                if (CONTACT_VIEW.getVisibility() == View.VISIBLE) {
                    CONTACT_VIEW.setVisibility(View.GONE);
                } else {
                    CONTACT_VIEW.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.File_Ib:
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
                break;
            case R.id.Video_Ib:
                break;
            case R.id.txvCancel:
                if (THEAME_VIEW.getVisibility() == View.GONE) {
                    THEAME_VIEW.setVisibility(View.VISIBLE);
                    OTHER_SHOW_View.setVisibility(View.GONE);
                } else {
                    THEAME_VIEW.setVisibility(View.GONE);
                    OTHER_SHOW_View.setVisibility(View.GONE);
                }
                break;
            case R.id.dismiss:
                OTHER_SHOW_View.setVisibility(View.GONE);
                if (Translation_View.getVisibility() == View.VISIBLE) {
                    Translation_View.setVisibility(View.GONE);
                } else {
                    Translation_View.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.speech_to_text:
                chatBoxSubMenu.setVisibility(View.GONE);
                displaySpeechRecognizer(1212);
                break;

        }

    }

    private void saveTimeDataSave(String text) {
        try {
            if (text.trim().equalsIgnoreCase("")) {
                ToastUtils.longToast("Please Select Timer");
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("time",text.replaceAll("minutes", "").trim());
                jsonObject.put("fulltime",System.currentTimeMillis());
                SharedPrefsHelper.getInstance().set_Timer(String.valueOf(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 895);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 598);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void translateSend(String SMS, String SelectLang_Code) {
        showProgressDialog(R.string.dlg_loading);
        String url = detectlanguage;
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("text", SMS);
        parms.putString("targetlan", SelectLang_Code);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, url,
                691, this, parms, false, false, Params_Object);

    }

    private void translateSend(String SMS, String SelectLang_Code, String text) {
        showProgressDialog(R.string.dlg_loading);
        String url = detectlanguage;
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("text", SMS);
        parms.putString("targetlan", SelectLang_Code);
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, url,
                882, this, parms, false, false, Params_Object);

    }

    private void ShowEmojieKeyBoard() {
        messageEditText.requestFocus();
        emojiKeyboard.createEmojiKeyboard();
        emojiKeyboard.showEmoji();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer(int SPEECH_REQUEST_CODE) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (qbChatDialog != null) {
            outState.putString(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (qbChatDialog == null) {
            qbChatDialog = QbDialogHolder.getInstance().getChatDialogById(savedInstanceState.getString(EXTRA_DIALOG_ID));
        }
    }

    @Override
    public void onResumeFinished() {
        if (Constant.isOnline(getApplicationContext())) {
            if (ChatHelper.getInstance().isLogged()) {
                if (qbChatDialog == null) {
                    qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
                }
                returnToChat();
            } else {
                showProgressDialog(R.string.dlg_loading);
                ChatHelper.getInstance().loginToChat(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        returnToChat();
                        hideProgressDialog();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        hideProgressDialog();
                        if (Constant.isOnline(getApplicationContext())) {
                            finish();
                        }
                    }
                });
            }
        }

    }

    private void returnToChat() {
        qbChatDialog.initForChat(QBChatService.getInstance());
        if (!qbChatDialog.isJoined()) {
            try {
                qbChatDialog.join(new DiscussionHistory());
            } catch (Exception e) {
                if (Constant.isOnline(getApplicationContext())) {
                    finish();
                }
            }
        }
        // Loading unread messages received in background
        if (qbChatDialog.getType() != QBDialogType.PRIVATE &&
                SharedPrefsHelper.getInstance().get(IS_IN_BACKGROUND, false)) {
            show_dialog();
            skipPagination = 0;
            checkAdapterInit = false;
            loadChatHistory();
        }
        returnListeners();
    }

    private void returnToChat(int i) {
        qbChatDialog.initForChat(QBChatService.getInstance());
        if (!qbChatDialog.isJoined()) {
            try {
                qbChatDialog.join(new DiscussionHistory());
            } catch (Exception e) {
                if (Constant.isOnline(getApplicationContext())) {
                    finish();
                }
            }
        }
        // Loading unread messages received in background
        if (qbChatDialog.getType() != QBDialogType.PRIVATE &&
                SharedPrefsHelper.getInstance().get(IS_IN_BACKGROUND, false)) {
            skipPagination = 0;
            checkAdapterInit = false;
            loadChatHistory();
        }
        returnListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.isOnline(getApplicationContext())) {
            boolean isIncomingCall = SharedPrefsHelper.getInstance().get(Consts.EXTRA_IS_INCOMING_CALL, false);
            System.out.println("ChatActivity Serivce: " + isCallServiceRunning(CallService.class));
            if (isCallServiceRunning(CallService.class)) {
                Log.d(TAG, "CallService is running now " + isIncomingCall);
                System.out.println("ChatActivity Serivce: " + isIncomingCall);
                CallActivity.start(this, isIncomingCall);
            }
            clearAppNotifications();
        }


        if (qbChatDialog.getPhoto() != null) {

            if (!qbChatDialog.getPhoto().equalsIgnoreCase("")) {
                if (qbChatDialog.getType() != QBDialogType.GROUP) {

                    if (qbChatDialog.getPhoto().contains("http")) {
                        try {
                            Picasso.get().load(qbChatDialog.getPhoto().trim()).placeholder(R.drawable.theme_2).into(wallpaper_et);
                        } catch (Exception e) {
                            wallpaper_et.setBackground(getResources().getDrawable(R.drawable.theme_2));
                        }
                    } else {
                        int iconResId = Integer.valueOf(qbChatDialog.getPhoto());
                        wallpaper_et.setBackground(getResources().getDrawable(iconResId));
                    }
                } else {
                    wallpaper_et.setBackground(getResources().getDrawable(R.drawable.theme_2));
                }

            } else {
                wallpaper_et.setBackground(getResources().getDrawable(R.drawable.theme_2));
            }
        } else {
            wallpaper_et.setBackground(getResources().getDrawable(R.drawable.theme_2));
        }
        if (Class_Name.equalsIgnoreCase("MapSendLocationActivity")) {
            sendChatMessage(SMS_VALUE, null);
            Class_Name = "";
        }
        if (SharedPrefsHelper.getInstance().get_E_CARD_URL() != null) {
            if (!SharedPrefsHelper.getInstance().get_E_CARD_URL().equalsIgnoreCase("")) {
                messageEditText.setText(SharedPrefsHelper.getInstance().get_E_CARD_URL());
                //sendChatMessage(SharedPrefsHelper.getInstance().get_E_CARD_URL(), null);
                SharedPrefsHelper.getInstance().set_E_CARD_URL("");
            }
        }
        //Send Forward
        if (SharedPrefsHelper.getInstance().get_FORWARD() != null) {
            if (!SharedPrefsHelper.getInstance().get_FORWARD().equalsIgnoreCase("")) {
                sendChatMessage(SharedPrefsHelper.getInstance().get_FORWARD(), null);
                ToastUtils.longToast("Chat Forwarded Successfully");
                SharedPrefsHelper.getInstance().set_FORWARD("");
            }
        }
    }

    private boolean isCallServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void clearAppNotifications() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }

    private void returnListeners() {
        if (systemMessagesManager != null) {
            systemMessagesManager.addSystemMessageListener(systemMessagesListener != null
                    ? systemMessagesListener : new SystemMessagesListener());
        }
        chatAdapter.setAttachImageClickListener(imageAttachClickListener);
        ChatHelper.getInstance().addConnectionListener(chatConnectionListener);
        qbMessageStatusesManager = QBChatService.getInstance().getMessageStatusesManager();
        qbMessageStatusesManager.addMessageStatusListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPrefsHelper.getInstance().setQuotes("");
        if (Constant.isOnline(this)) {
            chatAdapter.removeAttachImageClickListener();
            ChatHelper.getInstance().removeConnectionListener(chatConnectionListener);
            qbMessageStatusesManager.removeMessageStatusListener(this);
            SharedPrefsHelper.getInstance().save(IS_IN_BACKGROUND, true);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPrefsHelper.getInstance().setQuotes("");
        if (Constant.isOnline(this)) {
            if (timerHandler != null)
                timerHandler.removeCallbacks(updater);
            if (systemMessagesManager != null) {
                systemMessagesManager.removeSystemMessageListener(systemMessagesListener);
            }
            try {
                qbChatDialog.removeMessageListrener(chatMessageListener);
                dialogsManager.removeManagingDialogsCallbackListener(this);
                SharedPrefsHelper.getInstance().delete(IS_IN_BACKGROUND);
            } catch (Exception e) {
            }

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Constant.isOnline(getApplicationContext())) {
            qbChatDialog.removeMessageListrener(chatMessageListener);
        }
        sendDialogId();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat, menu);
        MenuItem menuItemLeave = menu.findItem(R.id.menu_chat_action_leave);
        MenuItem menuItemAdd = menu.findItem(R.id.menu_chat_action_add);
        MenuItem Serect_Chat = menu.findItem(R.id.Serect_Chat);
        MenuItem menuItemDelete = menu.findItem(R.id.menu_chat_action_delete);
        MenuItem menu_chat_action_info = menu.findItem(R.id.menu_chat_action_info);
        MenuItem menu_start_audio_call = menu.findItem(R.id.menu_start_audio_call);
        MenuItem menu_clear_chat = menu.findItem(R.id.menu_clear_chat);
        MenuItem menu_Block_user = menu.findItem(R.id.menu_Block_user);
        switch (qbChatDialog.getType()) {
            case GROUP:
                menuItemDelete.setVisible(false);
                menu_start_audio_call.setVisible(false);
                Serect_Chat.setVisible(false);
                menuItemAdd.setVisible(false);
                menuItemLeave.setVisible(true);
                menu_Block_user.setVisible(false);
                menu_clear_chat.setVisible(false);
                if (is_Serect != null) {
                    if (is_Serect.equalsIgnoreCase("true")) {
                        Serect_Chat.setVisible(false);
                    } else {
                        Serect_Chat.setVisible(true);
                    }
                }
                break;
            case PRIVATE:
                menuItemLeave.setVisible(false);
                menuItemAdd.setVisible(false);
                menuItemLeave.setVisible(false);
                menuItemDelete.setVisible(true);
                menu_clear_chat.setVisible(false);
                menu_start_audio_call.setVisible(true);
                menu_Block_user.setVisible(false);
                break;
            case PUBLIC_GROUP:
                menuItemDelete.setVisible(false);
                menu_start_audio_call.setVisible(false);
                Serect_Chat.setVisible(false);
                menu_Block_user.setVisible(false);
                menuItemAdd.setVisible(false);
                menu_clear_chat.setVisible(false);
                menuItemLeave.setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.attachedment_btn:
                if (OTHER_SHOW_View.getVisibility() == View.GONE) {
                    OTHER_SHOW_View.setVisibility(View.VISIBLE);
                } else {
                    OTHER_SHOW_View.setVisibility(View.GONE);
                }
                break;
            case R.id.google_translator_call:
                OTHER_SHOW_View.setVisibility(View.GONE);
                if (Translation_View.getVisibility() == View.VISIBLE) {
                    Translation_View.setVisibility(View.GONE);
                } else {
                    Translation_View.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.menu_start_audio_call:
                OTHER_SHOW_View.setVisibility(View.GONE);
                if (CallView.getVisibility() == View.GONE) {
                    CallView.setVisibility(View.VISIBLE);
                } else {
                    CallView.setVisibility(View.GONE);
                }
                return true;
            case R.id.menu_chat_action_info:
                //ChatInfoActivity.start(this, qbChatDialog);
                try {
                    if (qbChatDialog.getType() == QBDialogType.GROUP) {
                        Group_Details_View.Group_Dialog = qbChatDialog;
                        Intent intent = new Intent(this, Group_Details_View.class);
                        startActivity(intent);

                    } else {
                        QBChatDialog selectedDialog = qbChatDialog;
                        QBUser user = QbUsersHolder.getInstance().getUserById(selectedDialog.getRecipientId());
                        Intent intent = new Intent(this, Chat_profile.class);
                        QB_User_Id = String.valueOf(selectedDialog.getRecipientId());
                        intent.putExtra("User_Name", user.getFullName());
                        intent.putExtra("QB_User_Id", String.valueOf(selectedDialog.getRecipientId()));
                        intent.putExtra("User_Login", user.getLogin());
                        intent.putExtra("User_Image_url", user.getWebsite());
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.menu_chat_action_add:
                updateDialog();
                return true;
            case R.id.menu_chat_action_leave:
                leaveGroupChat();
                return true;
            case R.id.menu_chat_action_delete:
                deleteChat();
                return true;
            case R.id.send_ecard:
                Intent intent = new Intent(this, Ecards3D_Activity.class);
                startActivity(intent);
                break;
            case R.id.Serect_Chat:
                ToastUtils.longToast("Please Wait.Time take Time to Create Secret Chat Window.");
                SharedPrefsHelper.getInstance().set_SERCET_CHAT("1");
                CHAT_DIALOG_TYPE = SharedPrefsHelper.getInstance().get_SERCET_CHAT();
                wallpaper_et.setBackgroundColor(getResources().getColor(R.color.black));
                skipPagination = 0;
                checkAdapterInit = false;
                loadChatHistory();
                //  joinGroupChat();
                break;
            case R.id.menu_chat_disappearing_timer:
                setChatDisappearingTimer();
                break;
            case R.id.menu_clear_chat:
                Clear_chat_Dialog();
                break;
            case R.id.menu_Block_user:
                Block_user_Dialog();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private void setChatDisappearingTimer() {
        chatTimerLayout.setVisibility(View.VISIBLE);
    }

    private void Block_user_Dialog() {

    }

    private void Clear_chat_Dialog() {
        List<QBChatMessage> list = new ArrayList<>();
        Set<String> messagesIds = new HashSet<String>();
        list.clear();
        messagesIds.clear();

        list = chatAdapter.getList();
        for (int i = 0; i < list.size(); i++) {
            messagesIds.add(list.get(i).getId());
        }

        QBRestChatService.deleteMessages(messagesIds, false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void updateDialog() {
        ProgressDialogFragment.show(getSupportFragmentManager());
        Log.d(TAG, "Update Dialog");
        ChatHelper.getInstance().getDialogById(qbChatDialog.getDialogId(), new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog updatedChatDialog, Bundle bundle) {
                Log.d(TAG, "Update Dialog Successful: " + updatedChatDialog.getDialogId());
                qbChatDialog = updatedChatDialog;
                loadUsersFromQb(updatedChatDialog);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d(TAG, "Update Dialog Error: " + e.getMessage());
                ProgressDialogFragment.hide(getSupportFragmentManager());
                showErrorSnackbar(R.string.select_users_get_dialog_error, e, null);
            }
        });
    }

    private void startCall(boolean isVideoCall) {
        Log.d(TAG, "Starting Call");
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(qbChatDialog.getRecipientId());
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
        Log.d(TAG, "conferenceType = " + conferenceType);
        QBRTCSession newQbRtcSession = QBRTCClient.getInstance(this).createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);
        PushNotificationSender.sendPushMessage(opponentsList, currentUser.getFullName());
        CallActivity.start(this, false);

        Cursor cursor_call = dbManager.get_CALL_BY_RECIPIENTID(qbChatDialog.getRecipientId().toString());
        if (cursor_call.getCount() != 0) {

            Call_model call_model = new Call_model();
            call_model.setDB_CALL_COUNT("");
            call_model.setDB_CALL_RECIPIENTID(qbChatDialog.getRecipientId().toString());
            call_model.setDB_CALL_RECIPIENTNAME(QbUsersHolder.getInstance().getUserById(qbChatDialog.getRecipientId()).getFullName());
            if (isVideoCall) {
                call_model.setDB_CALL_TYPE("video");
            } else {
                call_model.setDB_CALL_TYPE("audio");
            }
            call_model.setCall_status("OutGoing");
            call_model.setDB_CALL_START_TIME(String.valueOf(System.currentTimeMillis()));
            call_model.setDB_CALL_QBUSER(String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
            dbManager.insertcall(call_model);
        } else {
            Call_model call_model = new Call_model();
            call_model.setDB_CALL_COUNT("1");

            try {
                call_model.setDB_CALL_RECIPIENTID(qbChatDialog.getRecipientId().toString());
            } catch (Exception e) {
                call_model.setDB_CALL_RECIPIENTID("");
            }
            try {
                call_model.setDB_CALL_RECIPIENTNAME(QbUsersHolder.getInstance().getUserById(qbChatDialog.getRecipientId()).getFullName());
            } catch (Exception e) {
                call_model.setDB_CALL_RECIPIENTNAME("");
            }

            if (isVideoCall) {
                call_model.setDB_CALL_TYPE("video");
            } else {
                call_model.setDB_CALL_TYPE("audio");
            }
            call_model.setCall_status("OutGoing");
            call_model.setDB_CALL_START_TIME(String.valueOf(System.currentTimeMillis()));
            call_model.setDB_CALL_QBUSER(String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
            dbManager.insertcall(call_model);

        }


    }

    private void startPermissionsActivity(boolean checkOnlyAudio) {
        PermissionsActivity.startActivity(this, checkOnlyAudio, Consts.PERMISSIONS);
    }

    private void loadUsersFromQb(final QBChatDialog qbChatDialog) {
        ArrayList<GenericQueryRule> rules = new ArrayList<>();
        rules.add(new GenericQueryRule(ORDER_RULE, ORDER_VALUE));
        QBPagedRequestBuilder qbPagedRequestBuilder = new QBPagedRequestBuilder();
        qbPagedRequestBuilder.setRules(rules);
        qbPagedRequestBuilder.setPerPage(LIMIT_PER_PAGE);
        Log.d(TAG, "Loading Users");
        QBUsers.getUsers(qbPagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                Log.d(TAG, "Loading Users Successful");
                ProgressDialogFragment.hide(getSupportFragmentManager());
                if (qbChatDialog.getOccupants().size() >= users.size()) {
                    ToastUtils.shortToast(R.string.added_users);
                } else {
                    SelectUsersActivity.startForResult(ChatActivity.this, REQUEST_CODE_SELECT_PEOPLE, qbChatDialog);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d(TAG, "Loading Users Error: " + e.getMessage());
                ProgressDialogFragment.hide(getSupportFragmentManager());
                showErrorSnackbar(R.string.select_users_get_users_error, e, null);
            }
        });
    }

    private void sendDialogId() {
        Intent result = new Intent();
        result.putExtra(EXTRA_DIALOG_ID, qbChatDialog.getDialogId());
        setResult(RESULT_OK, result);
    }

    private void leaveGroupChat() {
        ProgressDialogFragment.show(getSupportFragmentManager());
        dialogsManager.sendMessageLeftUser(qbChatDialog);

        dialogsManager.sendSystemMessageLeftUser(systemMessagesManager, qbChatDialog);
        Log.d(TAG, "Leaving Dialog");
        ChatHelper.getInstance().exitFromDialog(qbChatDialog, new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbDialog, Bundle bundle) {
                Log.d(TAG, "Leaving Dialog Successful: " + qbDialog.getDialogId());
                ProgressDialogFragment.hide(getSupportFragmentManager());
                QbDialogHolder.getInstance().deleteDialog(qbDialog);
                Intent intent = new Intent(getApplicationContext(), DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d(TAG, "Leaving Dialog Error: " + e.getMessage());
                ProgressDialogFragment.hide(getSupportFragmentManager());
                showErrorSnackbar(R.string.error_leave_chat, e, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leaveGroupChat();
                    }
                });
            }
        });
    }

    private boolean checkIsLoggedInChat() {
        if (!QBChatService.getInstance().isLoggedIn()) {
            startLoginService();
            //    ToastUtils.shortToast(R.string.dlg_relogin_wait);
            return false;
        }
        return true;
    }

    private void startLoginService() {
        if (Constant.isOnline(getApplicationContext())) {
            if (sharedPrefsHelper.hasQbUser()) {
                QBUser qbUser = sharedPrefsHelper.getQbUser();
                Intent tempIntent = new Intent(getApplicationContext(), LoginService.class);
                PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
                LoginService.start(this, qbUser, pendingIntent);
            }
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult with resultCode: " + resultCode + " requestCode: " + requestCode);
        if (resultCode == RESULT_OK) {

           /* if (requestCode == 598) {

                Uri uri = data.getData();
                wallpaper_et.setImageURI(null);
                wallpaper_et.setBackground(null);
                wallpaper_et.setImageURI(uri);
                THEAME_VIEW.setVisibility(View.GONE);
                OTHER_SHOW_View.setVisibility(View.GONE);
            }*/
            if (requestCode == REQUEST_CODE_SELECT_PEOPLE) {
                show_dialog();
                final ArrayList<QBUser> selectedUsers = (ArrayList<QBUser>) data.getSerializableExtra(
                        SelectUsersActivity.EXTRA_QB_USERS);
                List<Integer> existingOccupantsIds = qbChatDialog.getOccupants();
                final List<Integer> newUsersIds = new ArrayList<>();
                for (QBUser user : selectedUsers) {
                    if (!existingOccupantsIds.contains(user.getId())) {
                        newUsersIds.add(user.getId());
                    }
                }
                ChatHelper.getInstance().getDialogById(qbChatDialog.getDialogId(), new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        hide_dialog();
                        dialogsManager.sendMessageAddedUsers(qbChatDialog, newUsersIds);
                        dialogsManager.sendSystemMessageAddedUser(systemMessagesManager, qbChatDialog, newUsersIds);
                        ChatActivity.this.qbChatDialog = qbChatDialog;
                        updateDialog(selectedUsers);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        hide_dialog();
                        showErrorSnackbar(R.string.update_dialog_error, e, null);
                    }
                });
            }
            if (requestCode == SPEECH_REQUEST_CODE) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                messageEditText.append(spokenText);
            }
            if (requestCode == 1212) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                text.append(spokenText);
            }
            if (requestCode == DOODLE_SEND) {
                String filePath = data.getStringExtra(ChatConstants.EXTRA_DOODLE_IMAGE);
                Log.e("File", "filePath: " + filePath);
                try {
                    File file = new File(new URI("file://" + filePath.replace(" ", "%20")));
                    attachmentPreviewAdapter.add(file);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                //Send File
                onSendChatClick(layout_chat_container);

            }
            if (requestCode == PICKFILE_RESULT_CODE) {
                Bitmap bitmap = null;
                Uri selectedImage = data.getData();
                String pathnme = null;
                try {
                    pathnme = getFilePath(ChatActivity.this, selectedImage);
                } catch (URISyntaxException e) {

                }
                try {
                    hit_log_upload(pathnme);
                } catch (Exception e) {

                }
            }
            String imgPath;
            if (requestCode == 895) {
                try {
                    Uri selectedImage = data.getData();
                    bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    hit_bothsidecustomtheam(SaveImagetoSDcard(String.valueOf(System.currentTimeMillis()), bitmap, this));
                    wallpaper_et.setImageURI(null);
                    wallpaper_et.setBackground(null);
                    wallpaper_et.setImageBitmap(bitmap);
                    THEAME_VIEW.setVisibility(View.GONE);
                    OTHER_SHOW_View.setVisibility(View.GONE);
                    dialogsManager.sendMessageTheamSet(qbChatDialog, "Custom Theme Set Successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 598) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    Log.e("Activity", "Pick from Gallery::>>> ");
                    hit_bothsidecustomtheam(SaveImagetoSDcard(String.valueOf(System.currentTimeMillis()), bitmap, this));
                    imgPath = getRealPathFromURI(selectedImage);
                    wallpaper_et.setImageURI(null);
                    wallpaper_et.setBackground(null);
                    wallpaper_et.setImageBitmap(bitmap);
                    THEAME_VIEW.setVisibility(View.GONE);
                    OTHER_SHOW_View.setVisibility(View.GONE);
                    dialogsManager.sendMessageTheamSet(qbChatDialog, "Custom Theme Set Successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void hit_log_upload(String Path_Name) {
        showProgressDialog(R.string.dlg_loading);
        String url = Upload_File;
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        try {
            if (Path_Name != null) {
                mResponse.setFile("store_chat_file", Path_Name);
            }
        } catch (Exception e) {
        }
        mResponse.getResponse(Request.Method.POST, url, 370, this, parms, false, false, Params_Object);
    }

    private void hit_bothsidecustomtheam(String Path_Name) {
        showProgressDialog(R.string.dlg_loading);
        String url = Upload_File;
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        try {
            if (Path_Name != null) {
                mResponse.setFile("store_chat_file", Path_Name);
            }
        } catch (Exception e) {
        }
        mResponse.getResponse(Request.Method.POST, url, 243, this, parms, false, false, Params_Object);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SystemPermissionHelper.PERMISSIONS_FOR_SAVE_FILE_IMAGE_REQUEST && grantResults[0] != -1) {
            openImagePicker();
        }
    }

    @Override
    public void onImagePicked(int requestCode, File file) {
        switch (requestCode) {
            case REQUEST_CODE_ATTACHMENT:
                attachmentPreviewAdapter.add(file);
                break;
        }
    }

    @Override
    public void onImagePickError(int requestCode, Exception e) {
        showErrorSnackbar(0, e, null);
    }

    @Override
    public void onImagePickClosed(int ignored) {
    }

    public void onSendChatClick(View view) {

        int totalAttachmentsCount = attachmentPreviewAdapter.getCount();
        Collection<QBAttachment> uploadedAttachments = attachmentPreviewAdapter.getUploadedAttachments();
        if (!uploadedAttachments.isEmpty()) {
            if (uploadedAttachments.size() == totalAttachmentsCount) {
                for (QBAttachment attachment : uploadedAttachments) {
                    sendChatMessage(null, attachment);
                }
            } else {
                ToastUtils.shortToast(R.string.chat_wait_for_attachments_to_upload);
            }
        }
        String text = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            sendChatMessage(text, null);
        }
    }

    public void onAttachmentsClick(View view) {
        chatBoxSubMenu.setVisibility(View.GONE);
        if (attachmentPreviewAdapter.getCount() >= MAX_UPLOAD_FILES) {
            ToastUtils.shortToast(R.string.restriction_upload_files + MAX_UPLOAD_FILES);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        SystemPermissionHelper permissionHelper = new SystemPermissionHelper(this);
        if (permissionHelper.isSaveImagePermissionGranted()) {
            new ImagePickHelper().pickAnImage(this, REQUEST_CODE_ATTACHMENT);
        } else {
            permissionHelper.requestPermissionsForSaveFileImage();
        }
    }

    public void showMessage(QBChatMessage message) {
        if (isAdapterConnected()) {
            chatAdapter.add(message);
            scrollMessageListDown();
        } else {
            delayShowMessage(message);
        }
    }

    private boolean isAdapterConnected() {
        return checkAdapterInit;
    }

    private void delayShowMessage(QBChatMessage message) {
        if (unShownMessages == null) {
            unShownMessages = new ArrayList<>();
        }
        unShownMessages.add(message);
    }

    private void initViews() {

        messageEditText = findViewById(R.id.edit_chat_message);
        progressBar = findViewById(R.id.progress_chat);
        attachmentPreviewContainerLayout = findViewById(R.id.layout_attachment_preview_container);
        attachmentPreviewAdapter = new AttachmentPreviewAdapter(this,
                new AttachmentPreviewAdapter.AttachmentCountChangedListener() {
                    @Override
                    public void onAttachmentCountChanged(int count) {
                        attachmentPreviewContainerLayout.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                    }
                },
                new AttachmentPreviewAdapter.AttachmentUploadErrorListener() {
                    @Override
                    public void onAttachmentUploadError(QBResponseException e) {
                        showErrorSnackbar(0, e, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onAttachmentsClick(v);
                            }
                        });
                    }
                });
        AttachmentPreviewAdapterView previewAdapterView = findViewById(R.id.adapter_view_attachment_preview);
        previewAdapterView.setAdapter(attachmentPreviewAdapter);
    }

    private void initMessagesRecyclerView() {
        chatMessagesRecyclerView = findViewById(R.id.list_chat_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatMessagesRecyclerView.setLayoutManager(layoutManager);
        messagesList = new ArrayList<>();
        if (!Constant.isOnline(this)) {
            try {
                messagesList = (ArrayList<QBChatMessage>) getIntent().getSerializableExtra("AllChat");
                qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra(EXTRA_DIALOG_ID);
                USER_NAME = getIntent().getStringExtra("USER_NAME");
                ActionBar ab = getSupportActionBar();
                ab.setTitle(USER_NAME);
                setActionbarSubTitle("Offline");
            } catch (Exception e) {
            }
        }

        chatAdapter = new ChatAdapter(this, qbChatDialog, messagesList);
        chatAdapter.setPaginationHistoryListener(new PaginationListener());
        chatMessagesRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration(chatAdapter));
        chatMessagesRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        imageAttachClickListener = new ImageAttachClickListener();
    }

    private void sendChatMessage(final String textt, final QBAttachment attachment) {

        String text = Constant.base64Controller(textt,true);


        if (ChatHelper.getInstance().isLogged()) {
            QBChatMessage chatMessage = new QBChatMessage();
            if (attachment != null) {
                chatMessage.addAttachment(attachment);
            } else {
                chatMessage.setBody(text);
                if (CHAT_DIALOG_TYPE.equalsIgnoreCase("0")) {
                    if (SharedPrefsHelper.getInstance().getQuotes() != null) {
                        if (!SharedPrefsHelper.getInstance().getQuotes().trim().equalsIgnoreCase("")) {
                            chatMessage.setBody(SharedPrefsHelper.getInstance().getQuotes() + "\n" + text.trim());
                            SharedPrefsHelper.getInstance().setQuotes("");
                            QouteChatView.setVisibility(View.GONE);
                        } else {
                            chatMessage.setBody(text);
                        }
                    } else {
                        chatMessage.setBody(text);
                    }
                } else {
                    if (SharedPrefsHelper.getInstance().getQuotes() != null) {
                        if (!SharedPrefsHelper.getInstance().getQuotes().trim().equalsIgnoreCase("")) {

                            chatMessage.setBody(Constant.base64Controller(SharedPrefsHelper.getInstance().getQuotes() + text + "\n" + "@SERECT569821545", true));
                        } else {
                            chatMessage.setBody(Constant.base64Controller(textt + "\n" + "@SERECT569821545", true));
                        }
                    } else {
                        chatMessage.setBody(Constant.base64Controller(textt + "\n" + "@SERECT569821545", true));
                    }

                }

            }
            chatMessage.setSaveToHistory(true);
            chatMessage.setDateSent(System.currentTimeMillis() / 1000);
            chatMessage.setMarkable(true);
            if (!QBDialogType.PRIVATE.equals(qbChatDialog.getType()) && !qbChatDialog.isJoined()) {
                //  ToastUtils.shortToast(R.string.chat_still_joining);
                return;
            }
            try {
                Log.d(TAG, "Sending Message with ID: " + chatMessage.getId());
                System.out.println("Sending Message with ID: " + chatMessage.getId());
                qbChatDialog.sendMessage(chatMessage);
                if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                    showMessage(chatMessage);
                }
                if (attachment != null) {
                    attachmentPreviewAdapter.remove(attachment);
                } else {
                    messageEditText.setText(null);
                }
            } catch (SmackException.NotConnectedException e) {
                Log.w(TAG, e);
                ToastUtils.shortToast(R.string.chat_error_send_message);
            }
        } else {
            showProgressDialog(R.string.dlg_login);
            Log.d(TAG, "Relogin to Chat");
            ChatHelper.getInstance().loginToChat(ChatHelper.getCurrentUser(), new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    Log.d(TAG, "Relogin Successfull");
                    sendChatMessage(text, attachment);
                    hideProgressDialog();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d(TAG, "Relogin Error: " + e.getMessage());
                    hideProgressDialog();
                    // ToastUtils.shortToast(R.string.chat_error_send_message);
                }
            });
        }
    }

    private void initChat() {
        switch (qbChatDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:
                joinGroupChat();
                break;
            case PRIVATE:
                loadDialogUsers();
                break;
            default:
                if (Constant.isOnline(this)) {
                    finish();
                }
                break;
        }
    }

    public void joinGroupChat() {

        if (Constant.isOnline(this)) {
            show_dialog();
            ChatHelper.getInstance().join(qbChatDialog, new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void result, Bundle b) {
                    Log.d(TAG, "Joined to Dialog Successful");
                    notifyUsersAboutCreatingDialog();
                    hideProgressDialog();
                    loadDialogUsers();
                    hide_dialog();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d(TAG, "Joining Dialog Error:" + e.getMessage());
                    hide_dialog();
                    hideProgressDialog();
                    showErrorSnackbar(R.string.connection_error, e, null);
                }
            });
        } else {
            hide_dialog();
            hideProgressDialog();
            loadDialogUsers();
        }
    }

    private void notifyUsersAboutCreatingDialog() {
        if (getIntent().getBooleanExtra(EXTRA_IS_NEW_DIALOG, false)) {
            dialogsManager.sendMessageCreatedDialog(qbChatDialog);
            getIntent().removeExtra(EXTRA_IS_NEW_DIALOG);
        }
    }

    private void updateDialog(final ArrayList<QBUser> selectedUsers) {
        ChatHelper.getInstance().updateDialogUsers(qbChatDialog, selectedUsers,
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle args) {
                        qbChatDialog = dialog;
                        loadDialogUsers();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        showErrorSnackbar(R.string.chat_info_add_people_error, e,
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updateDialog(selectedUsers);
                                    }
                                });
                    }
                }
        );
    }

    private void loadDialogUsers() {
        if (Constant.isOnline(this)) {
            ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
                @Override
                public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
                    setChatNameToActionBar();
                    loadChatHistory();
                }

                @Override
                public void onError(QBResponseException e) {
                    hide_dialog();
                    showErrorSnackbar(R.string.chat_load_users_error, e,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadDialogUsers();
                                }
                            });
                }
            });
        } else {
            loadChatHistory();
        }
    }

    private void setChatNameToActionBar() {
        String chatName = QbDialogUtils.getDialogName(qbChatDialog);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(chatName);
            SharedPrefsHelper.getInstance().set_Dialog_Name(chatName);
        }
    }

    public void loadChatHistory() {
        if (Constant.isOnline(this)) {
            show_dialog();
            ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                    messages.size();
                    serectChatID.clear();
                    Collections.reverse(Chat_Filter(messages));
                    if (!checkAdapterInit) {
                        checkAdapterInit = true;
                        chatAdapter.addList(messages);
                        addDelayedMessagesToAdapter();
                    } else {
                        chatAdapter.addToList(messages);
                    }
                    if (skipPagination == 0) {
                        scrollMessageListDown();
                    }
                    skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
                    hide_dialog();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d(TAG, "Loading Dialog History Error: " + e.getMessage());
                    hide_dialog();
                    showErrorSnackbar(R.string.connection_error, e, null);
                }
            });
        } else {
            if (!checkAdapterInit) {
                checkAdapterInit = true;
                chatAdapter.addList(messagesList);
                addDelayedMessagesToAdapter();
            } else {
                chatAdapter.addToList(messagesList);
            }
            if (skipPagination == 0) {
                scrollMessageListDown();
            }
            skipPagination += ChatHelper.CHAT_HISTORY_ITEMS_PER_PAGE;
        }

    }

    private List<QBChatMessage> Chat_Filter(ArrayList<QBChatMessage> messagedds) {
        ListIterator<QBChatMessage> iterator = messagedds.listIterator();
        if (CHAT_DIALOG_TYPE.equalsIgnoreCase("0")) {
            while (iterator.hasNext()) {
                QBChatMessage qbChatMessage = iterator.next();
                if (Constant.base64Controller(qbChatMessage.getBody(), false).contains("@SERECT569821545")) {
                    iterator.remove();
                }
            }
        } else {
            while (iterator.hasNext()) {
                QBChatMessage qbChatMessage = iterator.next();
                if (!Constant.base64Controller(qbChatMessage.getBody(), false).contains("@SERECT569821545")) {
                    iterator.remove();
                }
            }
        }

        for (int i = 0; i < messagedds.size(); i++) {
            if (Constant.base64Controller(messagedds.get(i).getBody(), false).contains("@SERECT569821545")) {
              String sms =   Constant.base64Controller(Constant.base64Controller(messagedds.get(i).getBody(), false).replaceAll("@SERECT569821545", ""), true);
                messagedds.get(i).setBody(sms);
                SerectMessageModel serectMessageModel = new SerectMessageModel();
                serectMessageModel.setDeliveredIds(messagedds.get(i).getDeliveredIds());
                serectMessageModel.setMessageID(messagedds.get(i).getId());
                serectChatID.add(serectMessageModel);
            }
        }
        return messagedds;
    }

    private void addDelayedMessagesToAdapter() {
        if (unShownMessages != null && !unShownMessages.isEmpty()) {
            List<QBChatMessage> chatList = chatAdapter.getList();
            for (QBChatMessage message : unShownMessages) {
                if (!chatList.contains(message)) {
                    chatAdapter.add(message);
                }
            }
        }
    }

    private void scrollMessageListDown() {
        chatMessagesRecyclerView.scrollToPosition(messagesList.size() - 1);
    }

    private void deleteChat() {
        ChatHelper.getInstance().deleteDialog(qbChatDialog, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                showErrorSnackbar(R.string.dialogs_deletion_error, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteChat();
                            }
                        });
            }
        });
    }

    private void initChatConnectionListener() {
        View rootView = findViewById(R.id.list_chat_messages);
        chatConnectionListener = new VerboseQbChatConnectionListener(rootView) {
            @Override
            public void reconnectionSuccessful() {
                super.reconnectionSuccessful();
                Log.d(TAG, "Reconnection Successful");
                skipPagination = 0;
                switch (qbChatDialog.getType()) {
                    case GROUP:
                        checkAdapterInit = false;
                        // Join active room if we're in Group Chat
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                joinGroupChat();
                            }
                        });
                        break;
                }
            }
        };
    }

    @Override
    public void processMessageDelivered(String messageID, String dialogID, Integer userID) {
        if (qbChatDialog.getDialogId().equals(dialogID)) {
            chatAdapter.updateStatusDelivered(messageID, userID);
        }
    }

    @Override
    public void processMessageRead(String messageID, String dialogID, Integer userID) {
        if (qbChatDialog.getDialogId().equals(dialogID)) {
            chatAdapter.updateStatusRead(messageID, userID);
        }
    }

    @Override
    public void onDialogCreated(QBChatDialog chatDialog) {
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
    }

    @Override
    public void onNewDialogLoaded(QBChatDialog chatDialog) {
    }

    @Override
    public void onclick() {

    }

    @Override
    public void set_walpaper(String Photo_Name) {
        wallpaper_et.setImageURI(null);
        wallpaper_et.setBackground(null);
        try {
            Picasso.get().load(Photo_Name).placeholder(R.drawable.theme_2).into(wallpaper_et);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bothSideTheamUpdate(Photo_Name);
        THEAME_VIEW.setVisibility(View.GONE);
        OTHER_SHOW_View.setVisibility(View.GONE);
        ToastUtils.longToast(R.string.Theme_Set_Successfully);
    }

    private void bothSideTheamUpdate(String ImageNAME) {
        show_dialog();
        qbChatDialog.setPhoto(ImageNAME);
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        QBRestChatService.updateChatDialog(qbChatDialog, requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog updatedDialog, Bundle bundle) {
                qbChatDialog = updatedDialog;
                hide_dialog();
                ToastUtils.longToast(getString(R.string.Both_Side));
                try {
                    Constant.showErrorAlert(getApplicationContext(), getString(R.string.Both_Side));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Constant.showErrorAlert(getApplicationContext(), e.getMessage());
                hide_dialog();
            }
        });
    }

    @Override
    public void Send_Contact(String Name, String Contact) {
        String Contactt = "Contact person name: " + Name + "\n" + "Contact person number: " + Contact;
        sendChatMessage(Contactt, null);
        if (CONTACT_VIEW.getVisibility() == View.VISIBLE) {
            CONTACT_VIEW.setVisibility(View.GONE);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                break;
            case 1:
                SelectLang_Code = "en";
                break;
            case 2:
                SelectLang_Code = "es";
                break;
            case 3:
                SelectLang_Code = "hi";
                break;
            case 4:
                SelectLang_Code = "th";
                break;
            case 5:
                SelectLang_Code = "bn";
                break;
            case 6:
                SelectLang_Code = "te";
                break;
            case 7:
                SelectLang_Code = "mr";
                break;
            case 8:
                SelectLang_Code = "fr";
                break;
            case 9:
                SelectLang_Code = "zh";
                break;
            case 10:
                SelectLang_Code = "ko";
                break;
            case 11:
                SelectLang_Code = "ar";
                break;
            case 12:
                SelectLang_Code = "ja";
                break;
            case 13:
                SelectLang_Code = "ms";
                break;
            case 14:
                SelectLang_Code = "pt";
                break;
            case 15:
                SelectLang_Code = "vi";
                break;
            case 16:
                SelectLang_Code = "tl";
                break;
            case 17:
                SelectLang_Code = "it";
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public ArrayList<Contact_Model> getContactsForm_Phone(Context ctx) {
        ArrayList<Contact_Model> list = new ArrayList<>();
        ContentResolver cr = ctx.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    Contact_Model info = new Contact_Model();
                    info.Contact_Name = cursor.getString(nameIndex);
                    info.Contact_Number = cursor.getString(numberIndex);
                    info.QB_user_id = "   ";
                    list.add(info);
                }
            } finally {
                cursor.close();
            }
        }


        return list;
    }

    @OnClick({R.id.Do_voice_call, R.id.Do_Video_Call, R.id.Call_View})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.Do_voice_call:
                if (Constant.isOnline(getApplicationContext())) {
                    if (checkIsLoggedInChat()) {
                        startCall(false);
                    }
                    if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                        startPermissionsActivity(true);
                    }
                }

                break;
            case R.id.Do_Video_Call:
                if (Constant.isOnline(getApplicationContext())) {
                    if (checkIsLoggedInChat()) {
                        startCall(true);
                    }
                    if (checker.lacksPermissions(Consts.PERMISSIONS[1])) {
                        startPermissionsActivity(true);
                    }
                }

                break;

        }
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {
        hideProgressDialog();
        ToastUtils.shortToast(error.getMessage());
    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {
        if (requestCode == 370) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    messageEditText.setText(response.getString("data").toString(), null);
                } else {
                    ToastUtils.shortToast("Attachment Send Failure");
                    if (Translation_View.getVisibility() == View.VISIBLE) {
                        Translation_View.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                hideProgressDialog();
            }
        }

        if (requestCode == 243) {
            hideProgressDialog();
            try {
                if (response.getString("status").equalsIgnoreCase("success")) {
                    bothSideTheamUpdate(response.getString("data").toString());
                } else {
                    ToastUtils.shortToast("Theam Upload Failed");
                }
            } catch (Exception e) {
                ToastUtils.shortToast("Theam Upload Failed");
            }
        }
    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {
        if (requestCode == 691) {
            hideProgressDialog();
            messageEditText.setText(response);
            ToastUtils.shortToast("Translation Done");
        }
        if (requestCode == 882) {
            hideProgressDialog();
            messagesList.get(adapter_position).setBody(Constant.base64Controller(response, true));
            if (chatAdapter != null) {
                if (!response.equalsIgnoreCase("")) {
                    chatAdapter.notifyDataSetChanged();
                }
            }
            ToastUtils.shortToast("Translation Done");
        }

    }

    @Override
    public void Language_Translator(String Language, String Terget_Language_Code, String pos) {
        adapter_position = Integer.parseInt(pos);
        translateSend(Language, Terget_Language_Code, "");
    }

    private String getlastseen() {
        String lastseen = "";
        String appendstring = "";
        try {
            long lastUserActivity = QBChatService.getInstance().getLastUserActivity(qbChatDialog.getRecipientId()); //returns last activity in seconds or error

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -(int) lastUserActivity);

            String format = "dd MMM yyyy hh:mm a";
            if (DateUtils.isToday(calendar.getTimeInMillis())) {
                format = "hh:mm a";
                appendstring = "Today" + ",";
            }/* else if (isyesterday(calendar.getTimeInMillis())) {
                format = "hh:mm a";
                appendstring = "Yesterday" + ",";
            }*/ else if (Calendar.YEAR == Calendar.getInstance().YEAR)
                format = "dd MMM hh:mm aa";

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

            lastseen = simpleDateFormat.format(calendar.getTime());

        } catch (XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appendstring + lastseen;
    }

    void show_dialog() {
        Shimmer_Effect.startShimmerAnimation();
        Shimmer_Effect.setVisibility(View.VISIBLE);
    }

    void hide_dialog() {
        Shimmer_Effect.stopShimmerAnimation();
        Shimmer_Effect.setVisibility(View.GONE);

    }

    @Override
    public void openQuoteChat(String txt) {
        try {
            JSONObject jsonObject = new JSONObject(txt);
            QouteChatName.setText(jsonObject.getString("name"));
            QouteChatView.setVisibility(View.VISIBLE);
            QouteChatTV.setText(jsonObject.getString("sms"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ChatMessageListener extends QbChatDialogMessageListenerImp {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            Log.d(TAG, "Processing Received Message: " + qbChatMessage.getBody());
            showMessage(qbChatMessage);
        }
    }

    private class SystemMessagesListener implements QBSystemMessageListener {
        @Override
        public void processMessage(final QBChatMessage qbChatMessage) {
            Log.d(TAG, "System Message Received: " + qbChatMessage.getId());
            dialogsManager.onSystemMessageReceived(qbChatMessage);
        }

        @Override
        public void processError(QBChatException e, QBChatMessage qbChatMessage) {
            Log.d(TAG, "System Messages Error: " + e.getMessage() + "With MessageID: " + qbChatMessage.getId());
        }
    }

    private class ImageAttachClickListener implements AttachClickListener {

        @Override
        public void onLinkClicked(QBAttachment qbAttachment, int position) {
            if (qbAttachment != null) {
                String url = QBFile.getPrivateUrlForUID(qbAttachment.getId());
                AttachmentImageActivity.start(ChatActivity.this, url);
            }
        }
    }

    private class PaginationListener implements PaginationHistoryListener {

        @Override
        public void downloadMore() {
            Log.w(TAG, "Download More");
            loadChatHistory();
        }
    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            Local_Contact = getContactsForm_Phone(ChatActivity.this);

            return "this string is passed to onPostExecute";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Contact_send_adapter contact_send_adapter = new Contact_send_adapter(ChatActivity.this, Local_Contact);
            contact_recyler.setAdapter(contact_send_adapter);
        }
    }

    class TestAsync extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(Void... arg0) {

            if (Constant.isOnline(getApplicationContext())) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                skipPagination = 0;
                                checkAdapterInit = false;
                                ChatHelper.getInstance().loadChatHistory(qbChatDialog, skipPagination, new QBEntityCallback<ArrayList<QBChatMessage>>() {
                                    @Override
                                    public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                                        messages.size();
                                        serectChatID.clear();
                                        Collections.reverse(Chat_Filter(messages));
                                        chatAdapter.addList(messages);
                                        hide_dialog();
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {
                                        hide_dialog();
                                        showErrorSnackbar(R.string.connection_error, e, null);
                                    }
                                });
                            }
                        }
                );

            } else {
                chatAdapter.addList(messagesList);
            }


            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG + " onPostExecute", "" + result);
        }

    }

}