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
 * 鍵盤におけるキーひとつを表すインターフェイスです.
 *
 * @author desktopgame
 */
public interface Key {

    public static int WHITE = 0;
    public static int BLACK = 1;

    public void addKeyListener(KeyListener listener);

    public void removeKeyListener(KeyListener listener);

    public void extentBeatCount(int beatCount);

    public void shrinkBeatCount(int beatCount);

    /**
     * このキーが含むことができる小節の数を拡張します.
     *
     * @param measureCount
     */
    public void extentMeasureCount(int measureCount);

    /**
     * このキーが含むことができる小節の数を縮小します. 必ず後ろから縮小します。
     *
     * @param measureCount
     */
    public void shrinkMeasureCount(int measureCount);

    /**
     * 指定位置の小節を返します.
     *
     * @param i
     * @return
     */
    public Measure getMeasure(int i);

    /**
     * このキーに含まれる小節の数を返します.
     *
     * @return
     */
    public int getMeasureCount();

    /**
     * このキーの位置を返します.
     *
     * @return
     */
    public int getIndex();

    /**
     * このキーが含まれるモデルを返します.
     *
     * @return
     */
    public PianoRollModel getModel();

    public default List<Measure> copyMeasureList() {
        ArrayList<Measure> ml = new ArrayList<>();
        for (int i = 0; i < getMeasureCount(); i++) {
            ml.add(getMeasure(i));
        }
        return ml;
    }
}
