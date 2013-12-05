package cn.ac.iscas.tcse.appinsight.instrumenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.IntType;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.BooleanType;
import soot.dava.internal.javaRep.DIntConstant;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.util.Chain;

public class MonitorInstrumenter extends BodyTransformer
{

	static public SootClass apiClass, udidManagerClass, udidServiceClass,
		helloClass, toastClass;
		
	
	
	@Override
	protected void internalTransform(final Body b, String phaseName, Map options)
	{
		
		
//		System.out.println("***"+b.getMethod().getName());
		
		SootClass tmpSootClass = b.getMethod().getDeclaringClass();
		
		while(true)
		{
			
			if(tmpSootClass.getName().equals("java.lang.Object"))
				return;
			if(tmpSootClass.getName().equals("android.content.Context"))
				break;
			tmpSootClass = tmpSootClass.getSuperclass();
			
		}
		

		if(!b.getMethod().getName().equals("onCreate"))
		{
			return;
			
		}
		
		
		final PatchingChain units = b.getUnits();	
		Unit tmpUnit;
		// add some locals
		Local r0,m1,m2,m3,m4,m5,m6;
		SootClass declaringClass = b.getMethod().getDeclaringClass();
		Chain npUnits = units.getNonPatchingChain();	

		//r0 is the local variable "$r0"
		r0 = b.getLocals().getFirst();
		m1 = Jimple.v().newLocal("m1", RefType.v("android.content.Context"));
		m2 = Jimple.v().newLocal("m2", IntType.v());
		m3 = Jimple.v().newLocal("m3", RefType.v("android.widget.Toast"));
		m4 = Jimple.v().newLocal("m4", RefType.v("java.lang.String"));
//		m5 = Jimple.v().newLocal("m5", RefType.v("cn.ac.iscas.tcse.appinsight.api.AppInsight"));
		m6 = Jimple.v().newLocal("m6", RefType.v("com.chen.OuterFun"));
		b.getLocals().add(m1);
		b.getLocals().add(m2);
		b.getLocals().add(m3);
		b.getLocals().add(m4);
//		b.getLocals().add(m5);
		b.getLocals().add(m6);

		//m1 = context
		Unit baseUnit = null;
		tmpUnit = null;
		Iterator<Unit> unitIt = npUnits.iterator();
		while (unitIt.hasNext())
		{
			tmpUnit = unitIt.next();

			if (tmpUnit instanceof IdentityStmt)
			{
				baseUnit = tmpUnit;
			}
			baseUnit = tmpUnit;
		}
		SootMethod getConMethod = Scene
				.v()
				.getMethod(
						"<android.content.ContextWrapper: android.content.Context getApplicationContext()>");
	
		//m1=getApplicationContext()
		System.out.println(getConMethod);
		npUnits.insertBefore(
				Jimple.v().newAssignStmt(m1,
						Jimple.v().newSpecialInvokeExpr(r0,
								getConMethod.makeRef())),baseUnit);

		
		//m2 = Toast.LENGTH_SHORT
		npUnits.insertBefore(
				Jimple.v()
						.newAssignStmt(
								m2,
								Jimple.v()
										.newStaticFieldRef(
												Scene.v()
														.getField(
																"<android.widget.Toast: int LENGTH_SHORT>")
														.makeRef())),baseUnit);

		
		
		//m3 = Toast.makeText(m1,,m2)
		SootMethod makeTextMethod = Scene.v().
				getMethod("<android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>");
		npUnits.insertBefore(
				Jimple.v().newAssignStmt(
						m3,
						Jimple.v().newStaticInvokeExpr(
								makeTextMethod.makeRef(), m1,
								StringConstant.v("Hello world!"), m2)),
				baseUnit);

		//m3.show()
		SootMethod showMethod = Scene.v().getMethod("<android.widget.Toast: void show()>");
		npUnits.insertBefore(
				Jimple.v().newInvokeStmt(
						Jimple.v().newVirtualInvokeExpr(m3,
								showMethod.makeRef())), baseUnit);
		
		
		//m4 = Hello.replaceString("Hello world")
		SootMethod replaceStringMethod = Scene
				.v()
				.getMethod(
						"<cn.ac.iscas.tcse.appinsight.instrumenter.Hello: java.lang.String replaceString(java.lang.String)>");
		npUnits.insertBefore(
				Jimple.v().newAssignStmt(
						m4,
						Jimple.v().newStaticInvokeExpr(
								replaceStringMethod.makeRef(),
								StringConstant.v("Hello world!"))), baseUnit);
		
		//m3 = Toast.makeText(m1,m4,m2)
		npUnits.insertBefore(
				Jimple.v().newAssignStmt(
						m3,
						Jimple.v().newStaticInvokeExpr(
								makeTextMethod.makeRef(), m1, m4, m2)),
				baseUnit);
		
		//m3.show()
		npUnits.insertBefore(
				Jimple.v().newInvokeStmt(
						Jimple.v().newVirtualInvokeExpr(m3,
								showMethod.makeRef())), baseUnit);
		
//		//m5 = AppInsight.sharedInstance()
//		SootMethod sharedInstanceMethod = Scene
//				.v()
//				.getMethod(
//						"<cn.ac.iscas.tcse.appinsight.api.AppInsight: cn.ac.iscas.tcse.appinsight.api.AppInsight sharedInstance()>");
//		npUnits.insertBefore(Jimple.v().newAssignStmt(m5, Jimple.v().newStaticInvokeExpr(sharedInstanceMethod.makeRef())), baseUnit);
//		
//		
//		//m5.init(Context,String,String)
//		SootMethod initMethod = Scene
//				.v()
//				.getMethod(
//						"<cn.ac.iscas.tcse.appinsight.api.AppInsight: void init(android.content.Context,java.lang.String,java.lang.String)>");
//		List<Value> methodArg = new ArrayList<Value>();
//		methodArg.add(m1);
//		methodArg.add(StringConstant.v("http://133.133.133.84"));
//		methodArg.add(StringConstant
//				.v("7aafcf9e4281903db8e61e352e9c84864cc3d3ae"));
//		npUnits.insertBefore(
//				Jimple.v().newInvokeStmt(
//						Jimple.v().newVirtualInvokeExpr(m5,
//								initMethod.makeRef(), methodArg)), baseUnit);
//		
//		
//		//m5.path(Context,boolean)
//		SootMethod pathMethod = Scene
//				.v()
//				.getMethod(
//						"<cn.ac.iscas.tcse.appinsight.api.AppInsight: void path(android.content.Context,boolean)>");
//		npUnits.insertBefore(
//				Jimple.v().newInvokeStmt(
//						Jimple.v().newVirtualInvokeExpr(m5,
//								pathMethod.makeRef(), r0,
//								DIntConstant.v(1, BooleanType.v()))), baseUnit);
		
		
		//m6.out()
//		SootMethod outMethod = Scene.v().getMethod("<com.chen.OuterFun: void out()>");
//		npUnits.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(outMethod.makeRef())), baseUnit);
//		
		Chain<SootClass> sceneClasses = Scene.v().getClasses();
		Iterator<SootClass> iter = sceneClasses.iterator();
		while(iter.hasNext())
		{
			SootClass sclass = iter.next();
			System.out.println(sclass.getName());
		}
		
		//print and check
		unitIt = npUnits.iterator();
		tmpUnit = null;
		while (unitIt.hasNext())
		{
			tmpUnit = unitIt.next();
			System.out.println(tmpUnit);
			
		}
		
		System.out.println("hi");
		
		
		b.validate();
		
			
		
		
		
	}

}
