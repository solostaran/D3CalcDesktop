package android.content;

import android.content.res.Resources;

public class Context {
	
	private Resources res;
	
	public Context() {
		res = new Resources();
	}
	
	public String getString(int id) {
		return res.getString(id);
	}
	
	public Resources getResources() {
		if (res == null)
			res = new Resources();
		return res;
	}
}
