package net.discoveringpossibilities.attendancesharp.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import net.discoveringpossibilities.attendancesharp.R;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceDetailsInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceDetailsInformationParser;
import net.discoveringpossibilities.attendancesharp.helpers.TimeTableInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.TimeTableInformationParser;

public class TimeTableDayFragment extends Fragment {
	private GridView mGridView;
	private ListView mListView;
	private static TimeTableDayFragment mTimeTableDayFragment;

	private List<TimeTableInformationData> InformationList = null;
	private TimeTableListAdapter timetableListAdapter;
	private Integer day;
	private String parentFragment;

	public TimeTableDayFragment(List<TimeTableInformationData> InformationList, int day, String parentFragment) {
		this.InformationList = InformationList;
		this.day = day;
		this.parentFragment = parentFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.fragment_time_table_day, container, false);
		mGridView = (GridView) mView.findViewById(R.id.timetable_gridview);
		mListView = (ListView) mView.findViewById(R.id.timetable_listView);
		mTimeTableDayFragment = this;
		setHasOptionsMenu(true);
		return mView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		timetableListAdapter = new TimeTableListAdapter(getActivity());
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_about_us:
			new ContactDialog().show(getActivity().getSupportFragmentManager(), null);
			Toast.makeText(getActivity(), "Tap on the card to call that person!", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.action_add_item:
			new InformationAddDialog().show(getFragmentManager(), null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setGridView(Boolean isGrid) {
		if (isGrid) {
			mListView.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			mGridView.setAdapter(timetableListAdapter);
			mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					new InformationDialog(position, false).show(getFragmentManager(), null);
				}
			});
			mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					new InformationDialog(position, true).show(getFragmentManager(), null);
					return true;
				}
			});
		} else {
			mGridView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			mListView.setAdapter(timetableListAdapter);
			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					new InformationDialog(position, false).show(getFragmentManager(), null);
				}
			});
			mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					new InformationDialog(position, true).show(getFragmentManager(), null);
					return true;
				}
			});
		}
	}

	static class ViewHolder {
		protected TextView timetable_course_code;
		protected TextView timetable_course_place;
		protected TextView timetable_course_title;
		protected TextView timetable_course_type;
		protected TextView timetable_course_time;
		protected TextView timetable_course_teacher;
	}

	public class TimeTableListAdapter extends BaseAdapter {
		private LayoutInflater layoutInflater;
		private Context context;

		public TimeTableListAdapter(Context context) {
			this.context = context;
			this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return InformationList.size();
		}

		public Object getItem(int position) {
			return InformationList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mViewHolder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.fragment_courses_row, parent, false);
				mViewHolder = new ViewHolder();
				mViewHolder.timetable_course_code = (TextView) convertView.findViewById(R.id.timetable_course_code);
				mViewHolder.timetable_course_place = (TextView) convertView.findViewById(R.id.timetable_course_place);
				mViewHolder.timetable_course_title = (TextView) convertView.findViewById(R.id.timetable_course_title);
				mViewHolder.timetable_course_type = (TextView) convertView.findViewById(R.id.timetable_course_type);
				mViewHolder.timetable_course_time = (TextView) convertView.findViewById(R.id.timetable_course_time);
				mViewHolder.timetable_course_teacher = (TextView) convertView
						.findViewById(R.id.timetable_course_teacher);
				convertView.setTag(mViewHolder);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}

			mViewHolder.timetable_course_code.setText(InformationList.get(position).getMethod("Course_Code"));
			mViewHolder.timetable_course_place.setText(InformationList.get(position).getMethod("Place"));
			mViewHolder.timetable_course_title.setText(InformationList.get(position).getMethod("Subject"));
			mViewHolder.timetable_course_type.setText(InformationList.get(position).getMethod("LectureType"));
			mViewHolder.timetable_course_time.setText(InformationList.get(position).getMethod("Start_Time") + " - "
					+ InformationList.get(position).getMethod("End_Time"));
			mViewHolder.timetable_course_teacher.setText(InformationList.get(position).getMethod("Teacher"));
			return convertView;
		}
	}

	public class InformationDialog extends DialogFragment {

		private View InformationView;
		private AlertDialog InformationDialog;
		private int position;
		private Boolean isEditable = false;

		public InformationDialog(int position, Boolean editable) {
			this.position = position;
			this.isEditable = editable;
		}

		@SuppressLint("InflateParams")
		@Override
		public AlertDialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder InformationDialogBuilder = new AlertDialog.Builder(getActivity());

			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (isEditable) {
				InformationView = inflater.inflate(R.layout.dialog_time_table_information_editable, null);
				final EditText InformationTitle = new EditText(getActivity());
				InformationTitle.setText(InformationList.get(position).getMethod("Subject"));
				InformationTitle.setBackgroundColor(Color.DKGRAY);
				InformationTitle.setPadding(10, 10, 10, 10);
				InformationTitle.setGravity(Gravity.CENTER);
				InformationTitle.setTextColor(Color.WHITE);
				InformationTitle.setTextSize(20);
				Typeface font_fertigo = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Fertigo.ttf");
				InformationTitle.setTypeface(font_fertigo);
				InformationDialogBuilder.setCustomTitle(InformationTitle);

				final EditText timetable_course_code = (EditText) InformationView
						.findViewById(R.id.timetable_course_code);
				final EditText timetable_course_place = (EditText) InformationView
						.findViewById(R.id.timetable_course_place);
				final EditText timetable_course_type = (EditText) InformationView
						.findViewById(R.id.timetable_course_type);
				final EditText timetable_course_teacher = (EditText) InformationView
						.findViewById(R.id.timetable_course_teacher);
				final EditText timetable_course_time_start = (EditText) InformationView
						.findViewById(R.id.timetable_course_time_start);
				final EditText timetable_course_time_end = (EditText) InformationView
						.findViewById(R.id.timetable_course_time_end);
				timetable_course_time_start.setInputType(InputType.TYPE_NULL);
				timetable_course_time_start.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

						final Calendar mCalendar = Calendar.getInstance();
						int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
						int mMinute = mCalendar.get(Calendar.MINUTE);
						TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
								new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
								timetable_course_time_start.setText(hour_of_day + ":" + minute);
							}
						}, mHour, mMinute, false);
						mTimePickerDialog.show();
					}
				});
				timetable_course_time_start.setOnFocusChangeListener(new OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

							final Calendar mCalendar = Calendar.getInstance();
							int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
							int mMinute = mCalendar.get(Calendar.MINUTE);
							TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
									new TimePickerDialog.OnTimeSetListener() {
								@Override
								public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
									timetable_course_time_start.setText(hour_of_day + ":" + minute);
								}
							}, mHour, mMinute, false);
							mTimePickerDialog.show();
						}
					}
				});
				timetable_course_time_end.setInputType(InputType.TYPE_NULL);
				timetable_course_time_end.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

						final Calendar mCalendar = Calendar.getInstance();
						int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
						int mMinute = mCalendar.get(Calendar.MINUTE);
						TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
								new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
								timetable_course_time_end.setText(hour_of_day + ":" + minute);
							}
						}, mHour, mMinute, false);
						mTimePickerDialog.show();
					}
				});
				timetable_course_time_end.setOnFocusChangeListener(new OnFocusChangeListener() {
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

							final Calendar mCalendar = Calendar.getInstance();
							int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
							int mMinute = mCalendar.get(Calendar.MINUTE);
							TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
									new TimePickerDialog.OnTimeSetListener() {
								@Override
								public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
									timetable_course_time_end.setText(hour_of_day + ":" + minute);
								}
							}, mHour, mMinute, false);
							mTimePickerDialog.show();
						}
					}
				});

				timetable_course_code.setText(InformationList.get(position).getMethod("Course_Code"));
				timetable_course_place.setText(InformationList.get(position).getMethod("Place"));
				timetable_course_type.setText(InformationList.get(position).getMethod("LectureType"));
				timetable_course_time_start.setText(InformationList.get(position).getMethod("Start_Time"));
				timetable_course_time_end.setText(InformationList.get(position).getMethod("End_Time"));
				timetable_course_teacher.setText(InformationList.get(position).getMethod("Teacher"));

				Button save = (Button) InformationView.findViewById(R.id.save);
				Button cancel = (Button) InformationView.findViewById(R.id.cancel);
				Button delete = (Button) InformationView.findViewById(R.id.delete);

				save.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (TextUtils.isEmpty(InformationTitle.getText().toString())) {
							InformationTitle.setError("Subject is required!");
							InformationTitle.requestFocus();
						} else if (TextUtils.isEmpty(timetable_course_time_start.getText().toString())) {
							timetable_course_time_start.setError("Start Time is required!");
						} else if (TextUtils.isEmpty(timetable_course_time_end.getText().toString())) {
							timetable_course_time_end.setError("End Time is required!");
						} else {
							FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
							TimeTableFragment fragment = (TimeTableFragment) mFragmentManager
									.findFragmentByTag(parentFragment);
							fragment.NotificationManager(InformationList, position, day, false, false);
							fragment.NotificationManager(InformationList, position, day, false, true);

							InformationList.get(position).setMethod("Subject", InformationTitle.getText().toString());
							InformationList.get(position).setMethod("Course_Code",
									timetable_course_code.getText().toString());
							InformationList.get(position).setMethod("Place",
									timetable_course_place.getText().toString());
							InformationList.get(position).setMethod("LectureType",
									timetable_course_type.getText().toString());
							InformationList.get(position).setMethod("Teacher",
									timetable_course_teacher.getText().toString());
							InformationList.get(position).setMethod("Start_Time",
									timetable_course_time_start.getText().toString());
							InformationList.get(position).setMethod("End_Time",
									timetable_course_time_end.getText().toString());
							TimeTableInformationParser.reWriteListFile(getActivity(),
									"TimeTable/" + String.valueOf(day) + ".plist", InformationList);
							fragment.NotificationManager(InformationList, position, day, true, false);
							fragment.NotificationManager(InformationList, position, day, true, true);
							Toast.makeText(getActivity().getApplicationContext(), "Changes Saved!", Toast.LENGTH_LONG)
									.show();
							int orientation = getResources().getConfiguration().orientation;
							if (orientation == Configuration.ORIENTATION_LANDSCAPE)
								mTimeTableDayFragment.setGridView(true);
							else if (orientation == Configuration.ORIENTATION_PORTRAIT)
								mTimeTableDayFragment.setGridView(false);
							timetableListAdapter.notifyDataSetChanged();
							InformationDialog.dismiss();
						}
					}
				});

				cancel.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						int orientation = getResources().getConfiguration().orientation;
						if (orientation == Configuration.ORIENTATION_LANDSCAPE)
							mTimeTableDayFragment.setGridView(true);
						else if (orientation == Configuration.ORIENTATION_PORTRAIT)
							mTimeTableDayFragment.setGridView(false);
						InformationDialog.dismiss();
					}
				});
				delete.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						AlertDialog.Builder Logout = new AlertDialog.Builder(getActivity());
						Logout.setIcon(R.drawable.ic_menu_info);
						Logout.setTitle("Deleting Course: " + InformationTitle.getText().toString());
						Logout.setMessage("Sure you want to delete?");
						Logout.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
								TimeTableFragment fragment = (TimeTableFragment) mFragmentManager
										.findFragmentByTag(parentFragment);
								fragment.NotificationManager(InformationList, position, day, false, false);
								fragment.NotificationManager(InformationList, position, day, false, true);
								InformationList.remove(position);
								TimeTableInformationParser.reWriteListFile(getActivity(),
										"TimeTable/" + String.valueOf(day) + ".plist", InformationList);

								List<AttendanceDetailsInformationData> SudoAttendanceDetailInformationList = AttendanceDetailsInformationParser
										.parseList(AttendanceDetailsInformationParser.getListFile(getActivity(),
												"Attendance/attendance_details.plist"));
								List<AttendanceDetailsInformationData> mAttendanceDetailInformationList = new ArrayList<AttendanceDetailsInformationData>();
								for (int i = 0; i < SudoAttendanceDetailInformationList.size(); i++)
									if (!SudoAttendanceDetailInformationList.get(i).getMethod("Course_Name")
											.equals(InformationTitle.getText().toString()))
										mAttendanceDetailInformationList
												.add(SudoAttendanceDetailInformationList.get(i));
								AttendanceDetailsInformationParser.reWriteListFile(getActivity(),
										"Attendance/attendance_details.plist", mAttendanceDetailInformationList);

								Toast.makeText(getActivity().getApplicationContext(), "Course Deleted!",
										Toast.LENGTH_LONG).show();
								int orientation = getResources().getConfiguration().orientation;
								if (orientation == Configuration.ORIENTATION_LANDSCAPE)
									mTimeTableDayFragment.setGridView(true);
								else if (orientation == Configuration.ORIENTATION_PORTRAIT)
									mTimeTableDayFragment.setGridView(false);
								timetableListAdapter.notifyDataSetChanged();
								InformationDialog.dismiss();
							}
						});
						Logout.setNegativeButton("No!", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						AlertDialog LogoutDialog = Logout.create();
						LogoutDialog.show();
					}
				});

			} else {
				InformationView = inflater.inflate(R.layout.dialog_time_table_information, null);
				final TextView InformationTitle = new TextView(getActivity());
				InformationTitle.setText(InformationList.get(position).getMethod("Subject"));
				InformationTitle.setBackgroundColor(Color.DKGRAY);
				InformationTitle.setPadding(10, 10, 10, 10);
				InformationTitle.setGravity(Gravity.CENTER);
				InformationTitle.setTextColor(Color.WHITE);
				InformationTitle.setTextSize(20);
				Typeface font_fertigo = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Fertigo.ttf");
				InformationTitle.setTypeface(font_fertigo);
				InformationDialogBuilder.setCustomTitle(InformationTitle);

				final TextView timetable_course_code = (TextView) InformationView
						.findViewById(R.id.timetable_course_code);
				final TextView timetable_course_place = (TextView) InformationView
						.findViewById(R.id.timetable_course_place);
				final TextView timetable_course_type = (TextView) InformationView
						.findViewById(R.id.timetable_course_type);
				final TextView timetable_course_time_start = (TextView) InformationView
						.findViewById(R.id.timetable_course_time_start);
				final TextView timetable_course_time_end = (TextView) InformationView
						.findViewById(R.id.timetable_course_time_end);
				final TextView timetable_course_teacher = (TextView) InformationView
						.findViewById(R.id.timetable_course_teacher);

				timetable_course_code.setText(InformationList.get(position).getMethod("Course_Code"));
				timetable_course_place.setText(InformationList.get(position).getMethod("Place"));
				timetable_course_type.setText(InformationList.get(position).getMethod("LectureType"));
				timetable_course_time_start.setText(InformationList.get(position).getMethod("Start_Time"));
				timetable_course_time_end.setText(InformationList.get(position).getMethod("End_Time"));
				timetable_course_teacher.setText(InformationList.get(position).getMethod("Teacher"));

				Button okay = (Button) InformationView.findViewById(R.id.okay);
				okay.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						int orientation = getResources().getConfiguration().orientation;
						if (orientation == Configuration.ORIENTATION_LANDSCAPE)
							mTimeTableDayFragment.setGridView(true);
						else if (orientation == Configuration.ORIENTATION_PORTRAIT)
							mTimeTableDayFragment.setGridView(false);
						InformationDialog.dismiss();
					}
				});

			}
			InformationDialogBuilder.setView(InformationView);
			InformationDialog = InformationDialogBuilder.create();
			return InformationDialog;
		}
	}

	public class InformationAddDialog extends DialogFragment {

		private View InformationView;
		private AlertDialog InformationDialog;

		public InformationAddDialog() {
		}

		@SuppressLint("InflateParams")
		@Override
		public AlertDialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder InformationDialogBuilder = new AlertDialog.Builder(getActivity());

			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			InformationView = inflater.inflate(R.layout.dialog_time_table_information_editable, null);
			final EditText InformationTitle = new EditText(getActivity());
			InformationTitle.setHint("Subject Name");
			InformationTitle.setBackgroundColor(Color.DKGRAY);
			InformationTitle.setPadding(10, 10, 10, 10);
			InformationTitle.setGravity(Gravity.CENTER);
			InformationTitle.setTextColor(Color.WHITE);
			InformationTitle.setTextSize(20);
			Typeface font_fertigo = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Fertigo.ttf");
			InformationTitle.setTypeface(font_fertigo);
			InformationDialogBuilder.setCustomTitle(InformationTitle);

			final EditText timetable_course_code = (EditText) InformationView.findViewById(R.id.timetable_course_code);
			final EditText timetable_course_place = (EditText) InformationView
					.findViewById(R.id.timetable_course_place);
			final EditText timetable_course_type = (EditText) InformationView.findViewById(R.id.timetable_course_type);
			final EditText timetable_course_time_start = (EditText) InformationView
					.findViewById(R.id.timetable_course_time_start);
			final EditText timetable_course_time_end = (EditText) InformationView
					.findViewById(R.id.timetable_course_time_end);
			final EditText timetable_course_teacher = (EditText) InformationView
					.findViewById(R.id.timetable_course_teacher);

			timetable_course_code.setHint("Course Code");
			timetable_course_place.setHint("Lecture Location");
			timetable_course_type.setHint("Lecture Type");
			timetable_course_time_start.setHint("Start");
			timetable_course_time_end.setHint("End");
			timetable_course_teacher.setHint("Teacher Name");

			timetable_course_time_start.setInputType(InputType.TYPE_NULL);
			timetable_course_time_start.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

					final Calendar mCalendar = Calendar.getInstance();
					int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
					int mMinute = mCalendar.get(Calendar.MINUTE);
					TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
							new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
							timetable_course_time_start.setText(hour_of_day + ":" + minute);
						}
					}, mHour, mMinute, false);
					mTimePickerDialog.show();
				}
			});
			timetable_course_time_start.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

						final Calendar mCalendar = Calendar.getInstance();
						int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
						int mMinute = mCalendar.get(Calendar.MINUTE);
						TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
								new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
								timetable_course_time_start.setText(hour_of_day + ":" + minute);
							}
						}, mHour, mMinute, false);
						mTimePickerDialog.show();
					}
				}
			});
			timetable_course_time_end.setInputType(InputType.TYPE_NULL);
			timetable_course_time_end.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

					final Calendar mCalendar = Calendar.getInstance();
					int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
					int mMinute = mCalendar.get(Calendar.MINUTE);
					TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
							new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
							timetable_course_time_end.setText(hour_of_day + ":" + minute);
						}
					}, mHour, mMinute, false);
					mTimePickerDialog.show();
				}
			});
			timetable_course_time_end.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						InputMethodManager mInputMethodManager = (InputMethodManager) getActivity()
								.getSystemService(Context.INPUT_METHOD_SERVICE);
						mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

						final Calendar mCalendar = Calendar.getInstance();
						int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
						int mMinute = mCalendar.get(Calendar.MINUTE);
						TimePickerDialog mTimePickerDialog = new TimePickerDialog(getActivity(),
								new TimePickerDialog.OnTimeSetListener() {
							@Override
							public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
								timetable_course_time_end.setText(hour_of_day + ":" + minute);
							}
						}, mHour, mMinute, false);
						mTimePickerDialog.show();
					}
				}
			});

			Button save = (Button) InformationView.findViewById(R.id.save);
			Button cancel = (Button) InformationView.findViewById(R.id.cancel);
			Button delete = (Button) InformationView.findViewById(R.id.delete);
			delete.setVisibility(View.GONE);
			LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.5f);
			save.setLayoutParams(mLayoutParams);
			cancel.setLayoutParams(mLayoutParams);
			save.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (TextUtils.isEmpty(InformationTitle.getText().toString())) {
						InformationTitle.setError("Subject is required!");
						InformationTitle.requestFocus();
					} else if (TextUtils.isEmpty(timetable_course_time_start.getText().toString())) {
						timetable_course_time_start.setError("Start Time is required!");
					} else if (TextUtils.isEmpty(timetable_course_time_end.getText().toString())) {
						timetable_course_time_end.setError("End Time is required!");
					} else {
						TimeTableInformationData mInformationData = new TimeTableInformationData();
						mInformationData.setMethod("Subject", InformationTitle.getText().toString());
						mInformationData.setMethod("Course_Code", timetable_course_code.getText().toString());
						mInformationData.setMethod("Place", timetable_course_place.getText().toString());
						mInformationData.setMethod("LectureType", timetable_course_type.getText().toString());
						mInformationData.setMethod("Teacher", timetable_course_teacher.getText().toString());
						mInformationData.setMethod("Start_Time", timetable_course_time_start.getText().toString());
						mInformationData.setMethod("End_Time", timetable_course_time_end.getText().toString());
						InformationList.add(mInformationData);
						TimeTableInformationParser.reWriteListFile(getActivity(),
								"TimeTable/" + String.valueOf(day) + ".plist", InformationList);
						Toast.makeText(getActivity().getApplicationContext(), "Course Added!", Toast.LENGTH_LONG)
								.show();
						int orientation = getResources().getConfiguration().orientation;
						if (orientation == Configuration.ORIENTATION_LANDSCAPE)
							mTimeTableDayFragment.setGridView(true);
						else if (orientation == Configuration.ORIENTATION_PORTRAIT)
							mTimeTableDayFragment.setGridView(false);
						timetableListAdapter.notifyDataSetChanged();

						FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
						TimeTableFragment fragment = (TimeTableFragment) mFragmentManager
								.findFragmentByTag(parentFragment);
						fragment.NotificationManager(InformationList, InformationList.size() - 1, day, true, false);
						fragment.NotificationManager(InformationList, InformationList.size() - 1, day, true, true);
						InformationDialog.dismiss();
					}
				}
			});

			cancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int orientation = getResources().getConfiguration().orientation;
					if (orientation == Configuration.ORIENTATION_LANDSCAPE)
						mTimeTableDayFragment.setGridView(true);
					else if (orientation == Configuration.ORIENTATION_PORTRAIT)
						mTimeTableDayFragment.setGridView(false);
					InformationDialog.dismiss();
				}
			});
			InformationDialogBuilder.setView(InformationView);
			InformationDialog = InformationDialogBuilder.create();
			return InformationDialog;
		}
	}
}