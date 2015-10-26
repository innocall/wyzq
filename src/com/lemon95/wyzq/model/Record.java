package com.lemon95.wyzq.model;

public class Record {
	
	private int type; 
	private String id; //索引位
	private String cardId;  //卡号
	private String doorId;  //门号
	private String inOrOut; //进出
	private String valid; //有效
	private String time; //时间
	private String des; //描述
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getDoorId() {
		return doorId;
	}
	public void setDoorId(String doorId) {
		this.doorId = doorId;
	}
	public String getInOrOut() {
		return inOrOut;
	}
	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	
	

}
