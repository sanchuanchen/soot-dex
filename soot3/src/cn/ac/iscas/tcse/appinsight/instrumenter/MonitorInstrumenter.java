package cn.ac.iscas.tcse.appinsight.instrumenter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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

/** our instrumenter class */
public class MonitorInstrumenter extends BodyTransformer
{

	private SootClass declaringClass;
	
	private SootMethod getConMethod,makeTextMethod,showMethod,replaceStringMethod;
	
	private PatchingChain units;
	
	private Chain npUnits;
	
	private Unit tmpUnit,baseUnit;
	
	private Local r0,m1,m2,m3,m4,m5,m6;
	
	private Iterator<Unit> unitIt;
	
	private boolean hasOnCreate = false, hasOnStart = false,hasOnStop = false,
			hasOnResume = false,hasOnPause = false;
	
	private SootMethod onCreateMethod, onStartMethod, onStopMethod, onResumeMethod, onPauseMethod;
	
	@Override
	/**
	 * main procedure 
	 */
	protected void internalTransform(final Body b, String phaseName, Map options)
	{
		
		
//		System.out.println("***"+b.getMethod().getName());
		
		//check if the declaring class has been analyzed
		if(Global.sootClassSet.contains(b.getMethod().getDeclaringClass()))
			return;
		
		//check if it is an activity
		if(!checkActivity(b))
			return;
		
		getOtherInformation(b);
		
		analyzeAndInstruClass(b);
		
		storeClass(b);
		
		/*
		
		if(!checkOnCreate(b))
			return;
		
		init(b);
		
		instrument(b);
		
		*/
		
	}
	
	
	void analyzeAndInstruClass(Body b)
	{
		//init(b);
		
		if(hasOnCreate)
			instrumentOnCreate1(b);
		else
			instrumentOnCreate2(b);
		
		//TBD
		
		
		
	}
	
	
	void storeClass(Body b)
	{
		SootClass sootClass = b.getMethod().getDeclaringClass();
		Global.sootClassSet.add(sootClass);
	}
	
	
	/**
	 * check if it is an activity 
	 */
	private boolean checkActivity(Body b)
	{
		boolean isActivityClass = false;
		declaringClass = b.getMethod().getDeclaringClass();
		while(true)
		{
			//haven't find, return
			if(declaringClass.getName().equals("java.lang.Object"))
				break;
			
			//if current class is activity
			if(declaringClass.getName().equals("android.app.Activity"))
			{
				isActivityClass = true;
				break;
			}
			
			//find its super class
			declaringClass = declaringClass.getSuperclass();
		}
		
		return isActivityClass;
	}
	
	/**
	 * check if this declaring class has been analyzed
	 * @param b
	 * @return
	 */
	private boolean checkAnalysed(Body b)
	{
		boolean hasAnalysed = false;
		SootClass sootClass = b.getMethod().getDeclaringClass();
		if(Global.sootClassSet.contains(sootClass))
				hasAnalysed = true;
		return hasAnalysed;
	}
	
	
	/**
	 * get other information
	 * store the information into TreeSet<SootClassInformation> sootClassInfoSet
	 */
	private void getOtherInformation(Body b)
	{
		
		SootClass sootClass = b.getMethod().getDeclaringClass();
		ArrayList<SootMethod> sootMethodList = (ArrayList<SootMethod>)sootClass.getMethods();
		Iterator<SootMethod> sootMethodIterator = sootMethodList.iterator();
		SootMethod sootMethod;
		while(sootMethodIterator.hasNext())
		{
			sootMethod = sootMethodIterator.next();
			if(sootMethod.getSubSignature().equals("void onCreate(android.os.Bundle)"))
			{
				hasOnCreate = true;
				onCreateMethod = sootMethod;
			}
			if(sootMethod.getSubSignature().equals("void onStart()"))
			{
				hasOnStart = true;
				onStartMethod = sootMethod;
			}
			if(sootMethod.getSubSignature().equals("void onStop()"))
			{
				hasOnStop = true;
				onStopMethod = sootMethod;
			}
			if(sootMethod.getSubSignature().equals("void onResume()"))
			{
				hasOnResume = true;
				onResumeMethod = sootMethod;
			}
			if(sootMethod.getSubSignature().equals("void onPause()"))
			{
				hasOnPause = true;
				onPauseMethod = sootMethod;
			}
		}	
	}		
	
	
	
	private void instrumentOnCreate1(Body b)
	{
		Body body = onCreateMethod.getActiveBody();
		units = body.getUnits();	
		declaringClass = body.getMethod().getDeclaringClass();
		npUnits = units.getNonPatchingChain();
		r0 = body.getLocals().getFirst();
		m1 = Jimple.v().newLocal("m1", RefType.v("android.content.Context"));
		m2 = Jimple.v().newLocal("m2", IntType.v());
		m3 = Jimple.v().newLocal("m3", RefType.v("android.widget.Toast"));
		body.getLocals().add(m1);
		body.getLocals().add(m2);
		body.getLocals().add(m3);
		unitIt = npUnits.iterator();
		baseUnit = (Unit)npUnits.getFirst();
		boolean preIdentStmt = true;
		while (unitIt.hasNext())
		{
			tmpUnit = unitIt.next();

			if (tmpUnit instanceof IdentityStmt)
				preIdentStmt = true;
			else
			{
				if(preIdentStmt)
					baseUnit = tmpUnit;
				preIdentStmt = false;
			}
			//baseUnit = tmpUnit;
		}
		
		//print
		System.out.println("tmp unit is: " + tmpUnit);
		System.out.println("base unit is: " + baseUnit);
		
		//m1=getApplicationContext()
		getConMethod = Scene
				.v()
				.getMethod(
						"<android.content.ContextWrapper: android.content.Context getApplicationContext()>");
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
		makeTextMethod = Scene.v().
				getMethod("<android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>");
		npUnits.insertBefore(
				Jimple.v().newAssignStmt(
						m3,
						Jimple.v().newStaticInvokeExpr(
								makeTextMethod.makeRef(), m1,
								StringConstant.v("Hello world!"), m2)),
				baseUnit);

		//m3.show()
		showMethod = Scene.v().getMethod("<android.widget.Toast: void show()>");
		npUnits.insertBefore(
				Jimple.v().newInvokeStmt(
						Jimple.v().newVirtualInvokeExpr(m3,
								showMethod.makeRef())), baseUnit);
		
		//print
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
		
		//validate
		b.validate();
	}
	
	
	private void instrumentOnCreate2(Body b)
	{
		
	}
	
	
	
	
	
	
	/** 
	 * check if the body is OnCreate method body
	 * @return true if yes
	 * false if no 
	 */
	private boolean checkOnCreate(Body b)
	{
		if(!b.getMethod().getName().equals("onCreate"))
			return false;
		return true;
	}
	
	
	/**
	 * initialize variables and locals
	 */
	private void init(Body b)
	{
		initVariable(b);
		initLocals(b);
	}
	/**
	 * initialize variables
	 */
	private void initVariable(Body b)
	{
		units = b.getUnits();	
		declaringClass = b.getMethod().getDeclaringClass();
		npUnits = units.getNonPatchingChain();	
	}
	/**
	 * initialize locals
	 */
	private void initLocals(Body b)
	{
		// add some locals
		//r0 is the local variable "$r0"
		r0 = b.getLocals().getFirst();
		m1 = Jimple.v().newLocal("m1", RefType.v("android.content.Context"));
		m2 = Jimple.v().newLocal("m2", IntType.v());
		m3 = Jimple.v().newLocal("m3", RefType.v("android.widget.Toast"));
//		m4 = Jimple.v().newLocal("m4", RefType.v("java.lang.String"));
//		m5 = Jimple.v().newLocal("m5", RefType.v("cn.ac.iscas.tcse.appinsight.api.AppInsight"));
//		m6 = Jimple.v().newLocal("m6", RefType.v("com.chen.OuterFun"));
		b.getLocals().add(m1);
		b.getLocals().add(m2);
		b.getLocals().add(m3);
//		b.getLocals().add(m4);
//		b.getLocals().add(m5);
//		b.getLocals().add(m6);
	}
	
	
	/**
	 * instrument instructions
	 * @param b
	 */
	private void instrument(Body b)
	{
		//m1 = context
		unitIt = npUnits.iterator();
		while (unitIt.hasNext())
		{
			tmpUnit = unitIt.next();

			if (tmpUnit instanceof IdentityStmt)
			{
				baseUnit = tmpUnit;
			}
			baseUnit = tmpUnit;
		}
		getConMethod = Scene
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
		makeTextMethod = Scene.v().
				getMethod("<android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>");
		npUnits.insertBefore(
				Jimple.v().newAssignStmt(
						m3,
						Jimple.v().newStaticInvokeExpr(
								makeTextMethod.makeRef(), m1,
								StringConstant.v("Hello world!"), m2)),
				baseUnit);

		//m3.show()
		showMethod = Scene.v().getMethod("<android.widget.Toast: void show()>");
		npUnits.insertBefore(
				Jimple.v().newInvokeStmt(
						Jimple.v().newVirtualInvokeExpr(m3,
								showMethod.makeRef())), baseUnit);
		
		
//		//m4 = Hello.replaceString("Hello world")
//		replaceStringMethod = Scene
//				.v()
//				.getMethod(
//						"<cn.ac.iscas.tcse.appinsight.instrumenter.Hello: java.lang.String replaceString(java.lang.String)>");
//		npUnits.insertBefore(
//				Jimple.v().newAssignStmt(
//						m4,
//						Jimple.v().newStaticInvokeExpr(
//								replaceStringMethod.makeRef(),
//								StringConstant.v("Hello world!"))), baseUnit);
//		
//		//m3 = Toast.makeText(m1,m4,m2)
//		npUnits.insertBefore(
//				Jimple.v().newAssignStmt(
//						m3,
//						Jimple.v().newStaticInvokeExpr(
//								makeTextMethod.makeRef(), m1, m4, m2)),
//				baseUnit);
//		
//		//m3.show()
//		npUnits.insertBefore(
//				Jimple.v().newInvokeStmt(
//						Jimple.v().newVirtualInvokeExpr(m3,
//								showMethod.makeRef())), baseUnit);
//		
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
