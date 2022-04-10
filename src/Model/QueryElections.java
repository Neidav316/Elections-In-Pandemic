package Model;

import java.sql.*;
import java.util.ArrayList;

import Model.Ballot.eBallotType;
import Model.Candidate.Roles;
import Model.Party.eFaction;

public class QueryElections {

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	final private String dbUrl = "jdbc:mysql://localhost:3306/elections";

	//// SET COMMANDS
	private final static String RESET_AI_VOTE = "ALTER TABLE vote AUTO_INCREMENT = 1";
	private final static String CHANGE_BALLOT_FOR_CITIZEN = "UPDATE citizen SET BID = ? WHERE citizenID like ?";

	//// SHOW SPECIFIED OBJECT COMMANDS
	private final static String SHOW_BALLOT_BOX = "SELECT * FROM ballot WHERE ballotID = ?";
	private final static String SHOW_CITIZEN = "SELECT * FROM "+
										       "citizen LEFT JOIN soldier ON (citizenID = soldier.CID) LEFT JOIN candidate ON (citizenID = candidate.CID) LEFT JOIN party ON (candidate.PID = partyID)"+
										 	   " WHERE citizenID like ?";

	private final static String SHOW_SOLDIER = "SELECT CID, first_name, last_name,year_of_birth,num_of_days_in_inIsolation,BID, "
			+ "carry_weapon " + "FROM soldier inner join citizen on (CID = citizenID) " + "WHERE CID like ?";
	private final static String SHOW_PARTY = "SELECT * FROM party WHERE party_name like ?";
	private final static String SHOW_VOTES_FOR_PARTY = "SELECT * FROM votesperparty WHERE PID = ? AND date_of_elections = ?";
	private final static String SHOW_VOTES_FOR_PARTY_FROM_BALLOT = "SELECT COUNT(voteID) AS count_voters FROM vote INNER JOIN citizen ON (CID = citizenID)"
			+ "WHERE BID = ? AND PID = ?";
	private final static String SHOW_VOTES_PERCENTAGE_EACH_BALLOT = "SELECT * FROM votepercentageballot WHERE BID = ? AND date_of_elections = ?";// later
	private final static String SHOW_VOTES_SUM_FOR_PARTY = "SELECT PID ,COUNT(voteID) AS sum_of_votes FROM vote WHERE PID = ? GROUP BY PID";
	private final static String SHOW_VOTES_SUM_FOR_BALLOT = "SELECT c.BID ,COUNT(v.CID) as NUMBER_OF_VOTERS "
			+ "FROM vote as v LEFT JOIN citizen as c ON v.CID = c.citizenID " + "WHERE c.BID = ? GROUP BY voteID";
	

	//// GET ALL OBJECTS COMMANDS
	private final static String GET_ALL_BALLOT_BOXES = "SELECT * FROM ballot ";
	private final static String GET_ALL_CITIZENS = "SELECT * FROM citizen LEFT JOIN soldier ON (citizenID = soldier.CID) LEFT JOIN candidate ON (citizenID = candidate.CID) LEFT JOIN party ON (candidate.PID = partyID)";
	private final static String GET_ALL_CITIZENS_FROM_BALLOT = "SELECT * FROM citizen LEFT JOIN soldier ON (citizenID = soldier.CID) LEFT JOIN candidate ON (citizenID = candidate.CID) "
			+ "LEFT JOIN party ON (candidate.PID = partyID) " + " WHERE BID = ? ";
	private final static String GET_CITIZENS_COUNT_FROM_BALLOT = "SELECT COUNT(citizenID) as count_citizens FROM citizen "
			+ " WHERE BID = ? ";
	private final static String GET_ALL_SOLDIERS = "SELECT * from soldier";
	private final static String GET_ALL_CANDIDATES_FROM_PARTY = "SELECT * FROM citizen INNER JOIN candidate ON (CID = citizenID) "
			+ "INNER JOIN party ON (PID = partyID) " + "WHERE party_name like ?";
	private final static String GET_ALL_PARTIES = "SELECT * FROM party";
	private final static String GET_ALL_VOTES_FOR_EACH_PARTIES = "SELECT * FROM votesperparty";

	//// ADD COMMANDS
	private final static String ADD_BALLOT_BOX = "INSERT INTO ballot (ballot_type,city,street,home_number) values (?,?,?,?)";
	private final static String ADD_CITIZEN = "INSERT INTO citizen values (?,?,?,?,?,?)";
	private final static String ADD_CANDIDATE = "INSERT INTO candidate values (?,?,?)";
	private final static String ADD_SOLDIER = "INSERT INTO soldier values (?,?)";
	private final static String ADD_PARTY = "INSERT INTO party (party_name,faction_type_ID,date_of_creation) values (?,?,?)";
	private final static String ADD_VOTE = "INSERT INTO vote (CID,PID) values (?,?)";
	private final static String ADD_VOTE_TO_BALLOT_PERCENTAGE = "INSERT INTO votepercentageballot values (?,?,?)";
	private final static String ADD_VOTES_TO_PARTY = "INSERT INTO votesperparty values (?,?,?)";

	//// DELETE COMMANDS
	private final static String DELETE_SOLDIER = "DELETE FROM soldier WHERE CID like ? ";
	private final static String DELETE_CANDIDATE = "DELETE FROM candidate WHERE CID like ? ";
	private final static String DELETE_CITIZEN = "DELETE FROM citizen WHERE citizenID like ? ";
	private final static String RESET_VOTE_TABLE = "DELETE FROM vote";

	public Connection getConnection() {
		try {
			return DriverManager.getConnection(dbUrl, "root", "root1234");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public ArrayList<String> getAllSoldiers(Connection conn) throws SQLException {
		ArrayList<String> allSoldiers = new ArrayList<String>();
		try (PreparedStatement prepStmt = conn.prepareStatement(GET_ALL_SOLDIERS)) {
			try (ResultSet rs = prepStmt.executeQuery()) {
				while (rs.next())
					allSoldiers.add(rs.getString("CID"));
			}
		}
		return allSoldiers;

	}

	public Soldier checkForCitizenInSoldiers(Connection conn, String currentCitizenId) throws SQLException {
		Soldier foundSoldier = null;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_SOLDIER)) {
			prepStmt.setString(1, currentCitizenId);

			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					foundSoldier = new Soldier(rs.getString("first_name") + " " + rs.getString("last_name"),
							rs.getString("CID"), rs.getInt("year_of_birth"), showBallot(conn, rs.getShort("BID")),
							rs.getInt("num_of_days_in_inIsolation"), rs.getBoolean("carry_weapon"));
			}
		}

		return foundSoldier;
	}

	public void addSoldier(Connection conn, Citizen newSoldier, boolean carryWeapon) throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_SOLDIER)) {
			prepStmt.setString(1, newSoldier.getId());
			prepStmt.setBoolean(2, carryWeapon);
			prepStmt.executeUpdate();
		}
	}

	public void removeSoldier(Connection conn, String id) throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(DELETE_SOLDIER)) {
			prepStmt.setString(1, id);
			prepStmt.executeUpdate();
		}
	}
	public void changeBallot(Connection conn, String id, int ballotID) throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(CHANGE_BALLOT_FOR_CITIZEN)) {
			prepStmt.setInt(1, ballotID-1000);
			prepStmt.setString(2, id);
			prepStmt.executeUpdate();
		}
	}
	public void removeCandidate(Connection conn, String id) throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(DELETE_CANDIDATE)) {
			prepStmt.setString(1, id);
			prepStmt.executeUpdate();
		}
	}

	public void removeCitizen(Connection conn, String id) throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(DELETE_CITIZEN)) {
			prepStmt.setString(1, id);
			prepStmt.executeUpdate();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void addBallot(Connection conn, String[] fullAddress, int type) throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_BALLOT_BOX)) {
			prepStmt.setInt(1, type + 1);
			prepStmt.setString(2, fullAddress[0]);
			prepStmt.setString(3, fullAddress[1]);
			prepStmt.setString(4, fullAddress[2]);
			prepStmt.executeUpdate();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void addCitizen(Connection conn, Citizen newCitizen) throws SQLException {
		String[] fullname = newCitizen.getName().split(" ");
		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_CITIZEN)) {
			prepStmt.setString(1, newCitizen.getId());
			prepStmt.setString(2, fullname[0]);
			prepStmt.setString(3, fullname[1]);
			prepStmt.setInt(4, newCitizen.getBirthYear());
			prepStmt.setInt(5, newCitizen.getNumOfDaysInQuarantine());
			prepStmt.setInt(6, newCitizen.getBallotBox().getSerialNumber() - 1000);
			prepStmt.executeUpdate();
		}

		if (newCitizen instanceof Soldier) {
			try (PreparedStatement prepStmt = conn.prepareStatement(ADD_SOLDIER)) {
				prepStmt.setString(1, newCitizen.getId());
				prepStmt.setBoolean(2, ((Soldier) newCitizen).getCarryWeapon());
			}
		}
	}

	public ArrayList<Ballot<?>> getAllBallots(Connection conn) throws SQLException {
		ArrayList<Ballot<?>> allBallots = new ArrayList<Ballot<?>>();
		try (Statement prepStmt = conn.createStatement()) {
			try (ResultSet rs = prepStmt.executeQuery(GET_ALL_BALLOT_BOXES)) {
				Ballot<?> ballot;
				while (rs.next()) {
					ballot = new Ballot<>();
					ballot.setSERIAL_NUMBER(rs.getInt("ballotID") + 1000);
					ballot.setEBallotType(eBallotType.values()[rs.getInt("ballot_type") - 1]);
					ballot.setVotersList(getCitizesFromBallot(conn, ballot.getSERIAL_NUMBER()));
					allBallots.add(ballot);
				}
			}
		}
		return allBallots;
	}

	public boolean checkDuplicate(Connection conn, String newId) throws SQLException {

		try (Statement prepStmt = conn.createStatement()) {
			try (ResultSet rs = prepStmt.executeQuery(GET_ALL_CITIZENS)) {
				String id;
				while (rs.next()) {
					id = rs.getString("citizenID");
					if (newId.equals(id))
						return true;
				}
			}
		}
		return false;

	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public MySet<Citizen> getAllCitizens(Connection conn) throws SQLException {
		MySet<Citizen> allCitizens = new MySet<Citizen>();
		try (Statement prepStmt = conn.createStatement()) {
			try (ResultSet rs = prepStmt.executeQuery(GET_ALL_CITIZENS)) {
				Citizen citizen;
				while (rs.next()) {
					if (rs.getObject("soldier.CID") != null) // check if soldier
						citizen = new Soldier(rs.getString("first_name") + " " + rs.getString("last_name"),
								rs.getString("citizenID"), rs.getInt("year_of_birth"),
								showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation"),
								rs.getBoolean("carry_weapon"));
					else if (rs.getObject("candidate.CID") != null)
						citizen = new Candidate(
								new Citizen(rs.getString("first_name") + " " + rs.getString("last_name"),
										rs.getString("citizenID"), rs.getInt("year_of_birth"),
										showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation")),
								showParty(conn, rs.getString("party_name")), Roles.values()[rs.getInt("role_ID") - 1]);
					else
						citizen = new Citizen(rs.getString("first_name") + " " + rs.getString("last_name"),
								rs.getString("citizenID"), rs.getInt("year_of_birth"),
								showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation"));
					allCitizens.add(citizen);
				}
			}
		}
		return allCitizens;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public ArrayList<Party> getAllParties(Connection conn) throws SQLException {
		ArrayList<Party> allParties = new ArrayList<Party>();
		try (Statement prepStmt = conn.createStatement()) {
			try (ResultSet rs = prepStmt.executeQuery(GET_ALL_PARTIES)) {
				Party party;
				while (rs.next()) {
					@SuppressWarnings("deprecation")
					Date date = new Date(rs.getDate("date_of_creation").getYear(),
							rs.getDate("date_of_creation").getMonth(), rs.getDate("date_of_creation").getDay());
					party = new Party(rs.getString("party_name"), eFaction.values()[rs.getInt("faction_type_ID") - 1],
							date, getCandidatesFromParty(conn, rs.getString("party_name")));
					allParties.add(party);
				}
			}
		}
		return allParties;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void addParty(Connection conn, Party newParty) throws SQLException {

		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_PARTY)) {
			prepStmt.setString(1, newParty.getPartyName());
			prepStmt.setInt(2, newParty.getFaction().ordinal() + 1);
			System.out.println(newParty.toString());
			prepStmt.setDate(3, new java.sql.Date(newParty.getCreationDate().getTime()));
			prepStmt.executeUpdate();
		}
	}

	public void addCandidate(Connection conn, String id, String partyName) throws SQLException {
		Party currentParty = showParty(conn, partyName);
		int partyID = showPartyID(conn, partyName);
		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_CANDIDATE)) {
			prepStmt.setString(1, id);
			prepStmt.setInt(2, partyID);
			if (currentParty.getCandidates().isEmpty())
				prepStmt.setInt(3, 2); // empty party, new leader
			else
				prepStmt.setInt(3, 1);// already has a leader, becomes a member
			prepStmt.executeUpdate();
		}

	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public Citizen showCitizen(Connection conn, String id) throws SQLException {
		Citizen chosenCitizen = null;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_CITIZEN)) {
			prepStmt.setString(1, id);

			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					if (rs.getObject("soldier.CID") != null) // check if soldier
						chosenCitizen = new Soldier(rs.getString("first_name") + " " + rs.getString("last_name"),
								rs.getString("citizenID"), rs.getInt("year_of_birth"),
								showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation"),
								rs.getBoolean("carry_weapon"));
					else if (rs.getObject("candidate.CID") != null)
						chosenCitizen = new Candidate(
								new Citizen(rs.getString("first_name") + " " + rs.getString("last_name"),
										rs.getString("citizenID"), rs.getInt("year_of_birth"),
										showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation")),
								 new Party(rs.getString("party_name")), Roles.values()[rs.getInt("role_ID") - 1]);
					else
						chosenCitizen = new Citizen(rs.getString("first_name") + " " + rs.getString("last_name"),
								rs.getString("citizenID"), rs.getInt("year_of_birth"),
								showBallot(conn, rs.getShort("BID") + 1000), rs.getInt("num_of_days_in_inIsolation"));

			}
		}
		return chosenCitizen;
	}

	public Party showParty(Connection conn, String partyName) throws SQLException {
		Party chosenParty = null;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_PARTY)) {
			prepStmt.setString(1, partyName);

			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next()) {
					java.util.Date date = new java.util.Date(rs.getDate("date_of_creation").getTime());
					chosenParty = new Party(rs.getString("party_name"),
							eFaction.values()[rs.getInt("faction_type_ID") - 1], date,
							getCandidatesFromParty(conn, partyName));
				}
			}
		}
		return chosenParty;
	}

	public int showPartyID(Connection conn, String partyName) throws SQLException {
		int id = 0;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_PARTY)) {
			prepStmt.setString(1, partyName);
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					id = rs.getInt("partyID");
			}
		}
		return id;
	}

	public Ballot<?> showBallot(Connection conn, int id) throws SQLException {
		Ballot<?> chosenBallot = null;

		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_BALLOT_BOX)) {
			prepStmt.setInt(1, id - 1000);

			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next()) {
					if (eBallotType.values()[rs.getInt("ballot_type") - 1] == eBallotType.militaryBallot
							|| eBallotType.values()[rs.getInt("ballot_type") - 1] == eBallotType.militaryCoronaBallot)
						chosenBallot = new Ballot<Soldier>(
								rs.getString("city") + ", " + rs.getString("street") + ", "
										+ rs.getString("home_number"),
								eBallotType.values()[rs.getInt("ballot_type") - 1]);
					else
						chosenBallot = new Ballot<Candidate>(
								rs.getString("city") + ", " + rs.getString("street") + ", "
										+ rs.getString("home_number"),
								eBallotType.values()[rs.getInt("ballot_type") - 1]);

					chosenBallot.setSERIAL_NUMBER(rs.getInt("ballotID") + 1000);
					chosenBallot.setVotersList(getCitizesFromBallot(conn, chosenBallot.getSERIAL_NUMBER()));
				}
			}
		}
		return chosenBallot;
	}

	public MySet<Citizen> getCitizesFromBallot(Connection conn, int id) throws SQLException {
		MySet<Citizen> voters = new MySet<>();
		try (PreparedStatement prepStmt = conn.prepareStatement(GET_ALL_CITIZENS_FROM_BALLOT)) {
			prepStmt.setInt(1, id - 1000);
			Citizen citizen;
			try (ResultSet rs = prepStmt.executeQuery()) {
				while (rs.next()) {
					if (rs.getObject("soldier.CID") != null) // check if soldier
						citizen = new Soldier(rs.getString("first_name") + " " + rs.getString("last_name"),
								rs.getString("citizenID"), rs.getInt("year_of_birth"),
								showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation"),
								rs.getBoolean("carry_weapon"));
					else if (rs.getObject("candidate.CID") != null)
						citizen = new Candidate(
								new Citizen(rs.getString("first_name") + " " + rs.getString("last_name"),
										rs.getString("citizenID"), rs.getInt("year_of_birth"),
										showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation")),
								null, Roles.values()[rs.getInt("role_ID") - 1]);
					else
						citizen = new Citizen(rs.getString("first_name") + " " + rs.getString("last_name"),
								rs.getString("citizenID"), rs.getInt("year_of_birth"),
								showBallot(conn, rs.getShort("BID")), rs.getInt("num_of_days_in_inIsolation"));
					voters.add(citizen);
				}
			}
		}
		return voters;
	}

	public MySet<Citizen> getCandidatesFromParty(Connection conn, String partyName) throws SQLException {
		MySet<Citizen> candidates = new MySet<>();
		try (PreparedStatement prepStmt = conn.prepareStatement(GET_ALL_CANDIDATES_FROM_PARTY)) {
			prepStmt.setString(1, partyName);
			Candidate candidate;
			try (ResultSet rs = prepStmt.executeQuery()) {
				while (rs.next()) {
					candidate = new Candidate(showCitizen(conn, rs.getString("CID")), null,
							Roles.values()[rs.getInt("role_ID") - 1]);
					candidates.add(candidate);
				}
			}
		}
		return candidates;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("deprecation")
	public Date getPreviousElectionsYear(Connection conn) throws SQLException {
		Date date = new Date(2021,1,1);

		try (PreparedStatement prepStmt = conn.prepareStatement(GET_ALL_VOTES_FOR_EACH_PARTIES)) {
			try (ResultSet rs = prepStmt.executeQuery()) {
				while (rs.next()) {
					if (rs.getDate("date_of_elections").getYear() > date.getYear())
						date.setYear(rs.getDate("date_of_elections").getYear());
					if (rs.getDate("date_of_elections").getYear() == date.getYear() &&
							rs.getDate("date_of_elections").getMonth() > date.getMonth())
						date.setMonth(rs.getDate("date_of_elections").getMonth());
				}
			}
		}
		return date;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void addVote(Connection conn, int choosenPartyNum, String CitizenID) throws SQLException {

		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_VOTE)) {
			prepStmt.setString(1, CitizenID);
			prepStmt.setInt(2, choosenPartyNum + 1);
			prepStmt.executeUpdate();
		}
	}

	public void resetVoteTable(Connection conn) throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(RESET_VOTE_TABLE)) {
			prepStmt.executeUpdate();
		}
		try (PreparedStatement prepStmt = conn.prepareStatement(RESET_AI_VOTE)) {
			prepStmt.executeUpdate();
		}

	}

	public void addResultsForParty(Connection conn, int choosenPartyNum, java.util.Date currentDate)
			throws SQLException {

		int sumOfVotes = 0;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_VOTES_SUM_FOR_PARTY)) {
			prepStmt.setInt(1, choosenPartyNum + 1);
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					sumOfVotes = rs.getInt("sum_of_votes");
			}
		}

		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_VOTES_TO_PARTY)) {
			prepStmt.setInt(1, choosenPartyNum + 1);
			prepStmt.setInt(2, sumOfVotes);
			prepStmt.setDate(3, new java.sql.Date(currentDate.getTime()));
			prepStmt.executeUpdate();
		}
	}

	public void addResultsForBallot(Connection conn, int choosenBallotNum, java.util.Date currentDate)
			throws SQLException {
		int citizensCount = 0;
		try (PreparedStatement prepStmt = conn.prepareStatement(GET_CITIZENS_COUNT_FROM_BALLOT)) {
			prepStmt.setInt(1, choosenBallotNum + 1);
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					citizensCount = rs.getInt("count_citizens");
			}
		}
		int sumOfVotes = 0;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_VOTES_SUM_FOR_BALLOT)) {
			prepStmt.setInt(1, choosenBallotNum + 1);
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					sumOfVotes = rs.getInt("NUMBER_OF_VOTERS");
			}
		}

		double percentage = 0;
		if (citizensCount != 0)
			percentage = ((double) sumOfVotes / (double) citizensCount) * 100;
		try (PreparedStatement prepStmt = conn.prepareStatement(ADD_VOTE_TO_BALLOT_PERCENTAGE)) {
			prepStmt.setInt(1, choosenBallotNum + 1);
			prepStmt.setDouble(2, percentage);
			prepStmt.setDate(3, new java.sql.Date(currentDate.getTime()));
			prepStmt.executeUpdate();
		}
	}

	public int ShowResultsForParty(Connection conn, int choosenPartyNum, java.util.Date currentDate)
			throws SQLException {
		int voteCount = 0;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_VOTES_FOR_PARTY)) {
			prepStmt.setInt(1, choosenPartyNum + 1);
			prepStmt.setDate(2, new java.sql.Date(currentDate.getTime()));
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					voteCount = rs.getInt("votes_count");
			}
		}
		return voteCount;
	}

	public int ShowResultsForPartyFromBallot(Connection conn, int choosenPartyNum, int choosenBallotNum)
			throws SQLException {
		int voteCount = 0;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_VOTES_FOR_PARTY_FROM_BALLOT)) {
			prepStmt.setInt(1, choosenBallotNum + 1);
			prepStmt.setInt(2, choosenPartyNum + 1);
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					voteCount = rs.getInt("count_voters");
			}
		}
		return voteCount;
	}

	public double ShowResultsForBallot(Connection conn, int choosenBallotNum, java.util.Date currentDate)
			throws SQLException {
		double votePercentage = 0.0;
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_VOTES_PERCENTAGE_EACH_BALLOT)) {
			prepStmt.setInt(1, choosenBallotNum + 1);
			prepStmt.setDate(2, new java.sql.Date(currentDate.getTime()));
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					votePercentage = rs.getDouble("percent_votes");
			}
		}
		return votePercentage;
	}

	public boolean CheckResultsForBallot(Connection conn, int choosenBallotNum, java.util.Date currentDate)
			throws SQLException {
		try (PreparedStatement prepStmt = conn.prepareStatement(SHOW_VOTES_PERCENTAGE_EACH_BALLOT)) {
			prepStmt.setInt(1, choosenBallotNum + 1);
			prepStmt.setDate(2, new java.sql.Date(currentDate.getTime()));
			try (ResultSet rs = prepStmt.executeQuery()) {
				if (rs.next())
					return true;
			}
			return false;
		}
	}

}