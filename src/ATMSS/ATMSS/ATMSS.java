package ATMSS.ATMSS;

import ATMSS.Account.Account;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;


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
    private Account userAccount;
    private enum State {
    	WELCOME,
		PIN,
		INCORRECT_PIN,
		MAIN_MENU,
	}
	private boolean isLoggedIn;
	private State state;

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

		userAccount = new Account();
		isLoggedIn = false;
		state = State.WELCOME;

		initWelcomeScreen();
	} // init

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

				case TimesUp:
					handleTimesUp(msg);
					break;

				case PollAck:
					log.info("PollAck: " + msg.getDetails());
					break;

				case Terminate:
					quit = true;
					break;

				default:
					log.warning(id + ": unknown message type: [" + msg + "]");
			}
		}

		// declaring our departure
		appKickstarter.unregThread(this);
		log.info(id + ": terminating...");
    } // run


	//------------------------------------------------------------
	// handleTimesUp
	private void handleTimesUp(Msg msg) {
		Timer.setTimer(id, mbox, pollingTime);
		log.info("Poll: " + msg.getDetails());
		cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
	} // handleTimesUp


	//------------------------------------------------------------
	// initWelcomeScreen
	private void initWelcomeScreen() {
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
	} // initWelcomeScreen


	//------------------------------------------------------------
	// handleCardInserted
	private void handleCardInserted(Msg msg) {
		log.info("CardInserted: " + msg.getDetails());
		userAccount.setCardNum(msg.getDetails());
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Pin"));
		state = State.PIN;
	} // handleCardInserted


	//------------------------------------------------------------
	// handleSetAccountPin
	private void handleSetAccountPin(String pinNum) {
		if (userAccount.getPin() == null) {
			userAccount.setPin(pinNum);
		} else if(userAccount.getPin().length() < 6) {
			userAccount.setPin(userAccount.getPin() + pinNum);
		}
	} // handleCardInserted


	//------------------------------------------------------------
	// handleAuth
	private void handleAuth() {
		if (!userAccount.isValid()) {
			log.info("Invalid PIN: " + userAccount.getPin());
			userAccount.setPin(null);
			handleErase();
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Incorrect Pin"));
			state = State.INCORRECT_PIN;
			return;
		}
		log.info("Authorize account: " + userAccount.toString());
		state = State.MAIN_MENU;
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
	} // handleAuth


	//------------------------------------------------------------
	// handleCancel
	private void handleCancel() {
		userAccount.reset();
		cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
	} // handleCancel


	// handleEnter
	private void handleEnter() {
		if (state == State.PIN || state == State.INCORRECT_PIN) {
			handleAuth();
		}
	} // handleEnter


	//------------------------------------------------------------
	// handleErase
	private void handleErase() {
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
	} // handleErase


	//------------------------------------------------------------
	// handleNumkeyPress
	private void handleNumkeyPress(Msg msg) {
    	if (state == State.WELCOME || state == State.MAIN_MENU) return;
		if (msg.getDetails().compareToIgnoreCase("00") == 0) return;
		handleSetAccountPin(msg.getDetails());
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendPinText, "TD_AppendPinText"));
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
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
		log.info("MouseCLicked: " + msg.getDetails());
	} // processMouseClicked
} // CardReaderHandler
