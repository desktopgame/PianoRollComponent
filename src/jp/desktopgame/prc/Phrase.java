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
 * 一連のノートの配置や属性をまとめて保存するクラスです.
 *
 * @author desktopgame
 */
public class Phrase {

    /**
     * ノートの情報を仮想化したものクラスです. 保存時に実際のキーや小節を保存することはできないので、場所だけを保存します。
     */
    public static class VirtualNote {

        public final int keyIndex;
        public final int measureIndex;
        public final int beatIndex;
        public final int noteOffset;
        public final float noteLength;

        public VirtualNote(int keyIndex, int measureIndex, int beatIndex, int noteOffset, float noteLength) {
            this.keyIndex = keyIndex;
            this.measureIndex = measureIndex;
            this.beatIndex = beatIndex;
            this.noteOffset = noteOffset;
            this.noteLength = noteLength;
        }
    }

    private class VirtualNoteLocation {

        public final NoteLocation loc;
        public final VirtualNote note;

        public VirtualNoteLocation(NoteLocation loc, VirtualNote note) {
            this.loc = loc;
            this.note = note;
        }
    }

    private List<VirtualNote> virtualNotes;
    private String name;

    public Phrase() {
        this.virtualNotes = new ArrayList<>();
        this.name = "Untitled";
    }

    public Phrase(List<VirtualNote> virtualNotes) {
        this.virtualNotes = new ArrayList<>(virtualNotes);
        this.name = "Untitled";
    }

    /**
     * ノートの一覧からフレーズを作成します.
     *
     * @param notes
     * @return
     */
    public static Phrase createFromNotes(List<Note> notes) {
        Phrase p = new Phrase();
        for (Note note : notes) {
            VirtualNote vnote = new VirtualNote(note.getBeat().getMeasure().getKey().getIndex(), note.getBeat().getMeasure().getIndex(), note.getBeat().getIndex(), note.getOffset(), note.getLength());
            p.virtualNotes.add(vnote);
        }
        return p;
    }

    private List<VirtualNoteLocation> toNoteLocationList(int beatCount, int beatWidth) {
        List<VirtualNoteLocation> r = new ArrayList<>();
        for (VirtualNote vnote : this.virtualNotes) {
            Note note = new DefaultNote(null, vnote.noteOffset, vnote.noteLength);
            int gp = (vnote.measureIndex * (beatCount * beatWidth)) + (vnote.beatIndex * beatWidth) + vnote.noteOffset;
            NoteLocation nl = new NoteLocation(note, gp);
            r.add(new VirtualNoteLocation(nl, vnote));
        }
        return r;
    }

    /**
     * 仮想的なノートの一覧を返します.
     *
     * @return
     */
    public List<VirtualNote> getVirtualNotes() {
        return virtualNotes;
    }

    /**
     * このフレーズの名前を設定します.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * このフレーズの名前を返します.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * このフレーズをモデルの指定位置に挿入します.
     *
     * @param model
     * @param insertOffset
     * @param beatCount
     * @param beatWidth
     * @return
     */
    public boolean expand(PianoRollModel model, int insertOffset, int beatCount, int beatWidth) {
        List<VirtualNoteLocation> noteLocList = toNoteLocationList(beatCount, beatWidth);
        // 最も左のノートを取得する
        int basePosTemp = Short.MAX_VALUE;
        Note baseNote = null;
        for (int i = 0; i < noteLocList.size(); i++) {
            NoteLocation loc = noteLocList.get(i).loc;
            if (loc.globalPos < basePosTemp) {
                basePosTemp = loc.globalPos;
                baseNote = loc.note;
            }
        }
        if (baseNote == null) {
            return false;
        }
        final int basePos = basePosTemp;
        model.beginCompoundUndoableEdit();
        // 全てのノートを貼り付け
        noteLocList.forEach((vNoteLoc) -> {
            NoteLocation noteLoc = vNoteLoc.loc;
            int gp = noteLoc.globalPos;
            int lp = gp - basePos;
            //Note note = noteLoc.note;
            Key key = model.getKey(vNoteLoc.note.keyIndex - 1);
            Measure measure = key.getMeasure(vNoteLoc.note.measureIndex);
            Beat beat = measure.getBeat(vNoteLoc.note.beatIndex);
            int measureWidth = ((measure.getBeatCount() * beatWidth));

            int noffset = insertOffset + lp;
            int measureIndex = noffset / measureWidth;
            int measureMod = noffset % measureWidth;
            int beatIndex = measureMod / beatWidth;
            int beatMod = measureMod % beatWidth;
            Measure offsetMeasure = key.getMeasure(measureIndex);
            Beat offsetBeat = offsetMeasure.getBeat(beatIndex);
            offsetBeat.generateNote(beatMod, vNoteLoc.note.noteLength);
        });
        model.endCompoundUndoableEdit();
        return true;
    }
}
