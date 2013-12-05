package com.chen;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MyHttp {

	
	public static void hi()
	{
		String serverURL = new String("hi, everyone!");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try{
		HttpGet method;
		method = new HttpGet(new URI(serverURL + "/i?"));
		HttpResponse response;
		response = httpClient.execute(method);
		InputStream input;
		input = response.getEntity().getContent();
		while (input.read() != -1)
		httpClient.getConnectionManager().shutdown();
		}
		catch(Exception e)
		{
		}
		
	}
	
}
