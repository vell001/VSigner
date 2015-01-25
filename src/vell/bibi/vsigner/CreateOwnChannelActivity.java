package vell.bibi.vsigner;

import java.util.List;

import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.util.StrUtil;
import vell.bibi.vsigner.view.TipsDialog;
import vell.bibi.vsigner.view.OwnChannelFragment.OwnChannelBroadcastRecevier;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class CreateOwnChannelActivity extends BaseActivity implements OnFocusChangeListener{

	private TextView mManagerNameTextView;
	private EditText mChannelNameEditText;
	private EditText mChannelInfoEditText;
	
	private Channel mChannel = new Channel();
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_create_own_channel);
	}

	@Override
	public void initViews() {
		mManagerNameTextView = (TextView) findViewById(R.id.tv_manager_name);
		mChannelNameEditText = (EditText) findViewById(R.id.et_channel_name);
		mChannelInfoEditText = (EditText) findViewById(R.id.et_channel_info);
	}

	@Override
	public void initListeners() {
		mChannelNameEditText.setOnFocusChangeListener(this);
		mChannelInfoEditText.setOnFocusChangeListener(this);
	}

	@Override
	public void initData() {
		mManagerNameTextView.setText(String.format(getString(R.string.user_name_format), 
				mCurrentUser.getRealname(), 
				mCurrentUser.getUsername()));
	}

	/**
	 * 创建按钮按下事件
	 * @param v
	 */
	public void btn_create_new_channel_onclick(View v) {
		String channelName = mChannelNameEditText.getText().toString().trim();
		String channelInfo = mChannelInfoEditText.getText().toString().trim();
		
		if(StrUtil.isEmpty(channelName)) {
			ShowToast(getString(R.string.channel_name_hint));
			return;
		}
		if(StrUtil.isEmpty(channelInfo)) {
			ShowToast(getString(R.string.channel_info_hint));
			return;
		}
		
		mChannel.setName(channelName);
		mChannel.setInfo(channelInfo);
		mChannel.setManager(mCurrentUser);
		
		showProgressDialog(getString(R.string.request_server));
		
		BmobQuery<Channel> channelQuery = new BmobQuery<Channel>();
		channelQuery.addWhereEqualTo(Channel.NAME_KEY, channelName);
		channelQuery.addWhereEqualTo(Channel.MANAGER_KEY, mCurrentUser.getObjectId());
		channelQuery.findObjects(mContext, new FindListener<Channel>() {
			@Override
			public void onSuccess(List<Channel> channels) {
				if(channels == null || channels.size() == 0) { // 频道不存在，可以创建
					mChannel.save(mContext, new SaveListener() { // 保存频道信息
						@Override
						public void onSuccess() {
							hideProgressDialog();
							TipsDialog dialogTips = new TipsDialog(mContext, getString(R.string.create_success), getString(R.string.ok));
							dialogTips.SetOnDismissListener(new OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface arg0) {
									finish(); // 创建成功后退出
								}
							});
					    	dialogTips.show();
						}
						
						@Override
						public void onFailure(int code, String msg) {
							hideProgressDialog();
							new TipsDialog(mContext, getString(R.string.create_error) + ": " + msg, getString(R.string.ok)).show();
						}
					});
				} else { // 频道存在
					hideProgressDialog();
					new TipsDialog(mContext, getString(R.string.channel_exist), getString(R.string.ok)).show();
				}
			}
			
			@Override
			public void onError(int code, String msg) {
				hideProgressDialog();
				new TipsDialog(mContext, getString(R.string.create_error) + ": " + msg, getString(R.string.ok)).show();
			}
		});
	}

	@Override
	public void onFocusChange(View view, boolean isFocus) {
		String text = null;
		if(!isFocus) { // 动态验证
			switch (view.getId()) {
			case R.id.et_channel_name:
				text = mChannelNameEditText.getText().toString().trim();
				if(StrUtil.isEmpty(text)) {
					ShowToast(getString(R.string.channel_name_hint));
				}
				break;
			case R.id.et_channel_info:
				text = mChannelInfoEditText.getText().toString().trim();
				if(StrUtil.isEmpty(text)) {
					ShowToast(getString(R.string.channel_info_hint));
				}
			default:
				break;
			}
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
