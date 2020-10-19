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

/**
 *
 * @author desktopgame
 */
public class NoteDragEvent extends EventObject {

    public NoteDragEvent(NoteDragManager manager) {
        super(manager);
    }

    @Override
    public NoteDragManager getSource() {
        return (NoteDragManager) super.getSource();
    }
}
