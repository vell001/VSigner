package vell.bibi.vsigner;

import java.util.List;

import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.util.StrUtil;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SearchChannelActivity extends BaseActivity {

	private EditText mSearchChannelEditText;
	
	private QuickAdapter<Channel> mSearchedChannelAdapter;
	private ListView mSearchedChannelListView;
	
	private boolean isSearchingName = false;
	private boolean isSearchingInfo = false;
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_search_channel);
	}

	@Override
	public void initViews() {
		mSearchChannelEditText = (EditText) findViewById(R.id.et_search_channel);
		mSearchedChannelListView = (ListView) findViewById(R.id.lv_searched_channel);
	}

	@Override
	public void initListeners() {
		mSearchChannelEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence text,  int start, int lengthBefore, int lengthAfter) {
				refreshData(text.toString());
			}
			@Override
			public void beforeTextChanged(CharSequence text, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		
		mSearchedChannelListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				Intent intent = new Intent(mContext, SubscribeChannelDetail.class);
				intent.putExtra(Constants.CHANNEL_KEY, mSearchedChannelAdapter.getItem(pos));
				startActivity(intent);
				finish();
			}
		});
	}
	
	@Override
	public void initData() {
		if(mSearchedChannelAdapter == null) {
			mSearchedChannelAdapter = new QuickAdapter<Channel>(mContext, R.layout.item_channel) {
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
			mSearchedChannelListView.setAdapter(mSearchedChannelAdapter);
		}
	}
	
	public void refreshData(String keyWord) {
		mSearchedChannelAdapter.clear();
		if(isSearchingName || isSearchingInfo || StrUtil.isEmpty(keyWord)) {
			return;
		}
		// 按频道名搜索
		isSearchingName = true;
		BmobQuery<Channel> channelNameQuery = new BmobQuery<Channel>();
		channelNameQuery.addWhereContains(Channel.NAME_KEY, keyWord);
		channelNameQuery.include(Channel.MANAGER_KEY);
		channelNameQuery.setLimit(10);
		channelNameQuery.order("-" + Channel.IS_ACTIVE_KEY + ",-" + Constants.UPDATED_AT_KEY);
		channelNameQuery.findObjects(mContext, new FindListener<Channel>() {
			@Override
			public void onSuccess(List<Channel> channels) {
				mSearchedChannelAdapter.addAll(channels);
				isSearchingName = false;
			}
			
			@Override
			public void onError(int arg0, String msg) {
				ShowToast(getString(R.string.server_query_error_tips) + ": " + msg);
				isSearchingName = false;
			}
		});
		
		// 按频道信息搜索
		isSearchingInfo = true;
		BmobQuery<Channel> channelInfoQuery = new BmobQuery<Channel>();
		channelInfoQuery.addWhereContains(Channel.INFO_KEY, keyWord);
		channelInfoQuery.include(Channel.MANAGER_KEY);
		channelInfoQuery.setLimit(10);
		channelInfoQuery.order("-" + Channel.IS_ACTIVE_KEY + ",-" + Constants.UPDATED_AT_KEY);
		channelInfoQuery.findObjects(mContext, new FindListener<Channel>() {
			@Override
			public void onSuccess(List<Channel> channels) {
				for (Channel channel : channels) {
					if(!mSearchedChannelAdapter.contains(channel)) {
						mSearchedChannelAdapter.add(channel);
					}
				}
				isSearchingInfo = false;
			}
			
			@Override
			public void onError(int arg0, String msg) {
				ShowToast(getString(R.string.server_query_error_tips) + ": " + msg);
				isSearchingInfo = false;
			}
		});
	}

}
