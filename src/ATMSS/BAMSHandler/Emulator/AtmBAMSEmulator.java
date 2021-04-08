package ATMSS.BAMSHandler.Emulator;

import ATMSS.BAMSHandler.AtmBAMSHandler;
import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.*;

/**
 * BAMS Emulator Class to perform queries to the database
 */
public class AtmBAMSEmulator extends AtmBAMSHandler {
    /**
     * BAMSHandler instance to send queries
     */
    BAMSHandler bams;


    /**
     * Constructor of the class
     * @param id ID of the current thread
     * @param appKickstarter AppKickstarter instance
     * @param urlPrefix URL of the database
     */
    public AtmBAMSEmulator(String id, AppKickstarter appKickstarter, String urlPrefix) {
        super(id, appKickstarter, urlPrefix);
        this.bams = new BAMSHandler(urlPrefix, initLogger());
    }


    /**
     * Mock function to start the emulator
     */
    public void start() { } // start


    /**
     * Handle Log In of the user by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleLogin(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": login [" + details + "]");
        String[] values = details.split("/");
        String cred = bams.login(values[0], values[1]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Login, cred));
    } // handleLogin


    /**
     * Handle Log Out of the user by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleLogout(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": logout [" + details + "]");
        String[] values = details.split("/");
        String result = bams.logout(values[0], values[1]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Logout, result));
    } // handleLogout


    /**
     * Handle Balance Enquiry of the user by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleEnquiry(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": enquiry [" + details + "]");
        String[] values = details.split("/");
        double result = bams.enquiry(values[0], values[1], values[2]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Enquiry, String.valueOf(result)));
    } // handleEnquiry


    /**
     * Handle Account List of the user by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleAccounts(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": accounts [" + details + "]");
        String[] values = details.split("/");
        String result = bams.getAccounts(values[0], values[1]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Accounts, result));
    } // handleAccounts


    /**
     * Handle Withdraw Functionality by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleWithdraw(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": withdraw [" + details + "]");
        String[] values = details.split("/");
        double amount = Double.parseDouble(values[3]);
        double currentAmount = bams.enquiry(values[0], values[1], values[2]);
        if (amount > currentAmount) {
            atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Withdraw, "NOK"));
            return;
        }
        int outAmount = bams.withdraw(values[0], values[1], values[2], values[3]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Withdraw, String.valueOf(outAmount)));
    } // handleWithdraw


    /**
     * Handle Deposit Functionality by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleDeposit(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": deposit [" + details + "]");
        String[] values = details.split("/");
        double depAmount = bams.deposit(values[0], values[1], values[2], values[3]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Deposit, String.valueOf(depAmount)));
    } // handleDeposit


    /**
     * Handle Transfer Functionality by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleTransfer(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": transfer [" + details + "]");
        String[] values = details.split("/");
        double amount = Double.parseDouble(values[4]);
        double currentAmount = bams.enquiry(values[0], values[2], values[1]);
        if (amount > currentAmount) {
            atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Transfer, "NOK"));
            return;
        }
        double transAmount = bams.transfer(values[0], values[1], values[2], values[3], values[4]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Transfer, String.valueOf(transAmount)));
    } // handleTransfer


    /**
     * Handle Change Pin Functionality by sending query to the database
     * @param details The details for the query
     * @throws BAMSInvalidReplyException Exception thrown on invalid reply from the database
     * @throws IOException Input-Output Exception
     */
    protected void handleChangePin(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": change pin [" + details + "]");
        String[] values = details.split("/");
        String result = bams.chgPinReq(values[0], values[1], values[2], values[3]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_ChangePin, result));
    } // handleChangePin


    /**
     * Initialize logger
     * @return the logger instance
     */
    static Logger initLogger() {
        ConsoleHandler logConHdr = new ConsoleHandler();
        logConHdr.setFormatter(new AtmBAMSEmulator.LogFormatter());
        Logger log = Logger.getLogger("TestBAMSHandler");
        log.setUseParentHandlers(false);
        log.setLevel(Level.ALL);
        log.addHandler(logConHdr);
        logConHdr.setLevel(Level.ALL);
        return log;
    } // initLogger


    /**
     * Format the logs
     */
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
