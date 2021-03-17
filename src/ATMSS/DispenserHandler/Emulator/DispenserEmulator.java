package ATMSS.DispenserHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.DispenserHandler.DispenserHandler;
import ATMSS.DispenserHandler.Emulator.DispenserEmulatorController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

//======================================================================
// DispenserEmulator
public class DispenserEmulator extends DispenserHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private DispenserEmulatorController dispenserEmulatorController;

    //------------------------------------------------------------
    // DispenserEmulator
    public DispenserEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // DispenserEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "DispenserEmulator.fxml";
        loader.setLocation(DispenserEmulator.class.getResource(fxmlName));
        root = loader.load();
        dispenserEmulatorController = (DispenserEmulatorController) loader.getController();
        dispenserEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Dispenser");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // DispenserEmulator
} // DispenserEmulator

