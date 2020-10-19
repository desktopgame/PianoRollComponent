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
public class DefaultMeasure implements Measure {

    private Key key;
    private int index;
    private ArrayList<Beat> beatList;
    private EventListenerList listenerList;

    public DefaultMeasure(Key key, int index, int beatCount) {
        this.key = key;
        this.index = index;
        this.beatList = new ArrayList<>();
        this.listenerList = new EventListenerList();
        for (int i = 0; i < beatCount; i++) {
            addBeat(i);
        }
    }

    private void addBeat(int index) {
        Beat b = createBeat(index);
        beatList.add(b);
        b.addBeatListener(this::beatUpdate);

        MeasureEvent ee = new MeasureEvent(this, MeasureEventType.BEAT_CREATED, null);
        for (MeasureListener listener : listenerList.getListeners(MeasureListener.class)) {
            listener.measureUpdate(ee);
        }
    }

    protected Beat createBeat(int index) {
        return new DefaultBeat(this, index);
    }

    private void beatUpdate(BeatEvent e) {
        MeasureEvent ee = new MeasureEvent(this, MeasureEventType.PROPAGATION_BEAT_EVENTS, e);
        for (MeasureListener listener : listenerList.getListeners(MeasureListener.class)) {
            listener.measureUpdate(ee);
        }
    }

    @Override
    public void addMeasureListener(MeasureListener listener) {
        listenerList.add(MeasureListener.class, listener);
    }

    @Override
    public void removeMeasureListener(MeasureListener listener) {
        listenerList.remove(MeasureListener.class, listener);
    }

    @Override
    public void extentBeatCount(int beatCount) {
        int start = beatList.size();
        while (this.beatList.size() < beatCount) {
            addBeat(start);
            start++;
        }
    }

    @Override
    public void shrinkBeatCount(int beatCount) {
        while (beatCount != this.beatList.size()) {
            beatList.remove(beatList.size() - 1);
            MeasureEvent ee = new MeasureEvent(this, MeasureEventType.BEAT_REMOVED, null);
            for (MeasureListener listener : listenerList.getListeners(MeasureListener.class)) {
                listener.measureUpdate(ee);
            }
        }
    }

    @Override
    public Beat getBeat(int i) {
        return beatList.get(i);
    }

    @Override
    public int getBeatCount() {
        return beatList.size();
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public Key getKey() {
        return key;
    }
}
