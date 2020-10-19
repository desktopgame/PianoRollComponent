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
import java.util.Optional;

/**
 *
 * @author desktopgame
 */
public class NoteEvent extends EventObject {

    private NoteEventType type;
    private Optional<Object> oldValue;
    private Optional<Object> newValue;

    public NoteEvent(Note o, NoteEventType type, Object oldValue, Object newValue) {
        super(o);
        this.type = type;
        this.oldValue = Optional.ofNullable(oldValue);
        this.newValue = Optional.ofNullable(newValue);
    }

    public NoteEvent(Note o, NoteEventType type) {
        this(o, type, null, null);
    }

    @Override
    public Note getSource() {
        return (Note) super.getSource();
    }

    public NoteEventType getType() {
        return type;
    }

    public Optional<Object> getOldValue() {
        return oldValue;
    }

    public Optional<Object> getNewValue() {
        return newValue;
    }
}
