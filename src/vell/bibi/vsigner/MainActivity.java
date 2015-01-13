package vell.bibi.vsigner;

import vell.bibi.vsigner.view.MessageFragment;
import vell.bibi.vsigner.view.OwnChannelFragment;
import vell.bibi.vsigner.view.SetFragment;
import vell.bibi.vsigner.view.SubscribedChannelFragment;
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
	
	ImageView iv_message_tips,iv_own_channel_tips,iv_subscribed_channel_tips;//消息提示

	@Override
	public void setContentView() {
		Log.i(TAG, "MainActivity");
		setContentView(R.layout.activity_main);
		Log.i(TAG, mCurrentUser.getUsername());
		
		/*BmobQuery<Channel> channelQuery = new BmobQuery<Channel>();
//		channelQuery.include("subscribers");
		// 正确方法，将多对多关联关系当做数组
		channelQuery.addWhereContains("subscribers", mCurrentUser.getObjectId());
		// 尝试过的错误方法之一，此方法只用于一对多或者多对一问题的查询方法
//		channelQuery.addWhereRelatedTo("subscribers", new BmobPointer(mCurrentUser)); 
		channelQuery.findObjects(mContext, new FindListener<Channel>() {
			@Override
			public void onSuccess(List<Channel> channels) {
				for (Channel channel : channels) {
					Log.i(TAG, channel.getName());
				}
			}
			@Override
			public void onError(int arg0, String arg1) {
			}
		});*/
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
	}

	
	/**
	 * 界面切换
	 * @param view
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.btn_message:
			index = 0;
			break;
		case R.id.btn_own_channel:
			index = 1;
			break;
		case R.id.btn_subscribed_channel:
			index = 2;
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
