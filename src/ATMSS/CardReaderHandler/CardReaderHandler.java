package ATMSS.CardReaderHandler;

import ATMSS.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


//======================================================================
// CardReaderHandler
public class CardReaderHandler extends HWHandler {
    //------------------------------------------------------------
    // CardReaderHandler
    public CardReaderHandler(String id, AppKickstarter appKickstarter) {
	    super(id, appKickstarter);
    } // CardReaderHandler


    //------------------------------------------------------------
    // processMsg
    protected void processMsg(Msg msg) {
        if (this.isShutdown) return;
        switch (msg.getType()) {
            case Poll:
                handlePollAck();
                break;

            case CR_CardInserted:
                atmss.send(new Msg(id, mbox, Msg.Type.CR_CardInserted, msg.getDetails()));
                break;

            case CR_EjectCard:
                handleCardEject();
                break;

            case CR_CardRemoved:
                handleCardRemove();
                atmss.send(new Msg(id, mbox, Msg.Type.CR_CardRemoved, msg.getDetails()));
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
    // handleCardInsert
    protected void handleCardInsert() {
	log.info(id + ": card inserted");
    } // handleCardInsert


    //------------------------------------------------------------
    // handleCardEject
    protected void handleCardEject() { log.info(id + ": card ejected"); } // handleCardEject


    //------------------------------------------------------------
    // handleCardRemove
    protected void handleCardRemove() {
	    log.info(id + ": card removed");
    } // handleCardRemove
} // CardReaderHandler
