/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.util.EventListener;

/**
 * リージョンの更新を監視するリスナーです.
 *
 * @author desktopgame
 */
public interface RegionUpdateListener extends EventListener {

    /**
     * リージョンが更新されると呼ばれます.
     *
     * @param e
     */
    public void regionUpdate(RegionUpdateEvent e);
}
