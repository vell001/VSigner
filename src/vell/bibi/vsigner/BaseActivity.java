package vell.bibi.vsigner;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.User;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;

/**
 * 基础活动类，所有活动都继承自此
 * @author VellBibi
 *
 * @date Jan 8, 2015
 */
public abstract class BaseActivity extends FragmentActivity {

	protected Context mContext;
	protected BmobInstallation mBmobInstallation;
	protected String mInstallationId;
	protected User mCurrentUser = null;
	
	protected int mScreenWidth;
	protected int mScreenHeight;
	
	public static final String TAG = "vsigner";
	
	protected ProgressDialog mProgressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
		Bmob.initialize(this, Constants.BmobAPPID);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//获取当前屏幕宽高
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		
		// 初始化数据
		mContext = this;
		mBmobInstallation = BmobInstallation.getCurrentInstallation(this);
		mInstallationId = BmobInstallation.getInstallationId(this);
		mCurrentUser = BmobUser.getCurrentUser(this, User.class);

		setContentView();
		initViews();
		initListeners();
		initData();
	}
	/**
	 * 设置布局文件
	 */
	public abstract void setContentView();

	/**
	 * 初始化布局文件中的控件
	 */
	public abstract void initViews();

	/**
	 * 初始化控件的监听
	 */
	public abstract void initListeners();
	
	/** 进行数据初始化
	  * initData
	  */
	public abstract void initData();
	Toast mToast;
	public void ShowToast(String text) {
		if (!TextUtils.isEmpty(text)) {
			if (mToast == null) {
				mToast = Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_SHORT);
			} else {
				mToast.setText(text);
			}
			mToast.show();
		}
	}
	
	/** 获取当前状态栏的高度
	  * getStateBar
	  * @Title: getStateBar
	  * @throws
	  */
	public int getStateBar(){
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}
	
	public static int dip2px(Context context,float dipValue){
		float scale=context.getResources().getDisplayMetrics().density;		
		return (int) (scale*dipValue+0.5f);		
	}
	
	public void showProgressDialog(String message, boolean canceledOnTouchOutide){
		if(mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface arg0) {
					mProgressDialog = null;
				}
			});
		}
		mProgressDialog.setMessage(message);
		mProgressDialog.setCanceledOnTouchOutside(canceledOnTouchOutide);
		mProgressDialog.show();
	}
	
	public void showProgressDialog(String message) {
		showProgressDialog(message, false);
	}
	
	public void setProgressToDialog(int value) {
		mProgressDialog.setProgress(value);
	}
	
	public void hideProgressDialog() {
		if(mProgressDialog.isShowing()) {
			mProgressDialog.hide();
		}
	}
}
