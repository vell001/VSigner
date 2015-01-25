package vell.bibi.vsigner.view;

import java.util.List;

import vell.bibi.vsigner.R;
import vell.bibi.vsigner.SearchChannelActivity;
import vell.bibi.vsigner.SubscribeChannelDetail;
import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.model.ChannelSubscriber;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout.OnRefreshListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SubscribedChannelFragment extends BaseFragment{
	
	private ImageButton mSubscribeChannelImageButton;
	private PullToRefreshLayout mPullToRefreshLayout;
	
	private QuickAdapter<Channel> mSubscribedChannelAdapter;
	
	private ListView mSubscribedChannelListView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_subscribed_channel, container, false);
	}

	@Override
	public void initViews() {
		mSubscribeChannelImageButton = (ImageButton) findViewById(R.id.ib_subscribe_channel);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptrl_subscribed_channel);
		mSubscribedChannelListView = (ListView) findViewById(R.id.lv_subscribed_channel);
	}

	@Override
	public void initListeners() {
		mSubscribeChannelImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, SearchChannelActivity.class));
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
		mSubscribedChannelListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				Intent intent = new Intent(mContext, SubscribeChannelDetail.class);
				intent.putExtra(Constants.CHANNEL_KEY, mSubscribedChannelAdapter.getItem(pos));
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {
		if(mSubscribedChannelAdapter == null) {
			mSubscribedChannelAdapter = new QuickAdapter<Channel>(mContext, R.layout.item_channel) {
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
			mSubscribedChannelListView.setAdapter(mSubscribedChannelAdapter);
		}
		
		refreshData();
		mBaseActivity.registerReceiver(new SubscribedChannelBroadcastRecevier(), new IntentFilter(SubscribedChannelBroadcastRecevier.ACTION));
	}
	
	public void refreshData() {
		BmobQuery<ChannelSubscriber> channelSubscriberQuery = new BmobQuery<ChannelSubscriber>();
		channelSubscriberQuery.addWhereEqualTo(ChannelSubscriber.SUBSCRIBER_KEY, mCurrentUser.getObjectId());
		channelSubscriberQuery.include(String.format("%1$s,%2$s", 
				ChannelSubscriber.CHANNEL_KEY, 
				ChannelSubscriber.CHANNEL_KEY + "." + Channel.MANAGER_KEY));
		channelSubscriberQuery.setLimit(Constants.QUERY_MAX_NUMBER);
		channelSubscriberQuery.order("-" + Constants.UPDATED_AT_KEY);
		channelSubscriberQuery.findObjects(mContext, new FindListener<ChannelSubscriber>() {
			@Override
			public void onSuccess(List<ChannelSubscriber> channelSubscribers) {
				mSubscribedChannelAdapter.clear();
				for (ChannelSubscriber channelSubscriber : channelSubscribers) {
					mSubscribedChannelAdapter.add(channelSubscriber.getChannel());
				}
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
			@Override
			public void onError(int arg0, String msg) {
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			}
		});
	}
	
	/**
	 * 消息接收器
	 * @author VellBibi
	 *
	 * @date Jan 23, 2015
	 */
	public class SubscribedChannelBroadcastRecevier extends BroadcastReceiver {
		public static final String ACTION = "vell.bibi.vsigner.intent.action.subcribedChannelChanged";
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshData();
		}
	}
}
