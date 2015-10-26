package com.lemon95.wyzq.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 接口返回数据
 * 
 * @author wuxiaotie
 *
 */
public class Result {
	private String rows; // 行数
	private String message; // 结果说明
	private String url;
	private String fldtotalRecord; // 总条数
	private String fldTotalPage; // 总页数
	private Map<String, String> tab1 = new HashMap<String, String>();
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private List<Map<String, ImageViewModel>> listImage = new LinkedList<Map<String, ImageViewModel>>();

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFldtotalRecord() {
		return fldtotalRecord;
	}

	public void setFldtotalRecord(String fldtotalRecord) {
		this.fldtotalRecord = fldtotalRecord;
	}

	public String getFldTotalPage() {
		return fldTotalPage;
	}

	public void setFldTotalPage(String fldTotalPage) {
		this.fldTotalPage = fldTotalPage;
	}

	public Map<String, String> getTab1() {
		return tab1;
	}

	public void setTab1(Map<String, String> tab1) {
		this.tab1 = tab1;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Map<String, String>> getList() {
		return list;
	}

	public void setList(List<Map<String, String>> list) {
		this.list = list;
	}

	public List<Map<String, ImageViewModel>> getListImage() {
		return listImage;
	}

	public void setListImage(List<Map<String, ImageViewModel>> listImage) {
		this.listImage = listImage;
	}

}
