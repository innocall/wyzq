package com.lemon95.wyzq.utils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

/**
 *
 */
public class Identity {
	// 位权值数�?
	private static byte[] Wi = new byte[17];
	// 身份证前部分字符�?
	private static final byte fPart = 6;
	// 身份证算法求模关键�?
	private static final byte fMod = 11;
	// 旧身份证长度
	private static final byte oldIDLen = 15;
	// 新身份证长度
	private static final byte newIDLen = 18;
	// 新身份证年份标志
	private static final String yearFlag = "19";
	// 校验码串
	private static final String CheckCode = "10X98765432";
	// �?��的行政区划码
	private static final int minCode = 150000;
	// �?��的行政区划码
	private static final int maxCode = 700000;
	// 旧身份证号码
	// private String oldIDCard="";
	// 新身份证号码
	// private String newIDCard="";
	// 地区及编�?

	// private String Area[][2] =
	private static void setWiBuffer() {
		for (int i = 0; i < Wi.length; i++) {
			int k = (int) Math.pow(2, (Wi.length - i));
			Wi[i] = (byte) (k % fMod);
		}
	}

	// 获取新身份证的最后一�?�?���?
	private static String getCheckFlag(String idCard) {
		int sum = 0;
		// 进行加权求和
		for (int i = 0; i < 17; i++) {
			sum += Integer.parseInt(idCard.substring(i, i + 1)) * Wi[i];
		}
		// 取模运算，得到模�?
		byte iCode = (byte) (sum % fMod);
		return CheckCode.substring(iCode, iCode + 1);
	}

	// 判断串长度的合法�?
	private static boolean checkLength(final String idCard, boolean newIDFlag) {
		boolean right = (idCard.length() == oldIDLen) || (idCard.length() == newIDLen);
		newIDFlag = false;
		if (right) {
			newIDFlag = (idCard.length() == newIDLen);
		}
		return right;
	}

	// 获取时间�?
	private static String getIDDate(final String idCard, boolean newIDFlag) {
		String dateStr = "";
		if (newIDFlag)
			dateStr = idCard.substring(fPart, fPart + 8);
		else
			dateStr = yearFlag + idCard.substring(fPart, fPart + 6);
		return dateStr;
	}

	// 判断时间合法�?
	private static boolean checkDate(final String dateSource) {
		String dateStr = dateSource.substring(0, 4) + "-" + dateSource.substring(4, 6) + "-"
				+ dateSource.substring(6, 8);
		System.out.println(dateStr);
		DateFormat df = DateFormat.getDateInstance();
		df.setLenient(false);
		try {
			Date date = df.parse(dateStr);
			return (date != null);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}

	// 旧身份证转换成新身份证号�?
	public static String getNewIDCard(final String oldIDCard) {
		// 初始化方�?
		Identity.setWiBuffer();
		if (!checkIDCard(oldIDCard)) {
			return oldIDCard;
		}
		String newIDCard = oldIDCard.substring(0, fPart);
		newIDCard += yearFlag;
		newIDCard += oldIDCard.substring(fPart, oldIDCard.length());
		String ch = getCheckFlag(newIDCard);
		newIDCard += ch;
		return newIDCard;
	}

	// 新身份证转换成旧身份证号�?
	public static String getOldIDCard(final String newIDCard) {
		// 初始化方�?
		Identity.setWiBuffer();
		if (!checkIDCard(newIDCard)) {
			return newIDCard;
		}
		String oldIDCard = newIDCard.substring(0, fPart)
				+ newIDCard.substring(fPart + yearFlag.length(), newIDCard.length() - 1);
		return oldIDCard;
	}

	// 判断身份证号码的合法�?
	public static boolean checkIDCard(final String idCard) {
		// 初始化方�?
		Identity.setWiBuffer();
		boolean isNew = false;
		// String message = "";
		if (!checkLength(idCard, isNew)) {
			// message = "ID长度异常";
			return false;
		}
		String idDate = getIDDate(idCard, isNew);
		if (!checkDate(idDate)) {
			// message = "ID时间异常";
			return false;
		}
		if (isNew) {
			String checkFlag = getCheckFlag(idCard);
			String theFlag = idCard.substring(idCard.length() - 1, idCard.length());
			if (!checkFlag.equals(theFlag)) {
				// message = "新身份证校验位异�?;
				return false;
			}
		}
		return true;
	}

	// 获取�?��随机�?�?身份证号�?
	public static String getRandomIDCard(final boolean idNewID) {
		// 初始化方�?
		Identity.setWiBuffer();
		Random ran = new Random();
		String idCard = getAddressCode(ran) + getRandomDate(ran, idNewID) + getIDOrder(ran);
		if (idNewID) {
			String ch = getCheckFlag(idCard);
			idCard += ch;
		}
		return idCard;
	}

	// 产生随机的地区编�?
	private static String getAddressCode(Random ran) {
		if (ran == null) {
			return "";
		} else {
			int addrCode = minCode + ran.nextInt(maxCode - minCode);
			return Integer.toString(addrCode);
		}
	}

	// 产生随机的出生日�?
	private static String getRandomDate(Random ran, boolean idNewID) {
		// TODO Auto-generated method stub
		if (ran == null) {
			return "";
		}
		int year = 0;
		if (idNewID) {
			year = 1900 + ran.nextInt(2007 - 1900);
		} else {
			year = 1 + ran.nextInt(99);
		}
		int month = 1 + ran.nextInt(12);
		int day = 0;
		if (month == 2) {
			day = 1 + ran.nextInt(28);
		} else if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			day = 1 + ran.nextInt(31);
		} else {
			day = 1 + ran.nextInt(30);
		}
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMaximumIntegerDigits(2);
		nf.setMinimumIntegerDigits(2);
		String dateStr = Integer.toString(year) + nf.format(month) + nf.format(day);
		return dateStr;
	}

	// 产生随机的序列号
	private static String getIDOrder(Random ran) {
		// TODO Auto-generated method stub
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMaximumIntegerDigits(3);
		nf.setMinimumIntegerDigits(3);
		if (ran == null) {
			return "";
		} else {
			int order = 1 + ran.nextInt(999);
			return nf.format(order);
		}
	}

	public Identity() {
		setWiBuffer();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String randomID = Identity.getRandomIDCard(true);
		System.out.println("随机身份证：" + randomID);
		/*
		 * String oldID=""; String newID=Identity.getNewIDCard(oldID);
		 * System.out.println("旧身份证�?+oldID); System.out.println("新身份证�?+newID);
		 * String oldCard = Identity.getOldIDCard(newID);
		 * System.out.println("旧身份证�?+oldCard); /* String
		 * dateSource="2000-9-30"; if(id.checkDate(dateSource))
		 * System.out.println("正确时间串："+dateSource); else
		 * System.out.println("错误时间串："+dateSource);
		 * 
		 * 
		 */
	}
}