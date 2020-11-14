/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * ピアノロール中で繰り返す必要のある範囲を表すクラス.
 *
 * @author desktopgame
 */
public class Region {

    private int startOffset;
    private int endOffset;
    private int loopCount;
    private PropertyChangeSupport support;
    private Monitor monitor;
    private boolean removed;

    public Region() {
        this(0, 0, 0);
    }

    public Region(int startOffset, int endOffset, int loopCount) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.loopCount = loopCount;
        this.support = new PropertyChangeSupport(this);
        this.monitor = new Monitor();
    }

    void addNotify() {
        this.removed = false;
    }

    void removeNotify() {
        this.removed = true;
        monitor.reset();
    }

    /**
     * この範囲の通過回数をカウントするモニターを返します.
     *
     * @param reset
     * @return
     */
    public Monitor getMonitor(boolean reset) {
        if (reset) {
            monitor.reset();
        }
        return monitor;
    }

    /**
     * リージョンの変更を監視するリスナーを追加します.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * リージョンの変更を監視するリスナーを削除します.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    /**
     * リージョンの開始位置を設定します.
     *
     * @param startOffset
     */
    public void setStartOffset(int startOffset) {
        if (startOffset < 0) {
            startOffset = 0;
        }
        int a = this.startOffset;
        this.startOffset = startOffset;
        support.firePropertyChange("startOffset", a, startOffset);
    }

    /**
     * リージョンの開始位置を返します.
     *
     * @return
     */
    public int getStartOffset() {
        return startOffset;
    }

    /**
     * リージョンの終了位置を設定します.
     *
     * @param endOffset
     */
    public void setEndOffset(int endOffset) {
        int a = this.endOffset;
        this.endOffset = endOffset;
        support.firePropertyChange("endOffset", a, endOffset);
    }

    /**
     * リージョンの終了位置を返します.
     *
     * @return
     */
    public int getEndOffset() {
        return endOffset;
    }

    /**
     * リージョンのループ回数を設定します.
     *
     * @param loopCount
     */
    public void setLoopCount(int loopCount) {
        int a = this.loopCount;
        this.loopCount = loopCount;
        support.firePropertyChange("loopCount", a, loopCount);
    }

    /**
     * リージョンのループ回数を返します.
     *
     * @return
     */
    public int getLoopCount() {
        return loopCount;
    }

    /**
     * リージョンの長さを返します.
     *
     * @return
     */
    public int getLength() {
        return endOffset - startOffset;
    }

    /**
     * リージョンの通過回数をカウントするクラスです.
     */
    public class Monitor {

        private int loopCount;

        private Monitor() {
            this.loopCount = 0;
        }

        /**
         * 通過回数をリセットします.
         */
        public void reset() {
            this.loopCount = 0;
        }

        /**
         * 通過回数を+1します.
         */
        public void addLoop() {
            this.loopCount++;
        }

        /**
         * まだループ可能上限に達していなければtrueを返します.
         *
         * @return
         */
        public boolean canMoreLoop() {
            return loopCount < Region.this.loopCount && !Region.this.removed;
        }

        /**
         * このモニタの設定を返します.
         *
         * @return
         */
        public Region getRegion() {
            return Region.this;
        }
    }
}
