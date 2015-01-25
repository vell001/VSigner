package vell.bibi.vsigner;

import vell.bibi.vsigner.config.Conf;
import vell.bibi.vsigner.service.TimerService;
import vell.bibi.vsigner.view.MessageFragment;
import vell.bibi.vsigner.view.OwnChannelFragment;
import vell.bibi.vsigner.view.SetFragment;
import vell.bibi.vsigner.view.SubscribedChannelFragment;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author VellBibi
 *
 * @date Jan 13, 2015
 */
public class MainActivity extends BaseActivity {
	
	private Fragment[] mFragments;
	private MessageFragment mMessageFragment;
	private OwnChannelFragment mOwnChannelFragment;
	private SubscribedChannelFragment mSubscribedChannelFragment;
	private SetFragment mSetFragment;
	private Button[] mTabs;
	
	private int index;
	private int currentTabIndex;
	
	public ImageView iv_message_tips,iv_own_channel_tips,iv_subscribed_channel_tips;//消息提示

	@Override
	public void setContentView() {
		Log.i(TAG, "MainActivity");
		setContentView(R.layout.activity_main);
		Log.i(TAG, mCurrentUser.getUsername());
	}

	@Override
	public void initViews() {
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_message);
		mTabs[1] = (Button) findViewById(R.id.btn_own_channel);
		mTabs[2] = (Button) findViewById(R.id.btn_subscribed_channel);
		mTabs[3] = (Button) findViewById(R.id.btn_set);
		iv_message_tips = (ImageView) findViewById(R.id.iv_message_tips);
		iv_own_channel_tips = (ImageView) findViewById(R.id.iv_own_channel_tips);
		iv_subscribed_channel_tips = (ImageView) findViewById(R.id.iv_subscribed_channel_tips);
		//把第一个tab设为选中状态
		mTabs[0].setSelected(true);
	}

	@Override
	public void initListeners() {
	}

	@Override
	public void initData() {
		mMessageFragment = new MessageFragment();
		mOwnChannelFragment = new OwnChannelFragment();
		mSubscribedChannelFragment = new SubscribedChannelFragment();
		mSetFragment = new SetFragment();
		mFragments = new Fragment[] {
				mMessageFragment,
				mOwnChannelFragment, 
				mSubscribedChannelFragment, 
				mSetFragment};
		// 添加显示第一个fragment
		getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container, mMessageFragment)
			.add(R.id.fragment_container, mOwnChannelFragment)
			.add(R.id.fragment_container, mSubscribedChannelFragment)
			.add(R.id.fragment_container, mSetFragment)
			.show(mMessageFragment)
			.hide(mOwnChannelFragment)
			.hide(mSubscribedChannelFragment)
			.hide(mSetFragment)
			.commit();
		
		// 启动MessageService
		startService(new Intent(mContext, TimerService.class));
		// 读取配置信息
		Conf.load(mContext);
	}

	
	/**
	 * 界面切换
	 * @param view
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_message:
			index = 0;
			iv_message_tips.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_own_channel:
			index = 1;
			iv_own_channel_tips.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_subscribed_channel:
			index = 2;
			iv_subscribed_channel_tips.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_set:
			index = 3;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(mFragments[currentTabIndex]);
			if (!mFragments[index].isAdded()) {
				trx.add(R.id.fragment_container, mFragments[index]);
			}
			trx.show(mFragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		//把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}
}
