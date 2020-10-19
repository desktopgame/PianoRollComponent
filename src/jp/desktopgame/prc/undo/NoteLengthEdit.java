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
public class NoteLengthEdit extends AbstractUndoableEdit {

    private PianoRollModel model;
    private Note note;
    private float oldLength;
    private float newLength;

    public NoteLengthEdit(PianoRollModel model, Note note, float oldLength) {
        this.model = model;
        this.note = note;
        this.oldLength = oldLength;
        this.newLength = note.getLength();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        model.beginApplyUndoableEdit();
        try {
            note.setLength(oldLength);
        } finally {
            model.endApplyUndoableEdit();
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        model.beginApplyUndoableEdit();
        try {
            note.setLength(newLength);
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
