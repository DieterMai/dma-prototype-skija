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
package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.TreeItem.*;
import org.eclipse.swt.widgets.toolbar.ToolItemButtonRenderer.*;

/**
 *
 */
public class TreeItemRenderer implements ITreeItemRenderer {
	private enum ChildIndicator {
		NONE, OPEN, CLOSED
	}

	public enum ColorType {
		BORDER_DOWN(0.4f), BORDER_HOVER(0.1f), FILL_DOWN(0.2f), FILL_HOVER(0.1f);

		final float ratio;

		private ColorType(float ratio) {
			this.ratio = ratio;
		}
	}

	private record TreeItemLayout(Point size, ChildIndicator childIndicator, Image image, Rectangle imageBounds, String text,
			Rectangle textBounds) {
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


	private static final int PADDING_HORIZONTAL = 21;
	private static final int PADDING_TOP = 1;
	private static final int DEFAULT_HEIGHT = 18;

	private static final int[] CLOSED_POLILINE = { 8, 7, 12, 11, 8, 15 };
	private static final int[] OPEN_POLILINE = { 7, 7, 11, 12, 15, 7 };

	private final Tree tree;
	private final TreeItem item;

	public TreeItemRenderer(Tree tree, TreeItem item) {
		this.tree = tree;
		this.item = item;
	}

	@Override
	public void render(GC gc, Rectangle bounds) {
		Point size = new Point(bounds.width, bounds.height);
		Point offset = new Point(bounds.x, bounds.y);

		TreeItemLayout layout = computeLayout(size);
		renderLayout(gc, offset, layout);
	}

	private void renderLayout(GC gc, Point offset, TreeItemLayout layout) {
		renderChildIndicator(gc, offset, layout.childIndicator);
		renderHighlight(gc, offset, layout);

		if (layout.image != null) {
			Rectangle imageBounds = layout.imageBounds();
			gc.drawImage(item.image, offset.x + imageBounds.x, offset.y + imageBounds.y);
		}

		if (layout.text != null) {
			Rectangle textBounds = layout.textBounds();
			gc.setForeground(getTextColor()); // TODO move into render data
			gc.drawText(layout.text, offset.x + textBounds.x, offset.y + textBounds.y);
		}
	}

	private void renderChildIndicator(GC gc, Point offset, ChildIndicator childIndicator) {
		int[] relativeLine = switch (childIndicator) {
		case NONE -> null;
		case OPEN -> OPEN_POLILINE;
		case CLOSED -> CLOSED_POLILINE;
		};

		if (relativeLine == null) {
			return;
		}

		int[] absoluteLine = new int[relativeLine.length];
		for (int i = 0; i + 1 < absoluteLine.length; i += 2) {
			absoluteLine[i] = offset.x + relativeLine[i];
			absoluteLine[i + 1] = offset.y + relativeLine[i + 1];
		}

		gc.setForeground(new Color(139, 139, 139));
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(2);
		gc.drawPolyline(absoluteLine);
	}

	void renderHighlight(GC gc, Point offset, TreeItemLayout layout) {
		if (!tree.isEnabled()) {
			return;
		}

		Rectangle bounds = new Rectangle(PADDING_HORIZONTAL + offset.x, offset.y, layout.size.x - PADDING_HORIZONTAL,
				layout.size.y);
		if (item.isSelected()) {
			drawHighlight(gc, bounds, getColor(ColorType.BORDER_DOWN), getColor(ColorType.FILL_DOWN));
			return;
		}

		switch (item.getMouseState()) {
		case IDLE -> {
		}
		case HOVER -> drawHighlight(gc, bounds, getColor(ColorType.BORDER_HOVER), getColor(ColorType.FILL_HOVER));
		case DOWN -> drawHighlight(gc, bounds, getColor(ColorType.BORDER_DOWN), getColor(ColorType.FILL_DOWN));
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

	private TreeItemLayout computeLayout(Point treeSize) {


		Image image = null;
		String text = null;


		// 1. Collect elements
		if (hasImage()) {
			image = item.image;
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

		int xOffset = PADDING_HORIZONTAL;
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

		ChildIndicator state;
		if (item.getItemCount() <= 0) {
			state = ChildIndicator.NONE;
		} else if (item.getExpanded()) {
			state = ChildIndicator.OPEN;
		} else {
			state = ChildIndicator.CLOSED;
		}

		return new TreeItemLayout(size, state, image, imageBounds, text, textBounds);
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
		TreeItemLayout layout = computeLayout(new Point(0, 0));
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
