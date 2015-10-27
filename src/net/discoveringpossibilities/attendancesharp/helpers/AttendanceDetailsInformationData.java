package net.discoveringpossibilities.attendancesharp.helpers;

public class AttendanceDetailsInformationData {
	private String Course_Name = "";
	private String Course_Date = "";
	private String Course_Status = "";
	Types Type;

	public String getMethod(String selectedType) {
		String result = null;
		Type = Types.valueOf(selectedType);
		switch (Type) {
		case Course_Name:
			result = Course_Name;
			break;
		case Course_Date:
			result = Course_Date;
			break;
		case Course_Status:
			result = Course_Status;
			break;
		}
		return result;
	}

	public void setMethod(String selectedType, String someValue) {
		Type = Types.valueOf(selectedType);
		switch (Type) {
		case Course_Name:
			this.Course_Name = someValue;
			break;
		case Course_Date:
			this.Course_Date = someValue;
			break;
		case Course_Status:
			this.Course_Status = someValue;
			break;
		}
	}

	public enum Types {
		Course_Name, Course_Date, Course_Status
	}
}
