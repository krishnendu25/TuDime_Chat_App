package obj.quickblox.sample.chat.java.ui.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.activity.Ecards3D_Activity;

/**
 * Created by mahak on 7/13/2017.
 */

public class EcardsCategoriesPage extends Fragment implements View.OnClickListener {

    private View v;
    private Animation anim;
    private MediaPlayer mediaPlayer;
    public Bundle b;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.ecards_categories_page, container, false);

        v.findViewById(R.id.love_image).setOnClickListener(this);
        v.findViewById(R.id.birthday_image).setOnClickListener(this);
        v.findViewById(R.id.friendship_image).setOnClickListener(this);
        v.findViewById(R.id.some1_image).setOnClickListener(this);
        v.findViewById(R.id.missing_image).setOnClickListener(this);
        v.findViewById(R.id.congratuations_image).setOnClickListener(this);
        v.findViewById(R.id.good_luck_image).setOnClickListener(this);
        v.findViewById(R.id.get_well_soon_image).setOnClickListener(this);
        v.findViewById(R.id.holidays_image).setOnClickListener(this);
        v.findViewById(R.id.inviatation_image).setOnClickListener(this);
        v.findViewById(R.id.thanking_image).setOnClickListener(this);
        v.findViewById(R.id.sorry_image).setOnClickListener(this);

        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in_category_ecards);
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.beep26);
        b = new Bundle();
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.love_image:
                (v.findViewById(R.id.love_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("love", getString(R.string.love_cards));
                break;
            case R.id.birthday_image:
                (v.findViewById(R.id.birthday_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("birthday", getString(R.string.bday_cards));
                break;
            case R.id.friendship_image:
                (v.findViewById(R.id.friendship_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("friendship", getString(R.string.friendship_cards));
                break;
            case R.id.some1_image:
                (v.findViewById(R.id.some1_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("someonespecial", getString(R.string.some_special_cards));
                break;
            case R.id.missing_image:
                (v.findViewById(R.id.missing_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("missing", getString(R.string.miss_you_cards));
                break;
            case R.id.congratuations_image:
                (v.findViewById(R.id.congratuations_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("congrats", getString(R.string.congrax_cards));
                break;
            case R.id.good_luck_image:
                (v.findViewById(R.id.good_luck_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("goodluck", getString(R.string.good_luck_cards));
                break;
            case R.id.get_well_soon_image:
                (v.findViewById(R.id.get_well_soon_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("getwell", getString(R.string.get_well_soon_cards));
                break;
            case R.id.holidays_image:
                (v.findViewById(R.id.holidays_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("holiday", getString(R.string.holidays_cards));
                break;
            case R.id.inviatation_image:
                (v.findViewById(R.id.inviatation_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("inivitation", getString(R.string.invitation_cards));
                break;
            case R.id.thanking_image:
                (v.findViewById(R.id.thanking_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("thank_you", getString(R.string.thanku_cards));
                break;
            case R.id.sorry_image:
                (v.findViewById(R.id.sorry_image)).startAnimation(anim);
                mediaPlayer.start();
                setValueFragment("sorry", getString(R.string.sorry_cards));
                break;
            default:
                break;
        }
    }


    private void setValueFragment(String category, String title) {
        Missing_fragment frag = new Missing_fragment();
        b.putString("category", category);
        frag.setArguments(b);
        Ecards3D_Activity.backStack.add(title);
        callFragment(frag, title);
    }

    private void callFragment(final Fragment fragment, final String title) {
        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mediaPlayer.release();
                    ((Ecards3D_Activity) getActivity()).changeFragment(fragment, title);
                }
            }, 700);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
