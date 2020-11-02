/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import javax.swing.plaf.ComponentUI;

/**
 * Keyboard用のプラグイン可能なLook & Feelインターフェイスです.
 *
 * @author desktopgame
 */
public abstract class KeyboardUI extends ComponentUI {

    public abstract void setHighlight(int height, boolean b);

    public abstract boolean isHighlight(int height);

    public abstract void resetHighlight();
}
