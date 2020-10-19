/*
 * PianoRollComponent
 *
 * Copyright (c) 2020 desktopgame
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/mit-license.php
 */
package jp.desktopgame.prc.undo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import jp.desktopgame.prc.Note;
import jp.desktopgame.prc.PianoRollModel;

/**
 *
 * @author desktopgame
 */
public class NoteOffsetEdit extends AbstractUndoableEdit {

    private PianoRollModel model;
    private Note note;
    private int oldOffset;
    private int newOffset;

    public NoteOffsetEdit(PianoRollModel model, Note note, int oldOffset) {
        this.model = model;
        this.note = note;
        this.oldOffset = oldOffset;
        this.newOffset = note.getOffset();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        model.beginApplyUndoableEdit();
        try {
            note.setOffset(oldOffset);
        } finally {
            model.endApplyUndoableEdit();
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        model.beginApplyUndoableEdit();
        try {
            note.setOffset(newOffset);
        } finally {
            model.endApplyUndoableEdit();
        }
    }

    @Override
    public void die() {
        super.die();
        note = null;
    }
}
