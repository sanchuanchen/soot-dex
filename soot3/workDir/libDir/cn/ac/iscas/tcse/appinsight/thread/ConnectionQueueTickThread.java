package cn.ac.iscas.tcse.appinsight.thread;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import cn.ac.iscas.tcse.appinsight.UDID.UDIDManager;
import cn.ac.iscas.tcse.appinsight.connectionqueue.ConnectionQueue;

public class ConnectionQueueTickThread extends Thread {

	private ConnectionQueue connectionQueue;
	
	public ConnectionQueueTickThread(ConnectionQueue connectionQueue)
	{
		this.connectionQueue = connectionQueue;
	}
	
	public void run()
	{
		while (true)
		{
			String data = connectionQueue.queue_.peek();

			if (data == null)
				break;
			
			int index = data.indexOf("REPLACE_UDID");
			if (index != -1)
			{
				if (UDIDManager.isInitialized() == false)
					break;						
				data.replaceFirst("REPLACE_UDID", UDIDManager.getOpenUDID());						
			}
			
			
			
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			HttpGet method = null;
//			try {
//				method = new HttpGet(new URI(connectionQueue.serverURL_ + "/i?" + data));
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}			
//			HttpResponse response =null;
//			try {
//				response = httpClient.execute(method);
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			InputStream input = null;
//			try {
//				input = response.getEntity().getContent();
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			try {
//				while (input.read() != -1)
//					;
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			httpClient.getConnectionManager().shutdown();
//									
//			Log.d("Countly", "ok ->" + data);
//
//			connectionQueue.queue_.poll();
			
			
			
			
			
//			catch(URISyntaxException e)
//			{
//				Log.d("Countly", e.toString());
//				Log.d("Countly", "error ->" + data);
//				break;
//			}
//			catch(ClientProtocolException e)
//			{
//				Log.d("Countly", e.toString());
//				Log.d("Countly", "error ->" + data);
//				break;
//			}			
//			catch(IOException e)
//			{
//				Log.d("Countly", e.toString());
//				Log.d("Countly", "error ->" + data);
//				break;
//			}			
			
			
			
			
			
			
		}
		
	}

}
