package com.infraredctrl.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;

public class WelcomePage extends Activity implements OnClickListener {
	private int currIndex = 0;
	private ViewPager vp;
	// private PagerTitleStrip pts;
	private List<View> list = null;
	private List<String> title = null;
	private MyAdapter adapter = null;
	private Button WelStart;
	// private LayoutInflater inflater = null;
	@SuppressWarnings("unused")
	// private View view1, view2, view3, view, view4, view5, view6, pageSeting;
	private View view1, view2, view3, pageSeting;
	// private ImageView mPage1, mPage2, mPage3, mPage4, mPage0, mPage5, mPage6,
	// mPageImg;
	private ImageView mPage0, mPage1, mPage2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
		// 初始化xml里的系统资源
		getRes();
		// 调用介绍界面图片切换的效果
		SwitchImage();
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(WelcomePage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(WelcomePage.this);
		super.onPause();
	}

	/**
	 * 得到xml里的控件资源
	 */
	public void getRes() {
		// 获取控
		vp = (ViewPager) findViewById(R.id.viewpage);
		view1 = LayoutInflater.from(WelcomePage.this).inflate(R.layout.welcome1, null);
		view2 = LayoutInflater.from(WelcomePage.this).inflate(R.layout.welcome2, null);
		view3 = LayoutInflater.from(WelcomePage.this).inflate(R.layout.welcome3, null);
		// view4 =
		// LayoutInflater.from(WelcomePage.this).inflate(R.layout.welcome4,
		// null);
		// view5 =
		// LayoutInflater.from(WelcomePage.this).inflate(R.layout.welcome5,
		// null);
		// view6 =
		// LayoutInflater.from(WelcomePage.this).inflate(R.layout.welcome6,
		// null);
		// pageSeting =
		// LayoutInflater.from(WelcomePage.this).inflate(R.layout.page_setting_up,
		// null);
		// mPageImg = (ImageView) findViewById(R.id.p);
		WelStart = (Button) view3.findViewById(R.id.WelStart);
		WelStart.setOnClickListener(this);
		mPage0 = (ImageView) findViewById(R.id.page0);
		mPage1 = (ImageView) findViewById(R.id.page1);
		mPage2 = (ImageView) findViewById(R.id.page2);
		// mPage3 = (ImageView) findViewById(R.id.page3);
		// mPage4 = (ImageView) findViewById(R.id.page4);
		// mPage5 = (ImageView) findViewById(R.id.page5);
		// mPage6 = (ImageView) findViewById(R.id.page6);

	}

	/* 实现介绍界面的图片切换 */
	public void SwitchImage() {
		list = new ArrayList<View>();
		list.add(view1);
		list.add(view2);
		list.add(view3);
		// list.add(view4);
		// list.add(view5);
		// list.add(view6);
		// list.add(pageSeting);

		title = new ArrayList<String>();
		title.add("title1");
		title.add("title2");
		title.add("title3");
		// title.add("title4");
		// title.add("title5");
		// title.add("title6");
		// title.add("pageSeting");
		// 创建适配器并设置
		adapter = new MyAdapter();

		vp.setAdapter(adapter);
		vp.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	/**
	 * 定义图片切换的一个适配器
	 */
	public class MyAdapter extends PagerAdapter {
		// 重写适配器里的方法

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(list.get(position));
			// view = list.get(position);
			return list.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			// super.destroyItem(container, position, object);

			((ViewPager) container).removeView(list.get(position));
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return title.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		// int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		// int two = one * 2;// 页卡1 -> 页卡3 偏移量
		// int move = 20;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				if (currIndex == arg0 + 1) {
					animation = new TranslateAnimation(20 * (arg0 + 1), 20 * arg0, 0, 0);
				}
				break;
			case 1:
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage0.setImageDrawable(getResources().getDrawable(R.drawable.page));
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
				if (currIndex == arg0 - 1) {
					animation = new TranslateAnimation(20 * (arg0 - 1), 20 * arg0, 0, 0);

				} else if (currIndex == arg0 + 1) {
					animation = new TranslateAnimation(20 * (arg0 + 1), 20 * arg0, 0, 0);
				}
				break;
			case 2:
				mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
				mPage1.setImageDrawable(getResources().getDrawable(R.drawable.page));
				// mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page));
				if (currIndex == arg0 - 1) {
					animation = new TranslateAnimation(20 * (arg0 - 1), 20 * arg0, 0, 0);
				} else if (currIndex == arg0 + 1) {
					animation = new TranslateAnimation(20 * (arg0 + 1), 20 * arg0, 0, 0);
				}
				break;
			// case 3:
			// mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
			// mPage2.setImageDrawable(getResources().getDrawable(R.drawable.page));
			// mPage4.setImageDrawable(getResources().getDrawable(R.drawable.page));
			//
			// if (currIndex == arg0 - 1) {
			// animation = new TranslateAnimation(20 * (arg0 - 1), 20 * arg0, 0,
			// 0);
			//
			// } else if (currIndex == arg0 + 1) {
			// animation = new TranslateAnimation(20 * (arg0 + 1), 20 * arg0, 0,
			// 0);
			// }
			//
			// break;
			// case 4:
			// mPage4.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
			// mPage3.setImageDrawable(getResources().getDrawable(R.drawable.page));
			// mPage5.setImageDrawable(getResources().getDrawable(R.drawable.page));
			//
			// if (currIndex == arg0 - 1) {
			// animation = new TranslateAnimation(20 * (arg0 - 1), 20 * arg0, 0,
			// 0);
			//
			// } else if (currIndex == arg0 + 1) {
			// animation = new TranslateAnimation(20 * (arg0 + 1), 20 * arg0, 0,
			// 0);
			// }
			//
			// break;
			// case 5:
			// mPage5.setImageDrawable(getResources().getDrawable(R.drawable.page_now));
			// mPage4.setImageDrawable(getResources().getDrawable(R.drawable.page));
			// // mPage6.setImageDrawable(getResources().getDrawable(
			// // R.drawable.page));
			//
			// if (currIndex == arg0 - 1) {
			// animation = new TranslateAnimation(20 * (arg0 - 1), 20 * arg0, 0,
			// 0);
			//
			// } else if (currIndex == arg0 + 1) {
			// animation = new TranslateAnimation(20 * (arg0 + 1), 20 * arg0, 0,
			// 0);
			// }

			// break;
			case 6:
				animation = new TranslateAnimation(20 * (arg0 - 1), 20 * arg0, 0, 0);
				finish();
				break;
			}

			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(4000);
			// mPage.startAnimation(animation);
		}

		// public void startbutton(View view) {
		// Intent intent = new Intent(WelcomePage.this, TabHostPage.class);
		// startActivity(intent);
		// finish();
		// }

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(WelcomePage.this, TabHostPage.class);
		startActivity(intent);
		finish();
	}
}
