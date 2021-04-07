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
        try {
            switch (msg.getType()) {
                case BAMS_Login:
                    handleLogin(msg.getDetails());
                    break;

                case BAMS_Enquiry:
                    handleEnquiry(msg.getDetails());
                    break;

                case BAMS_Accounts:
                    handleAccounts(msg.getDetails());
                    break;

                case BAMS_Withdraw:
                    handleWithdraw(msg.getDetails());
                    break;

                case BAMS_Deposit:
                    handleDeposit(msg.getDetails());
                    break;

                case BAMS_Transfer:
                    handleTransfer(msg.getDetails());
                    break;

                case BAMS_ChangePin:
                    handleChangePin(msg.getDetails());
                    break;

                case BAMS_Logout:
                    handleLogout(msg.getDetails());
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