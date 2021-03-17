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
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg
} // DispenserHandler
