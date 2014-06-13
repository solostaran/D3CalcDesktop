package android.content.res;

import jodroid.d3calc.R;

public class Resources {

	public int getColor(int id) {
		switch (id) {
		case R.color.legendary:
			return 0xFF8C00;
		case R.color.rare:
			return 0xFFD700;
		case R.color.set:
			return 0x7FFF00;
		case R.color.magic:
			return 0x2534FF;
		case R.color.black:
			return 0x000000;
		case R.color.white:
			return 0xFFFFFF;
		}
		return 0;
	}
	
	public String getString(int id) {
		switch (id) {
		case R.string.hardcore:
			return "hardcode";
		case R.string.genderfemale:
			return "female";
		case R.string.gendermale:
			return "male";
		case R.string.paragon:
			return "paragon";
			
		case R.string.slot_head:
			return "head";
		case R.string.slot_neck:
			return "neck";
		case R.string.slot_shoulders:
			return "shoulders";
		case R.string.slot_torso:
			return "torso";
		case R.string.slot_bracers:
			return "bracers";
		case R.string.slot_hands:
			return "hands";
		case R.string.slot_waist:
			return "waist";
		case R.string.slot_rightFinger:
			return "right finger";
		case R.string.slot_leftFinger:
			return "left finger";
		case R.string.slot_legs:
			return "legs";
		case R.string.slot_feet:
			return "feet";
		case R.string.slot_mainHand:
			return "mainhand";
		case R.string.slot_offHand:
			return "offhand";
		case R.string.item_armor:
			return "Armor";
		case R.string.primary:
			return "primary";
		case R.string.secondary:
			return "secondary";
		case R.string.passive:
			return "passive";
		case R.string.title_active_skill:
			return "Active skill";
		case R.string.title_passive_skill:
			return "Passive Skill";
		}
		return "";
	}
	
	public String [] getStringArray(int id) {
		switch (id) {
		case R.array.HeroAttributesFields:
			return new String [] {"armor", "strength", "dexterity", "vitality", "intelligence"};
		case R.array.HeroOffenseFields:
			return new String [] {"damage", "attackSpeed", "critChance", "critDamage"};
		case R.array.HeroDefenseFields:
			return new String [] { "damageReduction", "physicalResist","fireResist","coldResist",
					"lightningResist","poisonResist","arcaneResist"};
		case R.array.HeroLifeFields:
			return new String [] { "life" };
		case R.array.ItemSlotFields:
			return new String [] { "head","neck","shoulders","torso","bracers","hands",
					"rightFinger","leftFinger","waist","legs","feet","mainHand","offHand" };
		case R.array.HeroStatsNames:
			return new String [] {"Level","Parangon","Elite kills","Last updated"};
		case R.array.HeroAttributesNames:
	        return new String [] {"Armor","Strength","Dexterity","Vitality","Intelligence" };
		case R.array.HeroOffenseNames:
			return new String [] {"Damage","Attacks per Second","Critical Hit Chance","Critical Hit Damage"};
		case R.array.HeroDefenseNames:
			return new String [] {"Damage Reduction","Physical Resist","Fire Resist","Cold Resist",
					"Lightning Resist","Poison Resist","Arcane/Holy Resist"};
		case R.array.HeroLifeNames:
			return new String [] {"Hit Points"};
		}
		return null;
	}
}
