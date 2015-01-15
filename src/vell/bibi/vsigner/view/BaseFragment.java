package vell.bibi.vsigner.view;

import cn.bmob.v3.BmobUser;
import vell.bibi.vsigner.BaseActivity;
import vell.bibi.vsigner.model.User;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseFragment extends Fragment {

	protected Context mContext;
	protected LayoutInflater mInflater;
	protected User mCurrentUser;
	protected BaseActivity mBaseActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		
		mContext = this.getActivity();
		mCurrentUser = BmobUser.getCurrentUser(mContext, User.class);
		mInflater = LayoutInflater.from(getActivity());
		mBaseActivity = (BaseActivity) getActivity();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initViews();
		initListeners();
		initData();
	}
	/**
	 * 初始化布局文件中的控件
	 */
	public abstract void initViews();

	/**
	 * 初始化控件的监听
	 */
	public abstract void initListeners();
	
	/** 进行数据初始化
	  * initData
	  */
	public abstract void initData();
	
	public View findViewById(int paramInt) {
		return getView().findViewById(paramInt);
	}
}
