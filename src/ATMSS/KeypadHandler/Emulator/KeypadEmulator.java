package ATMSS.KeypadHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.KeypadHandler.KeypadHandler;
import AppKickstarter.misc.Msg;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


/**
 * Keypad Emulator for the Keypad hardware of ATM class
 * Extends KeypadHandler class
 */
public class KeypadEmulator extends KeypadHandler {
	/**
	 * Instance of the ATMSSStarter
	 */
	private ATMSSStarter atmssStarter;
	/**
	 * ID of the current thread
	 */
	private String id;
	/**
	 * Stage of the emulator
	 */
	private Stage myStage;
	/**
	 * Instance of the Emulator Controller
	 */
    private KeypadEmulatorController keypadEmulatorController;


	/**
	 * Constructor of the class
	 * @param id ID of the current thread
	 * @param atmssStarter ATMSSStarter instance
	 */
    public KeypadEmulator(String id, ATMSSStarter atmssStarter) {
		super(id, atmssStarter);
		this.atmssStarter = atmssStarter;
		this.id = id;
    } // KeypadEmulator


	/**
	 * Function to start the emulator
	 * @throws Exception Throws Exception
	 */
    public void start() throws Exception {
		Parent root;
		myStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		String fxmlName = "KeypadEmulator.fxml";
		loader.setLocation(KeypadEmulator.class.getResource(fxmlName));
		root = loader.load();
		keypadEmulatorController = (KeypadEmulatorController) loader.getController();
		keypadEmulatorController.initialize(id, atmssStarter, log, this);
		myStage.initStyle(StageStyle.DECORATED);
		myStage.setScene(new Scene(root, 340, 270));
		myStage.setTitle("KeypadHandler");
		myStage.setResizable(false);
		myStage.setOnCloseRequest((WindowEvent event) -> {
			atmssStarter.stopApp();
			Platform.exit();
		});
		myStage.show();
    } // KeypadEmulator


	/**
	 * Handle Poll Acknowledgement
	 */
	protected void handlePollAck() {
		String result = keypadEmulatorController.getPollAck();
		atmss.send(new Msg(id, mbox, Msg.Type.PollAck, id + result));
	} // handlePollAck
} // KeypadEmulator
