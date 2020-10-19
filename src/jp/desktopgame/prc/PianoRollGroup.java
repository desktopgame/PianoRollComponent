/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.awt.Color;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;
import javax.swing.JLayer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.LayerUI;

/**
 * 同じ編集ペインにまとめられるピアノロールに対してオニオンスキン機能を提供するためにグループ化を行うクラスです.
 * グループに含まれているピアノロールそれぞれにユニークな色を割り当てます。
 *
 * @author desktopgame
 */
public class PianoRollGroup {

    private List<PianoRoll> pianoRollList;
    private List<Color> colorList;
    private Stack<Color> colorBuf;
    private PropertyChangeHandler propertyChangeHandler;
    private PianoRollModelHandler pianoRollModelHandler;
    private SequenceHandler sequenceHandler;
    private EventListenerList listenerList;
    private boolean syncPosition;
    private int syncOwner;

    public PianoRollGroup() {
        this.pianoRollList = new ArrayList<>();
        this.colorList = new ArrayList<>();
        this.colorBuf = new Stack<>();
        this.propertyChangeHandler = new PropertyChangeHandler();
        this.pianoRollModelHandler = new PianoRollModelHandler();
        this.sequenceHandler = new SequenceHandler();
        this.listenerList = new EventListenerList();
        this.syncPosition = true;
        this.syncOwner = -1;
        // 最初の10色ぐらいは分かりやすい色で決めておく
        Arrays.asList(
                Color.black,
                Color.white,
                Color.red,
                Color.green,
                Color.blue,
                Color.orange,
                Color.pink,
                Color.magenta
        ).forEach(colorBuf::push);
    }

    /**
     * オニオンスキンの再描画が必要になった状態で発火されるイベントを監視するリスナーを追加します.
     *
     * @param listener
     */
    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    /**
     * オニオンスキンの再描画が必要になった状態で発火されるイベントを監視するリスナーを削除します.
     *
     * @param listener
     */
    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    protected void fireStateChanged(ChangeEvent e) {
        for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
            listener.stateChanged(e);
        }
    }

    /**
     * 指定のピアノロールをグループに追加します.
     *
     * @param pianoRoll
     */
    public void addPianoRoll(PianoRoll pianoRoll) {
        pianoRollList.add(pianoRoll);
        pianoRoll.addPropertyChangeListener(propertyChangeHandler);
        pianoRoll.getModel().addPianoRollModelListener(pianoRollModelHandler);
        addColor();
        pianoRoll.setGroup(this);
        getLayer(pianoRoll).ifPresent((e) -> e.addSequenceListener(sequenceHandler));
    }

    /**
     * 指定位置のピアノロールを返します.
     *
     * @param i
     * @return
     */
    public PianoRoll getPianoRoll(int i) {
        return pianoRollList.get(i);
    }

    /**
     * 指定のピアノロールに割り当てられた色を返します.
     *
     * @param pianoRoll
     * @return
     */
    public Color getSkinColor(PianoRoll pianoRoll) {
        return getSkinColor(pianoRollList.indexOf(pianoRoll));
    }

    /**
     * 指定位置の色を返します.
     *
     * @param i
     * @return
     */
    public Color getSkinColor(int i) {
        return colorList.get(i);
    }

    /**
     * ピアノロールをグループから削除します.
     *
     * @param pianoRoll
     */
    public void removePianoRoll(PianoRoll pianoRoll) {
        removeColor(pianoRollList.indexOf(pianoRoll));
        pianoRollList.remove(pianoRoll);
        pianoRoll.setGroup(null);
        getLayer(pianoRoll).ifPresent((e) -> e.removeSequenceListener(sequenceHandler));
    }

    /**
     * ピアノロールをグループから削除します.
     *
     * @param i
     */
    public void removePianoRoll(int i) {
        removeColor(i);
        PianoRoll p = pianoRollList.get(i);
        p.setGroup(null);
        pianoRollList.remove(i);
        getLayer(p).ifPresent((e) -> e.removeSequenceListener(sequenceHandler));
    }

    /**
     * グループを空にします.
     */
    public void removeAllPianoRoll() {
        while (getPianoRollCount() > 0) {
            removePianoRoll(0);
        }
    }

    /**
     * グループの大きさを返します.
     *
     * @return
     */
    public int getPianoRollCount() {
        return pianoRollList.size();
    }

    /**
     * グループに含まれる全てにピアノロールのシーケンス位置を同期するかどうかを設定します.
     *
     * @param syncPosition
     */
    public void setSyncPosition(boolean syncPosition) {
        this.syncPosition = syncPosition;
    }

    /**
     * グループに含まれる全てにピアノロールのシーケンス位置を同期するなら trueを返します.
     *
     * @return
     */
    public boolean isSyncPosition() {
        return syncPosition;
    }

    /**
     * シーケンスの同期時に同期対象となるピアノロールのインデックスを設定します.
     *
     * @param syncOwner
     */
    public void setSyncOwner(int syncOwner) {
        this.syncOwner = syncOwner;
    }

    /**
     * シーケンスの同期時に同期対象となるピアノロールのインデックスを返します.
     *
     * @return
     */
    public int getSyncOwner() {
        return syncOwner;
    }

    private void removeColor(int i) {
        colorBuf.push(colorList.get(i));
        colorList.remove(i);
    }

    private void addColor() {
        if (!colorBuf.empty()) {
            colorList.add(colorBuf.pop());
            return;
        }
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        if (colorList.stream().anyMatch((c) -> nearEq(c.getRed(), r) && nearEq(c.getGreen(), g) && nearEq(c.getBlue(), b))) {
            addColor();
        } else {
            colorList.add(new Color(r, g, b));
        }
    }

    private static boolean nearEq(int a, int b) {
        return Math.abs(a - b) < 10;
    }

    private static Optional<PianoRollLayerUI> getLayer(PianoRoll p) {
        Container c = p.getParent();
        while (c != null) {
            if (c instanceof JLayer) {
                JLayer l = (JLayer) c;
                LayerUI lui = l.getUI();
                if (lui instanceof PianoRollLayerUI) {
                    return Optional.of((PianoRollLayerUI) lui);
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    private class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            Object ov = evt.getOldValue();
            Object nv = evt.getNewValue();
            if (name.equals("model")) {
                ((PianoRollModel) ov).removePianoRollModelListener(pianoRollModelHandler);
                ((PianoRollModel) ov).addPianoRollModelListener(pianoRollModelHandler);
                fireStateChanged(new ChangeEvent(PianoRollGroup.this));
            }
        }
    }

    private class PianoRollModelHandler implements PianoRollModelListener {

        @Override
        public void pianoRollModelUpdate(PianoRollModelEvent e) {
            fireStateChanged(new ChangeEvent(PianoRollGroup.this));
        }
    }

    private class SequenceHandler implements SequenceListener {

        @Override
        public void sequenceUpdate(SequenceEvent e) {
            if (!syncPosition) {
                return;
            }
            int index = -1;
            for (int i = 0; i < pianoRollList.size(); i++) {
                PianoRoll p = pianoRollList.get(i);
                if (e.getSource().getPianoRoll().equals(p)) {
                    index = i;
                    break;
                }
            }
            if (index < 0 || index != syncOwner || syncOwner == -1) {
                return;
            }
            for (PianoRoll p : pianoRollList) {
                Optional<PianoRollLayerUI> luiOpt = getLayer(p);
                luiOpt.ifPresent((lui) -> {
                    int pos = lui.getSequencePosition();
                    if (pos != e.getNewPosition()) {
                        lui.setSequencePosition(e.getNewPosition());
                    }
                });
            }
        }

    }
}
