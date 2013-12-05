package cn.ac.iscas.tcse.appinsight.thread;

import java.util.TimerTask;

import cn.ac.iscas.tcse.appinsight.api.AppInsight;

public class AppInsightTimerTask extends TimerTask {

	AppInsight appInsight;
	
	public AppInsightTimerTask(AppInsight appInsight) {
		this.appInsight = appInsight;
	}
	
	@Override
	public void run() {
		appInsight.onTimer();
	}

}
