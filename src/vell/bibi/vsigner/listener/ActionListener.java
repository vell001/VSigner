package vell.bibi.vsigner.listener;

import vell.bibi.vsigner.model.User;

/**
 * 动作监听
 * @author VellBibi
 *
 * @date Jan 14, 2015
 */
public interface ActionListener {
	/**
	 * 新签到请求
	 * @param count
	 */
	public void newSignRequest(int count);
	/**
	 * 新签到回复
	 * @param count
	 */
	public void newSignResponse(int count);
	/**
	 * 新订阅用户
	 * @param user
	 */
	public void newSubscriber(User user);
}
