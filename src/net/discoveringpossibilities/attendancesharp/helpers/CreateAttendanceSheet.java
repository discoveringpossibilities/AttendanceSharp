package net.discoveringpossibilities.attendancesharp.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class CreateAttendanceSheet extends AsyncTask<Void, Void, Boolean> {

	private Context mContext;

	public CreateAttendanceSheet(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	protected void onPreExecute() {
		Toast.makeText(mContext, "Creating excel file for attendance!", Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		ArrayList<String> CourseNames = new ArrayList<String>();
		ArrayList<String> CourseDates = new ArrayList<String>();
		ArrayList<String> CourseStatuses = new ArrayList<String>();
		
		ArrayList<String> TotalCourses = new ArrayList<String>();

		File sdCard = Environment.getExternalStorageDirectory();
		File directory = new File(sdCard.getAbsolutePath() + "/Attendance#");
		if (!directory.isDirectory()) {
			directory.mkdirs();
		}
		
		WorkbookSettings mWorkbookSettings = new WorkbookSettings();
		mWorkbookSettings.setLocale(new Locale("en", "EN"));
		WritableWorkbook mWritableWorkbook;

		List<AttendanceInformationData> InformationList = AttendanceInformationParser
				.parseList(AttendanceInformationParser.getListFile(mContext, "Attendance/attendance.plist"));
		for(int i=0;i<InformationList.size();i++){
			TotalCourses.add(InformationList.get(i).getMethod("Course_Name"));
		}
		
		
		try {
            File file = new File(mContext.getFilesDir() + "/Attendance/attendance_details.plist");
            InputStream is = new FileInputStream(file.getPath());
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document mDocument = docBuilder.parse(new InputSource(is));
			mDocument.getDocumentElement().normalize();

			NodeList lectures = mDocument.getElementsByTagName("Lecture");
			int totalLectures = lectures.getLength();

			for (int position = 0; position < totalLectures; position++) {
				Node LectureNode = lectures.item(position);
				if (LectureNode.getNodeType() == Node.ELEMENT_NODE) {
					Element LectureNodeElement = (Element) LectureNode;

					NodeList CourseNameList = LectureNodeElement.getElementsByTagName("Course_Name");
					Element CourseNameElement = (Element) CourseNameList.item(0);
					NodeList TextCourseNameList = CourseNameElement.getChildNodes();
					CourseNames.add(((Node) TextCourseNameList.item(0)).getNodeValue().trim());

					NodeList CourseDateList = LectureNodeElement.getElementsByTagName("Course_Date");
					Element CourseDateElement = (Element) CourseDateList.item(0);
					NodeList TextCourseDateList = CourseDateElement.getChildNodes();
					CourseDates.add(((Node) TextCourseDateList.item(0)).getNodeValue().trim());

					NodeList CourseStatusList = LectureNodeElement.getElementsByTagName("Course_Status");
					Element CourseStatusElement = (Element) CourseStatusList.item(0);
					NodeList TextCourseStatusList = CourseStatusElement.getChildNodes();
					CourseStatuses.add(((Node) TextCourseStatusList.item(0)).getNodeValue().trim());
				}
			}
		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		
		try {
			for(int pos=0;pos<TotalCourses.size();pos++){
				File mFile = new File(directory, TotalCourses.get(pos) + " - AttendanceList.xls");
				mWritableWorkbook = Workbook.createWorkbook(mFile, mWorkbookSettings);

				WritableSheet mWritableSheet = mWritableWorkbook.createSheet(TotalCourses.get(pos), 0);
				try {
					Map<String, Object[]> mHashMap = new HashMap<String, Object[]>();
					for (int i = 0; i < CourseNames.size(); i++) {
						if(CourseNames.get(i).equals(TotalCourses.get(pos)))
							mHashMap.put(i + "", new Object[] { CourseDates.get(i), CourseStatuses.get(i) });
					}
					Set<String> mDataKeySet = mHashMap.keySet();
					int rownum = 0;
					for (String key : mDataKeySet) {
						mWritableSheet.insertRow(rownum);
						Object[] objArr = mHashMap.get(key);
						int cellnum = 0;
						for (Object obj : objArr) {
							mWritableSheet.addCell(new Label(cellnum, rownum, obj.toString()));
							cellnum++;
						}
						rownum++;
					}
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				mWritableWorkbook.write();
				try {
					mWritableWorkbook.close();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	protected void onPostExecute(final Boolean success) {
		Toast.makeText(mContext,
				"Attendance Sheet Created! Check your phone's memory for Attendance#/ directory, and get the excel file in it!",
				Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onCancelled() {
	}
}