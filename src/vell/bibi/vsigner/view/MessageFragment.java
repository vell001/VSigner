package vell.bibi.vsigner.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import vell.bibi.vsigner.LocationActivity;
import vell.bibi.vsigner.MainActivity;
import vell.bibi.vsigner.R;
import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout.OnRefreshListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;

public class MessageFragment extends BaseFragment{

	private PullToRefreshLayout mPullToRefreshLayout;
	
	private QuickAdapter<Channel> mChannelAdapter;
	private ListView mChannelListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message, container, false);
	}

	@Override
	public void initViews() {
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.rv_message);
		mChannelListView = (ListView) findViewById(R.id.lv_messages);
	}

	@Override
	public void initListeners() {
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
		
		mChannelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int pos,
					final long id) {
				Intent intent = new Intent(mContext, LocationActivity.class);
				intent.putExtra(Constants.CHANNEL_KEY, mChannelAdapter.getItem(pos));
				startActivity(intent);
			}
		});
	}

	@Override
	public void initData() {
		if(mChannelAdapter == null) {
			mChannelAdapter = new QuickAdapter<Channel>(mContext, R.layout.item_channel) {
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
			mChannelListView.setAdapter(mChannelAdapter);
		}
		
		refreshData();
		
		// 绑定消息接收器
		mBaseActivity.registerReceiver(new MessageBroadcastRecevier(), new IntentFilter(MessageBroadcastRecevier.ACTION));
	}
	
	public void refreshData() {
/*		BmobQuery<ChannelSubscriber> channelSubscriberQuery = new BmobQuery<ChannelSubscriber>();
		channelSubscriberQuery.addWhereEqualTo(ChannelSubscriber.SUBSCRIBER_KEY, mCurrentUser.getObjectId());
		channelSubscriberQuery.include(String.format("%1$s,%2$s", 
				ChannelSubscriber.CHANNEL_KEY, 
				ChannelSubscriber.CHANNEL_KEY + "." + Channel.MANAGER_KEY));
		channelSubscriberQuery.setLimit(Constants.QUERY_MAX_NUMBER);
		channelSubscriberQuery.order("-" + Constants.UPDATED_AT_KEY);
		channelSubscriberQuery.findObjects(mContext, new FindListener<ChannelSubscriber>() {
			@Override
			public void onSuccess(List<ChannelSubscriber> channelSubscribers) {
				mChannelAdapter.clear();
				for (ChannelSubscriber channelSubscriber : channelSubscribers) {
					if(channelSubscriber.getChannel().isActive()) {
						mChannelAdapter.add(channelSubscriber.getChannel());
					}
				}
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
			@Override
			public void onError(int arg0, String msg) {
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			}
		});*/
		
		// 借助云端代码
		AsyncCustomEndpoints ace = new AsyncCustomEndpoints();
		try {
			ace.callEndpoint(mContext, "getNotSignChannelObjectId",new JSONObject("{userObjectId:" + mCurrentUser.getObjectId() + "}"), new CloudCodeListener() {
			    @Override
			    public void onSuccess(Object object) {
		    		String resp = (String) object;
		    		resp = resp.replace("\\\"", "");
		    		resp = resp.trim().substring(1, resp.length()-1);
		    		String[] channelObjectIds = resp.split(",");
		    		List<String> channelObjectIdList = new ArrayList<String>();
		    		for (String string : channelObjectIds) {
		    			channelObjectIdList.add(string.trim());
					}
		    		BmobQuery<Channel> cQuery = new BmobQuery<Channel>();
		    		cQuery.addWhereContainedIn("objectId", channelObjectIdList);
		    		cQuery.include(Channel.MANAGER_KEY);
		    		cQuery.setLimit(Constants.QUERY_MAX_NUMBER);
		    		cQuery.order("-" + Constants.UPDATED_AT_KEY);
		    		cQuery.findObjects(mContext, new FindListener<Channel>() {
		    			@Override
		    			public void onSuccess(List<Channel> channels) {
		    				mChannelAdapter.clear();
		    				mChannelAdapter.addAll(channels);
		    				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
		    			}
		    			
		    			@Override
		    			public void onError(int arg0, String msg) {
		    				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
		    			}
		    		});
			    }
			    @Override
			    public void onFailure(int code, String msg) {
			    	mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			    }
			});
		} catch (JSONException e) {
			e.printStackTrace();
			mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
		}
	}
	
	/**
	 * 消息接收器
	 * @author VellBibi
	 *
	 * @date Jan 23, 2015
	 */
	
	public class MessageBroadcastRecevier extends BroadcastReceiver {
		public static final String ACTION = "vell.bibi.vsigner.intent.action.NotSignChannelCount";
		private int mNotSignChannelCount = 0;
		@Override
		public void onReceive(Context context, Intent intent) {
			MainActivity mainActivity = (MainActivity) mBaseActivity;
			int count = intent.getIntExtra(Constants.COUNT_KEY, 0);
			if(count > 0 && mNotSignChannelCount < count) {
				mainActivity.iv_message_tips.setVisibility(View.VISIBLE);
			}
			mNotSignChannelCount = count;
			refreshData();
		}
	}
	
}
