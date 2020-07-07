package obj.quickblox.sample.chat.java.ui.fragments;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.activity.BaseActivity;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.chat.ChatHelper;

public class BaseFragment extends Fragment  {

    private String					frgamentName;
    public BaseActivity mActivity;
    public int						mPosition	= 0;
    private float					density;
    public ProgressDialog progressDialog;

    public BaseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        density = mActivity.getResources().getDisplayMetrics().density;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        String Load=getString(R.string.load);
        progressDialog.setMessage(Load);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // hideKeyboard();
    }

    public float getDensity() {
        return density;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;
    }

    public void setHeaderName(String headerName) {
        try {
            // ((MainActivity) mActivity).setHeaderName(headerName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void cancleDialog() {
        progressDialog.dismiss();
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

//	public void replaceChildFragment(Fragment fragment, Bundle bundle) {
//		replaceChildFragment(fragment, bundle, false);
//	}

//	public void replaceChildFragment(Fragment fragment, Bundle bundle, boolean isAddToBackStatck) {
//		if (bundle != null) {
//			fragment.setArguments(bundle);
//		}
//		String tag = fragment.getClass().getSimpleName();
//		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
//		Fragment fragmentLocal = getChildFragmentManager().findFragmentById(R.id.container);
//		if (fragmentLocal != null && fragmentLocal.getTag().equalsIgnoreCase(tag)) {
//			((BaseFragment) fragmentLocal).refreshFragment(bundle);
//			return;
//		}
//		ft.replace(R.id.container, fragment, tag);
//		if (isAddToBackStatck) {
//			ft.addToBackStack(tag);
//		}
//
//		// fragment.setRetainInstance(true);
//		try {
//			ft.commit();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			ft.commitAllowingStateLoss();
//		}
//	}

    public void finishFragment() {
        getChildFragmentManager().popBackStack();
    }

    protected void showKeyBoard(EditText etView) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void setFragmentName(String frgamentName) {
        this.frgamentName = frgamentName;
    }

    public String getFragmentName() {
        return frgamentName;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    /**
     * Override this method when refresh page is required in your fragment
     *
     * @param bundle
     */
    public void refreshFragment(Bundle bundle) {

    }

    public void updateView(Object object) {

    }
    /*public void showErrorSnackbar(@StringRes int resId, Exception e, View.OnClickListener clickListener) {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null) {
            ErrorUtils.showSnackbar(rootView, resId, e,
                    R.string.dialog_retry, clickListener).show();
        }
    }*/


    public void restartApp(Context context) {
        // Application needs to restart when user declined some permissions at runtime
        Intent restartIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        PendingIntent intent = PendingIntent.getActivity(context, 0, restartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC, System.currentTimeMillis() + 200, intent);
        System.exit(0);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (SharedPrefsHelper.getInstance().hasQbUser() && !QBChatService.getInstance().isLoggedIn()) {
            ChatHelper.getInstance().loginToChat(SharedPrefsHelper.getInstance().getQbUser(), new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    onResumeFinished();
                }

                @Override
                public void onError(QBResponseException e) {
                    onResumeFinished();
                }
            });
        } else {
            onResumeFinished();
        }
    }
    public void onResumeFinished() {
        // Need to Override onResumeFinished() method in nested classes if we need to handle returning from background in Activity
    }
}
