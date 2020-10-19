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
import jp.desktopgame.prc.Beat;
import jp.desktopgame.prc.Note;
import jp.desktopgame.prc.PianoRollModel;

/**
 *
 * @author desktopgame
 */
public class NoteCreateEdit extends AbstractUndoableEdit {

    private PianoRollModel model;
    private Beat beat;
    private Note note;

    public NoteCreateEdit(PianoRollModel model, Beat beat, Note note) {
        this.model = model;
        this.beat = beat;
        this.note = note;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        model.beginApplyUndoableEdit();
        try {
            note.removeFromBeat();
        } finally {
            model.endApplyUndoableEdit();
        }
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        model.beginApplyUndoableEdit();
        try {
            beat.restoreNote(note);
        } finally {
            model.endApplyUndoableEdit();
        }
    }

    @Override
    public void die() {
        super.die();
        beat = null;
        note = null;
    }
}
