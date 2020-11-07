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
        this.drumMap = new HashMap<>(MIDI.DRUM_MAP);
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

    public void setHighlight(int height, boolean b) {
        getUI().setHighlight(height, b);
    }

    public boolean isHighlight(int height) {
        return getUI().isHighlight(height);
    }

    public void resetHighlight() {
        getUI().resetHighlight();
    }
}
