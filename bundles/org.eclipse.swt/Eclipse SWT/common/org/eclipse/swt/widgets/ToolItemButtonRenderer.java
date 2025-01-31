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

public class ToolItemButtonRenderer implements ToolItemRenderer {
	private static final Point IMAGE_SIZE = new Point(16, 16);
	private static final int PADDING = 3;
	private static final int HEIGHT = 43;
	private static final int WIDTH = 37;
	private static final int TEXT_PADDING = 3;

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	private final ToolBar bar;
	private final ToolItem item;

	public static enum ContentType {
		ONLY_TEXT, ONLY_IMAGE, TEXT_AND_IMAGE
	}

	ToolItemButtonRenderer(ToolBar bar, ToolItem item) {
		this.bar = bar;
		this.item = item;
	}

	private ContentType getContenType() {
		boolean hasImage = item.getImage() != null;
		boolean hasText = item.getText() != null && !item.getText().isBlank();
		if (hasImage && hasText) {
			return ContentType.TEXT_AND_IMAGE;
		} else if (hasImage) {
			return ContentType.ONLY_IMAGE;
		} else {
			return ContentType.ONLY_TEXT;
		}
	}

	@Override
	public Point render(IGraphicsContext gc, int pos, int maxSize) {
		ContentType type = getContenType();
		return switch (type) {
		case TEXT_AND_IMAGE -> drawSimpleTextImage(gc, pos);
		case ONLY_IMAGE -> drawSimpleImage(gc, pos);
		case ONLY_TEXT -> drawSimpleText(gc, pos);
		};
	}

	private Point drawSimpleTextImage(IGraphicsContext gc, int position) {
		Image image = item.getImage();
		Rectangle imageBounds = image.getBounds();

		String text = item.getText();
		Point textSize = gc.textExtent(text, DRAW_FLAGS);

		int maxWidth = Math.max(imageBounds.width, textSize.x + TEXT_PADDING * 2);
		int totalHeight = imageBounds.height + textSize.y;

		int imagePosition = position + (maxWidth / 2) - (imageBounds.width / 2);
		int textPosition = position + (maxWidth / 2) - (textSize.x / 2);

		gc.drawImage(image, imagePosition, 0);

		gc.setForeground(getTextColor());
		gc.drawText(text, textPosition + TEXT_PADDING, imageBounds.height + 5);

		return new Point(maxWidth, totalHeight);
	}

	private Point drawSimpleImage(IGraphicsContext gc, int position) {
		Point imagePos = new Point(position + PADDING, PADDING);
		Point itemSize = new Point(IMAGE_SIZE.x + PADDING * 2, IMAGE_SIZE.y + PADDING * 2);

		Image image = item.getImage();
		gc.drawImage(image, imagePos.x, imagePos.y);

		return itemSize;
	}

	private Point drawSimpleText(IGraphicsContext gc, int position) {
		String text = item.getText();
		Point size = gc.textExtent(text, DRAW_FLAGS);

		gc.setForeground(getTextColor());
		gc.drawText(text, position + (WIDTH / 2) - (size.x / 2), 5);
		return new Point(WIDTH, HEIGHT);
	}

	private Color getTextColor() {
		if (bar.isEnabled()) {
			return bar.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		} else {
			return bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	@Override
	public int getPreferedWidth() {
		ContentType type = getContenType();
		return switch (type) {
		case TEXT_AND_IMAGE -> 5; // TODO
		case ONLY_IMAGE -> IMAGE_SIZE.x + PADDING * 2;
		case ONLY_TEXT -> 5; // TODO
		};
	}

	@Override
	public int getPreferedHeight() {
		ContentType type = getContenType();
		return switch (type) {
		case TEXT_AND_IMAGE -> 5; // TODO
		case ONLY_IMAGE -> IMAGE_SIZE.y + PADDING * 2;
		case ONLY_TEXT -> 5; // TODO
		};
	}
}