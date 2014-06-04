package android.util;

import java.io.PrintStream;

public class Log {
	private static PrintStream out;
	
	static {
		out = System.out;
	}
	
	public static void setPrintStream(PrintStream ps) {
		out = ps;
	}
	
	public static void v(String tag, String message) {
		out.println("V:"+ tag +" | "+ message);
	}
	
	public static void d(String tag, String message) {
		out.println("DEBUG:"+ tag +" | "+ message);
	}
	
	public static void i(String tag, String message) {
		out.println("INFO:"+ tag +" | "+ message);
	}
	
	public static void w(String tag, String message) {
		out.println("WARNING:"+ tag +" | "+ message);
	}
	
	public static void e(String tag, String message) {
		out.println("ERROR:"+ tag +" | "+ message);
	}
}
