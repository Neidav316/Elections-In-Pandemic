package Controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import Exception.CheckingValidElectionMonth;
import Exception.CheckingValidElectionYear;
import Exception.CheckingValidMonth;
import Exception.OurException;
import Model.Candidate;
import Model.Elections;
import Model.Model;
import View.View;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;

@SuppressWarnings("serial")
public class Controller implements Serializable {
	protected static final Elections Elections = new Elections();
	private Model theModel;
	private View theView;

	public Controller(Model p_model, View p_View) {
		theModel = p_model;
		theView = p_View;

		theModel.setElectionsData();
		theView.createWindowForElectionDate();

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Changing the choice on the toggle group
		ChangeListener<Toggle> chlMenu = new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2) {
				String kind = theView.getKind();
				if (kind == "Add Ballot Box") {
					theView.update(kind, null, null,null);
				} else if (kind == "Add Citizen") {
					theView.update(kind, null, null,null);
				} else if (kind == "Add party") {
					theView.update(kind, null, null,null);
				} else if (kind == "Adding candidate") {
					theView.update(kind, theModel.getAllCitizens(), theModel.getAllParties(),theModel.getElectionsObject());
				} else if (kind == "Showing all ballot box") {
					theView.update(kind, theModel.getAllBallotBoxes(), null,null);
				} else if (kind == "Showing all citizens") {
					theView.update(kind, theModel.getAllCitizens(), null,null);
				} else if (kind == "Showing all parties") {
					theView.update(kind, theModel.getAllParties(), null,null);
				} else if (kind == "Elections") {
					if (theModel.getCitizenIndex() == 0) {
						theModel.setHowManyChoices();
					}
					theView.update(kind, theModel.getAllParties(),
							theModel.getAllCitizens().getObject(theModel.getCitizenIndex()),null);
				} else if (kind == "Showing results") {
					theView.update(kind, theModel.getAllParties(), theModel.getAllBallotBoxes(),null);
				} //else if (kind == "Exit") {
				//	theView.update(kind, null, null,null);
				//}

			}

		};
		theView.addChangeListenerToToggleGroup(chlMenu);
		
		EventHandler<ActionEvent> ExitProgram = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				theView.exitJavafxWindow();
			}
		};
		theView.addEventToExit(ExitProgram);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Enter elections date section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Handling with the election date input
		EventHandler<ActionEvent> enterElectionFullDate = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					@SuppressWarnings("deprecation")
					int[] previousElectionDate = {theModel.getPreviousElectionsDate().getYear(),theModel.getPreviousElectionsDate().getMonth()};
					int electionYear = theView.enterElectionFullDate().getYear();
					@SuppressWarnings("unused")
					CheckingValidElectionYear checkingElectionsYear = new CheckingValidElectionYear(
							previousElectionDate, electionYear);
					theModel.setYear(electionYear);

					int electionMonth = theView.enterElectionFullDate().getMonthValue();
					if (previousElectionDate[0] == electionYear) { 
						try {
							checkingValidMonth(electionMonth);

							@SuppressWarnings("unused")
							CheckingValidElectionMonth checkingValidElectionMonth = new CheckingValidElectionMonth(
									previousElectionDate[1], electionMonth);
							theModel.setMonth(electionMonth);
							theView.start();
						} catch (OurException oe) {
							theView.showMessage(oe.getMessage());
						} catch (InputMismatchException itm) {
							theView.showMessage("A month consists only of numbers.\nTry again");
						} catch (Exception ex) {
							theView.showMessage("There was an error in the month input - try again");
						}
					} else {
						checkingValidMonth(electionMonth);
						theModel.setMonth(electionMonth);
						theModel.setSoldierTable();
						theModel.setCitizensTable();
						theView.start();
					}
				} catch (OurException oe) {
					theView.showMessage(oe.getMessage());
				} catch (InputMismatchException itm) {
					theView.showMessage("A year consists only of numbers.\nTry again");
				} catch (Exception ex) {
					theView.showMessage("There was an error in the year input - try again");
				}

			}

		};
		theView.addEventToEnterElectionFulLDate(enterElectionFullDate);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		// Creating ballot box section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Handling with the ballot box inputs
		EventHandler<ActionEvent> doneCreatingBallotBox = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ArrayList<String> ballotInforamtion = theView.sendNewBallotInformation();
				if (ballotInforamtion.get(0).isBlank()) {
					theView.showMessage("You need to enter city name");
				} else if (!theView.checkingStringContent(ballotInforamtion.get(0), "city")) {
					System.out.println("Error with the city input");
				} else if (ballotInforamtion.get(1).isBlank()) {
					theView.showMessage("You need to enter street name");
				} else if (!theView.checkingStringContent(ballotInforamtion.get(1), "Street")) {
					System.out.println("Error with the street input");
				} else if (ballotInforamtion.get(2).isBlank()) {
					theView.showMessage("You need to enter house number");
				} else if (!theView.checkingNumAsStringContent(ballotInforamtion.get(2), "house number")) {
					System.out.println("Error with the house number input");
				} else if (ballotInforamtion.get(3) == null) {
					theView.showMessage("You need to choose ballot box type");
				} else {

					String fullAddress = ballotInforamtion.get(0) + ", " + ballotInforamtion.get(1) + ", "
							+ ballotInforamtion.get(2);
					int type = theModel.getBallotType(ballotInforamtion.get(3));

					theModel.creatingBallotBox(fullAddress, type);
					theView.showMessage("You added new ballot box successfully");
					theView.creatingBallotBox("", 0);
				}
			}
		};
		theView.addEventToDoneCreateBallotBoxButton(doneCreatingBallotBox);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// creating citizen section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EventHandler<ActionEvent> doneCreatingCitizen = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				ArrayList<String> citizenInforamtion = theView.sendNewCitizenInformation();
				if (citizenInforamtion.get(0).isBlank() || citizenInforamtion.get(1).isBlank()) {
					theView.showMessage("You need to enter citizen's full name");
				} else if (!theView.checkingStringContent(citizenInforamtion.get(0), "citizen")
						|| !theView.checkingStringContent(citizenInforamtion.get(1), "citizen")) {
					System.out.println("Error with the name input");
				} else if (citizenInforamtion.get(2).isBlank()) {
					theView.showMessage("You need to enter ID number");
				} else if (theModel.checkingValidId(citizenInforamtion.get(2))) {
					theView.showMessage("You need to enter 9 numbers in the ID");
				} else if (!theView.checkingNumAsStringContent(citizenInforamtion.get(2), "ID")) {
					System.out.println("Error with the ID input");
				} else if (theModel.checkDuplicateId(citizenInforamtion.get(2))) {
					theView.showMessage("ID already exists in the system\n try another");
				} else if (theView.getCitizenBirthDateFromTf() == null) {
					theView.showMessage("You need to enter birth date");
				} else if (theModel.checkValidBirthDate(theView.getBirthYear())) {
					theView.showMessage("Age needs to be above 17 years");
				} else if (Boolean.valueOf(citizenInforamtion.get(3))
						&& !theView.checkingNumAsStringContent(citizenInforamtion.get(4), "Isolation")) {
					theView.showMessage("Need to put number of days in Isolation");
				} else if (Boolean.valueOf(citizenInforamtion.get(3))
						&& citizenInforamtion.get(4).isEmpty()) {
					theView.showMessage("Need to put number of days in isolation");
				} else {

					theModel.createCitizen(citizenInforamtion.get(0)+" "+citizenInforamtion.get(1),citizenInforamtion.get(2), theView.getBirthYear(),
							Boolean.valueOf(citizenInforamtion.get(5)), Integer.valueOf(citizenInforamtion.get(4)));
					theView.showMessage("You added new citizen successfully");
					theView.createCitizen("", "", 0, true, 0);
				}
			}
		};
		theView.addEventToDoneCreateCitizenButton(doneCreatingCitizen);

		EventHandler<ActionEvent> checkCitizenAge = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (theView.getCitizenBirthDateFromTf() != null) {
					int age = theModel.checkAge(theView.getBirthYear());
					if (age <= 21 && age >= 18)
						theView.setWeaponActive(false);
					else
						theView.setWeaponActive(true);
				}
			}
		};
		theView.addEventToCheckCitizenAgeDatePicker(checkCitizenAge);

		EventHandler<ActionEvent> setIsolationDays = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				boolean chkIsolationPressed = theView.getChkIsolationBoolean();
				if (chkIsolationPressed)
					theView.setNumOfIsolationDays(true);
				else
					theView.setNumOfIsolationDays(false);

			}

		};
		theView.addEventToSetNumOfIsolationDaysTextField(setIsolationDays);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// creating party section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EventHandler<ActionEvent> doneCreatingParty = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (theView.getPartyNameValue().isBlank())
					theView.showMessage("Enter a party name");
				else if (!theView.checkingStringContent(theView.getPartyNameValue(), "Party"))
					System.out.println("incorrect name, try again");
				else if (theView.getFactionValue() == null)
					theView.showMessage("Choose the party's faction");
				else if (theView.getEstablishmentDateValue() == null)
					theView.showMessage("Choose the party's establishment date");
				else if (!theModel.checkEstablishmentDate(theView.getEstablishmentDateValue()))
					theView.showMessage("Date is incorrect, check if the date is not after elections");
				else {
					theModel.createParty(theView.getPartyNameValue(), theView.getFactionValue(),
							theView.getEstablishmentDateValue());
					theView.showMessage("You added a new party successfully");
					theView.createParty("", null, null);
				}
			}

		};
		theView.addEventToDoneCreatePartyButton(doneCreatingParty);
		
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// creating candidate section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EventHandler<ActionEvent> doneCreatingCandidate = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
																	//change citizen name to id
				String citizenName = theView.getCitizensChoice();
				String partyName = theView.getPartyChoice();
				theModel.createCandidate(null,null,0, citizenName, partyName);
				theView.showMessage("Added a new candidate successfully");
				/*if (theModel.getPartyInfo(partyName).getCandidates().size() == 1
						&& theModel.sampleForResultsSize() != 0) {
					theModel.addAnotherChoice();
				}*/
				theView.createCandidate(theModel.getAllCitizens(),theModel.getAllParties(),theModel.getElectionsObject().getElectionYear(), "", "");
			}

		};
		theView.addEventToDoneCreateCandidateButton(doneCreatingCandidate);

		EventHandler<ActionEvent> chosenCitizenForCandidate = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				theModel.setChosenCitizenName(theView.getCitizensChoice());
				theModel.printCitizenListDetails();
				if (theModel.getChosenCitizen() instanceof Candidate)
					theView.setLblIsAlreadyCandidateVisible(true);
				else
					theView.setLblIsAlreadyCandidateVisible(false);

				if (theView.checkForChoicesForCandidate() && !theModel.samePartyForCandidate()) {
					theView.setDoneCreatingCandidateBtnAvaliable(true);
					theView.setLblCantAssaignSamePartyVisible(false);
				} else if (theView.checkForChoicesForCandidate() && theModel.samePartyForCandidate()) {
					theView.setDoneCreatingCandidateBtnAvaliable(false);
					theView.setLblCantAssaignSamePartyVisible(true);
				}

			}
		};
		theView.addEventToChosenCitizesComboBox(chosenCitizenForCandidate);

		EventHandler<ActionEvent> chosenPartyForCandidate = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				theModel.setChosenPartyName(theView.getPartyChoice());

				if (theView.checkForChoicesForCandidate() && !theModel.samePartyForCandidate()) {
					theView.setDoneCreatingCandidateBtnAvaliable(true);
					theView.setLblCantAssaignSamePartyVisible(false);
				} else if (theView.checkForChoicesForCandidate() && theModel.samePartyForCandidate()) {
					theView.setDoneCreatingCandidateBtnAvaliable(false);
					theView.setLblCantAssaignSamePartyVisible(true);
				}
			}
		};
		theView.addEventToChosenPartyChoicesComboBox(chosenPartyForCandidate);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Show ballot box section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		EventHandler<ActionEvent> showBallotBox = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String serialNum = theView.getChosenBallotBox();
				if (serialNum == null) {
					theView.showMessage("You need to choose one of the options");
				} else {
					theModel.setChosenBallotSerialNum(Integer.valueOf(serialNum));
					theModel.printBallotListDetails();
					theView.setChosenBallotBoxInforamtion(theModel.getChosenBallotBoxInformation());
					theView.printBallotListDetails();
				}
			}

		};
		theView.addEventToShowBallotBoxButton(showBallotBox);

		EventHandler<ActionEvent> chosenBallotBox = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (theView.getChosenBallotBox() != null) {
					theView.setBtShowBallotBoxActive(false);
				}
			}
		};
		theView.addEventToChosenBallotComboBox(chosenBallotBox);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Show citizen section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		EventHandler<ActionEvent> showCitizen = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				String citizenId = theView.getChosenCitizen();
				if (citizenId == null) {
					theView.showMessage("You need to choose one of the options");
				} else {
					theModel.setChosenCitizenName(citizenId);
					theModel.printCitizenListDetails();
					theView.setChosenCitizenInforamtion(theModel.getChosenCitizenInformation());
					theView.printCitizenListDetails();
				}
			}

		};
		theView.addEventToShowCitizenButton(showCitizen);
		EventHandler<ActionEvent> chosenCitizen = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				theView.setBtShowCitizenActive(true);
			}
		};

		theView.addEventToChosenCitizenComboBox(chosenCitizen);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Show party section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		EventHandler<ActionEvent> showParty = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				String partyName = theView.getChosenParty();
				if (partyName == null) {
					theView.showMessage("You need to choose one of the options");
				} else {
					theModel.setChosenPartyName(partyName);
					theModel.printPartyListDetails(true);
					theView.setChosenPartyInforamtion(theModel.getChosenPartyInformation());
					theView.printPartyListDetails(true);
				}
			}

		};
		theView.addEventToShowPartyButton(showParty);

		EventHandler<ActionEvent> chosenParty = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				theView.setBtShowPartyActive(true);
			}
		};

		theView.addEventToChosenPartyComboBox(chosenParty);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Vote section
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EventHandler<ActionEvent> doneVoting = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (theView.getSelectedIfVote() == -1) {
					theView.showMessage("You need to choose if to vote or not!");
				} else if (theView.getSelectedIfVote() == 1) {
					theView.showMessage("You decided not to vote");
					nextCitizenForVoting();
				} else if (theView.getIfInProtectiveSuitsDisable() == false
						&& theView.getSelectedIfInProtectiveSuite() == -1) {
					theView.showMessage("You decided to vote, so you need to tell us if you are in protective suit...");
				} else if (theView.getIfInProtectiveSuitsDisable() == false
						&& theView.getSelectedIfInProtectiveSuite() == 1) {
					theView.showMessage("You cant vote due to no protection suit \n next voter...");
					nextCitizenForVoting();
				} else if (theView.getValueOfPartiesCb() == null) {
					theView.showMessage("You need to choose one of the parties...");
				} else {
					theView.showMessage("You voted to \n" + theView.getValueOfPartiesCb());
					theModel.setPartyIndex(theView.getValueOfPartiesCb());
					int partyIndex = theModel.getPartyIndex();
					theModel.voting(partyIndex);
					nextCitizenForVoting();
				}

			}

		};
		theView.addEventToDoneVotingButton(doneVoting);

		ChangeListener<Toggle> chlVote = new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2) {
				int ifVote = theView.getSelectedIfVote();
				if (ifVote == 0) {
					if (theModel.ifCitizenInQuarentine() == true) {
						theView.setIfInProtectiveSuitsVisible(true);
					} else {
						theView.setIfInProtectiveSuitsVisible(false);
						theView.setPartiesComboBoxVisible(true);
					}
				} else if (ifVote == 1) {
					theView.setPartiesComboBoxVisible(false);
					theView.setPartiesComboBox(false);
					theView.setIfInProtectiveSuitsVisible(false);
					theView.setSelectedIfInProtectiveSuite(false);

				}
			}
		};
		theView.addChangeListenerToTgVote(chlVote);

		ChangeListener<Toggle> chlInProtectiveSuit = new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2) {
				int ifProtectiveSuits = theView.getSelectedIfInProtectiveSuite();
				if (ifProtectiveSuits == 0) {
					theView.setPartiesComboBoxVisible(true);
				} else if (ifProtectiveSuits == 1) {
					theView.setPartiesComboBoxVisible(false);
					theView.setPartiesComboBox(false);
				}
			}

		};

		theView.addChangeListenerToTgInProtectiveSuit(chlInProtectiveSuit);
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// show results
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		EventHandler<ActionEvent> showPartyResults = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				String partyName = theView.getChosenPartyForResults();
				if (partyName == null) {
					theView.showMessage("You need to choose one of the options");
				} else {
					theModel.setPartyIndex(partyName);
					theModel.printResultsForEachParty();
					theView.setChosenPartyResults(theModel.getPartyResults());
					theView.printResultsForEachParty();
				}
			}

		};
		theView.addEventToShowPartyResultsButton(showPartyResults);

		EventHandler<ActionEvent> showBallotResults = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String serialNum = theView.getChosenBallotBoxForResults();
				if (serialNum == null) {
					theView.showMessage("You need to choose one of the options");
				} else {
					theModel.setBallotIndex(Integer.valueOf(serialNum));
					theModel.printResultsForEachBallot();
					theView.setChosenBallotResults(theModel.getBallotResults());
					theView.printResultsForEachBallot();
				}
			}

		};
		theView.addEventToShowBallotResultsButton(showBallotResults);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	}

	public void checkingValidMonth(int electionMonth) {
		try {
			@SuppressWarnings("unused")
			CheckingValidMonth checkingValidMonth = new CheckingValidMonth(electionMonth);
		} catch (OurException oe) {
			theView.showMessage(oe.getMessage());
		} catch (InputMismatchException itm) {
			theView.showMessage("A month consists only of numbers.\nTry again");
		} catch (Exception ex) {
			theView.showMessage("There was an error in the month input - try again");
		}
	}

	public void nextCitizenForVoting() { // change so that the list doesn't be created over and over 
		if (theModel.getCitizenIndex() + 1 < theModel.getAllCitizens().size()) { 
			theModel.getToTheNextCitizen();
			theView.creatingVotingWindow(theModel.getAllParties(),
					theModel.getAllCitizens().getObject(theModel.getCitizenIndex()));
		} else {
			theView.showMessage("The voting over!");
			theModel.setResultsToTables();
			theModel.setCitizenIndex(0);
			theView.setAlreadyVoted();
			theView.start();
		}
	}

}
