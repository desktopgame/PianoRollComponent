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
import java.util.stream.Collectors;
import javax.swing.event.UndoableEditListener;

/**
 * ピアノロールを抽象化するモデルです。
 *
 * @author desktopgame
 */
public interface PianoRollModel {

    public void addUndoableEditListener(UndoableEditListener listener);

    public void removeUndoableEditListener(UndoableEditListener listener);

    public void addPianoRollModelListener(PianoRollModelListener listener);

    public void removePianoRollModelListener(PianoRollModelListener listener);

    public void extentKeyCount(int keyCount);

    public void shrinkKeyCount(int keyCount);

    public void extentMeasureCount(int measureCount);

    public void shrinkMeasureCount(int measureCount);

    public void extentBeatCount(int beatCount);

    public void shrinkBeatCount(int beatCount);

    public void resizeKeyCount(int keyCount);

    public void resizeMeasureCount(int measureCount);

    public void resizeBeatCount(int beatCount);

    /**
     * Undo/Redoの適用を開始します. このメソッド呼び出し以降の変更は記録されません。
     */
    public void beginApplyUndoableEdit();

    /**
     * Undo/RRedoの適用を終了します.
     */
    public void endApplyUndoableEdit();

    /**
     * まとめて戻したい変更を開始します.
     */
    public void beginCompoundUndoableEdit();

    /**
     * まとめて戻したい変更を終了します.
     */
    public void endCompoundUndoableEdit();

    public int getKeyHeight(int keyIndex);

    public Key getKey(int i);

    public int getKeyCount();

    public default List<Key> copyKeyList() {
        ArrayList<Key> kl = new ArrayList<>();
        for (int i = 0; i < getKeyCount(); i++) {
            kl.add(getKey(i));
        }
        return kl;
    }

    public default List<Note> getAllNotes() {
        return copyKeyList()
                .stream()
                .flatMap((e) -> e.copyMeasureList().stream())
                .flatMap((e) -> e.copyBeatList().stream())
                .flatMap((e) -> e.copyNoteList().stream()).collect(Collectors.toList());
    }

    public default List<Note> getSelectedNotes() {
        return copyKeyList()
                .stream()
                .flatMap((e) -> e.copyMeasureList().stream())
                .flatMap((e) -> e.copyBeatList().stream())
                .flatMap((e) -> e.copyNoteList().stream())
                .filter((e) -> e.isSelected()).collect(Collectors.toList());
    }

    public default void clearAllSelection() {
        copyKeyList().forEach((k) -> {
            k.copyMeasureList().forEach((me) -> {
                me.copyBeatList().forEach((bb) -> {
                    bb.copyNoteList().forEach((nn) -> {
                        nn.setSelected(false);
                    });
                });
            });
        });
    }
}
