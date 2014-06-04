package jopc.d3calc;

import java.text.DateFormat;
import java.util.Date;

import jodroid.d3calc.D3ContextInt;
import jodroid.d3calc.R;
import android.content.Context;

/**
 * Platform dependent context.
 * @author JRD
 *
 */
public class D3ContextDesktop implements D3ContextInt {
	
	transient protected Context context;
	
	public D3ContextDesktop() {
		context = new Context();
	}
	
	@Override
	public int getColor(String color, int defaultColor) {
		switch (color) {
		case "orange": return context.getResources().getColor(R.color.legendary);
		case "green": return context.getResources().getColor(R.color.set);
		case "yellow": return context.getResources().getColor(R.color.rare);
		case "blue": return context.getResources().getColor(R.color.magic);
		default: return defaultColor;
		} 
	}
	
	@Override
	public String date2text(Date date) {
		return DateFormat.getDateTimeInstance().format(date);
	}
	
	@Override
	public String [] getStringArray(int idStringArray) {
		return context.getResources().getStringArray(idStringArray);
	}
	@Override
	public String getString(int idString) {
		return context.getString(idString);
	}

}
