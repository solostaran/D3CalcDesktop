package jodroid.d3calc.cache.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import jodroid.d3calc.cache.AsyncObjectHandler;
import jodroid.d3calc.cache.D3Cache;
import jodroid.d3calc.cache.D3CacheDesktop;
import jodroid.d3obj.D3Icon;
import jodroid.d3obj.D3Profile;
import jodroid.d3calc.D3Prefs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class D3CacheDesktopTest {
	protected D3Cache cache;

	@Before
	public void setUp() throws Exception {
		cache = D3Cache.getInstance();
		cache.setDelegate(new D3CacheDesktop());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetItemIcon() {
		D3Icon icon = cache.getItemIcon("http://us.media.blizzard.com/d3/icons/items/large/amulet05_demonhunter_male.png");
		assertNotNull(icon);
		assertEquals(64, icon.icon.image.getWidth());
		assertEquals(64, icon.icon.image.getHeight());
		icon = cache.getItemIcon("http://us.media.blizzard.com/d3/icons/plop.png");
		Object ret = (icon == null ? new Object() : null);
		assertNotNull("Maybe file 'plop.png' is created !", ret);
		File fileimage = new File(cache.getBaseDirectory(), "plop.png");
		ret = fileimage.exists() ? null : new Object();
		assertNotNull("File 'plop.png' should not be created !", ret);
	}
	
	@Test
	public void testWriteProfile() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testWriteHero() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testReadProfile() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadHero() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetProfile() {
		D3Prefs prefs = new D3Prefs();
//		prefs.loadOld = false;
		cache.getProfile("eu.battle.net", "solo-2284", prefs, new AsyncObjectHandler<D3Profile>() {
			
			@Override
			public void onSuccess(D3Profile obj) {
				assertEquals("Solo#2284", obj.battleTag);
			}
			
			@Override
			public void onFailure(String code, String reason) {
				System.out.println(code+": "+reason);
				fail(code+": "+reason);
			}
		});
		
		cache.getProfile("eu.battle.net", "toto", prefs, new AsyncObjectHandler<D3Profile>() {
			
			@Override
			public void onSuccess(D3Profile obj) {
				fail("Profile should not exist.");
			}
			
			@Override
			public void onFailure(String code, String reason) {
				assertEquals("NOTFOUND", code);
			}
		});
	}
	
	@Test
	public void testGetHero() {
		fail("Not yet implemented");
	}
}
