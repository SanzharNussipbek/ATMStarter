package ATMSS.BuzzerHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BuzzerHandler extends HWHandler {
    //------------------------------------------------------------
    // BuzzerHandler
    public BuzzerHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // BuzzerHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case BZ_Play:
                play(msg.getDetails());
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    } // processMsg


    //------------------------------------------------------------
    // play
    protected void play(String audio) {
        log.info(id + ": play sound");
    } // play
}
