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
import java.util.List;

/**
 * 拍を表すインターフェイスです.
 *
 * @author desktopgame
 */
public interface Beat {

    public void addBeatListener(BeatListener listener);

    public void removeBeatListener(BeatListener listener);

    public Note generateNote(int offset, float length);

    public void restoreNote(Note note);

    public Note getNote(int i);

    public int getNoteCount();

    public int getIndex();

    public Measure getMeasure();

    public default List<Note> copyNoteList() {
        ArrayList<Note> notes = new ArrayList<>();
        for (int i = 0; i < getNoteCount(); i++) {
            notes.add(getNote(i));
        }
        return notes;
    }
}
