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
                case Poll:
                    atmss.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
                    break;

                case Terminate:
                    quit = true;
                    break;

                case Shutdown:
                    isShutdown = true;
                    String result = new Random().nextDouble() <= 0.9 ? "SUCCESS" : "FAILED";
                    atmss.send(new Msg(id, mbox, Msg.Type.Shutdown, result));
                    break;

                case Reset:
                    isShutdown = false;
                    String resetResult = new Random().nextDouble() <= 0.9 ? "HEALTHY" : "FAILURE";
                    atmss.send(new Msg(id, mbox, Msg.Type.Reset, resetResult));
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
