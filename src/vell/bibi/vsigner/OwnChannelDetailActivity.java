package vell.bibi.vsigner;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.view.OwnChannelFragment.OwnChannelBroadcastRecevier;
import vell.bibi.vsigner.view.TipsDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UpdateListener;

public class OwnChannelDetailActivity extends BaseActivity{

	private TextView mChannelNameTextView;
	private TextView mChannelInfoTextView;
	private TextView mManagerNameTextView;
	private Button mOpenCloseChannelButton;
	private Channel mChannel;
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_own_channel_detail);
	}

	@Override
	public void initViews() {
		mChannelNameTextView = (TextView) findViewById(R.id.tv_channel_name);
		mChannelInfoTextView = (TextView) findViewById(R.id.tv_channel_info);
		mManagerNameTextView = (TextView) findViewById(R.id.tv_manager_name);
		mOpenCloseChannelButton = (Button) findViewById(R.id.btn_open_close_channel);
	}

	@Override
	public void initListeners() {
	}

	@Override
	public void initData() {
		Intent intent = getIntent();
		Serializable serializable = intent.getSerializableExtra(Constants.CHANNEL_KEY);
		if(serializable != null && serializable instanceof Channel) {
			mChannel = (Channel) serializable;
		} else {
			ShowToast(getString(R.string.param_error));
			finish();
		}
		
		updateView(mChannel); // 更新界面
	}

	public void btn_show_signer_onclick(View v) {
		Intent intent = new Intent(mContext, ShowSignerActivity.class);
		intent.putExtra(Constants.CHANNEL_KEY, mChannel);
		startActivity(intent);
	}
	
	public void btn_open_close_channel_onclick(View v) {
		if(mChannel.isActive()) {
			mChannel.setActive(false);
		} else {
			mChannel.setActive(true);
		}
		
		showProgressDialog(getString(R.string.server_query_tips));
		mChannel.update(mContext, new UpdateListener() {
			@Override
			public void onSuccess() {
				hideProgressDialog();
				ShowToast(getString(R.string.server_update_success_tips));
				updateView(mChannel);
			}
			@Override
			public void onFailure(int arg0, String msg) {
				hideProgressDialog();
				ShowToast(getString(R.string.server_update_error_tips) + ": " + msg);
				// 重置isActive
				if(mChannel.isActive()) {
					mChannel.setActive(false);
				} else {
					mChannel.setActive(true);
				}
			}
		});
	}
	
	public void btn_delete_channel_onclick(View v) {
		final TipsDialog tipsDialog = new TipsDialog(mContext, getString(R.string.confirm_delete), getString(R.string.ok), getString(R.string.cancel));
		tipsDialog.SetOnSuccessListener(new OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) { // 确认删除
				showProgressDialog(getString(R.string.server_query_tips));
				AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
				try {
					ace.callEndpoint(mContext, "deleteChannel",new JSONObject("{channelObjectId:" + mChannel.getObjectId() + "}"), new CloudCodeListener() {
					    @Override
					    public void onSuccess(Object object) {
					    	hideProgressDialog();
							ShowToast(getString(R.string.server_update_success_tips));
							finish();
					    }
					    @Override
					    public void onFailure(int code, String msg) {
					    	hideProgressDialog();
							ShowToast(getString(R.string.server_update_error_tips) + ": " + msg);
					    }
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		tipsDialog.show();
	}
	
	/**
	 * 根据channel更新当前界面信息
	 * @param channel
	 */
	private void updateView(Channel channel) {
		mChannelNameTextView.setText(channel.getName());
		mChannelInfoTextView.setText(channel.getInfo());
		mManagerNameTextView.setText(String.format(getString(R.string.user_name_format), 
				channel.getManager().getRealname(), 
				channel.getManager().getUsername()));
		if(channel.isActive()) {
			mOpenCloseChannelButton.setText(R.string.close_channel);
			mOpenCloseChannelButton.setBackgroundResource(R.drawable.btn_logout_selector);
		} else {
			mOpenCloseChannelButton.setText(R.string.open_channel);
			mOpenCloseChannelButton.setBackgroundResource(R.drawable.btn_login_selector);
		}
	}
	
	public void btn_cancel_onclick(View v) {
		finish();
	}
	
	@Override
	public void finish() {
		super.finish();
		sendBroadcast(new Intent(OwnChannelBroadcastRecevier.ACTION));
	}
}
