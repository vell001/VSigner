package vell.bibi.vsigner.view;

import java.util.List;

import vell.bibi.vsigner.CreateOwnChannelActivity;
import vell.bibi.vsigner.R;
import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout.OnRefreshListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class OwnChannelFragment extends BaseFragment{
	public static final int REFRESH_ID = 0020; // 刷新ID
	
	private ImageButton mCreateOwnChannelImageButton;
	
	private QuickAdapter<Channel> mOwnChannelAdapter;
	
	private ListView mOwnChannelListView;
	
	private PullToRefreshLayout mPullToRefreshLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_own_channel, container, false);
	}

	@Override
	public void initViews() {
		mCreateOwnChannelImageButton = (ImageButton) findViewById(R.id.ib_create_own_channel);
		mOwnChannelListView = (ListView) findViewById(R.id.lv_own_channel);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.rv_own_channel);
	}

	@Override
	public void initListeners() {
		mCreateOwnChannelImageButton.setOnClickListener(new OnClickListener() { // 创建新频道
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, CreateOwnChannelActivity.class));
			}
		});
		
		mPullToRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
				refreshData();
			}
			
			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			}
		});
		
		mOwnChannelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
//				Channel channel = mOwnChannelAdapter.getItem(pos);
				System.out.println("onItemClick" + id);
			}
		});
		
		// 长按修改签到isActive属性
		mOwnChannelListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos,
					long id) {
				changeIsActive(mOwnChannelAdapter.getItem(pos));
				return true;
			}
		});
	}

	@Override
	public void initData() {
		if(mOwnChannelAdapter == null) {
			mOwnChannelAdapter = new QuickAdapter<Channel>(mContext, R.layout.item_channel) {
				@Override
				protected void convert(BaseAdapterHelper helper, Channel channel) {
					helper.setText(R.id.tv_channel_name, channel.getName())
						.setText(R.id.tv_manager_name, String.format(getString(R.string.manager_name_format), 
								channel.getManager().getRealname(),
								channel.getManager().getUsername()))
						.setText(R.id.tv_channel_info, channel.getInfo())
						.setText(R.id.tv_phone, channel.getManager().getPhoneNumber())
						.setText(R.id.tv_time, channel.getUpdatedAt());
					if(channel.isActive()) { // 正在签到
						helper.setBackgroundColor(R.id.ll_channel_item, getResources().getColor(R.color.channel_is_active_bg))
							.setVisible(R.id.tv_is_active, true);
					} else {
						helper.setBackgroundColor(R.id.ll_channel_item, getResources().getColor(R.color.channel_not_active_bg))
							.setVisible(R.id.tv_is_active, false);
					}
				}
			};
			mOwnChannelListView.setAdapter(mOwnChannelAdapter);
		}
		
		refreshData();
	}
	
	public void refreshData() {
		BmobQuery<Channel> channelQuery = new BmobQuery<Channel>();
		channelQuery.addWhereEqualTo(Channel.MANAGER_KEY, mCurrentUser.getObjectId());
		channelQuery.include(Channel.MANAGER_KEY);
		channelQuery.setLimit(Constants.QUERY_MAX_NUMBER);
		channelQuery.order("-" + Constants.UPDATED_AT_KEY);
		channelQuery.order("-" + Channel.IS_ACTIVE_KEY);
		channelQuery.findObjects(mContext, new FindListener<Channel>() {
			@Override
			public void onSuccess(List<Channel> channels) {
				mOwnChannelAdapter.clear();
				mOwnChannelAdapter.addAll(channels);
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
			@Override
			public void onError(int arg0, String msg) {
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			}
		});
	}
	
	/**
	 * 改变channel的isActive属性
	 * @param channel
	 */
	private void changeIsActive(final Channel channel) {
		String msg;
		if (channel.isActive()) {
			msg = String.format(getString(R.string.whether_close_sign_format), channel.getName());
		} else {
			msg = String.format(getString(R.string.whether_open_sign_format), channel.getName());
		}
		TipsDialog tipsDialog = new TipsDialog(mContext, msg, getString(R.string.ok_btn), getString(R.string.cancel_btn));
		// 按下确认按钮
		tipsDialog.SetOnSuccessListener(new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (channel.isActive()) {
					channel.setActive(false);
				} else {
					channel.setActive(true);
				}
				mBaseActivity.showProgressDialog(getString(R.string.request_server));
				channel.update(mContext, new UpdateListener() {
					@Override
					public void onSuccess() {
						refreshData();
						mBaseActivity.hideProgressDialog();
					}
					@Override
					public void onFailure(int arg0, String msg) {
						new TipsDialog(mContext, getString(R.string.server_update_error_tips) + ": " + msg, getString(R.string.ok_btn)).show();
						mBaseActivity.hideProgressDialog();
					}
				});
			}
		});
		
		tipsDialog.show();
	}
}
