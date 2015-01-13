package vell.bibi.vsigner.util;

public class StrUtil {
	public static boolean isEmpty(final String str) {
		if(str == null || str.trim().length() == 0) return true;
		else return false;
	}
	
	public static boolean isEmail(final String str) {
		String EMAIL_REGEX = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
		return str.matches(EMAIL_REGEX);
	}
}
