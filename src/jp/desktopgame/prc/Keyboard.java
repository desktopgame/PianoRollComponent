/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * キーボードの白鍵と黒鍵をPianoRollと対応する形で表示するコンポーネントです.
 *
 * @author desktopgame
 */
public class Keyboard extends JComponent {

    public static int[] BLACK_WHITE_TABLE = new int[]{
        Key.WHITE,
        Key.BLACK,
        Key.WHITE,
        Key.BLACK,
        Key.WHITE,
        Key.WHITE,
        Key.BLACK,
        Key.WHITE,
        Key.BLACK,
        Key.WHITE,
        Key.BLACK,
        Key.WHITE,};

    public static String[] KEY_STRING_TABLE = new String[]{
        "C",
        "C#",
        "D",
        "D#",
        "E",
        "F",
        "F#",
        "G",
        "G#",
        "A",
        "A#",
        "B",};

    private PianoRoll pianoRoll;
    private JScrollPane scrollPane;
    private Map<Integer, String> drumMap;
    private boolean useDrumMap;
    private static final String uiClassID = "KeyboardUI";

    public Keyboard(PianoRoll pianoRoll, JScrollPane scrollPane) {
        this.pianoRoll = pianoRoll;
        this.scrollPane = scrollPane;
        this.setPreferredSize(new Dimension(96, pianoRoll.getBeatHeight()));
        this.drumMap = new HashMap<>();
        drumMap.put(35, "Acoustic Bass Drum");
        drumMap.put(36, "Bass Drum 1");
        drumMap.put(37, "Side Stick");
        drumMap.put(38, "Acoustic Snare");
        drumMap.put(39, "Hand Clap");
        drumMap.put(40, "Electric Snare");
        drumMap.put(41, "Low Floor Tom");
        drumMap.put(43, "High Floor Tom");
        drumMap.put(45, "Low Tom");
        drumMap.put(47, "Low Mid Tom");
        drumMap.put(48, "High Mid Tom");
        drumMap.put(50, "High Tom");
        drumMap.put(42, "Closed Hi-Hat");
        drumMap.put(44, "Pedal Hi-Hat");
        drumMap.put(46, "Open Hi-Hat");
        drumMap.put(49, "Crash Cymbal 1");
        drumMap.put(51, "Ride Cymbal 1");
        drumMap.put(51, "Chinese Cymbal");
        drumMap.put(53, "Ride Bell");
        drumMap.put(55, "Splash Cymbal");
        drumMap.put(54, "Tambourine");
        drumMap.put(56, "Cowbell");
        drumMap.put(57, "Crash Cymbal 2");
        drumMap.put(58, "Vibraslap");
        drumMap.put(59, "Ride Cymbal 2");
        drumMap.put(60, "High Bongo");
        drumMap.put(61, "Low Bongo");
        drumMap.put(62, "Mute High Conga");
        drumMap.put(63, "Open High Conga");
        drumMap.put(64, "Low Conga");
        drumMap.put(65, "High Timbale");
        drumMap.put(66, "Low Timbale");
        drumMap.put(67, "High Agogo");
        drumMap.put(68, "Low Agogo");
        drumMap.put(69, "Cabase");
        drumMap.put(70, "Maracas");
        drumMap.put(71, "Short Whistle");
        drumMap.put(72, "Long Whistle");
        drumMap.put(73, "Short Guiro");
        drumMap.put(74, "Long Guiro");
        drumMap.put(75, "Claves");
        drumMap.put(76, "High Wood Block");
        drumMap.put(77, "Low Wood Block");
        drumMap.put(78, "Mute Cuica");
        drumMap.put(79, "Open Cuica");
        drumMap.put(80, "Mute Triangle");
        drumMap.put(81, "Open Triangle");
        this.useDrumMap = false;
        updateUI();
    }

    public void setUI(KeyboardUI ui) {
        super.setUI(ui);
    }

    public KeyboardUI getUI() {
        return (KeyboardUI) ui;
    }

    @Override
    public void updateUI() {
        setUI(new BasicKeyboardUI());
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    public PianoRoll getPianoRoll() {
        return pianoRoll;
    }

    public void setDrumMap(Map<Integer, String> drumMap) {
        Map<Integer, String> a = this.drumMap;
        this.drumMap = drumMap;
        firePropertyChange("drumMap", a, drumMap);
    }

    public Map<Integer, String> getDrumMap() {
        return drumMap;
    }

    public void setUseDrumMap(boolean useDrumMap) {
        boolean a = this.useDrumMap;
        this.useDrumMap = useDrumMap;
        firePropertyChange("useDrumMap", a, useDrumMap);
    }

    public boolean isUseDrumMap() {
        return useDrumMap;
    }

    public JScrollPane getPianoRollScrollPane() {
        return this.scrollPane;
    }
}
