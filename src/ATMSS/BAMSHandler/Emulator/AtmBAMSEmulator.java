package ATMSS.BAMSHandler.Emulator;

import ATMSS.BAMSHandler.AtmBAMSHandler;
import ATMSS.BAMSHandler.BAMSHandler;
import ATMSS.BAMSHandler.BAMSInvalidReplyException;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

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
        String[] values = details.split("/");
        String cred = bams.login(values[0], values[1]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Login, cred));
    } // handleLogin


    //------------------------------------------------------------
    // handleEnquiry
    protected void handleEnquiry(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": enquiry [" + details + "]");
        String[] values = details.split("/");
        double result = bams.enquiry(values[0], values[1], values[2]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Enquiry, String.valueOf(result)));
    } // handleEnquiry


    //------------------------------------------------------------
    // handleAccounts
    protected void handleAccounts(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": accounts [" + details + "]");
        String[] values = details.split("/");
        String result = bams.getAccounts(values[0], values[1]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Accounts, result));
    } // handleAccounts


    //------------------------------------------------------------
    // handleWithdraw
    protected void handleWithdraw(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": withdraw [" + details + "]");
        String[] values = details.split("/");
        double amount = Double.valueOf(values[3]);
        double currentAmount = bams.enquiry(values[0], values[1], values[2]);
        if (amount > currentAmount) {
            atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Withdraw, "NOK"));
            return;
        }
        int outAmount = bams.withdraw(values[0], values[1], values[2], values[3]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Withdraw, String.valueOf(outAmount)));
    } // handleWithdraw


    //------------------------------------------------------------
    // handleDeposit
    protected void handleDeposit(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": deposit [" + details + "]");
        String[] values = details.split("/");
        double depAmount = bams.deposit(values[0], values[1], values[2], values[3]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Deposit, String.valueOf(depAmount)));
    } // handleDeposit


    //------------------------------------------------------------
    // handleTransfer
    protected void handleTransfer(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": transfer [" + details + "]");
        String[] values = details.split("/");
        double amount = Double.valueOf(values[4]);
        double currentAmount = bams.enquiry(values[0], values[2], values[3]);
        if (amount > currentAmount) {
            atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Transfer, "NOK"));
            return;
        }
        double transAmount = bams.transfer(values[0], values[1], values[2], values[3], values[4]);
        atmss.send(new Msg(id, mbox, Msg.Type.BAMS_Transfer, String.valueOf(transAmount)));
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
