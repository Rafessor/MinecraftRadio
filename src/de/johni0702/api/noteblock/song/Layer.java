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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.SortedMap;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

public class Layer {

	@Getter
	@Setter
	private String name;
	@Getter
	private double volume;
	private final SortedMap<Integer, Note> noteBlocks = new TreeMap<>();

	public Layer() {
		this("", 1);
	}

	public Layer(String name) {
		this(name, 1);
	}

	public Layer(String name, double volume) {
		this.setName(name);
		this.setVolume(volume);
	}

	public void setVolume(double volume) {
		checkArgument(volume >= 0 && volume <= 1,
				"volume must be between 0 and 1 (inclusive)");
		this.volume = volume;
	}

	public Note getNoteBlock(int tick) {
		return noteBlocks.get(tick);
	}

	public void setNoteBlock(int tick, Note noteBlock) {
		noteBlocks.put(tick, noteBlock);
	}

	public SortedMap<Integer, Note> getNotes() {
		return noteBlocks;
	}
}