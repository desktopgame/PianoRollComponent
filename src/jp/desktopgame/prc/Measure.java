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
import java.util.List;

/**
 * 小節を表すインターフェイスです.
 *
 * @author desktopgame
 */
public interface Measure {

    public void addMeasureListener(MeasureListener listener);

    public void removeMeasureListener(MeasureListener listener);

    /**
     * この小節が含むことができる拍の数を拡張します.
     *
     * @param beatCount
     */
    public void extentBeatCount(int beatCount);

    /**
     * この小説が含むことができる拍の数を縮小します. 必ず後ろから縮小します。
     *
     * @param beatCount
     */
    public void shrinkBeatCount(int beatCount);

    /**
     * 指定位置の拍を返します.
     *
     * @param i
     * @return
     */
    public Beat getBeat(int i);

    /**
     * この小節に含まれる拍の数を返します.
     *
     * @return
     */
    public int getBeatCount();

    public int getIndex();

    public Key getKey();

    public default List<Beat> copyBeatList() {
        ArrayList<Beat> bl = new ArrayList<>();
        for (int i = 0; i < this.getBeatCount(); i++) {
            bl.add(getBeat(i));
        }
        return bl;
    }
}
