package vell.bibi.vsigner.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 获取手机信息工具类
 * @author VellBibi
 *
 * @date Jan 8, 2015
 */
public class PhoneStateUtil {
	/**
	 * 获取本机手机号码
	 * @param context
	 * @return
	 */
	public static String getPhoneIMSI(Context context){   
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		String deviceid = tm.getDeviceId(); 
//		String tel = tm.getLine1Number(); 
//		String imei =tm.getSimSerialNumber();
		String imsi =tm.getSubscriberId();
	    return imsi;
	}
}
