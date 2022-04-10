package Model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Date;

import Model.Candidate.Roles;
//import Model.MySet;

@SuppressWarnings("serial")
public class Party implements Serializable {
	public static enum eFaction {
		Right , Left, Middle;
	};

	private String partyName;
	private eFaction faction;
	private Date creationDate;
	private MySet<Citizen> candidates;

	public Party() {
		this("", null, null, null);
	}
	public Party(String name) {
		this(name,null,null,null);
	}

	public Party(String name, eFaction side, Date date, MySet<Citizen> candidates) {
		setPartyName(name);
		setFaction(side);
		setCreationDate(date);
		if (candidates == null) {
			this.candidates = new MySet<>();
		} else {
			setCandidates(candidates);
		}
	}

	public Party(String name, eFaction side, Date date) {
		setPartyName(name);
		setFaction(side);
		setCreationDate(date);
		this.candidates = new MySet<>();
	}

	public boolean setPartyName(String name) {
		partyName = name;
		return true;
	}

	public boolean setFaction(eFaction side) {
		faction = side;
		return true;
	}

	// Default date when creating a party.
	@SuppressWarnings("deprecation")
	public boolean setCreationDate(Date date) {
		if (date == null) {
			creationDate = new Date(2000,1,1);
		} else {
			creationDate = date;
		}
		return true;
	}

	public boolean setCandidates(MySet<Citizen> candidates) {
		this.candidates = candidates;
		return true;
	}

	public MySet<Citizen> getCandidates() {
		return this.candidates;
	}

	public String getPartyName() {
		return partyName;

	}
	public eFaction getFaction() {
		return faction;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}

	// Adding candidate to a party
	public boolean addCandidate(Candidate newCandidate) {
		candidates.add(newCandidate);
		return true;
	}

	// Removing candidate from a party
	public boolean removeCandidateFromParty(Citizen citizen, int num) {
		for (int i = 0; i < candidates.size(); i++) {
			if (candidates.getObject(i) != null) {
				if ((candidates.getObject(i)).equals(citizen)) {
					if ((((Candidate) candidates.getObject(i)).getRole() == Roles.PartyLeader) && (num == 0)) {
						chooseNewPartyLeader(i);
					}
					candidates.remove(i);
					break;
				}
			}
		}
		return true;
	}

	public boolean chooseNewPartyLeader(int indexCandidate) {
		for (int i = 0; i < candidates.size(); i++) {
			if (candidates.getObject(i) != null && ((Candidate) candidates.getObject(i)).getRole() == Roles.Member
					&& i != indexCandidate) {
				((Candidate) candidates.getObject(i)).setRole(Roles.PartyLeader);
				return true;
			}
		}
		System.out.println("couldn't find a new party leader");
		return false;
	}

	@SuppressWarnings("deprecation")
	public String toString() {
		StringBuffer data = new StringBuffer(
				"Party name: " + partyName + "\nFaction: " + faction + "\nCreation Date: ");
		if (creationDate != null) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			String stringDate  = dateFormat.format(creationDate);
			data.append(stringDate);
			}
		data.append("\nCandidates:\n");
		if (candidates != null) {
			for (int i = 0; i < candidates.size(); i++) {
				if (candidates.getObject(i) != null) {
					data.append((i + 1) + ") " + (candidates.getObject(i)).getName() + "- role: "
							+ ((Candidate) candidates.getObject(i)).getRole() + "\n");
				}
			}
		} else {
			data.append("\nThere are no candidates from this party...\n");
		}
		return data.toString();
	}

	public boolean checkIfEmpty() {
		for (int i = 0; i < candidates.size(); i++)
			if (candidates.getObject(i) != null)
				return true;
		return false;
	}
}
