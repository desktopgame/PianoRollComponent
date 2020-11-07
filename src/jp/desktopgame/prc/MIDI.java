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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * General MIDI準拠のドラムマップです. オクターブから名前を引きます。
     */
    public static Map<Integer, String> DRUM_MAP;

    /**
     * General MIDI準拠のドラムマップです. 名前からオクターブを引きます。
     */
    public static Map<String, Integer> DRUM_RMAP;

    static {
        DRUM_MAP = new HashMap<>();
        DRUM_RMAP = new HashMap<>();
        DRUM_MAP.put(35, "Acoustic Bass Drum");
        DRUM_MAP.put(36, "Bass Drum 1");
        DRUM_MAP.put(37, "Side Stick");
        DRUM_MAP.put(38, "Acoustic Snare");
        DRUM_MAP.put(39, "Hand Clap");
        DRUM_MAP.put(40, "Electric Snare");
        DRUM_MAP.put(41, "Low Floor Tom");
        DRUM_MAP.put(43, "High Floor Tom");
        DRUM_MAP.put(45, "Low Tom");
        DRUM_MAP.put(47, "Low Mid Tom");
        DRUM_MAP.put(48, "High Mid Tom");
        DRUM_MAP.put(50, "High Tom");
        DRUM_MAP.put(42, "Closed Hi-Hat");
        DRUM_MAP.put(44, "Pedal Hi-Hat");
        DRUM_MAP.put(46, "Open Hi-Hat");
        DRUM_MAP.put(49, "Crash Cymbal 1");
        DRUM_MAP.put(51, "Ride Cymbal 1");
        DRUM_MAP.put(51, "Chinese Cymbal");
        DRUM_MAP.put(53, "Ride Bell");
        DRUM_MAP.put(55, "Splash Cymbal");
        DRUM_MAP.put(54, "Tambourine");
        DRUM_MAP.put(56, "Cowbell");
        DRUM_MAP.put(57, "Crash Cymbal 2");
        DRUM_MAP.put(58, "Vibraslap");
        DRUM_MAP.put(59, "Ride Cymbal 2");
        DRUM_MAP.put(60, "High Bongo");
        DRUM_MAP.put(61, "Low Bongo");
        DRUM_MAP.put(62, "Mute High Conga");
        DRUM_MAP.put(63, "Open High Conga");
        DRUM_MAP.put(64, "Low Conga");
        DRUM_MAP.put(65, "High Timbale");
        DRUM_MAP.put(66, "Low Timbale");
        DRUM_MAP.put(67, "High Agogo");
        DRUM_MAP.put(68, "Low Agogo");
        DRUM_MAP.put(69, "Cabase");
        DRUM_MAP.put(70, "Maracas");
        DRUM_MAP.put(71, "Short Whistle");
        DRUM_MAP.put(72, "Long Whistle");
        DRUM_MAP.put(73, "Short Guiro");
        DRUM_MAP.put(74, "Long Guiro");
        DRUM_MAP.put(75, "Claves");
        DRUM_MAP.put(76, "High Wood Block");
        DRUM_MAP.put(77, "Low Wood Block");
        DRUM_MAP.put(78, "Mute Cuica");
        DRUM_MAP.put(79, "Open Cuica");
        DRUM_MAP.put(80, "Mute Triangle");
        DRUM_MAP.put(81, "Open Triangle");
        for (Integer k : DRUM_MAP.keySet()) {
            DRUM_RMAP.put(DRUM_MAP.get(k), k);
        }
        DRUM_MAP = Collections.unmodifiableMap(DRUM_MAP);
        DRUM_RMAP = Collections.unmodifiableMap(DRUM_RMAP);
    }

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
