package ATMSS.BuzzerHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.BuzzerHandler.BuzzerHandler;
import ATMSS.CollectorHandler.Emulator.CollectorEmulator;
import ATMSS.CollectorHandler.Emulator.CollectorEmulatorController;
import ATMSS.TouchDisplayHandler.Emulator.TouchDisplayEmulator;
import ATMSS.TouchDisplayHandler.Emulator.TouchDisplayEmulatorController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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


    //------------------------------------------------------------
    // start
    public void start() throws Exception {

    } // start


    //------------------------------------------------------------
    // play
    public void play(String audio) {
        log.info(id + ": play sound: [" + audio + "]");
        String audiofile = "etc\\sounds\\" + audio + ".wav";
        playSound(audiofile);
    } // play


    //------------------------------------------------------------
    // playSound
    private void playSound(String audiofile) {
        InputStream in = null;
        try {
            in = new FileInputStream(audiofile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // create an audiostream from the inputstream
        AudioStream audioStream = null;
        try {
            audioStream = new AudioStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // play the audio clip with the audioplayer class
        AudioPlayer.player.start(audioStream);
    } // playSound
}
