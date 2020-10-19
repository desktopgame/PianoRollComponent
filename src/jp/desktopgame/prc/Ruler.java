/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * Rularはピアノロールの再生中の位置を変更するためのコンポーネントです.
 *
 * @author desktopgame
 */
public class Ruler extends JComponent {

    private PianoRoll pianoRoll;
    private PianoRollLayerUI pianoRollLayerUI;
    private JScrollPane scrollPane;
    private Keyboard keyboard;
    private boolean seekable;

    private static final String uiClassID = "RulerUI";

    public Ruler(PianoRoll pianoRoll, PianoRollLayerUI pianoRollLayerUI, JScrollPane scrollPane, Keyboard keyboard) {
        super();
        this.pianoRoll = pianoRoll;
        this.pianoRollLayerUI = pianoRollLayerUI;
        this.scrollPane = scrollPane;
        this.keyboard = keyboard;
        setSeekable(true);
        updateUI();
    }

    public void setUI(RulerUI ui) {
        super.setUI(ui);
    }

    public RulerUI getUI() {
        return (RulerUI) ui;
    }

    @Override
    public void updateUI() {
        setUI(new BasicRulerUI());
    }

    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    public PianoRoll getPianoRoll() {
        return pianoRoll;
    }

    public PianoRollLayerUI getPianoRollLayerUI() {
        return pianoRollLayerUI;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    public void setSeekable(boolean seekable) {
        boolean a = this.seekable;
        this.seekable = seekable;
        firePropertyChange("seekable", a, seekable);
    }

    public boolean isSeekable() {
        return seekable;
    }
}
