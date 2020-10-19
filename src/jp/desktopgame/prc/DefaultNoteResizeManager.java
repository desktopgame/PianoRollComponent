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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.event.EventListenerList;

/**
 *
 * @author desktopgame
 */
public class DefaultNoteResizeManager implements NoteResizeManager {

    private Type type;
    private List<Note> notes;
    private int[] offsetTable;
    private float[] lengthTable;
    private int baseX;
    private int currentX;
    private boolean hasFocus;
    private EventListenerList listenerList;

    public DefaultNoteResizeManager() {
        this.notes = new ArrayList<>();
        this.hasFocus = false;
        this.listenerList = new EventListenerList();
    }

    @Override
    public void addNoteResizeListener(NoteResizeListener listener) {
        listenerList.add(NoteResizeListener.class, listener);
    }

    @Override
    public void removeNoteResizeListener(NoteResizeListener listener) {
        listenerList.remove(NoteResizeListener.class, listener);
    }

    @Override
    public void touch(Note note) {
        this.notes.add(note);
    }

    @Override
    public void touch(Collection<? extends Note> notes) {
        this.notes.addAll(notes);
    }

    @Override
    public void clear() {
        notes.clear();
    }

    @Override
    public void start(Type type, int x) {
        this.notes = notes.stream().distinct().collect(Collectors.toList());
        this.type = type;
        this.baseX = x;
        this.hasFocus = true;
        this.offsetTable = (notes.stream().mapToInt((e) -> e.getOffset()).toArray());
        this.lengthTable = doubleArrayToFloatArray(notes.stream().mapToDouble((e) -> e.getLength()).toArray());
        NoteResizeEvent e = new NoteResizeEvent(this);
        for (NoteResizeListener listener : listenerList.getListeners(NoteResizeListener.class)) {
            listener.resizeStart(e);
        }
    }

    private static float[] doubleArrayToFloatArray(double[] d) {
        float[] r = new float[d.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = (float) d[i];
        }
        return r;
    }

    @Override
    public void resize(int x, int beatWidth) {
        this.currentX = x;
        int diffX = currentX - baseX;
        float addX = ((float) diffX / (float) beatWidth);
        for (int i = 0; i < notes.size(); i++) {
            Note n = notes.get(i);
            if (type == Type.Resize) {
                float newLength = Math.max(0.1f, lengthTable[i] + addX);
                n.setLength(newLength);
            } else {
                if (lengthTable[i] - addX > 0.1f) {
                    int newOffset = (int) offsetTable[i] + diffX;
                    n.setOffset(newOffset);
                    n.setLength(Math.max(0.1f, lengthTable[i] - addX));
                }
            }
        }
    }

    @Override
    public void stop() {
        this.notes.clear();
        this.baseX = 0;
        this.currentX = 0;
        this.hasFocus = false;
        NoteResizeEvent e = new NoteResizeEvent(this);
        for (NoteResizeListener listener : listenerList.getListeners(NoteResizeListener.class)) {
            listener.resizeEnd(e);
        }
    }

    @Override
    public List<Note> getTargets() {
        return notes;
    }

    @Override
    public int getCurrentX() {
        return currentX;
    }

    @Override
    public int getBaseX() {
        return baseX;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean hasFocus() {
        return hasFocus;
    }
}
