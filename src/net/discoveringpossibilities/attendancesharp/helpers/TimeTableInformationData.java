package net.discoveringpossibilities.attendancesharp.helpers;

public class TimeTableInformationData {
	private String Course_Code;
	private String Start_Time;
	private String End_Time;
	private String Subject;
	private String Place;
	private String LectureType;
	private String Teacher;
	
	Types Type;

	public String getMethod(String selectedType) {
		String result = "";
		Type = Types.valueOf(selectedType);
		switch (Type) {
		case Course_Code:
			result = Course_Code;
			break;
		case Start_Time:
			result = Start_Time;
			break;
		case End_Time:
			result = End_Time;
			break;
		case Subject:
			result = Subject;
			break;
		case Place:
			result = Place;
			break;
		case LectureType:
			result = LectureType;
			break;
		case Teacher:
			result = Teacher;
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
		case Start_Time:
			this.Start_Time = someValue;
			break;
		case End_Time:
			this.End_Time = someValue;
			break;
		case Subject:
			this.Subject = someValue;
			break;
		case Place:
			this.Place = someValue;
			break;
		case LectureType:
			this.LectureType = someValue;
			break;
		case Teacher:
			this.Teacher = someValue;
			break;
		}
	}

	public enum Types {
		Course_Code, Start_Time, End_Time, Subject, Place, LectureType, Teacher
	}
}
