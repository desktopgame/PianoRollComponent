/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.util.ArrayList;
import javax.swing.event.EventListenerList;

/**
 *
 * @author desktopgame
 */
public class DefaultBeat implements Beat {

    private Measure measure;
    private int index;
    private ArrayList<Note> noteList;
    private EventListenerList listenerList;

    public DefaultBeat(Measure measure, int index) {
        this.measure = measure;
        this.index = index;
        this.noteList = new ArrayList<>();
        this.listenerList = new EventListenerList();
    }

    @Override
    public void addBeatListener(BeatListener listener) {
        listenerList.add(BeatListener.class, listener);
    }

    @Override
    public void removeBeatListener(BeatListener listener) {
        listenerList.remove(BeatListener.class, listener);
    }

    @Override
    public Note generateNote(int offset, float length) {
        Note note = createNote(offset, length);
        noteList.add(note);
        note.addNoteChangeListener(this::noteChange);
        BeatEvent ee = new BeatEvent(this, note, BeatEventType.NOTE_CREATED, null);
        for (BeatListener listener : listenerList.getListeners(BeatListener.class)) {
            listener.beatUpdate(ee);
        }
        return note;
    }

    @Override
    public void restoreNote(Note note) {
        noteList.add(note);
        BeatEvent ee = new BeatEvent(this, note, BeatEventType.NOTE_CREATED, null);
        for (BeatListener listener : listenerList.getListeners(BeatListener.class)) {
            listener.beatUpdate(ee);
        }
    }

    private void noteChange(NoteEvent e) {
        if (e.getType() == NoteEventType.REMOVED) {
            e.getSource().removeNoteChangeListener(this::noteChange);
            noteList.remove(e.getSource());
        }
        BeatEvent ee = new BeatEvent(this, e.getSource(), BeatEventType.PROPAGATION_NOTE_EVENTS, e);
        for (BeatListener listener : listenerList.getListeners(BeatListener.class)) {
            listener.beatUpdate(ee);
        }
    }

    protected Note createNote(int offset, float length) {
        return new DefaultNote(this, offset, length);
    }

    @Override
    public Note getNote(int i) {
        return noteList.get(i);
    }

    @Override
    public int getNoteCount() {
        return noteList.size();
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Measure getMeasure() {
        return measure;
    }

}
