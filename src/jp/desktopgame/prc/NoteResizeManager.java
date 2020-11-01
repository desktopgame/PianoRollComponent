/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.util.List;

/**
 *
 * @author desktopgame
 */
public interface NoteResizeManager extends NoteEditManager {

    public enum Type {
        Resize,
        Move
    }

    public void addNoteResizeListener(NoteResizeListener listener);

    public void removeNoteResizeListener(NoteResizeListener listener);

    public void start(Type type, int x);

    public void resize(int x, int beatWidth);

    public void stop();

    public Note getBaseNote();

    public List<Note> getTargets();

    public int getCurrentX();

    public int getBaseX();

    public Type getType();

    public boolean hasFocus();
}
