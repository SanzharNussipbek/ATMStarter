package ATMSS.PrinterHandler.Emulator;

import ATMSS.PrinterHandler.Emulator.PrinterEmulator;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;


//======================================================================
// PrinterEmulatorController
public class PrinterEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private PrinterEmulator printerEmulator;
    private MBox printerMBox;
    public TextArea printerOutput;


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
        printerMBox.send(new Msg(id, printerMBox, Msg.Type.BZ_Play, "button"));

        switch (btn.getText()) {
            case "Take out receipt":
                if (printerOutput.getText().length() == 0) return;
                log.info(id + ": receipt taken");
                printerOutput.setText("");
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // print
    public void print(String receipt) {
        printerMBox.send(new Msg(id, printerMBox, Msg.Type.BZ_Play, "beep"));
        printerOutput.appendText(receipt);
    } // print
} // print