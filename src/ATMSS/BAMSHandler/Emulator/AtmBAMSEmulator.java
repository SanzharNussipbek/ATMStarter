package ATMSS.BAMSHandler.Emulator;

import ATMSS.BAMSHandler.AtmBAMSHandler;
import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;
import AppKickstarter.AppKickstarter;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.*;

public class AtmBAMSEmulator extends AtmBAMSHandler {
    BAMSHandler bams = null;

    public AtmBAMSEmulator(String id, AppKickstarter appKickstarter, String urlPrefix) {
        super(id, appKickstarter, urlPrefix);
        this.bams = new BAMSHandler(urlPrefix, initLogger());
    }


    //------------------------------------------------------------
    // start
    public void start() throws Exception {

    } // start


    //------------------------------------------------------------
    // handleLogin
    protected void handleLogin(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": login [" + details + "]");
    } // handleLogin


    //------------------------------------------------------------
    // handleEnquiry
    protected void handleEnquiry(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": enquiry [" + details + "]");
    } // handleEnquiry


    //------------------------------------------------------------
    // handleAccounts
    protected void handleAccounts(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": accounts [" + details + "]");
    } // handleAccounts


    //------------------------------------------------------------
    // handleWithdraw
    protected void handleWithdraw(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": withdraw [" + details + "]");
    } // handleWithdraw


    //------------------------------------------------------------
    // handleDeposit
    protected void handleDeposit(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": deposit [" + details + "]");
    } // handleDeposit


    //------------------------------------------------------------
    // handleTransfer
    protected void handleTransfer(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": transfer [" + details + "]");
    } // handleTransfer


    //------------------------------------------------------------
    // initLogger
    static Logger initLogger() {
        // init our logger
        ConsoleHandler logConHdr = new ConsoleHandler();
        logConHdr.setFormatter(new AtmBAMSEmulator.LogFormatter());
        Logger log = Logger.getLogger("TestBAMSHandler");
        log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);
        log.addHandler(logConHdr);
        logConHdr.setLevel(Level.ALL);
        return log;
    } // initLogger


    static class LogFormatter extends Formatter {
        //------------------------------------------------------------
        // format
        public String format(LogRecord rec) {
            Calendar cal = Calendar.getInstance();
            String str = "";

            // get date
            cal.setTimeInMillis(rec.getMillis());
            str += String.format("%02d%02d%02d-%02d:%02d:%02d ",
                    cal.get(Calendar.YEAR) - 2000,
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND));

            // level of the log
            str += "[" + rec.getLevel() + "] -- ";

            // message of the log
            str += rec.getMessage();
            return str + "\n";
        } // format
    } // LogFormatter
}
