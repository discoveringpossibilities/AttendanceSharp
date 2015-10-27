package net.discoveringpossibilities.attendancesharp;

import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceDetailsInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceDetailsInformationParser;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceInformationData;
import net.discoveringpossibilities.attendancesharp.helpers.AttendanceInformationParser;

public class MarkAttendanceDialog extends Activity {

	private MarkAttendanceDialog MarkAttendanceDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mark_attendance);
		MarkAttendanceDialog = this;
		final String Course_Name = getIntent().getStringExtra("Course_Name");
		final String Course_Date = getIntent().getStringExtra("Course_Date");

		AlertDialog.Builder Logout = new AlertDialog.Builder(this);
		Logout.setIcon(R.drawable.ic_menu_info);
		Logout.setTitle("Mark attendance for " + Course_Name + "!");
		Logout.setMessage("Are you present for the above lecture?");
		Logout.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				List<AttendanceDetailsInformationData> mAttendanceDetailInformationList = AttendanceDetailsInformationParser
						.parseList(AttendanceDetailsInformationParser.getListFile(MarkAttendanceDialog, "Attendance/attendance_details.plist"));
				AttendanceDetailsInformationData mAttendanceDetailsInformationData = new AttendanceDetailsInformationData();
				mAttendanceDetailsInformationData.setMethod("Course_Name", Course_Name);
				mAttendanceDetailsInformationData.setMethod("Course_Date", Course_Date);
				mAttendanceDetailsInformationData.setMethod("Course_Status", "Present");
				mAttendanceDetailInformationList.add(mAttendanceDetailsInformationData);
				AttendanceDetailsInformationParser.reWriteListFile(MarkAttendanceDialog,
						"Attendance/attendance_details.plist", mAttendanceDetailInformationList);
				

				List<AttendanceInformationData> mAttendanceInformationList = AttendanceInformationParser
						.parseList(AttendanceInformationParser.getListFile(MarkAttendanceDialog, "Attendance/attendance.plist"));
				for(int i = 0; i < mAttendanceInformationList.size(); i++)
					if(mAttendanceInformationList.get(i).getMethod("Course_Name").equals(Course_Name))
						mAttendanceInformationList.get(i).setMethod("Classes_Attended", String.valueOf((Integer.parseInt(mAttendanceInformationList.get(i).getMethod("Classes_Attended")) + 1)));
				AttendanceInformationParser.reWriteListFile(MarkAttendanceDialog, "Attendance/attendance.plist", mAttendanceInformationList);
				dialog.cancel();
				MarkAttendanceDialog.finish();
			}
		});
		Logout.setNegativeButton("No!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				List<AttendanceDetailsInformationData> mAttendanceDetailInformationList = AttendanceDetailsInformationParser
						.parseList(AttendanceDetailsInformationParser.getListFile(MarkAttendanceDialog, "Attendance/attendance_details.plist"));
				AttendanceDetailsInformationData mAttendanceDetailsInformationData = new AttendanceDetailsInformationData();
				mAttendanceDetailsInformationData.setMethod("Course_Name", Course_Name);
				mAttendanceDetailsInformationData.setMethod("Course_Date", Course_Date);
				mAttendanceDetailsInformationData.setMethod("Course_Status", "Absent");
				mAttendanceDetailInformationList.add(mAttendanceDetailsInformationData);
				AttendanceDetailsInformationParser.reWriteListFile(MarkAttendanceDialog,
						"Attendance/attendance_details.plist", mAttendanceDetailInformationList);
				dialog.cancel();
				MarkAttendanceDialog.finish();
			}
		});
		AlertDialog LogoutDialog = Logout.create();
		LogoutDialog.show();
	}

}