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
	private static final int PADDING_IMAGE = 3;
	private static final int PADDING_TEXT_H = 5;
	private static final int PADDING_TEXT_TOP = 9;
	private static final int PADDING_TEXT_BOT = 7;

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
		case TEXT_AND_IMAGE -> drawTextImageItem(gc, pos);
		case ONLY_IMAGE -> drawImageItem(gc, pos);
		case ONLY_TEXT -> drawTextItem(gc, pos);
		};
	}

	private Point drawTextImageItem(IGraphicsContext gc, int position) {
		Point imageSize = getPreferedImageSize();
		Point textSize = getPreferedTextSize();

		int itemWidth = Math.max(imageSize.x, textSize.x);
		int itemHeight = imageSize.y + textSize.y;
		Point itemSize = new Point(itemWidth, itemHeight);

		Point imagePos = new Point(position + (itemSize.x - imageSize.x) / 2, PADDING_IMAGE);
		Point textPoint = new Point(position + (itemSize.x - imageSize.x) / 2, imagePos.y + imageSize.y);

		Image image = item.getImage();
		gc.drawImage(image, imagePos.x, imagePos.y);

		String text = item.getText();
//
		gc.setForeground(getTextColor());
		gc.drawText(text, position + textPoint.x, textPoint.y);

		return itemSize;
	}

	private Point drawImageItem(IGraphicsContext gc, int position) {
		Point imagePos = new Point(position + PADDING_IMAGE, PADDING_IMAGE);
		Point itemSize = getPreferedImageSize();

		Image image = item.getImage();
		gc.drawImage(image, imagePos.x, imagePos.y);

		return itemSize;
	}

	private Point drawTextItem(IGraphicsContext gc, int position) {
		String text = item.getText();
		Point itemSize = getPreferedTextSize();

		gc.setForeground(getTextColor());
		gc.drawText(text, position + PADDING_TEXT_H, PADDING_TEXT_TOP);
		return itemSize;
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
		case TEXT_AND_IMAGE -> getPreferedTextImageSize().y;
		case ONLY_IMAGE -> getPreferedImageSize().x;
		case ONLY_TEXT -> getPreferedTextSize().x;
		};
	}

	@Override
	public int getPreferedHeight() {
		ContentType type = getContenType();
		return switch (type) {
		case TEXT_AND_IMAGE -> getPreferedTextImageSize().y;
		case ONLY_IMAGE -> getPreferedImageSize().y;
		case ONLY_TEXT -> getPreferedTextSize().y;
		};
	}

	private Point getPreferedImageSize() {
		return new Point(IMAGE_SIZE.x + PADDING_IMAGE * 2, IMAGE_SIZE.y + PADDING_IMAGE * 2);
	}

	private Point getPreferedTextSize() {
		IGraphicsContext gc = getSimpleGC();
		gc.setFont(bar.getFont());
		Point textExtent = gc.textExtent(item.getText(), DRAW_FLAGS);

		int w = PADDING_TEXT_H + textExtent.x + PADDING_TEXT_H;
		int h = PADDING_TEXT_TOP + textExtent.y + PADDING_TEXT_BOT;
		return new Point(w, h);
	}

	private Point getPreferedTextImageSize() {
		return new Point(30, 50);
	}

	private IGraphicsContext getSimpleGC() {
		GC originalGC = new GC(bar);
		return SWT.USE_SKIJA ? new SkijaGC(originalGC, null) : originalGC;
	}


}