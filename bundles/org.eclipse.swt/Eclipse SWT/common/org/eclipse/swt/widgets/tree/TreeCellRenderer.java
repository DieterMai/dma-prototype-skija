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

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.tree.TreeCell.*;

/**
 *
 */
public class TreeCellRenderer implements ITreeCellRenderer {
	private record TreeCellLayout(Point size, Image image, Rectangle imageBounds, String text, Rectangle textBounds) {

	}

	private static final Color COLOR_SELECTED_FILL = new Color(204, 232, 255);
	private static final Color COLOR_SELECTED_BORDER = new Color(0, 120, 212);
	private static final Color COLOR_HOVER = new Color(229, 243, 255);

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	private static final int PADDING_LEFT = 2;
	private static final int PADDING_RIGHT = 3;
	private static final int PADDING_TOP = 1;
	private static final int SPACING = 5;
	private static final int DEFAULT_HEIGHT = 18;
	private static final int TEXT_OFFSET_HORIZONTAL = 4;

	private final Tree tree;
	private final TreeItem item;

	public TreeCellRenderer(Tree tree, TreeItem item) {
		this.tree = tree;
		this.item = item;
	}

	@Override
	public void render(GC gc, Rectangle bounds) {
		Point size = new Point(bounds.width, bounds.height);
		Point offset = new Point(bounds.x, bounds.y);

		TreeCellLayout layout = computeLayout(size);
		renderLayout(gc, offset, layout);
	}

	private void renderLayout(GC gc, Point offset, TreeCellLayout layout) {
		renderHighlight(gc, offset, layout);

		if (layout.image != null) {
			Rectangle imageBounds = layout.imageBounds().translate(offset);
			gc.drawImage(item.getImage(), imageBounds.x, imageBounds.y);
		}

		if (layout.text != null) {
			Rectangle textBounds = layout.textBounds().translate(offset);
			gc.setForeground(getTextColor()); // TODO move into render data
			gc.drawText(layout.text, textBounds.x, textBounds.y);
		}
	}

	void renderHighlight(GC gc, Point offset, TreeCellLayout layout) {
		if (!tree.isEnabled()) {
			return;
		}

		Rectangle bounds = new Rectangle(0, 0, layout.size.x, layout.size.y).translate(offset);

		if (item.isSelected()) {
			drawHighlight(gc, bounds, COLOR_SELECTED_BORDER, COLOR_SELECTED_FILL);
			return;
		} else if (item.isHover()) {
			drawHighlight(gc, bounds, COLOR_HOVER, COLOR_HOVER);
			return;
		}
	}

	private void drawHighlight(GC gc, Rectangle bounds, Color borderColor, Color fillColor) {
		gc.setAntialias(SWT.ON);
		gc.setBackground(fillColor);
		gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);

		gc.setForeground(borderColor);
		gc.setLineWidth(1);
		gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);
	}


	private TreeCellLayout computeLayout(Point treeSize) {
		Image image = null;
		String text = null;

		// 1. Collect elements
		if (hasImage()) {
			image = item.getImage();
		}
		if (hasText()) {
			text = item.getText();
		}

		// 2. Collect sizes
		Point imageSize = null;
		Point textSize = null;
		if (image != null) {
			imageSize = new Point(image.getBounds().width, image.getBounds().height);
		}
		if (text != null) {
			textSize = getTextSize();
		}

		// 3. Position elements
		Rectangle imageBounds = null;
		Rectangle textBounds = null;

		int xOffset = PADDING_LEFT;
		int yOffset = PADDING_TOP;
		int height = DEFAULT_HEIGHT;
		if (image != null) {
			imageBounds = new Rectangle(xOffset, yOffset, imageSize.x, imageSize.y);
			xOffset += imageSize.x;
			xOffset += SPACING;
		}

		if (text != null) {
			textBounds = new Rectangle(xOffset, PADDING_TOP, textSize.x, textSize.y);
			xOffset += textBounds.width;
		}

		xOffset += PADDING_RIGHT;

		Point size = new Point(xOffset, height);

		return new TreeCellLayout(size, image, imageBounds, text, textBounds);
	}

	private boolean hasImage() {
		return item.getImage() != null;
	}

	private boolean hasText() {
		return item.getText() != null && !item.getText().isBlank();
	}

	private Color getTextColor() {
		if (tree.isEnabled()) {
			return tree.getForeground();
		} else {
			return tree.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	@Override
	public Point getSize() {
		TreeCellLayout layout = computeLayout(new Point(0, 0));
		return layout.size();
	}

	private Point getTextSize() {
		return Drawing.executeOnGC(tree, this::doMesureText);
	}

	private Point doMesureText(GC gc) {
		gc.setFont(item.getFont());
		return gc.textExtent(item.getText(), DRAW_FLAGS);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
