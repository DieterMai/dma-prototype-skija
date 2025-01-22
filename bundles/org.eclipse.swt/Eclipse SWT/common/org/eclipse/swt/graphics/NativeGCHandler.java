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

class NativeGCHandler extends GCHandler {
	private final Image doubleBufferingImage;
	private final IGraphicsContext gc;

	NativeGCHandler(GC originalGC, Control control) {
		super(originalGC);
		Display display = control.getDisplay();
		Rectangle r = control.getBounds();

		initializeGC(getNativeGC(), control);

		if (SWT.getPlatform().equals("win32")) {
			// Use double buffering on windows
			doubleBufferingImage = new Image(display, r.width, r.height);
			getNativeGC().copyArea(doubleBufferingImage, 0, 0);
			GC doubleBufferingGC = new GC(doubleBufferingImage);
			doubleBufferingGC.setForeground(getNativeGC().getForeground());
			doubleBufferingGC.setBackground(getBackgroundColor());
			doubleBufferingGC.setAntialias(SWT.ON);
			doubleBufferingGC.fillRectangle(0, 0, r.width, r.height);
			gc = doubleBufferingGC;
		} else {
			doubleBufferingImage = null;
			gc = getNativeGC();
		}
	}

	@Override
	public IGraphicsContext getGraphicsContext() {
		return gc;
	}

	@Override
	public void close() {
		gc.commit();
		gc.dispose();
		if (doubleBufferingImage != null) {
			getNativeGC().drawImage(doubleBufferingImage, 0, 0);
			doubleBufferingImage.dispose();
		}
		getNativeGC().dispose();
	}
}
