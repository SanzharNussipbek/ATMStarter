package ATMSS.KeypadHandler;

import ATMSS.HWHandler.HWHandler;
import ATMSS.ATMSSStarter;
import AppKickstarter.misc.*;


//======================================================================
// KeypadHandler
public class KeypadHandler extends HWHandler {
    //------------------------------------------------------------
    // KeypadHandler
    public KeypadHandler(String id, ATMSSStarter atmssStarter) {
	super(id, atmssStarter);
    } // KeypadHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        if (this.isShutdown) return;
        switch (msg.getType()) {
            case Poll:
                handlePollAck();
                break;

            case KP_KeyPressed:
                atmss.send(new Msg(id, mbox, Msg.Type.KP_KeyPressed, msg.getDetails()));
                atmss.send(new Msg(id, mbox, Msg.Type.BZ_Play, "button"));
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg


    //------------------------------------------------------------
    // handlePollAck
    protected void handlePollAck() { } // handlePollAck
} // KeypadHandler
