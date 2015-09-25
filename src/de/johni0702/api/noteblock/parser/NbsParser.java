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

import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.google.common.io.LittleEndianDataInputStream;

import de.johni0702.api.noteblock.song.Instrument;
import de.johni0702.api.noteblock.song.Layer;
import de.johni0702.api.noteblock.song.Note;
import de.johni0702.api.noteblock.song.NotePitch;
import de.johni0702.api.noteblock.song.Song;

public class NbsParser implements Parser {

	@Override
	public Song parseFile(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		DataInput in = new LittleEndianDataInputStream(inputStream);

		int length = in.readShort();
		int height = in.readShort();
		String name = readString(in);
		String author = readString(in);
		String originalAuthor = readString(in);
		String description = readString(in);
		double tempo = in.readShort() / 100d;
		in.skipBytes(23);
		in.skipBytes(in.readInt());

		Layer[] layers = new Layer[height];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = new Layer();
		}

		int jump;
		int tick = -1;
		while ((jump = in.readShort()) != 0) {
			tick += jump;
			int layer = -1;
			while ((jump = in.readShort()) != 0) {
				layer += jump;

				Instrument instrument = Instrument.forId(in.readByte());
				NotePitch pitch = notePitchForId((in.readByte() - 33) % 24);

				layers[layer].setNoteBlock(tick, new Note(instrument, pitch));
			}
		}

		for (Layer layer : layers) {
			layer.setName(readString(in));
			layer.setVolume(in.readByte() / 100d);
		}

		Song song = new Song(length, name, author, originalAuthor, description,
				tempo);
		song.getLayers().addAll(Arrays.asList(layers));
		return song;
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

	private String readString(DataInput in) throws IOException {
		byte[] buf = new byte[in.readInt()];
		in.readFully(buf);
		return new String(buf);
	}

}
