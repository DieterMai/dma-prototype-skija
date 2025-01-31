package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class ToolItemRenderer {
	private static final int SEPARATOR_PADDING_START = 2;
	private static final int SEPARATOR_PADDING_END = 5;

	private static final int ARROW_WIDTH = 8;
	private static final int ARROW_HEIGHT = 4;

	private static final Point IMAGE_SIZE = new Point(16, 16);
	private static final int PADDING = 3;
	private static final int HEIGHT = 43;
	private static final int WIDTH = 37;
	private static final int TEXT_PADDING = 3;

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	private final ToolBar bar;
	private final ToolItem item;

	private enum ContentType {
		ONLY_TEXT, ONLY_IMAGE, TEXT_AND_IMAGE
	}

	ToolItemRenderer(ToolBar bar, ToolItem item) {
		this.bar = bar;
		this.item = item;
	}

	private ContentType getContentYpe() {
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

	public Point render(IGraphicsContext gc, int pos, int maxSze) {
		Point size = switch (item.getStyleType()) {
		case SWT.CHECK -> drawSimpleItem(gc, pos);
		case SWT.PUSH -> drawSimpleItem(gc, pos);
		case SWT.RADIO -> drawSimpleItem(gc, pos);
		case SWT.SEPARATOR -> drawSeparatorItem(gc, pos, maxSze);
		case SWT.DROP_DOWN -> drawDropDown(gc, pos);
		default -> new Point(WIDTH, HEIGHT);
		};

//		gc.setBackground(bar.getDisplay().getSystemColor(SWT.COLOR_BLACK));
//		gc.drawRectangle(new Rectangle(pos, 0, size.x, size.y));
		return size;
	}

	private Point drawSimpleItem(IGraphicsContext gc, int position) {
		ContentType type = getContentYpe();
		return switch (type) {
		case TEXT_AND_IMAGE -> drawSimpleTextImage(gc, position);
		case ONLY_IMAGE -> drawSimpleImage(gc, position);
		case ONLY_TEXT -> drawSimpleText(gc, position);
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

	private int getPreferedPushWidth() {
		ContentType type = getContentYpe();
		return switch (type) {
		case TEXT_AND_IMAGE -> 5;
		case ONLY_IMAGE -> IMAGE_SIZE.x + PADDING * 2;
		case ONLY_TEXT -> 5;
		};
	}

	private int getPreferedPushHeight() {
		ContentType type = getContentYpe();
		return switch (type) {
		case TEXT_AND_IMAGE -> 5;
		case ONLY_IMAGE -> IMAGE_SIZE.y + PADDING * 2;
		case ONLY_TEXT -> 5;
		};
	}
	private Color getTextColor() {
		if (bar.isEnabled()) {
			return bar.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		} else {
			return bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	private Point drawSeparatorItem(IGraphicsContext gc, int offset, int maxSze) {
		Point linePos = new Point(offset + SEPARATOR_PADDING_START, 0);
		Point size = new Point(getPreferedSeparatorWidth(), maxSze);
		if (bar.isFlat()) {
			gc.setForeground(bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			gc.drawLine(linePos.x, linePos.y, linePos.x, maxSze);
		}

		return size;
	}

	private int getPreferedSeparatorWidth() {
		return SEPARATOR_PADDING_START + SEPARATOR_PADDING_END + 1;
	}

	private Point drawDropDown(IGraphicsContext gc, int position) {
		Point itemSize = drawSimpleItem(gc, position);
		Point arrowSize = drawArrow(gc, position + itemSize.x);
		return new Point(itemSize.x + arrowSize.x, itemSize.y);
	}

	private Point drawArrow(IGraphicsContext gc, int position) {
		int PADDING = 4;
		Point topLeft = new Point(position + PADDING, 5);
		Point topRight = new Point(topLeft.x + ARROW_WIDTH, topLeft.y);
		Point bottom = new Point(topLeft.x + 3, topLeft.y + ARROW_HEIGHT);

		gc.setBackground(bar.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.fillPolygon(new int[] { topLeft.x, topLeft.y, topRight.x, topRight.y, bottom.x, bottom.y });

		return new Point(PADDING * 2 + ARROW_WIDTH, HEIGHT);
	}

	public int getPreferedWidth() {
		return switch (item.getStyleType()) {
		case SWT.CHECK, SWT.PUSH, SWT.RADIO -> getPreferedPushWidth();
		case SWT.SEPARATOR -> getPreferedSeparatorWidth();
		case SWT.DROP_DOWN -> getPreferedDropDownWidth();
		default -> 5; // TODO
		};
	}

	public int getPreferedHeight() {
		return switch (item.getStyleType()) {
		case SWT.CHECK, SWT.PUSH, SWT.RADIO -> getPreferedPushHeight();
		case SWT.SEPARATOR -> 1; // TODO
		case SWT.DROP_DOWN -> getPreferedDropDownHeight();
		default -> 5; // TODO
		};
	}

	private int getPreferedDropDownWidth() {
		return getPreferedPushWidth() + getPreferedArrowWidth();
	}

	private int getPreferedDropDownHeight() {
		return getPreferedPushHeight();
	}

	private int getPreferedArrowWidth() {
		return PADDING * 2 + ARROW_WIDTH;
	}

	private int calculateArrowHeight() {
		return 5 + 3 + 5; // TODO
	}
}
