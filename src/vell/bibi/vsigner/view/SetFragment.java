package vell.bibi.vsigner.view;

import vell.bibi.vsigner.R;
import vell.bibi.vsigner.SplashActivity;
import vell.bibi.vsigner.config.Conf;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import cn.bmob.v3.BmobUser;

public class SetFragment extends BaseFragment implements OnClickListener{
	
	private Button mLogoutButton;
	private ImageView mOpenNotificationImageView;
	private ImageView mCloseNotificationImageView;
	
	private ImageView mOpenVoiceImageView;
	private ImageView mCloseVoiceImageView;
	
	private ImageView mOpenVibrateImageView;
	private ImageView mCloseVibrateImageView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_set, container, false);
	}

	@Override
	public void initViews() {
		mLogoutButton = (Button) findViewById(R.id.btn_logout);
		mOpenNotificationImageView = (ImageView) findViewById(R.id.iv_open_notification);
		mCloseNotificationImageView = (ImageView) findViewById(R.id.iv_close_notification);
		
		mOpenVoiceImageView = (ImageView) findViewById(R.id.iv_open_voice);
		mCloseVoiceImageView = (ImageView) findViewById(R.id.iv_close_voice);
		
		mOpenVibrateImageView = (ImageView) findViewById(R.id.iv_open_vibrate);
		mCloseVibrateImageView = (ImageView) findViewById(R.id.iv_close_vibrate);
	}

	@Override
	public void initListeners() {
		mLogoutButton.setOnClickListener(this);
		mOpenNotificationImageView.setOnClickListener(this);
		mCloseNotificationImageView.setOnClickListener(this);
		
		mOpenVoiceImageView.setOnClickListener(this);
		mCloseVoiceImageView.setOnClickListener(this);
		
		mOpenVibrateImageView.setOnClickListener(this);
		mCloseVibrateImageView.setOnClickListener(this);
	}

	@Override
	public void initData() {
		updateView();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_open_notification:
			Conf.isNotify = false;
			break;
		case R.id.iv_close_notification:
			Conf.isNotify = true;
			break;
			
		case R.id.iv_open_voice:
			Conf.isNotifySound = false;
			break;
		case R.id.iv_close_voice:
			Conf.isNotifySound = true;
			break;
			
		case R.id.iv_open_vibrate:
			Conf.isNotifyVibrate = false;
			break;
		case R.id.iv_close_vibrate:
			Conf.isNotifyVibrate = true;
			break;
		
		case R.id.btn_logout:
			BmobUser.logOut(mContext);
			startActivity(new Intent(mContext, SplashActivity.class));
			getActivity().finish();
			break;

		default:
			break;
		}
		Conf.save(mContext);
		updateView();
	}
	
	private void updateView() {
		if(Conf.isNotify) {
			mOpenNotificationImageView.setVisibility(View.VISIBLE);
			mCloseNotificationImageView.setVisibility(View.INVISIBLE);
		} else {
			mOpenNotificationImageView.setVisibility(View.INVISIBLE);
			mCloseNotificationImageView.setVisibility(View.VISIBLE);
		}
		
		if(Conf.isNotifySound) {
			mOpenVoiceImageView.setVisibility(View.VISIBLE);
			mCloseVoiceImageView.setVisibility(View.INVISIBLE);
		} else {
			mOpenVoiceImageView.setVisibility(View.INVISIBLE);
			mCloseVoiceImageView.setVisibility(View.VISIBLE);
		}
		
		if(Conf.isNotifyVibrate) {
			mOpenVibrateImageView.setVisibility(View.VISIBLE);
			mCloseVibrateImageView.setVisibility(View.INVISIBLE);
		} else {
			mOpenVibrateImageView.setVisibility(View.INVISIBLE);
			mCloseVibrateImageView.setVisibility(View.VISIBLE);
		}
	}
}
