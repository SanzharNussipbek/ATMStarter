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
    public TextField amount;
    public TextField denomination100Input;
    public TextField denomination500Input;
    public TextField denomination1000Input;
    public Text totalCash;


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
    // appendAmountText
    public void appendAmountText(String value) {
        amount.appendText(value);
    } // appendAmountText


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

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.MAIN_MENU_ITEM, btnTxt));
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
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.CANCEL, "receipt_no"));
                break;

            case "YES":
                log.info(id + ": YES clicked");
                break;
        }
    } // handleReceiptChoiceClick


    //------------------------------------------------------------
    // handleAccountClick
    public void handleAccountClick(ActionEvent actionEvent) {
        log.info(id + ": account clicked");

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_PLAY, "button"));

        Button btn = (Button) actionEvent.getSource();
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.ACCOUNT, btn.getText()));

    } // handleAccountClick


    //------------------------------------------------------------
    // handleWithdrawAmountClick
    public void handleWithdrawAmountClick(ActionEvent actionEvent) {
        log.info(id + ": withdraw amount clicked");

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_PLAY, "button"));

        Button btn = (Button) actionEvent.getSource();
        String btnTxt = btn.getText();

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.WITHDRAW_AMOUNT, btnTxt));
    } // handleWithdrawAmountClick


    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_PLAY, "button"));

        switch (btn.getId()) {

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
    }

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
} // TouchDisplayEmulatorController
