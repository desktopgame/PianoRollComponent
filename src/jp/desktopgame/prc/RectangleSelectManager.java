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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author desktopgame
 */
public class RectangleSelectManager {

    private PianoRollUI ui;
    private int baseX;
    private int baseY;
    private int currentX;
    private int currentY;
    private List<Note> notes;

    public RectangleSelectManager(PianoRollUI ui) {
        this.ui = ui;
        this.notes = new ArrayList<>();
    }

    public void start(int x, int y) {
        this.baseX = this.currentX = x;
        this.baseY = this.currentY = y;
    }

    public void move(int x, int y) {
        this.currentX = x;
        this.currentY = y;
        Rectangle area = getAreaRect();
        notes.stream().filter((e) -> !ui.getNoteRect(e).intersects(area)).forEach((n) -> n.setSelected(false));
        notes.removeIf((e) -> !ui.getNoteRect(e).intersects(area));
        ui.getPianoRoll().getModel().getAllNotes().forEach(((e) -> {
            if (notes.contains(e)) {
                return;
            }
            Rectangle noteRect = ui.getNoteRect(e);
            if (area.intersects(noteRect)) {
                notes.add(e);
                e.setSelected(true);
            }
        }));
    }

    public void stop() {
        notes.clear();
        this.baseX = this.baseY = this.currentX = this.currentY = 0;
    }

    public Rectangle getAreaRect() {
        int minX = Math.min(baseX, currentX);
        int minY = Math.min(baseY, currentY);
        int maxX = Math.max(baseX, currentX);
        int maxY = Math.max(baseY, currentY);
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

}
