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
import javax.swing.event.EventListenerList;

/**
 *
 * @author desktopgame
 */
public class DefaultKey implements Key {

    private PianoRollModel model;
    private ArrayList<Measure> measureList;
    private EventListenerList listenerList;
    private int height;
    private int beatCount;

    public DefaultKey(PianoRollModel model, int measureCount, int beatCount, int height) {
        this.model = model;
        this.measureList = new ArrayList<>();
        this.listenerList = new EventListenerList();
        this.height = height;
        this.beatCount = beatCount;
        for (int i = 0; i < measureCount; i++) {
            addMeasure(i);
        }
    }

    private void addMeasure(int index) {
        Measure m = createMeasure(index, beatCount);
        measureList.add(m);
        m.addMeasureListener(this::measureUpdate);
        KeyEvent ee = new KeyEvent(this, KeyEventType.MEASURE_CREATED, null);
        for (KeyListener listener : listenerList.getListeners(KeyListener.class)) {
            listener.keyUpdate(ee);
        }
    }

    protected Measure createMeasure(int index, int beatCount) {
        return new DefaultMeasure(this, index, beatCount);
    }

    private void measureUpdate(MeasureEvent e) {
        KeyEvent ee = new KeyEvent(this, KeyEventType.PROPAGATION_MEASURE_EVENTS, e);
        for (KeyListener listener : listenerList.getListeners(KeyListener.class)) {
            listener.keyUpdate(ee);
        }
    }

    @Override
    public void addKeyListener(KeyListener listener) {
        listenerList.add(KeyListener.class, listener);
    }

    @Override
    public void removeKeyListener(KeyListener listener
    ) {
        listenerList.remove(KeyListener.class, listener);
    }

    @Override
    public void extentMeasureCount(int measureCount
    ) {
        int start = measureList.size();
        while (measureList.size() < measureCount) {
            addMeasure(start);
            start++;
        }
    }

    @Override
    public void shrinkMeasureCount(int measureCount) {
        while (measureCount != measureList.size()) {
            measureList.remove(measureList.size() - 1);
            KeyEvent ee = new KeyEvent(this, KeyEventType.MEASURE_REMOVED, null);
            for (KeyListener listener : listenerList.getListeners(KeyListener.class)) {
                listener.keyUpdate(ee);
            }
        }
    }

    @Override
    public Measure getMeasure(int i
    ) {
        return measureList.get(i);
    }

    @Override
    public int getMeasureCount() {
        return measureList.size();
    }

    @Override
    public int getIndex() {
        return height;
    }

    @Override
    public PianoRollModel getModel() {
        return model;
    }

    @Override
    public void extentBeatCount(int beatCount) {
        for (Measure m : measureList) {
            m.extentBeatCount(beatCount);
        }
        this.beatCount = beatCount;
    }

    @Override
    public void shrinkBeatCount(int beatCount) {
        for (Measure m : measureList) {
            m.shrinkBeatCount(beatCount);
        }
        this.beatCount = beatCount;
    }
}
