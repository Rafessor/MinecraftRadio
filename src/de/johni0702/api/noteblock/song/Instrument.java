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
package de.johni0702.api.noteblock.song;

import org.bukkit.Sound;

public enum Instrument {

	PIANO(Sound.NOTE_PIANO), BASS(Sound.NOTE_BASS), BASS_DRUM(
			Sound.NOTE_BASS_DRUM), SNARE_DRUM(Sound.NOTE_SNARE_DRUM), STICKS(
			Sound.NOTE_STICKS);

	private final Sound soundType;

	Instrument(Sound soundType) {
		this.soundType = soundType;
	}

	public Sound getSoundType() {

		return soundType;
	}

	public float getPitch(NotePitch pitch) {
		NotePitch[] all = NotePitch.values();
		int i = 0;
		while (true) {
			if (all.length == i + 1 || all[i] == pitch)
				break;
			i++;
		}
		return PITCH[i];

	}

	public static Instrument forId(int id) {
		if (id < 0 || id >= values().length) {
			return PIANO;
		}
		return values()[id];
	}

	private static final float[] PITCH = { 0.5f, 0.53f, 0.56f, 0.6f, 0.63f,
			0.67f, 0.7f, 0.75f, 0.8f, 0.85f, 0.9f, 0.95f, 1.0f, 1.05f, 1.1f,
			1.2f, 1.25f, 1.32f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f };
}
