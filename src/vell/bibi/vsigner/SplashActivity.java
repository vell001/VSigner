package vell.bibi.vsigner;

import java.util.List;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.User;
import vell.bibi.vsigner.util.PhoneStateUtil;
import vell.bibi.vsigner.util.StrUtil;
import vell.bibi.vsigner.view.TipsDialog;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;


/**
 * 闪屏页
 * 处理用户登录
 * @author VellBibi
 *
 * @date Jan 8, 2015
 */
@SuppressLint({ "HandlerLeak", "ShowToast" })
public class SplashActivity extends BaseActivity{

	private static final int GO_MAIN = 0100;
	private static final int GO_REGISTER = 0200;
	private static final int GO_LOGIN = 0300;
	
	private static final int JUMP_TIME = 2000; // 闪屏页跳转时间
	@Override
	public void setContentView() {
		Log.i(TAG, "SplashActivity");
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
		if (mCurrentUser == null) { // 未登录
			// 获取IMSI
			String phoneIMSI = PhoneStateUtil.getPhoneIMSI(mContext);
			if(StrUtil.isEmpty(phoneIMSI)) { // 获取IMSI失败
				TipsDialog dialogTips = new TipsDialog(mContext, getString(R.string.cannot_get_phone_imsi_tips), getString(R.string.exist));
				dialogTips.SetOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						finish();
					}
				});
				dialogTips.show();
				return;
			}
			// 判断IMSI是否已经存在
			BmobQuery<User> query = new BmobQuery<User>();
			query.addWhereEqualTo(Constants.IMSI_KEY, phoneIMSI);
			// 查询服务器
			ShowToast(getString(R.string.server_query_tips));
			query.findObjects(this, new FindListener<User>() {
				@Override
			    public void onSuccess(List<User> userList) {
			    	if(userList.size() > 0) { // 已存在用户
			    		mCurrentUser = userList.get(0);
			    		mHandler.sendEmptyMessageDelayed(GO_LOGIN, JUMP_TIME);
			    	} else { // 新用户
			    		mHandler.sendEmptyMessageDelayed(GO_REGISTER, JUMP_TIME);
					}
			    }
			    @Override
			    public void onError(int code, String msg) { // 服务器查找出错
			    	TipsDialog dialogTips = new TipsDialog(mContext, getString(R.string.server_query_error_tips) + ": " + msg, getString(R.string.exist));
					dialogTips.SetOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialogInterface) {
							finish();
						}
					});
					dialogTips.show();
			    }
			});
		} else {
			mHandler.sendEmptyMessageDelayed(GO_MAIN, JUMP_TIME);
		}
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
			case GO_LOGIN:
				intent = new Intent(mContext, LoginActivity.class);
				intent.putExtra(Constants.USERNAME_EXTRA, mCurrentUser.getUsername());
				break;
			}
			
			startActivity(intent);
			finish();
		}
	};
}
