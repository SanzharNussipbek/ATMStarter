package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.logging.Logger;


//======================================================================
// TouchDisplayEmulatorController
public class TouchDisplayEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private TouchDisplayEmulator touchDisplayEmulator;
    private MBox touchDisplayMBox;
    public TextField cardPin;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, TouchDisplayEmulator touchDisplayEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.touchDisplayEmulator = touchDisplayEmulator;
        this.touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // td_mouseClick
    public void td_mouseClick(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        log.fine(id + ": mouse clicked: -- (" + x + ", " + y + ")");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y));
    } // td_mouseClick


    //------------------------------------------------------------
    // appendCardPinText
    public void appendCardPinText() {
        if (cardPin.getText().length() >= 12) return;
        cardPin.appendText("* ");
    } // appendCardPinText


    //------------------------------------------------------------
    // clearCardPinText
    protected void clearCardPinText() {
        cardPin.setText("");
    } // clearCardPinText
} // TouchDisplayEmulatorController
