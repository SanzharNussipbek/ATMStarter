package ATMSS.TouchDisplayHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


/**
 * Touch Display Emulator for the Display hardware of ATM class
 * Extends TouchDisplayHandler class
 */
public class TouchDisplayEmulator extends TouchDisplayHandler {
	/**
	 * Fixed variables for display proportions
	 */
	private final int WIDTH = 680;
    private final int HEIGHT = 520;
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
    private TouchDisplayEmulatorController touchDisplayEmulatorController;


	/**
	 * Constructor of the class
	 * @param id ID of the current thread
	 * @param atmssStarter ATMSSStarter instance
	 * @throws Exception Throws Exception
	 */
    public TouchDisplayEmulator(String id, ATMSSStarter atmssStarter) throws Exception {
		super(id, atmssStarter);
		this.atmssStarter = atmssStarter;
		this.id = id;
    } // TouchDisplayEmulator


	/**
	 * Function to start the emulator
	 * @throws Exception Throws Exception
	 */
    public void start() throws Exception {
		Parent root;
		myStage = new Stage();
		FXMLLoader loader = new FXMLLoader();
		String fxmlName = "TouchDisplayEmulator.fxml";
		loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlName));
		root = loader.load();
		touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
		touchDisplayEmulatorController.initialize(id, atmssStarter, log, this);
		myStage.initStyle(StageStyle.DECORATED);
		myStage.setScene(new Scene(root, WIDTH, HEIGHT));
		myStage.setTitle("Touch Display");
		myStage.setResizable(false);
		myStage.setOnCloseRequest((WindowEvent event) -> {
			atmssStarter.stopApp();
			Platform.exit();
		});
		myStage.show();
    } // TouchDisplayEmulator


	/**
	 * Handle display update
	 * @param displayName Display name
	 */
	protected void handleUpdateDisplay(String displayName) {
		log.info(id + ": update display -- " +displayName);

		switch (displayName) {
			case "Incorrect Pin":
				handleIncorrectPin();
				break;

			case "Incorrect Admin Password":
				handleIncorrectAdminPassword();
				break;

			case "ChangePin":
				handleChangePin();
				break;

			case "Welcome":
			case "Pin":
			case "BlankScreen":
			case "MainMenu":
			case "AccountList":
			case "AmountInput":
			case "AmountList":
			case "Balance":
			case "AccountInput":
			case "InvalidAccount":
			case "RemoveCard":
			case "TakeOutCash":
			case "AnotherService":
			case "ReceiptChoice":
			case "InsertCash":
			case "WithdrawSuccess":
			case "WithdrawError":
			case "DepositSuccess":
			case "TransactionSuccess":
			case "TransactionError":
			case "TransferError":
			case "AdminPassword":
			case "AdminMenu":
			case "ChangePinSuccess":
				String filename = "TouchDisplay" + displayName + ".fxml";
				reloadStage(filename);
				break;

			default:
				log.severe(id + ": update display with unknown display type -- " + displayName);
				break;
		}
    } // handleUpdateDisplay


	/**
	 * Reload the display
	 * @param fxmlFName File Name of the new display screen
	 */
	private void reloadStage(String fxmlFName) {
        TouchDisplayEmulator touchDisplayEmulator = this;

        Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					log.info(id + ": loading fxml: " + fxmlFName);

					Parent root;
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlFName));
					root = loader.load();
					touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
					touchDisplayEmulatorController.initialize(id, atmssStarter, log, touchDisplayEmulator);
					myStage.setScene(new Scene(root, WIDTH, HEIGHT));
				} catch (Exception e) {
					log.severe(id + ": failed to load " + fxmlFName);
					e.printStackTrace();
				}
			}
		});
    } // reloadStage


	/**
	 * Handle the render of change pin screen
	 */
	protected void handleChangePin() {
		log.info(id + ": Change Pin");
		reloadStage("TouchDisplayPin.fxml");
		Platform.runLater(new Runnable() {
			public void run() {
				touchDisplayEmulatorController.handleNewPin();
			}
		});
	} // handleChangePin


	/**
	 * Handle rendering incorrect admin password text on screen
	 */
	protected void handleIncorrectAdminPassword() {
		log.info(id + ": Incorrect Admin Password");
		touchDisplayEmulatorController.handleIncorrectAdminPassword();
	} // handleIncorrectAdminPassword


	/**
	 * Handle display asterisks on password key input
	 * @param value Received character value
	 */
	protected void handleAdminPassword(String value) {
		log.info(id + ": admin password input");
		touchDisplayEmulatorController.handleSetAdminPassword(value);
	}


	/**
	 * Handle setting balance value on Balance screen
	 * @param balance Received Balance value
	 */
	protected void handleSetBalance(String balance) {
    	log.info(id + ": show balance [" + balance + "]");
		Platform.runLater(new Runnable() {
			public void run() {
				touchDisplayEmulatorController.setBalance(balance);
			}
		});
    } // handleSetBalance


	/**
	 * Handle rendering account list on AccountList screen
	 * @param accountList Received account list in one string separated by slash character
	 */
	protected void handleAccountList(String accountList) {
    	log.info(id + ": show accounts list [" + accountList + "]");
		Platform.runLater(new Runnable() {
			public void run() {
    			touchDisplayEmulatorController.handleSetAccountList(accountList);
			}
		});
    } // handleAccountList


	/**
	 * Handle rendering cash received text on screen
	 */
	protected void handleCashReceived() {
    	log.info(id + ": cash received");
		touchDisplayEmulatorController.handleCashReceived();
    } // handleCashReceived


	/**
	 * Handle rendering a card number value
	 * @param inputValue Received value
	 */
	protected void handleCardInput(String inputValue) {
		log.info(id + ": card input");
		touchDisplayEmulatorController.handleCardInput(inputValue);
	} // handleCardInput


	/**
	 * Handle sending amount to the ATMSS
	 */
	protected void handleGetAmount() {
		log.info(id + ": send amount");
		touchDisplayEmulatorController.handleSendAmount();
	} // handleGetAmount


	/**
	 * Handle sending card number value to the ATMSS
	 */
	protected void handleGetCard() {
		log.info(id + ": send card number");
		touchDisplayEmulatorController.handleSendCard();
	} // handleGetCard


	/**
	 * Handle rendering PIN characters on screen one by one
	 */
	protected void handleAppendPinText() {
		log.info(id + ": update pin text");
		touchDisplayEmulatorController.appendCardPinText();
	} // handleAppendText


	/**
	 * Handle rendering amount character by character on screen
	 * @param value Received  amount character
	 */
	protected void handleAppendAmountText(String value) {
		log.info(id + ": update amount text");
		touchDisplayEmulatorController.appendAmountText(value);
	} // handleAppendAmountText


	/**
	 * Handle clearing PIN text on screen
	 */
	protected void handleClearPinText() {
		log.info(id + ": clear pin text");
		touchDisplayEmulatorController.clearCardPinText();
	} // handleClearPinText


	/**
	 * Handle setting text on screen to Incorrect Pin
	 */
	protected void handleIncorrectPin() {
		log.info(id + ": Incorrect Pin");
		touchDisplayEmulatorController.handleIncorrectPin();
	} // handleIncorrectPin


} // TouchDisplayEmulator
