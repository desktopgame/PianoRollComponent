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
public class PianoRollModelEvent extends EventObject {

    private PianoRollModelEventType type;
    private Optional<KeyEvent> innerEvent;

    public PianoRollModelEvent(PianoRollModel o, PianoRollModelEventType type, KeyEvent innerEvent) {
        super(o);
        this.type = type;
        this.innerEvent = Optional.ofNullable(innerEvent);
    }

    @Override
    public PianoRollModel getSource() {
        return (PianoRollModel) super.getSource();
    }

    public PianoRollModelEventType getType() {
        return type;
    }

    public Optional<KeyEvent> getInnerEvent() {
        return innerEvent;
    }

    public Optional<MeasureEvent> getMeasureEvent() {
        if (!innerEvent.isPresent()) {
            return Optional.empty();
        }
        KeyEvent k = innerEvent.get();
        return k.getInnerEvent();
    }

    public Optional<BeatEvent> getBeatEvent() {
        Optional<MeasureEvent> m = getMeasureEvent();
        if (!m.isPresent()) {
            return Optional.empty();
        }
        MeasureEvent b = m.get();
        return b.getInnerEvent();
    }

    public Optional<NoteEvent> getNoteEvent() {
        Optional<BeatEvent> b = getBeatEvent();
        if (!b.isPresent()) {
            return Optional.empty();
        }
        BeatEvent be = b.get();
        return be.getInnerEvent();
    }
}
