package com.infraredctrl.test;

import com.infraredctrl.activity.R;
import com.infraredctrl.activity.R.layout;

import android.app.Activity;
import android.os.Bundle;

public class ViewPageTest extends Activity {
	// private TrapezoidImageButton mibtCenter, mibtUp, mibtDown, mibtLeft,
	// mibtRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_page_view);
		this.findViews();
	}

	private void findViews() {
		// this.mibtCenter = (TrapezoidImageButton)
		// findViewById(R.id.ibtCenter);
		// this.mibtUp = (TrapezoidImageButton) findViewById(R.id.ibtUp);
		// this.mibtDown = (TrapezoidImageButton) findViewById(R.id.ibtDown);
		// this.mibtLeft = (TrapezoidImageButton) findViewById(R.id.ibtLeft);
		// this.mibtRight = (TrapezoidImageButton) findViewById(R.id.ibtRight);
		// mibtCenter.setOnClickListener(clickListener);
		// mibtUp.setOnClickListener(clickListener);
		// mibtDown.setOnClickListener(clickListener);
		// mibtLeft.setOnClickListener(clickListener);
		// mibtRight.setOnClickListener(clickListener);
	}

	// private OnClickListener clickListener = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.ibtCenter:
	// Toast.makeText(ViewPageTest.this, "center", Toast.LENGTH_SHORT).show();
	// break;
	// case R.id.ibtUp:
	// Toast.makeText(ViewPageTest.this, "up", Toast.LENGTH_SHORT).show();
	// break;
	// case R.id.ibtDown:
	// Toast.makeText(ViewPageTest.this, "down", Toast.LENGTH_SHORT).show();
	// break;
	// case R.id.ibtLeft:
	// Toast.makeText(ViewPageTest.this, "left", Toast.LENGTH_SHORT).show();
	// break;
	// case R.id.ibtRight:
	// Toast.makeText(ViewPageTest.this, "right", Toast.LENGTH_SHORT).show();
	// break;
	// default:
	// break;
	// }
	// }
	// };
}
