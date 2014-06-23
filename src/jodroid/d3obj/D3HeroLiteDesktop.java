package jodroid.d3obj;

import jodroid.d3calc.R;

public class D3HeroLiteDesktop extends D3Obj implements ID3HeroLite {

	private static final long serialVersionUID = 20140623;

	@Override
	public String getHardcore() {
		return context.getString(R.string.hardcore);
	}

	@Override
	public String getGender(int gender) {
		return gender == 0 ? context.getString(R.string.gendermale) : context.getString(R.string.genderfemale);
	}

	@Override
	public String getParagon() {
		return context.getString(R.string.paragon);
	}

}
