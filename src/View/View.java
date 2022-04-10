package View;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;
import Exception.CheckingNumAsStringContent;
import Exception.CheckingStringContent;
import Exception.OurException;
import Model.Ballot;
import Model.Ballot.eBallotType;
import Model.Citizen;
import Model.Elections;
import Model.MySet;
import Model.Party;
import Model.Party.eFaction;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

@SuppressWarnings("serial")
public class View implements ManageUI, Serializable {
	private Scene scene;
	private ToggleGroup toggleGroup, tgVote, tgInProtectiveSuit/* , updatingToggleGroup */;
	private RadioButton rbAddCitizen, rbAddBallotBox, rbAddParty, rbAddingCandidate, rbShowingAllBallotBox,
			rbShowingAllCitizens, rbShowingAllParties, rbElections, rbShowResults, /*rbExit,*/ rbVoteYes, rbVoteNo,
			rbNotInProtectiveSuit, rbYesInProtectiveSuit;
	private BorderPane bp; // arrangement of Nodes to areas: LEFT,TOP,RIGHT,BOTTOM, CENTER
	private ComboBox<String> allBallotBoxes, allBallotBoxesTypes, allBallotBoxesForResults, allCitizens, allParties,
			allPartiesVote, allCitizensChoices, allPartiesChoices, allPartiesForResults;
	private ComboBox<eFaction> cbFaction;
	private Text txt, resultsForBallotBox, resultsForParty;
	private HBox hbOldData, hbSaveData, hbBallotBoxResults, hbPartiesResults, hbBallotBox;
	private VBox vbCenter, vbLeft, vbPartyResults, vbBallotResults;
	private Button btShowBallotBox, btShowCitizen, btShowParty, btDatePick, btLoadData, btDoNotLoadData, btSaveData,
			btDoNotSaveData, doneCreateBallotBoxBtn, doneCreateCitizenBtn, doneCreatePartyBtn, doneCreatingCadidateBtn,
			btDoneVoting, btShowPartyResults, btShowBallotResults,btExit;
	private Label lblLoadingOldData, lblSavingData, lblEmptyLine, lblWeapon, lblNumOfIsolationDays,
			lblInProtectiveSuite, lblChooseOneParty, lblIsAlreadyCandidate, lblCantAssaignSameParty;
	private TextField tfCity, tfStreet, tfHouseNumber, tfFirstName,tfLastName, tfID, tfNumOfIsolationDays, tfPartyName;
	private String chosenBallotBoxInformation, chosenCitizenInformation, chosenPartyInformation;
	private DatePicker datePick, birthDatePick, establishmentDatePick;
	private Stage globalStage;
	private CheckBox cbCheckIfInProtectiveSuite, chkWeapon, chkIsolation;
	private StringBuffer chosenBallotResults, chosenPartyResults;

	public View(Stage stage) {
		globalStage = stage;
		globalStage.setTitle("Elections");

		bp = new BorderPane();
		vbLeft = new VBox();// vertical box
		vbLeft.setAlignment(Pos.TOP_LEFT);
		vbLeft.setSpacing(10);
		vbLeft.setPadding(new Insets(10));

		vbCenter = new VBox();
		vbCenter.setAlignment(Pos.CENTER);
		vbCenter.setSpacing(10);
		vbCenter.setPadding(new Insets(100));

		// Load old data from binary file
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		lblLoadingOldData = new Label();
		lblLoadingOldData.setText("Do you want to load the old data?");

		btLoadData = new Button();
		btLoadData.setText("Yes");

		btDoNotLoadData = new Button();
		btDoNotLoadData.setText("No");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Save data to binary file
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		lblSavingData = new Label();
		lblSavingData.setText("Do you want to save the data?");

		btSaveData = new Button();
		btSaveData.setText("Yes");

		btDoNotSaveData = new Button();
		btDoNotSaveData.setText("No");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// For elections date
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// For choosing easily the election date
		datePick = new DatePicker();
		btDatePick = new Button();
		btDatePick.setText("Enter full Date");

		// For empty line label
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		lblEmptyLine = new Label("\n");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// For creating ballot box
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		doneCreateBallotBoxBtn = new Button("Add Ballot Box");

		// For creation citizen
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		doneCreateCitizenBtn = new Button("Add Citizen");
		birthDatePick = new DatePicker();
		chkIsolation = new CheckBox();
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// For creating party
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		doneCreatePartyBtn = new Button("Add Party");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// For creating candidate
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		doneCreatingCadidateBtn = new Button("Add Candidate");
		allCitizens = new ComboBox<>();
		allParties = new ComboBox<>();
		allCitizensChoices = new ComboBox<>();
		allPartiesChoices = new ComboBox<>();
		allBallotBoxes = new ComboBox<>();
		lblIsAlreadyCandidate = new Label("Note: this citizen is already a candidate");
		lblIsAlreadyCandidate.setTextFill(Color.RED);
		lblCantAssaignSameParty = new Label("Cant assaign the same party the \ncandidate is already assaigned");
		lblCantAssaignSameParty.setTextFill(Color.RED);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Button that shows 1 ballot box according to choosing from ComboBox
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		btShowBallotBox = new Button();
		btShowBallotBox.setText("Show ballot box");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Button that shows 1 citizen according to choosing from ComboBox
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		btShowCitizen = new Button();
		btShowCitizen.setText("Show citizen");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Button that shows 1 party according to choosing from ComboBox
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		btShowParty = new Button();
		btShowParty.setText("Show Party");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// For voting
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		cbCheckIfInProtectiveSuite = new CheckBox("Yes");
		btDoneVoting = new Button("Done");
		tgVote = new ToggleGroup();
		tgInProtectiveSuit = new ToggleGroup();
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		btShowPartyResults = new Button("Show party results");
		btShowBallotResults = new Button("Show ballot results");

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Creating toggle group for the menu
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		toggleGroup = new ToggleGroup();

		rbAddBallotBox = new RadioButton("Add Ballot Box");
		rbAddBallotBox.setToggleGroup(toggleGroup);

		rbAddCitizen = new RadioButton("Add Citizen");
		rbAddCitizen.setToggleGroup(toggleGroup);

		rbAddParty = new RadioButton("Add party");
		rbAddParty.setToggleGroup(toggleGroup);

		rbAddingCandidate = new RadioButton("Adding candidate");
		rbAddingCandidate.setToggleGroup(toggleGroup);

		rbShowingAllBallotBox = new RadioButton("Showing all ballot box");
		rbShowingAllBallotBox.setToggleGroup(toggleGroup);

		rbShowingAllCitizens = new RadioButton("Showing all citizens");
		rbShowingAllCitizens.setToggleGroup(toggleGroup);

		rbShowingAllParties = new RadioButton("Showing all parties");
		rbShowingAllParties.setToggleGroup(toggleGroup);

		rbElections = new RadioButton("Elections");
		rbElections.setToggleGroup(toggleGroup);

		rbShowResults = new RadioButton("Showing results");
		rbShowResults.setToggleGroup(toggleGroup);
		rbShowResults.setDisable(true);

		btExit = new Button("Exit");
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		vbLeft.getChildren().addAll(rbAddBallotBox, rbAddCitizen, rbAddParty, rbAddingCandidate, rbShowingAllBallotBox,
				rbShowingAllCitizens, rbShowingAllParties, rbElections, rbShowResults, btExit);

		bp.setCenter(vbCenter);

		scene = new Scene(bp, 750, 500);
		scene.setFill(Color.BLUEVIOLET);
		globalStage.setScene(scene);
		globalStage.show();
	}

	public void start() {
		vbCenter.getChildren().clear();
		bp.setLeft(vbLeft);
	}

	public void setAlreadyVoted() {
		rbElections.setDisable(true);
		rbShowResults.setDisable(false);
	}

	@SuppressWarnings("unchecked")
	public void update(String kind, Object obj1, Object obj2,Object obj3) {
		vbCenter.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		if (kind == "Add Ballot Box") {
			creatingBallotBox("", 0);
		} else if (kind == "Add Citizen") {
			createCitizen("", "", 0, true, 0);
		} else if (kind == "Add party") {
			createParty("", null, null);
		} else if (kind == "Adding candidate") {
			createCandidate(((MySet<Citizen>)obj1),((ArrayList<Party>)obj2),((Elections) obj3).getElectionYear(), "", "");
		} else if (kind == "Showing all ballot box") {
			createShowBallotBoxWindow((ArrayList<Ballot<?>>) obj1);
		} else if (kind == "Showing all citizens") {
			createShowCitizenWindow((MySet<Citizen>) obj1);
		} else if (kind == "Showing all parties") {
			createShowPartyWindow((ArrayList<Party>) obj1);
		} else if (kind == "Elections") {
			creatingVotingWindow((ArrayList<Party>) obj1, (Citizen) obj2);
		} else if (kind == "Showing results") {
			createShowResultsWindow((ArrayList<Party>) obj1, (ArrayList<Ballot<?>>) obj2);
		}
	}

	// Get the toggle group choice
	public String getKind() {
		if (rbAddBallotBox.isSelected()) {
			return rbAddBallotBox.getText();
		} else if (rbAddCitizen.isSelected())
			return rbAddCitizen.getText();
		else if (rbAddParty.isSelected())
			return rbAddParty.getText();
		else if (rbAddingCandidate.isSelected())
			return rbAddingCandidate.getText();
		else if (rbShowingAllBallotBox.isSelected())
			return rbShowingAllBallotBox.getText();
		else if (rbShowingAllCitizens.isSelected())
			return rbShowingAllCitizens.getText();
		else if (rbShowingAllParties.isSelected())
			return rbShowingAllParties.getText();
		else if (rbElections.isSelected())
			return rbElections.getText();
		else //if (rbShowResults.isSelected())
			return rbShowResults.getText();

	}

	// change listener has been created in Control
	public void addChangeListenerToToggleGroup(ChangeListener<Toggle> chl) {
		toggleGroup.selectedToggleProperty().addListener(chl);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void createWindowForElectionDate() {
		vbCenter.getChildren().clear();
		vbCenter.getChildren().addAll(datePick, btDatePick);
	}

	// Get the info from the datePicker
	public LocalDate enterElectionFullDate() {
		return datePick.getValue();
	}

	// Set event for the button with the full date election
	public void addEventToEnterElectionFulLDate(EventHandler<ActionEvent> event) {
		btDatePick.setOnAction(event);
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public String choosingFromMenu() {
		if (rbAddBallotBox.isSelected()) {
			return rbAddBallotBox.getText();
		} else if (rbAddCitizen.isSelected())
			return rbAddCitizen.getText();
		else if (rbAddParty.isSelected())
			return rbAddParty.getText();
		else if (rbAddingCandidate.isSelected())
			return rbAddingCandidate.getText();
		else if (rbShowingAllBallotBox.isSelected())
			return rbShowingAllBallotBox.getText();
		else if (rbShowingAllCitizens.isSelected())
			return rbShowingAllCitizens.getText();
		else if (rbShowingAllParties.isSelected())
			return rbShowingAllParties.getText();
		else if (rbElections.isSelected())
			return rbElections.getText();
		else //if (rbShowResults.isSelected())
			return rbShowResults.getText();

	}

	public boolean checkingNumAsStringContent(String txt, String type) {
		try {
			@SuppressWarnings("unused")
			CheckingNumAsStringContent checkingNumAsStringContent = new CheckingNumAsStringContent(txt, type);
			return true;
		} catch (OurException oe) {
			showMessage(oe.getMessage());
			return false;
		} catch (Exception ex) {
			showMessage("There was an error - " + ex.getMessage());
			return false;
		}
	}

	public boolean checkingStringContent(String txt, String type) {
		try {
			@SuppressWarnings("unused")
			CheckingStringContent checkingStringContent = new CheckingStringContent(txt, type);
			return true;
		} catch (OurException oe) {
			showMessage(oe.getMessage());
			return false;
		} catch (Exception ex) {
			showMessage("There was an error - " + ex.getMessage());
			return false;
		}
	}

	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void creatingBallotBox(String fullAddress, int type) {
		vbCenter.getChildren().clear();

		VBox vbLables = new VBox();

		VBox vbTextField = new VBox();

		HBox allTogether = new HBox();

		Label lblCity = new Label("Enter city: ");

		tfCity = new TextField();

		HBox hbStreet = new HBox();

		Label lblStreet = new Label("Enter street: ");
		tfStreet = new TextField();

		hbStreet.getChildren().addAll(lblStreet, tfStreet);

		Label lblHouseNumber = new Label("Enter house number: ");

		tfHouseNumber = new TextField();

		allBallotBoxesTypes = new ComboBox<>();
		allBallotBoxesTypes.getItems().addAll(eBallotType.regularBallot.toString(),
				eBallotType.militaryBallot.toString(), eBallotType.regularCoronaBallot.toString(),
				eBallotType.militaryCoronaBallot.toString());
		txt = new Text("Ballot boxes types: ");

		vbLables.getChildren().addAll(lblCity, lblStreet, lblHouseNumber, txt);
		vbTextField.getChildren().addAll(tfCity, tfStreet, tfHouseNumber, allBallotBoxesTypes, doneCreateBallotBoxBtn);
		vbLables.setSpacing(20);
		vbTextField.setSpacing(10);
		allTogether.getChildren().addAll(vbLables, vbTextField);

		vbCenter.getChildren().addAll(allTogether);
		vbCenter.setAlignment(Pos.CENTER);
	}

	public ArrayList<String> sendNewBallotInformation() {
		ArrayList<String> information = new ArrayList<>();
		information.add(tfCity.getText());
		information.add(tfStreet.getText());
		information.add(tfHouseNumber.getText());
//		information.add(allBallotBoxes.getValue());
		information.add(allBallotBoxesTypes.getValue());
		return information;
	}

	public void addEventToDoneCreateBallotBoxButton(EventHandler<ActionEvent> event) {
		doneCreateBallotBoxBtn.setOnAction(event);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void createCitizen(String citizenName, String id, int birthYear, boolean carryWeapon,
			int numOfDaysInIsolation) {
		vbCenter.getChildren().clear();
		birthDatePick.setValue(null);

		VBox vbLables = new VBox();

		VBox vbTextField = new VBox();

		HBox allTogether = new HBox();

		Label lblFirstName = new Label("Enter First name: ");

		tfFirstName = new TextField();
		
		Label lblLastName = new Label("Enter Last name: ");

		tfLastName = new TextField();

		Label lblID = new Label("Enter ID: ");

		tfID = new TextField();

		Label lblBirthYear = new Label("Enter birth year: ");

		Label lblIsolation = new Label("Does he need to be in isolation?");

		lblNumOfIsolationDays = new Label("Number of days in isolation: ");

		tfNumOfIsolationDays = new TextField();

		lblWeapon = new Label("He is a soldier\nDoes he carry a weapon?: ");

		chkWeapon = new CheckBox();

		lblWeapon.setDisable(true);
		chkWeapon.setDisable(true);
		tfNumOfIsolationDays.setVisible(false);
		lblNumOfIsolationDays.setVisible(false);
		chkIsolation.setSelected(false);

		vbLables.getChildren().addAll(lblFirstName,lblLastName, lblID, lblBirthYear, lblIsolation, lblNumOfIsolationDays, lblWeapon);
		vbTextField.getChildren().addAll(tfFirstName,tfLastName, tfID, birthDatePick, chkIsolation, tfNumOfIsolationDays, lblEmptyLine,
				chkWeapon, doneCreateCitizenBtn);
		vbLables.setSpacing(20);
		vbTextField.setSpacing(10);
		allTogether.getChildren().addAll(vbLables, vbTextField);
		allTogether.setSpacing(10);

		vbCenter.getChildren().addAll(allTogether);
		vbCenter.setAlignment(Pos.CENTER);

	}

	public void setWeaponActive(boolean set) {
		lblWeapon.setDisable(set);
		chkWeapon.setDisable(set);
	}

	public boolean getChkIsolationBoolean() {
		return chkIsolation.isSelected();
	}

	public void setNumOfIsolationDays(boolean set) {
		tfNumOfIsolationDays.setVisible(set);
		lblNumOfIsolationDays.setVisible(set);
	}

	public void addEventToDoneCreateCitizenButton(EventHandler<ActionEvent> event) {
		doneCreateCitizenBtn.setOnAction(event);
	}

	public void addEventToCheckCitizenAgeDatePicker(EventHandler<ActionEvent> event) {
		birthDatePick.setOnAction(event);
	}

	public void addEventToSetNumOfIsolationDaysTextField(EventHandler<ActionEvent> event) {
		chkIsolation.setOnAction(event);
	}

	public int getBirthYear() {
		return birthDatePick.getValue().getYear();
	}

	public LocalDate getCitizenBirthDateFromTf() {
		return birthDatePick.getValue();
	}

	public boolean checkIfCarryWeapon() {
		return chkWeapon.isSelected();
	}

	public ArrayList<String> sendNewCitizenInformation() {
		ArrayList<String> information = new ArrayList<>();
		information.add(tfFirstName.getText());
		information.add(tfLastName.getText());
		information.add(tfID.getText());
		information.add(String.valueOf(chkIsolation.isSelected()));
		if (tfNumOfIsolationDays.isVisible())
			information.add(tfNumOfIsolationDays.getText());
		else
			information.add(String.valueOf(0));
		if (!chkWeapon.isDisabled())
			information.add(String.valueOf(chkWeapon.isSelected()));
		else
			information.add(String.valueOf(false));
		return information;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void createParty(String partyName, eFaction faction, Date fullDate) {
		vbCenter.getChildren().clear();

		VBox vbLables = new VBox();

		VBox vbTextField = new VBox();

		HBox allTogether = new HBox();

		Label lblPartyName = new Label("Enter party's name: ");

		tfPartyName = new TextField();

		Label lblFaction = new Label("Choose party's faction: ");

		cbFaction = new ComboBox<eFaction>();
		cbFaction.getItems().addAll(eFaction.Right, eFaction.Left, eFaction.Middle);

		Label lblestablishmentDate = new Label("Enter party's date of establishment: ");

		establishmentDatePick = new DatePicker();

		vbLables.getChildren().addAll(lblPartyName, lblFaction, lblestablishmentDate);
		vbTextField.getChildren().addAll(tfPartyName, cbFaction, establishmentDatePick, doneCreatePartyBtn);
		vbLables.setSpacing(20);
		vbTextField.setSpacing(10);
		allTogether.getChildren().addAll(vbLables, vbTextField);
		allTogether.setSpacing(10);

		vbCenter.getChildren().addAll(allTogether);
		vbCenter.setAlignment(Pos.CENTER);

	}

	public void addEventToDoneCreatePartyButton(EventHandler<ActionEvent> event) {
		doneCreatePartyBtn.setOnAction(event);
	}

	public String getPartyNameValue() {
		return tfPartyName.getText();
	}

	public eFaction getFactionValue() {
		return cbFaction.getValue();
	}

	public Date getEstablishmentDateValue() {
		if (establishmentDatePick.getValue() != null) {
			Calendar c = Calendar.getInstance();
			c.set(establishmentDatePick.getValue().getYear(),establishmentDatePick.getValue().getMonthValue() - 1
					, establishmentDatePick.getValue().getDayOfMonth());
			Date fullDate = c.getTime();
			return fullDate;
		} else
			return null;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void createCandidate(MySet<Citizen> citizens,ArrayList<Party> parties,int electionsYear, String citizenName, String partyName) {
		vbCenter.getChildren().clear();

		VBox vbLables = new VBox();

		VBox vbTextField = new VBox();

		HBox allTogether = new HBox();

		Label lblCitizenOptions = new Label("Choose from the following, to be a cadidate: ");
		//MySet<Citizen> citizens = //elections.getAllCitizens();// change to model.getAllCitizens
		allCitizensChoices.getItems().clear();
		for (int i = 0; i < citizens.size(); i++) {
			if (electionsYear - citizens.getObject(i).getBirthYear() >= 21)
				allCitizensChoices.getItems().add(String.valueOf(citizens.getObject(i).getId()));// change to id and add name next to it
		}

		Label lblPartyOptions = new Label("Choose the party he will join: ");
		//ArrayList<Party> parties = elections.getAllParties();
		allPartiesChoices.getItems().clear();
		for (int i = 0; i < parties.size(); i++) {
			allPartiesChoices.getItems().add(String.valueOf(parties.get(i).getPartyName()));
		}
		doneCreatingCadidateBtn.setDisable(true);
		lblIsAlreadyCandidate.setVisible(false);
		lblCantAssaignSameParty.setVisible(false);
		vbLables.getChildren().addAll(lblCitizenOptions, lblIsAlreadyCandidate, lblPartyOptions,
				lblCantAssaignSameParty);
		vbTextField.getChildren().addAll(allCitizensChoices, allPartiesChoices, doneCreatingCadidateBtn);
		vbLables.setSpacing(20);
		vbTextField.setSpacing(50);
		allTogether.getChildren().addAll(vbLables, vbTextField);
		allTogether.setSpacing(10);

		vbCenter.getChildren().addAll(allTogether);
		vbCenter.setAlignment(Pos.CENTER);

	}

	public void addEventToDoneCreateCandidateButton(EventHandler<ActionEvent> event) {
		doneCreatingCadidateBtn.setOnAction(event);
	}

	public void addEventToChosenCitizesComboBox(EventHandler<ActionEvent> event) {
		allCitizensChoices.setOnAction(event);
	}

	public void addEventToChosenPartyChoicesComboBox(EventHandler<ActionEvent> event) {
		allPartiesChoices.setOnAction(event);
	}

	public boolean checkForChoicesForCandidate() {
		if (!allCitizensChoices.getSelectionModel().isEmpty() && !allPartiesChoices.getSelectionModel().isEmpty())
			return true;
		else
			return false;
	}

	public void setDoneCreatingCandidateBtnAvaliable(boolean set) {
		doneCreatingCadidateBtn.setDisable(!set);
	}

	public String getCitizensChoice() {
		return allCitizensChoices.getValue();
	}

	public String getPartyChoice() {
		return allPartiesChoices.getValue();
	}

	public void setLblIsAlreadyCandidateVisible(boolean set) {
		lblIsAlreadyCandidate.setVisible(set);
	}

	public void setLblCantAssaignSamePartyVisible(boolean set) {
		lblCantAssaignSameParty.setVisible(set);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void printBallotListDetails() {
		if (vbCenter.getChildren().size() == 3) {
			vbCenter.getChildren().remove(2);
		}

		txt = new Text(chosenBallotBoxInformation);
		vbCenter.getChildren().add(txt);
	}

	public void createShowBallotBoxWindow(ArrayList<Ballot<?>> ballotBox) {
		vbCenter.getChildren().clear();

		BorderPane bp1 = new BorderPane();

		allBallotBoxes.getItems().clear();
		for (int i = 0; i < ballotBox.size(); i++) {
			allBallotBoxes.getItems().add(String.valueOf(ballotBox.get(i).getSerialNumber())); //change to 
		}
		txt = new Text("Ballot boxes list: ");

		hbBallotBox = new HBox();
		hbBallotBox.getChildren().addAll(txt, allBallotBoxes);
		hbBallotBox.setAlignment(Pos.TOP_CENTER);

		bp1.setTop(hbBallotBox);
		btShowBallotBox.setDisable(true);

		vbCenter.getChildren().addAll(hbBallotBox, btShowBallotBox);
		vbCenter.setAlignment(Pos.TOP_CENTER);
	}

	public void addEventToChosenBallotComboBox(EventHandler<ActionEvent> event) {
		allBallotBoxes.setOnAction(event);
	}

	public String getChosenBallotBox() {
		return allBallotBoxes.getValue();
	}

	public void setBtShowBallotBoxActive(boolean set) {
		btShowBallotBox.setDisable(set);
	}

	public void setChosenBallotBoxInforamtion(String information) {
		chosenBallotBoxInformation = information;
	}

	public void addEventToShowBallotBoxButton(EventHandler<ActionEvent> event) {
		btShowBallotBox.setOnAction(event);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void printCitizenListDetails() {
		if (vbCenter.getChildren().size() == 3) {
			vbCenter.getChildren().remove(2);
		}

		txt = new Text(chosenCitizenInformation);
		vbCenter.getChildren().add(txt);
	}

	public void createShowCitizenWindow(MySet<Citizen> citizens) {
		vbCenter.getChildren().clear();

		BorderPane bp1 = new BorderPane();

		allCitizens.getItems().clear();
		for (int i = 0; i < citizens.size(); i++) {
			allCitizens.getItems().add(String.valueOf(citizens.getObject(i).getId()));
		}
		txt = new Text("Citizens list: ");

		HBox hbCitizens = new HBox();
		hbCitizens.getChildren().addAll(txt, allCitizens);
		hbCitizens.setAlignment(Pos.TOP_CENTER);

		bp1.setTop(hbCitizens);
		btShowCitizen.setDisable(true);

		vbCenter.getChildren().addAll(hbCitizens, btShowCitizen);
		vbCenter.setAlignment(Pos.TOP_CENTER);
	}

	public String getChosenCitizen() {
		return allCitizens.getValue();
	}

	public void setChosenCitizenInforamtion(String information) {
		chosenCitizenInformation = information.toString();
	}

	public void setBtShowCitizenActive(boolean set) {
		btShowCitizen.setDisable(!set);
	}

	public void addEventToChosenCitizenComboBox(EventHandler<ActionEvent> event) {
		allCitizens.setOnAction(event);
	}

	public void addEventToShowCitizenButton(EventHandler<ActionEvent> event) {
		btShowCitizen.setOnAction(event);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void printPartyListDetails(boolean printEmptyParty) {
		if (vbCenter.getChildren().size() == 3) {
			vbCenter.getChildren().remove(2);
		}

		txt = new Text(chosenPartyInformation);
		vbCenter.getChildren().add(txt);
	}

	public void createShowPartyWindow(ArrayList<Party> parties) {
		vbCenter.getChildren().clear();

		BorderPane bp1 = new BorderPane();

		allParties.getItems().clear();
		for (int i = 0; i < parties.size(); i++) {
			allParties.getItems().add(String.valueOf(parties.get(i).getPartyName()));
		}
		allParties.setVisible(true);
		txt = new Text("Parties list: ");

		HBox hbParties = new HBox();
		hbParties.getChildren().addAll(txt, allParties);
		hbParties.setAlignment(Pos.TOP_CENTER);

		bp1.setTop(hbParties);
		btShowParty.setDisable(true);

		vbCenter.getChildren().addAll(hbParties, btShowParty);
		vbCenter.setAlignment(Pos.TOP_CENTER);
	}

	public String getChosenParty() {
		return allParties.getValue();
	}

	public void setChosenPartyInforamtion(String information) {
		chosenPartyInformation = information.toString();
	}

	public void setBtShowPartyActive(boolean set) {
		btShowParty.setDisable(!set);
	}

	public void addEventToChosenPartyComboBox(EventHandler<ActionEvent> event) {
		allParties.setOnAction(event);
	}

	public void addEventToShowPartyButton(EventHandler<ActionEvent> event) {
		btShowParty.setOnAction(event);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Voting
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void voting(int choosenPartyNum) {
		vbCenter.getChildren().clear();

	}

	public void creatingVotingWindow(ArrayList<Party> parties, Citizen citizen) {
		vbCenter.getChildren().clear();

		Label lblWouldLikeToVote = new Label("Hello " + citizen.getName() + " Would you like to vote?");
		lblInProtectiveSuite = new Label("Are you in protective suite?");
		lblChooseOneParty = new Label("Please choose one of the following parties:");
		HBox hbYesNoForVoting = new HBox();

		rbVoteYes = new RadioButton("Yes");
		rbVoteYes.setToggleGroup(tgVote);
		rbVoteNo = new RadioButton("No");
		rbVoteNo.setToggleGroup(tgVote);
		hbYesNoForVoting.getChildren().addAll(rbVoteYes, rbVoteNo);
		hbYesNoForVoting.setAlignment(Pos.CENTER);

		HBox hbYesNoForInProtectiveSuite = new HBox();

		rbYesInProtectiveSuit = new RadioButton("Yes");
		rbYesInProtectiveSuit.setToggleGroup(tgInProtectiveSuit);
		rbYesInProtectiveSuit.setVisible(false);
		rbNotInProtectiveSuit = new RadioButton("No");
		rbNotInProtectiveSuit.setToggleGroup(tgInProtectiveSuit);
		rbNotInProtectiveSuit.setVisible(false);
		hbYesNoForInProtectiveSuite.getChildren().addAll(rbYesInProtectiveSuit, rbNotInProtectiveSuit);
		hbYesNoForInProtectiveSuite.setAlignment(Pos.CENTER);
		lblInProtectiveSuite.setVisible(false);
		lblChooseOneParty.setVisible(false);

		allPartiesVote = new ComboBox<>();
		for (int i = 0; i < parties.size(); i++) {
			if (parties.get(i).getCandidates().getObject(0) != null) {
				allPartiesVote.getItems().add(String.valueOf(parties.get(i).getPartyName()));
			}
		}
		allPartiesVote.setVisible(false);

		vbCenter.getChildren().addAll(lblWouldLikeToVote, hbYesNoForVoting, lblInProtectiveSuite,
				hbYesNoForInProtectiveSuite, lblChooseOneParty, allPartiesVote, btDoneVoting);
		vbCenter.setAlignment(Pos.TOP_CENTER);

	}

	public String getValueOfPartiesCb() {
		return allPartiesVote.getValue();
	}

	public void addEventToDoneVotingButton(EventHandler<ActionEvent> event) {
		btDoneVoting.setOnAction(event);
	}

	public void setPartiesComboBoxDisable(boolean condition) {
		allPartiesVote.setDisable(condition);
	}

	public void setPartiesComboBoxVisible(boolean condition) {
		allPartiesVote.setVisible(condition);
		lblChooseOneParty.setVisible(condition);
	}

	public void setPartiesComboBox(boolean condition) {
		allPartiesVote.setValue(null);
	}

	public void setIfInProtectiveSuitsVisible(boolean set) {
		rbNotInProtectiveSuit.setVisible(set);
		rbYesInProtectiveSuit.setVisible(set);
		lblInProtectiveSuite.setVisible(set);
		rbYesInProtectiveSuit.setDisable(!set);
	}

	public void addChangeListenerToTgVote(ChangeListener<Toggle> chl) {
		tgVote.selectedToggleProperty().addListener(chl);
	}

	public int getSelectedIfVote() {
		if (rbVoteYes.isSelected())
			return 0;
		else if (rbVoteNo.isSelected())
			return 1;
		else
			return -1;
	}

	public void addChangeListenerToTgInProtectiveSuit(ChangeListener<Toggle> chl) {
		tgInProtectiveSuit.selectedToggleProperty().addListener(chl);
	}

	public int getSelectedIfInProtectiveSuite() {
		if (rbYesInProtectiveSuit.isSelected())
			return 0;
		else if (rbNotInProtectiveSuit.isSelected())
			return 1;
		else
			return -1;
	}

	public void setSelectedIfInProtectiveSuite(boolean condition) {
		cbCheckIfInProtectiveSuite.setSelected(condition);
	}

	public boolean getIfInProtectiveSuitsDisable() {
		return rbYesInProtectiveSuit.isDisable();
	}

	public void setIfInProtectiveSuitsDisable(boolean condition) {
		rbYesInProtectiveSuit.setDisable(condition);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void printResultsForEachBallot() {
		if (vbBallotResults.getChildren().size() == 3) {
			vbBallotResults.getChildren().remove(2);
		}

		resultsForBallotBox = new Text(getChosenBallotResults());
		vbBallotResults.getChildren().add(resultsForBallotBox);
	}

	@Override
	public void printResultsForEachParty() {
		if (vbPartyResults.getChildren().size() == 3) {
			vbPartyResults.getChildren().remove(2);
		}

		resultsForParty = new Text(getChosenPartyResults());
		vbPartyResults.getChildren().add(resultsForParty);
	}

	public void createShowResultsWindow(ArrayList<Party> parties, ArrayList<Ballot<?>> ballotBox) {
		vbCenter.getChildren().clear();

		BorderPane bp1 = new BorderPane();

		allPartiesForResults = new ComboBox<>();
		for (int i = 0; i < parties.size(); i++) {
			if (!parties.get(i).getCandidates().isEmpty()) {
				allPartiesForResults.getItems().add(String.valueOf(parties.get(i).getPartyName()));
			}
		}

		Text txt1 = new Text("Parties list: ");

		hbPartiesResults = new HBox();

		hbPartiesResults.getChildren().addAll(txt1, allPartiesForResults);

		hbPartiesResults.setAlignment(Pos.TOP_CENTER);

		vbPartyResults = new VBox();

		vbPartyResults.getChildren().addAll(hbPartiesResults, btShowPartyResults);

		vbPartyResults.setAlignment(Pos.TOP_CENTER);

		vbPartyResults.setSpacing(10);

		bp1.setTop(vbPartyResults);

		BorderPane bp2 = new BorderPane();

		allBallotBoxesForResults = new ComboBox<>();
		for (int i = 0; i < ballotBox.size(); i++) {
			if (!ballotBox.get(i).getVotersList().isEmpty())
				allBallotBoxesForResults.getItems().add(String.valueOf(ballotBox.get(i).getSerialNumber()));
		}

		Text txt2 = new Text("Ballot boxes list: ");

		hbBallotBoxResults = new HBox();

		hbBallotBoxResults.getChildren().addAll(txt2, allBallotBoxesForResults);

		hbBallotBoxResults.setAlignment(Pos.TOP_CENTER);

		vbBallotResults = new VBox();

		vbBallotResults.getChildren().addAll(hbBallotBoxResults, btShowBallotResults);

		vbBallotResults.setAlignment(Pos.TOP_CENTER);

		vbBallotResults.setSpacing(10);

		bp2.setTop(vbBallotResults);

		HBox allTogether = new HBox();

		allTogether.getChildren().addAll(bp1, bp2);

		allTogether.setSpacing(10);

		vbCenter.getChildren().addAll(allTogether);

		vbCenter.setAlignment(Pos.TOP_CENTER);
	}

	public String getChosenBallotBoxForResults() {
		return allBallotBoxesForResults.getValue();
	}

	public String getChosenPartyForResults() {
		return allPartiesForResults.getValue();
	}

	public void addEventToShowPartyResultsButton(EventHandler<ActionEvent> event) {
		btShowPartyResults.setOnAction(event);
	}

	public void addEventToShowBallotResultsButton(EventHandler<ActionEvent> event) {
		btShowBallotResults.setOnAction(event);
	}

	public void setChosenBallotResults(StringBuffer results) {
		chosenBallotResults = results;
	}

	public String getChosenBallotResults() {
		return chosenBallotResults.toString();
	}

	public void setChosenPartyResults(StringBuffer results) {
		chosenPartyResults = results;
	}

	public String getChosenPartyResults() {
		return chosenPartyResults.toString();
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void endingElections() {
		vbCenter.getChildren().clear();
		hbSaveData = new HBox();
		hbSaveData.getChildren().addAll(btSaveData, btDoNotSaveData);
		hbSaveData.setAlignment(Pos.CENTER);
		vbCenter.getChildren().addAll(lblSavingData, hbSaveData);
	}

	public void addEventToSaveData(EventHandler<ActionEvent> event) {
		btSaveData.setOnAction(event);
	}

	public void addEventToNotSaveData(EventHandler<ActionEvent> event) {
		btDoNotSaveData.setOnAction(event);
	}
	public void addEventToExit(EventHandler<ActionEvent> event) {
		btExit.setOnAction(event);
	}

	public void exitJavafxWindow() {
		showMessage("ByeBye");
		globalStage.close();
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
