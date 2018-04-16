package com.infraredctrl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mobstat.StatService;

public class WebViewPage extends Activity {

	private Button mbtBack;
	private ProgressBar progressBar;
	private TextView tvTitle;
	private WebView webView;
	private String url, title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_web_view);
		Intent intent = getIntent();
		if (intent.hasExtra("URL")) {
			url = intent.getStringExtra("URL");
		}
		this.tvTitle = (TextView) findViewById(R.id.tvTitle);
		if (intent.hasExtra("TITLE")) {
			title = intent.getStringExtra("TITLE");
			tvTitle.setText(title);
		}
		this.mbtBack = (Button) findViewById(R.id.btBack);
		this.progressBar = (ProgressBar) findViewById(R.id.pbWebView);
		this.webView = (WebView) findViewById(R.id.webView);

		progressBar.setMax(100);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.setInitialScale(39);

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar.setProgress(100);
					progressBar.setVisibility(View.GONE);
				} else {
					progressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				progressBar.setProgress(10);
				view.loadUrl(url);
				return true;
			}
		});

		webView.loadUrl(url);

		this.mbtBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(WebViewPage.this);
	}

	@Override
	protected void onPause() {
		StatService.onPause(WebViewPage.this);
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return true;
	}

	private void back() {
		if (webView.canGoBack()) {
			webView.goBack();
		} else {
			onBackPressed();
		}
	}

}
