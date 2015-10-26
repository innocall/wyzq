package com.lemon95.wyzq.fragment;

import java.util.List;
import java.util.Map;

import com.lemon95.wyzq.R;
import com.lemon95.wyzq.adapter.OpenDorpAdapter;
import com.lemon95.wyzq.drop.WgUdpCommShort;
import com.lemon95.wyzq.global.ConstantValue;
import com.lemon95.wyzq.manage.MiddleManager;
import com.lemon95.wyzq.model.Record;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.view.BaseUI;
import com.lemon95.wyzq.webserver.WebServiceUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OpenDorpFragment extends BaseUI {
	
	private final static String TAG = OpenDorpFragment.class.getSimpleName();
	private final static int MENHAO = 0;
	private final static int KAIMEN = 1;
	private SharedPreferences sp;
	private int ret = 0;
	private ListView open_id;
	private TextView accept_no_order1;
	private Record record;
	private String xqid;
	private OpenDorpAdapter adapter;
	private List<Map<String, String>> result;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
				case MENHAO:
					if(result != null) {
						if(result.size() > 0) {
							accept_no_order1.setVisibility(View.GONE);
							open_id.setVisibility(View.VISIBLE);
							adapter = new OpenDorpAdapter(context, result);
							open_id.setAdapter(adapter);
							return;
						}
					}
					accept_no_order1.setVisibility(View.VISIBLE);
					open_id.setVisibility(View.GONE);
					break;
				case KAIMEN:
					//ret = testWatchingServer(controllerIP, controllerSN, watchServerIP,watchServerPort); // 接收服务器设置
					//ret = WatchingServerRuning(watchServerIP, watchServerPort); // 服务器运行....
					PromptManager.closeProgressDialog();
					if(ret == 1) {
						Toast.makeText(context, "开门成功", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "开门失败", Toast.LENGTH_SHORT).show();
					}
					break;
			}
		};
	};
	
	public OpenDorpFragment(Context context) {
		super(context);
	}

	@Override
	public void init() {
		showInMiddle = (LinearLayout) View.inflate(context, R.layout.opendrop_fragment, null);
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		open_id = (ListView) findViewById(R.id.open_id);
		accept_no_order1 = (TextView) findViewById(R.id.accept_no_order1);
		xqid = sp.getString("XQID", "");
	}

	@Override
	public void setListener() {
		open_id.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(result != null) {
					Map<String,String> map = result.get(position);
					String bianma = map.get("bianma");
					String phone = sp.getString("USERNAME", "");
					opendoor(phone,bianma);
				}
			}
		});
	}

	protected void opendoor(final String phone,final String bianma) {
		PromptManager.showProgressDialog(context, "正在开门，请稍候...");
		new Thread(){
			public void run() {
				ret = WebServiceUtils.opendoor(phone,xqid,bianma,1);
				Message msg = new Message();
				msg.what = KAIMEN;
				handler.sendMessage(msg);
			};
		}.start();
	}

	@Override
	public int getID() {
		return ConstantValue.OPENDORPFRAGMENT;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
		MobclickAgent.onResume(context);
		getManHao();
	}
	
	
	private void getManHao() {
		new Thread(){
			public void run() {
				result = WebServiceUtils.fanghao(xqid, "", 1000, 0);
				Message msg = new Message();
				msg.what = MENHAO;
				handler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * 开门
	 * @param controllerSN 控制器sn
	 * @param controllerIP 控制器ip
	 */
	public void openDown(final long controllerSN,final String controllerIP) {
		//final long controllerSN = 123182732; //8C 9E 57 07
		//final String controllerIP = "192.168.1.114";    //设备ip
		//String watchServerIP = "192.168.1.103";   //本机ip
		//int watchServerPort = 61005;
		log("测试开始...");
		new Thread(){
			public void run() {
				ret = testBasicFunction(controllerIP, controllerSN,1); // 基本功能测试
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	public void log(String str) {
		Log.i(TAG, str);
	}
	
	/**
	 * 基本功能测试
	 * @param controllerIP  控制器ip
	 * @param controllerSN  控制器sn
	 * @param type 状态 1 开门    2获取记录
	 * @return
	 */
	@SuppressWarnings("unused")
	public int testBasicFunction(String controllerIP, long controllerSN,int type) {
		byte[] recvBuff;
		int success = 0;
		WgUdpCommShort pkt = new WgUdpCommShort();
		pkt.iDevSn = controllerSN;
		log("控制器SN = " + controllerSN);
		// 打开udp连接
		pkt.CommOpen(controllerIP);
		if(type == 1) {
			// 1.10 远程开门[功能号: 0x40]
			int doorNO = 1;
			pkt.Reset();
			pkt.functionID = (byte) 0x40;
			pkt.iDevSn = controllerSN;
			pkt.data[0] = (byte) (doorNO & 0xff); // 2013-11-03 20:56:33
			recvBuff = pkt.run();
			success = 0;
			if (recvBuff != null) {
				if (WgUdpCommShort.getIntByByte(recvBuff[8]) == 1) {
					// 有效开门.....
					log("1.10远程开门	 成功...");
					success = 1;
				}
			}
		} else if(type == 2) {
			// 1.4 查询控制器状态[功能号: 0x20](实时监控用)
			pkt.Reset();
			pkt.functionID = (byte) 0x20;
			pkt.iDevSn = controllerSN;
			recvBuff = pkt.run();
			success = 0;
			if (recvBuff != null) {
				// 读取信息成功...
				success = 1;
				log("1.4 查询控制器状态 成功...");
				// 最后一条记录的信息
				displayRecordInformation(recvBuff);
			}
		}
		// 关闭udp连接
		pkt.CommClose();
		return success;
	}

	/**
	 * 开门记录
	 * @param sn
	 * @param ip
	 */
	private void findOpenDate(String sn, String ip) {
		if(sn == null || sn.equals("")) {
			Toast.makeText(context, "请输入控制器sn号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(ip == null || ip.equals("")) {
			Toast.makeText(context, "请输入控制器ip地址", Toast.LENGTH_SHORT).show();
			return;
		}
		Editor ed = sp.edit();
		ed.putString("sn", sn);
		ed.putString("ip", ip);
		ed.commit();
		long d_sn = Long.parseLong(sn);
		findOpenDown(d_sn,ip);  //查询记录
	}

	private void findOpenDown(final long d_sn, final String ip) {
		log("测试开始...");
		new Thread(){
			public void run() {
				ret = testBasicFunction(ip, d_sn,2); // 基本功能测试
				Message msg = new Message();
				msg.what = 2;
				handler.sendMessage(msg);
			};
		}.start();
	}

	private void openDrop(String sn, String ip) {
		if(sn == null || sn.equals("")) {
			Toast.makeText(context, "请输入控制器sn号", Toast.LENGTH_SHORT).show();
			return;
		}
		if(ip == null || ip.equals("")) {
			Toast.makeText(context, "请输入控制器ip地址", Toast.LENGTH_SHORT).show();
			return;
		}
		Editor ed = sp.edit();
		ed.putString("sn", sn);
		ed.putString("ip", ip);
		ed.commit();
		long d_sn = Long.parseLong(sn);
		openDown(d_sn,ip);
	}
	
	public void displayRecordInformation(byte[] recvBuff) {

		// 8-11 最后一条记录的索引号
		// (=0表示没有记录) 4 0x00000000
		long recordIndex = WgUdpCommShort.getLongByByte(recvBuff, 8, 4);
		// 12 记录类型
		// 0=无记录
		// 1=刷卡记录
		// 2=门磁,按钮, 设备启动, 远程开门记录
		// 3=报警记录 1
		int recordType = WgUdpCommShort.getIntByByte(recvBuff[12]);

		// 13 有效性(0 表示不通过, 1表示通过) 1
		int recordValid = WgUdpCommShort.getIntByByte(recvBuff[13]);

		// 14 门号(1,2,3,4) 1
		int recordDoorNO = WgUdpCommShort.getIntByByte(recvBuff[14]);

		// 15 进门/出门(1表示进门, 2表示出门) 1 0x01
		int recordInOrOut = WgUdpCommShort.getIntByByte(recvBuff[15]);

		// 16-19 卡号(类型是刷卡记录时)
		// 或编号(其他类型记录) 4
		long recordCardNO = WgUdpCommShort.getLongByByte(recvBuff, 16, 4);

		// 20-26 刷卡时间:
		// 年月日时分秒 (采用BCD码)见设置时间部分的说明
		String recordTime = String.format("%02X%02X-%02X-%02X %02X:%02X:%02X",
				WgUdpCommShort.getIntByByte(recvBuff[20]),
				WgUdpCommShort.getIntByByte(recvBuff[21]),
				WgUdpCommShort.getIntByByte(recvBuff[22]),
				WgUdpCommShort.getIntByByte(recvBuff[23]),
				WgUdpCommShort.getIntByByte(recvBuff[24]),
				WgUdpCommShort.getIntByByte(recvBuff[25]),
				WgUdpCommShort.getIntByByte(recvBuff[26]));

		// 2012.12.11 10:49:59 7
		// 27 记录原因代码(可以查 "刷卡记录说明.xls"文件的ReasonNO)
		// 处理复杂信息才用 1
		int reason = WgUdpCommShort.getIntByByte(recvBuff[27]);

		// 0=无记录
		// 1=刷卡记录
		// 2=门磁,按钮, 设备启动, 远程开门记录
		// 3=报警记录 1
		// 0xFF=表示指定索引位的记录已被覆盖掉了. 请使用索引0, 取回最早一条记录的索引值
		
		record = new Record();
		if (recordType == 0) {
			//log(String.format("索引位=%u  无记录", recordIndex));
			log("索引位="+recordIndex+"  无记录");
			record.setType(0);
		} else if (recordType == 0xff) {
			record.setType(100);  //记录丢失
			log(" 指定索引位的记录已被覆盖掉了,请使用索引0, 取回最早一条记录的索引值");
		} else if (recordType == 1) // 2015-06-10 08:49:31 显示记录类型为卡号的数据
		{
			// 卡号
			record.setType(1);
			log(String.format("索引位=%d  ", recordIndex));
			log(String.format("  卡号 = %d", recordCardNO));
			log(String.format("  门号 = %d", recordDoorNO));
			log(String.format("  进出 = %s", recordInOrOut == 1 ? "进门" : "出门"));
			log(String.format("  有效 = %s", recordValid == 1 ? "通过" : "禁止"));
			log(String.format("  时间 = %s", recordTime));
			log(String.format("  描述 = %s", getReasonDetailChinese(reason)));
			record.setType(1);
		} else if (recordType == 2) {
			// 其他处理
			// 门磁,按钮, 设备启动, 远程开门记录
			record.setType(2);
			log(String.format("索引位=%d  非刷卡记录", recordIndex));
			log(String.format("  编号 = %d", recordCardNO));
			log(String.format("  门号 = %d", recordDoorNO));
			log(String.format("  时间 = %s", recordTime));
			log(String.format("  描述 = %s", getReasonDetailChinese(reason)));
		} else if (recordType == 3) {
			// 其他处理
			// 报警记录
			record.setType(3);
			log(String.format("索引位=%d  报警记录", recordIndex));
			log(String.format("  编号 = %d", recordCardNO));
			log(String.format("  门号 = %d", recordDoorNO));
			log(String.format("  时间 = %s", recordTime));
			log(String.format("  描述 = %s", getReasonDetailChinese(reason)));
		}
		record.setId(recordIndex + "");
		record.setCardId(recordCardNO + "");
		record.setDoorId(recordDoorNO + "");
		record.setInOrOut(recordInOrOut == 1 ? "进门" : "出门");
		record.setValid( recordValid == 1 ? "通过" : "禁止");
		record.setTime(recordTime + "");
		record.setDes(getReasonDetailChinese(reason));
	}
	
	public String getReasonDetailChinese(int Reason) // 中文
	{
		if (Reason > 45) {
			return "";
		}
		if (Reason <= 0) {
			return "";
		}
		return RecordDetails[(Reason - 1) * 4 + 3]; // 中文信息
	}
	
	public String RecordDetails[] = {
		// 记录原因 (类型中 SwipePass 表示通过; SwipeNOPass表示禁止通过; ValidEvent 有效事件(如按钮
		// 门磁 超级密码开门); Warn 报警事件)
		// 代码 类型 英文描述 中文描述
		"1", "SwipePass", "Swipe", "刷卡开门", "2", "SwipePass", "Swipe Close",
		"刷卡关", "3", "SwipePass", "Swipe Open", "刷卡开", "4", "SwipePass",
		"Swipe Limited Times", "刷卡开门(带限次)", "5", "SwipeNOPass",
		"Denied Access: PC Control", "刷卡禁止通过: 电脑控制", "6", "SwipeNOPass",
		"Denied Access: No PRIVILEGE", "刷卡禁止通过: 没有权限", "7", "SwipeNOPass",
		"Denied Access: Wrong PASSWORD", "刷卡禁止通过: 密码不对", "8",
		"SwipeNOPass", "Denied Access: AntiBack", "刷卡禁止通过: 反潜回", "9",
		"SwipeNOPass", "Denied Access: More Cards", "刷卡禁止通过: 多卡", "10",
		"SwipeNOPass", "Denied Access: First Card Open", "刷卡禁止通过: 首卡",
		"11", "SwipeNOPass", "Denied Access: Door Set NC", "刷卡禁止通过: 门为常闭",
		"12", "SwipeNOPass", "Denied Access: InterLock", "刷卡禁止通过: 互锁",
		"13", "SwipeNOPass", "Denied Access: Limited Times",
		"刷卡禁止通过: 受刷卡次数限制", "14", "SwipeNOPass",
		"Denied Access: Limited Person Indoor", "刷卡禁止通过: 门内人数限制", "15",
		"SwipeNOPass", "Denied Access: Invalid Timezone",
		"刷卡禁止通过: 卡过期或不在有效时段", "16", "SwipeNOPass",
		"Denied Access: In Order", "刷卡禁止通过: 按顺序进出限制", "17", "SwipeNOPass",
		"Denied Access: SWIPE GAP LIMIT", "刷卡禁止通过: 刷卡间隔约束", "18",
		"SwipeNOPass", "Denied Access", "刷卡禁止通过: 原因不明", "19",
		"SwipeNOPass", "Denied Access: Limited Times", "刷卡禁止通过: 刷卡次数限制",
		"20", "ValidEvent", "Push Button", "按钮开门", "21", "ValidEvent",
		"Push Button Open", "按钮开", "22", "ValidEvent", "Push Button Close",
		"按钮关", "23", "ValidEvent", "Door Open", "门打开[门磁信号]", "24",
		"ValidEvent", "Door Closed", "门关闭[门磁信号]", "25", "ValidEvent",
		"Super Password Open Door", "超级密码开门", "26", "ValidEvent",
		"Super Password Open", "超级密码开", "27", "ValidEvent",
		"Super Password Close", "超级密码关", "28", "Warn",
		"Controller Power On", "控制器上电", "29", "Warn", "Controller Reset",
		"控制器复位", "30", "Warn", "Push Button Invalid: Disable",
		"按钮不开门: 按钮禁用", "31", "Warn", "Push Button Invalid: Forced Lock",
		"按钮不开门: 强制关门", "32", "Warn", "Push Button Invalid: Not On Line",
		"按钮不开门: 门不在线", "33", "Warn", "Push Button Invalid: InterLock",
		"按钮不开门: 互锁", "34", "Warn", "Threat", "胁迫报警", "35", "Warn",
		"Threat Open", "胁迫报警开", "36", "Warn", "Threat Close", "胁迫报警关",
		"37", "Warn", "Open too long", "门长时间未关报警[合法开门后]", "38", "Warn",
		"Forced Open", "强行闯入报警", "39", "Warn", "Fire", "火警", "40", "Warn",
		"Forced Close", "强制关门", "41", "Warn", "Guard Against Theft",
		"防盗报警", "42", "Warn", "7*24Hour Zone", "烟雾煤气温度报警", "43", "Warn",
		"Emergency Call", "紧急呼救报警", "44", "RemoteOpen", "Remote Open Door",
		"操作员远程开门", "45", "RemoteOpen", "Remote Open Door By USB Reader",
		"发卡器确定发出的远程开门" };
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(context);
		MiddleManager.getInstance().clearNew(OpenDorpFragment.class);
	}
	
	

}
