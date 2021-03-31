package ATMSS.ATMSS;

import ATMSS.Account.Account;
import ATMSS.User.User;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.text.SimpleDateFormat;
import java.util.Calendar;


//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private int pollingTime;
    private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
    private MBox collectorMBox;
    private MBox dispenserMBox;
    private MBox printerMBox;
    private MBox buzzerMBox;
    private User user;
    private enum State {
    	WELCOME,
		PIN,
		INCORRECT_PIN,
		MAIN_MENU,
		AMOUNT_INPUT,
		ACCOUNT_INPUT,
		ACCOUNT_LIST,
	}
	private enum Operation {
    	WITHDRAW,
		DEPOSIT,
		TRANSFER,
		BALANCE,
		NONE,
	}
	private boolean isLoggedIn;
	private State state;
	private Operation operation;
	private String transferAcc;
	private String transferAmount;

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

		user = new User();
		isLoggedIn = false;
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

				case BZ_PLAY:
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

				case MAIN_MENU_ITEM:
					handleMainMenuItemClick(msg);
					break;

				case ACCOUNT:
					handleAccountClick(msg.getDetails());
					break;

				case CANCEL:
					handleCancel();
					break;

				case WITHDRAW_AMOUNT:
					handleWithdrawAmount(msg.getDetails());
					break;

				case TD_SendCard:
					handleReceiveTransferAcc(msg.getDetails());
					break;

				case TD_MainMenu:
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
					break;

				case AmountInput:
					handleAmountInput(msg.getDetails());
					break;

				case TD_GetBalance:
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SendBalance, "1000.0"));
					break;

				case Receipt:
					handleReceipt(msg.getDetails());
					break;

				case AnotherService:
					handleAnotherService(msg.getDetails());
					break;

				case TakeOutCash:
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "WithdrawSuccess"));
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


	private void restart() {
    	updateState(State.WELCOME);
    	updateOperation(Operation.NONE);
	}


	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}


	private String generateReceipt() {
    	String receipt = "";
    	receipt += "\n============================\n\n";
    	receipt += "                           RECEIPT\n";

    	receipt += "\nOperation: " + operation + "\n";
    	receipt += "\nDate&Time: " + now() + "\n";

    	receipt += "\n============================\n";
    	return receipt;
	}


	private void handleReceipt(String choice) {
    	if (choice.equals("YES")) {
			log.info(id + ": print receipt");
			String receipt = generateReceipt();
			printerMBox.send(new Msg(id, mbox, Msg.Type.PR_PRINT, receipt));
		}
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AnotherService"));
	}


	private void handleAnotherService(String choice) {
		if (choice.equals("YES")) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
			return;
		}
		handleCancel();
	}


	//------------------------------------------------------------
	// handleReceiveTrahandleAmountInputnsferCard
	private void handleAmountInput(String amount) {
    	if (operation == Operation.WITHDRAW) {
    		handleWithdrawAmount(amount);
    		return;
		}

    	if (operation == Operation.TRANSFER) {
    		transferAmount = amount;
    		handleTransfer();
    		return;
		}

		if (operation == Operation.DEPOSIT) {
			handleDeposit(amount);
			return;
		}
	} // handleAmountInput


	private void handleDeposit(String amount) {
		log.info(id + ": cash deposit received HKD$" + amount);
		collectorMBox.send(new Msg(id, mbox, Msg.Type.Reset, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.CashReceived, ""));
		boolean depositSuccess = true;
		if (depositSuccess) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "DepositSuccess"));
		}
	}


	//------------------------------------------------------------
	// handleTransfer
	private void handleTransfer() {
		log.info(id + ": TRANSFER HKD$" + transferAmount + " FROM [" + user.getCurrentAcc().getAccountNo() + "] TO [" + transferAcc + "]");
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "TransactionSuccess"));
	} // handleTransfer


	//------------------------------------------------------------
	// handleReceiveTransferCard
	private void handleReceiveTransferAcc(String accountNo) {
		log.info(id + ": received account number: " + accountNo);
		/** boolean accountExists = user.hasAccount(accountNo); */
		boolean accountExists = true;
		if (!accountExists) {
			handleCancel();
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "InvalidAccount"));
			updateState(State.WELCOME);
			updateOperation(Operation.NONE);
			return;
		}
		transferAcc = accountNo;
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AmountInput"));
		updateState(State.AMOUNT_INPUT);
	} // handleReceiveTransferCard


	//------------------------------------------------------------
	// handleWithdrawAmount
	private void handleWithdrawAmount(String value) {
		if (value.equals("Other")) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AmountInput"));
			updateState(State.AMOUNT_INPUT);
			return;
		}

		log.info(id + ": withdraw amount HKD$" + value);
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "TakeOutCash"));
		dispenserMBox.send(new Msg(id, mbox, Msg.Type.DispenseCash, value));
	} // handleWithdrawAmount


	//------------------------------------------------------------
	// handleAccountClick
	private void handleAccountClick(String accIndex) {
		log.info(id + ": Account chosen: [" + accIndex + "]");
		/** Change later when there is back-end */
		/** Account chosenAcc = user.getAccounts()[Integer.parseInt(accIndex)]; */
		Account chosenAcc = new Account("1111-1111-1111-1111", 1000.0);
		user.setCurrentAcc(chosenAcc);
		log.info(id + ": Current Account: [" + user.getCurrentAcc().getAccountNo() + "]");
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
		updateState(State.MAIN_MENU);
	} // handleAccountClick


	//------------------------------------------------------------
	// handleMainMenuItemClick
	private void handleMainMenuItemClick(Msg msg) {
		log.info(id + ": Main Menu Item clicked [" + msg.getDetails() + "]");

		switch (msg.getDetails()) {
			case "WITHDRAW":
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "WithdrawAmount"));
				updateOperation(Operation.WITHDRAW);
				break;

			case "DEPOSIT":
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "InsertCash"));
				collectorMBox.send(new Msg(id, mbox, Msg.Type.TD_GetAmount, "InsertCash"));
				updateOperation(Operation.DEPOSIT);
				break;

			case "BALANCE":
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Balance"));
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_SendBalance, "1000.0"));
				updateOperation(Operation.BALANCE);
				break;

			case "TRANSFER":
				touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AccountInput"));
				updateOperation(Operation.TRANSFER);
				updateState(State.ACCOUNT_INPUT);
				break;

			case "CANCEL":
				handleCancel();
				break;

			default:
				log.info(id + ": unknown menu item " + msg.getDetails());
				break;
		}
	} // handleMainMenuItemClick


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
	} // handleTimesUp


	//------------------------------------------------------------
	// handleCardInserted
	private void handleCardInserted(Msg msg) {
		log.info("CardInserted: " + msg.getDetails());
		buzz("beep");
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

		if(user.getPin().length() < 6) {
			user.setPin(user.getPin() + pinNum);
		}
	} // handleCardInserted


	//------------------------------------------------------------
	// handleAuth
	private void handleAuth() {
		buzz("beep");
		if (!user.isValid()) {
			log.info("Invalid PIN: " + user.getPin());
			user.setPin(null);
			handleErase();
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Incorrect Pin"));
			updateState(State.INCORRECT_PIN);
			return;
		}
		log.info("Authorize account: " + user.toString());
		updateState(State.ACCOUNT_LIST);
		// user.setAccounts(bams.getAccounts(user.getCardNum(), user.getPin());
		String accounts = "1111-1111-1111-1111/2222-2222-2222-2222/3333-3333-3333-3333";
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "AccountList"));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AccountList, accounts));
	} // handleAuth


	//------------------------------------------------------------
	// handleCancel
	private void handleCancel() {
		buzz("beep");
		restart();
		user.reset();
		cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
		if (state == State.PIN || state == State.INCORRECT_PIN) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
		}
	} // handleCancel


	// handleEnter
	private void handleEnter() {
		if (state == State.PIN || state == State.INCORRECT_PIN) {
			handleAuth();
		}
		if (state == State.AMOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_GetAmount, ""));
		}
		if (state == State.ACCOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_GetCard, ""));
		}
	} // handleEnter


	//------------------------------------------------------------
	// handleErase
	private void handleErase() {
    	if (state == State.PIN || state == State.INCORRECT_PIN) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
		}
	} // handleErase


	//------------------------------------------------------------
	// handleNumkeyPress
	private void handleNumkeyPress(Msg msg) {
    	if (msg.getDetails().compareToIgnoreCase("00") == 0) return;

    	if (state == State.WELCOME || state == State.MAIN_MENU) return;

    	if (state == State.PIN || state == State.INCORRECT_PIN) {
			handleSetAccountPin(msg.getDetails());
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendPinText, ""));
			return;
		}

    	if (state == State.AMOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendAmountText, msg.getDetails()));
			return;
		}

    	if (state == State.ACCOUNT_INPUT) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_CardInput, msg.getDetails()));
			return;
		}
	} // handleNumkeyPress


    //------------------------------------------------------------
    // processKeyPressed
    private void processKeyPressed(Msg msg) {
		log.info("KeyPressed: " + msg.getDetails());
		// *** The following is an example only!! ***
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
        	handleCancel();
		} else if (msg.getDetails().compareToIgnoreCase("Erase") == 0) {
			handleErase();
		} else if (msg.getDetails().compareToIgnoreCase("???") == 0) {

		} else if (msg.getDetails().compareToIgnoreCase("Enter") == 0) {
			handleEnter();
		} else if (msg.getDetails().compareToIgnoreCase(".") == 0) {

		} else {
			handleNumkeyPress(msg);
		}
    } // processKeyPressed


	//------------------------------------------------------------
	// buzz
	private void buzz(String variant) {
		buzzerMBox.send(new Msg(id, mbox, Msg.Type.BZ_PLAY, variant));
	} // buzz


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
		log.info("MouseCLicked: " + msg.getDetails());
	} // processMouseClicked
} // CardReaderHandler
