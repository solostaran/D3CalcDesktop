package jodroid.d3calc;

import java.io.PrintStream;

public class LogDesktop implements ILog {
	
	private PrintStream out;
	private PrintStream err;
	
	public LogDesktop() {
		out = System.out;
		err = System.err;
	}
	public LogDesktop(PrintStream ps) {
		out = ps;
	}
	public LogDesktop(PrintStream out, PrintStream err) {
		this.out = out;
		this.err = err;
	}
	public PrintStream getOutputStream() {
		return out;
	}
	public void setOutputStream(PrintStream ps) {
		out = ps;
	}
	public PrintStream getErrorStream() {
		return err;
	}
	public void setErrorStream(PrintStream ps) {
		err = ps;
	}

	@Override
	public void v(String tag, String message) {
		out.println("V:"+ tag +" | "+ message);
	}

	@Override
	public void d(String tag, String message) {
		out.println("DEBUG:"+ tag +" | "+ message);
	}

	@Override
	public void i(String tag, String message) {
		out.println("INFO:"+ tag +" | "+ message);
	}

	@Override
	public void w(String tag, String message) {
		out.println("WARNING:"+ tag +" | "+ message);
	}

	@Override
	public void e(String tag, String message) {
		err.println("ERROR:"+ tag +" | "+ message);
	}

	@Override
	public void wtf(String tag, String message) {
		err.println("W.T.F.!!!!:"+ tag +" | "+ message);
	}

	@Override
	public void println(int priority, String tag, String msg) {
		if (priority < 2) e(tag, msg);
		else v(tag, msg);
	}

}
