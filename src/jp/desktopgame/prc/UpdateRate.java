/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

/**
 * シーケンサーの更新レートと同期するGUIの更新レートを計算するクラスです.
 *
 * @author desktopgame
 */
public class UpdateRate {

    public final float timebase;
    public final float bpm;
    public final float tick;
    public final float secPerBeat;

    public UpdateRate(float timebase, float bpm, float tick, float secPerBeat) {
        this.timebase = timebase;
        this.bpm = bpm;
        this.tick = tick;
        this.secPerBeat = secPerBeat;
    }

    /**
     * タイムベースとBPMから更新レートを計算します.
     *
     * @param timebase
     * @param bpm
     * @return
     */
    public static UpdateRate bpmToUpdateRate(float timebase, float bpm) {
        float tick = 60f / bpm / timebase;
        float secPerBeat = tick * timebase;
        return new UpdateRate(timebase, bpm, tick, secPerBeat);
    }

    /**
     * 拍一つあたりの横幅からタイマーの更新レートを計算します.
     *
     * @param beatWidth
     * @return
     */
    public int computeTimerDelay(int beatWidth) {
        return (int) Math.round((secPerBeat * 1000f) / (float) beatWidth);
    }

    /**
     * 拍一つあたりの横幅から一秒間にシーケンサが移動するGUI上の幅を返します.
     *
     * @param beatWidth
     * @return
     */
    public int computeDistancePerSec(int beatWidth) {
        return 1000 / computeTimerDelay(beatWidth);
    }

    /**
     * デバッグ情報を出力します.
     *
     * @param beatWidth
     */
    public void debug(int beatWidth) {
        System.out.printf("timebase=%f bpm=%f tick=%f secPerBeat=%f delay=%d distancePerSec=%d\n", timebase, bpm, tick, secPerBeat, computeTimerDelay(beatWidth), computeDistancePerSec(beatWidth));
    }
}
