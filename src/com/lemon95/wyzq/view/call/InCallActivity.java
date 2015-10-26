package com.lemon95.wyzq.view.call;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.feiyucloud.sdk.FYCall;
import com.feiyucloud.sdk.FYCallListener;
import com.feiyucloud.sdk.FYError;
import com.lemon95.wyzq.R;
import com.lemon95.wyzq.model.Result;
import com.lemon95.wyzq.receiver.PhoneReceiver;
import com.lemon95.wyzq.utils.PromptManager;
import com.lemon95.wyzq.webserver.WebServiceUtils;

import java.util.Timer;
import java.util.TimerTask;

public class InCallActivity extends Activity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, Dialpad.OnDialKeyListener  {

    private static final String TAG = InCallActivity.class.getSimpleName();
    private static final int CALL = 4;
	private TextView mTextDisplayName;
    private TextView mTextCallStatus;
    private ToggleButton mToggleMute;
    private ToggleButton mToggleDailpad;
    private ToggleButton mToggleSpeaker;
    private Button mBtnHangup;
    private Button mBtnRefuse;
    private Button mBtnConnect;
    private Chronometer mCallDuration;
    private View panelBottomIncoming;
    private View panelBottomCalling;
    private Dialpad mDialPad;
    private Timer quitTimer;
    private Result result;
    private PhoneReceiver phoneReceiver;
    private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what){
				case CALL:
					if(result != null) {
						if(result.getRows().equals("0")) {
							//PromptManager.showToast(getApplicationContext(), "呼叫成功,请稍候！");
							mTextCallStatus.setText("呼叫成功,请稍候...");
							startReceiver();
						} else {
							PromptManager.showToast(getApplicationContext(), result.getMessage());
							exit();
						}
					} else {
						PromptManager.showToast(getApplicationContext(), "呼叫失败!");
						exit();
					}
					break;
			}
		};
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        setContentView(R.layout.layout_incall);
        initView();
        
//        FYCall.addListener(mFeiyuCallListener);
//        mFeiyuCall = FYCall.instance();
        Intent intent = getIntent();
        boolean flagIncoming = intent.getBooleanExtra("Flag_Incoming", false);
        final String number = intent.getStringExtra("CallNumber");
        final String zhujiao = intent.getStringExtra("zhujiao");
        if (flagIncoming) {
            panelBottomIncoming.setVisibility(View.VISIBLE);
            panelBottomCalling.setVisibility(View.GONE);
        } else {
            panelBottomIncoming.setVisibility(View.GONE);
            panelBottomCalling.setVisibility(View.VISIBLE);
            int calltype = intent.getIntExtra("CallType", -1);
            if (calltype == 2) {
                mTextDisplayName.setText(number);
                new Thread(){
        			public void run() {
        				result = WebServiceUtils.call(zhujiao,number);
        				Message msg = new Message();
        				msg.what = CALL;
        				handler.sendMessage(msg);
        			};
        		}.start();
               // mFeiyuCall.callback(number, FYCall.SHOW_NUMBER_OFF, "");
            } else if (calltype == 1) {
               // mFeiyuCall.directCall(number, FYCall.SHOW_NUMBER_OFF, "");
            } else if (calltype == 0) {
                //mFeiyuCall.networkCall(number, FYCall.SHOW_NUMBER_OFF, "");
            }
        }
        mTextDisplayName.setText(number);
        quitTimer = new Timer("Quit-timer");
    }

    protected void startReceiver() {
    	phoneReceiver = new PhoneReceiver();
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction("android.intent.action.PHONE_STATE");
    	intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(phoneReceiver, intentFilter);
	}

	@Override
    protected void onResume() {
        super.onResume();
        /*if (!FYClient.instance().isConnected()) {
            String appId = "4F239DE03CDAA5F5C6BF7CB4174D2D8E";
            String appToken = "100E71E1C170C133D69074231D2B03D6";
            String accountId = "FY4F239OHJ7M6";
            String accountPwd = "JP40WD";
            FYClient.instance().connect(appId, appToken, accountId, accountPwd);
        } else {
            LogUtils.i(TAG,"FeiyuClient already connected.");
        } */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(phoneReceiver != null) {
        	unregisterReceiver(phoneReceiver);
        }
        FYCall.removeListener(mFeiyuCallListener);
        if (quitTimer != null) {
            quitTimer.cancel();
            quitTimer.purge();
            quitTimer = null;
        }
    }

    private void initView() {
        mTextDisplayName = (TextView) findViewById(R.id.tv_display_name);
        mTextCallStatus = (TextView) findViewById(R.id.tv_call_status);

        mToggleMute = (ToggleButton) findViewById(R.id.toggle_mute);
        mToggleMute.setOnCheckedChangeListener(this);
        mToggleDailpad = (ToggleButton) findViewById(R.id.toggle_dialpad);
        mToggleDailpad.setOnCheckedChangeListener(this);
        mToggleSpeaker = (ToggleButton) findViewById(R.id.toggle_speaker);
        mToggleSpeaker.setOnCheckedChangeListener(this);

        mBtnHangup = (Button) findViewById(R.id.btn_hangup);
        mBtnHangup.setOnClickListener(this);
        mBtnRefuse = (Button) findViewById(R.id.btn_refuse);
        mBtnRefuse.setOnClickListener(this);
        mBtnConnect = (Button) findViewById(R.id.btn_connect);
        mBtnConnect.setOnClickListener(this);

        mDialPad = (Dialpad) findViewById(R.id.dialPad);
        mDialPad.setOnDialKeyListener(this);
        mCallDuration = (Chronometer) findViewById(R.id.ch_call_duration);

        panelBottomIncoming = findViewById(R.id.panel_incoming_bottom);
        panelBottomCalling = findViewById(R.id.panel_call_bottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hangup:
               // mFeiyuCall.endCall();
                mTextCallStatus.setText("正在挂断");
                if(phoneReceiver != null) {
                	phoneReceiver.toEnd();
                }
                exit();
                break;

            case R.id.btn_refuse:
               // mFeiyuCall.rejectCall();
                mTextCallStatus.setText("正在挂断");
                if(phoneReceiver != null) {
                	phoneReceiver.toEnd();
                }
                exit();
                break;

            case R.id.btn_connect:
               // mFeiyuCall.answerCall();
                break;
        }
    }

    @Override
    public void onTrigger(char dtmf) {
        Dump.d("onTrigger:" + dtmf);
        //mFeiyuCall.sendDtmf(dtmf);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.toggle_mute:
               // mFeiyuCall.setMuteEnabled(mToggleMute.isChecked());
            	//关闭声音
            	
                break;

            case R.id.toggle_dialpad:
                boolean visible = mDialPad.getVisibility() == View.VISIBLE;
                mDialPad.setVisibility(visible ? View.GONE : View.VISIBLE);
                mTextDisplayName.setVisibility(visible ? View.VISIBLE : View.GONE);
                break;

            case R.id.toggle_speaker:
            	if(phoneReceiver != null) {
            		phoneReceiver.switchspeaker();
                }
                //mFeiyuCall.setSpeakerEnabled(mToggleSpeaker.isChecked());
                break;
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private FYCallListener mFeiyuCallListener = new FYCallListener() {

        @Override
        public void onCallFailed(FYError feiyuError) {
            Dump.d("onCallFailed:" + feiyuError);
            ToastUtil.showShortToast(InCallActivity.this, "onCallFailed:"
                    + feiyuError.getMsg());
            exit();
        }

        @Override
        public void onIncomingCall(String s) {
            Dump.d("onIncomingCall:" + s);
            mTextCallStatus.setText("来电");
        }

        @Override
        public void onOutgoingCall(String s) {
            Dump.d("onOutgoingCall:" + s);
            mTextCallStatus.setText("正在呼叫");
        }

        @Override
        public void onCallRunning(String s) {
            Dump.d("onCallRunning:" + s);
            mCallDuration.setVisibility(View.VISIBLE);
            mCallDuration.setBase(SystemClock.elapsedRealtime());
            mCallDuration.start();
            mTextCallStatus.setText("");
            panelBottomIncoming.setVisibility(View.GONE);
            panelBottomCalling.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCallEnd() {
            Dump.d("onCallEnd");
            mCallDuration.stop();
            mTextCallStatus.setText("通话结束");
            exit();
        }

        @Override
        public void onCallbackSuccessful() {
            Dump.d("onCallbackSuccessful");
            ToastUtil.showShortToast(InCallActivity.this, "回拨成功，请等待来电");
            exit();
        }

        @Override
        public void onCallbackFailed(FYError feiyuError) {
            Dump.d("onCallbackFailed:" + feiyuError);
            ToastUtil.showShortToast(InCallActivity.this, "" + feiyuError.getMsg());
            exit();
        }

        @Override
        public void onCallAlerting(String s) {
            Dump.d("onCallAlerting:" + s);
        }
    };

    private void exit() {
        if (quitTimer != null) {
            quitTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else {
            finish();
        }
    }
}
