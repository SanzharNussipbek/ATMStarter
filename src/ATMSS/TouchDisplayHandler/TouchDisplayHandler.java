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
        switch (msg.getType()) {
            case TD_MouseClicked:
                atmss.send(new Msg(id, mbox, Msg.Type.TD_MouseClicked, msg.getDetails()));
                break;

            case TD_UpdateDisplay:
                handleUpdateDisplay(msg);
                break;

            case TD_AppendPinText:
                handleAppendPinText();
                break;

            case TD_AppendAmountText:
                handleAppendAmountText(msg);
                break;

            case TD_ClearPinText:
                handleClearPinText();
                break;

            case BZ_PLAY:
                atmss.send(new Msg(id, mbox, Msg.Type.BZ_PLAY, msg.getDetails()));
                break;

            case MAIN_MENU_ITEM:
                atmss.send(new Msg(id, mbox, Msg.Type.MAIN_MENU_ITEM, msg.getDetails()));
                break;

            case ACCOUNT:
                atmss.send(new Msg(id, mbox, Msg.Type.ACCOUNT, msg.getDetails()));
                break;

            case CANCEL:
                atmss.send(new Msg(id, mbox, Msg.Type.CANCEL, msg.getDetails()));
                break;

            case WITHDRAW_AMOUNT:
                atmss.send(new Msg(id, mbox, Msg.Type.WITHDRAW_AMOUNT, msg.getDetails()));
                break;

            case TD_GetAmount:
                handleGetAmount();
                break;

            case TD_CardInput:
                handleCardInput(msg);
                break;

            case TD_GetCard:
                handleGetCard();
                break;

            case TD_SendCard:
                atmss.send(new Msg(id, mbox, Msg.Type.TD_SendCard, msg.getDetails()));
                break;

            case TD_MainMenu:
                atmss.send(new Msg(id, mbox, Msg.Type.TD_MainMenu, msg.getDetails()));
                break;

            case AmountInput:
                atmss.send(new Msg(id, mbox, Msg.Type.AmountInput, msg.getDetails()));
                break;

            case CashReceived:
                handleCashReceived();
                break;

            case TD_AccountList:
                handleAccountList(msg.getDetails());
                break;

            case TD_GetBalance:
                atmss.send(new Msg(id, mbox, Msg.Type.TD_GetBalance, msg.getDetails()));
                break;

            case TD_SendBalance:
                handleSetBalance(msg.getDetails());
                break;

            case Receipt:
                atmss.send(new Msg(id, mbox, Msg.Type.Receipt, msg.getDetails()));
                break;

            case AnotherService:
                atmss.send(new Msg(id, mbox, Msg.Type.AnotherService, msg.getDetails()));
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
                break;
        }
    } // processMsg


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
    // handleCardInput
    protected void handleCardInput(Msg msg) { log.info(id + ": card input"); } // handleCardInput


    //------------------------------------------------------------
    // handleGetAmount
    protected void handleGetAmount() {
        log.info(id + ": send amount");
    } // handleGetAmount


    //------------------------------------------------------------
    // handleGetCard
    protected void handleGetCard() {
        log.info(id + ": send card number");
    } // handleGetCard


    //------------------------------------------------------------
    // handleUpdateDisplay
    protected void handleUpdateDisplay(Msg msg) {
	    log.info(id + ": update display -- " + msg.getDetails());
    } // handleUpdateDisplay


    //------------------------------------------------------------
    // handleAppendPinText
    protected void handleAppendPinText() {
        log.info(id + ": update pin text");
    } // handleAppendPinText

    //------------------------------------------------------------
    // handleAppendAmountText
    protected void handleAppendAmountText(Msg msg) {
        log.info(id + ": update amount text");
    } // handleAppendAmountText


    //------------------------------------------------------------
    // handleClearPinText
    protected void handleClearPinText() {
        log.info(id + ": clear pin text");
    } // handleClearPinText
} // TouchDisplayHandler
