/**
 /**
 * Created by Donny Dominic on 27-01-2016.
 */
package obj.quickblox.sample.chat.java.doodle.utils.doodleutils.photosortr;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.utils.ToastUtils;

public class PhotoSortrActivity extends Activity {
	
PhotoSortrView photoSorter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
		this.setTitle(getString(R.string.Instructions));
		photoSorter = new PhotoSortrView(this);
		setContentView(photoSorter);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		photoSorter.loadImages(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		photoSorter.unloadImages();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			photoSorter.trackballClicked();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}