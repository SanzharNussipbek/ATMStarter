package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.logging.Logger;


//======================================================================
// TouchDisplayEmulatorController
public class TouchDisplayEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private TouchDisplayEmulator touchDisplayEmulator;
    private MBox touchDisplayMBox;
    public TextField cardPin;
    public Text pin_title;
    public Text pin_subtitle;
    public Text menu_title;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, TouchDisplayEmulator touchDisplayEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.touchDisplayEmulator = touchDisplayEmulator;
        this.touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // td_mouseClick
    public void td_mouseClick(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        log.fine(id + ": mouse clicked: -- (" + x + ", " + y + ")");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y));
    } // td_mouseClick


    //------------------------------------------------------------
    // appendCardPinText
    public void appendCardPinText() {
        if (cardPin.getText().length() >= 12) return;
        cardPin.appendText("* ");
    } // appendCardPinText


    //------------------------------------------------------------
    // clearCardPinText
    protected void clearCardPinText() {
        cardPin.setText("");
    } // clearCardPinText


    //------------------------------------------------------------
    // handleIncorrectPin
    protected void handleIncorrectPin() {
        pin_title.setText("Incorrect PIN");
        pin_title.setTranslateX(10);
        pin_subtitle.setVisible(true);
    } // handleIncorrectPin


    //------------------------------------------------------------
    // handleMenuItemClick
    public void handleMenuItemClick(ActionEvent actionEvent) {
        log.info(id + ": menu item clicked");

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_PLAY, "button"));

        Button btn = (Button) actionEvent.getSource();
        String btnTxt = btn.getText();

        switch (btnTxt) {
            case "TRANSFER":
                menu_title.setTranslateX(-50.0);
                menu_title.setText(btnTxt + " clicked");
                break;

            case "DEPOSIT":
                menu_title.setText(btnTxt + " clicked");
                break;

            case "WITHDRAW":
                menu_title.setText(btnTxt + " clicked");
                break;

            case "BALANCE":
                menu_title.setText(btnTxt + " clicked");
                break;

            case "CANCEL":
                menu_title.setText(btnTxt + " clicked");
                break;

            case "???":
                menu_title.setText(btnTxt + " clicked");
                break;
        }
    } // handleMenuItemClick

    //------------------------------------------------------------
    // handleReceiptChoiceClick
    public void handleReceiptChoiceClick(ActionEvent actionEvent) {
        log.info(id + ": receipt choice clicked");

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_PLAY, "button"));

        Button btn = (Button) actionEvent.getSource();
        String btnTxt = btn.getText();

        switch (btnTxt) {
            case "NO":
                log.info(id + ": NO clicked");
                break;

            case "YES":
                log.info(id + ": YES clicked");
                break;
        }
    } // handleReceiptChoiceClick

} // TouchDisplayEmulatorController
