/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.util.EventListener;

/**
 *
 * @author desktopgame
 */
public interface NoteResizeListener extends EventListener {

    public void resizeStart(NoteResizeEvent e);

    public void resizeEnd(NoteResizeEvent e);
}
