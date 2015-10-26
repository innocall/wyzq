package com.dodola.model;

public class DuitangInfo {

	private int height;
	private String albid = ""; // b2id
	private String msg = ""; // 内容
	private String isrc = ""; // 图片
	private String dateTime; // 时间
	private String type; // 类型
	private String nickName; // 昵称
	private String pinglun;  //评论
	private String zan;
	private String username;

	public int getWidth() {
		return 200;
	}

	public String getAlbid() {
		return albid;
	}

	public void setAlbid(String albid) {
		this.albid = albid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getIsrc() {
		return isrc;
	}

	public void setIsrc(String isrc) {
		this.isrc = isrc;
	}

	/*
	 * public int getHeight() { return 150; }
	 */

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPinglun() {
		return pinglun;
	}

	public void setPinglun(String pinglun) {
		this.pinglun = pinglun;
	}

	public String getZan() {
		return zan;
	}

	public void setZan(String zan) {
		this.zan = zan;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * public void setHeight(int height) { this.height = 150; }
	 */

}
