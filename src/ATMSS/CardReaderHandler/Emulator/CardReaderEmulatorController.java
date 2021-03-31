package ATMSS.CardReaderHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


//======================================================================
// CardReaderEmulatorController
public class CardReaderEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CardReaderEmulator cardReaderEmulator;
    private MBox cardReaderMBox;
    public TextField cardNumField;
    public TextField cardStatusField;
    public TextArea cardReaderTextArea;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CardReaderEmulator cardReaderEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
		this.log = log;
		this.cardReaderEmulator = cardReaderEmulator;
		this.cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
		Button btn = (Button) actionEvent.getSource();
		cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.BZ_Play, "button"));

		switch (btn.getText()) {
			case "Card 1":
				cardNumField.setText(appKickstarter.getProperty("CardReader.Card1"));
				break;

			case "Card 2":
				cardNumField.setText(appKickstarter.getProperty("CardReader.Card2"));
				break;

			case "Card 3":
				cardNumField.setText(appKickstarter.getProperty("CardReader.Card3"));
				break;

			case "Reset":
				cardNumField.setText("");
				break;

			case "Insert Card":
				if (isValidCardNum(cardNumField.getText())) {
					cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardInserted, cardNumField.getText()));
					cardReaderTextArea.appendText("Sending " + cardNumField.getText()+"\n");
					cardStatusField.setText("Card Inserted");
				}
				break;

			case "Remove Card":
				if (cardStatusField.getText().compareTo("Card Ejected") == 0) {
					cardReaderTextArea.appendText("Removing card\n");
					cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardRemoved, cardNumField.getText()));
				}
				break;

			default:
				log.warning(id + ": unknown button: [" + btn.getText() + "]");
				break;
		}
    } // buttonPressed


    //------------------------------------------------------------
    // updateCardStatus
    public void updateCardStatus(String status) {
	cardStatusField.setText(status);
    } // updateCardStatus


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
	cardReaderTextArea.appendText(status+"\n");
    } // appendTextArea


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
} // CardReaderEmulatorController
