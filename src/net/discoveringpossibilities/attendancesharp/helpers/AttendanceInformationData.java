package net.discoveringpossibilities.attendancesharp.helpers;

public class AttendanceInformationData {
	private String Course_Code;
	private String Course_Name;
	private String Course_Type;
	private String Classes_Total = "0";
	private String Classes_Attended = "0";
	Types Type;

	public String getMethod(String selectedType) {
		String result = null;
		Type = Types.valueOf(selectedType);
		switch (Type) {
		case Course_Code:
			result = Course_Code;
			break;
		case Course_Name:
			result = Course_Name;
			break;
		case Course_Type:
			result = Course_Type;
			break;
		case Classes_Total:
			result = Classes_Total;
			break;
		case Classes_Attended:
			result = Classes_Attended;
			break;
		}
		return result;
	}

	public void setMethod(String selectedType, String someValue) {
		Type = Types.valueOf(selectedType);
		switch (Type) {
		case Course_Code:
			this.Course_Code = someValue;
			break;
		case Course_Name:
			this.Course_Name = someValue;
			break;
		case Course_Type:
			this.Course_Type = someValue;
			break;
		case Classes_Total:
			this.Classes_Total = someValue;
			break;
		case Classes_Attended:
			this.Classes_Attended = someValue;
			break;
		}
	}

	public enum Types {
		Course_Code, Course_Name, Course_Type, Classes_Total, Classes_Attended
	}
}
