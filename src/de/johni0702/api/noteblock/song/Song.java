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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import lombok.Getter;

public class Song {

	@Getter
	private int length;
	@Getter
	private final String name, author, originalAuthor, description;
	@Getter
	private double tempo;
	@Getter
	private final List<Layer> layers = new ArrayList<>();

	public Song(int length, String name, String author, String originalAuthor,
			String description, double tempo) {
		this.length = length;
		this.name = name;
		this.author = author;
		this.originalAuthor = originalAuthor;
		this.description = description;
		this.tempo = tempo;
	}

	public void updateLength() {
		length = 0;
		for (Layer layer : layers) {
			SortedMap<Integer, Note> notes = layer.getNotes();
			int layerLength = notes.isEmpty() ? 0 : notes.lastKey();
			if (layerLength > length) {
				length = layerLength;
			}
		}
	}
}
