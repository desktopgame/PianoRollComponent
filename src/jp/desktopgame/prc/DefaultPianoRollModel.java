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
import java.util.Optional;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEditSupport;
import jp.desktopgame.prc.undo.NoteCreateEdit;
import jp.desktopgame.prc.undo.NoteLengthEdit;
import jp.desktopgame.prc.undo.NoteOffsetEdit;
import jp.desktopgame.prc.undo.NoteRemoveEdit;
import jp.desktopgame.prc.undo.NoteSelectEdit;

/**
 *
 * @author desktopgame
 */
public class DefaultPianoRollModel implements PianoRollModel {

    private ArrayList<Key> keyList;
    private EventListenerList listenerList;
    private int measureCount;
    private int beatCount;
    private UndoableEditSupport ues;
    private int undoableEditStack;

    public DefaultPianoRollModel(int keyCount, int measureCount, int beatCount) {
        this.keyList = new ArrayList<>();
        this.listenerList = new EventListenerList();
        this.measureCount = measureCount;
        this.beatCount = beatCount;
        this.ues = new UndoableEditSupport();
        for (int i = 0; i < keyCount; i++) {
            addKey(i);
        }
    }

    private void addKey(int height) {
        Key k = createKey(measureCount, beatCount, height);
        keyList.add(k);
        k.addKeyListener(this::keyUpdate);
        PianoRollModelEvent ee = new PianoRollModelEvent(this, PianoRollModelEventType.KEY_CREATED, null);
        postUndoableEdit(ee);
        for (PianoRollModelListener listener : listenerList.getListeners(PianoRollModelListener.class)) {
            listener.pianoRollModelUpdate(ee);
        }
    }

    protected Key createKey(int measureCount, int beatCount, int height) {
        return new DefaultKey(this, measureCount, beatCount, height + 1);
    }

    private void keyUpdate(KeyEvent e) {
        PianoRollModelEvent ee = new PianoRollModelEvent(this, PianoRollModelEventType.PROPAGATION_KEY_EVENTS, e);
        postUndoableEdit(ee);
        for (PianoRollModelListener listener : listenerList.getListeners(PianoRollModelListener.class)) {
            listener.pianoRollModelUpdate(ee);
        }
    }

    private void postUndoableEdit(PianoRollModelEvent e) {
        if (undoableEditStack > 0) {
            return;
        }
        Optional<BeatEvent> beOpt = e.getBeatEvent();
        if (beOpt.isPresent()) {
            BeatEvent be = beOpt.get();
            if (be.getBeatEventType() == BeatEventType.NOTE_CREATED) {
                ues.postEdit(new NoteCreateEdit(this, be.getSource(), be.getNote()));
                return;
            }
        }
        Optional<NoteEvent> noOpt = e.getNoteEvent();
        if (noOpt.isPresent()) {
            NoteEvent no = noOpt.get();
            Optional<Object> o = no.getOldValue();
            Optional<Object> n = no.getNewValue();
            if (no.getType() == NoteEventType.REMOVED) {
                ues.postEdit(new NoteRemoveEdit(this, no.getSource().getBeat(), no.getSource()));
            } else if (no.getType() == NoteEventType.OFFSET_CHANGE) {
                ues.postEdit(new NoteOffsetEdit(this, no.getSource(), (int) o.get()));
            } else if (no.getType() == NoteEventType.LENGTH_CHANGE) {
                ues.postEdit(new NoteLengthEdit(this, no.getSource(), (float) o.get()));
            } else if (no.getType() == NoteEventType.SELECTION_CHANGE) {
                ues.postEdit(new NoteSelectEdit(this, no.getSource()));
            }
            return;
        }
    }

    @Override
    public void addUndoableEditListener(UndoableEditListener listener) {
        ues.addUndoableEditListener(listener);
    }

    @Override
    public void removeUndoableEditListener(UndoableEditListener listener) {
        ues.removeUndoableEditListener(listener);
    }

    @Override
    public void addPianoRollModelListener(PianoRollModelListener listener) {
        listenerList.add(PianoRollModelListener.class, listener);
    }

    @Override
    public void removePianoRollModelListener(PianoRollModelListener listener) {
        listenerList.remove(PianoRollModelListener.class, listener);
    }

    @Override
    public void extentKeyCount(int keyCount) {
        int start = keyList.size();
        while (keyList.size() < keyCount) {
            addKey(start);
            start++;
        }
    }

    @Override
    public void shrinkKeyCount(int keyCount) {
        while (keyCount != keyList.size()) {
            keyList.remove(keyList.size() - 1);
            PianoRollModelEvent ee = new PianoRollModelEvent(this, PianoRollModelEventType.KEY_CREATED, null);
            postUndoableEdit(ee);
            for (PianoRollModelListener listener : listenerList.getListeners(PianoRollModelListener.class)) {
                listener.pianoRollModelUpdate(ee);
            }
        }
    }

    @Override
    public void extentMeasureCount(int measureCount) {
        for (Key k : keyList) {
            k.extentMeasureCount(measureCount);
        }
        this.measureCount = measureCount;
    }

    @Override
    public void shrinkMeasureCount(int measureCount) {
        for (Key k : keyList) {
            k.shrinkMeasureCount(measureCount);
        }
        this.measureCount = measureCount;
    }

    @Override
    public void extentBeatCount(int beatCount) {
        for (Key k : keyList) {
            k.extentBeatCount(beatCount);
        }
        this.beatCount = beatCount;
    }

    @Override
    public void shrinkBeatCount(int beatCount) {
        for (Key k : keyList) {
            k.shrinkBeatCount(beatCount);
        }
        this.beatCount = beatCount;
    }

    @Override
    public void resizeKeyCount(int keyCount) {
        if (getKeyCount() < keyCount) {
            extentKeyCount(keyCount);
        } else {
            shrinkKeyCount(keyCount);
        }
    }

    @Override
    public void resizeMeasureCount(int measureCount) {
        for (Key k : keyList) {
            if (k.getMeasureCount() < measureCount) {
                k.extentMeasureCount(measureCount);
            } else {
                k.shrinkMeasureCount(measureCount);
            }
        }
    }

    @Override
    public void resizeBeatCount(int beatCount) {
        for (Key k : keyList) {
            for (int i = 0; i < k.getMeasureCount(); i++) {
                Measure m = k.getMeasure(i);
                if (m.getBeatCount() < beatCount) {
                    m.extentBeatCount(beatCount);
                } else {
                    m.shrinkBeatCount(beatCount);
                }
            }
        }
        this.beatCount = beatCount;
    }

    @Override
    public void beginApplyUndoableEdit() {
        this.undoableEditStack++;
    }

    @Override
    public void endApplyUndoableEdit() {
        this.undoableEditStack--;
    }

    @Override
    public void beginCompoundUndoableEdit() {
        ues.beginUpdate();
    }

    @Override
    public void endCompoundUndoableEdit() {
        ues.endUpdate();
    }

    @Override
    public int getKeyHeight(int keyIndex) {
        return keyList.size() - keyIndex;
    }

    @Override
    public Key getKey(int i
    ) {
        return keyList.get(i);
    }

    @Override
    public int getKeyCount() {
        return keyList.size();
    }

}
