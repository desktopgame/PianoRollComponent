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
public class SequenceEvent extends EventObject {

    public enum Type {
        Reset,
        Move
    }

    private int oldPosition;
    private int newPosition;
    private Type type;

    public SequenceEvent(PianoRollLayerUI ui, int oldPosition, int newPosition, Type type) {
        super(ui);
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
        this.type = type;
    }

    @Override
    public PianoRollLayerUI getSource() {
        return (PianoRollLayerUI) super.getSource(); //To change body of generated methods, choose Tools | Templates.
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public int getNewPosition() {
        return newPosition;
    }

    public Type getType() {
        return type;
    }
}
