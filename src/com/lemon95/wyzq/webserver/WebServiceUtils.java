package com.lemon95.wyzq.webserver;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.model.Channel;
import com.lemon95.wyzq.model.ImageViewModel;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.LogUtils;
import com.starschina.types.DChannel;

public class WebServiceUtils {
	
	/**
	 * 根据手机号获取用户信息
	 * @param phone
	 * @return
	 */
	public static Result info(String phone) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.info);  
            soapObject.addProperty("mobile", phone);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.info, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回手机信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) > 0) {
                		JSONArray tab = new JSONArray(json.getString("tab"));
                        resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
                        resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
                        JSONArray tabs = new JSONArray(json.getString("tab1"));
                        Map<String, String> tab1 = new HashMap<String, String>();
                        tab1.put("realname", tabs.getJSONObject(0).getString("realname"));
                        tab1.put("mobile", tabs.getJSONObject(0).getString("mobile"));
                        tab1.put("grade", tabs.getJSONObject(0).getString("grade"));
                        tab1.put("created", tabs.getJSONObject(0).getString("created"));
                        tab1.put("xqid", tabs.getJSONObject(0).getString("xqid"));
                        tab1.put("islive", tabs.getJSONObject(0).getString("islive"));
                        tab1.put("remobile", tabs.getJSONObject(0).getString("remobile"));
                        tab1.put("fanghao", tabs.getJSONObject(0).getString("fanghao"));
                        tab1.put("bianma", tabs.getJSONObject(0).getString("bianma"));
                        tab1.put("rowId", tabs.getJSONObject(0).getString("rowId"));
                        resutl.setTab1(tab1);
                        return resutl;
                	}
                } else {
                	resutl.setRows("-1");
                	resutl.setMessage("获取数据异常,请稍后再试");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("-1");
        	resutl.setMessage("获取数据异常,请稍后再试");
        }
		return resutl;  
	}
	
	/**
	 * 用户登陆
	 * @param phone
	 * @param password
	 * @return
	 */
	public static Result login(String phone,String password) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.login);  
            soapObject.addProperty("mobile", phone);
            soapObject.addProperty("password", DigestUtils.md5Hex(password));
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
  
            // 调用webservice  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.login, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回登陆信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if("0".equals(rows)) {
                		resutl.setMessage("请输入正确的用户名和密码");
                	} else {
                		JSONArray tab = new JSONArray(json.getString("tab"));
                        resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
                        resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
                        JSONArray tabs = new JSONArray(json.getString("tab1"));
                        Map<String, String> tab1 = new HashMap<String, String>();
                        tab1.put("realname", tabs.getJSONObject(0).getString("realname"));
                        tab1.put("mobile", tabs.getJSONObject(0).getString("mobile"));
                        tab1.put("grade", tabs.getJSONObject(0).getString("grade"));
                        tab1.put("created", tabs.getJSONObject(0).getString("created"));
                        tab1.put("xqid", tabs.getJSONObject(0).getString("xqid"));
                        tab1.put("islive", tabs.getJSONObject(0).getString("islive"));
                        tab1.put("remobile", tabs.getJSONObject(0).getString("remobile"));
                        tab1.put("fanghao", tabs.getJSONObject(0).getString("fanghao"));
                        tab1.put("bianma", tabs.getJSONObject(0).getString("bianma"));
                        tab1.put("rowId", tabs.getJSONObject(0).getString("rowId"));
                        resutl.setTab1(tab1);
                        return resutl;
                	}
                } else {
                	resutl.setRows("0");
                	resutl.setMessage("获取数据异常,请稍后再试");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        	resutl.setMessage("获取数据异常,请稍后再试");
        }
		return resutl;  
	}
	
	
	public static Result add(String phone,String city,String grade,String xqid,String password,String realname,String remobile) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.add);  
            soapObject.addProperty("mobile", phone);
            soapObject.addProperty("city", city);
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("grade", grade);
            soapObject.addProperty("password", password);
            soapObject.addProperty("realname", realname);
            soapObject.addProperty("remobile", remobile);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.add, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回注册信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if("0".equals(rows)) {
                		resutl.setMessage(json.getString("errmsg"));
                	} else {
                		resutl.setMessage("注册成功");
                        return resutl;
                	}
                } else {
                	resutl.setRows("0");
                	resutl.setMessage("注册失败,请稍后再试");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        	resutl.setMessage("注册失败,请稍后再试");
        }
		return resutl;    
	}
	
	
	public static List<Map<String,String>> xiaoqu(String title,String city,int pagesize,int pageid,String xqid) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.xiaoqu);  
            //soapObject.addProperty("title", title);
            if(!StringUtils.isBlank(city)){
            	soapObject.addProperty("city", city);
            }
            soapObject.addProperty("pagesize",pagesize);
            soapObject.addProperty("pageid", pageid);
            if(!StringUtils.isBlank(xqid)){
            	soapObject.addProperty("xqid", xqid);
            }
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
  
            // 调用webservice  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.xiaoqu, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回小区信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	if(!"0".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		for(int i=0; i<tab.length(); i++) {
                			Map<String, String> tab1 = new HashMap<String, String>();
                			tab1.put("id", tab.getJSONObject(i).getString("id"));
                			tab1.put("name", tab.getJSONObject(i).getString("name"));
                			list.add(tab1);
                		}
                	}
                } else {
                }
            }  
        } catch (Exception e) {
        	e.printStackTrace();
        }
		return list;  
	}
	
	/**
	 * 设置请求头
	 * @return
	 */
	public static Element[] getHeader() {
		
		Element[] header = new Element[1];
        header[0] = new Element().createElement(WebServiceName.SERVICE_NAMESPACE, "mysoapheader");
        Element username = new Element().createElement(WebServiceName.SERVICE_NAMESPACE, "username");
        username.addChild(Node.TEXT, WebServiceName.userName);
        header[0].addChild(Node.ELEMENT, username);
        Element pass = new Element().createElement(WebServiceName.SERVICE_NAMESPACE, "password");
        pass.addChild(Node.TEXT, WebServiceName.passWord);
        header[0].addChild(Node.ELEMENT, pass);
        
        return header;
	}

	/**
	 * 判断是否需要更新
	 * @param versionName
	 * @return
	 */
	public static Result appv(String versionName) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.appv);  
            soapObject.addProperty("ver", versionName);
            soapObject.addProperty("pp", 1);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.appv, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取更新信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if("1".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		resutl.setMessage(tab.getJSONObject(0).getString("cc"));
                		resutl.setUrl(tab.getJSONObject(0).getString("url"));
                	}
                } else {
                	resutl.setRows("0");
                	resutl.setMessage("当前已是最新版本");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        	resutl.setMessage("当前已是最新版本");
        }
		return resutl;    
	}

	/**
	 * 一键开门
	 * @param string
	 * @param phone2
	 * @return
	 */
	public static Result kaimen(String fanghao, String phone2) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.kaimen);  
            soapObject.addProperty("fanghao", fanghao);
            soapObject.addProperty("mobile", phone2);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.kaimen, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取开门信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if("0".equals(rows)) {
                		resutl.setMessage(json.getString("errmsg"));
                	} else {
                		resutl.setMessage("开门成功");
                        return resutl;
                	}
                } else {
                	resutl.setRows("0");
                	resutl.setMessage("开门失败，请联系管理员");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        	resutl.setMessage("开门失败");
        }
		return resutl;
	}

	/**
	 * 判断界面是否上线
	 * @param channellist
	 * @return
	 */
	public static ArrayList<Channel> tv(DChannel[] channellist,long bid) {
		ArrayList<Channel> list = new ArrayList<Channel>();
		ArrayList<Channel> list2 = new ArrayList<Channel>();
		try {  
    		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
    		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                      SoapEnvelope.VER11);  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
              		WebServiceName.tv);  
            envelope.headerOut = getHeader();
            ht.debug = true;  
            soapObject.addProperty("cid", bid);
            soapObject.addProperty("tvid", 0);
            soapObject.addProperty("isshow", true);
            soapObject.addProperty("pagesize", 1000);
            soapObject.addProperty("pageid", 0);
            soapObject.addProperty("tvname", "");
            soapObject.addProperty("tvnick", "");
            soapObject.addProperty("tvtype", "");
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.tv, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取节目信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	if(!"0".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		for(int i=0; i<tab.length(); i++) {
                			Channel ch = new Channel();
                			ch.setVideoId(Integer.parseInt(tab.getJSONObject(i).getString("tvid")));
                			ch.setVideoName(tab.getJSONObject(i).getString("tvname"));
                			ch.setNickName(tab.getJSONObject(i).getString("tvnick"));
                			list.add(ch);
                		}
                	}
                } 
            }  
        } catch (Exception e) {
        }
		if(list != null && list.size() > 0) {
			for(int j=0;j<list.size();j++) {
        		Channel ch2 = list.get(j);
        		for(int i=0; i< channellist.length; i++) {
    				DChannel ch = channellist[i];
	        		if(ch.name.equals(ch2.getVideoName().trim())) {
	        			ch2.setShareImgUrl(ch.captureImg);
	        			ch2.setVideoImg(ch.icon);
	        			ch2.setExp(ch.currentEpg);
	        			ch2.setNext(ch.nextEpg);
	        			ch2.setId(ch.id);
	        			list2.add(ch2);
	        		}
    	        }
			}
		}
		return list2;
	}

	public static Result push(String xqId) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.push);  
            soapObject.addProperty("xqid", xqId);
            soapObject.addProperty("pagesize", 1);
            soapObject.addProperty("pageid", 0);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.push, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取推送信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if("1".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		resutl.setMessage(tab.getJSONObject(0).getString("cc"));
                	}
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl;
	}

	public static void tvlog(String phone, String xqid, String tvid, String startDate, String endDate) {
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.tvlog);  
            soapObject.addProperty("mobile", phone);
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("tvid", tvid);
            soapObject.addProperty("st", startDate);
            soapObject.addProperty("et", endDate);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.tvlog, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("上传播放日志信息：", res + "");
            }  
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	public static Result xiaoqu_ad(String xqid, int tvid) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.xiaoqu_ad);  
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("tvid", tvid);
            soapObject.addProperty("pagesize", 1);
            soapObject.addProperty("pageid", 0);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.xiaoqu_ad, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取广告信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if("1".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		resutl.setMessage(tab.getJSONObject(0).getString("imgpath"));
                	}
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl;
	}

	public static Result news(String xqid,int pageid,List<Map<String,String>> list) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.news);  
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("pagesize", ConstantValue.PAGESIZE);
            soapObject.addProperty("pageid", pageid);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.news, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取社区公告：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) >= 1) {
                		JSONArray tab = new JSONArray(json.getString("tab"));
                		resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
                		resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
                		JSONArray tab1 = new JSONArray(json.getString("tab1"));
                		for(int i=0; i<tab1.length();i++) {
                			Map<String, String> tabMap = new HashMap<String, String>();
                			tabMap.put("id", tab1.getJSONObject(i).getString("id"));
                			tabMap.put("title", tab1.getJSONObject(i).getString("title"));
                			tabMap.put("addtime", tab1.getJSONObject(i).getString("addtime"));
                			tabMap.put("hot", tab1.getJSONObject(i).getString("hot"));
                			list.add(tabMap);
                		}
                		resutl.setList(list);
                	}
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl;
	}
	
	public static List<Map<String, String>> fanghao(String xqid, String string, int pagesize, int pageid) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.fanghao);  
            soapObject.addProperty("xqid",xqid);
            soapObject.addProperty("pagesize",pagesize);
            soapObject.addProperty("pageid", pageid);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
  
            // 调用webservice  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.fanghao, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回房号信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	if(!"0".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		for(int i=0; i<tab.length(); i++) {
                			Map<String, String> tab1 = new HashMap<String, String>();
                			tab1.put("bianma", tab.getJSONObject(i).getString("bianma"));
                			tab1.put("men", tab.getJSONObject(i).getString("men"));
                			list.add(tab1);
                		}
                	}
                } else {
                }
            }  
        } catch (Exception e) {
        }
		return list;  
	}

	public static boolean senYzm(String phone, String yzm) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL2);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE2,  
            		WebServiceName.g_Submit);  
            soapObject.addProperty("sname","lhszhdtx");
            soapObject.addProperty("spwd","adeson9999");
            soapObject.addProperty("scorpid", "");
            soapObject.addProperty("sprdid", "1012818");
            soapObject.addProperty("sdst", phone);
            soapObject.addProperty("smsg", yzm);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            // 调用webservice  
            ht.call(WebServiceName.SERVICE_NAMESPACE2 + WebServiceName.g_Submit, envelope);  
            if (envelope.getResponse() != null) {  
            	SoapObject res = (SoapObject) envelope.getResponse();
                LogUtils.i("返回短信接口信息：", res + "");
                String State =res.getProperty(0).toString();
            	if("0".equals(State)) {
            		return true;
            	}
            }  
        } catch (Exception e) {
        }
		return false;  
	}

	public static Result add2(String phone, String city, String youke, String xqid, String password,
			String realname, String remobile, String menhao, String fanghao) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.add2);  
            soapObject.addProperty("mobile", phone);
            soapObject.addProperty("city", city);
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("grade", youke);
            soapObject.addProperty("password", DigestUtils.md5Hex(password));
            soapObject.addProperty("realname", realname);
            soapObject.addProperty("remobile", remobile);
            soapObject.addProperty("menhao", menhao);
            soapObject.addProperty("fanghao", fanghao);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.add2, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回注册信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if("0".equals(rows)) {
                		resutl.setMessage(json.getString("errmsg"));
                	} else {
                		resutl.setMessage("注册成功");
                        return resutl;
                	}
                } else {
                	resutl.setRows("0");
                	resutl.setMessage("注册失败,请稍后再试");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        	resutl.setMessage("注册失败,请稍后再试");
        }
		return resutl;  
	}

	public static Result newsinfo(String ggid) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.newsinfo);  
            soapObject.addProperty("id", ggid);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.newsinfo, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取社区公告内容：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) >= 1) {
                		JSONArray tab = new JSONArray(json.getString("tab"));
                		resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
                		resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
                		JSONArray tab1 = new JSONArray(json.getString("tab1"));
            			Map<String, String> tabMap = new HashMap<String, String>();
            			tabMap.put("id", tab1.getJSONObject(0).getString("id"));
            			tabMap.put("title", tab1.getJSONObject(0).getString("title"));
            			tabMap.put("addtime", tab1.getJSONObject(0).getString("addtime"));
            			tabMap.put("hot", tab1.getJSONObject(0).getString("hot"));
            			tabMap.put("content", tab1.getJSONObject(0).getString("content"));
                		resutl.setTab1(tabMap);
                	}
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl; 
	}

	/**
	 * 获取说说类型
	 * @param i
	 * @return
	 */
	public static Result bbslist(int i) {
		Result resutl = new Result();
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbstitle);  
            soapObject.addProperty("cid", i);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbstitle, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取说说类型：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) >= 1) {
                		JSONArray tab = new JSONArray(json.getString("tab"));
                		resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
                		resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
                		JSONArray tab1 = new JSONArray(json.getString("tab1"));
                		for(int j=0; j<tab1.length(); j++) {
                			Map<String, String> tabMap = new HashMap<String, String>();
                			tabMap.put("id", tab1.getJSONObject(j).getString("id"));
                			tabMap.put("title", tab1.getJSONObject(j).getString("title"));
                			tabMap.put("pic", tab1.getJSONObject(j).getString("pic"));
                			list.add(tabMap);
                		}
                		resutl.setList(list);
                	}
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl; 
	}

	public static Result bbsadd(String username, String id, String xqid, String title, String des,long b2id) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbsadd);  
            soapObject.addProperty("username", username);
            soapObject.addProperty("bid", id);
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("title", title);
            soapObject.addProperty("cc", des);
            soapObject.addProperty("b2id", b2id);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbsadd, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("提交说说类型：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) >= 1) {
                		resutl.setMessage("提交成功");
                	}
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl; 
	}

	public static Result bbspicadd(String file, long b2id, String imageType) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbspicadd);  
            soapObject.addProperty("byteFileStream", file);
            soapObject.addProperty("b2id", b2id);
            soapObject.addProperty("fileName", imageType);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbspicadd, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("提交说说类型：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) >= 1) {
                		resutl.setMessage("提交成功");
                	}
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl; 
	}

	public static Result call(String zhujiao, String phone) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.call);  
            soapObject.addProperty("zhujiao", zhujiao);
            soapObject.addProperty("beijiao", phone);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.call, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("呼叫电话：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("resultCode");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) == 0) {
                		resutl.setMessage("呼叫成功");
                	} else {
                		String resultMsg = json.getString("resultMsg");
                		resutl.setMessage(resultMsg);
                	}
                } else {
                	resutl.setMessage("呼叫失败");
                	resutl.setRows("-1");
                }
            }  
        } catch (Exception e) {
        	resutl.setMessage("呼叫失败");
        	resutl.setRows("-1");
        }
		return resutl; 
	}

	public static String bbslist(String xqid, String userName, String pagesize, int pageId) {
		String resutl = "";
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbslist);  
            soapObject.addProperty("bid", 0);
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("username", userName);
            soapObject.addProperty("pagesize", pagesize);
            soapObject.addProperty("pageid", pageId);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbslist, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取说说列表：", res + "");
                resutl = res.toString();
            }  
        } catch (Exception e) {
        }
		return resutl; 
	}

	public static List<Map<String, String>> citys() {
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.citys);  
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            // 调用webservice  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.citys, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回城市信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	if(!"0".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		for(int i=0; i<tab.length(); i++) {
                			Map<String, String> tab1 = new HashMap<String, String>();
                			tab1.put("city", tab.getJSONObject(i).getString("city"));
                			tab1.put("rowId", tab.getJSONObject(i).getString("rowId"));
                			list.add(tab1);
                		}
                	}
                } else {
                }
            }  
        } catch (Exception e) {
        }
		return list;  
	}

	public static Result editpassword(String phone, String password, String xqid) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.editpassword);  
            soapObject.addProperty("mobile", phone);
            soapObject.addProperty("password", DigestUtils.md5Hex(password));
            soapObject.addProperty("xqid", xqid);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.editpassword, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("修改密码：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) == 1) {
                		resutl.setMessage("修改成功");
                	} else {
                		String resultMsg = json.getString("errmsg");
                		resutl.setMessage(resultMsg);
                	}
                } else {
                	resutl.setMessage("密码修改失败");
                	resutl.setRows("-1");
                }
            }  
        } catch (Exception e) {
        	resutl.setMessage("密码修改失败");
        	resutl.setRows("-1");
        }
		return resutl; 
	}

	/**
	 * 获取说说图片
	 * @param b2id
	 * @return
	 */
	public static Result bbspic(String b2id) {
		Result resutl = new Result();
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbspic);  
            soapObject.addProperty("b2id", b2id);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbspic, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取照片：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	JSONArray tab = new JSONArray(json.getString("tab"));
            		resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
            		resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
            		JSONArray tab1 = new JSONArray(json.getString("tab1"));
            		for(int j=0; j<tab1.length(); j++) {
            			Map<String, String> tabMap = new HashMap<String, String>();
            			tabMap.put("pic", tab1.getJSONObject(j).getString("pic"));
            			list.add(tabMap);
            		}
            		resutl.setList(list);
                } else {
                	resutl.setRows("0");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("0");
        }
		return resutl; 
	}

	public static Result bbscommentadd(String userName, String b2id, String sya_content) {
		Result resutl = new Result();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbscommentadd);  
            soapObject.addProperty("username", userName);
            soapObject.addProperty("b2id", b2id);
            soapObject.addProperty("cc", sya_content);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbscommentadd, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("提交评论：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	if(Integer.parseInt(rows) == 1) {
                		resutl.setMessage("提交成功");
                	} else {
                		String resultMsg = json.getString("errmsg");
                		resutl.setMessage(resultMsg);
                	}
                } else {
                	resutl.setMessage("提交失败");
                	resutl.setRows("-1");
                }
            }  
        } catch (Exception e) {
        	resutl.setMessage("提交失败");
        	resutl.setRows("-1");
        }
		return resutl;
	}

	public static Result bbscomment(String b2id, int pagesize, int pageid, String userName) {
		Result resutl = new Result();
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbscomment);  
            soapObject.addProperty("username", userName);
            soapObject.addProperty("b2id", b2id);
            soapObject.addProperty("pagesize", pagesize);
            soapObject.addProperty("pageid", pageid);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbscomment, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("获取评论：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	JSONArray tab = new JSONArray(json.getString("tab"));
            		resutl.setFldTotalPage(tab.getJSONObject(0).getString("fldTotalPage"));
            		resutl.setFldtotalRecord(tab.getJSONObject(0).getString("fldtotalRecord"));
            		JSONArray tab1 = new JSONArray(json.getString("tab1"));
            		for(int j=0; j<tab1.length(); j++) {
            			Map<String, String> tabMap = new HashMap<String, String>();
            			tabMap.put("cc", tab1.getJSONObject(j).getString("cc"));
            			tabMap.put("addtime", tab1.getJSONObject(j).getString("addtime"));
            			tabMap.put("username", tab1.getJSONObject(j).getString("username"));
            			list.add(tabMap);
            		}
            		resutl.setList(list);
                } else {
                	resutl.setRows("-1");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("-1");
        }
		return resutl;
	}

	public static List<Map<String, String>> myvip(String userName,String xqid) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.myvip);  
            soapObject.addProperty("mobile", userName);
            soapObject.addProperty("xqid", xqid);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            // 调用webservice  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.myvip, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回房下信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	if(!"0".equals(rows)) {
                		JSONArray tab = new JSONArray(json.getString("tab1"));
                		for(int i=0; i<tab.length(); i++) {
                			Map<String, String> tab1 = new HashMap<String, String>();
                			tab1.put("realname", tab.getJSONObject(i).getString("realname"));
                			tab1.put("mobile", tab.getJSONObject(i).getString("mobile"));
                			tab1.put("grade", tab.getJSONObject(i).getString("grade"));
                			tab1.put("created", tab.getJSONObject(i).getString("created"));
                			tab1.put("islive", tab.getJSONObject(i).getString("islive"));
                			tab1.put("remobile", tab.getJSONObject(i).getString("remobile"));
                			tab1.put("fanghao", tab.getJSONObject(i).getString("fanghao"));
                			tab1.put("bianma", tab.getJSONObject(i).getString("bianma"));
                			tab1.put("xqid", tab.getJSONObject(i).getString("xqid"));
                			list.add(tab1);
                		}
                	}
                } else {
                }
            }  
        } catch (Exception e) {
        }
		return list;  
	}

	public static int opendoor(String phone, String xqid, String bianma, int i) {
		int rows = 0;
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.opendoor);  
            soapObject.addProperty("mobile", phone);
            soapObject.addProperty("xqid", xqid);
            soapObject.addProperty("men", bianma);
            soapObject.addProperty("isopen", i);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            // 设置与.NET提供的webservice保持较好的兼容性  
            envelope.dotNet = true;  
            // 调用webservice  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.opendoor, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("返回开门信息：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	rows = json.getInt("rows");
                } else {
                }
            }  
        } catch (Exception e) {
        }
		return rows;
	}

	public static Result bbszan(String b2id) {
		Result resutl = new Result();
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbszan);  
            soapObject.addProperty("b2id", b2id);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbszan, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("赞：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	resutl.setMessage(json.getString("errmsg"));
                } else {
                	resutl.setRows("-1");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("-1");
        }
		return resutl;
	}

	public static Result bbsdelete(String username, String b2id) {
		Result resutl = new Result();
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbsdelete);  
            soapObject.addProperty("b2id", b2id);
            soapObject.addProperty("username", username);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbsdelete, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("删除：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	resutl.setRows(rows);
                	resutl.setMessage(json.getString("errmsg"));
                } else {
                	resutl.setRows("-1");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("-1");
        }
		return resutl;
	}

	public static Result bbsinfo(String b2id) {
		Result resutl = new Result();
		List<Map<String,String>> list = new ArrayList<Map<String,String>> ();
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.bbsinfo);  
            soapObject.addProperty("b2id", b2id);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.bbsinfo, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("赞：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	resutl.setRows("1");
                	JSONObject json = new JSONObject(result);
                	JSONArray tab = new JSONArray(json.getString("tab1"));
            		for(int i=0; i<tab.length(); i++) {
            			Map<String, String> tab1 = new HashMap<String, String>();
            			tab1.put("zan", tab.getJSONObject(i).getString("zan"));
            			list.add(tab1);
            		}
            		resutl.setList(list);
                } else {
                	resutl.setRows("-1");
                }
            }  
        } catch (Exception e) {
        	resutl.setRows("-1");
        }
		return resutl;
	}

	public static boolean vipdelete(String string, String string2, String string3) {
		boolean isParam = false;
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.vipdelete);  
            soapObject.addProperty("mobile", string);
            soapObject.addProperty("xqid", string2);
            soapObject.addProperty("remobile", string3);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.vipdelete, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("删除人：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	if("1".equals(rows)) {
                		isParam = true;
                	}
                } 
            }  
        } catch (Exception e) {
        }
		return isParam;
	}

	public static boolean jihuo(String string, String string2) {
		boolean isParam = false;
		//  创建httpTranaportSE传输对象
		HttpTransportSE ht = new HttpTransportSE(WebServiceName.SERVICE_URL);
		try {  
            ht.debug = true;  
            // 使用SOAP1.1协议创建Envelop对象  
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
                    SoapEnvelope.VER11);  
            // 实例化SoapObject对象  
            SoapObject soapObject = new SoapObject(WebServiceName.SERVICE_NAMESPACE,  
            		WebServiceName.jihuo);  
            soapObject.addProperty("mobile", string);
            soapObject.addProperty("xqid", string2);
            envelope.headerOut = getHeader();
            envelope.bodyOut = soapObject;  
            envelope.dotNet = true;  
            ht.call(WebServiceName.SERVICE_NAMESPACE + WebServiceName.jihuo, envelope);  
            if (envelope.getResponse() != null) {  
                Object res = envelope.getResponse();
                LogUtils.i("激活：", res + "");
                String result = res.toString();
                if(!StringUtils.isBlank(result)) {
                	JSONObject json = new JSONObject(result);
                	String rows = json.getString("rows");
                	if("1".equals(rows)) {
                		isParam = true;
                	}
                } 
            }  
        } catch (Exception e) {
        }
		return isParam;
	}
	
}
