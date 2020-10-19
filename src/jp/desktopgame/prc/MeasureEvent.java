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
public class MeasureEvent extends EventObject {

    private MeasureEventType type;
    private Optional<BeatEvent> innerEvent;

    public MeasureEvent(Measure o, MeasureEventType type, BeatEvent innerEvent) {
        super(o);
        this.type = type;
        this.innerEvent = Optional.ofNullable(innerEvent);
    }

    @Override
    public Measure getSource() {
        return (Measure) super.getSource();
    }

    public MeasureEventType getType() {
        return type;
    }

    public Optional<BeatEvent> getInnerEvent() {
        return innerEvent;
    }
}
