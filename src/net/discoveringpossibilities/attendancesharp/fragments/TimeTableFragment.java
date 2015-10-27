package net.discoveringpossibilities.attendancesharp.fragments;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import net.discoveringpossibilities.attendancesharp.R;
import net.discoveringpossibilities.attendancesharp.helpers.AlarmReceiver;
import net.discoveringpossibilities.attendancesharp.helpers.TimeTableInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.TimeTableInformationParser;
import net.discoveringpossibilities.attendancesharp.helpers.ViewPagerAdapter;

public class TimeTableFragment extends Fragment {
	public static ViewPager mViewPager;
	public static TabLayout mTabLayout;

	private static String[] days = new String[] { "", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY",
			"SATURDAY" };
	private List<TimeTableInformationData> InformationList = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_time_table, container, false);
		System.out.println(this.getTag());

		mTabLayout = (TabLayout) mView.findViewById(R.id.tablayout);
		mViewPager = (ViewPager) mView.findViewById(R.id.viewpager);
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
		for (int day = 1; day <= 7; day++) {
			InformationList = TimeTableInformationParser
					.parseList(TimeTableInformationParser.getListFile(getActivity(), "TimeTable/" + day + ".plist"));
			viewPagerAdapter.addFrag(new TimeTableDayFragment(InformationList, day, getTag()), days[day]);
			for (int position = 0; position < InformationList.size(); position++) {
				//Create 5 minutes before reminder for all time table courses.
				NotificationManager(InformationList, position, day, true, false);
				//Create 5 minutes after reminder for all time table courses for attendance.
				NotificationManager(InformationList, position, day, true, true);
			}
		}
		mViewPager.setAdapter(viewPagerAdapter);

		mTabLayout.setupWithViewPager(mViewPager);
		mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		setHasOptionsMenu(true);
		Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		mToolbar.setTitle("Timetable");
		return mView;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.toolbar, menu);
		MenuItem item = menu.add(Menu.NONE, R.id.action_add_item, 20, R.string.action_add_item);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item.setIcon(R.drawable.ic_action_add);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void NotificationManager(List<TimeTableInformationData> InformationList, int position, int day, boolean create, boolean isGoingCheck) {
		String startTime = InformationList.get(position).getMethod("Start_Time");
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		mCalendar.set(Calendar.WEEK_OF_MONTH, day < mCalendar.get(Calendar.DAY_OF_WEEK) ? mCalendar.get(Calendar.WEEK_OF_MONTH) + 1 : mCalendar.get(Calendar.WEEK_OF_MONTH));
		mCalendar.set(Calendar.DAY_OF_WEEK, day);
		mCalendar.set(Calendar.HOUR_OF_DAY, startTime.isEmpty() ? 0 : Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
		if(isGoingCheck) {
			mCalendar.set(Calendar.MINUTE, startTime.isEmpty() ? 0 : Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)) + 5);
			if (mCalendar.get(Calendar.MINUTE) > 60) {
				mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1 > 24 ? 0 : mCalendar.get(Calendar.HOUR_OF_DAY) + 1);
				mCalendar.set(Calendar.MINUTE, Math.abs(mCalendar.get(Calendar.MINUTE) - 60));
			}
		} else {
			mCalendar.set(Calendar.MINUTE, startTime.isEmpty() ? 0 : Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)) - 5);
			if (mCalendar.get(Calendar.MINUTE) < 0) {
				mCalendar.set(Calendar.HOUR_OF_DAY,
						mCalendar.get(Calendar.HOUR_OF_DAY) - 1 < 0 ? 23 : mCalendar.get(Calendar.HOUR_OF_DAY) - 1);
				mCalendar.set(Calendar.MINUTE, 60 - Math.abs(mCalendar.get(Calendar.MINUTE)));
			}
		}
		mCalendar.set(Calendar.SECOND, 0);
		PendingIntent mPendingIntent;
		Intent mIntent = new Intent(getActivity(), AlarmReceiver.class);
		mIntent.putExtra("Subject", InformationList.get(position).getMethod("Subject"));
		mIntent.putExtra("Place", InformationList.get(position).getMethod("Place"));
		mIntent.putExtra("RepeatTime", Long.toString(AlarmManager.INTERVAL_DAY * 7));
		mIntent.putExtra("Calendar", Long.toString(mCalendar.getTimeInMillis()));
		if(isGoingCheck) {
			mIntent.putExtra("isGoingCheck", isGoingCheck);
			mPendingIntent = PendingIntent.getBroadcast(getActivity(), position + 515, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			mPendingIntent = PendingIntent.getBroadcast(getActivity(), position + 516, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		}


		AlarmManager mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		if(create) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				mAlarmManager.setWindow(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), TimeUnit.SECONDS.toMillis(10), mPendingIntent);
			} else {
				mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, mPendingIntent);
			}
			System.out.println(mCalendar);
		} else {
			mAlarmManager.cancel(mPendingIntent);
		}
	}
}
