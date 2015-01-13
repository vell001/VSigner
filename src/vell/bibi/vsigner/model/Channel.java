package vell.bibi.vsigner.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Channel extends BmobObject {
	private static final long serialVersionUID = 700371249133583521L;

	private String name;
	private String info;
	private User manager;
	
	private BmobRelation subscribers;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
