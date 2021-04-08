package ATMSS.PrinterHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.PrinterHandler.PrinterHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Printer Emulator for the Printer hardware of ATM class
 * Extends PrinterHandler class
 */
public class PrinterEmulator extends PrinterHandler {
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
    private PrinterEmulatorController printerEmulatorController;


    /**
     * Constructor of the class
     * @param id ID of the current thread
     * @param atmssStarter ATMSSStarter instance
     */
    public PrinterEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // PrinterEmulator


    /**
     * Function to start the emulator
     * @throws Exception Throws Exception
     */
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "PrinterEmulator.fxml";
        loader.setLocation(PrinterEmulator.class.getResource(fxmlName));
        root = loader.load();
        printerEmulatorController = (PrinterEmulatorController) loader.getController();
        printerEmulatorController.initialize(id, atmssStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Printer");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // PrinterEmulator


    /**
     * Handle Printing the receipt
     * @param receipt Receipt text
     */
    public void handlePrint(String receipt) {
        printerEmulatorController.print(receipt);
    } // handlePrint
} // PrinterEmulator

