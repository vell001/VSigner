package vell.bibi.vsigner.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import vell.bibi.vsigner.MainActivity;
import vell.bibi.vsigner.R;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.view.MessageFragment.MessageBroadcastRecevier;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;

/**
 * 定时向服务器请求数据
 * @author VellBibi
 *
 * @date Jan 14, 2015
 */
public class TimerService extends BaseService {
	
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
		
		startService(new Intent(this, TimerService.class)); // 初步防止被清理
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
					Log.i("TimerService", "run");
					checkNotSignChannelCount();
//					checkNewSignerCount();
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
	
	/**
	 * 检查订阅频道是否开启签到
	 */
	private int mNotSignChannelCount = 0;
	private void checkNotSignChannelCount() {
		AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
		try {
			ace.callEndpoint(mContext, "checkNotSignChannelCount",new JSONObject("{userObjectId:" + mCurrentUser.getObjectId() + "}"), new CloudCodeListener() {
			    @Override
			    public void onSuccess(Object object) {
			    	int count = Integer.parseInt(object.toString());
			    	if(count != mNotSignChannelCount) {
			    		Intent intent = new Intent(MessageBroadcastRecevier.ACTION);
			    		intent.putExtra(Constants.COUNT_KEY, count);
			    		sendBroadcast(intent);
			    		
			    		if(count > mNotSignChannelCount) {
			    			showNotSignChannelCountNotify(count);
			    		}
			    	}
			    	mNotSignChannelCount = count;
			    }
			    @Override
			    public void onFailure(int code, String msg) {
			    }
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 检查新签到用户
	 */
/*	private void checkNewSignerCount() {
		AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
		try {
			ace.callEndpoint(mContext, "checkNewSignerCount",new JSONObject("{userObjectId:" + mCurrentUser.getObjectId() + "}"), new CloudCodeListener() {
			    @Override
			    public void onSuccess(Object object) {
			    	int count = Integer.parseInt(object.toString());
			    	if(count != mNotSignChannelCount) {
			    		Intent intent = new Intent(MessageBroadcastRecevier.ACTION);
			    		intent.putExtra(Constants.COUNT_KEY, count);
			    		sendBroadcast(intent);
			    	}
			    	mNotSignChannelCount = count;
			    }
			    @Override
			    public void onFailure(int code, String msg) {
			    }
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}*/
	
	private void showNotSignChannelCountNotify(int count) {
		showNotify(getString(R.string.new_sign_channel_tips), String.format(getString(R.string.new_sign_channel_notify_format), count), MainActivity.class);
	}
//	
//	private void checkChannelNewSignerCount() {
//		BmobQuery<Channel> cQuery = new BmobQuery<Channel>();
//		cQuery.addWhereEqualTo(Channel.MANAGER_KEY, arg1)
//	}
}
