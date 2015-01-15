package vell.bibi.vsigner.model;

import vell.bibi.vsigner.util.StrUtil;
import android.annotation.SuppressLint;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

@SuppressLint("ShowToast")
public class User extends BmobUser {
	private static final long serialVersionUID = 2729436933566780707L;
	
	private String realname; // 真名
	private String info; // 描述信息
	private String avatar; // 头像
	private String IMSI; // 手机卡唯一识别
	private String phoneNumber; // 电话号码
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	private BmobRelation channels;
	
	public BmobRelation getChannels() {
		return channels;
	}
	public void setChannels(BmobRelation channels) {
		this.channels = channels;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getIMSI() {
		return IMSI;
	}
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
	}
	/**
	 * 判断用户信息是否完整
	 * 判断依据：
	 * 1. 用户名
	 * 2. 姓名
	 * 3. 密码
	 * 4. IMSI
	 * 只要以上数据任何一个为空本用户信息不完整
	 * @return
	 */
	public boolean isComplete() {
		if(StrUtil.isEmpty(getUsername())
				|| StrUtil.isEmpty(getPassword())
				|| StrUtil.isEmpty(getRealname())
				|| StrUtil.isEmpty(getIMSI())
				|| StrUtil.isEmpty(getEmail())) {
			return false;
		} else 
			return true;
	}
}
