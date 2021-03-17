package ATMSS.DispenserHandler.Emulator;

import ATMSS.DispenserHandler.Emulator.DispenserEmulator;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


//======================================================================
// DispenserEmulatorController
public class DispenserEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private DispenserEmulator dispenserEmulator;
    private MBox dispenserMBox;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, DispenserEmulator dispenserEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.dispenserEmulator = dispenserEmulator;
        this.dispenserMBox = appKickstarter.getThread("DispenserHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed
} // DispenserEmulatorController