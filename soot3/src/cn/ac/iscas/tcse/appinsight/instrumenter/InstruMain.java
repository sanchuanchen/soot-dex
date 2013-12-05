package cn.ac.iscas.tcse.appinsight.instrumenter;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.LongType;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;

public class InstruMain
{
	public static void main(String[] args)
	{
		InstruMain instruMain = new InstruMain();
		instruMain.instru(args);
	}
	
	public void instru(String[] args)
	{
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_format(Options.output_format_dex);
		Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.String", SootClass.SIGNATURES);
		Scene.v().addBasicClass("cn.ac.iscas.tcse.appinsight.instrumenter.Hello",SootClass.SIGNATURES);
//		Scene.v().addBasicClass("cn.ac.iscas.tcse.appinsight.UDID.UDIDManager",SootClass.SIGNATURES);
//		Scene.v().addBasicClass("cn.ac.iscas.tcse.appinsight.connectionqueue.ConnectionQueue",SootClass.SIGNATURES);
//		Scene.v().addBasicClass("cn.ac.iscas.tcse.appinsight.event.Event",SootClass.SIGNATURES);
//		Scene.v().addBasicClass("cn.ac.iscas.tcse.appinsight.eventqueue.EventQueue",SootClass.SIGNATURES);
//		Scene.v().addBasicClass("cn.ac.iscas.tcse.appinsight.info.DeviceInfo",SootClass.SIGNATURES);
//		Scene.v().addBasicClass("cn.ac.iscas.tcse.appinsight.api.AppInsight",SootClass.SIGNATURES);
		Scene.v().addBasicClass("android.widget.Toast", SootClass.SIGNATURES);
		Scene.v().addBasicClass("com.chen.MyHttp", SootClass.SIGNATURES);
		Scene.v().addBasicClass("com.chen.OuterFun", SootClass.SIGNATURES);
		
		
		MonitorInstrumenter monitorInstrumenter = new MonitorInstrumenter();
		
		PackManager.v().getPack("jtp").add(new Transform(
				"jtp.myInstrumenter", monitorInstrumenter));
		
		soot.Main.main(args);
	}
}
