package jopc.d3calc.gui;

import java.util.ArrayList;
import java.util.List;

import jodroid.d3obj.D3ProfileLite;

public class D3Profiles {
	public List<D3ProfileLite> listProfiles;
	
	public D3Profiles() {
		listProfiles = new ArrayList<D3ProfileLite>();
	}
	
	public boolean contains(D3ProfileLite obj) {
		return listProfiles.contains(obj);
	}
	
	public boolean add(D3ProfileLite element) {
		return listProfiles.add(element);
	}
	
	public void add(int index, D3ProfileLite element) {
		listProfiles.add(index, element);
	}
	
	public void remove(D3ProfileLite element) {
		listProfiles.remove(element);
	}
	
	public void remove(int index) {
		listProfiles.remove(index);
	}
	
	public void set(int index, D3ProfileLite element) {
		listProfiles.set(index, element);
	}
}
