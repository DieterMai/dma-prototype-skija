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
import org.eclipse.swt.widgets.TreeItem.*;

/**
 *
 */
public class TreeCellRenderer implements ITreeItemRenderer {
	public enum ColorType {
		BORDER_DOWN(0.4f), BORDER_HOVER(0.1f), FILL_DOWN(0.2f), FILL_HOVER(0.1f);

		final float ratio;

		private ColorType(float ratio) {
			this.ratio = ratio;
		}
	}

	private record TreeCellLayout(Point size, Image image, Rectangle imageBounds, String text, Rectangle textBounds) {

	}

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	/*
	 * The background color is displayed when the button is pressed or hovered - but
	 * not as-is. The background color is shifted in the direction of the target
	 * color by the ratio defined in ColorType.
	 */
	private static final RGB TARGET_RGB = new RGB(0, 139, 255);

	/* If no color is set, this is used as fallback. */
	private static final RGB DEFAULT_RGB = new RGB(225, 241, 255);


	private static final int PADDING_TOP = 1;
	private static final int DEFAULT_HEIGHT = 18;

	private final Tree tree;
	private final TreeItem item;

	public TreeCellRenderer(Tree tree, TreeItem item) {
		this.tree = tree;
		this.item = item;
	}

	@Override
	public void render(GC gc, Rectangle bounds, int parentIndent) {
		Point size = new Point(bounds.width, bounds.height);
		Point offset = new Point(bounds.x, bounds.y);

		TreeCellLayout layout = computeLayout(size);
		renderLayout(gc, offset, layout);
	}

	private void renderLayout(GC gc, Point offset, TreeCellLayout layout) {
		renderHighlight(gc, offset, layout);

		if (layout.image != null) {
			Rectangle imageBounds = translate(layout.imageBounds(), offset);
			gc.drawImage(item.getImage(), imageBounds.x, imageBounds.y);
		}

		if (layout.text != null) {
			Rectangle textBounds = translate(layout.textBounds(), offset);
			gc.setForeground(getTextColor()); // TODO move into render data
			gc.drawText(layout.text, textBounds.x, textBounds.y);
		}
	}

	void renderHighlight(GC gc, Point offset, TreeCellLayout layout) {
		if (!tree.isEnabled()) {
			return;
		}

		Rectangle bounds = new Rectangle(0, 0, layout.size.x, layout.size.y);
		bounds = translate(bounds, offset);

		if (item.isSelected()) {
			drawHighlight(gc, bounds, getColor(ColorType.BORDER_DOWN), getColor(ColorType.FILL_DOWN));
			return;
		} else if (item.isHover()) {
			drawHighlight(gc, bounds, getColor(ColorType.BORDER_HOVER), getColor(ColorType.FILL_HOVER));
			return;
		}
	}

	private void drawHighlight(GC gc, Rectangle bounds, Color borderColor, Color fillColor) {
		gc.setAntialias(SWT.ON);
		gc.setBackground(fillColor);
		gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);

		gc.setForeground(borderColor);
		gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);
	}

	private Color getColor(ColorType type) {
		Color backgroundColor = item.getBackground();
		RGB set;
		if (backgroundColor != null) {
			set = backgroundColor.getRGB();
		} else {
			set = DEFAULT_RGB;
		}

		int red = Math.round(set.red - (set.red - TARGET_RGB.red) * type.ratio);
		int gree = Math.round(set.green - (set.green - TARGET_RGB.green) * type.ratio);
		int blue = Math.round(set.blue - (set.blue - TARGET_RGB.blue) * type.ratio);

		return new Color(red, gree, blue);
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

		int xOffset = 0;
		int yOffset = PADDING_TOP;
		int height = DEFAULT_HEIGHT;
		if (image != null) {
			imageBounds = new Rectangle(xOffset, yOffset, imageSize.x, imageSize.y);
			xOffset += imageSize.x;
		}

		if (text != null) {
			textBounds = new Rectangle(xOffset, PADDING_TOP, textSize.x, textSize.y);
			xOffset += textBounds.width;
		}

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

	private Rectangle translate(Rectangle bounds, Point offset) {
		return new Rectangle(bounds.x + offset.x, bounds.y + offset.y, bounds.width, bounds.height);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
