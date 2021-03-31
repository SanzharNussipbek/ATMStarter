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
        switch (msg.getType()) {
            case PR_Print:
                handlePrint(msg);
                break;

            case BZ_Play:
                atmss.send(new Msg(id, mbox, Msg.Type.BZ_Play, msg.getDetails()));
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg

    //------------------------------------------------------------
    // handlePrint
    public void handlePrint(Msg msg) {
        log.info(id + ": printer output");
    } // handlePrint
} // PrinterHandler
