package com.douwan.ap;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.douwan.ap.MainActivity.MsgReceiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class WifiService extends Service {

	private Wifi wifi = null;	
	private List<ScanResult> wifi_list = null;
	private final int DHCP_FAIL = 1;
	private final int WEB_BACK = 2;
	private final int UPDATE_LISTVIEW = 3;
	private final int PORTAL_FAIL = 4;
	public static Handler myHandler = null;
	private MsgReceiver msgReceiver = null;
	long new_time = 0;
	String no_redirect = null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i("debug.info", "onBind");
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		recBroadcast();
		wifi = new Wifi(WifiService.this);
		myHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				Intent it = new Intent("com.douwan.ap.WifiService");
				switch(msg.what)
				{
				
				case UPDATE_LISTVIEW:	
					it.putExtra("wifiservice","update_listview");
					sendBroadcast(it);
					Log.i("debug.info", "sendBroadcast:update_listview");
					break;
				case WEB_BACK:
					it.putExtra("wifiservice","web_back");
					it.putExtra("time",new_time);
					it.putExtra("redirect", no_redirect);
					Log.i("debug.info", "sendBroadcast:web_back");
					sendBroadcast(it);
					break;
				case DHCP_FAIL:
					it.putExtra("wifiservice","dhcpfail");
					Log.i("debug.info", "sendBroadcast:dhcpfail");
					sendBroadcast(it);
					break;
				}
				super.handleMessage(msg);
			}	
		};
		Log.i("debug.info", "onCreate");
		
	}
	
	public void execute()
	{
		executeListView();
	}
	
	public void executeListView()
	{
		Timer timer = new Timer(true);
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = UPDATE_LISTVIEW;
				myHandler.sendMessage(message);
			}
		};
		timer.schedule(timerTask, 1000, 10000);
	}  
	
	public void recBroadcast()
	{
		msgReceiver = new MsgReceiver();  
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction("com.douwan.ap.web");
        intentFilter.addAction("com.douwan.ap.mainActivity");
        registerReceiver(msgReceiver, intentFilter);
	}
	
	public class MsgReceiver extends BroadcastReceiver{  
		  
        @Override  
        public void onReceive(Context context, Intent intent) {   
        	receiveWebBroadcast(intent);
        	
        }  
          
    }
	
	public void receiveWebBroadcast(Intent intent)
	{
		Message message = new Message();
    	switch(intent.getStringExtra("web"))
    	{
    	case "web_back":
    		Log.i("debug.info", "web_back");
    		new_time = intent.getExtras().getLong("time");
    		no_redirect = intent.getExtras().getString("redirect");
			message.what = WEB_BACK;
			myHandler.sendMessage(message);
			break;
    	}
    	
	}
	
	public void processOpenWifi(Intent intent,Message message)
	{
		switch(intent.getStringExtra("mainActivity"))
    	{
    	case "dhcpfail":
    		message.what = DHCP_FAIL;
    		myHandler.sendMessage(message);
    	}
	}
	

	
	public class MyBinder extends Binder{  
        
        public WifiService getService(){  
            return WifiService.this;  
        }  
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

		
	
}
