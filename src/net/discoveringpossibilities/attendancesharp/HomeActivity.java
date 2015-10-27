package net.discoveringpossibilities.attendancesharp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import net.discoveringpossibilities.attendancesharp.fragments.AttendanceFragment;
import net.discoveringpossibilities.attendancesharp.fragments.ContactDialog;
import net.discoveringpossibilities.attendancesharp.fragments.HomeFragment;
import net.discoveringpossibilities.attendancesharp.fragments.TimeTableFragment;
import net.discoveringpossibilities.attendancesharp.helpers.ToolbarActivity;

public class HomeActivity extends ToolbarActivity {
	public static ToolbarActivity HomeActivity;

	/**
	 * #Region Drawer Layout
	 */
	private FrameLayout mContentFrame;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	private boolean mUserLearnedDrawer;
	private int mCurrentSelectedPosition;
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * #Region Fragments
	 */
	private Fragment mFragment = null;
	private AttendanceFragment mAttendanceFragment = null;
	private TimeTableFragment mTimeTableFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
		mNavigationView = (NavigationView) findViewById(R.id.nav_view);

		mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);

		mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(this, PREF_USER_LEARNED_DRAWER, "false"));

		mFragmentManager = getSupportFragmentManager();

		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
		}

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.nav_contentframe, new HomeFragment()).commit();

		setUpNavDrawer();
		mAttendanceFragment = new AttendanceFragment();
		mTimeTableFragment = new TimeTableFragment();

		mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				mFragment = null;
				switch (menuItem.getItemId()) {
				case R.id.navigation_item_1:
					mCurrentSelectedPosition = 0;
					mFragment = mAttendanceFragment;
					break;
				case R.id.navigation_item_2:
					mCurrentSelectedPosition = 1;
					mFragment = mTimeTableFragment;
					break;
				default:
					new AlertDialog.Builder(HomeActivity.this).setIcon(R.drawable.ic_menu_info)
							.setTitle(menuItem.getTitle() + "!")
							.setMessage("Please wait while we implement this feature in upcoming updates!").show();
					break;
				}
				if (mFragment != null && !mFragment.isVisible()) {
					mFragmentManager.beginTransaction().replace(R.id.nav_contentframe, mFragment, mFragment.toString())
							.commit();
					menuItem.setChecked(true);
				}
				Snackbar.make(mContentFrame, menuItem.getTitle(), Snackbar.LENGTH_SHORT).show();
				mDrawerLayout.closeDrawer(GravityCompat.START);
				return true;
			}
		});
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.activity_home;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
		Menu menu = mNavigationView.getMenu();
		menu.getItem(mCurrentSelectedPosition).setChecked(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toolbar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about_us:
			new ContactDialog().show(getSupportFragmentManager(), null);
			Toast.makeText(this, "Tap on the card to call that person!", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
			mDrawerLayout.closeDrawer(GravityCompat.START);
		else
			super.onBackPressed();
	}

	private void setUpNavDrawer() {
		if (mToolbar != null) {
			mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mDrawerLayout.openDrawer(GravityCompat.START);
				}
			});
		}

		if (!mUserLearnedDrawer) {
			mDrawerLayout.openDrawer(GravityCompat.START);
			mUserLearnedDrawer = true;
			saveSharedSetting(this, PREF_USER_LEARNED_DRAWER, "true");
		}
	}

	public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
		SharedPreferences sharedPref = ctx.getSharedPreferences("AttendanceSharp", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(settingName, settingValue);
		editor.apply();
	}

	public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
		SharedPreferences sharedPref = ctx.getSharedPreferences("AttendanceSharp", Context.MODE_PRIVATE);
		return sharedPref.getString(settingName, defaultValue);
	}
}
