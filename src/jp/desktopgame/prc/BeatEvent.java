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
public class BeatEvent extends EventObject {

    private Note note;
    private BeatEventType beatEventType;
    private Optional<NoteEvent> innerEvent;

    public BeatEvent(Beat o, Note note, BeatEventType beatEventType, NoteEvent noteEvent) {
        super(o);
        this.note = note;
        this.beatEventType = beatEventType;
        this.innerEvent = Optional.ofNullable(noteEvent);
    }

    @Override
    public Beat getSource() {
        return (Beat) super.getSource();
    }

    public Note getNote() {
        return note;
    }

    public BeatEventType getBeatEventType() {
        return beatEventType;
    }

    public Optional<NoteEvent> getInnerEvent() {
        return innerEvent;
    }
}
