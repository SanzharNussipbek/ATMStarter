package ATMSS.DispenserHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.DispenserHandler.DispenserHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Dispenser Emulator for the Dispenser hardware of ATM class
 * Extends DispenserHandler class
 */
public class DispenserEmulator extends DispenserHandler {
    /**
     * Instance of the ATMSSStarter
     */
    private ATMSSStarter atmssStarter;
    /**
     * ID of the current thread
     */
    private String id;
    /**
     * Stage of the emulator
     */
    private Stage myStage;
    /**
     * Instance of the Emulator Controller
     */
    private DispenserEmulatorController dispenserEmulatorController;

    /**
     * Constructor of the class
     * @param id ID of the current thread
     * @param atmssStarter ATMSSStarter instance
     */
    public DispenserEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // DispenserEmulator


    /**
     * Function to start the emulator
     * @throws Exception Throws Exception
     */
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


    /**
     * Handle dispensing of the cash
     */
    protected void handleDispenseCash(String amount) {
        log.info(id + ": dispense cash HKD$" + amount);
        dispenserEmulatorController.handleDispenseCash(amount);
    } // handleDispenseCash


    /**
     * Reset the emulator and controller
     */
    protected void handleReset() {
        log.info(id + ": reset");
        dispenserEmulatorController.clear();
    } // handleDispenseCash
} // DispenserEmulator

