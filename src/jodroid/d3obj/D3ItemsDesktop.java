package jodroid.d3obj;

import java.util.ArrayList;

import jodroid.d3calc.R;

public class D3ItemsDesktop extends D3Obj implements ID3Items {

	private static final long serialVersionUID = 20140623;

	private void addItem(ArrayList<D3ItemLite> list, D3ItemLite item, int resId) {
		list.add(item);
		item.itemSlot = context.getString(resId);
	}
	
	@Override
	public D3ItemLite [] buildItemArray(D3Items items) {
		ArrayList<D3ItemLite> list = new ArrayList<D3ItemLite>();
		if (items.head != null) addItem(list, items.head, R.string.slot_head);
		if (items.neck != null) addItem(list, items.neck, R.string.slot_neck);
		if (items.shoulders != null) addItem(list, items.shoulders, R.string.slot_shoulders);
		if (items.torso != null) addItem(list, items.torso, R.string.slot_torso);
		if (items.bracers != null) addItem(list, items.bracers, R.string.slot_bracers);
		if (items.hands != null) addItem(list, items.hands, R.string.slot_hands);
		if (items.waist != null) addItem(list, items.waist, R.string.slot_waist);
		if (items.rightFinger != null) addItem(list, items.rightFinger, R.string.slot_rightFinger);
		if (items.leftFinger != null) addItem(list, items.leftFinger, R.string.slot_leftFinger);
		if (items.legs != null) addItem(list, items.legs, R.string.slot_legs);
		if (items.feet != null) addItem(list, items.feet, R.string.slot_feet);
		if (items.mainHand != null) addItem(list, items.mainHand, R.string.slot_mainHand);
		if (items.offHand != null) addItem(list, items.offHand, R.string.slot_offHand);
		return list.toArray(new D3ItemLite[1]);
	}

}
