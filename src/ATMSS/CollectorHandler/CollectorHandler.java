package ATMSS.CollectorHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;


//======================================================================
// CollectorHandler
public class CollectorHandler extends HWHandler {
    //------------------------------------------------------------
    // CollectorHandler
    public CollectorHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // CollectorHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        if (this.isShutdown) return;
        switch (msg.getType()) {
            case Poll:
                handlePollAck();
                break;

            case TD_GetAmount:
                handleGetAmount();
                break;

            case AmountInput:
                atmss.send(new Msg(id, mbox, Msg.Type.AmountInput, msg.getDetails()));
                break;

            case Reset:
                handleReset();
                break;

            case BZ_Play:
                atmss.send(new Msg(id, mbox, Msg.Type.BZ_Play, msg.getDetails()));
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg


    //------------------------------------------------------------
    // handlePollAck
    protected void handlePollAck() { } // handlePollAck


    //------------------------------------------------------------
    // handleGetAmount
    protected void handleGetAmount() {
        log.info(id + ": ready to receive cash");
    } // handleGetAmount


    //------------------------------------------------------------
    // handleReset
    protected void handleReset() {
        log.info(id + ": reset");
    } // handleReset

} // CollectorHandler
