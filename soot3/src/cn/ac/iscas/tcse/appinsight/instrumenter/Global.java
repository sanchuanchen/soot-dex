package cn.ac.iscas.tcse.appinsight.instrumenter;

import java.util.Comparator;
import java.util.TreeSet;

import soot.SootClass;

public class Global {

	public static TreeSet<SootClass> sootClassSet = new TreeSet(new Comparator() {
		public int compare(Object a, Object b)
		{
			SootClass aClass = (SootClass)a;
			SootClass bClass = (SootClass)b;
			return aClass.getPackageName().compareTo(bClass.getPackageName());
		}
	});
}
