package ATMSS.CollectorHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CollectorHandler.CollectorHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Collector Emulator for the Collector hardware of ATM class
 * Extends CollectorHandler class
 */
public class CollectorEmulator extends CollectorHandler {
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
    private CollectorEmulatorController collectorEmulatorController;


    /**
     * Constructor of the class
     * @param id ID of the current thread
     * @param atmssStarter ATMSSStarter instance
     */
    public CollectorEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // CollectorEmulator


    /**
     * Function to start the emulator
     * @throws Exception Throws Exception
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "CollectorEmulator.fxml";
        loader.setLocation(CollectorEmulator.class.getResource(fxmlName));
        root = loader.load();
        collectorEmulatorController = (CollectorEmulatorController) loader.getController();
        collectorEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Collector");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // CollectorEmulator


    /**
     * Handle receiving the cash from the user
     */
    protected void handleGetAmount() {
        log.info(id + ": ready to receive cash");
        collectorEmulatorController.getReady();
    } // handleGetAmount


    /**
     * Reset the emulator and controller
     */
    protected void handleReset() {
        log.info(id + ": reset");
        collectorEmulatorController.reset();
    } // handleReset
} // CollectorEmulator

