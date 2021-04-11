package ATMSS.BAMSHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;

import java.io.IOException;

public class AtmBAMSHandler extends HWHandler {
    //------------------------------------------------------------
    // AtmBAMSHandler
    public AtmBAMSHandler(String id, AppKickstarter appKickstarter, String urlPrefix) {
        super(id, appKickstarter);
    } // BuzzerHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        if (this.isShutdown) return;
        String details = msg.getDetails();
        try {
            switch (msg.getType()) {
                case Poll:
                    handlePollAck();
                    break;

                case BAMS_Login:
                    handleLogin(details);
                    break;

                case BAMS_Enquiry:
                    handleEnquiry(details);
                    break;

                case BAMS_Accounts:
                    handleAccounts(details);
                    break;

                case BAMS_Withdraw:
                    handleWithdraw(details);
                    break;

                case BAMS_Deposit:
                    handleDeposit(details);
                    break;

                case BAMS_Transfer:
                    handleTransfer(details);
                    break;

                case BAMS_ChangePin:
                    handleChangePin(details);
                    break;

                case BAMS_Logout:
                    handleLogout(details);
                    break;

                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
            }
        } catch (Exception e) {
            System.out.println("TestBAMSHandler: Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
    } // processMsg


    //------------------------------------------------------------
    // handlePollAck
    protected void handlePollAck() { } // handlePollAck


    //------------------------------------------------------------
    // handleLogin
    protected void handleLogin(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": login");
    } // handleLogin


    //------------------------------------------------------------
    // handleEnquiry
    protected void handleEnquiry(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": enquiry");
    } // handleEnquiry


    //------------------------------------------------------------
    // handleAccounts
    protected void handleAccounts(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": accounts");
    } // handleAccounts


    //------------------------------------------------------------
    // handleWithdraw
    protected void handleWithdraw(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": withdraw");
    } // handleWithdraw


    //------------------------------------------------------------
    // handleDeposit
    protected void handleDeposit(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": deposit");
    } // handleDeposit


    //------------------------------------------------------------
    // handleTransfer
    protected void handleTransfer(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": transfer");
    } // handleTransfer


    //------------------------------------------------------------
    // handleChangePin
    protected void handleChangePin(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": change pin");
    } // handleChangePin


    //------------------------------------------------------------
    // handleLogout
    protected void handleLogout(String details) throws BAMSInvalidReplyException, IOException {
        log.info(id + ": logout");
    } // handleLogout
}