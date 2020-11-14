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

    public Monitor getMonitor(boolean reset) {
        if (reset) {
            monitor.reset();
        }
        return monitor;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    public void setStartOffset(int startOffset) {
        if (startOffset < 0) {
            startOffset = 0;
        }
        int a = this.startOffset;
        this.startOffset = startOffset;
        support.firePropertyChange("startOffset", a, startOffset);
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setEndOffset(int endOffset) {
        int a = this.endOffset;
        this.endOffset = endOffset;
        support.firePropertyChange("endOffset", a, endOffset);
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setLoopCount(int loopCount) {
        int a = this.loopCount;
        this.loopCount = loopCount;
        support.firePropertyChange("loopCount", a, loopCount);
    }

    public int getLoopCount() {
        return loopCount;
    }

    public int getLength() {
        return endOffset - startOffset;
    }

    public class Monitor {

        private int loopCount;

        private Monitor() {
            this.loopCount = 0;
        }

        public void reset() {
            this.loopCount = 0;
        }

        public void addLoop() {
            this.loopCount++;
        }

        public boolean canMoreLoop() {
            return loopCount < Region.this.loopCount && !Region.this.removed;
        }

        public Region getRegion() {
            return Region.this;
        }
    }
}
