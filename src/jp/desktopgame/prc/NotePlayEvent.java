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
public class NotePlayEvent extends EventObject {

    private Note note;
    private NotePlayEventType type;

    public NotePlayEvent(PianoRollLayerUI source, Note note, NotePlayEventType type) {
        super(source);
        this.note = note;
        this.type = type;
    }

    @Override
    public PianoRollLayerUI getSource() {
        return (PianoRollLayerUI) super.getSource(); //To change body of generated methods, choose Tools | Templates.
    }

    public Note getNote() {
        return note;
    }

    public NotePlayEventType getType() {
        return type;
    }

}
