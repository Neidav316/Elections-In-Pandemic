package Model;

import java.io.Serializable;
import java.util.ArrayList;
import Model.Ballot.eBallotType;
import Model.Candidate.Roles;

@SuppressWarnings("serial")
public class Elections implements Serializable {

	private int month;
	private int year;
	private ArrayList<Ballot<?>> ballotBoxes;

	private MySet<Citizen> citizens;

	private ArrayList<Party> parties;

	private int previousElectionsYear;
	private int previousElectionsMonth;

	public Elections() {
		this(null, null, null);
	}

	public Elections(ArrayList<Ballot<?>> ballotsList, MySet<Citizen> citizensList, ArrayList<Party> partiesList) {
		if (citizensList != null) {
			setCitizensList(citizensList);
		}
		if (ballotsList != null) {
			setBallotBoxes(ballotsList);
		}

		if (partiesList != null) {
			setPartiesList(partiesList);
		}
	}

	public boolean setPartiesList(ArrayList<Party> partiesList) {
		if (parties == null) {
			parties = new ArrayList<>();
			for (int i = 0; i < partiesList.size(); i++) {
				parties.add(partiesList.get(i));
			}
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean setCitizensList(MySet<Citizen> citizensList) {

		if (citizens == null) {
			citizens = new MySet();
			for (int i = 0; i < citizensList.size(); i++) {
				citizens.add(citizensList.getObject(i));
			}
		}
		return true;
	}

	// Creating the ballot boxes list, and then sending every citizen to the list of
	// his ballot box (addVoter).
	public boolean setBallotBoxes(ArrayList<Ballot<?>> b) {
		if (ballotBoxes == null) {
			ballotBoxes = b;
			if (b != null) {
				for (int i = 0; i < b.size(); i++) {
					if (b.get(i) != null) {
						for (int j = 0; j < citizens.size(); j++) {
							if (citizens.getObject(j) != null
									&& (b.get(i)).getSerialNumber() == ((Citizen) citizens.getObject(j)).getBallotBox()
											.getSerialNumber()) {
								(ballotBoxes.get(i)).addVoter(citizens.getObject(j));
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean setMonth(int m) {
		if (m <= 1)
			month = 1;
		else if (m >= 12)
			month = 12;
		else
			month = m;
		return true;
	}

	public boolean setYear(int y) {
		year = y;
		return true;
	}


	public int getCitizenAge(Citizen citizen) {
		return year - citizen.getBirthYear();
	}

	public int getElectionYear() {
		return year;
	}

	public int getElectionMonth() {
		return month;
	}

	public boolean setPreviousElectionsYear(int p_year) {
		previousElectionsYear = p_year;
		return true;
	}

	public int getPreviousElectionsYear() {
		return previousElectionsYear;
	}

	public boolean setPreviousElectionsMonth(int p_month) {
		previousElectionsMonth = p_month;
		return true;
	}

	public int getPreviousElectionsMonth() {
		return previousElectionsMonth;
	}

	// Adding new citizen to the citizens list.
	public boolean addCitizen(String citizenName, String id, int birthYear, boolean carryWeapon,
			int numOfDaysInIsolation) {
		Citizen citizen;
		Ballot<?> citizenBallot = gettingBallot(numOfDaysInIsolation, getElectionYear() - birthYear, citizenName);
		if (getElectionYear() - birthYear < 21) {
			citizen = new Soldier(citizenName, id, birthYear, citizenBallot, numOfDaysInIsolation, carryWeapon);
		} else {
			citizen = new Citizen(citizenName, id, birthYear, citizenBallot, numOfDaysInIsolation);
		}
		addCitizenToBallotBox(citizen, false);
		return true;
	}

	public void addParty(Party newParty) {
		parties.add(newParty);
	}

	// Adding citizen to ballot box.
	public boolean addCitizenToBallotBox(Citizen citizen, boolean alreadyThere) {
		if (!alreadyThere) {
			citizens.add(citizen);
			int index = citizens.size() - 1;
			((Citizen) citizens.getObject(index)).getBallotBox().addVoter(citizens.getObject(index));
			return true;
		}
		for (int i = 0; i < citizens.size(); i++) {
			if (alreadyThere && (citizens.getObject(i).equals(citizen))) {
				((Citizen) citizens.getObject(i)).getBallotBox().addVoter(citizens.getObject(i));
				break;
			}
		}
		return true;
	}

	// Get random number to get random ballot
	public Ballot<?> raffleBallot(ArrayList<Ballot<?>> ballots) {
		int randomNum = (int) (Math.random() * ballots.size());
		while (ballots.get(randomNum) == null)
			randomNum = (int) (Math.random() * ballots.size());
		return ballots.get(randomNum);
	}

	// Getting a random ballot for the citizen when we creating him in the system,
	// and checking if it feet to him.
	public Ballot<?> gettingBallot(int numOfDays, int citizenAge, String name) {
		Ballot<?> citizenBallot = raffleBallot(ballotBoxes);
		if (numOfDays != 0 && (citizenAge >= 18 && citizenAge <= 21)) {
			while (!(citizenBallot.getBallotType() == eBallotType.militaryCoronaBallot)) {
				citizenBallot = raffleBallot(ballotBoxes);
			}
		} else if (numOfDays != 0) {
			while (!(citizenBallot.getBallotType() == eBallotType.regularCoronaBallot)) {
				citizenBallot = raffleBallot(ballotBoxes);
			}
		} else if (citizenAge >= 18 && citizenAge <= 21) {
			while (!(citizenBallot.getBallotType() == eBallotType.militaryBallot)) {
				citizenBallot = raffleBallot(ballotBoxes);
			}
		} else {
			while (!(citizenBallot.getBallotType() == eBallotType.regularBallot)) {
				citizenBallot = raffleBallot(ballotBoxes);
			}
		}
		return citizenBallot;
	}

	public boolean createAndAddBallotBox(String address, int choice) {
		if (choice == 1) {
			ballotBoxes.add(new Ballot<Candidate>(address, eBallotType.regularCoronaBallot));
		} else if (choice == 2) {
			ballotBoxes.add(new Ballot<Soldier>(address, eBallotType.militaryBallot));
		} else if (choice == 3) {
			ballotBoxes.add(new Ballot<Soldier>(address, eBallotType.militaryCoronaBallot));
		} else {
			ballotBoxes.add(new Ballot<Candidate>(address, eBallotType.regularBallot));
		}
		return true;
	}

	// Checking if the same id is already exist.
	public boolean checkDuplicateId(String id) {
		for (int i = 0; i < citizens.size(); i++)
			if (citizens.getObject(i) != null && (citizens.getObject(i)).getId().equals(id))
				return false;

		return true;
	}



	// Checking if there is any candidate for the party - if there is at least one,
	// the new candidate will get the role 'Member', and if there is not even one,
	// the new candidate will get the role 'PartyLeader'.
	public boolean givingRoleToNewCandidate(Citizen chosenCitizen, Party choosenParty, int citizenIndex, int num) {
		for (int i = 0; i < choosenParty.getCandidates().size(); i++) {
			if (choosenParty.getCandidates().getObject(i) != null) {
				citizens.add(new Candidate(chosenCitizen, choosenParty, Roles.Member));
				citizens.setObject(citizenIndex, new Candidate(chosenCitizen, choosenParty, Roles.Member));
				return true;
			}
		}
		citizens.add(new Candidate(chosenCitizen, choosenParty, Roles.PartyLeader));
		citizens.setObject(citizenIndex, new Candidate(chosenCitizen, choosenParty, Roles.PartyLeader));
//		}
		return true;
	}

	// Removing candidate from a citizens list
	public boolean removeCitizenFromCitizensList(String name) {
		for (int i = 0; i < citizens.size(); i++) {
			if (citizens.getObject(i) != null) {
				if ((citizens.getObject(i)).getName().equals(name)) {
					citizens.remove(i);
					break;
				}
			}
		}
		return true;
	}

	// Checks if there is any citizen that cross the age 100, and if we find some,
	// we removing him/them from all our data (citizens list, voters list of his
	// ballot, and if he is candidate - from his party).
	// Also, checks if there is a former soldier for transferring him to a ballot
	// box that is not for soldiers.
	public boolean refreshingVotersList() {
		for (int i = 0; i < citizens.size(); i++) {
			if (citizens.getObject(i) != null) {
				if (getCitizenAge(citizens.getObject(i)) > 100) {
					(citizens.getObject(i)).getBallotBox().removeVoterFromBallot((citizens.getObject(i)).getName());
					if (citizens.getObject(i) instanceof Candidate) {
						((Candidate) citizens.getObject(i)).getParty().removeCandidateFromParty((citizens.getObject(i)),
								0);
					}
					citizens.remove(i);
					i--;
				}

				// Checking if someone need to change his ballot box:
				// Checking if soldier released from the army
				else if (((citizens.getObject(i)).getBallotBox().getBallotType() == eBallotType.militaryBallot
						&& getCitizenAge(citizens.getObject(i)) > 21)
						|| ((citizens.getObject(i)).getBallotBox().getBallotType() == eBallotType.militaryCoronaBallot
								&& getCitizenAge(citizens.getObject(i)) > 21)) {
					Citizen citizen = new Citizen(citizens.getObject(i).getName(), citizens.getObject(i).getId(),
							citizens.getObject(i).getBirthYear(), citizens.getObject(i).getBallotBox(),
							citizens.getObject(i).getNumOfDaysInQuarantine());
					citizens.setObject(i, citizen);
//					}
					changingBallot(citizens.getObject(i));

				}
				// Checking the rest.
				else if (((citizens.getObject(i)).getBallotBox().getBallotType() == eBallotType.militaryCoronaBallot
						&& (citizens.getObject(i)).getNumOfDaysInQuarantine() == 0
						&& getCitizenAge(citizens.getObject(i)) <= 21)
						|| ((citizens.getObject(i)).getBallotBox().getBallotType() == eBallotType.militaryBallot
								&& (citizens.getObject(i)).getNumOfDaysInQuarantine() != 0
								&& getCitizenAge(citizens.getObject(i)) <= 21)
						|| ((citizens.getObject(i)).getBallotBox().getBallotType() == eBallotType.regularCoronaBallot
								&& (citizens.getObject(i)).getNumOfDaysInQuarantine() == 0)
						|| ((citizens.getObject(i)).getBallotBox().getBallotType() == eBallotType.regularBallot
								&& (citizens.getObject(i)).getNumOfDaysInQuarantine() != 0)) {
					changingBallot(citizens.getObject(i));
				}
			}
		}
		return true;
	}

	public boolean changingBallot(Citizen citizen) {
		citizen.getBallotBox().removeVoterFromBallot(citizen.getName());
		citizen.setBallotBox(gettingBallot(citizen.getNumOfDaysInQuarantine(),
				(getElectionYear() - citizen.getBirthYear()), citizen.getName()));
		addCitizenToBallotBox(citizen, true);
		return true;
	}

	public boolean checkDuplicatePartyName(String partyName) {
		for (int i = 0; i < parties.size(); i++)
			if (parties.get(i) != null && (parties.get(i)).getPartyName().equals(partyName))
				return false;
		return true;
	}

}
