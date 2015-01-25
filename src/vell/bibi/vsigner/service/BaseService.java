package vell.bibi.vsigner.service;

import vell.bibi.vsigner.R;
import vell.bibi.vsigner.config.Conf;
import vell.bibi.vsigner.model.User;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import cn.bmob.v3.BmobUser;

public class BaseService extends Service{

	protected User mCurrentUser;
	protected Context mContext;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		mCurrentUser = BmobUser.getCurrentUser(this, User.class);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	protected void showNotify(String content, Class<?> jumpClass) {
		showNotify(getString(R.string.tips), content, "", jumpClass);
	}
	
	protected void showNotify(String title, String content, Class<?> jumpClass) {
		showNotify(title, content, "", jumpClass);
	}
	
	@SuppressLint("NewApi")
	protected void showNotify(String title, String content, String info, Class<?> jumpClass) {
		if(!Conf.isNotify) return;
		Intent intent = new Intent(mContext, jumpClass);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,0); 
		//获得通知管理器
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //构建一个通知对象(需要传递的参数有三个,分别是图标,标题和 时间)
        Notification notification = new Notification.Builder(mContext)
        	.setContentText(content)
        	.setContentTitle(title)
        	.setContentInfo(info)
        	.setContentIntent(pendingIntent)
        	.setWhen(System.currentTimeMillis())
        	.setSmallIcon(R.drawable.ic_launcher)
        	.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 自动销毁
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        
        if(Conf.isNotifySound) {
        	notification.defaults |= Notification.DEFAULT_SOUND;
        }
        if(Conf.isNotifyVibrate) {
        	notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        
        manager.notify(0, notification);//发动通知,id由自己指定，每一个Notification对应的唯一标志
	}
}
