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

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg


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
