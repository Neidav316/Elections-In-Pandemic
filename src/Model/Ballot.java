package Model;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class Ballot<T extends Citizen> implements Serializable {

	public static enum eBallotType {
		regularBallot("Regular Ballot Box"),
		militaryBallot("Military Ballot Box"),
		regularCoronaBallot("Regular Corona Ballot Box"),
		militaryCoronaBallot("Military Corona Ballot Box");
		private final String name;
		private eBallotType(String n) {
			name = n;
		}
		public String toString() {
			return this.name;
		}
	};

	protected int serialNum;
	protected String address;

	protected eBallotType type;

	protected MySet<T> votersList;

	protected double votersPercentage;

	protected ArrayList<Integer> results;

	protected int numOfVoters;

	public Ballot() {
		this(null, null, new MySet<>(), new ArrayList<>());
	}

	public Ballot(String address, eBallotType type) {
		this(address, type, new MySet<>(), new ArrayList<>());
	}

	public Ballot(String address, eBallotType p_type, MySet<Citizen> voters, ArrayList<Integer> results) {
		numOfVoters = 0;
		setAddress(address);
		setVotersList(voters);
		setVotersPercentage();
		setResults(results);
		this.type = p_type;
	}

	public int getSERIAL_NUMBER() {
		return serialNum;
	}

	public void setSERIAL_NUMBER(int SERIAL_NUMBER) {
		serialNum = SERIAL_NUMBER;
	}

	public boolean setAddress(String address) {
		this.address = address;
		return true;
	}

	public boolean setVotersList(MySet<Citizen> voters) {
		if (voters == null)
			votersList = new MySet<>();
		else {
			votersList = (MySet<T>) voters;
			numOfVoters = votersList.size();
		}
		return true;
	}
	public boolean setNumOfVoters(int sum) {
		numOfVoters = sum;
		return true;
	}
	
	public boolean setEBallotType(eBallotType p_type) {
		this.type = p_type;
		return true;
	}

	// Calculating the percentage of citizens who votes per ballot
	public boolean setVotersPercentage() {
		if (numOfVoters != 0) {
			double count = 0;
			if (this.results != null) {
				for (int i = 0; i < results.size(); i++) {
					count = count + results.get(i);
				}
			}
			this.votersPercentage = (double) (100 * (count / numOfVoters));
		}
		return true;
	}

	public double getVotersPercentage() {
		return this.votersPercentage;
	}

	public boolean setResults(ArrayList<Integer> results) {
		this.results = results;
		return true;
	}

	public ArrayList<Integer> getResults() {
		return results;
	}

	public boolean setNewResults(int index) {
		results.set(index, results.get(index) + 1);
		return true;
	}

	// Reseting the old results (of the previous elections) in the specific ballot
	// box
	public boolean resetingResultsInBallot(int index) {
		if (results.size() != 0) {
			results.set(index, 0);
		}
		return true;
	}

	public int getSerialNumber() {
		return serialNum;
	}

	public MySet<T> getVotersList() {
		return votersList;
	}

	public String getAddress() {
		return address;
	}

	public int getNumOfVoters() {
		return numOfVoters;
	}

	public eBallotType getBallotType() {
		return this.type;
	}

	public String toString() {
		StringBuffer data = new StringBuffer("The serial number for this ballot: " + serialNum + "\nThe address: "
				+ address + "\nNumber of citizens appointed to this ballot:" + numOfVoters + "\n");
		for (int i = 0; i < votersList.size(); i++) {
			if (votersList.getObject(i) != null) {
				data.append("citizen: " + (getVotersList().getObject(i)).getName() + "\n");
			}
		}
		data.append("\nBallot box type: " + this.type.toString());
		return data.toString();
	}

	// Adding new voter to the voters list of the ballot
	@SuppressWarnings("unchecked")
	public boolean addVoter(Citizen newVoter) {
		if (!getVoter(newVoter)) {
			return false;
		}
		votersList.add((T) newVoter);
		numOfVoters++;
		return true;
	}

	// Removing voter from a voters list
	public boolean removeVoterFromBallot(String name) {
		for (int i = 0; i < votersList.size(); i++) {
			if (votersList.getObject(i) != null) {
				if (((Citizen) votersList.getObject(i)).getName().equals(name)) {
					votersList.remove(i);
					numOfVoters--;
					break;
				}
			}
		}
		return true;
	}

	// Defining how many parties there will be in the election for the citizen to
	// vote, so that after that there will be the possibility to show the internal
	// results of every ballot individually
	public boolean setHowManyChoices(int numOfParties) {
		for (int i = 0; i < numOfParties; i++) {
			results.add(0);
		}
		return true;
	}
	
	public boolean addAnotherChoice() {
		results.add(0);
		return true;
	}
	
	public int getResultsSize() {
		return results.size();
	}

	public boolean getVoter(Citizen voter) {
		if (votersList != null) {
			for (int i = 0; i < votersList.size(); i++) {
				if (votersList.getObject(i) != null && votersList.getObject(i).equals(voter)) {
					return false;
				}
			}
		}
		return true;
	}

}
