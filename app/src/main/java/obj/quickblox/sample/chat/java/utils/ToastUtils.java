package obj.quickblox.sample.chat.java.utils;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.google.android.material.snackbar.Snackbar;

import obj.quickblox.sample.chat.java.App;

public class ToastUtils {

    private ToastUtils() {
        //empty
    }

    public static void shortToast(String message) {
        show(message, Toast.LENGTH_LONG);
    }

    public static void shortToast(@StringRes int resource) {
        show(App.getInstance().getString(resource), Toast.LENGTH_SHORT);
    }

    public static void longToast(String message) {
        show(message, Toast.LENGTH_LONG);
    }

    public static void longToast(@StringRes int resource) {
        show(App.getInstance().getString(resource), Toast.LENGTH_LONG);
    }

    private static void show(String message, int length) {
        Toast.makeText(App.getInstance(), message, length).show();
    }
    public static void showSnackBar(View view, String pMessage) {
        Snackbar.make(view, pMessage, Snackbar.LENGTH_SHORT).show();
    }
}