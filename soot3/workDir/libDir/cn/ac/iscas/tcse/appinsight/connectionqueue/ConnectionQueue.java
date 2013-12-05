package cn.ac.iscas.tcse.appinsight.connectionqueue;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import cn.ac.iscas.tcse.appinsight.UDID.UDIDManager;
import cn.ac.iscas.tcse.appinsight.info.DeviceInfo;
import cn.ac.iscas.tcse.appinsight.thread.ConnectionQueueTickThread;

public class ConnectionQueue {
	public ConcurrentLinkedQueue<String> queue_ = new ConcurrentLinkedQueue<String>();
	public ConnectionQueueTickThread thread_ = new ConnectionQueueTickThread(this);;
	public String appKey_;
	public Context context_;
	public String serverURL_;
	
	public void setAppKey(String appKey)
	{
		appKey_ = appKey;
	}

	public void setContext(Context context)
	{
		context_ = context;
	}
	
	public void setServerURL(String serverURL)
	{
		serverURL_ = serverURL;
	}
	
	public void beginSession()
	{
		String data;
		data  =       "app_key=" + appKey_;
		data += "&" + "device_id=" + DeviceInfo.getUDID();
		data += "&" + "timestamp=" + (long)(System.currentTimeMillis() / 1000.0);
		data += "&" + "sdk_version=" + "1.0";
		data += "&" + "begin_session=" + "1";
		data += "&" + "metrics=" + DeviceInfo.getMetrics(context_);
		
		queue_.offer(data);		
	
		int i = 0;
		tick();
	}

	public void updateSession(int duration)
	{
		String data;
		data  =       "app_key=" + appKey_;
		data += "&" + "device_id=" + DeviceInfo.getUDID();
		data += "&" + "timestamp=" + (long)(System.currentTimeMillis() / 1000.0);
		data += "&" + "session_duration=" + duration;

		queue_.offer(data);		

		tick();
	}
	
	public void endSession(int duration)
	{
		String data;
		data  =       "app_key=" + appKey_;
		data += "&" + "device_id=" + DeviceInfo.getUDID();
		data += "&" + "timestamp=" + (long)(System.currentTimeMillis() / 1000.0);
		data += "&" + "end_session=" + "1";
		data += "&" + "session_duration=" + duration;

		queue_.offer(data);		
		
		tick();
	}
	
	public void recordEvents(String events)
	{
		String data;
		data  =       "app_key=" + appKey_;
		data += "&" + "device_id=" + DeviceInfo.getUDID();
		data += "&" + "timestamp=" + (long)(System.currentTimeMillis() / 1000.0);
		data += "&" + "events=" + events;

		queue_.offer(data);		
		
		tick();		
	}
	
	public void tick()
	{
		if (thread_ != null && thread_.isAlive())
			return;
		
		if (queue_.isEmpty())
			return;
				
		thread_ = new ConnectionQueueTickThread(this);

		thread_.start();
		
		
	}
}
