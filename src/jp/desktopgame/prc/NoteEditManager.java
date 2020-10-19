/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.util.Collection;

/**
 *
 * @author desktopgame
 */
public interface NoteEditManager {

    public void touch(Note note);

    public void touch(Collection<? extends Note> notes);

    public void clear();
}
