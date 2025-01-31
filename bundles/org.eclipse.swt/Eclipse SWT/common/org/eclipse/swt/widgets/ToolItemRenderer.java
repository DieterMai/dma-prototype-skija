package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class ToolItemRenderer {
	private static final Point IMAGE_SIZE = new Point(16, 16);
	private static final int PADDING = 3;
	private static final int HEIGHT = 43;
	private static final int WIDTH = 37;
	private static final int TEXT_PADDING = 3;

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	private final ToolBar bar;
	private final ToolItem item;

	ToolItemRenderer(ToolBar bar, ToolItem item) {
		this.bar = bar;
		this.item = item;
	}

	public Point render(IGraphicsContext gc, int pos) {
		Point size = switch (item.getStyleType()) {
		case SWT.CHECK -> drawSimpleItem(gc, pos);
		case SWT.PUSH -> drawSimpleItem(gc, pos);
		case SWT.RADIO -> drawSimpleItem(gc, pos);
		case SWT.SEPARATOR -> drawSeparatorItem(gc, pos);
		case SWT.DROP_DOWN -> drawDropDown(gc, pos);
		default -> new Point(WIDTH, HEIGHT);
		};

		gc.setBackground(bar.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(new Rectangle(pos, 0, size.x, size.y));
		return size;
	}

	private Point drawSimpleItem(IGraphicsContext gc, int position) {
		boolean hasImage = item.getImage() != null;
		boolean hasText = item.getText() != null && !item.getText().isBlank();

		if (hasImage && hasText) {
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
		} else if (hasImage) {
			Point imagePos = new Point(position + PADDING, PADDING);
			Point itemSize = new Point(IMAGE_SIZE.x + PADDING * 2, IMAGE_SIZE.y + PADDING * 2);

			Image image = item.getImage();
			gc.drawImage(image, imagePos.x, imagePos.y);

			return itemSize;
		} else if (hasText) {
			String text = item.getText();
			Point size = gc.textExtent(text, DRAW_FLAGS);

			gc.setForeground(getTextColor());
			gc.drawText(text, position + (WIDTH / 2) - (size.x / 2), 5);
			return new Point(WIDTH, HEIGHT);
		} else {
			return new Point(WIDTH, HEIGHT);// can this happen?
		}
	}

	private Color getTextColor() {
		if (bar.isEnabled()) {
			return bar.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		} else {
			return bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	private Point drawSeparatorItem(IGraphicsContext gc, int offset) {
		final int PADDING_START = 2;
		final int PADDING_END = 5;
		Point linePos = new Point(offset + PADDING_START, 0);
		Point size = new Point(PADDING_START + PADDING_END + 1, HEIGHT);
		if (bar.isFlat()) {
			gc.setForeground(bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			gc.drawLine(linePos.x, linePos.y, linePos.x, HEIGHT);
		}

		return size;
	}

	private Point drawDropDown(IGraphicsContext gc, int position) {
		Point itemSize = drawSimpleItem(gc, position);
		Point arrowSize = drawArrow(gc, position + itemSize.x);
		return new Point(itemSize.x + arrowSize.x, HEIGHT);
	}

	private Point drawArrow(IGraphicsContext gc, int position) {
		int PADDING = 4;
		int ARROW_WIDTH = 8;
		int ARROW_HEIGHT = 4;
		Point topLeft = new Point(position + PADDING, 5);
		Point topRight = new Point(topLeft.x + ARROW_WIDTH, topLeft.y);
		Point bottom = new Point(topLeft.x + 3, topLeft.y + ARROW_HEIGHT);

		gc.setBackground(bar.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.fillPolygon(new int[] { topLeft.x, topLeft.y, topRight.x, topRight.y, bottom.x, bottom.y });

		return new Point(PADDING * 2 + ARROW_WIDTH, HEIGHT);
	}
}
