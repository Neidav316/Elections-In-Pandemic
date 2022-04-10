package Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import Model.Ballot.eBallotType;
import Model.Candidate.Roles;
import Model.Party.eFaction;
import View.ManageUI;
import java.sql.*;

@SuppressWarnings("serial")
public class Model implements ManageUI, Serializable {


	private Elections fastRound;
	
	private QueryElections jdbc;
	private ResultSet rs;
	
	private int ballotSerialNum;
	private String citizenId, partyName;
	@SuppressWarnings("rawtypes")
	private Ballot chosenBallotBox;
	private Citizen chosenCitizen;
	private Party chosenParty;
	private int citizenIndex, candidateIndex, partyIndex, ballotIndex;
	StringBuffer ballotResults, partyResults;

	public Model() throws FileNotFoundException, ClassNotFoundException, IOException {

		jdbc = new QueryElections();
		fastRound = new Elections();

	}

	public Elections getObject() {
		return fastRound;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	Comparator<Party> ComparePartyByName = new Comparator<Party>() {

		@Override
		public int compare(Party p1, Party p2) {
			return p1.getPartyName().compareTo(p2.getPartyName());
		}
	};

	@SuppressWarnings("rawtypes")
	Comparator<Ballot> CompareBallotBySerialNumber = new Comparator<Ballot>() {

		@Override
		public int compare(Ballot b1, Ballot b2) {
			return Double.compare(b1.getSerialNumber(), b2.getSerialNumber());
		}
	};


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public void setElectionsData() {
		fastRound = new Elections();
		fastRound.setPreviousElectionsYear(2020);
		fastRound.setPreviousElectionsMonth(12);
		
		try (Connection conn = jdbc.getConnection()){
			jdbc.resetVoteTable(conn);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void setYear(int electionYear) {
		fastRound.setYear(electionYear);
	}

	public Date getPreviousElectionsDate() {
		Date previousElectionDate = new Date(2020,1,1);
		try (Connection conn = jdbc.getConnection()){
			previousElectionDate = jdbc.getPreviousElectionsYear(conn);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return previousElectionDate;
	}

	public int getElectionYear() {
		return fastRound.getElectionYear();
	}

	public void setMonth(int electionMonth) {
		fastRound.setMonth(electionMonth);
	}
	public void setSoldierTable() {
		MySet<Citizen> allCitizens = new MySet<Citizen>();
		try (Connection conn = jdbc.getConnection()){
			allCitizens = jdbc.getAllCitizens(conn);
			for(int i = 0; i<allCitizens.size();i++) {
				if(fastRound.getElectionYear() - allCitizens.getObject(i).getBirthYear() > 21 &&
					jdbc.checkForCitizenInSoldiers(conn, allCitizens.getObject(i).getId()) != null ) {
					jdbc.removeSoldier(conn, allCitizens.getObject(i).getId());
					Ballot<?> newBallot = gettingBallot(conn,allCitizens.getObject(i).getNumOfDaysInQuarantine(),22);
					jdbc.changeBallot(conn, allCitizens.getObject(i).getId(), newBallot.getSERIAL_NUMBER());
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setCitizensTable() {
		MySet<Citizen> allCitizens = new MySet<Citizen>();
		try (Connection conn = jdbc.getConnection()){
			allCitizens = jdbc.getAllCitizens(conn);
			for(int i = 0; i<allCitizens.size();i++) {
				if(fastRound.getElectionYear() - allCitizens.getObject(i).getBirthYear() > 100) {
					if(allCitizens.getObject(i) instanceof Candidate)
						jdbc.removeCandidate(conn,allCitizens.getObject(i).getId());
					
					jdbc.removeCitizen(conn, allCitizens.getObject(i).getId());
				}
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void creatingBallotBox(String fullAddress, int type) {
		
	try (Connection conn = jdbc.getConnection()){
	
		String[] arrayAddress = fullAddress.split(", ");
		jdbc.addBallot(conn, arrayAddress, type);
		
		} catch (SQLException ex) {
		ex.printStackTrace();
		}
		

	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public int getBallotType(String ballotType) {
		int type = 0;
		eBallotType[] allBallotTypes = eBallotType.values();
		for (int i = 0; i < allBallotTypes.length; i++)
			if (ballotType.equals(allBallotTypes[i].toString()))
				type = i;
		return type;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void createCitizen(String citizenName, String id, int birthYear, boolean carryWeapon,
			int numOfDaysInIsolation) {
		try (Connection conn = jdbc.getConnection()){
			
			Citizen newCitizen;
			Ballot<?> citizenBallot = gettingBallot(conn,numOfDaysInIsolation, getElectionYear() - birthYear);
			if (getElectionYear() - birthYear < 21) {
				newCitizen = new Soldier(citizenName, id, birthYear, citizenBallot, numOfDaysInIsolation, carryWeapon);
			} else {
				newCitizen = new Citizen(citizenName, id, birthYear, citizenBallot, numOfDaysInIsolation);
			}
			jdbc.addCitizen(conn, newCitizen);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		

	}
	public Ballot<?> gettingBallot(Connection conn, int numOfDaysInIsolation, int citizenAge) throws SQLException {
		
		
		ArrayList<Ballot<?>> allBallots = jdbc.getAllBallots(conn);
		Ballot<?> citizenBallot = raffleBallot(allBallots);
		if (numOfDaysInIsolation != 0 && (citizenAge >= 18 && citizenAge <= 21)) {
			while (!(citizenBallot.getBallotType() == eBallotType.militaryCoronaBallot)) {
				citizenBallot = raffleBallot(allBallots);
			}
		} else if (numOfDaysInIsolation != 0) {
			while (!(citizenBallot.getBallotType() == eBallotType.regularCoronaBallot)) {
				citizenBallot = raffleBallot(allBallots);
			}
		} else if (citizenAge >= 18 && citizenAge <= 21) {
			while (!(citizenBallot.getBallotType() == eBallotType.militaryBallot)) {
				citizenBallot = raffleBallot(allBallots);
			}
		} else {
			while (!(citizenBallot.getBallotType() == eBallotType.regularBallot)) {
				citizenBallot = raffleBallot(allBallots);
			}
		}
		return citizenBallot;
	}
		
	public Ballot<?> raffleBallot(ArrayList<Ballot<?>> ballots) {
		int randomNum = (int) (Math.random() * ballots.size());
		while (ballots.get(randomNum) == null)
			randomNum = (int) (Math.random() * ballots.size());
		return ballots.get(randomNum);
	}


	public boolean checkingValidId(String s) {
		if (s.length() != 9)
			return true;
		else
			return false;
	}

	public boolean checkDuplicateId(String id) {
		
		try(Connection conn = jdbc.getConnection()){
			return jdbc.checkDuplicate(conn, id);
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean checkValidBirthDate(int birthYear) {
		if ((fastRound.getElectionYear() - birthYear) <= 17)
			return true;
		else
			return false;
	}

	public int checkAge(int age) {

		return fastRound.getElectionYear() - age;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void createParty(String partyName, eFaction faction, Date fullDate) {
		
		try (Connection conn = jdbc.getConnection()){
			Party newParty = new Party(partyName, faction, fullDate);
			jdbc.addParty(conn, newParty);
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

	}

	public boolean checkEstablishmentDate(Date fullDate) {
		if (fullDate.getMonth() >= fastRound.getElectionMonth() && fullDate.getYear() == fastRound.getElectionYear())
			return false;
		else if (fullDate.getYear() > fastRound.getElectionYear())
			return false;
		else
			return true;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void createCandidate(MySet<Citizen> citizens,ArrayList<Party> parties,int electionsYear, String id, String partyName) {  // change to id instead of name 
	
		
		try (Connection conn = jdbc.getConnection()){
			
			jdbc.addCandidate(conn,id,partyName);
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	public Citizen getChosenCitizen() {
		return chosenCitizen;
	}

	public boolean samePartyForCandidate() {
		if (chosenCitizen instanceof Candidate && ((Candidate) chosenCitizen).getPartyName() == partyName)
			return true;
		else
			return false;
	}
	

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void printBallotListDetails() {

		try (Connection conn = jdbc.getConnection()){
			
			chosenBallotBox = jdbc.showBallot(conn, ballotSerialNum);
			
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		

	}

	public ArrayList<Ballot<?>> getAllBallotBoxes() {
		
		ArrayList<Ballot<?>> ballotboxes = new ArrayList<Ballot<?>>();
		try (Connection conn = jdbc.getConnection()){
			
			ballotboxes = jdbc.getAllBallots(conn);
		
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		return ballotboxes;
	}

	public void setChosenBallotSerialNum(int chosenSerialNum) {
		ballotSerialNum = chosenSerialNum;
	}

	public String getChosenBallotBoxInformation() {
		return chosenBallotBox.toString();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void printCitizenListDetails() {

		try (Connection conn = jdbc.getConnection()){
			
			chosenCitizen = jdbc.showCitizen(conn, citizenId);
		
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public MySet<Citizen> getAllCitizens() {
		
		MySet<Citizen> citizensList = new MySet<Citizen>();
		try (Connection conn = jdbc.getConnection()){
			
			citizensList = jdbc.getAllCitizens(conn);
		
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		
		return citizensList;
	}

	public void setChosenCitizenName(String chosenCitizenId) {
		citizenId = chosenCitizenId;
	}

	public String getChosenCitizenInformation() {
		return chosenCitizen.toString();
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void printPartyListDetails(boolean printEmptyParty) {

		
		try (Connection conn = jdbc.getConnection()){
			
			chosenParty = jdbc.showParty(conn, partyName);
		
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public ArrayList<Party> getAllParties() {
		ArrayList<Party> partiesList = new ArrayList<Party>();
		
		try (Connection conn = jdbc.getConnection()){
			
			partiesList = jdbc.getAllParties(conn);

		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		
		return partiesList;
	}

	public void setChosenPartyName(String ChosenPartyName) {
		partyName = ChosenPartyName;
	}

	public String getChosenPartyInformation() {
		return chosenParty.toString();
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void voting(int choosenPartyNum) {
		try (Connection conn = jdbc.getConnection()){
			jdbc.addVote(conn,choosenPartyNum,getAllCitizens().getObject(getCitizenIndex()).getId());
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	public void setHowManyChoices() {
		ArrayList<Ballot<?>> allBallotBoxes = getAllBallotBoxes();
		int allPartiesSize = getAllParties().size();
		for (int i = 0; i < allBallotBoxes.size(); i++) {
			(allBallotBoxes.get(i)).setHowManyChoices(allPartiesSize);
		}
	}

	public int getCitizenIndex() {
		return citizenIndex;
	}

	public int getPartyIndex() {
		return partyIndex;
	}

	public void setPartyIndex(String partyName) {
		ArrayList<Party> parties = getAllParties();
		for (int i = 0; i < parties.size(); i++) {
			if (parties.get(i).getPartyName().equals(partyName)) {
				partyIndex = i;
			}
		}
	}

	public void setCitizenIndex(int num) {
		citizenIndex = num;
	}

	public void getToTheNextCitizen() {
		citizenIndex++;
	}

	public boolean ifCitizenInQuarentine() {
		if (getAllCitizens().getObject(getCitizenIndex()).getNumOfDaysInQuarantine() != 0) {
			return true;
		} else {
			return false;
		}
	}
	public void setResultsToTables() {
		Calendar c = Calendar.getInstance();
		c.set(fastRound.getElectionYear(), fastRound.getElectionMonth(), 1);
		Date date = c.getTime();
		try (Connection conn = jdbc.getConnection()){
			ArrayList<Party> allParties = jdbc.getAllParties(conn);
			for(int i = 0; i< allParties.size(); i++)
				jdbc.addResultsForParty(conn, i , date);
			
			ArrayList<Ballot<?>> allBallots = jdbc.getAllBallots(conn);
			for(int i = 0; i< allBallots.size(); i++)
				jdbc.addResultsForBallot(conn, i, date );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void printResultsForEachBallot() {
		ArrayList<Party> allParties = getAllParties();
		StringBuffer msg = new StringBuffer();
		ArrayList<Ballot<?>> allBallots = getAllBallotBoxes();
		Calendar c = Calendar.getInstance();
		c.set(fastRound.getElectionYear(), fastRound.getElectionMonth(), 1);
		Date date = c.getTime();
		try (Connection conn = jdbc.getConnection()){
			if(!jdbc.CheckResultsForBallot(conn,ballotIndex,date)){
				msg.append("Cant show results for a ballot \n created after the elections");
			}else {	
				msg.append("The results for  ballot number "
						+ allBallots.get(ballotIndex).getSerialNumber() + ":\n");
				double count = jdbc.ShowResultsForBallot(conn, ballotIndex, date);
				msg.append("Ballot S.R. : " + (allBallots.get(ballotIndex)).getSerialNumber() + "\n");
				if ((allBallots.get(ballotIndex)).getNumOfVoters() == 0)
					msg.append("No voters assagined to it.\n");
				else {
					msg.append("The results: \nPercentage of voters: "
							+ count + "\n");
					for (int j = 0; j < allParties.size(); j++) {
						if (allParties.get(j) != null) {
							int votersCount = jdbc.ShowResultsForPartyFromBallot(conn, j, ballotIndex);
							msg.append((j + 1) + ". party: " + (allParties.get(j)).getPartyName() + " they got: "
								+ votersCount + " votes\n");
					}
				}
			}
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		setBallotResults(msg);
	}

	private void setBallotResults(StringBuffer msg) {
		ballotResults = msg;
	}

	public StringBuffer getBallotResults() {
		return ballotResults;
	}

	public void setBallotIndex(int serialNumber) {
		ArrayList<Ballot<?>> allBallots = getAllBallotBoxes();
		int size = allBallots.size();
		for (int i = 0; i < size; i++) {
			if (allBallots.get(i).getSerialNumber() == serialNumber) {
				ballotIndex = i;
			}
		}
	}

	@Override
	public void printResultsForEachParty() {
		StringBuffer msg = new StringBuffer();
		Calendar c = Calendar.getInstance();
		c.set(fastRound.getElectionYear(), fastRound.getElectionMonth(), 1);
		Date date = c.getTime();
		int sumForEachParty = 0;
		try (Connection conn = jdbc.getConnection()){
			sumForEachParty = jdbc.ShowResultsForParty(conn,partyIndex,date);
		}catch (SQLException ex) {
			ex.printStackTrace();
		}
		msg.append("The party " + (getAllParties().get(partyIndex)).getPartyName() + ", got - "
				+ sumForEachParty + " votes\n");

		setChosenPartyResults(msg);
	}

	private void setChosenPartyResults(StringBuffer msg) {
		partyResults = msg;
	}

	public StringBuffer getPartyResults() {
		return partyResults;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void endingElections() throws FileNotFoundException, IOException {
		ObjectOutputStream outFile = new ObjectOutputStream(new FileOutputStream("elections.dat"));
		outFile.writeObject(fastRound);
		outFile.close();
	}

	public Elections getElectionsObject() {
		return fastRound;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}