package ATMSS.ATMSS;

import ATMSS.User.User;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static ATMSS.ATMSSStarter.ADMIN_PASSWORD;


/**
 * ATM Software System Class
 * Binds all emulators together and is responsible for the operation of whole system
 */
public class ATMSS extends AppThread {
	/**
	 * Time period after which polling message will be send
	 */
    private final int pollingTime;

	/**
	 * Message Boxes for the emulators
	 */
	private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
    private MBox collectorMBox;
    private MBox dispenserMBox;
    private MBox printerMBox;
    private MBox buzzerMBox;
    private MBox bamsMBox;

	/**
	 * User instance to save card and account information of the current session
	 */
	private User user;

	/**
	 * Enum for the state of the system
	 * Is being changed all over the class
	 */
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

	/**
	 * Enum for the operation state of the system
	 * Is being changed all over the class
	 */
	private enum Operation {
    	WITHDRAW,
		DEPOSIT,
		TRANSFER,
		BALANCE,
		NONE,
	}

	/**
	 * State enum instance
	 */
	private State state;

	/**
	 * Operation enum instance
	 */
	private Operation operation;

	/**
	 * Variable buffers to save intermediate information
	 */
	private String transferAcc;
	private String amount;
	private String adminPassword;
	private String oldPin;
	private int authTries;
	private int adminTries;

	private boolean cardReaderPollAck;
	private boolean buzzerPollAck;
	private boolean collectorPollAck;
	private boolean dispenserPollAck;
	private boolean printerPollAck;
	private boolean keypadPollAck;
	private boolean touchDisplayPollAck;
	private boolean bamsPollAck;


	/**
	 * ATM-SS Constructor
	 * @param id Id of the running thread
	 * @param appKickstarter AppKickstarter instance
	 * @throws Exception Throws Exception
	 */
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
		super(id, appKickstarter);
		pollingTime = Integer.parseInt(appKickstarter.getProperty("ATMSS.PollingTime"));
    } // ATMSS


	/**
	 * Initialize global variables
	 */
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

		setAllPollAcks(true);

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


	/**
	 * Initialize Welcome screen
	 */
	private void initWelcomeScreen() {
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
	} // initWelcomeScreen


	/**
	 * Run ATM System Software
	 * Analyzes incoming messages and executes function according to the message type and sender
	 */
	public void run() {
		init();

		for (boolean quit = false; !quit;) {
			Msg msg = mbox.receive();
			String details = msg.getDetails();
			String sender = msg.getSender();

			log.fine(id + ": message received: [" + msg + "].");

			switch (msg.getType()) {
				case TimesUp:
					handleTimesUp(details);
					break;

				case PollAck:
					handlePollAck(sender, details);
					break;

				case Terminate:
					quit = true;
					break;

				case Shutdown:
					handleShutdownReply(sender, details);
					break;

				case Reset:
					handleResetReply(sender, details);
					break;

				case Cancel:
				case CR_CardRemoved:
					handleCancel();
					break;

				case CR_CardInserted:
					handleCardInserted(details);
					break;

				case TD_MouseClicked:
					processMouseClicked(details);
					break;

				case TD_SendAccount:
					handleReceiveTransferAcc(details);
					break;

				case TD_AnotherService:
					handleAnotherService(details);
					break;

				case KP_KeyPressed:
					processKeyPressed(details);
					break;

				case BZ_Play:
					buzz(details);
					break;

				case PR_Receipt:
					handleReceipt(details);
					break;

				case DP_TakeOutCash:
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "WithdrawSuccess"));
					break;

				case MainMenuItem:
					handleMainMenuItemClick(details);
					break;

				case AccountItem:
					handleAccountClick(details);
					break;

				case AmountInput:
					handleAmountInput(details);
					break;

				case AmountList:
					handleAmountListItemClick(details);
					break;

				case BAMS_Login:
					handleAuth(details);
					break;

				case BAMS_Accounts:
					handleAccountList(details);
					break;

				case BAMS_Enquiry:
					handleShowBalance(details);
					break;

				case BAMS_Withdraw:
					handleWithdrawBAMS(details);
					break;

				case BAMS_Deposit:
					handleDepositBAMS(details);
					break;

				case BAMS_Transfer:
					handleTransferBAMS(details);
					break;

				case BAMS_ChangePin:
					handleChangePinBAMS(details);
					break;

				case BAMS_Logout:
					handleLogoutBAMS(details);
					break;

				case ADMIN_MenuItem:
					handleCommand(details);
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


	/**
	 * Handle Admin button click on Keypad
	 * Update state to ADMIN and change the display to password input
	 */
	private void handleAdminClick() {
		if (state != State.WELCOME) return;
		updateState(State.ADMIN);
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AdminPassword"));
	}


	/**
	 * Handle Password input for Admin
	 * If password is not correct, increment admintries and update display
	 * Otherwise, login the admin and change display to Admin Menu
	 */
	private void handleAdminPasswordEnter() {
		adminTries++;
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


	/**
	 * Handle click of a menu item in Main Menu screen
	 * @param clickedMenuItem Clicked Menu Item name
	 */
	private void handleMainMenuItemClick(String clickedMenuItem) {
		log.info(id + ": Main Menu Item clicked [" + clickedMenuItem + "]");

		switch (clickedMenuItem) {
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
				log.info(id + ": unknown menu item " + clickedMenuItem);
				break;
		}
	} // handleMainMenuItemClick


	/**
	 * Handle selection of Another Service in AnotherService display
	 * @param choice Choice name
	 */
	private void handleAnotherService(String choice) {
		if (choice.equals("YES")) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
			return;
		}
		handleCancel();
	} // handleAnotherService


	/**
	 * Handle amount item click in AmountList screen
	 * @param selectedAmount Selected Amount
	 */
	private void handleAmountListItemClick(String selectedAmount) {
		if (selectedAmount.equals("Other")) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AmountInput"));
			updateState(State.AMOUNT_INPUT);
		}
		else if (operation == Operation.WITHDRAW) {
			handleWithdrawAmount(selectedAmount);
		}
		else if (operation == Operation.TRANSFER) {
			handleTransfer(selectedAmount);
		}
	}// handleAmountListItemClick


	/**
	 * Handle user's input for amount
	 * @param amount Received Amount value
	 */
	private void handleAmountInput(String amount) {
    	if (operation == Operation.WITHDRAW) {
    		handleWithdrawAmount(amount);
		}
    	else if (operation == Operation.TRANSFER) {
    		handleTransfer(amount);
		}
		else if (operation == Operation.DEPOSIT) {
			handleDeposit(amount);
		}
	} // handleAmountInput


	/**
	 * Handle the result of BAMS Logout function
	 * @param result The result of the logout function of BAMS Emulator
	 */
	private void handleLogoutBAMS(String result) {
		if (result.equals("succ")) {
			handleLogout();
		}
		else if (result.equals("ERROR")) {
			log.info(id + ": error while logging out");
		}
	} // handleLogoutBAMS


	/**
	 * Update Display to New Pin Input due to Change Pin functionality
	 */
	private void showChangePinDisplay() {
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "ChangePin"));
		updateState(State.CHANGEPIN);
		oldPin = user.getPin();
		user.setPin(null);
	}


	/**
	 * Handle the result of BAMS Change Pin function
	 * @param result The result of changing pin from BAMS Emulator
	 */
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


	/**
	 * Handle rendering Balance screen
	 * @param balance The balance value received from BAMS Emulator
	 */
	private void handleShowBalance(String balance) {
		amount = balance;
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Balance"));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SendBalance, balance));
		updateOperation(Operation.BALANCE);
	} // handleShowBalance


	/**
	 * Handle sending deposit amount to the BAMS
	 * @param depositAmount The deposit amount received from Collector Emulator
	 */
	private void handleDeposit(String depositAmount) {
		log.info(id + ": cash deposit received HKD$" + amount);
		amount = depositAmount;
		collectorMBox.send(new Msg(id, mbox, Msg.Type.Reset, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.CL_CashReceived, ""));
		String msgDetail = user.getCardNum() + "/" + user.getCurrentAcc() + "/" + user.getCredential() + "/" + amount;
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Deposit, msgDetail));
	} // handleDeposit


	/**
	 * Handle the result of BAMS Deposit function
	 * @param result The result of depositing the cash from BAMS Emulator
	 */
	private void handleDepositBAMS(String result) {
    	if (result.equals("NOK")) {
			log.info(id + ": invalid amount for deposit");
			return;
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "DepositSuccess"));
	} // handleDepositBAMS


	/**
	 * Handle transfer functionality using transfer amount
	 * @param transferAmount Transfer amount received from display input
	 */
	private void handleTransfer(String transferAmount) {
    	amount = transferAmount;
		log.info(id + ": TRANSFER HKD$" + amount + " FROM [" + user.getCurrentAcc() + "] TO [" + transferAcc + "]");
		String msgDetail = user.getCardNum() + "/" + user.getCredential() + "/" + user.getCurrentAcc() + "/" + transferAcc + "/" + amount;
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Transfer, msgDetail));
	} // handleTransfer


	/**
	 * Handle the result of sending transfer query to BAMS Emulator
	 * @param result The result received from BAMS Emulator
	 */
	private void handleTransferBAMS(String result) {
		if (result.equals("NOK")) {
			log.info(id + ": invalid transfer");
			cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "TransferError"));
			return;
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "TransferSuccess"));
	} // handleTransferBAMS


	/**
	 * Receive the account where an amount is to be trasnferred
	 * Check for existing of the account in user's accounts
	 * Save to the transferAccBuffer
	 * @param accountNo The account number received from the user
	 */
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


	/**
	 * Handle the withdraw functionality
	 * Send the query to the BAMS Emulator
	 * @param withdrawAmount The amount received from the display
	 */
	private void handleWithdrawAmount(String withdrawAmount) {
    	amount = withdrawAmount;
		log.info(id + ": withdraw amount HKD$" + withdrawAmount);
		String msgDetail = user.getCardNum() + "/" + user.getCurrentAcc() + "/" + user.getCredential() + "/" + withdrawAmount;
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Withdraw, msgDetail));
	} // handleWithdrawAmount


	/**
	 * Handle the result of the withdraw from BAMS Emulator
	 * Update display according to the result
	 * @param result The result of the withdrawing from BAMS Emulator
	 */
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


	/**
	 * Handle the received account list of the user
	 * Update display and show the accounts
	 * @param accountList The result received from BAMS Emulator
	 */
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


	/**
	 * Handle the click of an account on the AccountList screen
	 * Save the account in the user instance
	 * Update display to the main menu screen
	 * @param accIndex The index of the clicked account (e.g. 0, 1, 2)
	 */
	private void handleAccountClick(String accIndex) {
		log.info(id + ": Account chosen: [" + accIndex + "]");
		int index = Integer.parseInt(accIndex);
		user.setCurrentAcc(user.getAccounts()[index]);
		log.info(id + ": Current Account: [" + user.getCurrentAcc() + "]");
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
		updateState(State.MAIN_MENU);
	} // handleAccountClick


	/**
	 * Handle the card number inserted in the card reader
	 * Save the card number in the user instance
	 * Update the display to prompt for PIN
	 * @param cardNum The card number received from CardReader Emulator
	 */
	private void handleCardInserted(String cardNum) {
		log.info("CardInserted: " + cardNum);
		buzz("beep");
		authTries = 0;
		user.setCardNum(cardNum);
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Pin"));
		updateState(State.PIN);
	} // handleCardInserted


	/**
	 * Handle single PIN number entered by the user on keypad
	 * @param pinNum Single PIN number from keypad
	 */
	private void handleSetAccountPin(String pinNum) {
		if (user.getPin() == null) {
			user.setPin(pinNum);
			return;
		}
		if (user.getPin().length() < 6) {
			user.setPin(user.getPin() + pinNum);
		}
	} // handleCardInserted


	/**
	 * Handle user authorization and the result of the BAMS Login
	 * Check the credential
	 * @param credential The result of login from BAMS Emulator
	 */
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


	/**
	 * Handle Cancel command
	 * Is used when user pressed Cancel button on keypad
	 * Or across the class to logout the user and end the session
	 */
	private void handleCancel() {
		buzz("beep");
		if (state == State.ADMIN || state == State.ADMIN_MENU) {
			handleLogout();
			return;
		}
		String details = user.getCardNum() + "/" + user.getCredential();
		bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Logout, details));
	} // handleCancel


	/**
	 * Handle Enter command pressed by the user on keypad
	 */
	private void handleEnter() {
		if (state == State.PIN || state == State.INCORRECT_PIN) {
			bamsMBox.send(new Msg(id, mbox, Msg.Type.BAMS_Login, user.getCardNum() + "/" + user.getPin()));
		}
		if (state == State.AMOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_GetAmount, ""));
		}
		if (state == State.ACCOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_GetAccount, ""));
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


	/**
	 * Handle Erase command pressed by the user on keypad
	 */
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


	/**
	 * Handle the number key pressed on number values on keypad
	 * @param numkey Received NumKey
	 */
	private void handleNumkeyPress(String numkey) {
    	if (numkey.compareToIgnoreCase("00") == 0 || numkey.compareToIgnoreCase(".") == 0) return;

    	if (state == State.WELCOME || state == State.MAIN_MENU) return;

    	if (state == State.PIN || state == State.INCORRECT_PIN || state == State.CHANGEPIN) {
			handleSetAccountPin(numkey);
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendPinText, ""));
		}
    	if (state == State.AMOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendAmountText, numkey));
		}
    	if (state == State.ACCOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AccountInput, numkey));
		}
    	if (state == State.ADMIN) {
			adminPassword += numkey;
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.ADMIN_pwd, ""));
		}
	} // handleNumkeyPress


	/**
	 * Handle single key pressed on keypad
	 * @param key Received key name from KeyPad Emulator
	 */
	private void processKeyPressed(String key) {
		log.info("KeyPressed: " + key);
        if (key.compareToIgnoreCase("Cancel") == 0) {
        	handleCancel();
		} else if (key.compareToIgnoreCase("Erase") == 0) {
			handleErase();
		} else if (key.compareToIgnoreCase("Admin") == 0) {
			handleAdminClick();
		} else if (key.compareToIgnoreCase("Enter") == 0) {
			handleEnter();
		} else {
			handleNumkeyPress(key);
		}
    } // processKeyPressed


	/**
	 * Generate a receipt of the operation to print
	 * @return Return the receipt text
	 */
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


	/**
	 * Handle printing receipt
	 * Analyzes the choice entered by the user: YES or NO
	 * @param choice Choice name received from the TouchDisplay Emulator
	 */
	private void handleReceipt(String choice) {
		if (choice.equals("YES")) {
			log.info(id + ": print receipt");
			String receipt = generateReceipt();
			printerMBox.send(new Msg(id, mbox, Msg.Type.PR_Print, receipt));
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AnotherService"));
	} // handleReceipt


	/**
	 * Handle logging out of the user
	 * Reset all buffers, variables and states/operations
	 */
	private void handleLogout() {
		setAllPollAcks(true);
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
		}
		if (state != State.ADMIN_MENU && state != State.ADMIN){
			cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
		}
		if (state == State.PIN || state == State.INCORRECT_PIN) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
		}
		updateState(State.WELCOME);
		updateOperation(Operation.NONE);
	}


	/**
	 * Handle admin command chosen by the user on Admin Menu
	 * @param commandName Command name selected by the user
	 */
	private void handleCommand(String commandName) {
		Msg.Type command = commandName.equals("SHUTDOWN") ? Msg.Type.Shutdown : Msg.Type.Reset;
		cardReaderMBox.send(new Msg(id, mbox, command, ""));
		keypadMBox.send(new Msg(id, mbox, command, ""));
		touchDisplayMBox.send(new Msg(id, mbox, command, ""));
		collectorMBox.send(new Msg(id, mbox, command, ""));
		dispenserMBox.send(new Msg(id, mbox, command, ""));
		printerMBox.send(new Msg(id, mbox, command, ""));
		buzzerMBox.send(new Msg(id, mbox, command, ""));
		bamsMBox.send(new Msg(id, mbox, command, ""));
	}


	/**
	 * Handle the result of the SHUTDOWN command received by single emulator
	 * @param sender Sender of the reply
	 * @param result Result of the shutdown
	 */
	private void handleShutdownReply(String sender, String result) {
		log.info(id + ": Shutdown result: [" + result + " | " + sender + "]");
	}


	/**
	 * Handle the result of the RESET command received by single emulator
	 * @param sender Sender of the reply
	 * @param result Result of the shutdown
	 */
	private void handleResetReply(String sender, String result) {
		log.info(id + ": Reset result: [" + result + " | " + sender + "]");
	}


	/**
	 * Handle polling message sending when polling time period comes up
	 * @param details  The details to be printed
	 */
	private void handleTimesUp(String details) {
		Timer.setTimer(id, mbox, pollingTime);
		log.info("Poll: " + details);

		if (!cardReaderPollAck || !touchDisplayPollAck
				|| !collectorPollAck || !printerPollAck
				|| !dispenserPollAck || !keypadPollAck
				|| !bamsPollAck || !buzzerPollAck) {
			log.info(id + ": Poll Ack not received");
			setOutOfService();
			return;
		}

		setAllPollAcks(false);

		cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		collectorMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		dispenserMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		printerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		buzzerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		bamsMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
	} // handleTimesUp


	/**
	 * Handle Poll Acknowledgement message
	 * @param sender Message sender
	 * @param details Message details
	 */
	private void handlePollAck(String sender, String details) {
		log.info("PollAck: " + details);
		if (details.equals(sender + " is broken!")) {
			setOutOfService();
			return;
		}
		switch (sender) {
			case "CardReaderHandler":
				cardReaderPollAck = true;
				break;

			case "TouchDisplayHandler":
				touchDisplayPollAck = true;
				break;

			case "DispenserHandler":
				dispenserPollAck = true;
				break;

			case "PrinterHandler":
				printerPollAck = true;
				break;

			case "BuzzerHandler":
				buzzerPollAck = true;
				break;

			case "BAMSHandler":
				bamsPollAck = true;
				break;

			case "CollectorHandler":
				collectorPollAck = true;
				break;

			case "KeypadHandler":
				keypadPollAck = true;
				break;
		}
	}


	/**
	 * Miscellaneous function to set the ATM to out of service
	 */
	private void setOutOfService() {
		log.info(id + ": Out of service");
		handleCancel();
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, "OutOfService"));
	}


	/**
	 * Update the current state of the system
	 * @param newState New state value
	 */
	private void updateState(State newState) {
		state = newState;
	} // updateOperation


	/**
	 * Update the current operation of the system
	 * @param newOperation New operation value
	 */
	private void updateOperation(Operation newOperation) {
		operation = newOperation;
	} // updateOperation


	/**
	 * Miscellaneous function to set all poll ack values at once
	 * @param value Value to be set with
	 */
	private void setAllPollAcks(Boolean value) {
		cardReaderPollAck = value;
		buzzerPollAck = value;
		collectorPollAck = value;
		dispenserPollAck = value;
		printerPollAck = value;
		keypadPollAck = value;
		touchDisplayPollAck = true;
		bamsPollAck = value;
	}


	/**
	 * Handle buzzing of the system
	 * @param variant The variant of the sound: Button sound or Beep sound
	 */
	private void buzz(String variant) {
		buzzerMBox.send(new Msg(id, mbox, Msg.Type.BZ_Play, variant));
	} // buzz


	/**
	 * Handle the coordinates of the mouse click on the display
	 * @param clickDetais The coordinates
	 */
	private void processMouseClicked(String clickDetais) {
		log.info("MouseCLicked: " + clickDetais);
	} // processMouseClicked


	/**
	 * Miscellaneous function used to get the current timestamp
	 * Used for receipt generation
	 * @return Returns current formatted time
	 */
	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	} // now
} // CardReaderHandler
