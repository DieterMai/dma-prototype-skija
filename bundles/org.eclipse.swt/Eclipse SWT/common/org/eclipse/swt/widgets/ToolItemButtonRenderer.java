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
import org.eclipse.swt.widgets.ToolItem.*;

public class ToolItemButtonRenderer implements ToolItemRenderer {
	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	private final ToolBar bar;
	private final ToolItem item;

	private Image disabledImage;
	private Rectangle bounds;

	public static enum LayoutType {
		TEXT_ONLY, IMAGE_ONLY, STACKED, SIDE_BY_SIDE
	}

	private static record Blueprint(Point size, Rectangle image, Rectangle text) {
	}

	ToolItemButtonRenderer(ToolBar bar, ToolItem item) {
		this.bar = bar;
		this.item = item;
	}

	@Override
	public Image getDisabledImage() {
		return disabledImage;
	}

	private LayoutType getContenType() {
		boolean hasImage = hasImage();
		boolean hasText = hasText();
		if (hasImage && hasText) {
			if (bar.isRight()) {
				return LayoutType.SIDE_BY_SIDE;
			}
			return LayoutType.STACKED;
		} else if (hasImage) {
			return LayoutType.IMAGE_ONLY;
		} else {
			return LayoutType.TEXT_ONLY;
		}
	}

	private boolean hasImage() {
		return item.getImage() != null;
	}

	private boolean hasText() {
		return item.getText() != null && !item.getText().isBlank();
	}

	@Override
	public void render(GC gc, Rectangle bounds) {
		this.bounds = bounds;
		Blueprint blueprint = getBlueprint(new Point(bounds.width, bounds.height));

		renderHighlight(gc, bounds);

		if (hasImage()) {
			Rectangle imageBounds = blueprint.image;
			Image image = getUsedImage();
			gc.drawImage(image, bounds.x + imageBounds.x, bounds.y + imageBounds.y);
		}

		if (hasText()) {
			Rectangle textBounds = blueprint.text;
			String text = item.getText();

			gc.setForeground(getTextColor());
			gc.drawText(text, bounds.x + textBounds.x, bounds.y + textBounds.y);
		}
	}

	private void renderHighlight(GC gc, Rectangle bounds) {
		if (item.isSelected()) {
			drawHighlight(gc, bounds, new Color(153, 209, 255), new Color(204, 232, 255));
			return;
		}

		switch(item.getState()) {
		case IDLE -> {
		}
		case HOVER -> drawHighlight(gc, bounds, new Color(204, 232, 255), new Color(229, 243, 255));
		case DOWN -> drawHighlight(gc, bounds, new Color(153, 209, 255), new Color(204, 232, 255));
		}
	}

	private void drawHighlight(GC gc, Rectangle bounds, Color borderColor, Color fillColor) {
		gc.setAntialias(SWT.ON);
		gc.setBackground(fillColor);
		gc.fillRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);
//
		gc.setForeground(borderColor);
		gc.drawRoundRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 4, 4);
	}

	private Image getUsedImage() {
		if (item.isEnabled() || bar.isEnabled()) {
			return item.image;
		} else {
			if (disabledImage == null) {
				disabledImage = new Image(bar.getDisplay(), item.image, SWT.IMAGE_DISABLE);
			}
			return disabledImage;
		}
	}



	private Color getTextColor() {
		if (bar.isEnabled()) {
			return bar.getForeground();
		} else {
			return bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	@Override
	public Point getSize() {
		Blueprint blueprint = getBlueprint(new Point(0, 0));
//		cachedSize = new Point(blueprint.size.x, blueprint.size.y);
		return blueprint.size();
	}

	private Blueprint getBlueprint(Point requestedSize) {
		LayoutType type = getContenType();
		return switch (type) {
		case IMAGE_ONLY -> getImageOnlyBlueprint(requestedSize);
		case TEXT_ONLY -> getTextOnlyBlueprint(requestedSize);
		case STACKED -> getStackedBlueprint(requestedSize);
		case SIDE_BY_SIDE -> getImageTextBlueprint(requestedSize);
		};
	}

	private Blueprint getImageOnlyBlueprint(Point requestedSize) {
		final int PADDING = 3;

		Rectangle imageBounds = item.getImage().getBounds();
		imageBounds.x = PADDING;
		imageBounds.y = PADDING;

		int width = PADDING + imageBounds.width + PADDING;
		int height = PADDING + imageBounds.height + PADDING;
		Point minSize = new Point(width, height);
		Point size = maxSize(requestedSize, minSize);
		int imagePos = (size.x - imageBounds.width) / 2;

		imageBounds.x = imagePos;

		if (item.getState() == State.DOWN) {
			imageBounds.x++;
			imageBounds.y++;
		}

		return new Blueprint(size, imageBounds, null);
	}

	private Blueprint getTextOnlyBlueprint(Point requestedSize) {
		final int PADDING_HORIZONTAL = 5;
		final int PADDING_TOP = 9;
		final int PADDING_BOT = 7;

		Point textExtent = getTextSize();

		int w = PADDING_HORIZONTAL + textExtent.x + PADDING_HORIZONTAL;
		int h = PADDING_TOP + textExtent.y + PADDING_BOT;
		Point minSize = new Point(w, h);
		Point size = maxSize(requestedSize, minSize);

		int textPosX = (size.x - textExtent.x) / 2;

		Rectangle textBounds = new Rectangle(textPosX, PADDING_TOP, textExtent.x, textExtent.y);

		if (item.getState() == State.DOWN) {
			textBounds.x++;
			textBounds.y++;
		}

		Blueprint bp = new Blueprint(size, null, textBounds);
		return bp;
	}

	private Point getTextSize() {
		GC gc = new GC(bar);
		gc.setFont(bar.getFont());
		return gc.textExtent(item.getText(), DRAW_FLAGS);
	}

	private Blueprint getStackedBlueprint(Point requestedSize) {
		final int PADDING_H_IMAGE = 3;
		final int PADDING_H_TEXT = 5;
		final int PADDING_TOP = 3;
		final int PADDING_BOT = 7;
		final int SPACING = 3;

		Rectangle imageBounds = item.getImage().getBounds();
		Point textExtent = getTextSize();

		int preferedImageWidth = PADDING_H_IMAGE + imageBounds.width + PADDING_H_IMAGE;
		int preferedTextWidth = PADDING_H_TEXT + textExtent.x + PADDING_H_TEXT;

		int itemWidth = Math.max(preferedImageWidth, preferedTextWidth);
		int itemHeight = PADDING_TOP + imageBounds.height + SPACING + textExtent.y + PADDING_BOT;

		Point minSize = new Point(itemWidth, itemHeight);
		Point size = maxSize(requestedSize, minSize);
		int imagePos = (size.x - imageBounds.width) / 2;
		int textPosX = (size.x - textExtent.x) / 2;


		imageBounds.x = imagePos;
		imageBounds.y = PADDING_TOP;

		Rectangle textBounds = new Rectangle(textPosX, imageBounds.y + imageBounds.height + SPACING, textExtent.x,
				textExtent.y);

		if (item.getState() == State.DOWN) {
			imageBounds.x++;
			imageBounds.y++;
			textBounds.x++;
			textBounds.y++;
		}

		return new Blueprint(minSize, imageBounds, textBounds);
	}

	private Blueprint getImageTextBlueprint(Point requestedSize) {
		final int PADDING_TOP = 7;
		final int PADDING_LEFT = 3;
		final int PADDING_RIGHT = 7;
		final int PADDING_BOT = 7;
		final int SPACING = 4;
		Rectangle imageBounds = item.getImage().getBounds();
		Point textExtent = getTextSize();

		imageBounds.x = PADDING_LEFT;
		imageBounds.y = PADDING_TOP;

		Rectangle textBounds = new Rectangle(imageBounds.x + imageBounds.width + SPACING, PADDING_TOP, textExtent.x,
				textExtent.y);

		int width = textBounds.x + textBounds.width + PADDING_RIGHT;

		int preferedImageHeight = imageBounds.y + imageBounds.height + PADDING_BOT;
		int preferedTextHeight = textBounds.y + textBounds.height + PADDING_BOT;
		int height = Math.max(preferedImageHeight, preferedTextHeight);
		Point minSize = new Point(width, height);
		Point size = maxSize(requestedSize, minSize);

		if (item.getState() == State.DOWN) {
			imageBounds.x++;
			imageBounds.y++;
			textBounds.x++;
			textBounds.y++;
		}

		return new Blueprint(size, imageBounds, textBounds);
	}

	private Point maxSize(Point sizeA, Point sizeB) {
		int width = Math.max(sizeA.x, sizeB.x);
		int height = Math.max(sizeA.y, sizeB.y);
		return new Point(width, height);
	}

	@Override
	public boolean isOnButton(Point location) {
		return bounds.contains(location);
	}
}