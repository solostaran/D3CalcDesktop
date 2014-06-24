package jodroid.d3calc.cache;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;

import javax.imageio.ImageIO;

import jodroid.d3calc.D3Prefs;
import jodroid.d3calc.Log;
import jodroid.d3obj.D3Hero;
import jodroid.d3obj.D3IconDesktop;
import jodroid.d3obj.D3Item;
import jodroid.d3obj.D3Obj;
import jodroid.d3obj.D3ObjFactory;
import jodroid.d3obj.D3Profile;
import jodroid.d3obj.D3ProfileLite;
import jodroid.d3obj.ID3Icon;
import jopc.d3calc.D3ContextDesktop;
import jopc.d3calc.gui.D3Profiles;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;
import android.graphics.Bitmap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import d3api.D3Url;

public class D3CacheDesktop implements ID3Cache {
	public final static String D3BASEDIR = "cache";
	public final static int TIMEOUT_CONNECT = 10000;
	public final static int TIMEOUT_READ = 30000;

	public final static String GETPROFILE = "GetProfile";
	public final static String GETHERO = "GetHero";
	public final static String GETITEM = "GetItem";

	private Resty resty;
	private D3ObjFactory factory;

	public D3CacheDesktop() {
		resty = new Resty();
		factory = new D3ObjFactory();
	}

	@Override
	public File getBaseDirectory() {
		File baseDirectory = new File(D3BASEDIR);
		if (!baseDirectory.exists()) {
			Log.i(D3Cache.class.getSimpleName(), "Creating dir : "+baseDirectory.getPath());
			if (!baseDirectory.mkdirs()) return null;
		}
		return baseDirectory;
	}

	@Override
	public void createDir(File dir) {
		if (dir.exists()) return;
		Log.i(D3Cache.class.getSimpleName(), "Creating dir : "+dir.getPath());
		dir.mkdirs();
	}

	@Override
	public ID3Icon getItemIcon(String url) {
		int index = url.indexOf("d3/");
		File fileimage = new File(getBaseDirectory(), url.substring(index+3));
		Bitmap bm = null;
		BufferedImage image = null;
		InputStream is = null;
		BufferedOutputStream bos = null;
		if (!fileimage.exists()) {
			createDir(fileimage.getParentFile());
			try {
				URL aURL = new URL(url);
				URLConnection conn = aURL.openConnection();
				conn.setConnectTimeout(TIMEOUT_CONNECT);
				conn.setReadTimeout(TIMEOUT_READ);
				conn.connect();
				is = conn.getInputStream();
				// Android (unused)
				//				BufferedInputStream bis = new BufferedInputStream(is);

				FileOutputStream fos = new FileOutputStream(fileimage);
				bos = new BufferedOutputStream(fos);

				byte [] buffer = new byte[1024];
				int bytesRead = 0;
				while ((bytesRead = is.read(buffer, 0, buffer.length)) >= 0) {
					bos.write(buffer, 0, bytesRead);
				}

				// Android (unused)
				//				bm = BitmapFactory.decodeStream(bis); // obtain an image directly from the url
				//				bis.close();
				is.close();
				bos.flush();
				bos.close();
				fos.close();
			} catch (IOException e) {
				Log.e(this.getClass().getSimpleName(), "Error getting bitmap : "+e.getMessage());
				return null;
			} finally {
				if (is != null) {
					try { is.close(); } catch (IOException e) {}
				}
			}
		}		

		// Desktop version
		try {
			image = ImageIO.read(fileimage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (image != null) {
			bm = new Bitmap();
			bm.image = image;
		}
		// Android version
		//		bm = BitmapFactory.decodeFile(fileimage);
		return new D3IconDesktop(bm);
	}

	@Override
	public D3Profile readProfile(String battlehost, String battletag) {
		if (battlehost == null) return null;
		D3Profile ret = null;
		File filecache = new File(getBaseDirectory(), battlehost+"/"+battletag.replace('#', '-')+".obj");
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(filecache);
			ois = new ObjectInputStream(fis);
			ret = (D3Profile)ois.readObject();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			Log.w(D3Cache.class.getName(), e.getMessage());
		} catch (StreamCorruptedException e) {
			Log.e(D3Cache.class.getName(), e.getMessage());
		} catch (IOException e) {
			Log.e(D3Cache.class.getName(), e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.e(D3Cache.class.getName(), e.getMessage());
		}
		return ret;
	}

	@Override
	public void writeProfile(D3Profile profile) {
		if (profile.battlehost == null) return;
		File filecache = new File(getBaseDirectory(), profile.battlehost+"/"+profile.battleTag.replace('#', '-')+".obj");
		if (!filecache.exists()) createDir(filecache.getParentFile());
		profile.lastSaveDate = (long)(new Date().getTime() / 1000);
		try {
			FileOutputStream fos = new FileOutputStream(filecache);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(profile);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		}
	}

	@Override
	public D3Hero readHero(String battlehost, String battletag, String heroID) {
		if (battlehost == null) return null;
		if (heroID == null) return null;
		D3Hero ret = null;
		File filecache = new File(getBaseDirectory(), battlehost+"/"+battletag.replace('#', '-')+"/"+heroID+".obj");
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(filecache);
			ois = new ObjectInputStream(fis);
			ret = (D3Hero)ois.readObject();
			if (ret != null && ret.items != null) ret.items.buildItemArray();
			ois.close();
			fis.close();
		} catch (FileNotFoundException e) {
			Log.w(D3Cache.class.getName(), e.getMessage());
		} catch (StreamCorruptedException e) {
			Log.e(D3Cache.class.getName(), e.getMessage());
		} catch (IOException e) {
			Log.e(D3Cache.class.getName(), e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.e(D3Cache.class.getName(), e.getMessage());
		}
		return ret;
	}

	@Override
	public void writeHero(D3Hero hero) {
		if (hero.battlehost == null) return;
		File filecache = new File(getBaseDirectory(), hero.battlehost+"/"+hero.battletag.replace('#', '-')+"/"+hero.id+".obj");
		if (!filecache.exists()) createDir(filecache.getParentFile());
		hero.last_save = (long)(new Date().getTime() / 1000);
		try {
			if (hero.items.itemArray != null) {
				Log.i(this.getClass().getSimpleName(), "Generating item array for:"+hero.name);
				hero.items.itemArray2Items();
			}
			FileOutputStream fos = new FileOutputStream(filecache);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hero);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		}
	}

	public void writeJsonObject(String filename, JSONObject obj) {
		if (obj == null || filename == null || filename.isEmpty()) return;
		File filejson = new File(getBaseDirectory(), filename);
		if (!filejson.exists()) createDir(filejson.getParentFile());
		try {
			FileOutputStream fos = new FileOutputStream(filejson);
			Writer writer = new OutputStreamWriter(fos, Charset.forName("UTF-8"));
			writer.write(obj.toString(2));
			writer.close();
		} catch (FileNotFoundException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
		}
	}

	@Override
	public void getProfile(String battlehost, String battletag, D3Prefs prefs,
			AsyncObjectHandler<D3Profile> aoh) {

		String tag = battletag.replace("#", "-");
		// Cache management
		D3Profile profile;
		if (prefs.isLoadOnDemand()) { 
			profile = readProfile(battlehost, tag);
			if (profile != null ) {
				if (prefs.isLoadOld()) {
					if (!prefs.isTooOld(profile.lastSaveDate)) {
						Log.i(GETPROFILE, battlehost+"/"+tag+" from cache, not too old");
						aoh.onSuccess(profile);
						return;
					} else {
						Log.i(GETPROFILE, battlehost+"/"+tag+" too old -> loading");
					}
				} else {
					Log.i(GETPROFILE, battlehost+"/"+tag+" from cache");
					aoh.onSuccess(profile);
					return;
				}
			} else {
				Log.i(GETPROFILE, battlehost+"/"+tag+" not in cache -> loading");
			}
		} else {
			Log.i(GETPROFILE, battlehost+"/"+tag+" forceload -> loading");
		}

		// Getting json from web api
		String url = D3Url.playerProfile2Url(battlehost, tag);
		Log.i(GETPROFILE, url);
		JSONObject obj = null;
		JSONResource res = null;
		try {
			res = resty.json(url);
			obj = res.object();
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("IOException", "Resty msg: "+e.getMessage());
			return;
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("JSONException", "Resty msg: "+e.getMessage());
			return;
		} catch (ClassCastException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("ClassCastException", "Error getting profile");
			return;
		}

		// json returning an error
		try {
			String code = obj.getString("code");
			if (code != null) {
				String reason = obj.getString("reason");
				Log.w(this.getClass().getSimpleName(), "GetProfile error: code="+code+", reason="+reason);
				aoh.onFailure(code,reason);
				return;
			}
		} catch(JSONException e) {}

		// parsing
		profile = (D3Profile)factory.jsonBuild(D3Profile.class, obj);
		profile.battlehost = battlehost;
		// saving
		if (prefs.isSaveJson()) writeJsonObject(battlehost+"/"+tag+".json", obj);
		writeProfile(profile);
		aoh.onSuccess(profile);
	}
	
	@Override
	public void getProfile(D3ProfileLite prof, D3Prefs prefs,
			AsyncObjectHandler<D3Profile> aoh) {
		getProfile(prof.battlehost, prof.battleTag, prefs, aoh);
	}

	@Override
	public void getHero(String battlehost, String battletag, String heroID, D3Prefs prefs,
			AsyncObjectHandler<D3Hero> aoh) {
		// Cache management
		D3Hero hero = null;
		String heropath = battlehost+"/"+battletag.replace('#', '-')+"/"+heroID;
		if (prefs.isLoadOnDemand()) { 
			hero = readHero(battlehost, battletag, heroID);
			if (hero != null ) {
				if (prefs.isLoadOld()) {
					if (!prefs.isTooOld(hero.last_save)) {
						Log.i(GETHERO, heropath+" from cache, not too old");
						aoh.onSuccess(hero);
						return;
					} else {
						Log.i(GETHERO, heropath+" too old -> loading");
					}
				} else {
					Log.i(GETHERO, heropath+" from cache");
					aoh.onSuccess(hero);
					return;
				}
			} else {
				Log.i(GETHERO, heropath+" not in cache -> loading");
			}
		} else {
			Log.i(GETHERO, heropath+" forceload -> loading");
		}

		// Getting json from web api
		String url = D3Url.hero2Url(battlehost, battletag, heroID);
		Log.i(GETHERO, url);
		JSONObject obj = null;
		JSONResource res = null;
		try {
			res = resty.json(url);
			obj = res.object();

		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("IOException", "Resty msg: "+e.getMessage());
			return;
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("JSONException", "Resty msg: "+e.getMessage());
			return;
		} catch (ClassCastException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("ClassCastException", "Error getting profile");
			return;
		}

		// json returning an error
		try {
			String code = obj.getString("code");
			if (code != null) {
				String reason = obj.getString("reason");
				Log.w(this.getClass().getSimpleName(), "GetHero error: code="+code+", reason="+reason);
				aoh.onFailure(code,reason);
				return;
			}
		} catch(JSONException e) {}

		// parsing
		hero = (D3Hero)factory.jsonBuild(D3Hero.class, obj);
		hero.battlehost = battlehost;
		hero.battletag = battletag;
		if (hero != null && hero.items != null) hero.items.buildItemArray();
		// saving
		if (prefs.isSaveJson()) writeJsonObject(heropath+".json", obj);
		writeHero(hero);
		aoh.onSuccess(hero);
	}

	@Override
	public void getItem(String url, AsyncObjectHandler<D3Item> aoh) {
		// Getting json from web api
		D3Item item = null;
		Log.d(GETITEM, url);
		JSONObject obj = null;
		JSONResource res = null;
		try {
			res = resty.json(url);
			obj = res.object();

		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("IOException", "Resty msg: "+e.getMessage());
			return;
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("JSONException", "Resty msg: "+e.getMessage());
			return;
		} catch (ClassCastException e) {
			Log.e(this.getClass().getSimpleName(), e.getMessage());
			aoh.onFailure("ClassCastException", "Error getting profile");
			return;
		}

		// json returning an error
		try {
			String code = obj.getString("code");
			if (code != null) {
				String reason = obj.getString("reason");
				Log.w(this.getClass().getSimpleName(), "GetItem error: code="+code+", reason="+reason);
				aoh.onFailure(code,reason);
				return;
			}
		} catch(JSONException e) {}

		// parsing
		item = (D3Item)factory.jsonBuild(D3Item.class, obj);
		aoh.onSuccess(item);
	}

	public static void main(String [] args) {
		D3Cache cache = D3Cache.getInstance().setDelegate(new D3CacheDesktop());
		D3Obj.setContext(new D3ContextDesktop());
		D3Prefs prefs = new D3Prefs();
		//		prefs.loadOld = false;

		//		cache.getProfile("eu.battle.net", "solo-2284", prefs, new AsyncObjectHandler<D3Profile>() {
		//
		//			@Override
		//			public void onSuccess(D3Profile obj) {
		//				System.out.println(obj.battleTag);
		//				System.out.println(obj.getLastSave());
		//				System.out.println(obj.getLastUpdated());
		//			}
		//
		//			@Override
		//			public void onFailure(String code, String reason) {
		//				System.out.println(code+": "+reason);
		//			}
		//		});

		cache.getHero("eu.battle.net", "solo-2284", "4808413", prefs, new AsyncObjectHandler<D3Hero>() {

			@Override
			public void onSuccess(D3Hero hero) {
				System.out.println("id="+hero.id);
				System.out.println(hero.getLastSave());
				System.out.println(hero.getLastUpdated());
			}

			@Override
			public void onFailure(String code, String reason) {
				System.out.println(code+": "+reason);
			}
		});
	}

	@Override
	public void xmlEncodeToFile(Object obj, String filename) {
		File f = new File(getBaseDirectory(), filename);
//		FileOutputStream fos = null;
		try {
//			fos = new FileOutputStream(f);
			
			XStream xstream = new XStream(new StaxDriver());
			xstream.alias("D3Prefs", D3Prefs.class);
			xstream.alias("D3Profiles", D3Profiles.class);
			xstream.alias("D3ProfileLite", D3ProfileLite.class);
//			xstream.toXML(obj, fos);
			xstream.marshal(obj, new PrettyPrintWriter(new FileWriter(f)));
		} catch (FileNotFoundException e) {
			Log.w(this.getClass().getSimpleName(), e.getMessage());
		} catch (IOException e) {
			Log.w(this.getClass().getSimpleName(), e.getMessage());
		} finally {
//			if (fos != null)
//				try { fos.close(); } catch (IOException e) {}
		}

		//		XStream xstream = new XStream(new StaxDriver());
		//		xstream.alias("D3Prefs", D3Prefs.class);
		//		String xml = xstream.toXML(obj);
		//		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		//		out.write(xml);
		//		out.close();
	}

	@Override
	public Object xmlDecodeFromFile(String filename) {
		File f = new File(getBaseDirectory(), filename);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);

			XStream xstream = new XStream(new StaxDriver());
			xstream.alias("D3Prefs", D3Prefs.class);
			xstream.alias("D3Profiles", D3Profiles.class);
			xstream.alias("D3ProfileLite", D3ProfileLite.class);
			return xstream.fromXML(fis);

		} catch (FileNotFoundException e) {
			Log.w(this.getClass().getSimpleName(), e.getMessage());
		} finally {
			if (fis != null)
				try { fis.close(); } catch (IOException e) {}
		}

		//			Scanner scan = new Scanner(f);  
		//			scan.useDelimiter("\\Z");  
		//			String xml = scan.next();
		//			scan.close();		
		//			XStream xstream = new XStream(new StaxDriver());
		//			xstream.alias("D3Prefs", D3Prefs.class);
		//			return (D3Prefs)xstream.fromXML(xml);
		return null;
	}
}
