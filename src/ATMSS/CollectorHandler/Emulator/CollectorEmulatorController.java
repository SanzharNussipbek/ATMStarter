package ATMSS.CollectorHandler.Emulator;

import ATMSS.CollectorHandler.Emulator.CollectorEmulator;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

//======================================================================
// CollectorEmulatorController
public class CollectorEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CollectorEmulator collectorEmulator;
    private MBox collectorMBox;
    public TextField denomination100Input;
    public TextField denomination500Input;
    public TextField denomination1000Input;
    public Text totalCash;
    public TextField collectorStatusField;
    private String STATUS_READY = "Ready for deposit";

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CollectorEmulator collectorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.collectorEmulator = collectorEmulator;
        this.collectorMBox = appKickstarter.getThread("CollectorHandler").getMBox();
    } // initialize


    public void getReady() {
        collectorStatusField.setText(STATUS_READY);
    }


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        collectorMBox.send(new Msg(id, collectorMBox, Msg.Type.BZ_Play, "button"));
        if (!collectorStatusField.getText().equals(STATUS_READY)) return;

        switch (btn.getId()) {
            case "insertCash":
                handleInsertCash();
                break;

            case "decrease100":
                handleDecreaseDenomination(100);
                break;

            case "decrease500":
                handleDecreaseDenomination(500);
                break;

            case "decrease1000":
                handleDecreaseDenomination(1000);
                break;

            case "increase100":
                handleIncreaseDenomination(100);
                break;

            case "increase500":
                handleIncreaseDenomination(500);
                break;

            case "increase1000":
                handleIncreaseDenomination(1000);
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    //------------------------------------------------------------
    // handleInsertCash
    public void handleInsertCash() {
        int value = Integer.parseInt(totalCash.getText());
        log.info(id + ": insert cash HKD$" + value);

        if (value == 0) {
            collectorStatusField.setText("Error: collector is empty");
            return;
        }

        collectorStatusField.setText("Received cash: HKD$" + value);
        collectorMBox.send(new Msg(id, collectorMBox, Msg.Type.AmountInput, String.valueOf(value)));
    } // handleInsertCash


    //------------------------------------------------------------
    // handleDecreaseDenomination
    public void handleDecreaseDenomination(int nominal) {
        log.info(id + ": decrease number of denomination " + nominal);

        int value;
        switch (nominal) {
            case 100:
                value = Integer.parseInt(denomination100Input.getText());
                if (value == 0) return;
                denomination100Input.setText(String.valueOf(value-1));
                break;

            case 500:
                value = Integer.parseInt(denomination500Input.getText());
                if (value == 0) return;
                denomination500Input.setText(String.valueOf(value-1));
                break;

            case 1000:
                value = Integer.parseInt(denomination1000Input.getText());
                if (value == 0) return;
                denomination1000Input.setText(String.valueOf(value-1));
                break;
        }

        int prevAmount = Integer.parseInt(totalCash.getText());
        totalCash.setText(String.valueOf(prevAmount - nominal));
    } // handleDecreaseDenomination


    //------------------------------------------------------------
    // handleIncreaseDenomination
    public void handleIncreaseDenomination(int nominal) {
        log.info(id + ": increase number of denomination " + nominal);

        int value;
        switch (nominal) {
            case 100:
                value = Integer.parseInt(denomination100Input.getText());
                denomination100Input.setText(String.valueOf(value+1));
                break;

            case 500:
                value = Integer.parseInt(denomination500Input.getText());
                denomination500Input.setText(String.valueOf(value+1));
                break;

            case 1000:
                value = Integer.parseInt(denomination1000Input.getText());
                denomination1000Input.setText(String.valueOf(value+1));
                break;
        }
        int prevAmount = Integer.parseInt(totalCash.getText());
        totalCash.setText(String.valueOf(prevAmount + nominal));
    } // handleIncreaseDenomination


    public void reset() {
        collectorStatusField.setText("Closed");
        denomination100Input.setText("0");
        denomination500Input.setText("0");
        denomination1000Input.setText("0");
        totalCash.setText("0");
    }
} // CollectorEmulatorController