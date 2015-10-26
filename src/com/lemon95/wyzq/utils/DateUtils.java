package com.lemon95.wyzq.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.StringUtils;

public class DateUtils {
	public static Long dateString2Long(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date thisDate = null;
		try {
			thisDate = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return new Long(thisDate.getTime());
	}

	public static Long dateTimeString2Long(String date, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		Date thisDate = null;
		try {
			thisDate = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return new Long(thisDate.getTime());
	}

	public static Long dateTimeString2Long(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date thisDate = null;
		try {
			thisDate = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return new Long(thisDate.getTime());
	}

	public static Long dateTimeString2LongNotss(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date thisDate = null;
		try {
			thisDate = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return new Long(thisDate.getTime());
	}

	public static Long dateTime2Long(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String thisDate = null;
		long dateTime = 0;
		try {
			thisDate = format.format(date);
		} catch (Exception e) {
			return 0L;
		}
		return dateTime;
	}

	public static Long dateTime2LongNotS(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String thisDate = null;
		long dateTime = 0;
		try {
			thisDate = format.format(date);
			if (thisDate != null) {
				dateTime = dateTimeString2Long(thisDate);
			}
		} catch (Exception e) {
			return 0L;
		}
		return dateTime;
	}
	
	public static Long dateTime3LongNotS(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String thisDate = null;
		long dateTime = 0;
		try {
			thisDate = format.format(date);
		} catch (Exception e) {
			return 0L;
		}
		return dateTime;
	}

	public static Long dateTime2Long(Date date, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		String thisDate = null;
		long dateTime = 0;
		try {
			thisDate = format.format(date);
			if (thisDate != null) {
				dateTime = dateTimeString2Long(thisDate, formatString);
			}
		} catch (Exception e) {
			return 0L;
		}
		return dateTime;
	}

	public static Date stringTime2Date(String date) {
		long longDate = dateTimeString2Long(date);
		return new Date(longDate);
	}

	public static Date stringTime2DateNotss(String date) {
		long longDate = dateTimeString2LongNotss(date);
		return new Date(longDate);
	}

	public static Date stringDay2Date(String date) {
		long longDate = dateString2Long(date);
		return new Date(longDate);
	}

	public static String dateTime2String(Date date) {
		long longdate = dateTime2Long(date);
		return long2TimeString(longdate);
	}

	
	public static String dateTime2StringNotS(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String thisDate = null;
		try {
			thisDate = format.format(date);
		} catch (Exception e) {
			return "";
		}
		return thisDate;
	}

	/**
	 * 日期转换为去除秒后的字符
	 * 
	 * @param date
	 *            要转换的日期
	 * @return 转换后的
	 */

	public static String dateTime2String(Date date, String formatString) {
		long time = dateTime2Long(date, formatString);
		return long2TimeString(time, formatString);
	}

	public static String dateTimeNotS2String(Date date) {
		String stringdate = dateTime2String(date);
		long stringDate = dateTimeNotSString2Long(stringdate);
		return long2TimeStringNotS(stringDate);
	}

	public static Date stringTimeNotS2Date(String date) {
		long longDate = dateTimeNotSString2Long(date);
		return new Date(longDate);
	}

	public static Long dateTimeNotSString2Long(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date thisDate = null;
		try {
			thisDate = format.parse(date);
		} catch (ParseException e) {
			return null;
		}
		return new Long(thisDate.getTime());
	}

	public static String long2DateString(Long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (time != null) {
			if (time.longValue() != 0) {
				return format.format(new Date(time.longValue()));
			}
		}
		return null;
	}

	public static String long2MonthString(Long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		if (time != null) {
			if (time.longValue() != 0) {
				return format.format(new Date(time.longValue()));
			}
		}
		return null;
	}

	public static String long2DateTimeString(Long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		if (time != null) {
			if (time.longValue() != 0) {
				return format.format(new Date(time.longValue()));
			}
		}
		return null;
	}

	public static String long2TimeString(Long time, String formatString) {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		if (time != null) {
			if (time.longValue() != 0) {
				return format.format(new Date(time.longValue()));
			}
		}
		return null;
	}

	public static String long2TimeString(Long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (time != null) {
			if (time.longValue() != 0) {
				return format.format(new Date(time.longValue()));
			}
		}
		return null;
	}

	public static String long2TimeStringNotS(Long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (time != null) {
			if (time.longValue() != 0) {
				return format.format(new Date(time.longValue()));
			}
		}
		return null;
	}

	/**
	 * 月份左右移动某几个月
	 * 
	 * @param time
	 * @param step
	 * @return
	 */
	public static long getMoveMonthDate(long time, int step) {
		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(time);
		end.roll(Calendar.MONTH, step);
		long endTime = end.getTimeInMillis();
		return endTime;
	}

	/**
	 * 时间左右移动几年
	 */
	public static long getMoveYearDate(long time, int step) {
		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(time);
		end.roll(Calendar.YEAR, step);
		long endTime = end.getTimeInMillis();
		return endTime;
	}

	/**
	 * 某时间左右移动某几天
	 * 
	 * @param time
	 * @param step
	 * @return
	 */
	public static long getEndValidDate4Day(long time, int step) {
		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(time);
		end.add(Calendar.DATE, step);
		long endTime = end.getTimeInMillis();
		return endTime;
	}

	public static Date getEndValidDate4Day(Date date, int step) {
		Calendar end = Calendar.getInstance();
		end.setTime(date);
		end.add(Calendar.DATE, step);
		return end.getTime();
	}

	public static Long getDayLong2ValidLong(Long day) {
		day = day == null ? new Long(new Date().getTime()) : day;
		String str = long2DateString(day);
		Long result = dateString2Long(str);
		return result;
	}

	/**
	 * 得到本周周一的时间�?
	 * 
	 * @param time
	 * @return
	 */
	public static long getMondayDayOfWeek(long time) {
		time = getDayLong2ValidLong(new Long(time)).longValue();
		Calendar day = new GregorianCalendar();
		day.setTimeInMillis(time);
		day.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
		long validTime = day.getTimeInMillis();
		return validTime;
	}

	/**
	 * 格式化手机时�?
	 * 
	 * @param str
	 *            要格式的时间
	 * @return 格式后的时间
	 */
	public static String phoneDateFormat(String str) {
		if (str.equals("")) {
			return str;
		} else {
			str = str.replaceAll("[^0-9:-]", " ");
		}
		return str;
	}

	public static String getNewTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}

	public static String getTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(d);
	}

	public static String getDate() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(d);
	}
	
	/**
	 * 获取�?�?�?
	 * @param date
	 * @param step 0 �? 1�? 2�?
	 * @return
	 */
	public static int getDate(String date,int step) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = GregorianCalendar.getInstance();
		try {
		calendar.setTime(sdf.parse(date));
		} catch (ParseException e) {
		}	     
		if(step == 0) {
			return calendar.get(Calendar.YEAR);
		} else if (step == 1) {
			return calendar.get(Calendar.MONTH);
		} else if (step == 2) {
			return calendar.get(Calendar.DATE);
		}
		return 0;
	}
	
	public static String getStartYear(String str) {
		long d = dateTimeString2Long(str);
		return long2DateString(d);
	}
	
	public static String getStartDate(Date date,int step) {
		Long time = dateTime2Long(date,"yyyy-MM-dd");
		long end = getMoveMonthDate(time,step);
		return long2DateString(end);
	}
	
	/* 获取当前�?*/
	public static String getYear() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		return new Integer(year) + "";
	}

	public static String formatDate(String date) {
		int len = date.length();
		String year = "";
		String mothd = "";
		String day = "";
		if (len < 5) {
			return date;
		} else if (len == 5) {
			year = date.substring(0, 4);
			mothd = date.substring(4, date.length());
			return (year + "-0" + mothd);
		} else if (len == 6) {
			year = date.substring(0, 4);
			mothd = date.substring(4, date.length());
			return (year + "-" + mothd);
		} else if (len == 7) {
			year = date.substring(0, 4);
			mothd = date.substring(4, date.length() - 1);
			day = date.substring(date.length() - 1, date.length());
			return (year + "-" + mothd + "-0" + day);
		} else {
			year = date.substring(0, 4);
			mothd = date.substring(4, 6);
			day = date.substring(6, 8);
			return (year + "-" + mothd + "-" + day);
		}
	}

}
