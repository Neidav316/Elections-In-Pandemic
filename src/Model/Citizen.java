package Model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Citizen implements Quarantinable, Serializable {

	protected String name;
	protected String ID;
	protected int birthYear;
	protected Ballot<?> ballotBox;
	int numberOfDaysInIsolation;

	public Citizen() {
		this("", "", 0, null, 0);
	}
	public Citizen(String id) {
		this("",id,0,null,0);
	}

	public Citizen(String name, String ID, int birthYear, Ballot<?> ballotBox, int numberOfDaysInIsolation) {
		setName(name);
		setID(ID);
		setBirthYear(birthYear);
		setBallotBox(ballotBox);
		setNumOfDaysInQuarantine(numberOfDaysInIsolation);
	}

	public boolean setName(String name) {
		this.name = name;
		return true;
	}

	public boolean setID(String ID) {
		if (ID.length() != 9)
			return false;
		else
			this.ID = ID;
		return true;
	}

	public boolean setBirthYear(int birthYear) {
		this.birthYear = birthYear;
		return true;
	}

	public boolean setBallotBox(Ballot<?> ballotBox) {
		this.ballotBox = ballotBox;

		return true;
	}

	public String getId() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public int getBirthYear() {

		return birthYear;
	}

	public Ballot<?> getBallotBox() {
		return ballotBox;
	}

	public String toString() {
		StringBuffer data = new StringBuffer("Citizen name: " + name + "\nID: " + ID + "\nBirth Year: " + birthYear);
		if (ballotBox != null) {
			data.append("\nBallot box address: " + ballotBox.getAddress() + "\n");
		}
		if (numberOfDaysInIsolation != 0) {
			data.append("\n"+name + " is " + numberOfDaysInIsolation + " days in isolation\n");
		}
		return data.toString();

	}

	public boolean equals(Object other) {
		if (!(other instanceof Citizen)) {
			return false;
		}
		Citizen c = (Citizen) other;
		return c.ID == ID && c.name == name && c.birthYear == birthYear;
	}

	@Override
	public int getNumOfDaysInQuarantine() {
		return numberOfDaysInIsolation;
	}

	@Override
	public boolean setNumOfDaysInQuarantine(int numOfDays) {
		numberOfDaysInIsolation = numOfDays;
		return false;
	}

}
