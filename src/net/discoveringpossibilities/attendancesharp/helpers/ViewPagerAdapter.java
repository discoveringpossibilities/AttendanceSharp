package net.discoveringpossibilities.attendancesharp.helpers;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private final List<Fragment> mFragmentList;
	private final List<String> mFragmentTitleList;

	public ViewPagerAdapter(FragmentManager mFragmentManager) {
		super(mFragmentManager);
		mFragmentList = new ArrayList<Fragment>();
		mFragmentTitleList = new ArrayList<String>();
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	public void addFrag(Fragment fragment, String title) {
		mFragmentList.add(fragment);
		mFragmentTitleList.add(title);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitleList.get(position);
	}
}