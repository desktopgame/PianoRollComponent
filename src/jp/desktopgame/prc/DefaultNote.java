/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import javax.swing.event.EventListenerList;

/**
 *
 * @author desktopgame
 */
public class DefaultNote implements Note {

    private Beat beat;
    private EventListenerList listenerList;
    private boolean isTrigger;
    private boolean selected;
    private int offset;
    private float length;

    public DefaultNote(Beat beat, int offset, float length) {
        this.beat = beat;
        this.listenerList = new EventListenerList();
        this.isTrigger = true;
        this.selected = false;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void addNoteChangeListener(NoteListener listener) {
        listenerList.add(NoteListener.class, listener);
    }

    @Override
    public void removeNoteChangeListener(NoteListener listener) {
        listenerList.remove(NoteListener.class, listener);
    }

    @Override
    public void setSelected(boolean selected) {
        if (this.selected == selected) {
            return;
        }
        this.selected = selected;
        NoteEvent e = new NoteEvent(this, NoteEventType.SELECTION_CHANGE, !selected, selected);
        for (NoteListener listener : listenerList.getListeners(NoteListener.class)) {
            listener.noteChange(e);
        }
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setOffset(int offset) {
        int old = this.offset;
        this.offset = offset;
        NoteEvent e = new NoteEvent(this, NoteEventType.OFFSET_CHANGE, old, offset);
        for (NoteListener listener : listenerList.getListeners(NoteListener.class)) {
            listener.noteChange(e);
        }
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setLength(float length) {
        float len = length;
        this.length = length;
        NoteEvent e = new NoteEvent(this, NoteEventType.LENGTH_CHANGE, len, length);
        for (NoteListener listener : listenerList.getListeners(NoteListener.class)) {
            listener.noteChange(e);
        }
    }

    @Override
    public float getLength() {
        return length;
    }

    @Override
    public void play() {
        NoteEvent e = new NoteEvent(this, NoteEventType.PLAY, null, null);
        for (NoteListener listener : listenerList.getListeners(NoteListener.class)) {
            listener.noteChange(e);
        }
    }

    @Override
    public void removeFromBeat() {
        NoteEvent e = new NoteEvent(this, NoteEventType.REMOVED);
        for (NoteListener listener : listenerList.getListeners(NoteListener.class)) {
            listener.noteChange(e);
        }
    }

    @Override
    public void setTrigger(boolean isTrigger) {
        if (this.isTrigger == isTrigger) {
            return;
        }
        this.isTrigger = isTrigger;
        NoteEvent e = new NoteEvent(this, NoteEventType.TRIGGER_CHANGE, !isTrigger, isTrigger);
        for (NoteListener listener : listenerList.getListeners(NoteListener.class)) {
            listener.noteChange(e);
        }
    }

    @Override
    public boolean isTrigger() {
        return isTrigger;
    }

    @Override
    public Beat getBeat() {
        return beat;
    }
}
