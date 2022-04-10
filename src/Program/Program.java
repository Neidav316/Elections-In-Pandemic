package Program;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import Controller.Controller;
//import Model.Elections;
import Model.Model;
//import View.ConsuleUI;
//import View.ManageUI;
import View.View;
import javafx.application.Application;
import javafx.stage.Stage;

@SuppressWarnings("serial")
public class Program extends Application implements Serializable {

	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		launch(args);

	}

	@SuppressWarnings("unused")
	@Override
	public void start(Stage args) throws Exception {
		View theView = new View(args);
		Model theModel = new Model();
		Controller theController = new Controller(theModel, theView);
	}

}
