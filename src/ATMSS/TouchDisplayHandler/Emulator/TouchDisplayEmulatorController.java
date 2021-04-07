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
    public Text insertCashTitle;
    public TextField cardNumber;
    public Button firstAcc;
    public Button secondAcc;
    public Button thirdAcc;
    public Text balance;


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
    // handleCashReceived
    public void handleCashReceived() {
        insertCashTitle.setText("Cash Received");
    } // handleCashReceived


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
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_Play, "button"));

        Button btn = (Button) actionEvent.getSource();
        String btnTxt = btn.getText();

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.MainMenuItem, btnTxt));
    } // handleMenuItemClick


    //------------------------------------------------------------
    // handleAnotherServiceClick
    public void handleAnotherServiceClick(ActionEvent actionEvent) {
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_Play, "button"));

        Button btn = (Button) actionEvent.getSource();
        String btnTxt = btn.getText();

        log.info(id + ": another service choice [" + btnTxt + "]");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_AnotherService, btnTxt));
    } // handleAnotherServiceClick


    //------------------------------------------------------------
    // handleReceiptChoiceClick
    public void handleReceiptChoiceClick(ActionEvent actionEvent) {
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_Play, "button"));

        Button btn = (Button) actionEvent.getSource();
        String btnTxt = btn.getText();

        log.info(id + ": receipt choice [" + btnTxt + "]");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.PR_Receipt, btnTxt));
    } // handleReceiptChoiceClick


    //------------------------------------------------------------
    // handleSendAmount
    public void handleSendAmount() {
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.AmountInput, totalCash.getText()));
    } // handleSendAmount


    //------------------------------------------------------------
    // handleSendCard
    public void handleSendCard() {
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_SendCard, cardNumber.getText()));
    } // handleSendCard


    //------------------------------------------------------------
    // handleAccountClick
    public void handleAccountClick(ActionEvent actionEvent) {
        log.info(id + ": account clicked");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_Play, "button"));

        Button btn = (Button) actionEvent.getSource();
        String accIndex = btn.getId().equals("firstAcc") ? "0" : (btn.getId().equals("secondAcc") ? "1" : "2");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.AccountItem, accIndex));

    } // handleAccountClick


    //------------------------------------------------------------
    // handleWithdrawAmountClick
    public void handleAmountListClick(ActionEvent actionEvent) {
        log.info(id + ": amount list item clicked");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_Play, "button"));

        Button btn = (Button) actionEvent.getSource();
        String btnTxt = btn.getText();
        if (!btnTxt.equals("Other")) btnTxt = btnTxt.substring(4);

        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.AmountList, btnTxt));
    } // handleWithdrawAmountClick


    //------------------------------------------------------------
    // handleCardInput
    public void handleCardInput(String value) {
        if (isValidCardNum(cardNumber.getText())) return;
        cardNumber.appendText(value);
    } // handleCardInput


    //------------------------------------------------------------
    // amountButtonPressed
    public void amountButtonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.BZ_Play, "button"));

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
    }// amountButtonPressed


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


    //------------------------------------------------------------
    // handleSetAccountList
    public void handleSetAccountList(String accountList) {
        if (firstAcc == null || secondAcc == null || thirdAcc == null) return;
        String[] accounts = accountList.split("/");
        firstAcc.setText(accounts[0]);
        secondAcc.setText(accounts[1]);
        thirdAcc.setText(accounts[2]);
    } // handleSetAccountList


    //------------------------------------------------------------
    // setBalance
    public void setBalance(String amount) {
        if (balance == null) return;
        balance.setText("HKD$" + amount);
    } // setBalance


    //------------------------------------------------------------
    // isValidCardNum
    private boolean isValidCardNum(String value) {
        return numCount(value) == 16;
    } // isValidCardNum


    //------------------------------------------------------------
    // numCount
    private int numCount(String value) {
        int counter = 0;
        for (char ch : value.toCharArray()) {
            counter += Character.isDigit(ch) ? 1 : 0;
        }
        return counter;
    }// numCount
} // TouchDisplayEmulatorController
