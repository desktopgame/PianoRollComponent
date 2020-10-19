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
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;

/**
 * MIDI信号に関するユーティリティクラスです.
 *
 * @author desktopgame
 */
public class MIDI {

    private MIDI() {
    }

    public static int TIMEBASE_DEFAULT = 480;

    public static List<NoteLocation> noteListToNoteLocationList(List<Note> notes, int timebase, int beatWidth) {
        float f = (float) timebase / (float) beatWidth;
        return notes.stream().map((a) -> {
            Beat aB = a.getBeat();
            Measure aM = aB.getMeasure();
            int aAdd = ((aM.getIndex() * aM.getBeatCount()) * timebase) + (aB.getIndex() * timebase);
            int aO = (int) Math.round((float) a.getOffset() * f) + aAdd;
            return new NoteLocation(a, aO);
        }).collect(Collectors.toList());
    }

    public static List<MidiEvent> pianoRollModelToMidiEvents(PianoRollModel model, int channel, int timebase, int velocity, int beatWidth) throws InvalidMidiDataException {
        ArrayList<MidiEvent> r = new ArrayList<>();
        List<NoteLocation> notes = noteListToNoteLocationList(model.getAllNotes(), timebase, beatWidth);
        notes.sort((a, b) -> a.globalPos - b.globalPos);
        for (int i = 0; i < notes.size(); i++) {
            NoteLocation noteLoc = notes.get(i);
            int height = model.getKeyHeight(noteLoc.note.getBeat().getMeasure().getKey().getIndex());
            ShortMessage onMessage = new ShortMessage(ShortMessage.NOTE_ON, channel, height, velocity);
            MidiEvent onEvent = new MidiEvent(onMessage, noteLoc.globalPos);
            r.add(onEvent);
            ShortMessage offMessage = new ShortMessage(ShortMessage.NOTE_OFF, channel, height, 0);
            MidiEvent offEvent = new MidiEvent(offMessage, noteLoc.globalPos + noteLoc.note.scaledLength(timebase));
            r.add(offEvent);
        }
        return r;
    }
}
