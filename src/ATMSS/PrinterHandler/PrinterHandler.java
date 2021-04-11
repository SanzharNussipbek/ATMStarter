package ATMSS.PrinterHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;


//======================================================================
// PrinterHandler
public class PrinterHandler extends HWHandler {
    //------------------------------------------------------------
    // PrinterHandler
    public PrinterHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // PrinterHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        if (this.isShutdown) return;
        switch (msg.getType()) {
            case Poll:
                handlePollAck();
                break;

            case PR_Print:
                handlePrint(msg.getDetails());
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
    // handlePrint
    public void handlePrint(String receipt) {
        log.info(id + ": printer output");
    } // handlePrint
} // PrinterHandler
