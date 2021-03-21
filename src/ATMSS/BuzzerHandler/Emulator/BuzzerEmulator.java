package ATMSS.BuzzerHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.BuzzerHandler.BuzzerHandler;
import ATMSS.CollectorHandler.Emulator.CollectorEmulator;
import ATMSS.CollectorHandler.Emulator.CollectorEmulatorController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class BuzzerEmulator extends BuzzerHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;

    //------------------------------------------------------------
    // BuzzerEmulator
    public BuzzerEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // BuzzerEmulator
}
