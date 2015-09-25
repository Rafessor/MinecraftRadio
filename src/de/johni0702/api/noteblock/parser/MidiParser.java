/*
 * This file is part of NoteBlockAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Johni0702 <https://github.com/johni0702>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.johni0702.api.noteblock.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import de.johni0702.api.noteblock.song.Instrument;
import de.johni0702.api.noteblock.song.Layer;
import de.johni0702.api.noteblock.song.Note;
import de.johni0702.api.noteblock.song.NotePitch;
import de.johni0702.api.noteblock.song.Song;

public class MidiParser implements Parser {

	@Override
	public Song parseFile(File file) throws InvalidMidiDataException,
			IOException {
		FileInputStream inputStream = new FileInputStream(file);
		Sequence sequence = MidiSystem.getSequence(inputStream);
		double midiTickToMicro = (double) sequence.getMicrosecondLength()
				/ sequence.getTickLength();
		List<Layer> layers = new ArrayList<>();
		for (Track track : sequence.getTracks()) {
			Instrument[] instrument = new Instrument[16];
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				if (message instanceof ShortMessage) {
					ShortMessage m = (ShortMessage) message;
					if (m.getCommand() == ShortMessage.NOTE_ON) {
						int tick = (int) (midiTickToMicro * event.getTick() / 50_000);
						double volume = m.getData2() / 127d;
						NotePitch pitch = notePitchForId((m.getData1() - 6) % 24);
						Layer layer = null;
						for (Layer l : layers) {
							if (l.getVolume() == volume
									&& l.getNoteBlock(tick) == null) {
								layer = l;
							}
						}
						if (layer == null) {
							layers.add(layer = new Layer("Layer"
									+ layers.size(), volume));
						}
						layer.setNoteBlock(tick,
								new Note(instrument[m.getChannel()], pitch));
					} else if (m.getCommand() == ShortMessage.PROGRAM_CHANGE) {
						instrument[m.getChannel()] = programToInstrument(
								m.getData1(), m.getChannel());
					}
				}
			}
		}
		int length = 0;
		for (Layer layer : layers) {
			SortedMap<Integer, Note> notes = layer.getNotes();
			length = Math.max(length, notes.isEmpty() ? 0 : notes.lastKey());
		}
		return new Song(length, file.getName().split(".")[0], "unknown",
				"unknown", "Imported MIDI file", 1);
	}

	private NotePitch notePitchForId(int id) {
		NotePitch result = null;
		int i = 0;
		for (NotePitch pitch : NotePitch.values()) {
			if (pitch.toString().equalsIgnoreCase("NOTE_" + i)) {
				result = pitch;
				break;
			}
			i++;
		}
		return result;
	}

	private Instrument programToInstrument(int program, int channel) {
		if (channel == 9)
			return Instrument.BASS_DRUM;

		if (program >= 24 && program <= 39 || program >= 43 && program <= 46)
			return Instrument.BASS;

		if (program >= 113 && program <= 119)
			return Instrument.BASS_DRUM;

		if (program >= 120 && program <= 127)
			return Instrument.SNARE_DRUM;

		return Instrument.PIANO;
	}

}
