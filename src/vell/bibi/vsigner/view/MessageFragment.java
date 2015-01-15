package vell.bibi.vsigner.view;

import vell.bibi.vsigner.R;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout;
import vell.bibi.vsigner.view.pullable.PullToRefreshLayout.OnRefreshListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends BaseFragment{

	public static final int REFRESH_ID = 0010; // 刷新ID
	
	private PullToRefreshLayout mPullToRefreshLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message, container, false);
	}

	@Override
	public void initViews() {
		mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.rv_message);
	}

	@Override
	public void initListeners() {
		mPullToRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			}
			
			@Override
			public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			}
		});
	}

	@Override
	public void initData() {
	}
}
