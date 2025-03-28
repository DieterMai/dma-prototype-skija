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
	private record TreeCellLayout(//
			Point size, //
			Image image, //
			Rectangle imageBounds, //
			String text, //
			Rectangle textBounds//
	) {
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

	private final Tree tree;
	private final TreeItem item;
	private final TreeCell cell;

	public TreeCellRenderer(Tree tree, TreeItem item, TreeCell cell) {
		this.tree = tree;
		this.item = item;
		this.cell = cell;
	}

	@Override
	public void render(GC gc, Rectangle bounds) {
		Point size = new Point(bounds.width, bounds.height);
		Point offset = new Point(bounds.x, bounds.y);

		TreeCellLayout layout = computeLayout(size);
		render(gc, layout, offset);
	}

	private void render(GC gc, TreeCellLayout layout, Point offset) {
		renderHighlight(gc, layout, offset);
		renderCheckbox(gc, layout, offset);
		renderImage(gc, layout, offset);
		renderText(gc, layout, offset);
	}

	public void renderHighlight(GC gc, TreeCellLayout layout, Point offset) {
		if (!tree.isEnabled()) {
			return;
		}

		Rectangle bounds = new Rectangle(0, 0, layout.size.x, layout.size.y).translate(offset);

		if (item.isSelected()) {
			renderHighlight(gc, bounds, COLOR_SELECTED_BORDER, COLOR_SELECTED_FILL);
			return;
		} else if (item.isHover()) {
			renderHighlight(gc, bounds, COLOR_HOVER, COLOR_HOVER);
			return;
		}
	}

	private void renderHighlight(GC gc, Rectangle bounds, Color borderColor, Color fillColor) {
		gc.setAntialias(SWT.ON);
		gc.setBackground(fillColor);
		gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);

		gc.setForeground(borderColor);
		gc.setLineWidth(1);
		gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);
	}

	private void renderCheckbox(GC gc, TreeCellLayout layout, Point offset) {

	}

	private void renderImage(GC gc, TreeCellLayout layout, Point offset) {
		if (layout.image != null) {
			Rectangle imageBounds = layout.imageBounds().translate(offset);
			gc.drawImage(item.getImage(), imageBounds.x, imageBounds.y);
		}
	}

	private void renderText(GC gc, TreeCellLayout layout, Point offset) {
		if (layout.text != null) {
			Rectangle textBounds = layout.textBounds().translate(offset);
			gc.setForeground(getTextColor()); // TODO move into render data
			gc.drawText(layout.text, textBounds.x, textBounds.y);
		}
	}

	private TreeCellLayout computeLayout(Point treeSize) {
		Image image = null;
		String text = null;

		// 1. Collect elements
		if (hasImage()) {
			image = cell.getImage();
		}
		if (hasText()) {
			text = cell.getText();
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
		return cell.getImage() != null;
	}

	private boolean hasText() {
		return cell.getText() != null && !cell.getText().isBlank();
	}

	private Color getTextColor() {
		if (tree.isEnabled()) {
			return tree.getForeground();
		} else {
			return tree.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	@Override
	public int getHeight() {
		return DEFAULT_HEIGHT;
	}

	private Point getTextSize() {
		return Drawing.executeOnGC(tree, this::doMesureText);
	}

	private Point doMesureText(GC gc) {
		gc.setFont(cell.getFont());
		return gc.textExtent(cell.getText(), DRAW_FLAGS);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}

	@Override
	public int computePreferedWidth() { // TODO duplicated code -> computeLayout
		Image image = null;
		String text = null;

		// 1. Collect elements
		if (hasImage()) {
			image = cell.getImage();
		}
		if (hasText()) {
			text = cell.getText();
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
		Rectangle textBounds = null;

		int width = PADDING_LEFT;
		if (image != null) {
			width += imageSize.x;
			width += SPACING;
		}

		if (text != null) {
			textBounds = new Rectangle(width, PADDING_TOP, textSize.x, textSize.y);
			width += textBounds.width;
		}

		width += PADDING_RIGHT;

		return width;
	}
}
