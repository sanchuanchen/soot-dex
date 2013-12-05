package cn.ac.iscas.tcse.appinsight.instrumenter;

public class Hello
{
	static String s = "HELLO";
	
	public static String replaceString(String string)
	{
		string = s;
		return string;
	}
}
