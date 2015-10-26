package com.lemon95.wyzq.model;

import java.io.Serializable;

import org.json.JSONObject;

import com.starschina.types.Epg;

public class Channel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5387497563802039553L;
	public int videoId;
	public String id;
	public String videoName;
	public String nickName;
	public String videoImg;
	public String shareImgUrl;
	private Epg next;  
	private Epg exp; //当前节目单

	public static Channel parseChannel(JSONObject jObjChannel) {
		Channel ch = null;
		if (jObjChannel != null) {
			ch = new Channel();
			ch.videoId = jObjChannel.optInt("videoId");
			ch.id = jObjChannel.optString("id");
			ch.videoName = jObjChannel.optString("videoName");
			ch.nickName = jObjChannel.optString("nickName");
			ch.videoImg = jObjChannel.optString("videoImage");
			ch.shareImgUrl = jObjChannel.optString("shareImage");
		}
		return ch;
	}

	public int getVideoId() {
		return videoId;
	}

	public void setVideoId(int videoId) {
		this.videoId = videoId;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoImg() {
		return videoImg;
	}

	public void setVideoImg(String videoImg) {
		this.videoImg = videoImg;
	}

	public String getShareImgUrl() {
		return shareImgUrl;
	}

	public void setShareImgUrl(String shareImgUrl) {
		this.shareImgUrl = shareImgUrl;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Epg getNext() {
		return next;
	}

	public void setNext(Epg next) {
		this.next = next;
	}

	public Epg getExp() {
		return exp;
	}

	public void setExp(Epg exp) {
		this.exp = exp;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
