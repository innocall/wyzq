package com.lemon95.wyzq.webserver;

public interface WebServiceName {
	
	//webservice 命名空间
	public static final String SERVICE_NAMESPACE = "http://app.lemon95.com/";
	public static final String SERVICE_NAMESPACE2 = "http://tempuri.org/";
	
	//定义webservice提供的服务url
	public static final String SERVICE_URL = "http://app.lemon95.com/api/vip.asmx";
	
	public static final String SERVICE_URL2 = "http://cf.lmobile.cn/submitdata/service.asmx";

	String userName = "app";
	
	String passWord = "app@#";
	
	String info = "info";  //返回用户信息
	
	String add = "add"; //注册用户
	
	String xiaoqu = "xiaoqu"; //返回小区
	
	String xiaoqu_ad = "xiaoqu_ad"; //根据小区返回节目广告
	
	String tv = "tv"; //返回节目列表
	
	String login = "login";
	
	String appv = "appv"; //判断更新
	
	String kaimen = "kaimen";  //开门
	
	String push = "push"; //获取推送消息
	
	String tvlog = "tvlog"; //上传播放记录
	
	String news = "news";  //获取社区公告列表
	
	String fanghao = "fanghao"; //获取房号
	
	String g_Submit = "g_Submit"; //发送短信
	
	String add2 = "add2"; //注册
	
	String newsinfo = "newsinfo"; //获取公告内容
	
	String bbstitle = "bbstitle"; //获取说说类型
	
	String bbsadd = "bbsadd";//添加说说
	
	String bbspicadd = "bbspicadd"; //上传图片
	
	String call = "call"; //拨打电话
	
	String bbslist = "bbslist"; //获取说说列表
	
	String citys = "citys"; //获取城市
	
	String editpassword = "editpassword";  //修改密码
	
	String bbspic = "bbspic"; //获取说说图片
	
	String bbscommentadd = "bbscommentadd"; //提交评论
	
	String bbscomment = "bbscomment"; //获取评论
	
	String myvip = "myvip";  //房下账号
	
	String opendoor = "opendoor"; //开门
	
	String bbszan = "bbszan"; //赞
	
	String bbsdelete = "bbsdelete"; //删除帖子
	
	String bbsinfo = "bbsinfo"; //赞数量
	
	String vipdelete = "vipdelete"; //删除人
	
	String jihuo = "jihuo";  //激活
	
	String boxlist = "boxlist"; //设备列表
	
	String videoclass = "videoclass";  //获取视频类型
	
}
