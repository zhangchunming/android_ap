package com.douwan.ap;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;

public class Wifi {
	public WifiManager wifiManager = null;
	private List<ScanResult> wifi_list = null;
	List<WifiConfiguration> wifi_configuration = null;
	long old_time = 0;
	public Wifi(Context context)
	{
		wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		wifiManager.getConnectionInfo();
	}
	
	public void openWifi()
	{
			if(!wifiManager.isWifiEnabled())
			{
				wifiManager.setWifiEnabled(true);
				Log.i("debug.info","open WIFI...");
			}
		
	}
	
	public void closeWifi()
	{
		if(wifiManager.isWifiEnabled())
		{
			wifiManager.setWifiEnabled(false);
			Log.i("debug.info","close WIFI...");
		}
	}
	
	public String checkWifiState()
	{
		String result = null;
		switch (wifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_ENABLED:
			result = "WIFI_STATE_ENABLED";
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			result = "WIFI_STATE_DISABLED";		
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			result = "WIFI_STATE_ENABLING";
			break;
		case WifiManager.WIFI_STATE_DISABLING:
			result = "WIFI_STATE_DISABLING";
			break;
		default:
			result = "UNKNOWN";
			break;
		}
		
		return result;
	}
	
	public List<ScanResult> startScan()
	{
		if(wifiManager.startScan())
		{
			wifi_list = wifiManager.getScanResults();
			/*for(int i=0;i<wifi_list.size();i++)
			{
				Log.i("debug.info","SSID " + wifi_list.size());
				Log.i("debug.info","SSID " + wifi_list.get(i).BSSID);
				sb.append(wifi_list.get(i).BSSID);
			}*/
		}	
		return wifi_list;
	}
	
	public List<WifiConfiguration> scanConfigurations()
	{
		if(wifiManager.startScan())
		{
			wifi_configuration = wifiManager.getConfiguredNetworks();
		}
		
		return wifi_configuration;
	}
	
	public WifiConfiguration IsExsits(String SSID)   
    {   
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();   
           for (WifiConfiguration existingConfig : existingConfigs)    
           {   
             if (existingConfig.SSID.equals("\""+SSID+"\""))   
             {   
            	 Log.i("debug.info","existingConfig.SSID ="+SSID);
            	 return existingConfig;   
             }   
           }   
        return null;    
    } 
	
	public String MacToSsid(String mac)
	{
		String ssid=null;
		wifi_list = startScan();
		if(mac != null)
		{
			for(int i=0;i<wifi_list.size();i++)
			{
				if(wifi_list.get(i).BSSID.equals(mac))
				{
					ssid = wifi_list.get(i).SSID;
					Log.i("debug.info","MAC:"+wifi_list.get(i).BSSID+" To "+"SSID:"+ssid);
					break;
				}
			}
		}
		return ssid;
	}
	@SuppressLint("DefaultLocale")
	public boolean isMacValid(String mac)
	{
		mac = mac.toString();
		mac = mac.toLowerCase();
		if(!mac.isEmpty())
		{
			if(mac.matches("^[a-z0-9]{2}(:[a-z0-9]{2}){5}$"))
			{
				return true;
			}

		}
		return false;
	}
	public WifiConfiguration CreateWifiInfo(String mac, String Password)  
    {  
		
		while(!wifiManager.disconnect());//close wifi
		String SSID = MacToSsid(mac);
		WifiConfiguration tempConfig = this.IsExsits(SSID);            
	    if(tempConfig != null) 
	    {   
	        wifiManager.removeNetwork(tempConfig.networkId);   
	    }
		
		WifiConfiguration config = new WifiConfiguration();    
	    config.allowedAuthAlgorithms.clear();  
	    config.allowedGroupCiphers.clear();  
	    config.allowedKeyManagement.clear();  
	    config.allowedPairwiseCiphers.clear();  
	    config.allowedProtocols.clear();
	      
	    config.SSID = "\"" + SSID + "\""; 
	//  config.preSharedKey = "\"\"";
	
	    config.hiddenSSID = false;
	//  config.status = WifiConfiguration.Status.ENABLED;
	//  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	//  config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
	//  config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
	//  config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	    config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);// WPA2/IEEE 802.11i 
	      
	    return config;  
    }
	
	public void addNetwork(WifiConfiguration wcg) {  
	     int wcgID = wifiManager.addNetwork(wcg); 
//	     Log.i("debug.info","networkID="+wcgID);
	     boolean b =  wifiManager.enableNetwork(wcgID, true); 
	     old_time = SystemClock.uptimeMillis();
	     Log.i("debug.info","networkID="+wcgID+" EnableNetwork "+b);
	 }
	
	public  boolean getDhcp()
	{
		DhcpInfo info = wifiManager.getDhcpInfo();
		int i = 0;
		i = info.ipAddress;
		if(i != 0)
		{
			Log.i("debug.info","ip "+(i & 0xFF) + "." + 
					((i >> 8) & 0xFF) + "." +  
					((i >> 16) & 0xFF) + "." +  
					(i >> 24 & 0xFF));
			return true;
		}
		return false;
	}
	
	public int isWifiConnected(Context context) 
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifiNetworkInfo.isConnected()) {
			return 0;
		}
		long new_time = SystemClock.uptimeMillis();
		if((new_time-old_time) >= 30*1000)
		{
			return -1;
		}
//		Log.i("debug.info","ip "+wifiManager.getDhcpInfo().ipAddress);
//		return false;
		return 1;
	}
	
}















