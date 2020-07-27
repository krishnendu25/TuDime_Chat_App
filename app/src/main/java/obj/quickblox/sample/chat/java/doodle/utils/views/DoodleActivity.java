package obj.quickblox.sample.chat.java.doodle.utils.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;
import com.quickblox.core.request.QueryRule;

import org.jivesoftware.smack.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconHandler;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.constants.AppConstants;
import obj.quickblox.sample.chat.java.doodle.utils.adapter.EmojiGridAdapter;
import obj.quickblox.sample.chat.java.doodle.utils.doodleutils.photosortr.PhotoSortrView;
import obj.quickblox.sample.chat.java.doodle.utils.doodleutils.photosortr.UpdateDrawable;
import obj.quickblox.sample.chat.java.doodle.utils.doodleutils.photosortr.updateTabBarIcon;
import obj.quickblox.sample.chat.java.ui.Callback.ChatConstants;
import obj.quickblox.sample.chat.java.ui.Callback.OnButtonClicked;
import obj.quickblox.sample.chat.java.ui.activity.BaseActivity;
import obj.quickblox.sample.chat.java.ui.adapter.WallpaperGridAdapter;
import obj.quickblox.sample.chat.java.ui.adapter.listeners.SetclickCallback;
import obj.quickblox.sample.chat.java.util.BitmapUtils;
import obj.quickblox.sample.chat.java.util.FileUtils;
import obj.quickblox.sample.chat.java.util.ImageOnlyOptionsDialog;
import obj.quickblox.sample.chat.java.utils.DocumentUtils;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;

public class DoodleActivity extends BaseActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, updateTabBarIcon, UpdateDrawable, ColorPickerDialogFragment.ColorPickerDialogListener, View.OnClickListener, SetclickCallback {
    public static Context context;
    public static boolean isPencilDoodle = false;
    /* access modifiers changed from: private */
    public View emojiconsFrame;
    /* access modifiers changed from: private */
    public ImageView ivPencil;
    public DisplayMetrics metrics;
    /* access modifiers changed from: private */
    public TextView tvText;
    /* access modifiers changed from: private */
    public TextView tvUndoDoodleDraw;
    boolean editableImageItemDelted = false;
    TextView my_theme;
    private int countBackPress = 0;
    private float density;
    private int editClickCount = 0;
    private FrameLayout flEmoji;
    private ImageView imgBack;
    private PhotoSortrView.Img imgObject = null;
    private boolean isKeyboardOpened;
    private ImageView ivDelete;
    private ImageView ivSavePhoto;
    private ImageView ivUploadProfilePic;
    private LinearLayout llFooter;
    private Bitmap mImageBitmap;
    private boolean openTextInEditableMode = false;
    private int pencilClickCount = 0;
    private Drawable pencilDrawable;
    private Drawable pencilPressed;
    private File photo;
    private PhotoSortrView photoSorter;
    private TextView tvIconClick;
    private TextView tvOpenCamera;
    private TextView tvPicker;
    private LinearLayout THEAME_VIEW_DODO;
    private GridView grdWallpapers;
    private TextView txvDone, txvCancel;
    private EmojiGridAdapter emojiGridAdapter;

    private static String colorToHexString(int color) {
        return String.format("#%06X", new Object[]{Integer.valueOf(color & -1)});
    }

    public static boolean isEmpty(Bitmap bmp) {
        if (bmp != null) {
            int[] pixels = new int[(bmp.getWidth() * bmp.getHeight())];
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
            for (int i : pixels) {
                if (Color.alpha(i) != 0 && (Color.blue(i) != 255 || Color.red(i) != 255 || Color.green(i) != 255)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetLan(SharedPrefsHelper.getInstance().get_Language());
        setContentView((int) R.layout.activity_doodles);
        hideActionbar();
        this.THEAME_VIEW_DODO = (LinearLayout) findViewById(R.id.THEAME_VIEW_DODO);
        this.flEmoji = (FrameLayout) findViewById(R.id.flUploadedPic);
        this.tvOpenCamera = (TextView) findViewById(R.id.tvOpenCamera);
        this.ivUploadProfilePic = (ImageView) findViewById(R.id.ivUploadProfilePic);
        this.tvPicker = (TextView) findViewById(R.id.tvPicker);
        this.ivDelete = (ImageView) findViewById(R.id.ivDelete);
        this.ivPencil = (ImageView) findViewById(R.id.ivPencil);
        this.tvUndoDoodleDraw = (TextView) findViewById(R.id.tvUndoDoodleDraw);
        this.tvText = (TextView) findViewById(R.id.tvText);
        this.ivSavePhoto = (ImageView) findViewById(R.id.ivSavePhoto);
        this.metrics = getResources().getDisplayMetrics();
        this.emojiconsFrame = findViewById(R.id.emojicons);
        this.photoSorter = new PhotoSortrView(this);
        setEmojiconFragment(false);
        this.tvIconClick = (TextView) findViewById(R.id.tvIconClick);
        this.llFooter = (LinearLayout) findViewById(R.id.llFooter);
        this.imgBack = (ImageView) findViewById(R.id.imgBack);
        this.density = getResources().getDisplayMetrics().density;
        new EditTextViewActivity(this);
        this.tvOpenCamera.setOnClickListener(this);
        this.ivUploadProfilePic.setOnClickListener(this);
        this.tvPicker.setOnClickListener(this);
        this.ivDelete.setOnClickListener(this);
        this.ivPencil.setOnClickListener(this);
        this.tvUndoDoodleDraw.setOnClickListener(this);
        this.tvText.setOnClickListener(this);
        this.ivSavePhoto.setOnClickListener(this);
        this.imgBack.setOnClickListener(this);
        my_theme = findViewById(R.id.my_theme);
        my_theme.setVisibility(View.GONE);
        setSendIconVisibility();
        this.pencilDrawable = getResources().getDrawable(R.drawable.doodle_preview_color_picker_button);
        this.pencilPressed = getResources().getDrawable(R.drawable.doodle_preview_color_picker_button);
        grdWallpapers = (GridView) findViewById(R.id.grdWallpapers);
        txvDone = (TextView) findViewById(R.id.txvDone);
        txvDone.setVisibility(View.INVISIBLE);
        txvCancel = (TextView) findViewById(R.id.txvCancel);
        txvDone.setOnClickListener(this);
        txvCancel.setOnClickListener(this);
        emojiGridAdapter = new EmojiGridAdapter(this);
        grdWallpapers.setAdapter(emojiGridAdapter);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager().beginTransaction().replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault)).commit();
    }

    public void onEmojiconBackspaceClicked(View v) {
    }

    public void onEmojiconClicked(Emojicon emojicon) {
        this.photoSorter.clearCanvas();
        this.tvUndoDoodleDraw.setClickable(true);
        this.tvText.setClickable(true);
        this.llFooter.setVisibility(View.VISIBLE);
        addImageItem(emojicon);
        setSendIconVisibility();
    }

    private void addImageItem(Emojicon emojicon) {
        this.flEmoji.removeView(this.photoSorter);
        this.photoSorter = new PhotoSortrView(this);
        PhotoSortrView.Img img = new PhotoSortrView.Img(emojicon.hashCode()
                , getResources(), false);
        PhotoSortrView.DOODLE_IMAGE.add(img);
        this.flEmoji.addView(this.photoSorter);
        this.photoSorter.loadImageItem(img, this);
        this.emojiconsFrame.setVisibility(View.GONE);
        this.ivDelete.setVisibility(View.VISIBLE);
    }

    private void addImage(String emojicon) {
        this.tvUndoDoodleDraw.setClickable(true);
        int iconResId = getResources().getIdentifier(emojicon, "drawable", getPackageName());
        this.flEmoji.removeView(this.photoSorter);
        this.photoSorter = new PhotoSortrView(this);
        PhotoSortrView.Img img = new PhotoSortrView.Img(iconResId
                , getResources(), false);
        PhotoSortrView.DOODLE_IMAGE.add(img);
        this.flEmoji.addView(this.photoSorter);
        this.photoSorter.loadImageItem(img, this);
        this.emojiconsFrame.setVisibility(View.GONE);
        this.ivDelete.setVisibility(View.VISIBLE);
        THEAME_VIEW_DODO.setVisibility(View.GONE);
        this.ivSavePhoto.setVisibility(View.VISIBLE);
    }

    private void removeImageItem() {
        PhotoSortrView photoSortrView = this.photoSorter;
        if (!PhotoSortrView.DOODLE_IMAGE.isEmpty()) {
            PhotoSortrView photoSortrView2 = this.photoSorter;
            ArrayList<PhotoSortrView.Img> arrayList = PhotoSortrView.DOODLE_IMAGE;
            PhotoSortrView photoSortrView3 = this.photoSorter;
            if (((PhotoSortrView.Img) arrayList.get(PhotoSortrView.DOODLE_IMAGE.size() - 1)).getisEditiable()) {
                this.editableImageItemDelted = true;
            } else {
                this.editableImageItemDelted = false;
            }
            PhotoSortrView photoSortrView4 = this.photoSorter;
            PhotoSortrView photoSortrView5 = this.photoSorter;
            ArrayList<PhotoSortrView.Img> arrayList2 = PhotoSortrView.DOODLE_IMAGE;
            PhotoSortrView photoSortrView6 = this.photoSorter;
            photoSortrView4.unloadImageItem((PhotoSortrView.Img) arrayList2.get(PhotoSortrView.DOODLE_IMAGE.size() - 1), this);
            PhotoSortrView photoSortrView7 = this.photoSorter;
            ArrayList<PhotoSortrView.Img> arrayList3 = PhotoSortrView.DOODLE_IMAGE;
            PhotoSortrView photoSortrView8 = this.photoSorter;
            arrayList3.remove(PhotoSortrView.DOODLE_IMAGE.size() - 1);
            this.flEmoji.removeView(this.photoSorter);
            this.flEmoji.addView(this.photoSorter);
        }
        PhotoSortrView photoSortrView9 = this.photoSorter;
        if (PhotoSortrView.DOODLE_IMAGE.size() <= 0) {
            this.ivDelete.setVisibility(View.GONE);
        } else {
            animateView(this.ivDelete);
        }
        setSendIconVisibility();
    }

    public void closeFrame() {
        this.emojiconsFrame.setVisibility(View.GONE);
    }

    private void savePhoto() {
        try {
            String filePath = FileUtils.saveImage(this, viewToBitmap(this.flEmoji), "doodle_" + System.currentTimeMillis() + FileUtils.bitmapExtension);
            Intent intent = new Intent();
            intent.putExtra(ChatConstants.EXTRA_DOODLE_IMAGE, filePath);
            setResult(-1, intent);
            PhotoSortrView photoSortrView = this.photoSorter;
            PhotoSortrView.DOODLE_IMAGE.clear();
            this.photoSorter.clearCanvas();
            finish();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void startCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        this.photo = new File(Environment.getExternalStorageDirectory(), "image" + System.currentTimeMillis() + FileUtils.bitmapExtension);
        intent.putExtra(QueryRule.OUTPUT, Uri.fromFile(this.photo));
        startActivityForResult(intent, 1002);
    }

    public void pickFromGallery() {
        String fileType = "image/*";
        Intent photoPickerIntent = new Intent("android.intent.action.PICK");
        photoPickerIntent.setType(fileType);
        startActivityForResult(photoPickerIntent, 1003);
    }

    private void showPhotoOptionsDialog() {
        ImageOnlyOptionsDialog dialog = new ImageOnlyOptionsDialog();
        dialog.setonButtonClickListener(new OnButtonClicked() {
            public void onButtonCLick(int buttonId) {
                switch (buttonId) {
                    case R.id.btnGallery /*2131624663*/:
                        DoodleActivity.this.pickFromGallery();
                        return;
                    case R.id.btnCamera /*2131624673*/:
                        DoodleActivity.this.startCamera();
                        return;
                    default:
                        return;
                }
            }
        });
        dialog.show(getSupportFragmentManager(), ImageOnlyOptionsDialog.class.getSimpleName());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == 1002) {
                if (resultCode == -1) {
                    try {
                        Bitmap sourceBitmap = BitmapUtils.getScaledBitmap(this.photo, BitmapUtils.getMaxSize(this));
                        int orientation = new ExifInterface(this.photo.getPath()).getAttributeInt("Orientation", 1);
                        Log.e("Capture image", "oreination" + orientation);
                        Matrix matrix = new Matrix();
                        switch (orientation) {
                            case 3:
                                matrix.postRotate(180.0f);
                                break;
                            case 6:
                                matrix.postRotate(90.0f);
                                break;
                            case 8:
                                matrix.postRotate(270.0f);
                                break;
                        }
                        this.mImageBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
                        this.ivUploadProfilePic.setImageBitmap(this.mImageBitmap);
                        this.ivSavePhoto.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(this, "" + getString(R.string.image_not_supported), Toast.LENGTH_LONG).show();
                    }
                }
            } else if (requestCode == 1003 && resultCode == -1) {
                try {
                    String filePath = DocumentUtils.getPath(this, data.getData());
                    Log.e("File", "filePath: " + filePath);
                    File file = new File(new URI("file://" + filePath.replace(" ", "%20")));
                    Bitmap sourceBitmap2 = BitmapUtils.getScaledBitmap(file, BitmapUtils.getMaxSize(this));
                    int orientation2 = new ExifInterface(file.getPath()).getAttributeInt("Orientation", 1);
                    Matrix matrix2 = new Matrix();
                    switch (orientation2) {
                        case 3:
                            matrix2.postRotate(180.0f);
                            break;
                        case 6:
                            matrix2.postRotate(90.0f);
                            break;
                        case 8:
                            matrix2.postRotate(270.0f);
                            break;
                    }
                    sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + file.getAbsolutePath())));
                    this.mImageBitmap = Bitmap.createBitmap(sourceBitmap2, 0, 0, sourceBitmap2.getWidth(), sourceBitmap2.getHeight(), matrix2, true);
                    this.ivUploadProfilePic.setImageBitmap(this.mImageBitmap);
                    this.ivSavePhoto.setVisibility(View.INVISIBLE);
                } catch (URISyntaxException ex2) {
                    ex2.printStackTrace();
                    Toast.makeText(this, "" + getString(R.string.image_name_error), Toast.LENGTH_LONG).show();
                } catch (Exception ex3) {
                    ex3.printStackTrace();
                    Toast.makeText(this, "" + getString(R.string.image_not_supported), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void updateDeleteIcon(float x, float y, PhotoSortrView.Img imgObject2, boolean isPointerDown, boolean isPointerUp) {
        float centreX = this.ivDelete.getX() + ((float) (this.ivDelete.getWidth() / 2));
        float centreY = this.ivDelete.getY() + ((float) (this.ivDelete.getHeight() / 2));
        int w = this.metrics.widthPixels;
        int h = this.metrics.heightPixels;
        int percentX = ((int) (100.0f * x)) / w;
        int percentY = ((int) (100.0f * y)) / h;
        int xScroll = ((int) (100.0f * centreX)) / w;
        int yScroll = ((int) (100.0f * centreY)) / h;
        if (imgObject2 != null) {
            this.imgObject = imgObject2;/*
            Log.e("DOODLE--(TOUCH POSITION)>percentX and percentY", "" + percentX + "and" + percentY);
            Log.e("DOODLE--(ICON POSITION)>xScroll and yScroll", "" + xScroll + "and" + yScroll);*/
            if (!imgObject2.getisEditiable()) {
                this.editableImageItemDelted = false;
            } else if (this.openTextInEditableMode) {
                storeImage(((BitmapDrawable) imgObject2.getDrawable()).getBitmap(), "textdoodleImage");
                this.photoSorter.unloadImageItem(imgObject2, this);
                PhotoSortrView photoSortrView = this.photoSorter;
                ArrayList<PhotoSortrView.Img> arrayList = PhotoSortrView.DOODLE_IMAGE;
                PhotoSortrView photoSortrView2 = this.photoSorter;
                arrayList.remove(PhotoSortrView.DOODLE_IMAGE.size() - 1);
                this.flEmoji.removeView(this.photoSorter);
                this.flEmoji.addView(this.photoSorter);
                this.openTextInEditableMode = false;
                this.editableImageItemDelted = true;
                startActivity(new Intent(this, EditTextViewActivity.class));
            }
            if (percentY >= 0 && percentY < yScroll && percentX >= 0 && percentX >= xScroll) {
             /*   Log.e("DOODLEdeletd--(TOUCH POSITION)>percentX and percentY", "" + percentX + "and" + percentY);
                Log.e("DOODLEdeleed--(ICON POSITION)>xScroll and yScroll", "" + xScroll + "and" + yScroll);*/
                animateView(this.ivDelete);
                PhotoSortrView photoSortrView3 = this.photoSorter;
                if (!PhotoSortrView.DOODLE_IMAGE.isEmpty() && imgObject2 != null) {
                    this.photoSorter.unloadImageItem(imgObject2, this);
                    PhotoSortrView photoSortrView4 = this.photoSorter;
                    ArrayList<PhotoSortrView.Img> arrayList2 = PhotoSortrView.DOODLE_IMAGE;
                    PhotoSortrView photoSortrView5 = this.photoSorter;
                    arrayList2.remove(PhotoSortrView.DOODLE_IMAGE.size() - 1);
                    this.flEmoji.removeView(this.photoSorter);
                    this.flEmoji.addView(this.photoSorter);
                    setSendIconVisibility();
                }
                PhotoSortrView photoSortrView6 = this.photoSorter;
                if (PhotoSortrView.DOODLE_IMAGE.size() <= 0) {
                    fadeOutAndHideLayout(this.ivDelete);
                } else {
                    this.ivDelete.setVisibility(View.VISIBLE);
                }
                if (imgObject2.getisEditiable()) {
                    this.tvText.setVisibility(View.VISIBLE);
                }
            }
            if (!isPointerDown || imgObject2 == null || !(!isPencilDoodle)) {
                this.tvUndoDoodleDraw.setVisibility(View.VISIBLE);
                this.ivSavePhoto.setVisibility(View.VISIBLE);
                return;
            }
            this.tvUndoDoodleDraw.setVisibility(View.INVISIBLE);
            return;
        }
        if (isPointerDown && isPencilDoodle) {
            this.tvUndoDoodleDraw.setVisibility(View.VISIBLE);
            this.ivSavePhoto.setVisibility(View.VISIBLE);
        }
        if (isPointerUp && isPencilDoodle) {
            this.tvUndoDoodleDraw.setVisibility(View.VISIBLE);
        }
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    private void animateView(View tvAnimate) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList = new ArrayList<>();
        animatorList.add(ObjectAnimator.ofFloat(tvAnimate, "ScaleX", new float[]{0.0f, 1.2f, 1.0f}));
        animatorList.add(ObjectAnimator.ofFloat(tvAnimate, "ScaleY", new float[]{0.0f, 1.2f, 1.0f}));
        animatorSet.playTogether(animatorList);
        tvAnimate.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    private void fadeOutAndHideLayout(final ImageView layout) {
        Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                layout.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
        layout.startAnimation(fadeOut);
    }

    public void updateDoodleImage(Drawable drawable) {
        this.flEmoji.removeView(this.photoSorter);
        this.photoSorter = new PhotoSortrView(this);
        PhotoSortrView.Img img = new PhotoSortrView.Img(drawable, getResources(), true);
        PhotoSortrView.DOODLE_IMAGE.add(img);
        this.flEmoji.addView(this.photoSorter);
        this.photoSorter.loadImageDrawable(img, this);
        this.emojiconsFrame.setVisibility(View.GONE);
        this.ivDelete.setVisibility(View.VISIBLE);
    }

    public void updateDrawable(Drawable drawable, boolean isTextConfirmed) {
        this.llFooter.setVisibility(View.VISIBLE);
        if (isTextConfirmed) {
            if (drawable != null) {
                updateDoodleImage(drawable);
            }
            setSendIconVisibility();
        } else if (this.editClickCount - 1 != 1) {
            if (drawable != null) {
                File sdIconStorageDir = new File(Environment.getExternalStorageDirectory() + "/DoodleImages");
                sdIconStorageDir.mkdirs();
                updateDoodleImage(Drawable.createFromPath(sdIconStorageDir.toString() + "/" + "textdoodleImage" + FileUtils.bitmapExtension));
            }
            setSendIconVisibility();
        }
    }

    public void onColorSelected(int dialogId, int color) {
        com.doodle.utils.preferences.DoodlePreferences.getInstance(this).setDoodleDrawColor(colorToHexString(color));
        com.doodle.utils.preferences.DoodlePreferences.getInstance(this).setDoodleDrawColorInt(Integer.valueOf(color));
        this.ivPencil.setImageDrawable(this.pencilPressed);
        this.ivPencil.setBackgroundColor(Color.parseColor(com.doodle.utils.preferences.DoodlePreferences.getInstance(this).getDoodleDrawColor()));
        this.pencilClickCount--;
        this.flEmoji.removeView(this.photoSorter);
        this.photoSorter = new PhotoSortrView(this);
        PhotoSortrView.Img img = new PhotoSortrView.Img(getResources().getDrawable(R.drawable.ic_launcher), getResources(), false);
        PhotoSortrView.DOODLE_IMAGE.add(img);
        this.flEmoji.addView(this.photoSorter);
        this.photoSorter.loadImageDrawable(img, this);
        PhotoSortrView photoSortrView = this.photoSorter;
        if (!PhotoSortrView.DOODLE_IMAGE.isEmpty()) {
            PhotoSortrView photoSortrView2 = this.photoSorter;
            PhotoSortrView photoSortrView3 = this.photoSorter;
            ArrayList<PhotoSortrView.Img> arrayList = PhotoSortrView.DOODLE_IMAGE;
            PhotoSortrView photoSortrView4 = this.photoSorter;
            photoSortrView2.unloadImageItem((PhotoSortrView.Img) arrayList.get(PhotoSortrView.DOODLE_IMAGE.size() - 1), this);
        }
        PhotoSortrView photoSortrView5 = this.photoSorter;
        ArrayList<PhotoSortrView.Img> arrayList2 = PhotoSortrView.DOODLE_IMAGE;
        PhotoSortrView photoSortrView6 = this.photoSorter;
        arrayList2.remove(PhotoSortrView.DOODLE_IMAGE.size() - 1);
        this.flEmoji.removeView(this.photoSorter);
        this.flEmoji.addView(this.photoSorter);
    }

    public void onDialogDismissed(int dialogId) {
        if (!StringUtils.isNullOrEmpty(com.doodle.utils.preferences.DoodlePreferences.getInstance(this).getDoodleDrawColor())) {
            this.ivPencil.setImageDrawable(this.pencilPressed);
            this.ivPencil.setBackgroundColor(Color.parseColor(com.doodle.utils.preferences.DoodlePreferences.getInstance(this).getDoodleDrawColor()));
            this.pencilClickCount--;
            return;
        }
        this.ivPencil.setImageDrawable(this.pencilDrawable);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txvCancel:
                THEAME_VIEW_DODO.setVisibility(View.GONE);
                break;

            case R.id.imgBack /*2131624203*/:
                PhotoSortrView photoSortrView = this.photoSorter;
                PhotoSortrView.DOODLE_IMAGE.clear();
                this.photoSorter.clearCanvas();
                finish();
                return;

            case R.id.tvPicker /*2131624629*/:
                isPencilDoodle = false;
                this.ivPencil.setBackgroundColor(getResources().getColor(R.color.transparent));
                if (THEAME_VIEW_DODO.getVisibility() == View.GONE) {
                    THEAME_VIEW_DODO.setVisibility(View.VISIBLE);
                } else {
                    THEAME_VIEW_DODO.setVisibility(View.GONE);
                }
                return;
            case R.id.tvText /*2131624630*/:
                try{
                    isPencilDoodle = false;
                    this.ivPencil.setBackgroundColor(getResources().getColor(R.color.transparent));
                    this.editClickCount = 0;
                    handleTextClick();
                }catch (Exception e){}
                return;
            case R.id.ivPencil /*2131624631*/:
                if (!isPencilDoodle || this.pencilClickCount != 0) {
                    this.pencilClickCount++;
                    animateView(this.ivPencil);
                    openColorPicker();
                    isPencilDoodle = true;
                    return;
                }
                isPencilDoodle = false;
                animateView(this.ivPencil);
                this.ivPencil.setImageDrawable(this.pencilDrawable);
                this.ivPencil.setBackgroundColor(getResources().getColor(R.color.transparent));
                return;
            case R.id.tvOpenCamera /*2131624632*/:
                isPencilDoodle = false;
                this.ivPencil.setBackgroundColor(getResources().getColor(R.color.transparent));
                animateView(this.tvOpenCamera);
                showPhotoOptionsDialog();
                return;
            case R.id.tvUndoDoodleDraw /*2131624636*/:
                animateView(this.tvUndoDoodleDraw);
                isPencilDoodle = true;
                this.tvUndoDoodleDraw.setVisibility(View.INVISIBLE);
                this.photoSorter.clearCanvas();
                setSendIconVisibility();
                return;
            case R.id.ivDelete /*2131624637*/:
                animateView(this.ivDelete);
                removeImageItem();
                setSendIconVisibility();
                return;
            case R.id.ivSavePhoto /*2131624638*/:
                animateView(this.ivSavePhoto);
                savePhoto();
                return;
            default:
                return;
        }
    }

    private void setSendIconVisibility() {
        PhotoSortrView photoSortrView = this.photoSorter;
        if (PhotoSortrView.DOODLE_IMAGE.isEmpty() && this.mImageBitmap == null) {
            PhotoSortrView photoSortrView2 = this.photoSorter;
            if (isEmpty(PhotoSortrView.mBitmap)) {
                this.ivSavePhoto.setVisibility(View.INVISIBLE);
                return;
            }
        }
        this.ivSavePhoto.setVisibility(View.VISIBLE);
    }

    private void openColorPicker() {
        ColorPickerDialogFragment f = ColorPickerDialogFragment.newInstance(0, null, null, com.doodle.utils.preferences.DoodlePreferences.getInstance(this).getDoodleDrawColorInt().intValue(), true);
        //f.setStyle(0, R.style.LightPickerDialogTitleStyle);
        f.show(getFragmentManager(), "d");
    }

    private void openDoodleEmoji() {
        if (this.emojiconsFrame.getVisibility() == View.VISIBLE) {
            this.emojiconsFrame.setVisibility(View.GONE);
            this.ivPencil.setClickable(true);
            this.tvUndoDoodleDraw.setClickable(true);
            this.tvText.setClickable(true);
            return;
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                DoodleActivity.this.emojiconsFrame.setVisibility(View.VISIBLE);
                DoodleActivity.this.ivPencil.setClickable(false);
                DoodleActivity.this.tvUndoDoodleDraw.setClickable(false);
                DoodleActivity.this.tvText.setClickable(false);
            }
        }, 50);
    }

    private void handleTextClick() {
        switch (this.editClickCount) {
            case 0:
                if (this.imgObject != null) {

                   for (int i=0;i< PhotoSortrView.DOODLE_IMAGE.size();i++){
                        PhotoSortrView photoSortrView = this.photoSorter;
                            PhotoSortrView photoSortrView2 = this.photoSorter;
                            if (((PhotoSortrView.Img) PhotoSortrView.DOODLE_IMAGE.get(i)).getisEditiable()) {
                                PhotoSortrView photoSortrView3 = this.photoSorter;
                                storeImage(((BitmapDrawable) ((PhotoSortrView.Img) PhotoSortrView.DOODLE_IMAGE.get(i)).getDrawable()).getBitmap(), "textdoodleImage");
                                PhotoSortrView photoSortrView4 = this.photoSorter;
                                PhotoSortrView photoSortrView5 = this.photoSorter;
                                photoSortrView4.unloadImageItem((PhotoSortrView.Img) PhotoSortrView.DOODLE_IMAGE.get(i), this);
                                PhotoSortrView photoSortrView6 = this.photoSorter;
                                PhotoSortrView.DOODLE_IMAGE.remove(i);
                                this.openTextInEditableMode = false;
                        }
                    }

                }
                this.flEmoji.removeView(this.photoSorter);
                this.flEmoji.addView(this.photoSorter);
                this.llFooter.setVisibility(View.GONE);
                Intent intent = new Intent(this, EditTextViewActivity.class);
                this.editClickCount++;
                startActivity(intent);
                return;
            case 1:
                this.editClickCount++;
                this.openTextInEditableMode = true;
                if (this.editableImageItemDelted) {
                    com.doodle.utils.preferences.DoodlePreferences.getInstance(this).setdoodleText("");
                    return;
                }
                return;
            case 2:
                this.editClickCount = 0;
                if (this.imgObject != null) {

                    for (int i2=0 ;i2<PhotoSortrView.DOODLE_IMAGE.size();i2++){
                        PhotoSortrView photoSortrView7 = this.photoSorter;
                            PhotoSortrView photoSortrView8 = this.photoSorter;
                            if (((PhotoSortrView.Img) PhotoSortrView.DOODLE_IMAGE.get(i2)).getisEditiable()) {
                                PhotoSortrView photoSortrView9 = this.photoSorter;
                                PhotoSortrView photoSortrView10 = this.photoSorter;
                                photoSortrView9.unloadImageItem((PhotoSortrView.Img) PhotoSortrView.DOODLE_IMAGE.get(i2), this);
                                PhotoSortrView photoSortrView11 = this.photoSorter;
                                PhotoSortrView.DOODLE_IMAGE.remove(i2);
                                this.openTextInEditableMode = false;
                            }
                    }

                }
                this.llFooter.setVisibility(View.GONE);
                startActivity(new Intent(this, EditTextViewActivity.class));
                return;
            default:
                return;
        }
    }

    public void onBackPressed() {
        if (this.emojiconsFrame.getVisibility() == View.GONE) {
            super.onBackPressed();
            PhotoSortrView photoSortrView = this.photoSorter;
            PhotoSortrView.DOODLE_IMAGE.clear();
            this.photoSorter.clearCanvas();
            com.doodle.utils.preferences.DoodlePreferences.getInstance(this).setdoodleText("");
        } else if (this.countBackPress == View.VISIBLE) {
            this.emojiconsFrame.setVisibility(View.GONE);
            isPencilDoodle = true;
            this.ivPencil.setClickable(true);
            this.tvUndoDoodleDraw.setClickable(true);
            this.tvText.setClickable(true);
            this.countBackPress++;
            this.llFooter.setVisibility(View.VISIBLE);
        } else if (this.countBackPress == 1) {
            if (this.photoSorter != null) {
                PhotoSortrView photoSortrView2 = this.photoSorter;
                PhotoSortrView.DOODLE_IMAGE.clear();
                this.photoSorter.clearCanvas();
            }
            finish();
        }
    }

    public Drawable storeImage(Bitmap imageData, String filename) {
        File sdIconStorageDir = new File(Environment.getExternalStorageDirectory() + "/DoodleImages");
        sdIconStorageDir.mkdirs();
        try {
            String filePath = sdIconStorageDir.toString() + "/" + filename + FileUtils.bitmapExtension;
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
            return Drawable.createFromPath(filePath);
        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return null;
        } catch (IOException e2) {
            Log.w("TAG", "Error saving image file: " + e2.getMessage());
            return null;
        }
    }

    @Override
    public void onclick() {
    }

    @Override
    public void set_walpaper(String Photo_Name) {
        addImage(Photo_Name);
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }
}
