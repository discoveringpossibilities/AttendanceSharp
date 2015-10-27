package net.discoveringpossibilities.attendancesharp.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import net.discoveringpossibilities.attendancesharp.MarkAttendanceDialog;
import net.discoveringpossibilities.attendancesharp.R;

public class AlarmReceiver extends BroadcastReceiver {

	private static int NOTIFICATION = 123;
	private NotificationManager mNotificationManager;
	private Calendar mCalendar;

	@Override
	public void onReceive(Context context, Intent intent) {

		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(Long.valueOf(intent.getStringExtra("Calendar")));
		String LectureTime = new SimpleDateFormat("hh:mm aa", Locale.US).format(mCalendar.getTime());
		mCalendar.setTimeInMillis(Long.valueOf(intent.getStringExtra("Calendar")) + Long.valueOf(intent.getStringExtra("RepeatTime")));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			CreateAlarm(context, intent);
		}
		
		/**
		 * Code for making notification on Time!
		 */
		if(Calendar.getInstance().getTimeInMillis() > (Long.valueOf(intent.getStringExtra("Calendar")) + TimeUnit.SECONDS.toMillis(15))) return;
		
		Boolean isGoingCheck = intent.getBooleanExtra("isGoingCheck", false);
		String NotificationMessage = null;
		System.out.println("Notification Called!");

		Calendar mDateCalendar = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.US);
		String Course_Date = df.format(mDateCalendar.getTime());
		
		Intent mIntent = new Intent(context, MarkAttendanceDialog.class);
		mIntent.putExtra("Course_Name", intent.getStringExtra("Subject"));
		mIntent.putExtra("Course_Date", LectureTime + ", " + Course_Date);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(context);
		
		if (intent.hasExtra("Subject") && intent.hasExtra("Place")) {
			if(isGoingCheck) {
				mNotificationBuilder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("Are you present in lecture?").setContentIntent(mPendingIntent);
				NotificationMessage = "Subject: " + intent.getStringExtra("Subject") + ", Class Room: " + intent.getStringExtra("Place") + "  Click to answer!";
			} else {
				mNotificationBuilder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("Lecture is going to start!");
				NotificationMessage = "Subject: " + intent.getStringExtra("Subject") + " in Room Number: " + intent.getStringExtra("Place");
			}
		} else if (intent.hasExtra("Course_Name") && intent.hasExtra("Class_Percentage")) {
			mNotificationBuilder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("Attendance is falling short!");
			NotificationMessage = "Subject: " + intent.getStringExtra("Course_Name") + ", Percentage: " + intent.getStringExtra("Class_Percentage");
		}
		mNotificationBuilder.setWhen(System.currentTimeMillis()).setAutoCancel(true)
				.setContentText(NotificationMessage)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(NotificationMessage))
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
		mNotificationManager.notify(NOTIFICATION++, mNotificationBuilder.build());
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void CreateAlarm(Context context, Intent intent) {
		PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mAlarmManager.setWindow(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), TimeUnit.SECONDS.toMillis(10), mPendingIntent);
		}
	}
}