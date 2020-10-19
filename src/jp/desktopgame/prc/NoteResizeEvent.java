/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.util.EventObject;

/**
 *
 * @author desktopgame
 */
public class NoteResizeEvent extends EventObject {

    public NoteResizeEvent(NoteResizeManager manager) {
        super(manager);
    }

    @Override
    public NoteResizeManager getSource() {
        return (NoteResizeManager) super.getSource();
    }
}
