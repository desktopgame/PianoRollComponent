/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

/**
 *
 * @author desktopgame
 */
public interface Note {

    public void addNoteChangeListener(NoteListener listener);

    public void removeNoteChangeListener(NoteListener listener);

    public void play();

    public void removeFromBeat();

    public void setSelected(boolean selected);

    public boolean isSelected();

    public void setOffset(int offset);

    public int getOffset();

    public void setLength(float length);

    public float getLength();

    public void setTrigger(boolean isTrigger);

    public boolean isTrigger();

    public Beat getBeat();

    public default int scaledLength(int i) {
        float f = (float) i;
        return (int) Math.round(f * getLength());
    }
}
