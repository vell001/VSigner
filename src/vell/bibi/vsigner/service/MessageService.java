package vell.bibi.vsigner.service;

import java.util.Timer;
import java.util.TimerTask;

import vell.bibi.vsigner.config.Constants;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 定时向服务器请求数据
 * @author VellBibi
 *
 * @date Jan 14, 2015
 */
public class MessageService extends BaseService {
	public final static String NEW_SIGN_REQUEST_ACTION = "vell.bibi.vsigner.intent.action.NewSignRequest";
	public final static String NEW_SIGN_REQUEST_COUNT_EXTRA = "NewSignRequestCount";
	
	private Timer mTimer = null;
	private TimerTask mTimerTask = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		startTimer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimer();
		
		startService(new Intent(this, MessageService.class)); // 初步防止被清理
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void startTimer() {
		if(mTimer == null) {
			mTimer = new Timer();
			mTimerTask = new TimerTask() {
				@Override
				public void run() { // 定时向服务器刷新数据
					Log.i("messageService", "run");
					checkNewSignRequest();
				}
			};
			
			mTimer.schedule(mTimerTask, 100, Constants.MESSAGE_REFRESH_PERIOD);
		}
	}
	
	private void stopTimer() {
		if(mTimer != null) {
			mTimerTask.cancel();
			mTimer.cancel();
			
			mTimerTask = null;
			mTimer = null;
		}
	}
	
	private void checkNewSignRequest() {
	}

}
