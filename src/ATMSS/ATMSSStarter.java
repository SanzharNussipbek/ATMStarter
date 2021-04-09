package ATMSS;

import ATMSS.BAMSHandler.AtmBAMSHandler;
import ATMSS.BuzzerHandler.BuzzerHandler;
import ATMSS.CollectorHandler.CollectorHandler;
import ATMSS.DispenserHandler.DispenserHandler;
import ATMSS.PrinterHandler.PrinterHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

import ATMSS.ATMSS.ATMSS;
import ATMSS.CardReaderHandler.CardReaderHandler;
import ATMSS.KeypadHandler.KeypadHandler;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;

import javafx.application.Platform;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


//======================================================================
// ATMSSStarter
public class ATMSSStarter extends AppKickstarter {
    protected Timer timer;
    protected ATMSS atmss;
    protected CardReaderHandler cardReaderHandler;
    protected KeypadHandler keypadHandler;
    protected TouchDisplayHandler touchDisplayHandler;
    protected CollectorHandler collectorHandler;
    protected DispenserHandler dispenserHandler;
    protected PrinterHandler printerHandler;
    protected BuzzerHandler buzzerHandler;
    protected AtmBAMSHandler bamsHandler;

    protected static String URL_PREFIX = "";
    public static String ADMIN_PASSWORD = "";


    //------------------------------------------------------------
    // main
    public static void main(String [] args) throws IOException {
        new ATMSSStarter().startApp();
    } // main


    //------------------------------------------------------------
    // ATMStart
    public ATMSSStarter() {
	super("ATMSSStarter", "etc/ATM.cfg");
    } // ATMStart


    //------------------------------------------------------------
    // startApp
    protected void startApp() throws IOException {
		// start our application
		log.info("");
		log.info("");
		log.info("============================================================");
		log.info(id + ": Application Starting...");

		getProperties();
		startHandlers();
    } // startApp


	protected void getProperties() throws IOException {
		Properties p = new Properties();
		FileInputStream in = new FileInputStream("config.properties");
		p.load(in);
		URL_PREFIX = p.getProperty("urlPrefix");
		ADMIN_PASSWORD = p.getProperty("adminPassword");
		in.close();
	}


    //------------------------------------------------------------
    // startHandlers
    protected void startHandlers() {
		// create handlers
		try {
			timer = new Timer("timer", this);
			atmss = new ATMSS("ATMSS", this);
			cardReaderHandler = new CardReaderHandler("CardReaderHandler", this);
			keypadHandler = new KeypadHandler("KeypadHandler", this);
			touchDisplayHandler = new TouchDisplayHandler("TouchDisplayHandler", this);
			collectorHandler = new CollectorHandler("CollectorHandler", this);
			dispenserHandler = new DispenserHandler("DispenserHandler", this);
			printerHandler = new PrinterHandler("PrinterHandler", this);
			buzzerHandler = new BuzzerHandler("BuzzerHandler", this);
			bamsHandler = new AtmBAMSHandler("BAMSHandler", this, URL_PREFIX);
		} catch (Exception e) {
			System.out.println("AppKickstarter: startApp failed");
			e.printStackTrace();
			Platform.exit();
		}

		// start threads
		new Thread(timer).start();
		new Thread(atmss).start();
		new Thread(cardReaderHandler).start();
		new Thread(keypadHandler).start();
		new Thread(touchDisplayHandler).start();
		new Thread(collectorHandler).start();
		new Thread(dispenserHandler).start();
		new Thread(printerHandler).start();
		new Thread(buzzerHandler).start();
		new Thread(bamsHandler).start();
    } // startHandlers


    //------------------------------------------------------------
    // stopApp
    public void stopApp() {
		log.info("");
		log.info("");
		log.info("============================================================");
		log.info(id + ": Application Stopping...");
		atmss.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		cardReaderHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		keypadHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		touchDisplayHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		collectorHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		dispenserHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		printerHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		buzzerHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		bamsHandler.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
		timer.getMBox().send(new Msg(id, null, Msg.Type.Terminate, "Terminate now!"));
    } // stopApp
} // ATM.ATMSSStarter
