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
	private static final int INDENT = 17;
	private static final int DEFAULT_HEIGHT = 18;

	private static final int[] POLILINE_CLOSED = { 8, 7, 12, 11, 8, 15 };
	private static final int[] POLILINE_OPEN = { 7, 7, 11, 12, 15, 7 };
	private static final int[] POLILINE_CHECK = { 2, 6, 5, 9, 9, 3 };

	private static final Color COLOR_CLOSED = new Color(139, 139, 139);
	private static final Color COLOR_OPEN = new Color(0, 0, 0);
	private static final Color COLOR_CHECKBOX_BORDER = new Color(98, 98, 98);
	private static final Color COLOR_CHECKBOX_FILL = new Color(243, 243, 243);
	private static final Color COLOR_CHECKBOX_CHECKED = new Color(0, 95, 184);

	private final Tree tree;
	private final TreeItem item;
	private Point size;

	private TreeItemLayout renderedLayout;

	public TreeItemRenderer(Tree tree, TreeItem item) {
		this.tree = tree;
		this.item = item;
	}

	@Override
	public void render(GC gc, Rectangle bounds, int dept, List<TreeCell> cells) {
		Point size = new Point(bounds.width, bounds.height);
		Point offset = new Point(bounds.x, bounds.y);

		renderedLayout = computeLayout(size, cells, dept);
		renderLayout(gc, offset, renderedLayout, cells);
	}

	private void renderLayout(GC gc, Point offset, TreeItemLayout layout, List<TreeCell> cells) {
		renderChildIndicator(gc, layout, offset);
		renderCheckbox(gc, layout, offset);
		renderCells(gc, layout, offset, cells);
	}

	private void renderChildIndicator(GC gc, TreeItemLayout layout, Point offset) {
		Color color = switch (layout.childIndicator()) {
		case NONE -> null;
		case OPEN -> COLOR_OPEN;
		case CLOSED -> COLOR_CLOSED;
		};

		if (color == null) {
			return;
		}

		int[] absoluteLine = switch (layout.childIndicator()) {
		case NONE -> null; // can not happen
		case OPEN -> translate(POLILINE_OPEN, offset.x + layout.indent(), offset.y);
		case CLOSED -> translate(POLILINE_CLOSED, offset.x + layout.indent(), offset.y);
		};

		gc.setForeground(color);
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(2);
		gc.drawPolyline(absoluteLine);
	}

	private void renderCheckbox(GC gc, TreeItemLayout layout, Point offset) {
		Rectangle bounds = layout.checkboxBounds();
		if (bounds == null) {
			return;
		}

		Rectangle absolute = bounds.translate(offset);
		if (layout.isChecked()) {
			gc.setLineWidth(2);
			gc.setBackground(COLOR_CHECKBOX_CHECKED);
			gc.setForeground(new Color(255, 255, 255));
			gc.setAntialias(SWT.ON);

			int[] absoluteLine = translate(POLILINE_CHECK, absolute.x, absolute.y);
			gc.fillRectangle(absolute);
			gc.drawPolyline(absoluteLine);
		} else {
			gc.setLineWidth(1);
			gc.setBackground(COLOR_CHECKBOX_FILL);
			gc.setForeground(COLOR_CHECKBOX_BORDER);

			gc.fillRectangle(absolute);
			gc.drawRectangle(absolute);
		}
	}

	private void renderCells(GC gc, TreeItemLayout layout, Point offset, List<TreeCell> cells) {
		for (int i = 0; i < cells.size(); i++) {
			cells.get(i).render(gc, layout.boundsList().get(i).translate(offset));
		}
	}

	private TreeItemLayout computeLayout(Point treeSize, List<TreeCell> cells, int depth) {
		boolean isChecked = item.getChecked();
		Rectangle checkboxBounds = new Rectangle(0, 0, 0, 0);
		int indent = getIndent(depth);
		int height = DEFAULT_HEIGHT;

		int xOffset = indent + INDENT;
		if (tree.isCheck()) {
			checkboxBounds = new Rectangle(xOffset + 3, 3, 12, 12);
			xOffset += 16;
		}

		// 1. Collect preferred size
		List<Rectangle> boundsList = new ArrayList<>();
		for (int i = 0; i < cells.size(); i++) {
			int cellHeight = cells.get(i).getHeight();
			int cellWidth = tree.getColumn(i).getWidth();
			boundsList.add(new Rectangle(0, 0, cellWidth, cellHeight));
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

		return new TreeItemLayout(size, checkboxBounds, isChecked, indent, state, boundsList);
	}

	@Override
	public Point getSize(List<TreeCell> cells, int depth) {
		if (renderedLayout != null) {
			return renderedLayout.size();
		} else {
			TreeItemLayout layout = computeLayout(new Point(0, 0), cells, depth);
			return layout.size();
		}
	}

	private int[] translate(int[] original, int x, int y) {
		int[] translatedPath = new int[original.length];
		for (int i = 0; i + 1 < translatedPath.length; i += 2) {
			translatedPath[i] = x + original[i];
			translatedPath[i + 1] = y + original[i + 1];
		}
		return translatedPath;
	}

	@Override
	public boolean isOnChildIndicator(Point location) {
		if (location.x > renderedLayout.indent() && location.x < renderedLayout.indent() + INDENT) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOnCheckbox(Point locations) {
		if (renderedLayout == null) {
			return false;
		}
		return renderedLayout.checkboxBounds().contains(locations);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}

	@Override
	public int getIndent(int dept) {
		return dept * INDENT;
	}
}
