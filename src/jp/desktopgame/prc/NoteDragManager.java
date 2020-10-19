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
public interface NoteDragManager extends NoteEditManager {

    public void addNoteDragListener(NoteDragListener listener);

    public void removeNoteDragListener(NoteDragListener listener);

    public void start(int baseX, int baseY);

    public void move(int x, int y);

    public void stop();

    public List<Note> getTargets();

    public boolean hasFocus();

    public int getBaseX();

    public int getBaseY();

    public int getCurrentX();

    public int getCurrentY();
}
