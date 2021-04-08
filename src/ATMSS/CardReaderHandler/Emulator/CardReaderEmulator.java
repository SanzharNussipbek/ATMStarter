package ATMSS.CardReaderHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CardReaderHandler.CardReaderHandler;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


/**
 * Card Reader Emulator for the Card Reader hardware of ATM class
 * Extends CardReaderHandler class
 */
public class CardReaderEmulator extends CardReaderHandler {
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
    private CardReaderEmulatorController cardReaderEmulatorController;


	/**
	 * Constructor of the class
	 * @param id ID of the current thread
	 * @param atmssStarter ATMSSStarter instance
	 */
    public CardReaderEmulator(String id, ATMSSStarter atmssStarter) {
		super(id, atmssStarter);
		this.atmssStarter = atmssStarter;
		this.id = id;
    } // CardReaderEmulator


	/**
	 * Function to start the emulator
	 * @throws Exception Throws Exception
	 */
	public void start() throws Exception {
		Parent root;
		myStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		String fxmlName = "CardReaderEmulator.fxml";
		loader.setLocation(CardReaderEmulator.class.getResource(fxmlName));
		root = loader.load();
		cardReaderEmulatorController = (CardReaderEmulatorController) loader.getController();
		cardReaderEmulatorController.initialize(id, atmssStarter, log, this);
		myStage.initStyle(StageStyle.DECORATED);
		myStage.setScene(new Scene(root, 350, 470));
		myStage.setTitle("Card Reader");
		myStage.setResizable(false);
		myStage.setOnCloseRequest((WindowEvent event) -> {
			atmssStarter.stopApp();
			Platform.exit();
		});
		myStage.show();
    } // CardReaderEmulator


	/**
	 * Handle Insertion of the card to the card reader
	 */
	protected void handleCardInsert() {
		super.handleCardInsert();
		cardReaderEmulatorController.appendTextArea("Card Inserted");
		cardReaderEmulatorController.updateCardStatus("Card Inserted");
    } // handleCardInsert


	/**
	 * Handle Ejection of the card from the card reader
	 */
    protected void handleCardEject() {
		super.handleCardEject();
		cardReaderEmulatorController.appendTextArea("Card Ejected");
		cardReaderEmulatorController.updateCardStatus("Card Ejected");
    } // handleCardEject


	/**
	 * Handle Removal of the card from the card reader
	 */
    protected void handleCardRemove() {
		super.handleCardRemove();
		cardReaderEmulatorController.appendTextArea("Card Removed");
		cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
    } // handleCardRemove
} // CardReaderEmulator
