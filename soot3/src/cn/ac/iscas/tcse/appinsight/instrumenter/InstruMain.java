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


//project entry and main procedure
public class InstruMain
{
	
	static private InstruMain instruMain = new InstruMain();;
	MonitorInstrumenter monitorInstrumenter;
	
	//class main method
	public static void main(String[] args)
	{
		instruMain.instru(args);
	}
	
	//primary procedure
	public void instru(String[] args)
	{
		setOption();
		addClasses();
		registerInstrumenter();
		soot.Main.main(args);
	}
	
	//set android specified options
	private void setOption()
	{
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_output_format(Options.output_format_dex);
	}
	
	//pre load needed classes
	private void addClasses()
	{
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
	}
	
	
	//register a new instrumenter to instrument android apps.
	private void registerInstrumenter()
	{
		monitorInstrumenter = new MonitorInstrumenter();
		
		PackManager.v().getPack("jtp").add(new Transform(
				"jtp.myInstrumenter", monitorInstrumenter));
	}
}
