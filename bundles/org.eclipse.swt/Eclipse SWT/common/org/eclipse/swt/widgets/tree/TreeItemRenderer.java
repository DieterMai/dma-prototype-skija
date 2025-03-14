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

import java.util.*;
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.TreeItem.*;
import org.eclipse.swt.widgets.tree.TreeItemLayout.*;

/**
 *
 */
public class TreeItemRenderer implements ITreeItemRenderer {
	public enum ColorType {
		BORDER_DOWN(0.4f), BORDER_HOVER(0.1f), FILL_DOWN(0.2f), FILL_HOVER(0.1f);

		final float ratio;

		private ColorType(float ratio) {
			this.ratio = ratio;
		}
	}


	private static final int INDENT = 17;
	private static final int DEFAULT_HEIGHT = 18;

	private static final int[] CLOSED_POLILINE = { 8, 7, 12, 11, 8, 15 };
	private static final int[] OPEN_POLILINE = { 7, 7, 11, 12, 15, 7 };

	private static final Color CLOSED_COLOR = new Color(139, 139, 139);
	private static final Color OPEN_COLOR = new Color(0, 0, 0);

	private final Tree tree;
	private final TreeItem item;

	private int indent = INDENT;

	public TreeItemRenderer(Tree tree, TreeItem item) {
		this.tree = tree;
		this.item = item;
	}

	@Override
	public void render(GC gc, Rectangle bounds, int parentIndent, List<TreeCell> cells) {
		indent = parentIndent + INDENT;
		Point size = new Point(bounds.width, bounds.height);
		Point offset = new Point(bounds.x, bounds.y);

		TreeItemLayout layout = computeLayout(size, cells);
		renderLayout(gc, offset, layout, cells);
	}

	private void renderLayout(GC gc, Point offset, TreeItemLayout layout, List<TreeCell> cells) {
		renderChildIndicator(gc, offset, layout.childIndicator());

		for (int i = 0; i < cells.size(); i++) {
			cells.get(i).render(gc, layout.boundsList().get(i).translate(offset));
		}
	}

	private void renderChildIndicator(GC gc, Point offset, ChildIndicator childIndicator) {
		int[] absoluteLine = switch (childIndicator) {
		case NONE -> null;
		case OPEN -> translate(OPEN_POLILINE, offset);
		case CLOSED -> translate(CLOSED_POLILINE, offset);
		};

		Color color = switch (childIndicator) {
		case NONE -> null;
		case OPEN -> OPEN_COLOR;
		case CLOSED -> CLOSED_COLOR;
		};

		if (absoluteLine == null) {
			return;
		}

		gc.setForeground(color);
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(2);
		gc.drawPolyline(absoluteLine);
	}

	private TreeItemLayout computeLayout(Point treeSize, List<TreeCell> cells) {
		int xOffset = indent;
		int height = DEFAULT_HEIGHT;

		// 1. Collect preferred size
		List<Rectangle> boundsList = new ArrayList<>();
		for (int i = 0; i < cells.size(); i++) {
			Point size = cells.get(i).getSize();
			boundsList.add(new Rectangle(0, 0, size.x, size.y));
		}

		// 2. Position
		for (int i = 0; i < boundsList.size(); i++) {
			Rectangle bounds = boundsList.get(i);
			bounds.x = xOffset;
			xOffset += bounds.width;
		}

		// 3. Normalize Height
		// TODO

		Point size = new Point(xOffset, height);

		ChildIndicator state;
		if (item.getItemCount() <= 0) {
			state = ChildIndicator.NONE;
		} else if (item.getExpanded()) {
			state = ChildIndicator.OPEN;
		} else {
			state = ChildIndicator.CLOSED;
		}

		return new TreeItemLayout(size, state, boundsList);
	}

	@Override
	public Point getSize(java.util.List<TreeCell> cells) {
		TreeItemLayout layout = computeLayout(new Point(0, 0), cells);
		return layout.size();
	}

	private int[] translate(int[] original, Point offset) {
		int[] translatedPath = new int[original.length];
		for (int i = 0; i + 1 < translatedPath.length; i += 2) {
			translatedPath[i] = offset.x + original[i];
			translatedPath[i + 1] = offset.y + original[i + 1];
		}
		return translatedPath;
	}

	@Override
	public boolean isOnChildIndicator(Point location) {
		if (location.x > indent - INDENT && location.x < indent) {
			return true;
		}
		return false;
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
