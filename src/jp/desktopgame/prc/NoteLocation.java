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
public class NoteLocation {

    public final Note note;
    public final int globalPos;

    public NoteLocation(Note note, int globalPos) {
        this.note = note;
        this.globalPos = globalPos;
    }
}
