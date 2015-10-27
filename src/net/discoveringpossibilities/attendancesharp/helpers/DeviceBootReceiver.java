package net.discoveringpossibilities.attendancesharp.helpers;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class DeviceBootReceiver extends BroadcastReceiver {

	private List<TimeTableInformationData> TimeTableInformationList = null;
	private List<AttendanceInformationData> AttendanceInformationList = null;
	AlarmManager mAlarmManager;
	PendingIntent mPendingIntent;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			
			mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			
			for (int day = 1; day <= 7; day++) {
				TimeTableInformationList = TimeTableInformationParser.parseList(TimeTableInformationParser.getListFile(context, "TimeTable/" + day + ".plist"));
				for (int position = 0; position < TimeTableInformationList.size(); position++) {
					CreateNotification(position, context, day, false);
					CreateNotification(position, context, day, true);
				}
			}

			AttendanceInformationList = AttendanceInformationParser.parseList(AttendanceInformationParser.getListFile(context, "Attendance/attendance.plist"));
			for (int position = 0; position < AttendanceInformationList.size(); position++) {
				CreateReminder(position, context);
			}
		}
	}
	
	private void CreateReminder(int position, Context context) {
		Float Class_Percentage = (Float.parseFloat(AttendanceInformationList.get(position).getMethod("Classes_Attended")) / Float.parseFloat(AttendanceInformationList.get(position).getMethod("Classes_Total")) * 100);
		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		mCalendar.set(Calendar.HOUR_OF_DAY, 7);
		mCalendar.set(Calendar.MINUTE, 30);
		mCalendar.set(Calendar.SECOND, 0);
		
		Intent mIntent = new Intent(context, AlarmReceiver.class);
		mIntent.putExtra("Course_Name", AttendanceInformationList.get(position).getMethod("Course_Name"));
		mIntent.putExtra("Class_Percentage", Class_Percentage.toString());
		mIntent.putExtra("RepeatTime", Long.toString(AlarmManager.INTERVAL_DAY));
		mIntent.putExtra("Calendar", Long.toString(mCalendar.getTimeInMillis()));
		
		mPendingIntent = PendingIntent.getBroadcast(context, position + 514, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		if (Class_Percentage < 75) {
			CreateAlarm(mCalendar, AlarmManager.INTERVAL_DAY);
		} else {
			mAlarmManager.cancel(mPendingIntent);
		}
	}
	
	private void CreateNotification(int position, Context context, int day, boolean isGoingCheck) {
		String startTime = TimeTableInformationList.get(position).getMethod("Start_Time");
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

		Intent mIntent = new Intent(context, AlarmReceiver.class);
		mIntent.putExtra("Subject", TimeTableInformationList.get(position).getMethod("Subject"));
		mIntent.putExtra("Place", TimeTableInformationList.get(position).getMethod("Place"));
		mIntent.putExtra("RepeatTime", Long.toString(AlarmManager.INTERVAL_DAY * 7));
		mIntent.putExtra("Calendar", Long.toString(mCalendar.getTimeInMillis()));
		if(isGoingCheck) {
			mIntent.putExtra("isGoingCheck", isGoingCheck);
			mPendingIntent = PendingIntent.getBroadcast(context, position + 515, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			mPendingIntent = PendingIntent.getBroadcast(context, position + 516, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		CreateAlarm(mCalendar, AlarmManager.INTERVAL_DAY * 7);
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void CreateAlarm(Calendar mCalendar, Long repeatTime){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			mAlarmManager.setWindow(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), TimeUnit.SECONDS.toMillis(10), mPendingIntent);
		else
			mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), repeatTime, mPendingIntent);
	}
}