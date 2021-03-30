package ATMSS.TouchDisplayHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;
import AppKickstarter.misc.Msg;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// TouchDisplayEmulator
public class TouchDisplayEmulator extends TouchDisplayHandler {
    private final int WIDTH = 680;
    private final int HEIGHT = 520;
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private TouchDisplayEmulatorController touchDisplayEmulatorController;

    //------------------------------------------------------------
    // TouchDisplayEmulator
    public TouchDisplayEmulator(String id, ATMSSStarter atmssStarter) throws Exception {
		super(id, atmssStarter);
		this.atmssStarter = atmssStarter;
		this.id = id;
    } // TouchDisplayEmulator


    //------------------------------------------------------------
    // start
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


    //------------------------------------------------------------
    // handleUpdateDisplay
    protected void handleUpdateDisplay(Msg msg) {
		log.info(id + ": update display -- " + msg.getDetails());

		switch (msg.getDetails()) {
			case "Welcome":
				reloadStage("TouchDisplayWelcome.fxml");
				break;

			case "Pin":
				reloadStage("TouchDisplayPin.fxml");
				break;

			case "Incorrect Pin":
				handleIncorrectPin();
				break;

			case "BlankScreen":
				reloadStage("TouchDisplayEmulator.fxml");
				break;

			case "MainMenu":
				reloadStage("TouchDisplayMainMenu.fxml");
				break;

			case "AccountList":
				reloadStage("TouchDisplayAccountList.fxml");
				break;

			case "AmountInput":
				reloadStage("TouchDisplayAmountInput.fxml");
				break;

			case "Balance":
				handleShowBalance();
				break;

			case "AccountInput":
				reloadStage("TouchDisplayAccountInput.fxml");
				break;

			case "InvalidAccount":
				reloadStage("TouchDisplayInvalidAccount.fxml");
				break;

			case "RemoveCard":
				reloadStage("TouchDisplayRemoveCard.fxml");
				break;

			case "RemoveCash":
				reloadStage("TouchDisplayRemoveCash.fxml");
				break;

			case "AnotherService":
				reloadStage("TouchDisplayAnotherService.fxml");
				break;

			case "ReceiptChoice":
				reloadStage("TouchDisplayReceiptChoice.fxml");
				break;

			case "WithdrawAmount":
				reloadStage("TouchDisplayWithdrawAmountList.fxml");
				break;

			case "InsertCash":
				reloadStage("TouchDisplayInsertCash.fxml");
				break;

			case "WithdrawSuccess":
				reloadStage("TouchDisplayWithdrawSuccess.fxml");
				break;

			case "DepositSuccess":
				reloadStage("TouchDisplayDepositSuccess.fxml");
				break;

			case "TransactionSuccess":
				reloadStage("TouchDisplayTransactionSuccess.fxml");
				break;

			case "TransactionError":
				reloadStage("TouchDisplayTransactionError.fxml");
				break;

			default:
				log.severe(id + ": update display with unknown display type -- " + msg.getDetails());
				break;
		}
    } // handleUpdateDisplay


    //------------------------------------------------------------
    // reloadStage
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


	protected void handleShowBalance() {
    	reloadStage("TouchDisplayBalance.fxml");
		touchDisplayEmulatorController.enquireBalance();
	}


	//------------------------------------------------------------
	// handleSetBalance
	protected void handleSetBalance(String balance) {
    	log.info(id + ": show balance [" + balance + "]");
    	touchDisplayEmulatorController.setBalance(balance);
    } // handleSetBalance


	//------------------------------------------------------------
	// handleAccountList
	protected void handleAccountList(String accountList) {
    	log.info(id + ": show accounts list [" + accountList + "]");
    	touchDisplayEmulatorController.handleSetAccountList(accountList);
    } // handleAccountList


	//------------------------------------------------------------
	// handleCashReceived
	protected void handleCashReceived() {
    	log.info(id + ": cash received");
		touchDisplayEmulatorController.handleCashReceived();
    } // handleCashReceived


	//------------------------------------------------------------
	// handleCardInput
	protected void handleCardInput(Msg msg) {
		log.info(id + ": card input");
		touchDisplayEmulatorController.handleCardInput(msg.getDetails());
	} // handleCardInput


	//------------------------------------------------------------
	// handleGetAmount
	protected void handleGetAmount() {
		log.info(id + ": send amount");
		touchDisplayEmulatorController.handleSendAmount();
	} // handleGetAmount


	//------------------------------------------------------------
	// handleGetCard
	protected void handleGetCard() {
		log.info(id + ": send card number");
		touchDisplayEmulatorController.handleSendCard();
	} // handleGetCard


	//------------------------------------------------------------
	// handleAppendText
	protected void handleAppendPinText() {
		log.info(id + ": update pin text");
		touchDisplayEmulatorController.appendCardPinText();
	} // handleAppendText


	//------------------------------------------------------------
	// handleAppendAmountText
	protected void handleAppendAmountText(Msg msg) {
		log.info(id + ": update amount text");
		touchDisplayEmulatorController.appendAmountText(msg.getDetails());
	} // handleAppendAmountText


	//------------------------------------------------------------
	// handleClearPinText
	protected void handleClearPinText() {
		log.info(id + ": clear pin text");
		touchDisplayEmulatorController.clearCardPinText();
	} // handleClearPinText


	//------------------------------------------------------------
	// handleIncorrectPin
	protected void handleIncorrectPin() {
		log.info(id + ": Incorrect Pin");
		touchDisplayEmulatorController.handleIncorrectPin();
	} // handleIncorrectPin
} // TouchDisplayEmulator
