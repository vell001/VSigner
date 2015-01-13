package vell.bibi.vsigner.model;

import cn.bmob.v3.BmobObject;

/**
 * 保存订阅关系
 * @author VellBibi
 *
 * @date Jan 11, 2015
 */
public class UserToChannel extends BmobObject{
	private static final long serialVersionUID = 6200104359246670245L;
	private User user;
	private Channel channel;
	public UserToChannel(User user, Channel channel) {
		this.user = user;
		this.channel = channel;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
}
