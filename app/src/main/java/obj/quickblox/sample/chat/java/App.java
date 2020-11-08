package obj.quickblox.sample.chat.java;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.devs.acr.AutoErrorReporter;
import com.quickblox.auth.session.QBSettings;
import com.stripe.android.PaymentConfiguration;
import com.zxy.recovery.callback.RecoveryCallback;
import com.zxy.recovery.core.Recovery;

import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.managers.BackgroundListener;
import obj.quickblox.sample.chat.java.ui.activity.DashBoard;
import obj.quickblox.sample.chat.java.ui.activity.TestActivity;
import obj.quickblox.sample.chat.java.util.QBResRequestExecutor;
import obj.quickblox.sample.chat.java.utils.ActivityLifecycle;
import obj.quickblox.sample.chat.java.utils.Constant;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDex;

public class App extends Application {

    //Chat settings

    public static final int CHAT_PORT = 5223;
    public static final int SOCKET_TIMEOUT = 300;
    public static final boolean KEEP_ALIVE = true;
    public static final boolean USE_TLS = true;
    public static final boolean AUTO_JOIN = false;
    public static final boolean AUTO_MARK_DELIVERED = true;
    public static final boolean RECONNECTION_ALLOWED = true;
    public static final boolean ALLOW_LISTEN_NETWORK = true;

    //App credentials
    static final String ACCOUNT_KEY = "3WWWzquyso67WCFtmehc";
    static final String APPLICATION_ID = "69917";
    static final String AUTH_KEY = "6KNKWeLCTJGEprK";
    static final String AUTH_SECRET = "ZBCTNWbLehYWXkO";
    public static final String USER_DEFAULT_PASSWORD = "12345678";

    //Chat settings range
    private static final int MAX_PORT_VALUE = 65535;
    private static final int MIN_PORT_VALUE = 1000;
    private static final int MIN_SOCKET_TIMEOUT = 300;
    private static final int MAX_SOCKET_TIMEOUT = 60000;
    private QBResRequestExecutor qbResRequestExecutor;
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
        ActivityLifecycle.init(this);
        checkAppCredentials();
        checkChatSettings();
        initCredentials();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new BackgroundListener());

        //Stripe - Configaration
        PaymentConfiguration.init(getApplicationContext(), AppConstants.Stripe_Publishable_key);


       Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable e) {
                        handleUncaughtException (thread,e);
                    }});

        AutoErrorReporter.get(this)
                .setEmailAddresses("otptudime@gmail.com")
                .setEmailSubject("Crash Report:-->  "+ Constant.Get_back_date(Constant.GET_timeStamp()))
                .start();

        Recovery.getInstance()
                .debug(true)
                .recoverInBackground(false)
                .recoverStack(true)
                .mainPage(DashBoard.class)
                .recoverEnabled(true)
                .callback(new MyCrashCallback())
                .silent(true, Recovery.SilentMode.RECOVER_ACTIVITY_STACK)
                .skip(TestActivity.class)
                .init(this);
    }



    static final class MyCrashCallback implements RecoveryCallback {
        @Override
        public void stackTrace(String exceptionMessage) {
            Log.e("zxy", "exceptionMessage:" + exceptionMessage);
        }

        @Override
        public void cause(String cause) {
            Log.e("zxy", "cause:" + cause);
        }

        @Override
        public void exception(String exceptionType, String throwClassName, String throwMethodName, int throwLineNumber) {
            Log.e("zxy", "exceptionClassName:" + exceptionType);
            Log.e("zxy", "throwClassName:" + throwClassName);
            Log.e("zxy", "throwMethodName:" + throwMethodName);
            Log.e("zxy", "throwLineNumber:" + throwLineNumber);
        }

        @Override
        public void throwable(Throwable throwable) {

        }
    }


    private void initApplication()
    {
        instance = this;
    }

    private void handleUncaughtException (Thread thread, Throwable e) {
        Intent intent = new Intent (getApplicationContext(), DashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void checkAppCredentials() {
        if (APPLICATION_ID.isEmpty() || AUTH_KEY.isEmpty() || AUTH_SECRET.isEmpty() || ACCOUNT_KEY.isEmpty()) {
            throw new AssertionError(getString(R.string.error_credentials_empty));
        }
    }

    private void checkChatSettings() {
        if (USER_DEFAULT_PASSWORD.isEmpty() || (CHAT_PORT < MIN_PORT_VALUE || CHAT_PORT > MAX_PORT_VALUE)
                || (SOCKET_TIMEOUT < MIN_SOCKET_TIMEOUT || SOCKET_TIMEOUT > MAX_SOCKET_TIMEOUT)) {
            throw new AssertionError(getString(R.string.error_credentials_empty));
        }
    }

    private void initCredentials() {
        QBSettings.getInstance().init(getApplicationContext(), APPLICATION_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

    public static App getInstance() {
        return instance;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}