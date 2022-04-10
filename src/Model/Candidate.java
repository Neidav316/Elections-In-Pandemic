package Model;

@SuppressWarnings("serial")
public class Candidate extends Citizen {

	public static enum Roles {
		Member , PartyLeader
	};

	private Roles role;
	private Party party;

	public Candidate() {
		this(null, null, null);
	}
	
	public Candidate(Citizen newCandidate, Party party, Roles role) {
		super(newCandidate.getName(), newCandidate.getId(), newCandidate.getBirthYear(), newCandidate.getBallotBox(),
				newCandidate.getNumOfDaysInQuarantine());
		setParty(party);
		setRole(role);
	}

	public boolean setRole(Roles role) {
		this.role = role;
		return true;
	}

	public boolean setParty(Party party) {
		this.party = party;
		return true;
	}

	public String getPartyName() {
		return party.getPartyName();
	}

	public Party getParty() {
		return party;
	}

	public Roles getRole() {
		return role;
	}

	public String toString() {
		return super.toString() + "He is a candidate for the party: " + party.getPartyName() + "\nIn the role of: "
				+ getRole() + "\n";
	}
}
