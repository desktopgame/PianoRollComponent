/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.awt.Rectangle;
import java.util.List;
import java.util.Optional;
import javax.swing.plaf.ComponentUI;

/**
 * PianoRollUI用のプラグイン可能なLook & Feelインターフェイスです.
 *
 * @author desktopgame
 */
public abstract class PianoRollUI extends ComponentUI {

    public abstract int computeWidth();

    public abstract int computeHeight();

    public abstract Optional<Measure> getMeasureAt(int x, int y);

    public abstract List<Note> getNotesAt(int x, int y);

    public abstract int getRelativeBeatIndex(int x);

    public abstract int measureIndexToXOffset(int i);

    public abstract Rectangle getNoteRect(Note note);

    public abstract PianoRoll getPianoRoll();

    public abstract NoteDragManager getNoteDragManager();

    public abstract NoteResizeManager getNoteResizeManager();
}
