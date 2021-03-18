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
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg
} // PrinterHandler
