package vell.bibi.vsigner.config;


/**
 * 常量
 * @author VellBibi
 *
 * @date Jan 8, 2015
 */
public class Constants {

	public static final String BmobAPPID = "9056308ca775c87ddd7bd1580f5aa034";// bmob sdk_APPID
	public final static String USERNAME_EXTRA = "username";
	public final static String IMSI_KEY = "IMSI";
	public final static int USERNAME_MIN_LENGTH = 6; // 用户名最小长度
	public final static int PASSWORD_MIN_LENGTH = 6; // 密码最小长度
	public final static long MESSAGE_REFRESH_PERIOD = 60000; // 刷新消息周期
	
	public final static int QUERY_MAX_NUMBER = 300; // 单次最大查询数
	
	public final static String UPDATED_AT_KEY = "updatedAt";

	public final static String CHANNEL_KEY = "channel";
	public final static String COUNT_KEY = "count";
	
	public final static int FLING_MIN_DISTANCE = 50;   
    public final static int FLING_MIN_VELOCITY = 0;
    
    public static final int DOUBLE_CLICK_TIME = 250; // 双击间隔时间
}
