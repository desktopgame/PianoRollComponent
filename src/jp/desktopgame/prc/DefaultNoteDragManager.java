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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.event.EventListenerList;

/**
 *
 * @author desktopgame
 */
public class DefaultNoteDragManager implements NoteDragManager {

    private PianoRollUI ui;
    private List<Note> dragTargets;
    private int baseX;
    private int baseY;
    private int currentX;
    private int currentY;
    private boolean hasFocus;
    private EventListenerList listenerList;
    private Note baseNote;

    public DefaultNoteDragManager(PianoRollUI ui) {
        this.ui = ui;
        this.dragTargets = new ArrayList<>();
        this.listenerList = new EventListenerList();
    }

    @Override
    public void addNoteDragListener(NoteDragListener listener) {
        listenerList.add(NoteDragListener.class, listener);
    }

    @Override
    public void removeNoteDragListener(NoteDragListener listener) {
        listenerList.remove(NoteDragListener.class, listener);
    }

    @Override
    public void touch(Note note) {
        this.dragTargets.add(note);
        this.baseNote = dragTargets.get(dragTargets.size() - 1);
    }

    @Override
    public void touch(Collection<? extends Note> notes) {
        this.dragTargets.addAll(notes);
        if (!dragTargets.isEmpty()) {
            this.baseNote = dragTargets.get(dragTargets.size() - 1);
        }
    }

    @Override
    public void clear() {
        dragTargets.clear();
    }

    @Override
    public void start(int baseX, int baseY) {
        this.dragTargets = dragTargets.stream().distinct().collect(Collectors.toList());
        this.baseX = baseX;
        this.baseY = baseY;
        this.currentX = baseX;
        this.currentY = baseY;
        this.hasFocus = true;
        NoteDragEvent e = new NoteDragEvent(this);
        for (NoteDragListener listener : listenerList.getListeners(NoteDragListener.class)) {
            listener.dragStart(e);
        }
    }

    @Override
    public void move(int x, int y) {
        this.currentX = Math.max(x, 0);
        this.currentY = Math.max(y, 0);
    }

    @Override
    public void stop() {
        int diffX = (this.getCurrentX() - this.getBaseX());
        int diffY = (this.getCurrentY() - this.getBaseY());
        for (Note note : this.getTargets()) {
            Rectangle rect = ui.getNoteRect(note);
            rect.x += diffX;
            rect.y += diffY;
            Optional<Measure> measureOpt = ui.getMeasureAt(rect.x, rect.y);
            measureOpt.ifPresent((Measure measure) -> {
                int xOffset = ui.measureIndexToXOffset(measure.getIndex());
                xOffset += ui.getPianoRoll().getBeatWidth() * ui.getRelativeBeatIndex(rect.x);
                measure.getBeat(ui.getRelativeBeatIndex(rect.x)).generateNote(rect.x - xOffset, note.getLength());
            });
            if (measureOpt.isPresent()) {
                note.removeFromBeat();
            }
        }
        dragTargets.clear();
        this.hasFocus = false;
        this.baseNote = null;
        NoteDragEvent e = new NoteDragEvent(this);
        for (NoteDragListener listener : listenerList.getListeners(NoteDragListener.class)) {
            listener.dragEnd(e);
        }
    }

    @Override
    public Note getBaseNote() {
        return baseNote;
    }

    @Override
    public List<Note> getTargets() {
        return dragTargets;
    }

    @Override
    public boolean hasFocus() {
        return hasFocus;
    }

    @Override
    public int getBaseX() {
        return baseX;
    }

    @Override
    public int getBaseY() {
        return baseY;
    }

    @Override
    public int getCurrentX() {
        return currentX;
    }

    @Override
    public int getCurrentY() {
        return currentY;
    }
}
