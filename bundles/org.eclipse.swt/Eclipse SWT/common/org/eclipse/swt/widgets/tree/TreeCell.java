/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.widgets.tree;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class TreeCell {
	interface ITreeCellRenderer {
		/**
		 * Renders the {@link TreeItem}.
		 *
		 * @param gc     GC to render with.
		 * @param bounds Bounds of the rendering. x and y are always 0.
		 */
		void render(GC gc, Rectangle bounds);

		/**
		 * Returns the size of the rendered {@link ToolItem}.
		 *
		 * @return The size as a {@link Point}.
		 */
		Point getSize();
	}

	private final Tree tree;
	private final TreeItem item;

	private String text;

	private Color backgroundColor;
	private Color foregroundColor;
	private Font font;

	private Rectangle bounds = new Rectangle(0, 0, 0, 0);

	public TreeCell(Tree tree, TreeItem item) {
		this.tree = tree;
		this.item = item;
	}

	public Color backgroundColor() {
		return backgroundColor;
	}

	public Color foregroundColor() {
		return foregroundColor;
	}

	public Font font() {
		return font;
	}

	public String text() {
		return text;
	}

	public void backgroundColor(Color color) {
		this.backgroundColor = color;
	}

	public void foregroundColor(Color color) {
		this.foregroundColor = color;
	}

	public void font(Font font) {
		this.font = font;
	}

	public void text(String text) {
		this.text = text;
	}

	public Rectangle bounds() {
		return bounds;
	}
}
