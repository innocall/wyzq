package com.lemon95.wyzq.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {
	
	public static boolean isPhone(String phone) {
		Pattern pattern = Pattern.compile("^13[0-9]{9}|14[0-9]{9}|15[0-9]{9}|16[0-9]{9}|17[0-9]{9}|18[0-9]{9}$");
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            return true;
        }
        return false;
	}
	
	public static boolean isSN(String content) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
        Matcher matcher = pattern.matcher(content);
        if (matcher.matches()) {
            return true;
        }
        return false;
	}
	
	public static String filterStr(String str) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern pat = Pattern.compile(regEx);
		Matcher m1 = pat.matcher(str);
		return m1.replaceAll("").trim();
	}
	
	public static String ummFormat(String umm) {
		String str = "";
		str = umm.replaceAll("UUID:", "").replaceAll("Major:", "").replaceAll("Minor:", "").replace(' ', '$');
		return str;
	}
	
	public static String nameNick(String name) {
		if(!StringUtils.isBlank(name)) {
			String lastStr = name.substring(name.length()-1, name.length());
			name = name.replace(lastStr, "*");
		}
		return name;
	}
	
	
}
