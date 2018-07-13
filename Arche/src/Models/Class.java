package Models;

public class Class {
	private int id;
	private String name, abbreviation, notes;
	
	//initialization constructor
	public Class(int i, String n, String a, String no) {
		id = i;
		name = n;
		abbreviation = a;
		notes = no;
	}
	//new class constructor
	public Class(String n, String a, String no) {
		name = n;
		abbreviation = a;
		notes = no;
	}
	public void setId(int i) {
		id = i;
	}
	public void setName(String n) {
		name = n;
	}
	public void setAbbreviation(String a) {
		abbreviation = a;
	}
	public void setNotes(String n) {
		notes = n;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	public String getNotes() {
		return notes;
	}
}
