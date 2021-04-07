package ATMSS.ATMSS;

import ATMSS.User.User;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;


//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private final int pollingTime;
    private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
    private MBox collectorMBox;
    private MBox dispenserMBox;
    private MBox printerMBox;
    private MBox buzzerMBox;
    private MBox bamsMBox;
    private User user;
    private enum State {
    	WELCOME,
		PIN,
		INCORRECT_PIN,
		MAIN_MENU,
		AMOUNT_INPUT,
		ACCOUNT_INPUT,
		ACCOUNT_LIST,
		ADMIN,
		ADMIN_MENU,
		CHANGEPIN,
	}
	private enum Operation {
    	WITHDRAW,
		DEPOSIT,
		TRANSFER,
		BALANCE,
		NONE,
	}
	private State state;
	private Operation operation;
	private String transferAcc;
	private String amount;
	private String adminPassword;
	private String oldPin;
	private int authTries;
	private int adminTries;

    //------------------------------------------------------------
    // ATMSS
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
		super(id, appKickstarter);
		pollingTime = Integer.parseInt(appKickstarter.getProperty("ATMSS.PollingTime"));
    } // ATMSS


	//------------------------------------------------------------
	// init
	private void init() {
		Timer.setTimer(id, mbox, pollingTime);
		log.info(id + ": starting...");

		cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
		keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
		touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
		collectorMBox = appKickstarter.getThread("CollectorHandler").getMBox();
		dispenserMBox = appKickstarter.getThread("DispenserHandler").getMBox();
		printerMBox = appKickstarter.getThread("PrinterHandler").getMBox();
		buzzerMBox = appKickstarter.getThread("BuzzerHandler").getMBox();
		bamsMBox = appKickstarter.getThread("BAMSHandler").getMBox();

		user = new User();
		authTries = 0;
		adminTries = 0;
		transferAcc = "";
		amount = "";
		adminPassword = "";
		oldPin = "";
		updateState(State.WELCOME);
		updateOperation(Operation.NONE);

		initWelcomeScreen();
	} // init


	//------------------------------------------------------------
	// initWelcomeScreen
	private void initWelcomeScreen() {
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
	} // initWelcomeScreen


    //------------------------------------------------------------
    // run
    public void run() {
		init();

		for (boolean quit = false; !quit;) {
			Msg msg = mbox.receive();

			log.fine(id + ": message received: [" + msg + "].");

			switch (msg.getType()) {
				case TD_MouseClicked:
					processMouseClicked(msg);
					break;

				case KP_KeyPressed:
					processKeyPressed(msg);
					break;

				case CR_CardInserted:
					handleCardInserted(msg);
					break;

				case BZ_Play:
					buzz(msg.getDetails());
					break;

				case TimesUp:
					handleTimesUp(msg);
					break;

				case PollAck:
					log.info("PollAck: " + msg.getDetails());
					break;

				case Terminate:
					quit = true;
					break;

				case MainMenuItem:
					handleMainMenuItemClick(msg);
					break;

				case AccountItem:
					handleAccountClick(msg.getDetails());
					break;

				case Cancel:
				case CR_CardRemoved:
					handleCancel();
					break;

				case TD_SendCard:
					handleReceiveTransferAcc(msg.getDetails());
					break;

				case AmountInput:
					handleAmountInput(msg.getDetails());
					break;

				case AmountList:
					handleAmountListItemClick(msg.getDetails());
					break;

				case TD_GetBalance:
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SendBalance, "1000.0"));
					break;

				case PR_Receipt:
					handleReceipt(msg.getDetails());
					break;

				case TD_AnotherService:
					handleAnotherService(msg.getDetails());
					break;

				case DP_TakeOutCash:
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "WithdrawSuccess"));
					break;

				case BAMS_Login:
					handleAuth(msg.getDetails());
					break;

				case BAMS_Accounts:
					handleAccountList(msg.getDetails());
					break;

				case BAMS_Enquiry:
					handleShowBalance(msg.getDetails());
					break;

				case BAMS_Withdraw:
					handleWithdrawBAMS(msg.getDetails());
					break;

				case BAMS_Deposit:
					handleDepositBAMS(msg.getDetails());
					break;

				case BAMS_Transfer:
					handleTransferBAMS(msg.getDetails());
					break;

				case BAMS_ChangePin:
					handleChangePinBAMS(msg.getDetails());
					break;

				case BAMS_Logout:
					handleLogoutBAMS(msg.getDetails());
					break;

				case Shutdown:
					handleShutdownReply(msg.getSender(), msg.getDetails());
					break;

				case Reset:
					handleResetReply(msg.getSender(), msg.getDetails());
					break;

				case ADMIN_MenuItem:
					handleCommand(msg.getDetails());
					break;

				default:
					log.warning(id + ": unknown message type: [" + msg + "]");
					break;
			}
		}

		// declaring our departure
		appKickstarter.unregThread(this);
		log.info(id + ": terminating...");
    } // run


	private void handleAdmin() {
		if (state != State.WELCOME) return;
		updateState(State.ADMIN);
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AdminPassword"));
	}


	private void handleAdminPasswordEnter() {
		adminTries++;
		String ADMIN_PASSWORD = "0000";
		if (!adminPassword.equals(ADMIN_PASSWORD)) {
			adminPassword = "";
			if (adminTries >= 3) {
				adminTries = 0;
				handleCancel();
				return;
			}
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Incorrect Admin Password"));
			return;
		}
		adminTries = 0;
		adminPassword = "";
		updateState(State.ADMIN_MENU);
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AdminMenu"));
	}


	/*******************************************************************************************************************
	 *
	 * MAIN MENU
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleMainMenuItemClick
	private void handleMainMenuItemClick(Msg msg) {
		log.info(id + ": Main Menu Item clicked [" + msg.getDetails() + "]");

		switch (msg.getDetails()) {
			case "WITHDRAW":
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AmountList"));
				updateOperation(Operation.WITHDRAW);
				break;

			case "DEPOSIT":
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "InsertCash"));
				collectorMBox.send(new Msg(id, mbox, Msg.Type.TD_GetAmount, "InsertCash"));
				updateOperation(Operation.DEPOSIT);
				break;

			case "BALANCE":
				String msgDetail = user.getCardNum() + "/" + user.getCurrentAcc() + "/" + user.getCredential();
				bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Enquiry, msgDetail));
				break;

			case "TRANSFER":
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AccountInput"));
				updateOperation(Operation.TRANSFER);
				updateState(State.ACCOUNT_INPUT);
				break;

			case "CANCEL":
				handleCancel();
				break;

			case "CHANGE PIN":
				showChangePinDisplay();
				break;

			default:
				log.info(id + ": unknown menu item " + msg.getDetails());
				break;
		}
	} // handleMainMenuItemClick


	//------------------------------------------------------------
	// handleAnotherService
	private void handleAnotherService(String choice) {
		if (choice.equals("YES")) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
			return;
		}
		handleCancel();
	} // handleAnotherService


	/*******************************************************************************************************************
	 *
	 * AMOUNT INPUT
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleAmountListItemClick
	private void handleAmountListItemClick(String value) {
		if (value.equals("Other")) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AmountInput"));
			updateState(State.AMOUNT_INPUT);
		}
		if (operation == Operation.WITHDRAW) {
			handleWithdrawAmount(value);
		}
		if (operation == Operation.TRANSFER) {
			handleTransfer(value);
		}
	}// handleAmountListItemClick


	//------------------------------------------------------------
	// handleAmountInput
	private void handleAmountInput(String amount) {
    	if (operation == Operation.WITHDRAW) {
    		handleWithdrawAmount(amount);
		}
    	if (operation == Operation.TRANSFER) {
    		handleTransfer(amount);
		}
		if (operation == Operation.DEPOSIT) {
			handleDeposit(amount);
		}
	} // handleAmountInput


	/*******************************************************************************************************************
	 *
	 * LOGOUT
	 *
	 ******************************************************************************************************************/


	private void handleLogoutBAMS(String result) {
		if (result.equals("succ")) {
			handleLogout();
		}
		if (result.equals("ERROR")) {
			log.info(id + ": error while logging out");
		}
	}


	/*******************************************************************************************************************
	 *
	 * CHANGE PIN
	 *
	 ******************************************************************************************************************/


	private void showChangePinDisplay() {
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "ChangePin"));
		updateState(State.CHANGEPIN);
		oldPin = user.getPin();
		user.setPin(null);
	}


	private void handleChangePinBAMS(String result) {
		if (result.equals("succ")) {
			log.info(id + ": new pin successfully set [" + user.getPin() + "]");
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "ChangePinSuccess"));
		}
		if (result.equals("ERROR")) {
			log.info(id + ": error while changing pin");
			handleCancel();
		}
	}


	/*******************************************************************************************************************
	 *
	 * BALANCE
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleShowBalance
	private void handleShowBalance(String balance) {
		amount = balance;
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Balance"));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SendBalance, balance));
		updateOperation(Operation.BALANCE);
	} // handleShowBalance


	/*******************************************************************************************************************
	 *
	 * DEPOSIT
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleDeposit
	private void handleDeposit(String value) {
		log.info(id + ": cash deposit received HKD$" + amount);
		amount = value;
		collectorMBox.send(new Msg(id, mbox, Msg.Type.Reset, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.CL_CashReceived, ""));
		String msgDetail = user.getCardNum() + "/" + user.getCurrentAcc() + "/" + user.getCredential() + "/" + amount;
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Deposit, msgDetail));
	} // handleDeposit


	//------------------------------------------------------------
	// handleDepositBAMS
	private void handleDepositBAMS(String result) {
    	if (result.equals("NOK")) {
			log.info(id + ": invalid amount for deposit");
			return;
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "DepositSuccess"));
	} // handleDepositBAMS


	/*******************************************************************************************************************
	 *
	 * TRANSFER
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleTransfer
	private void handleTransfer(String value) {
    	amount = value;
		log.info(id + ": TRANSFER HKD$" + amount + " FROM [" + user.getCurrentAcc() + "] TO [" + transferAcc + "]");
		String msgDetail = user.getCardNum() + "/" + user.getCredential() + "/" + user.getCurrentAcc() + "/" + transferAcc + "/" + amount;
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Transfer, msgDetail));
	} // handleTransfer


	//------------------------------------------------------------
	// handleTransferBAMS
	private void handleTransferBAMS(String result) {
		if (result.equals("NOK")) {
			log.info(id + ": invalid transfer");
			cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "TransferError"));
			return;
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "TransactionSuccess"));
	} // handleTransferBAMS


	//------------------------------------------------------------
	// handleReceiveTransferCard
	private void handleReceiveTransferAcc(String accountNo) {
		log.info(id + ": received account number: " + accountNo);
		if (!user.hasAccount(accountNo)) {
			handleCancel();
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "InvalidAccount"));
			updateState(State.WELCOME);
			updateOperation(Operation.NONE);
			return;
		}
		transferAcc = accountNo;
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AmountList"));
		updateState(State.AMOUNT_INPUT);
	} // handleReceiveTransferCard


	/*******************************************************************************************************************
	 *
	 * WITHDRAW
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleWithdrawAmount
	private void handleWithdrawAmount(String value) {
    	amount = value;
		log.info(id + ": withdraw amount HKD$" + value);
		String msgDetail = user.getCardNum() + "/" + user.getCurrentAcc() + "/" + user.getCredential() + "/" + value;
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Withdraw, msgDetail));
	} // handleWithdrawAmount


	//------------------------------------------------------------
	// handleWithdrawBAMS
	private void handleWithdrawBAMS(String result) {
    	if (result.equals("NOK")) {
			cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "WithdrawError"));
			log.info(id + ": invalid amount for withdrawal");
			return;
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "TakeOutCash"));
		dispenserMBox.send(new Msg(id, mbox, Msg.Type.DP_DispenseCash, result));
	} // handleWithdrawBAMS


	/*******************************************************************************************************************
	 *
	 * ACCOUNT
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleAccountList
	private void handleAccountList(String accountList) {
		if (accountList.equals("CREDNOK")) {
			log.info(id + ": invalid credential");
			handleCancel();
			return;
		}
		updateState(State.ACCOUNT_LIST);
		String[] accounts = accountList.split("/");
		user.setAccounts(accounts);
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AccountList"));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AccountList, accountList));
	} // handleAccountList


	//------------------------------------------------------------
	// handleAccountClick
	private void handleAccountClick(String accIndex) {
		log.info(id + ": Account chosen: [" + accIndex + "]");
		int index = Integer.parseInt(accIndex);
		user.setCurrentAcc(user.getAccounts()[index]);
		log.info(id + ": Current Account: [" + user.getCurrentAcc() + "]");
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
		updateState(State.MAIN_MENU);
		log.info(id + ": " + user);
	} // handleAccountClick


	/*******************************************************************************************************************
	 *
	 * AUTHORIZATION
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleCardInserted
	private void handleCardInserted(Msg msg) {
		log.info("CardInserted: " + msg.getDetails());
		buzz("beep");
		authTries = 0;
		user.setCardNum(msg.getDetails());
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Pin"));
		updateState(State.PIN);
	} // handleCardInserted


	//------------------------------------------------------------
	// handleSetAccountPin
	private void handleSetAccountPin(String pinNum) {
		if (user.getPin() == null) {
			user.setPin(pinNum);
			return;
		}
		if (user.getPin().length() < 6) {
			user.setPin(user.getPin() + pinNum);
		}
	} // handleCardInserted


	//------------------------------------------------------------
	// handleAuth
	private void handleAuth(String credential) {
		buzz("beep");
		boolean validator = !credential.equals("NOK");
		if (!validator) {
			authTries++;
			log.info(id + ": Invalid PIN(try no. " + authTries + "):" + user.getPin());
			user.setPin(null);
			handleErase();
			if (authTries >= 3) {
				log.info(id + ": Three tries were expired!");
				handleCancel();
				return;
			}
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Incorrect Pin"));
			updateState(State.INCORRECT_PIN);
			return;
		}
		authTries = 0;
		user.setCredential(credential);
		log.info(id + ": Authorize account: " + user.getCardNum());
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Accounts, user.getCardNum() + "/" + credential));
	} // handleAuth


	/*******************************************************************************************************************
	 *
	 * KEYPAD
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// handleCancel
	private void handleCancel() {
		buzz("beep");
		String details = user.getCardNum() + "/" + user.getCredential();
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Logout, details));
	} // handleCancel


	// handleEnter
	private void handleEnter() {
		if (state == State.PIN || state == State.INCORRECT_PIN) {
			bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Login, user.getCardNum() + "/" + user.getPin()));
		}
		if (state == State.AMOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_GetAmount, ""));
		}
		if (state == State.ACCOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_GetCard, ""));
		}
		if (state == State.ADMIN) {
			handleAdminPasswordEnter();
		}
		if (state == State.CHANGEPIN) {
			log.info(id + ": old pin [" + oldPin + "], new pin [" + user.getPin() + "]");
			String details = user.getCardNum() + "/" + oldPin + "/" + user.getPin() + "/" + user.getCredential();
			bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_ChangePin, details));
		}
	} // handleEnter


	//------------------------------------------------------------
	// handleErase
	private void handleErase() {
    	if (state == State.PIN || state == State.INCORRECT_PIN || state == State.CHANGEPIN) {
    		user.setPin("");
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
		}

		if (state == State.ADMIN) {
			adminPassword = "";
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.ADMIN_pwd, "CLEAR"));
		}
	} // handleErase


	//------------------------------------------------------------
	// handleNumkeyPress
	private void handleNumkeyPress(Msg msg) {
    	if (msg.getDetails().compareToIgnoreCase("00") == 0 || msg.getDetails().compareToIgnoreCase(".") == 0) return;

    	if (state == State.WELCOME || state == State.MAIN_MENU) return;

    	if (state == State.PIN || state == State.INCORRECT_PIN || state == State.CHANGEPIN) {
			handleSetAccountPin(msg.getDetails());
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendPinText, ""));
		}
    	if (state == State.AMOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendAmountText, msg.getDetails()));
		}
    	if (state == State.ACCOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_CardInput, msg.getDetails()));
		}
    	if (state == State.ADMIN) {
			adminPassword += msg.getDetails();
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.ADMIN_pwd, ""));
		}
	} // handleNumkeyPress


    //------------------------------------------------------------
    // processKeyPressed
    private void processKeyPressed(Msg msg) {
		log.info("KeyPressed: " + msg.getDetails());
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
        	handleCancel();
		} else if (msg.getDetails().compareToIgnoreCase("Erase") == 0) {
			handleErase();
		} else if (msg.getDetails().compareToIgnoreCase("Admin") == 0) {
        	handleAdmin();
		} else if (msg.getDetails().compareToIgnoreCase("Enter") == 0) {
			handleEnter();
		} else {
			handleNumkeyPress(msg);
		}
    } // processKeyPressed


	/*******************************************************************************************************************
	 *
	 * PRINTER
	 *
	 ******************************************************************************************************************/


	//------------------------------------------------------------
	// generateReceipt
	private String generateReceipt() {
		String receipt = "\n============================\n\n";
		receipt += "                           RECEIPT\n";
		receipt += "\nOperation: " + operation + "\n";
		receipt += "\nDate&Time: " + now() + "\n";
		receipt += "\nCard Number: " + user.getCardNum() + "\n";
		receipt += "\nAccount Number: " + user.getCurrentAcc() + "\n";
		receipt += "\nAmount: HKD$" + amount + "\n";
		receipt += operation == Operation.TRANSFER ? "\nTo: " + transferAcc + "\n" : "";
		receipt += "\n============================\n";
		return receipt;
	} // generateReceipt


	//------------------------------------------------------------
	// handleReceipt
	private void handleReceipt(String choice) {
		if (choice.equals("YES")) {
			log.info(id + ": print receipt");
			String receipt = generateReceipt();
			printerMBox.send(new Msg(id, mbox, Msg.Type.PR_Print, receipt));
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AnotherService"));
	} // handleReceipt


	/*******************************************************************************************************************
	 *
	 * MISCELLANEOUS
	 *
	 ******************************************************************************************************************/


	private void handleLogout() {
		restart();
		user.reset();
		transferAcc = "";
		amount = "";
		authTries = 0;
		adminTries = 0;
		adminPassword = "";
		oldPin = "";
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
		if (state == State.ADMIN) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.ADMIN_pwd, "CLEAR"));
		} else {
			cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
		}
		if (state == State.PIN || state == State.INCORRECT_PIN) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
		}
	}


	private void handleCommand(String btn) {
		Msg.Type command = btn.equals("SHUTDOWN") ? Msg.Type.Shutdown : Msg.Type.Reset;
		cardReaderMBox.send(new Msg(id, mbox, command, ""));
		keypadMBox.send(new Msg(id, mbox, command, ""));
		touchDisplayMBox.send(new Msg(id, mbox, command, ""));
		collectorMBox.send(new Msg(id, mbox, command, ""));
		dispenserMBox.send(new Msg(id, mbox, command, ""));
		printerMBox.send(new Msg(id, mbox, command, ""));
		buzzerMBox.send(new Msg(id, mbox, command, ""));
		bamsMBox.send(new Msg(id, mbox, command, ""));
	}


	private void handleShutdownReply(String sender, String result) {
		log.info(id + ": Shutdown result: [" + result + " | " + sender + "]");
	}


	private void handleResetReply(String sender, String result) {
		log.info(id + ": Reset result: [" + result + " | " + sender + "]");
	}


	//------------------------------------------------------------
	// handleTimesUp
	private void handleTimesUp(Msg msg) {
		Timer.setTimer(id, mbox, pollingTime);
		log.info("Poll: " + msg.getDetails());
		cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		collectorMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		dispenserMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		printerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		buzzerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		bamsMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
	} // handleTimesUp


	//------------------------------------------------------------
	// updateOperation
	private void updateState(State newState) {
		state = newState;
	} // updateOperation


	//------------------------------------------------------------
	// updateOperation
	private void updateOperation(Operation newOperation) {
		operation = newOperation;
	} // updateOperation


	//------------------------------------------------------------
	// restart
	private void restart() {
		updateState(State.WELCOME);
		updateOperation(Operation.NONE);
	} // restart


	//------------------------------------------------------------
	// buzz
	private void buzz(String variant) {
		buzzerMBox.send(new Msg(id, mbox, Msg.Type.BZ_Play, variant));
	} // buzz


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
		log.info("MouseCLicked: " + msg.getDetails());
	} // processMouseClicked


	//------------------------------------------------------------
	// now
	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	} // now
} // CardReaderHandler
