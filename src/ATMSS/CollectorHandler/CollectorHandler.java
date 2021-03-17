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
        switch (msg.getType()) {
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg
} // CollectorHandler
