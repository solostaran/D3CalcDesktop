package jodroid.d3obj;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import android.util.Log;

/**
 * Build D3Obj objects through a JSONObject and reflection.
 * @author JRD
 */
public class D3ObjFactory {

	public D3Obj jsonBuild(Class<?> c, JSONObject jsonObject) {
		return jsonBuild(c, jsonObject, true);
	}
	
	@SuppressWarnings("unchecked")
	public D3Obj jsonBuild(Class<?> c, JSONObject jsonObject, boolean debug) {
		D3Obj newobj = null;
		try {
			newobj = (D3Obj)c.newInstance();
		} catch (InstantiationException e) {
			Log.e(this.getClass().getSimpleName(), c.getClass().getName() + ": " + e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			Log.e(this.getClass().getSimpleName(), c.getClass().getName() + ": " + e.getMessage());
			return null;
		}
		
		Field[] fields=c.getFields();  // Get all fields
		for (Field f : fields) {
			if ((f.getModifiers() & Modifier.TRANSIENT) != 0) continue;

			String t=f.getGenericType().toString();
			if(t.startsWith("class")) {
				t=t.substring(6);
			}

			// General annotation 
			D3FieldAnnotation annot = f.getAnnotation(D3FieldAnnotation.class);
			String jsonName = f.getName();
			if (annot != null) {
				if (annot.notInJson()) continue;
				if (!annot.jsonName().isEmpty()) jsonName = annot.jsonName();
				if (!annot.debug()) debug = false;
			}

			try {
				// If we expect an array
				if (f.getType().isArray()) {

					// Getting the array
					JSONArray jsonArray = jsonObject.getJSONArray(jsonName);
					// Allocating the new array
					Class<?> arrayComponentType = f.getType().getComponentType();
					Object tab = Array.newInstance(arrayComponentType, jsonArray.length());
					// Setting the array to this new instance
					f.set(newobj, tab);
					// Parsing each item of the array
					for (int i =  0; i < jsonArray.length(); i++) {
						if (arrayComponentType.isPrimitive() || arrayComponentType == String.class) {
							Array.set(tab, i, jsonArray.get(i));
						} else {
							JSONObject jsonTmp = jsonArray.getJSONObject(i);
							D3Obj val = jsonBuild((Class<D3Obj>)arrayComponentType, jsonTmp, debug);
							Array.set(tab, i, val);
//							Object val = arrayComponentType.newInstance();
//							Array.set(tab, i, val);
//							((D3Obj)val).jsonBuild(jsonTmp, debug);
							
						}
					}
				} else { // if this is not an array

					// Getting the object depends on type (other types invoke an exception)
					if (f.getType().isPrimitive() || f.getType() == String.class) {
						f.set(newobj, jsonObject.get(jsonName));
					} else { 
						// all D3Obj types
						JSONObject obj;
						obj = jsonObject.getJSONObject(jsonName);
						D3Obj val = jsonBuild((Class<D3Obj>)f.getType(), obj, debug);
						f.set(newobj, val);
//						Object val = f.getType().newInstance();
//						f.set(this, val);
//						((D3Obj)val).jsonBuild(obj, debug); // exception if not a D3Obj
					}
				}
			} catch (IllegalArgumentException e) {
				Log.e(this.getClass().getName(), e.getClass().getSimpleName() + "["+f.getName()+"]: " + e.getMessage());
			} catch (IllegalAccessException e) {
				Log.e(this.getClass().getName(), e.getClass().getSimpleName() + "["+f.getName()+"]: " + e.getMessage());
			} catch (JSONException e) {
				if (debug)
					Log.w(this.getClass().getName(), e.getClass().getSimpleName() + ": " + e.getMessage() + " in "+this.getClass().getSimpleName());
			}
			
			if (annot != null && !annot.debug()) debug = true;
		}
		return newobj;
	}
}
