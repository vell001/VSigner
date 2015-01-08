package vell.bibi.vsigner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;


/**
 * 闪屏页
 * @author VellBibi
 *
 * @date Jan 8, 2015
 */
@SuppressLint("HandlerLeak")
public class SplashActivity extends BaseActivity {

	private static final int GO_MAIN = 0100;
	private static final int GO_REGISTER = 0200;
	
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_splash);
	}

	@Override
	public void initViews() {

	}

	@Override
	public void initListeners() {

	}

	@Override
	public void initData() {
		
//		mHandler.sendEmptyMessageDelayed(GO_MAIN, 3000);
		mHandler.sendEmptyMessageDelayed(GO_REGISTER, 2000);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent intent = null;
			switch (msg.what) {
			case GO_MAIN:
				intent = new Intent(mContext, MainActivity.class);
				break;
			case GO_REGISTER:
				intent = new Intent(mContext, RegisterActivity.class);
				break;
			}
			
			startActivity(intent);
			finish();
		}
	};
}
