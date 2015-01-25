package vell.bibi.vsigner.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class ChannelSubscriber extends BmobObject {
	private static final long serialVersionUID = 5343225202191841847L;
	public static final String CHANNEL_KEY = "channel";
	public static final String SUBSCRIBER_KEY = "subscriber";
	public static final String SUBSCRIBE_DATE_KEY = "subscribeDate";
	public static final String TABLE_NAME= "ChannelSubscriber";
	
	private Channel channel;
	private User subscriber;
	private BmobDate subscribeDate;
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public User getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(User subscriber) {
		this.subscriber = subscriber;
	}
	public BmobDate getSubscribeDate() {
		return subscribeDate;
	}
	public void setSubscribeDate(BmobDate subscribeDate) {
		this.subscribeDate = subscribeDate;
	}
}
