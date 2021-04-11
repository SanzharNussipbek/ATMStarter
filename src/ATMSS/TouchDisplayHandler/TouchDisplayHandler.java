package ATMSS.TouchDisplayHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


//======================================================================
// TouchDisplayHandler
public class TouchDisplayHandler extends HWHandler {

    //------------------------------------------------------------
    // TouchDisplayHandler
    public TouchDisplayHandler(String id, AppKickstarter appKickstarter) throws Exception {
	    super(id, appKickstarter);
    } // TouchDisplayHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        if (msg.getType() == Msg.Type.ADMIN_MenuItem) {
            atmss.send(new Msg(id, mbox, msg.getType(), msg.getDetails()));
        }
        if (this.isShutdown) return;
        String details = msg.getDetails();
        switch (msg.getType()) {
            case Poll:
                handlePollAck();
                break;

            case TD_UpdateDisplay:
                handleUpdateDisplay(details);
                break;

            case TD_AppendPinText:
                handleAppendPinText();
                break;

            case TD_AppendAmountText:
                handleAppendAmountText(details);
                break;

            case TD_ClearPinText:
                handleClearPinText();
                break;

            case TD_GetAmount:
                handleGetAmount();
                break;

            case TD_AccountInput:
                handleAccountInput(details);
                break;

            case TD_GetAccount:
                handleGetAccount();
                break;

            case CL_CashReceived:
                handleCashReceived();
                break;

            case TD_AccountList:
                handleAccountList(details);
                break;

            case TD_SendBalance:
                handleSetBalance(details);
                break;

            case ADMIN_pwd:
                handleAdminPassword(details);
                break;

            case TD_MouseClicked:
            case BZ_Play:
            case MainMenuItem:
            case AccountItem:
            case Cancel:
            case AmountList:
            case TD_SendAccount:
            case AmountInput:
            case PR_Receipt:
            case TD_AnotherService:
                atmss.send(new Msg(id, mbox, msg.getType(), details));
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
                break;
        }
    } // processMsg


    //------------------------------------------------------------
    // handlePollAck
    protected void handlePollAck() { } // handlePollAck


    //------------------------------------------------------------
    // handleAdminPassword
    protected void handleAdminPassword(String value) {
        log.info(id + ": admin password input");
    } // handleAdminPassword


    //------------------------------------------------------------
    // handleSetBalance
    protected void handleSetBalance(String balance) { log.info(id + ":show balance [" + balance + "]"); } // handleSetBalance


    //------------------------------------------------------------
    // handleAccountList
    protected void handleAccountList(String accountList) { log.info(id + ": show accounts list [" + accountList + "]"); } // handleAccountList


    //------------------------------------------------------------
    // handleCashReceived
    protected void handleCashReceived() { log.info(id + ": cash received"); } // handleCashReceived


    //------------------------------------------------------------
    // handleAccountInput
    protected void handleAccountInput(String inputValue) { log.info(id + ": account input"); } // handleAccountInput


    //------------------------------------------------------------
    // handleGetAmount
    protected void handleGetAmount() {
        log.info(id + ": send amount");
    } // handleGetAmount


    //------------------------------------------------------------
    // handleGetCard
    protected void handleGetAccount() { log.info(id + ": send account number"); } // handleGetCard


    //------------------------------------------------------------
    // handleUpdateDisplay
    protected void handleUpdateDisplay(String displayName) { log.info(id + ": update display -- " + displayName); } // handleUpdateDisplay


    //------------------------------------------------------------
    // handleAppendPinText
    protected void handleAppendPinText() {
        log.info(id + ": update pin text");
    } // handleAppendPinText

    //------------------------------------------------------------
    // handleAppendAmountText
    protected void handleAppendAmountText(String value) { log.info(id + ": update amount text"); } // handleAppendAmountText


    //------------------------------------------------------------
    // handleClearPinText
    protected void handleClearPinText() {
        log.info(id + ": clear pin text");
    } // handleClearPinText
} // TouchDisplayHandler
