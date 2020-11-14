/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.EventListenerList;

/**
 *
 * @author desktopgame
 */
public class RegionManager {

    private List<Region> regions;
    private PropertyChangeHandler propHandler;
    private EventListenerList listenerList;

    public RegionManager() {
        this.regions = new ArrayList<>();
        this.propHandler = new PropertyChangeHandler();
        this.listenerList = new EventListenerList();
    }

    public void addRegionUpdateListener(RegionUpdateListener listener) {
        listenerList.add(RegionUpdateListener.class, listener);
    }

    public void removeRegionUpdateListener(RegionUpdateListener listener) {
        listenerList.remove(RegionUpdateListener.class, listener);
    }

    private void fireRegionUpdate(RegionUpdateEvent e) {
        for (RegionUpdateListener listener : listenerList.getListeners(RegionUpdateListener.class)) {
            listener.regionUpdate(e);
        }
    }

    public void addRegion(Region region) {
        regions.add(region);
        region.addPropertyChangeListener(propHandler);
        region.addNotify();
        fireRegionUpdate(new RegionUpdateEvent(this, null, region));
    }

    public void removeRegion(Region region) {
        regions.remove(region);
        region.removePropertyChangeListener(propHandler);
        region.removeNotify();
        fireRegionUpdate(new RegionUpdateEvent(this, region, null));
    }

    public void removeRegion(int i) {
        Region region = regions.remove(i);
        region.removePropertyChangeListener(propHandler);
        region.removeNotify();
        fireRegionUpdate(new RegionUpdateEvent(this, region, null));
    }

    public Region getRegion(int i) {
        return regions.get(i);
    }

    public List<Region> getRegions() {
        return Collections.unmodifiableList(new ArrayList<>(regions));
    }

    public int getRegionCount() {
        return regions.size();
    }

    private void onPropertyChange(PropertyChangeEvent e) {
        Region src = (Region) e.getSource();
        String pnam = e.getPropertyName();
        Object ov = e.getOldValue();
        Object nv = e.getNewValue();
        int startOffset = src.getStartOffset();
        int endOffset = src.getEndOffset();
        int loopCount = src.getLoopCount();
        if (pnam.equals("startOffset")) {
            startOffset = (int) ov;
        } else if (pnam.equals("endOffset")) {
            endOffset = (int) ov;
        } else if (pnam.equals("loopCount")) {
            loopCount = (int) ov;
        }
        fireRegionUpdate(new RegionUpdateEvent(this, new Region(startOffset, endOffset, loopCount), src));
    }

    private class PropertyChangeHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            onPropertyChange(e);
        }
    }
}
