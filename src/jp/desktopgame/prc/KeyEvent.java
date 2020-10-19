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
public class KeyEvent extends EventObject {

    private KeyEventType type;
    private Optional<MeasureEvent> innerEvent;

    public KeyEvent(Key o, KeyEventType type, MeasureEvent innerEvent) {
        super(o);
        this.type = type;
        this.innerEvent = Optional.ofNullable(innerEvent);
    }

    @Override
    public Key getSource() {
        return (Key) super.getSource();
    }

    public KeyEventType getType() {
        return type;
    }

    public Optional<MeasureEvent> getInnerEvent() {
        return innerEvent;
    }
}
