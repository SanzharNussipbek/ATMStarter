package ATMSS.BuzzerHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.BuzzerHandler.BuzzerHandler;
import javafx.stage.Stage;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Buzzer Emulator for the Buzzer hardware of ATM class
 * Extends BuzzerHandler class
 */
public class BuzzerEmulator extends BuzzerHandler {
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
     * Constructor of the class
     * @param id ID of the current thread
     * @param atmssStarter ATMSSStarter instance
     */
    public BuzzerEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
    } // BuzzerEmulator


    /**
     * Mock function to start the emulator
     */
    public void start() { } // start


    /**
     * Play the audio according to the received audio name
     * @param audio Audio File name
     */
    public void play(String audio) {
        log.info(id + ": play sound: [" + audio + "]");
        String sound = "etc\\sounds\\" + audio + ".wav";
        playSound(sound);
    } // play


    /**
     * Perform sound playning from the audio file
     * @param audiofile Audio file where the sound is to be extracted from
     */
    private void playSound(String audiofile) {
        try {
            InputStream in = new FileInputStream(audiofile);
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // playSound
}
