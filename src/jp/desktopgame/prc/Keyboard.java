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
    private static final String uiClassID = "KeyboardUI";

    public Keyboard(PianoRoll pianoRoll, JScrollPane scrollPane) {
        this.pianoRoll = pianoRoll;
        this.scrollPane = scrollPane;
        this.setPreferredSize(new Dimension(48, pianoRoll.getBeatHeight()));
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

    public JScrollPane getPianoRollScrollPane() {
        return this.scrollPane;
    }
}
