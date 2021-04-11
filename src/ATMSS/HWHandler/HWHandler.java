package ATMSS.HWHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

import java.util.Random;


//======================================================================
// HWHandler
public class HWHandler extends AppThread {
    protected MBox atmss = null;
    protected boolean isShutdown = false;

    //------------------------------------------------------------
    // HWHandler
    public HWHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // HWHandler


    //------------------------------------------------------------
    // run
    public void run() {
        atmss = appKickstarter.getThread("ATMSS").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case Terminate:
                    quit = true;
                    break;

                case Shutdown:
                    isShutdown = true;
                    atmss.send(new Msg(id, mbox, Msg.Type.Shutdown, "SUCCESS"));
                    break;

                case Reset:
                    isShutdown = false;
                    atmss.send(new Msg(id, mbox, Msg.Type.Reset, "HEALTHY"));
                    break;

                default:
                    processMsg(msg);
                    break;
            }
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        log.warning(id + ": unknown message type: [" + msg + "]");
    } // processMsg
}
