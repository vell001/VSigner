package vell.bibi.vsigner;

import java.io.Serializable;
import java.util.Date;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.model.ChannelSigner;
import vell.bibi.vsigner.util.StrUtil;
import vell.bibi.vsigner.view.MessageFragment.MessageBroadcastRecevier;
import vell.bibi.vsigner.view.TipsDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.SaveListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;


public class LocationActivity extends BaseActivity{

	public LocationClient mLocationClient = null;
	public BDLocation mLocation = null;
	public BDLocationListener mBdLocationListener = new MyLocationListener();
	
	private TextView mLatitudeTextView;
	private TextView mLongtitudeTextView;
	private TextView mAddressTextView;
	private TextView mRefreshTextView;
	private TextView mChannelNameTextView;
	private Channel mChannel;
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_location);
	}

	@Override
	public void initViews() {
		mLatitudeTextView = (TextView) findViewById(R.id.tv_latitude);
		mLongtitudeTextView = (TextView) findViewById(R.id.tv_longtitude);
		mAddressTextView = (TextView) findViewById(R.id.tv_sign_address);
		mRefreshTextView = (TextView) findViewById(R.id.tv_refresh_time);
		mChannelNameTextView = (TextView) findViewById(R.id.tv_channel_name);
	}

	@Override
	public void initListeners() {
		mLocationClient = new LocationClient(mContext);
		mLocationClient.registerLocationListener(mBdLocationListener);
	}

	@Override
	public void initData() {
		initLocationClientOption();
		mLocationClient.start();
		refreshLocation();
		
		Intent intent = getIntent();
		Serializable serializable = intent.getSerializableExtra(Constants.CHANNEL_KEY);
		if(serializable != null && serializable instanceof Channel) {
			mChannel = (Channel) serializable;
		} else {
			ShowToast(getString(R.string.param_error));
			finish();
		}
		if(!mChannel.isActive()) {
			TipsDialog tipsDialog = new TipsDialog(mContext, getString(R.string.channel_is_not_active), getString(R.string.ok));
			tipsDialog.SetOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface arg0) {
						finish();
					}
				});
			tipsDialog.show();
		}
		
		mChannelNameTextView.setText(mChannel.getName());
	}
	
	/**
	 * 初始化选项
	 */
	private void initLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		option.setOpenGps(true);// 打开gps
		option.setIgnoreKillProcess(true);
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * 接收位置信息
	 * @author VellBibi
	 *
	 * @date Jan 22, 2015
	 */
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			hideProgressDialog();
			if (location == null || StrUtil.isEmpty(location.getAddrStr())) {
				ShowToast(getString(R.string.location_error));
				return;
			}
			mLocation = location;
			mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
			mLongtitudeTextView.setText(String.valueOf(location.getLongitude()));
			mAddressTextView.setText(location.getAddrStr());
			mRefreshTextView.setText(String.format(getString(R.string.refresh_time_format), location.getTime()));
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		mLocationClient.unRegisterLocationListener(mBdLocationListener);
	}
	
	/**
	 * 刷新位置信息
	 */
	private void refreshLocation() {
		showProgressDialog(getString(R.string.locating));
		if(mLocationClient != null) {
			mLocationClient.requestLocation();
		} else {
			ShowToast(getString(R.string.location_error));
		}
	}
	
	public void btn_refresh_location_onclick(View v) {
		refreshLocation();
	}
	
	public void btn_sign_onclick(View v) {
		showProgressDialog(getString(R.string.server_query_tips));
		// 检查是否已经签到过了
		BmobQuery<ChannelSigner> channelSignerQuery = new BmobQuery<ChannelSigner>();
		channelSignerQuery.addWhereGreaterThanOrEqualTo(ChannelSigner.SIGN_DATE_KEY, mChannel.getStartSignDate()); 
		channelSignerQuery.addWhereEqualTo(ChannelSigner.CHANNEL_KEY, mChannel.getObjectId());
		channelSignerQuery.addWhereEqualTo(ChannelSigner.SIGNER_KEY, mCurrentUser.getObjectId());
		
		channelSignerQuery.count(mContext, ChannelSigner.class, new CountListener() {
			@Override
			public void onSuccess(int count) {
				hideProgressDialog();
				if(count > 0) { // 已经签到过了
					TipsDialog tipsDialog = new TipsDialog(mContext, getString(R.string.already_signed), getString(R.string.ok));
					tipsDialog.SetOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface arg0) {
								finish();
							}
						});
					tipsDialog.show();
				} else {
					// 签到
					signToServer();
				}
			}
			
			@Override
			public void onFailure(int arg0, String msg) {
				hideProgressDialog();
				new TipsDialog(mContext, getString(R.string.server_query_error_tips) + ": " + msg, getString(R.string.ok)).show();
			}
		});
	}
	
	public void btn_cancel_onclick(View v) {
		finish();
	}
	
	/**
	 * 签到
	 */
	private void signToServer() {
		showProgressDialog(getString(R.string.server_query_tips));
		ChannelSigner channelSigner = new ChannelSigner();
		channelSigner.setChannel(mChannel);
		channelSigner.setSigner(mCurrentUser);
		channelSigner.setSignDate(new BmobDate(new Date(System.currentTimeMillis())));
		channelSigner.setSignGeoPoint(new BmobGeoPoint(mLocation.getLongitude(), mLocation.getLatitude()));
		channelSigner.setAddress(mLocation.getAddrStr());
		
		channelSigner.save(mContext, new SaveListener() {
			@Override
			public void onSuccess() {
				hideProgressDialog();
				TipsDialog tipsDialog = new TipsDialog(mContext, getString(R.string.sign_success), getString(R.string.ok));
				tipsDialog.SetOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface arg0) {
							finish();
						}
					});
				tipsDialog.show();
			}
			
			@Override
			public void onFailure(int arg0, String msg) {
				hideProgressDialog();
				new TipsDialog(mContext, getString(R.string.save_error) + ": " + msg, getString(R.string.ok)).show();
			}
		});
	}
	
	@Override
	public void finish() {
		super.finish();
		sendBroadcast(new Intent(MessageBroadcastRecevier.ACTION));
	}
}

