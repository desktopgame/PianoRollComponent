/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.util.EventObject;
import java.util.Optional;

/**
 *
 * @author desktopgame
 */
public class RegionUpdateEvent extends EventObject {

    private Optional<Region> oldValue;
    private Optional<Region> newValue;

    public RegionUpdateEvent(RegionManager regionManager, Region oldValue, Region newValue) {
        super(regionManager);
        this.oldValue = Optional.ofNullable(oldValue);
        this.newValue = Optional.ofNullable(newValue);
    }

    @Override
    public RegionManager getSource() {
        return (RegionManager) super.getSource(); //To change body of generated methods, choose Tools | Templates.
    }

    public Optional<Region> getOldValue() {
        return oldValue;
    }

    public Optional<Region> getNewValue() {
        return newValue;
    }

}
