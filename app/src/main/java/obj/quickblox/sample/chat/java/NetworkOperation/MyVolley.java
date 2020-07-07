package obj.quickblox.sample.chat.java.NetworkOperation;

/**
 * Created by KRISHNENDU MANNA on 28/6/19.
 */

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyVolley {
    private static RequestQueue mRequestQueue;
    private MyVolley() {
    }
    public static void init(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
    }
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

}