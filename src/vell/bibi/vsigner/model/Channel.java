package vell.bibi.vsigner.model;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class Channel extends BmobObject {
	private static final long serialVersionUID = 700371249133583521L;
	public final static String TABLE_NAME = "Channel";

	public final static String NAME_KEY = "name";
	public final static String MANAGER_KEY = "manager";
	public final static String IS_ACTIVE_KEY = "isActive";
	public final static String INFO_KEY = "info";
	
	private String name;
	private String info;
	private User manager; // 频道创建者
	private boolean isActive = false; // 是否真正进行签到
	private int signCount = 0; // 当前签到人数
	private BmobDate startSignDate;
	
	public int getSignCount() {
		return signCount;
	}
	public void setSignCount(int signCount) {
		this.signCount = signCount;
	}
	public BmobDate getStartSignDate() {
		return startSignDate;
	}
	public void setStartSignDate(BmobDate startSignDate) {
		this.startSignDate = startSignDate;
	}
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
		if(isActive) { // 设置开始签到时间
			setStartSignDate(new BmobDate(new Date(System.currentTimeMillis()))); 
		} else {
			signCount = 0;
		}
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
	@Override
	public boolean equals(Object o) {
		if(o instanceof Channel) {
			Channel channel = (Channel) o;
			return channel.getObjectId().equals(this.getObjectId());
		} else {
			return false;
		}
	}
}
