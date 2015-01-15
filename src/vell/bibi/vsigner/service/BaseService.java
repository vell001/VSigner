package vell.bibi.vsigner.service;

import cn.bmob.v3.BmobUser;
import vell.bibi.vsigner.model.User;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

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
}
