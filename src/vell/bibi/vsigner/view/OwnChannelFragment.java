package vell.bibi.vsigner.view;

import java.util.List;

import vell.bibi.vsigner.CreateOwnChannelActivity;
import vell.bibi.vsigner.R;
import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.view.RefreshableView.PullToRefreshListener;
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

public class OwnChannelFragment extends BaseFragment{
	public static final int REFRESH_ID = 0020; // 刷新ID
	
	private ImageButton mCreateOwnChannelImageButton;
	
	private QuickAdapter<Channel> mOwnChannelAdapter;
	
	private ListView mOwnChannelListView;
	
	private RefreshableView mRefreshableView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_own_channel, container, false);
	}

	@Override
	public void initViews() {
		mCreateOwnChannelImageButton = (ImageButton) findViewById(R.id.ib_create_own_channel);
		mOwnChannelListView = (ListView) findViewById(R.id.lv_own_channel);
		mRefreshableView = (RefreshableView) findViewById(R.id.rv_own_channel);
	}

	@Override
	public void initListeners() {
		mCreateOwnChannelImageButton.setOnClickListener(new OnClickListener() { // 创建新频道
			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, CreateOwnChannelActivity.class));
			}
		});
		
		mRefreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				refreshData();
			}
		}, REFRESH_ID);
		
		mOwnChannelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
//				Channel channel = mOwnChannelAdapter.getItem(pos);
				System.out.println("onItemClick" + id);
			}
		});
		
		mOwnChannelListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int pos,
					long id) {
				System.out.println("onItemLongClick" + id);
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
		channelQuery.findObjects(mContext, new FindListener<Channel>() {
			@Override
			public void onSuccess(List<Channel> channels) {
				mOwnChannelAdapter.clear();
				mOwnChannelAdapter.addAll(channels);
				mRefreshableView.finishRefreshing();
			}
			
			@Override
			public void onError(int arg0, String msg) {
				mRefreshableView.finishRefreshing();
			}
		});
	}
}
