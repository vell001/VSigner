package vell.bibi.vsigner.view;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import vell.bibi.vsigner.LocationActivity;
import vell.bibi.vsigner.R;
import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.model.ChannelSubscriber;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout.OnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessageFragment extends BaseFragment{

	private PullToRefreshLayout mPullToRefreshLayout;
	
	private QuickAdapter<Channel> mChannelAdapter;
	private ListView mChannelListView;
	
	// 双击事件记录最近一次点击的位置
	private int lastClickPos;
	// 双击事件记录最近一次点击的时间
	private long lastClickTime;
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
	}

	@Override
	public void initData() {
		if(mChannelAdapter == null) {
			mChannelAdapter = new QuickAdapter<Channel>(mContext, R.layout.item_channel) {
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
			mChannelListView.setAdapter(mChannelAdapter);
		}
		
		refreshData();
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
		});
	}
	
	/**
	 * ListView 单击事件
	 * @param parent
	 * @param view
	 * @param pos
	 * @param id
	 */
	private void itemSingleClick(AdapterView<?> parent,
			View view, int pos, long id) {
		Intent intent = new Intent(mContext, LocationActivity.class);
		intent.putExtra(Constants.CHANNEL_KEY, mChannelAdapter.getItemId(pos));
		startActivity(intent);
	}
}
