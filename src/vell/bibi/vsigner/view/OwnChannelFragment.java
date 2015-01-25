package vell.bibi.vsigner.view;

import java.util.List;

import vell.bibi.vsigner.CreateOwnChannelActivity;
import vell.bibi.vsigner.OwnChannelDetailActivity;
import vell.bibi.vsigner.R;
import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout.OnRefreshListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
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
	
	private QuickAdapter<Channel> mOwnChannelAdapter;
	
	private ListView mOwnChannelListView;
	private ImageButton mCreateOwnChannelImageButton;
	
	private PullToRefreshLayout mPullToRefreshLayout;
	
	// 双击事件记录最近一次点击的位置
	private int lastClickPos;
	// 双击事件记录最近一次点击的时间
	private long lastClickTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_own_channel, container, false);
	}

	@Override
	public void initViews() {
		mOwnChannelListView = (ListView) findViewById(R.id.lv_own_channel);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptrl_own_channel);
		mCreateOwnChannelImageButton = (ImageButton) findViewById(R.id.ib_create_own_channel);
	}

	@Override
	public void initListeners() {
		mCreateOwnChannelImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
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
				mPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		});
		
		mOwnChannelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int pos,
					final long id) {
				if (lastClickPos == pos &&
						Math.abs(lastClickTime-System.currentTimeMillis()) < Constants.DOUBLE_CLICK_TIME) {
					lastClickPos = -1;
					lastClickTime = 0;
				} else {
					lastClickPos = pos;
					lastClickTime = System.currentTimeMillis();
					new Handler().postDelayed(new Runnable(){
					    public void run() {
					    	if(lastClickTime == 0) {
					    		System.out.println("double click");
					    	} else {
					    		itemSingleClick(parent, view, pos, id);
							}
					    }
					 }, Constants.DOUBLE_CLICK_TIME);
				}
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
						.setText(R.id.tv_manager_name, String.format(getString(R.string.user_name_format), 
								channel.getManager().getRealname(),
								channel.getManager().getUsername()))
						.setText(R.id.tv_channel_info, channel.getInfo())
						.setText(R.id.tv_channel_manager_phone, channel.getManager().getPhoneNumber())
						.setText(R.id.tv_channel_updated_time, channel.getUpdatedAt());
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
		
		mBaseActivity.registerReceiver(new OwnChannelBroadcastRecevier(), new IntentFilter(OwnChannelBroadcastRecevier.ACTION));
	}
	
	public void refreshData() {
		BmobQuery<Channel> channelQuery = new BmobQuery<Channel>();
		channelQuery.addWhereEqualTo(Channel.MANAGER_KEY, mCurrentUser.getObjectId());
		channelQuery.include(Channel.MANAGER_KEY);
		channelQuery.setLimit(Constants.QUERY_MAX_NUMBER);
		channelQuery.order("-" + Channel.IS_ACTIVE_KEY + ",-" + Constants.UPDATED_AT_KEY);
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
		TipsDialog tipsDialog = new TipsDialog(mContext, msg, getString(R.string.ok), getString(R.string.cancel));
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
						new TipsDialog(mContext, getString(R.string.server_update_error_tips) + ": " + msg, getString(R.string.ok)).show();
						mBaseActivity.hideProgressDialog();
					}
				});
			}
		});
		
		tipsDialog.show();
	}
	
	/**
	 * ListView 单击事件
	 * @param parent
	 * @param view
	 * @param pos
	 * @param id
	 */
	private void itemSingleClick(AdapterView<?> parent, View view, final int pos,
			long id) {
		Intent intent = new Intent(mContext, OwnChannelDetailActivity.class);
		intent.putExtra(Constants.CHANNEL_KEY, mOwnChannelAdapter.getItem(pos));
		startActivity(intent);
	}
	
	/**
	 * 消息接收器
	 * @author VellBibi
	 *
	 * @date Jan 23, 2015
	 */
	public class OwnChannelBroadcastRecevier extends BroadcastReceiver {
		public static final String ACTION = "vell.bibi.vsigner.intent.action.ownChannelChanged";
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshData();
		}
	}
}
