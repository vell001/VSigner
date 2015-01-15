package vell.bibi.vsigner.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Channel extends BmobObject {
	private static final long serialVersionUID = 700371249133583521L;

	public final static String IS_ACTIVE_KEY = "isActive";
	public final static String SUBSCRIBERS_KEY = "subscribers";
	
	private String name;
	private String info;
	private User manager; // 频道创建者
	private boolean isActive; // 是否真正进行签到
	
	private BmobRelation subscribers;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public User getManager() {
		return manager;
	}
	public void setManager(User manager) {
		this.manager = manager;
	}
	public BmobRelation getSubscribers() {
		return subscribers;
	}
	public void setSubscribers(BmobRelation subscribers) {
		this.subscribers = subscribers;
	}
}
