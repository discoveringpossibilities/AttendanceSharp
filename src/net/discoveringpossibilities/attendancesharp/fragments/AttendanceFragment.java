package net.discoveringpossibilities.attendancesharp.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.discoveringpossibilities.attendancesharp.R;
import net.discoveringpossibilities.attendancesharp.helpers.AlarmReceiver;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceDetailsInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceDetailsInformationParser;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceInformationParser;
import net.discoveringpossibilities.attendancesharp.helpers.CreateAttendanceSheet;
import net.discoveringpossibilities.attendancesharp.helpers.TimeTableInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.TimeTableInformationParser;

public class AttendanceFragment extends Fragment {
	private GridView mGridView;
	private ListView mListView;

	private List<AttendanceInformationData> InformationList = null;
	private AttendanceListAdapter attendanceListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_attendance, container, false);
		mGridView = (GridView) mView.findViewById(R.id.attendance_gridview);
		mListView = (ListView) mView.findViewById(R.id.attendance_listView);
		InformationList = AttendanceInformationParser
				.parseList(AttendanceInformationParser.getListFile(getActivity(), "Attendance/attendance.plist"));
		InformationList = getAttendanceList();
		setHasOptionsMenu(true);
		Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		mToolbar.setTitle("Attendance");
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		attendanceListAdapter = new AttendanceListAdapter(getActivity(), InformationList);

		for (int position = 0; position < InformationList.size(); position++)
			CreateReminder(position);

		int orientation = getActivity().getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE)
			setGridView(true);
		else if (orientation == Configuration.ORIENTATION_PORTRAIT)
			setGridView(false);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setGridView(true);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setGridView(false);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.toolbar, menu);
		MenuItem item = menu.add(Menu.NONE, R.id.action_share_attendance, 20, R.string.action_share_attendance);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item.setIcon(R.drawable.ic_action_share_image);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_about_us:
				new ContactDialog().show(getActivity().getSupportFragmentManager(), null);
				Toast.makeText(getActivity(), "Tap on the card to call that person!", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.action_share_attendance:
				new CreateAttendanceSheet(getActivity()).execute();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void CreateReminder(int position) {
		Float Class_Percentage = (Float.parseFloat(InformationList.get(position).getMethod("Classes_Attended"))
				/ Float.parseFloat(InformationList.get(position).getMethod("Classes_Total")) * 100);
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		mCalendar.set(Calendar.HOUR_OF_DAY, 7);
		mCalendar.set(Calendar.MINUTE, 30);
		mCalendar.set(Calendar.SECOND, 0);
		Intent mIntent = new Intent(getActivity(), AlarmReceiver.class);
		mIntent.putExtra("Course_Name", InformationList.get(position).getMethod("Course_Name"));
		mIntent.putExtra("Class_Percentage", Class_Percentage.toString());
		mIntent.putExtra("RepeatTime", Long.toString(AlarmManager.INTERVAL_DAY));
		mIntent.putExtra("Calendar", Long.toString(mCalendar.getTimeInMillis()));

		PendingIntent mPendingIntent = PendingIntent.getBroadcast(getActivity(), position + 514, mIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		if (Class_Percentage < 75) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				mAlarmManager.setWindow(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),
						TimeUnit.SECONDS.toMillis(10), mPendingIntent);
				System.out.println(Calendar.getInstance().getTimeInMillis() + TimeUnit.SECONDS.toMillis(20));
			} else {
				mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(),
						AlarmManager.INTERVAL_DAY, mPendingIntent);
			}
		} else {
			mAlarmManager.cancel(mPendingIntent);
		}
	}

	public void setGridView(Boolean isGrid) {
		if (isGrid) {
			mListView.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			mGridView.setAdapter(attendanceListAdapter);
			mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					new AttendanceDialog(position).show(getFragmentManager(), null);
				}
			});
			mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					new AttendanceDetailDialog(position).show(getFragmentManager(), null);
					return true;
				}
			});
		} else {
			mGridView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(attendanceListAdapter);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					new AttendanceDialog(position).show(getFragmentManager(), null);
				}
			});
			mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					new AttendanceDetailDialog(position).show(getFragmentManager(), null);
					return true;
				}
			});
		}
	}

	public List<AttendanceInformationData> getAttendanceList() {
		List<TimeTableInformationData> TotalTimeTableInformationList = new ArrayList<TimeTableInformationData>();
		List<TimeTableInformationData> SudoTimeTableInformationList = new ArrayList<TimeTableInformationData>();
		for (int day = 1; day <= 7; day++) {
			List<TimeTableInformationData> InformationList = TimeTableInformationParser
					.parseList(TimeTableInformationParser.getListFile(getActivity(), "TimeTable/" + day + ".plist"));
			for (int j = 0; j < InformationList.size(); j++) {
				SudoTimeTableInformationList.add(InformationList.get(j));
			}
		}
		Collections.sort(SudoTimeTableInformationList, new Comparator<TimeTableInformationData>() {
			@Override
			public int compare(TimeTableInformationData lhs, TimeTableInformationData rhs) {
				return lhs.getMethod("Subject").compareToIgnoreCase(rhs.getMethod("Subject"));
			}
		});
		ArrayList<Integer> courseCount = new ArrayList<Integer>();
		for (int i = 0; i < SudoTimeTableInformationList.size();) {
			int count = 1;
			int j = i + 1;
			for (; j < SudoTimeTableInformationList.size(); j++) {
				if (SudoTimeTableInformationList.get(i).getMethod("Subject")
						.equals(SudoTimeTableInformationList.get(j).getMethod("Subject")))
					count++;
				else
					break;
			}
			courseCount.add(count);
			TotalTimeTableInformationList.add(SudoTimeTableInformationList.get(i));
			i = j;
		}
		
		List<AttendanceDetailsInformationData> mAttendanceDetailInformationList = AttendanceDetailsInformationParser
				.parseList(AttendanceDetailsInformationParser.getListFile(getActivity(), "Attendance/attendance_details.plist"));
		
		List<AttendanceInformationData> mInformationList = new ArrayList<AttendanceInformationData>();
		for (int i = 0; i < TotalTimeTableInformationList.size(); i++) {
			int count = 0;
			for(int x = 0; x < mAttendanceDetailInformationList.size(); x++)
				if(mAttendanceDetailInformationList.get(x).getMethod("Course_Name").equals(TotalTimeTableInformationList.get(i).getMethod("Subject")) && mAttendanceDetailInformationList.get(x).getMethod("Course_Status").equals("Present"))
					count++;
			
			AttendanceInformationData mAttendanceInformationData = new AttendanceInformationData();
			mAttendanceInformationData.setMethod("Course_Code", TotalTimeTableInformationList.get(i).getMethod("Course_Code"));
			mAttendanceInformationData.setMethod("Course_Name", TotalTimeTableInformationList.get(i).getMethod("Subject"));
			mAttendanceInformationData.setMethod("Course_Type", TotalTimeTableInformationList.get(i).getMethod("LectureType"));
			mAttendanceInformationData.setMethod("Classes_Total", String.valueOf(courseCount.get(i) * 12));
			/*
			for (int pos = 0; pos < InformationList.size(); pos++)
				if (mAttendanceInformationData.getMethod("Course_Name").equals(InformationList.get(pos).getMethod("Course_Name")))
			*/
			mAttendanceInformationData.setMethod("Classes_Attended", String.valueOf(count));
			mInformationList.add(mAttendanceInformationData);
		}
		AttendanceInformationParser.reWriteListFile(getActivity(), "Attendance/attendance.plist", mInformationList);
		return mInformationList;
	}

	static class ViewHolder {
		protected TextView attendance_course_code;
		protected TextView attendance_course_name;
		protected TextView attendance_course_type;
		protected TextView attendance_classes_attended;
		protected TextView attendance_classes_total;
		protected TextView attendance_percentage;
		protected ImageView attendance_percentage_background;
	}

	public class AttendanceListAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;

		private Context context;

		private List<AttendanceInformationData> _AttendanceList;

		public AttendanceListAdapter(Context context, List<AttendanceInformationData> AttendanceList) {
			this._AttendanceList = AttendanceList;
			this.context = context;
			this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return _AttendanceList.size();
		}

		public Object getItem(int position) {
			return _AttendanceList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder;
			// displays three fields in list items (Name,Title and Department)
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.fragment_attendance_row, parent, false);
				mViewHolder = new ViewHolder();
				mViewHolder.attendance_course_code = (TextView) convertView.findViewById(R.id.attendance_course_code);
				mViewHolder.attendance_course_name = (TextView) convertView.findViewById(R.id.attendance_course_name);
				mViewHolder.attendance_course_type = (TextView) convertView.findViewById(R.id.attendance_course_type);
				mViewHolder.attendance_classes_total = (TextView) convertView
						.findViewById(R.id.attendance_classes_total);
				mViewHolder.attendance_classes_attended = (TextView) convertView
						.findViewById(R.id.attendance_classes_attended);
				mViewHolder.attendance_percentage = (TextView) convertView.findViewById(R.id.attendance_percentage);
				mViewHolder.attendance_percentage_background = (ImageView) convertView
						.findViewById(R.id.attendance_percentage_background);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}

			mViewHolder.attendance_course_code.setText(_AttendanceList.get(position).getMethod("Course_Code"));
			mViewHolder.attendance_course_name.setText(_AttendanceList.get(position).getMethod("Course_Name"));
			mViewHolder.attendance_course_type.setText(_AttendanceList.get(position).getMethod("Course_Type"));
			mViewHolder.attendance_classes_total.setText(_AttendanceList.get(position).getMethod("Classes_Total"));
			mViewHolder.attendance_classes_attended
					.setText(_AttendanceList.get(position).getMethod("Classes_Attended"));
			Float Class_Percentage = (Float.parseFloat(_AttendanceList.get(position).getMethod("Classes_Attended"))
					/ Float.parseFloat(_AttendanceList.get(position).getMethod("Classes_Total")) * 100);
			mViewHolder.attendance_percentage.setText(String.format("%.2f", Class_Percentage));

			LinearLayout.LayoutParams PercentageParams = new LinearLayout.LayoutParams(0,
					LinearLayout.LayoutParams.MATCH_PARENT);
			Float percentage = Class_Percentage / 100;
			PercentageParams.weight = percentage;
			mViewHolder.attendance_percentage_background.setLayoutParams(PercentageParams);

			if (percentage > 0.90) {
				mViewHolder.attendance_percentage_background.setBackgroundColor(Color.rgb(0, 128, 0));
			}
			;
			if (percentage > 0.75 && percentage < 0.90) {
				mViewHolder.attendance_percentage_background.setBackgroundColor(Color.rgb(255, 165, 0));
			}
			;
			if (percentage < 0.75) {
				mViewHolder.attendance_percentage_background.setBackgroundColor(Color.rgb(255, 0, 0));
			}
			;
			mViewHolder.attendance_percentage_background.refreshDrawableState();

			return convertView;
		}
	}

	public class AttendanceDialog extends DialogFragment {

		private View InformationView;
		private AlertDialog InformationDialog;
		private int position;

		public AttendanceDialog(int position) {
			this.position = position;
		}

		@SuppressLint("InflateParams")
		@Override
		public AlertDialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder InformationDialogBuilder = new AlertDialog.Builder(getActivity());

			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			InformationView = inflater.inflate(R.layout.dialog_attendance_information, null);

			final TextView InformationTitle = new TextView(getActivity());
			InformationTitle.setText(InformationList.get(position).getMethod("Course_Name"));
			InformationTitle.setBackgroundColor(Color.DKGRAY);
			InformationTitle.setPadding(10, 10, 10, 10);
			InformationTitle.setGravity(Gravity.CENTER);
			InformationTitle.setTextColor(Color.WHITE);
			InformationTitle.setTextSize(20);
			Typeface font_fertigo = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Fertigo.ttf");
			InformationTitle.setTypeface(font_fertigo);
			InformationDialogBuilder.setCustomTitle(InformationTitle);

			final TextView timetable_course_code = (TextView) InformationView.findViewById(R.id.timetable_course_code);
			final TextView timetable_course_type = (TextView) InformationView.findViewById(R.id.timetable_course_type);
			final TextView timetable_classes_attended = (TextView) InformationView
					.findViewById(R.id.timetable_classes_attended);
			final TextView timetable_total_classes = (TextView) InformationView
					.findViewById(R.id.timetable_total_classes);
			final TextView timetable_classes_needed = (TextView) InformationView
					.findViewById(R.id.timetable_classes_needed);

			timetable_course_code.setText(InformationList.get(position).getMethod("Course_Code"));
			timetable_course_type.setText(InformationList.get(position).getMethod("Course_Type"));
			timetable_classes_attended.setText(InformationList.get(position).getMethod("Classes_Attended"));
			timetable_total_classes.setText(InformationList.get(position).getMethod("Classes_Total"));
			
			Integer Classes_Needed=(int) (Integer.parseInt(InformationList.get(position).getMethod("Classes_Total")) * 0.75)-(Integer.parseInt(InformationList.get(position).getMethod("Classes_Attended")));
			timetable_classes_needed.setText(Classes_Needed.toString());
			
			InformationDialogBuilder.setView(InformationView);
			InformationDialog = InformationDialogBuilder.create();
			return InformationDialog;
		}
	}

	public class AttendanceDetailDialog extends DialogFragment {

		private View InformationView;
		private AlertDialog InformationDialog;
		private int position;

		public AttendanceDetailDialog(int position) {
			this.position = position;
		}

		@SuppressLint("InflateParams")
		@Override
		public AlertDialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder InformationDialogBuilder = new AlertDialog.Builder(getActivity());

			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			InformationView = inflater.inflate(R.layout.dialog_attendance_detail_information, null);

			final TextView InformationTitle = new TextView(getActivity());
			InformationTitle.setText(InformationList.get(position).getMethod("Course_Name"));
			InformationTitle.setBackgroundColor(Color.DKGRAY);
			InformationTitle.setPadding(10, 10, 10, 10);
			InformationTitle.setGravity(Gravity.CENTER);
			InformationTitle.setTextColor(Color.WHITE);
			InformationTitle.setTextSize(20);
			Typeface font_fertigo = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Fertigo.ttf");
			InformationTitle.setTypeface(font_fertigo);
			InformationDialogBuilder.setCustomTitle(InformationTitle);

			ListView mListView = (ListView) InformationView.findViewById(R.id.dialog_attendance_detail_listView);
			List<AttendanceDetailsInformationData> SudoAttendanceDetailInformationList = AttendanceDetailsInformationParser
					.parseList(AttendanceDetailsInformationParser.getListFile(getActivity(),
							"Attendance/attendance_details.plist"));

			List<AttendanceDetailsInformationData> mAttendanceDetailInformationList = new ArrayList<AttendanceDetailsInformationData>();

			for (int pos = 0; pos < SudoAttendanceDetailInformationList.size(); pos++) {
				if (SudoAttendanceDetailInformationList.get(pos).getMethod("Course_Name")
						.equals(InformationList.get(position).getMethod("Course_Name"))) {
					mAttendanceDetailInformationList.add(SudoAttendanceDetailInformationList.get(pos));
				}
			}

			AttendanceDialogListAdapter mAttendanceDialogListAdapter = new AttendanceDialogListAdapter(getActivity(),
					mAttendanceDetailInformationList);
			mListView.setAdapter(mAttendanceDialogListAdapter);

			InformationDialogBuilder.setView(InformationView);
			InformationDialog = InformationDialogBuilder.create();
			return InformationDialog;
		}

		private class AttendanceViewHolder {
			protected TextView attendance_dialog_row_date;
			protected TextView attendance_dialog_row_status;
		}

		public class AttendanceDialogListAdapter extends BaseAdapter {
			private LayoutInflater layoutInflater;

			private Context context;

			private List<AttendanceDetailsInformationData> _InformationList;

			public AttendanceDialogListAdapter(Context context,
					List<AttendanceDetailsInformationData> InformationList) {
				this._InformationList = InformationList;
				this.context = context;
				this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			public int getCount() {
				return _InformationList.size();
			}

			public Object getItem(int position) {
				return _InformationList.get(position);
			}

			public long getItemId(int position) {
				return position;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				AttendanceViewHolder mViewHolder;
				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.dialog_attendance_detail_information_row, parent,
							false);
					mViewHolder = new AttendanceViewHolder();
					mViewHolder.attendance_dialog_row_date = (TextView) convertView
							.findViewById(R.id.attendance_dialog_detail_row_date);
					mViewHolder.attendance_dialog_row_status = (TextView) convertView
							.findViewById(R.id.attendance_dialog_detail_row_status);

					convertView.setTag(mViewHolder);
				} else {
					mViewHolder = (AttendanceViewHolder) convertView.getTag();
				}
				mViewHolder.attendance_dialog_row_date.setText(_InformationList.get(position).getMethod("Course_Date"));
				mViewHolder.attendance_dialog_row_status
						.setText(_InformationList.get(position).getMethod("Course_Status"));
				return convertView;
			}
		}
	}
}