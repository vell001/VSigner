package vell.bibi.vsigner;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.model.ChannelSubscriber;
import vell.bibi.vsigner.view.SubscribedChannelFragment.SubscribedChannelBroadcastRecevier;
import vell.bibi.vsigner.view.TipsDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class SubscribeChannelDetail extends BaseActivity {
	
	private TextView mChannelNameTextView;
	private TextView mChannelInfoTextView;
	private TextView mManagerNameTextView;
	private Button mSubscribeChannelButton;
	private Channel mChannel;
	
	ChannelSubscriber mChannelSubscriber = null;
	
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_subscribe_channel_detail);
	}

	@Override
	public void initViews() {
		mChannelNameTextView = (TextView) findViewById(R.id.tv_channel_name);
		mChannelInfoTextView = (TextView) findViewById(R.id.tv_channel_info);
		mManagerNameTextView = (TextView) findViewById(R.id.tv_manager_name);
		mSubscribeChannelButton = (Button) findViewById(R.id.btn_subscribe_channel);
		mSubscribeChannelButton.setEnabled(false);
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
		
		showProgressDialog(getString(R.string.server_query_tips));
		BmobQuery<ChannelSubscriber> channelSubscriberQuery = new BmobQuery<ChannelSubscriber>();
		channelSubscriberQuery.addWhereEqualTo(ChannelSubscriber.SUBSCRIBER_KEY, mCurrentUser.getObjectId());
		channelSubscriberQuery.addWhereEqualTo(ChannelSubscriber.CHANNEL_KEY, mChannel.getObjectId());
		channelSubscriberQuery.setLimit(Constants.QUERY_MAX_NUMBER);
		channelSubscriberQuery.order("-" + Constants.UPDATED_AT_KEY);
		channelSubscriberQuery.findObjects(mContext, new FindListener<ChannelSubscriber>() {
			@Override
			public void onSuccess(List<ChannelSubscriber> channelSubscribers) {
				mSubscribeChannelButton.setEnabled(true);
				hideProgressDialog();
				if(channelSubscribers.size() > 0) {
					mSubscribeChannelButton.setText(R.string.already_subscribed);
					mChannelSubscriber = channelSubscribers.get(0);
				} else {
					mSubscribeChannelButton.setText(R.string.subscribe_channel);
				}
			}

			@Override
			public void onError(int arg0, String msg) {
				mSubscribeChannelButton.setEnabled(false);
			}
		});
	}
	
	public void btn_subscribe_channel_onclick(View v) {
		showProgressDialog(getString(R.string.server_query_tips));
		if(mChannelSubscriber == null) { // 订阅
			mChannelSubscriber = new ChannelSubscriber();
			mChannelSubscriber.setChannel(mChannel);
			mChannelSubscriber.setSubscriber(mCurrentUser);
			mChannelSubscriber.setSubscribeDate(new BmobDate(new Date(System.currentTimeMillis())));
			
			mChannelSubscriber.save(mContext, new SaveListener() {
				@Override
				public void onSuccess() {
					hideProgressDialog();
					TipsDialog tipsDialog = new TipsDialog(mContext, getString(R.string.subscribe_success), getString(R.string.ok));
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
					ShowToast(getString(R.string.server_query_error_tips) + ": " + msg);
				}
			});
		} else { // 取消订阅
			mChannelSubscriber.delete(mContext, new DeleteListener() {
				@Override
				public void onSuccess() {
					hideProgressDialog();
					TipsDialog tipsDialog = new TipsDialog(mContext, getString(R.string.unsubscribe_success), getString(R.string.ok));
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
					ShowToast(getString(R.string.server_query_error_tips) + ": " + msg);
				}
			});
		}
	}
	
	public void btn_cancel_onclick(View v) {
		finish();
	}
	
	@Override
	public void finish() {
		super.finish();
		sendBroadcast(new Intent(SubscribedChannelBroadcastRecevier.ACTION));
	}
}
