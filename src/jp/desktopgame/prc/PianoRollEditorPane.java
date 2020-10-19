/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Optional;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/**
 *
 * @author desktopgame
 */
public class PianoRollEditorPane extends JPanel {

    private PianoRoll pianoRoll;
    private PianoRollLayerUI pianoRollLayerUI;
    private Keyboard keyboard;
    private Ruler ruler;
    private UndoManager undoManager;

    private UndoableEditHandler undoableEditHandler;
    private NoteResizeHandler noteResizeHandler;
    private NoteDragHandler noteDragHandler;
    private PianoRollModelHandler pianoRollModelHandler;

    public PianoRollEditorPane() {
        super(new BorderLayout());
        this.pianoRoll = new PianoRoll();
        this.pianoRollLayerUI = new PianoRollLayerUI();
        JScrollPane s = new JScrollPane(new JLayer<PianoRoll>(pianoRoll, pianoRollLayerUI));
        this.keyboard = new Keyboard(pianoRoll, s);
        this.ruler = new Ruler(pianoRoll, pianoRollLayerUI, s, keyboard);
        this.undoManager = new UndoManager();
        this.undoableEditHandler = new UndoableEditHandler();
        this.noteResizeHandler = new NoteResizeHandler();
        this.noteDragHandler = new NoteDragHandler();
        this.pianoRollModelHandler = new PianoRollModelHandler();
        add(ruler, BorderLayout.NORTH);
        add(keyboard, BorderLayout.WEST);
        add(s, BorderLayout.CENTER);
        // モデルが更新されるたびに変更を破棄する
        pianoRoll.getModel().addUndoableEditListener(undoableEditHandler);
        pianoRoll.getModel().addPianoRollModelListener(pianoRollModelHandler);
        pianoRoll.addPropertyChangeListener((pe) -> {
            if (pe.getPropertyName().equals("model")) {
                undoManager.discardAllEdits();
                ((PianoRollModel) pe.getOldValue()).removeUndoableEditListener(undoableEditHandler);
                ((PianoRollModel) pe.getNewValue()).addUndoableEditListener(undoableEditHandler);
                ((PianoRollModel) pe.getOldValue()).removePianoRollModelListener(pianoRollModelHandler);
                ((PianoRollModel) pe.getNewValue()).addPianoRollModelListener(pianoRollModelHandler);
            }
        });
        // ドラッグ/リサイズの変更をまとめてUndo/Redoする
        pianoRoll.getUI().getNoteResizeManager().addNoteResizeListener(noteResizeHandler);
        pianoRoll.getUI().getNoteDragManager().addNoteDragListener(noteDragHandler);
        this.setPreferredSize(new Dimension(800, 600));
    }

    public void paste() {
        pianoRoll.paste(pianoRollLayerUI.getSequencePosition());
    }

    public void copy() {
        pianoRoll.copy();
    }

    public void cut() {
        pianoRoll.cut();
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public PianoRoll getPianoRoll() {
        return pianoRoll;
    }

    public PianoRollLayerUI getPianoRollLayerUI() {
        return pianoRollLayerUI;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    private class UndoableEditHandler implements UndoableEditListener {

        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
        }
    }

    private class NoteDragHandler implements NoteDragListener {

        @Override
        public void dragStart(NoteDragEvent e) {
            getPianoRoll().getModel().beginCompoundUndoableEdit();
        }

        @Override
        public void dragEnd(NoteDragEvent e) {
            getPianoRoll().getModel().endCompoundUndoableEdit();
        }

    }

    private class NoteResizeHandler implements NoteResizeListener {

        @Override
        public void resizeStart(NoteResizeEvent e) {
            getPianoRoll().getModel().beginCompoundUndoableEdit();
        }

        @Override
        public void resizeEnd(NoteResizeEvent e) {
            getPianoRoll().getModel().endCompoundUndoableEdit();
        }
    }

    private class PianoRollModelHandler implements PianoRollModelListener {

        @Override
        public void pianoRollModelUpdate(PianoRollModelEvent e) {
            // 基準となる値が変更された場合には変更履歴自体を削除
            Optional<MeasureEvent> meOpt = e.getMeasureEvent();
            Optional<KeyEvent> keOpt = e.getInnerEvent();
            if (meOpt.isPresent()) {
                MeasureEvent me = meOpt.get();
                if (me.getType() == MeasureEventType.BEAT_CREATED || me.getType() == MeasureEventType.BEAT_REMOVED) {
                    undoManager.discardAllEdits();
                }
            } else if (keOpt.isPresent()) {
                KeyEvent ke = keOpt.get();
                if (ke.getType() == KeyEventType.MEASURE_CREATED || ke.getType() == KeyEventType.MEASURE_REMOVED) {
                    undoManager.discardAllEdits();
                }
            } else {
                if (e.getType() == PianoRollModelEventType.KEY_CREATED || e.getType() == PianoRollModelEventType.KEY_REMOVED) {
                    undoManager.discardAllEdits();
                }
            }
        }

    }
}
