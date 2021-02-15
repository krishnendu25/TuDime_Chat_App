/**
 * Created by Donny Dominic on 27-01-2016.
 */
package com.TuDime.doodle.utils.doodleutils.visualizer2;

import android.app.Activity;
import android.os.Bundle;

public class MultiTouchVisualizerActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("hello");
		setContentView(new MultiTouchVisualizerView(this));
	}
}