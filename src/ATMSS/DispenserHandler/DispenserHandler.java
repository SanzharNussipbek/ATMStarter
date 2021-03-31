package ATMSS.DispenserHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;


//======================================================================
// DispenserHandler
public class DispenserHandler extends HWHandler {
    //------------------------------------------------------------
    // DispenserHandler
    public DispenserHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // DispenserHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case DispenseCash:
                handleDispenseCash(msg.getDetails());
                break;

            case TakeOutCash:
                atmss.send(new Msg(id, mbox, Msg.Type.TakeOutCash, msg.getDetails()));
                break;

            case Reset:
                handleReset();
                break;

            case BZ_PLAY:
                atmss.send(new Msg(id, mbox, Msg.Type.BZ_PLAY, msg.getDetails()));
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
                break;
        }
    } // processMsg


    //------------------------------------------------------------
    // handleDispenseCash
    protected void handleDispenseCash(String amount) {
        log.info(id + ": dispense cash HKD$" + amount);
    } // handleDispenseCash


    //------------------------------------------------------------
    // handleReset
    protected void handleReset() {
        log.info(id + ": reset");
    } // handleDispenseCash
} // DispenserHandler
