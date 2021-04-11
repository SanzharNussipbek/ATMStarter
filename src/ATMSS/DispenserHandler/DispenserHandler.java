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
        if (this.isShutdown) return;
        switch (msg.getType()) {
            case Poll:
                handlePollAck();
                break;

            case DP_DispenseCash:
                handleDispenseCash(msg.getDetails());
                break;

            case DP_TakeOutCash:
                atmss.send(new Msg(id, mbox, Msg.Type.DP_TakeOutCash, msg.getDetails()));
                break;

            case Reset:
                handleReset();
                break;

            case BZ_Play:
                atmss.send(new Msg(id, mbox, Msg.Type.BZ_Play, msg.getDetails()));
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
