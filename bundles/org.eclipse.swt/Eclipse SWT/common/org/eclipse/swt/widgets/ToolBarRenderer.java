package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.ToolBar.IToolBarRenderer;

class ToolBarRenderer implements IToolBarRenderer {
	private static final int DEFAULT_WIDTH = 24;
	private static final int DEFAULT_HEIGHT = 22;

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	private static Color background;

	private final ToolBar toolbar;

	ToolBarRenderer(ToolBar toolbar) {
		this.toolbar = toolbar;
	}

	@Override
	public void render(GC nativeGc, Rectangle bounds) {
		initBackground(nativeGc, bounds);

		IGraphicsContext sgc = initSkijaGc(nativeGc, bounds);

		renderToolbar(sgc, 0, 0, bounds.width - 1, bounds.height - 1);

		sgc.commit();
		sgc.dispose();
	}

	private void initBackground(GC originalGC, Rectangle bounds) {
		if (SWT.getPlatform().equals("win32") | SWT.getPlatform().equals("gtk")) {
			// Extract background color on first execution
			if (background == null) {
				extractAndStoreBackgroundColor(bounds, originalGC);
			}
			toolbar.style |= SWT.NO_BACKGROUND;
		}
	}

	private void extractAndStoreBackgroundColor(Rectangle r, GC originalGC) {
		Image backgroundColorImage = new Image(toolbar.getDisplay(), r.width, r.height);
		originalGC.copyArea(backgroundColorImage, 0, 0);
		int pixel = backgroundColorImage.getImageData().getPixel(0, 0);
		backgroundColorImage.dispose();
		background = SWT.convertPixelToColor(pixel);
	}

	public IGraphicsContext initSkijaGc(GC originalGC, Rectangle bounds) {
		IGraphicsContext gc = new SkijaGC(originalGC, background);

		originalGC.setClipping(bounds.x, bounds.y, bounds.width, bounds.height);

		originalGC.setForeground(toolbar.getForeground());
		originalGC.setBackground(toolbar.getBackground());
		originalGC.setClipping(new Rectangle(0, 0, bounds.width, bounds.height));
		originalGC.setAntialias(SWT.ON);

		return gc;
	}

	private void renderToolbar(IGraphicsContext gc, int x, int y, int w, int h) {
		// just to see where the widget is
		gc.setForeground(toolbar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		gc.fillRectangle(x, y, w, h);

		int nextPos = 3;
		for (int i = 0; i < toolbar.getItemCount(); i++) {
			System.out.println("ToolBarRenderer.renderToolbar() render item " + i);
			ToolItem item = toolbar.getItem(i);
			int width = switch(item.getStyleType()) {
			case SWT.CHECK -> drawSimpleItem(gc, item, nextPos);
			case SWT.PUSH -> drawSimpleItem(gc, item, nextPos);
			case SWT.RADIO -> drawSimpleItem(gc, item, nextPos);
			case SWT.SEPARATOR -> drawSeparatorItem(gc, item, nextPos, w, h);
			case SWT.DROP_DOWN -> drawDropDown(gc, item, nextPos);
			default -> 0;
			};

			nextPos += width + 7;

		}
	}

	private int drawSimpleItem(IGraphicsContext gc, ToolItem item, int position) {
		boolean hasImage = item.getImage() != null;
		boolean hasText = item.getText() != null && !item.getText().isBlank();

		if(hasImage && hasText) {
			Image image = item.getImage();
			Rectangle imageBounds = image.getBounds();

			final int TEXT_PADDING = 2;
			String text = item.getText();
			Point textSize = gc.textExtent(text, DRAW_FLAGS);

			int maxWidth = Math.max(imageBounds.width, textSize.x + TEXT_PADDING * 2);

			int imagePosition = position + (maxWidth / 2) - (imageBounds.width / 2);
			int textPosition = position + (maxWidth / 2) - (textSize.x / 2);

			gc.drawImage(image, imagePosition, 0);

			gc.setForeground(getTextColor());
			gc.drawText(text, textPosition + TEXT_PADDING, imageBounds.height + 5);

			return maxWidth;
		}else if(hasImage) {
			Image image = item.getImage();
			gc.drawImage(image, position, 0);

			Rectangle imageBounds = image.getBounds();
			return imageBounds.width;
		}else if(hasText) {
			final int PADDING = 2;
			String text = item.getText();
			Point size = gc.textExtent(text, DRAW_FLAGS);

			gc.setForeground(getTextColor());
			gc.drawText(text, position + PADDING, 5);
			return size.x + PADDING * 2;
		}else {
			return 0;
		}
	}

	private Color getTextColor() {
		if (toolbar.isEnabled()) {
			return toolbar.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		} else {
			return toolbar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	private int drawSeparatorItem(IGraphicsContext gc, ToolItem item, int position, int w, int h) {
		if (toolbar.isFlat()) {
			gc.setForeground(toolbar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			gc.drawLine(position, 0, position, h);
		}

		return 2;
	}

	private int drawDropDown(IGraphicsContext gc, ToolItem item, int position) {
		int width = 0;
		width += drawSimpleItem(gc, item, position);
		width += drawArrow(gc, item, position + width);
		return width;
	}

	private int drawArrow(IGraphicsContext gc, ToolItem item, int position) {
		Point topLeft = new Point(position + 7, 5);
		Point topRight = new Point(topLeft.x + 8, topLeft.y);
		Point bottom = new Point(topLeft.x + 3, topLeft.y + 4);

		gc.setBackground(toolbar.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.fillPolygon(new int[] { topLeft.x, topLeft.y, topRight.x, topRight.y, bottom.x, bottom.y });

		return 7;
	}

	@Override
	public int computeWidth() {
		int totalWidth = 0;
		for (int i = 0; i < toolbar.getItemCount(); i++) {
			totalWidth += toolbar.getItem(i).getWidth();
		}
		return totalWidth + 200; // TODO
	}

	@Override
	public int computeHeight() {
		return DEFAULT_HEIGHT + 50;
	}
}
