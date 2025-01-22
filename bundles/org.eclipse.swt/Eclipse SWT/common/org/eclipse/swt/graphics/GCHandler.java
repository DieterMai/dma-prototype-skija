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
package org.eclipse.swt.graphics;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public abstract class GCHandler implements AutoCloseable {
	private static boolean requiresBackground = SWT.getPlatform().equals("win32") | SWT.getPlatform().equals("gtk");
	private static Color background;

	public static GCHandler of(Event event, Control control) {
		if (SWT.USE_SKIJA) {
			return new SkijaGCHandler(GCUtil.toNativeGC(event), control);
		} else {
			return new NativeGCHandler(GCUtil.toNativeGC(event), control);
		}
	}

	public static GCHandler of(GC originalGC, Control control) {
		if (SWT.USE_SKIJA) {
			return new SkijaGCHandler(originalGC, control);
		} else {
			return new NativeGCHandler(originalGC, control);
		}
	}

	private final GC nativeGC;

	protected GCHandler(GC originalGC) {
		this.nativeGC = originalGC;
	}

	public abstract IGraphicsContext getGraphicsContext();

	@Override
	public abstract void close();

	protected GC getNativeGC() {
		return nativeGC;
	}

	public Color getBackgroundColor() {
		initializeBackgournd();
		return background;

	}

	public void fillBackground(Rectangle bounds) {
		if (getBackgroundColor() != null) {
			getGraphicsContext().setBackground(background);
			getGraphicsContext().fillRectangle(bounds);
		}
	}

	public int applyStyle(int style) { // TODO remove
		if (background != null) {
			return style | SWT.NO_BACKGROUND;
		} else {
			return style;
		}
	}

	private void initializeBackgournd() {
		if (background == null && requiresBackground) {
			// Extract background color on first execution
			background = extractAndStoreBackgroundColor();
		}
	}

	private Color extractAndStoreBackgroundColor() {
		Image backgroundColorImage = new Image(nativeGC.getDevice(), 1, 1);
		nativeGC.copyArea(backgroundColorImage, 0, 0);
		int pixel = backgroundColorImage.getImageData().getPixel(0, 0);
		backgroundColorImage.dispose();
		return SWT.convertPixelToColor(pixel);
	}

	protected void initializeGC(GC gc, Control control) {
		Rectangle bounds = control.getBounds();
		nativeGC.setForeground(control.getForeground());
		nativeGC.setBackground(control.getBackground());
		nativeGC.setClipping(new Rectangle(0, 0, bounds.width, bounds.height));
		nativeGC.setAntialias(SWT.ON);
	}


}
