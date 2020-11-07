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
    public static final Map<Integer, String> DRUM_MAP;

    /**
     * General MIDI準拠のドラムマップです. 名前からオクターブを引きます。
     */
    public static final Map<String, Integer> DRUM_RMAP;

    private static Map<Integer, String> _DRUM_MAP;
    private static Map<String, Integer> _DRUM_RMAP;

    static {
        _DRUM_MAP = new HashMap<>();
        _DRUM_RMAP = new HashMap<>();
        _DRUM_MAP.put(35, "Acoustic Bass Drum");
        _DRUM_MAP.put(36, "Bass Drum 1");
        _DRUM_MAP.put(37, "Side Stick");
        _DRUM_MAP.put(38, "Acoustic Snare");
        _DRUM_MAP.put(39, "Hand Clap");
        _DRUM_MAP.put(40, "Electric Snare");
        _DRUM_MAP.put(41, "Low Floor Tom");
        _DRUM_MAP.put(43, "High Floor Tom");
        _DRUM_MAP.put(45, "Low Tom");
        _DRUM_MAP.put(47, "Low Mid Tom");
        _DRUM_MAP.put(48, "High Mid Tom");
        _DRUM_MAP.put(50, "High Tom");
        _DRUM_MAP.put(42, "Closed Hi-Hat");
        _DRUM_MAP.put(44, "Pedal Hi-Hat");
        _DRUM_MAP.put(46, "Open Hi-Hat");
        _DRUM_MAP.put(49, "Crash Cymbal 1");
        _DRUM_MAP.put(51, "Ride Cymbal 1");
        _DRUM_MAP.put(51, "Chinese Cymbal");
        _DRUM_MAP.put(53, "Ride Bell");
        _DRUM_MAP.put(55, "Splash Cymbal");
        _DRUM_MAP.put(54, "Tambourine");
        _DRUM_MAP.put(56, "Cowbell");
        _DRUM_MAP.put(57, "Crash Cymbal 2");
        _DRUM_MAP.put(58, "Vibraslap");
        _DRUM_MAP.put(59, "Ride Cymbal 2");
        _DRUM_MAP.put(60, "High Bongo");
        _DRUM_MAP.put(61, "Low Bongo");
        _DRUM_MAP.put(62, "Mute High Conga");
        _DRUM_MAP.put(63, "Open High Conga");
        _DRUM_MAP.put(64, "Low Conga");
        _DRUM_MAP.put(65, "High Timbale");
        _DRUM_MAP.put(66, "Low Timbale");
        _DRUM_MAP.put(67, "High Agogo");
        _DRUM_MAP.put(68, "Low Agogo");
        _DRUM_MAP.put(69, "Cabase");
        _DRUM_MAP.put(70, "Maracas");
        _DRUM_MAP.put(71, "Short Whistle");
        _DRUM_MAP.put(72, "Long Whistle");
        _DRUM_MAP.put(73, "Short Guiro");
        _DRUM_MAP.put(74, "Long Guiro");
        _DRUM_MAP.put(75, "Claves");
        _DRUM_MAP.put(76, "High Wood Block");
        _DRUM_MAP.put(77, "Low Wood Block");
        _DRUM_MAP.put(78, "Mute Cuica");
        _DRUM_MAP.put(79, "Open Cuica");
        _DRUM_MAP.put(80, "Mute Triangle");
        _DRUM_MAP.put(81, "Open Triangle");
        for (Integer k : _DRUM_MAP.keySet()) {
            _DRUM_RMAP.put(_DRUM_MAP.get(k), k);
        }
        DRUM_MAP = Collections.unmodifiableMap(_DRUM_MAP);
        DRUM_RMAP = Collections.unmodifiableMap(_DRUM_RMAP);
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
