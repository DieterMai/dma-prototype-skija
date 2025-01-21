package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.ToolBar.IToolBarRenderer;

class ToolBarRenderer implements IToolBarRenderer {
	private static final int DEFAULT_WIDTH = 24;
	private static final int DEFAULT_HEIGHT = 22;


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
			Point itemBounds = item.render(gc, nextPos);

			nextPos += itemBounds.x + 7;
		}
	}



	@Override
	public int computeWidth() {
		int totalWidth = 0;
		for (int i = 0; i < toolbar.getItemCount(); i++) {
			totalWidth += toolbar.getItem(i).getWidth();
		}
		return totalWidth + 250; // TODO
	}

	@Override
	public int computeHeight() {
		return DEFAULT_HEIGHT + 50;
	}
}
