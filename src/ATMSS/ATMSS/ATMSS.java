package ATMSS.ATMSS;

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

    //------------------------------------------------------------
    // ATMSS
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
		super(id, appKickstarter);
		pollingTime = Integer.parseInt(appKickstarter.getProperty("ATMSS.PollingTime"));
    } // ATMSS


    //------------------------------------------------------------
    // run
    public void run() {
		Timer.setTimer(id, mbox, pollingTime);
		log.info(id + ": starting...");

		cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
		keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
		touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
		collectorMBox = appKickstarter.getThread("CollectorHandler").getMBox();
		dispenserMBox = appKickstarter.getThread("DispenserHandler").getMBox();
		printerMBox = appKickstarter.getThread("PrinterHandler").getMBox();

		initWelcomeScreen();

		for (boolean quit = false; !quit;) {
			Msg msg = mbox.receive();

			log.fine(id + ": message received: [" + msg + "].");

			switch (msg.getType()) {
				case TD_MouseClicked:
					log.info("MouseCLicked: " + msg.getDetails());
					processMouseClicked(msg);
					break;

				case KP_KeyPressed:
					log.info("KeyPressed: " + msg.getDetails());
					processKeyPressed(msg);
					break;

				case CR_CardInserted:
					log.info("CardInserted: " + msg.getDetails());
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Pin"));
					break;

				case TimesUp:
					Timer.setTimer(id, mbox, pollingTime);
					log.info("Poll: " + msg.getDetails());
					cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
					keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
					touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
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
	// initWelcomeScreen
	private void initWelcomeScreen() {
		touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
	} // initWelcomeScreen


    //------------------------------------------------------------
    // processKeyPressed
    private void processKeyPressed(Msg msg) {
        // *** The following is an example only!! ***
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
			cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Welcome"));
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
		} else if (msg.getDetails().compareToIgnoreCase("Erase") == 0) {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_ClearPinText, "TD_ClearPinText"));
		} else if (msg.getDetails().compareToIgnoreCase("???") == 0) {

		} else if (msg.getDetails().compareToIgnoreCase("Enter") == 0) {

		} else if (msg.getDetails().compareToIgnoreCase("00") == 0) {

		} else if (msg.getDetails().compareToIgnoreCase(".") == 0) {

		} else {
			touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_AppendPinText, "TD_AppendPinText"));
		}
    } // processKeyPressed


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
		// *** process mouse click here!!! ***
    } // processMouseClicked
} // CardReaderHandler
