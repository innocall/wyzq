package com.lemon95.wyzq.receiver;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.lemon95.wyzq.utils.LogUtils;
import com.lemon95.wyzq.utils.PromptManager;

/**
 * 自动接听
 * @author wu
 */
public class PhoneReceiver extends BroadcastReceiver {
	private WeakReference<Context> mApp;
	private WeakReference<Intent> mIntent;
	private static TelephonyManager manager;
	private static ITelephony mITelephony;
	private static String number;
	private TelephonyManager tm;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.i("phone", "num");
		mApp = new WeakReference<Context>(context);
		mIntent = new WeakReference<Intent>(intent);
		this.context = context;
		final String action = mIntent.get().getAction();
		tm = (TelephonyManager) mApp.get().getSystemService(Service.TELEPHONY_SERVICE);
		start();
		switch (tm.getCallState()) {
		case TelephonyManager.CALL_STATE_RINGING:  //当电话进来时
			number = mIntent.get().getStringExtra("incoming_number");
			if(!StringUtils.isBlank(number) && number.equals("4009304063")) {
				toAnswer();
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK: //接起电话
			break;
		case TelephonyManager.CALL_STATE_IDLE:    //挂电话
			break;
		}
	}

	void start() {
		if (manager == null)
			manager = (TelephonyManager) mApp.get().getSystemService(Context.TELEPHONY_SERVICE);
	}

	//声音
	public void switchspeaker() {
		try {
			AudioManager audioManager = (AudioManager) mApp.get()
					.getSystemService(Context.AUDIO_SERVICE);
			if (!audioManager.isSpeakerphoneOn()) {
				audioManager.setMode(AudioManager.ROUTE_SPEAKER);
				audioManager.setSpeakerphoneOn(true);
			} else {
				audioManager.setSpeakerphoneOn(false);
			}
			audioManager
					.setStreamVolume(
							AudioManager.STREAM_VOICE_CALL,
							audioManager
									.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
							AudioManager.STREAM_VOICE_CALL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void toAnswer() {
		try {
			Method getITelephonyMethod = TelephonyManager.class
					.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			mITelephony = (ITelephony) getITelephonyMethod.invoke(manager,
					(Object[]) null);
			answerRingingCallWithBroadcast(mApp.get());
		} catch (Exception e) {
			PromptManager.showToast(context, "回拨成功，请接听电话！");
		}
	}

	//挂电话
	public void toEnd() {
		try {
			Method getITelephonyMethod = TelephonyManager.class
					.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			mITelephony = (ITelephony) getITelephonyMethod.invoke(manager,
					(Object[]) null);
			mITelephony.endCall();
		} catch (Exception e) {
		}
	}

	private void answerRingingCallWithBroadcast(Context contextF) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		//是否有耳机
		if (!audioManager.isWiredHeadsetOn()) {
			// 4.1以上系统限制了部分权限， 使用三星4.1版本测试提示警告：Permission Denial: not allowed to
			// send broadcast android.intent.action.HEADSET_PLUG from pid=1324,
			// uid=10017
			// 这里需要注意一点，发送广播时加了权限“android.permission.CALL_PRIVLEGED”，则接受该广播时也需要增加该权限。但是4.1以上版本貌似这个权限只能系统应用才可以得到。测试的时候，自定义的接收器无法接受到此广播，后来去掉了这个权限，设为NULL便可以监听到了。
			if (android.os.Build.VERSION.SDK_INT >= 15) {
				Log.i("cece", android.os.Build.VERSION.SDK_INT + "");
				try {
	                Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
	                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
	                intent.putExtra("android.intent.extra.KEY_EVENT",keyEvent);
	                context.sendOrderedBroadcast(intent,"android.permission.CALL_PRIVILEGED");
	                 
	                intent = new Intent("android.intent.action.MEDIA_BUTTON");
	                keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
	                intent.putExtra("android.intent.extra.KEY_EVENT",keyEvent);
	                context.sendOrderedBroadcast(intent,"android.permission.CALL_PRIVILEGED");
	                 
	                Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
	                localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	                localIntent1.putExtra("state", 1);
	                localIntent1.putExtra("microphone", 1);
	                localIntent1.putExtra("name", "Headset");
	                context.sendOrderedBroadcast(localIntent1,"android.permission.CALL_PRIVILEGED");
	                 
	                Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	                KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
	                        KeyEvent.KEYCODE_HEADSETHOOK);
	                localIntent2.putExtra("android.intent.extra.KEY_EVENT",    localKeyEvent1);
	                context.sendOrderedBroadcast(localIntent2,"android.permission.CALL_PRIVILEGED");
	                 
	                Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
	                KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
	                        KeyEvent.KEYCODE_HEADSETHOOK);
	                localIntent3.putExtra("android.intent.extra.KEY_EVENT",    localKeyEvent2);
	                context.sendOrderedBroadcast(localIntent3,"android.permission.CALL_PRIVILEGED");
	                 
	                Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
	                localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	                localIntent4.putExtra("state", 0);
	                localIntent4.putExtra("microphone", 1);
	                localIntent4.putExtra("name", "Headset");
	                context.sendOrderedBroadcast(localIntent4,"android.permission.CALL_PRIVILEGED");
	            } catch (Exception e2) {
	                e2.printStackTrace();
	                Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);  
	                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);  
	                meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT,keyEvent);  
	                context.sendOrderedBroadcast(meidaButtonIntent, null);
	            }
			} else {
				// 以下适用于Android2.3及2.3以上的版本上 ，但测试发现4.1系统上不管用。
				Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent1.putExtra("state", 1);
				localIntent1.putExtra("microphone", 1);
				localIntent1.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent1,
						"android.permission.CALL_PRIVILEGED");
				Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent2.putExtra(Intent.EXTRA_KEY_EVENT, localKeyEvent1);
				context.sendOrderedBroadcast(localIntent2,
						"android.permission.CALL_PRIVILEGED");

				Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent3.putExtra(Intent.EXTRA_KEY_EVENT, localKeyEvent2);
				context.sendOrderedBroadcast(localIntent3,
						"android.permission.CALL_PRIVILEGED");

				Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent4.putExtra("state", 0);
				localIntent4.putExtra("microphone", 1);
				localIntent4.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent4,
						"android.permission.CALL_PRIVILEGED");
			}
		} else {
			Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
			KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
					KeyEvent.KEYCODE_HEADSETHOOK);
			meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
			context.sendOrderedBroadcast(meidaButtonIntent, null);
		}
	}
}
