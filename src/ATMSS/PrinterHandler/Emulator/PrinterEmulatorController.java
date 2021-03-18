package ATMSS.PrinterHandler.Emulator;

import ATMSS.PrinterHandler.Emulator.PrinterEmulator;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.util.logging.Logger;


//======================================================================
// PrinterEmulatorController
public class PrinterEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PrinterEmulator printerEmulator;
    private MBox printerMBox;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, PrinterEmulator printerEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.printerEmulator = printerEmulator;
        this.printerMBox = appKickstarter.getThread("PrinterHandler").getMBox();
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
} // PrinterEmulatorController