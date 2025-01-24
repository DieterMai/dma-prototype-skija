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
	private final GC bufferdGC;
	private final GC originalGC;

	NativeGCHandler(GC nativeGC, Control control) {
		Display display = control.getDisplay();
		Rectangle r = control.getBounds();

		Rectangle bounds = control.getBounds();
		nativeGC.setForeground(control.getForeground());
		nativeGC.setBackground(control.getBackground());
		nativeGC.setClipping(new Rectangle(0, 0, bounds.width, bounds.height));
		nativeGC.setAntialias(SWT.ON);

		if (SWT.getPlatform().equals("win32")) {
			// Use double buffering on windows
			doubleBufferingImage = new Image(display, r.width, r.height);
			nativeGC.copyArea(doubleBufferingImage, 0, 0);
			bufferdGC = new GC(doubleBufferingImage);
			bufferdGC.setForeground(nativeGC.getForeground());
			bufferdGC.setBackground(GCUtil.getBackground(nativeGC, control));
			bufferdGC.setAntialias(SWT.ON);
			bufferdGC.fillRectangle(0, 0, r.width, r.height);
			originalGC = nativeGC;
		} else {
			doubleBufferingImage = null;
			bufferdGC = null;
			originalGC = nativeGC;
		}
	}

	@Override
	public IGraphicsContext getGraphicsContext() {
		return bufferdGC;
	}

	@Override
	public void close() {
		bufferdGC.commit();
		bufferdGC.dispose();
		if (doubleBufferingImage != null) {
			originalGC.drawImage(doubleBufferingImage, 0, 0);
			doubleBufferingImage.dispose();
		}
		originalGC.dispose();
	}
}
