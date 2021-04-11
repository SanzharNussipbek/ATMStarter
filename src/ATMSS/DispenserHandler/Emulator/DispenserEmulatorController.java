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

import javax.xml.soap.Text;


//======================================================================
// DispenserEmulatorController
public class DispenserEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private DispenserEmulator dispenserEmulator;
    private MBox dispenserMBox;
    public TextArea dispenserTextArea;
    public TextField dispenserStatusField;

    private boolean ejectorBroken;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, DispenserEmulator dispenserEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.dispenserEmulator = dispenserEmulator;
        this.dispenserMBox = appKickstarter.getThread("DispenserHandler").getMBox();
        ejectorBroken = false;
    } // initialize


    //------------------------------------------------------------
    // getPollAck
    public String getPollAck() {
        return ejectorBroken ? " is broken!" : " is up!";
    } // getPollAck


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        dispenserMBox.send(new Msg(id, dispenserMBox, Msg.Type.BZ_Play, "button"));

        switch (btn.getText()) {
            case "Take Out Cash":
                if (!dispenserStatusField.getText().equals("Dispenser open")) return;
                dispenserMBox.send(new Msg(id, dispenserMBox, Msg.Type.DP_TakeOutCash, ""));
                dispenserTextArea.appendText("Cash taken out\n");
                dispenserStatusField.setText("Dispenser closed");
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // handleDispenseCash
    protected void handleDispenseCash(String amount) {
        dispenserTextArea.appendText("Dispensing HKD$" + amount +"\nCash is out\n");
        dispenserStatusField.setText("Dispenser open");
    } // handleDispenseCash

    //------------------------------------------------------------
    // clear
    protected void clear() {
        dispenserTextArea.setText("");
        dispenserStatusField.setText("Dispenser closed");
    } // clear
} // DispenserEmulatorController