package vell.bibi.vsigner;

import java.io.Serializable;
import java.util.List;

import vell.bibi.vsigner.adapter.BaseAdapterHelper;
import vell.bibi.vsigner.adapter.QuickAdapter;
import vell.bibi.vsigner.config.Constants;
import vell.bibi.vsigner.model.Channel;
import vell.bibi.vsigner.model.ChannelSigner;
import vell.bibi.vsigner.model.User;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout.OnRefreshListener;
import android.content.Intent;
import android.widget.ListView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class ShowSignerActivity extends BaseActivity {

	private QuickAdapter<ChannelSigner> mChannelSignerAdapter;
	
	private ListView mSignerListView;
	private PullToRefreshLayout mPullToRefreshLayout;
	private Channel mChannel;
	@Override
	public void setContentView() {
		setContentView(R.layout.activity_show_signer);
	}

	@Override
	public void initViews() {
		mSignerListView = (ListView) findViewById(R.id.lv_signer);
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptrl_signer);
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
		
		if(mChannelSignerAdapter == null) {
			mChannelSignerAdapter = new QuickAdapter<ChannelSigner>(mContext, R.layout.item_signer) {
				@Override
				protected void convert(BaseAdapterHelper helper, ChannelSigner channelSigner) {
					User signer = channelSigner.getSigner();
					helper.setText(R.id.tv_signer_realname, signer.getRealname())
						.setText(R.id.tv_signer_username, signer.getUsername())
						.setText(R.id.tv_sign_address, channelSigner.getAddress())
						.setText(R.id.tv_sign_date, channelSigner.getSignDate().getDate())
						.setText(R.id.tv_signer_phone, signer.getPhoneNumber());
				}
			};
			mSignerListView.setAdapter(mChannelSignerAdapter);
		}
		
		refreshData();
	}
	
	public void refreshData() {
		BmobQuery<ChannelSigner> channelQuery = new BmobQuery<ChannelSigner>();
		channelQuery.addWhereEqualTo(ChannelSigner.CHANNEL_KEY, mChannel.getObjectId());
		channelQuery.addWhereGreaterThanOrEqualTo(ChannelSigner.SIGN_DATE_KEY, mChannel.getStartSignDate());
		channelQuery.include(String.format("%1$s,%2$s", 
				ChannelSigner.SIGNER_KEY,
				ChannelSigner.CHANNEL_KEY));
		channelQuery.setLimit(Constants.QUERY_MAX_NUMBER);
		channelQuery.order(ChannelSigner.SIGNER_KEY);
		channelQuery.findObjects(mContext, new FindListener<ChannelSigner>() {
			@Override
			public void onSuccess(List<ChannelSigner> channelSigners) {
				mChannelSignerAdapter.clear();
				mChannelSignerAdapter.addAll(channelSigners);
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
			
			@Override
			public void onError(int arg0, String msg) {
				mPullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			}
		});
	}
}
