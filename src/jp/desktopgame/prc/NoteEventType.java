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
public enum NoteEventType {
    CREATED,
    REMOVED,
    TRIGGER_CHANGE,
    OFFSET_CHANGE,
    LENGTH_CHANGE,
    SELECTION_CHANGE,
    PLAY,
}
