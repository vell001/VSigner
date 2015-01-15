package vell.bibi.vsigner.service;

import java.util.Timer;
import java.util.TimerTask;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;

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
		BmobQuery<Channel> channelQuery = new BmobQuery<Channel>();
		channelQuery.addWhereContains(Channel.SUBSCRIBERS_KEY, mCurrentUser.getObjectId());
		channelQuery.addWhereEqualTo(Channel.IS_ACTIVE_KEY, true);
		
		channelQuery.count(mContext, Channel.class, new CountListener() {
			@Override
			public void onSuccess(int count) {
				if (count != 0) {
					Intent intent = new Intent();
					intent.putExtra(NEW_SIGN_REQUEST_COUNT_EXTRA, count);
					intent.setAction(NEW_SIGN_REQUEST_ACTION);
					mContext.sendBroadcast(intent);
				}
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				Log.i("vsigner", arg1);
			}
		});
	}

}
