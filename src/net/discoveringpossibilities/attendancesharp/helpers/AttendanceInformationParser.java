package net.discoveringpossibilities.attendancesharp.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class AttendanceInformationParser {

	public static String getListFile(Context context, String listName) {
		StringBuffer mStringBuffer = new StringBuffer();
		BufferedReader mBufferedReader = null;
		try {
			mBufferedReader = new BufferedReader(new FileReader(new File(context.getFilesDir() + "/" + listName)));
			String temp;
			while ((temp = mBufferedReader.readLine()) != null)
				mStringBuffer.append(temp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				mBufferedReader.close(); // stop reading
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return mStringBuffer.toString();
	}

	public static void reWriteListFile(Context context, String fileName, List<AttendanceInformationData> InformationList) {
		try {
			File mFile = new File(context.getFilesDir() + "/" + fileName);
			if (!mFile.exists())
				mFile.createNewFile();
			FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
			OutputStreamWriter mOutputStreamWriter = new OutputStreamWriter(mFileOutputStream);

			mOutputStreamWriter.write(
					"<?XML version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n<plist version=\"2.0\">");

			for (int position = 0; position < InformationList.size(); position++) {
				mOutputStreamWriter.write("\n\t<Lecture>");
				mOutputStreamWriter.write("\n\t\t<Course_Code>" + InformationList.get(position).getMethod("Course_Code") + "</Course_Code>");
				mOutputStreamWriter.write("\n\t\t<Course_Name>" + InformationList.get(position).getMethod("Course_Name") + "</Course_Name>");
				mOutputStreamWriter.write("\n\t\t<Course_Type>" + InformationList.get(position).getMethod("Course_Type") + "</Course_Type>");
				mOutputStreamWriter.write("\n\t\t<Classes_Total>" + InformationList.get(position).getMethod("Classes_Total") + "</Classes_Total>");
				mOutputStreamWriter.write("\n\t\t<Classes_Attended>" + InformationList.get(position).getMethod("Classes_Attended") + "</Classes_Attended>");
				mOutputStreamWriter.write("\n\t</Lecture>");
			}

			mOutputStreamWriter.write("\n</plist>");

			mOutputStreamWriter.close();
			mFileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<AttendanceInformationData> parseList(String input) {
		List<AttendanceInformationData> InformationList = null;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(new StringReader(input));

			int eventType = xpp.getEventType();

			InformationList = new ArrayList<AttendanceInformationData>();
			AttendanceInformationData InformationData = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String ListTag = xpp.getName();
					if (ListTag.equals("Lecture"))
						InformationData = new AttendanceInformationData();
					if (ListTag.equals("Course_Code") || ListTag.equals("Course_Name") || ListTag.equals("Course_Type") || ListTag.equals("Classes_Total")
							|| ListTag.equals("Classes_Attended")) {
						InformationData.setMethod(ListTag, xpp.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (xpp.getName().equals("Lecture") && !InformationData.getMethod("Course_Name").isEmpty())
						InformationList.add(InformationData);
					break;
				}
				eventType = xpp.nextToken();
			}
		} catch (Exception ex) {
			Log.i("ListParser", "Parsing exception with message->" + ex.getMessage());
		}
		return InformationList;
	}
}
