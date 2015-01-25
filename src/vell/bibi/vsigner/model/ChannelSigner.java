package vell.bibi.vsigner.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class ChannelSigner extends BmobObject {
	private static final long serialVersionUID = -6217101121855641519L;
	public static final String TABLE_NAME= "ChannelSigner";
	public static final String SIGN_DATE_KEY= "signDate";
	public static final String SIGNER_KEY= "signer";
	public static final String CHANNEL_KEY= "channel";
	
	private User signer;
	private Channel channel;
	
	private BmobDate signDate;
	private BmobGeoPoint signGeoPoint;
	private String address;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public User getSigner() {
		return signer;
	}
	public void setSigner(User signer) {
		this.signer = signer;
	}
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public BmobDate getSignDate() {
		return signDate;
	}
	public void setSignDate(BmobDate signDate) {
		this.signDate = signDate;
	}
	public BmobGeoPoint getSignGeoPoint() {
		return signGeoPoint;
	}
	public void setSignGeoPoint(BmobGeoPoint signGeoPoint) {
		this.signGeoPoint = signGeoPoint;
	}
}
