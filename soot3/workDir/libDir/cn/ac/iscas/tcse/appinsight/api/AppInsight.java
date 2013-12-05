package cn.ac.iscas.tcse.appinsight.api;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import cn.ac.iscas.tcse.appinsight.UDID.UDIDManager;
import cn.ac.iscas.tcse.appinsight.connectionqueue.ConnectionQueue;
import cn.ac.iscas.tcse.appinsight.event.Event;
import cn.ac.iscas.tcse.appinsight.eventqueue.EventQueue;
import cn.ac.iscas.tcse.appinsight.info.DeviceInfo;
import cn.ac.iscas.tcse.appinsight.thread.AppInsightTimerTask;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class AppInsight {
	private static AppInsight sharedInstance_;
	private Timer timer_;
	private ConnectionQueue queue_;
	private EventQueue eventQueue_;
	private boolean isVisible_;
	private double unsentSessionLength_;
	private double lastTime_;
	private int activityCount_;
	private boolean init = false;

	static public AppInsight sharedInstance()
	{
		if (sharedInstance_ == null)
			sharedInstance_ = new AppInsight();
		
		return sharedInstance_;
	}
	
	private AppInsight()
	{
		queue_ = new ConnectionQueue();
		eventQueue_ = new EventQueue();
		timer_ = new Timer();
		AppInsightTimerTask appInsightTimerTask = new AppInsightTimerTask(this);
		timer_.schedule(appInsightTimerTask, 30 * 1000,  30 * 1000);

		isVisible_ = false;
		unsentSessionLength_ = 0;
		activityCount_ = 0;
	}
	
	public void init(Context context, String serverURL, String appKey)
	{
		if(init == false)
		{
		UDIDManager.sync(context);
		queue_.setContext(context);
		queue_.setServerURL(serverURL);
		queue_.setAppKey(appKey);
		init = true;
		}
	}

	public void onStart()
	{
		activityCount_++;
		if (activityCount_ == 1)
			onStartHelper();
	}

	public void onStop()
	{
		activityCount_--;
		if (activityCount_ == 0)
			onStopHelper();
	}

	public void onStartHelper()
	{
		lastTime_ = System.currentTimeMillis() / 1000.0;

		queue_.beginSession();

		isVisible_ = true;
	}

	public void onStopHelper()
	{
		if (eventQueue_.size() > 0)
			queue_.recordEvents(eventQueue_.events());

		double currTime = System.currentTimeMillis() / 1000.0;
		unsentSessionLength_ += currTime - lastTime_;

		int duration = (int)unsentSessionLength_;
		queue_.endSession(duration);
		unsentSessionLength_ -= duration;

		isVisible_ = false;
	}
	
	public void recordEvent(String key, int count)
	{
		eventQueue_.recordEvent(key, count);

		if (eventQueue_.size() >= 5)
			queue_.recordEvents(eventQueue_.events());
	}

	public void recordEvent(String key, int count, double sum)
	{
		eventQueue_.recordEvent(key, count, sum);

		if (eventQueue_.size() >= 5)
			queue_.recordEvents(eventQueue_.events());		
	}

	public void recordEvent(String key, Map<String, String> segmentation, int count)
	{
		eventQueue_.recordEvent(key, segmentation, count);
		
		if (eventQueue_.size() >= 5)
			queue_.recordEvents(eventQueue_.events());		
	}

	public void recordEvent(String key, Map<String, String> segmentation, int count, double sum)
	{
		eventQueue_.recordEvent(key, segmentation, count, sum);
		
		if (eventQueue_.size() >= 5)
			queue_.recordEvents(eventQueue_.events());		
	}
	
	//@author sanchuan
	public void path(Context context, boolean b)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		long currentTime = System.currentTimeMillis();
		String s = String.valueOf(currentTime);
		map.put("time",s);
		if(b) map.put("type", "resume");
		else map.put("type", "pause");
		map.put("context", context.toString());
		AppInsight.sharedInstance().recordEvent("path", map, 1);
	}
	
	public void onTimer()
	{
		if (isVisible_ == false)
			return;
		
		double currTime = System.currentTimeMillis() / 1000.0;
		unsentSessionLength_ += currTime - lastTime_;
		lastTime_ = currTime;
		
		int duration = (int)unsentSessionLength_;
		queue_.updateSession(duration);
		unsentSessionLength_ -= duration;

		if (eventQueue_.size() > 0)
			queue_.recordEvents(eventQueue_.events());		
	}
}