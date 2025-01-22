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

import java.util.*;

import org.eclipse.swt.widgets.*;

class SkijaGCHandler extends GCHandler {
	private final IGraphicsContext gc;

	SkijaGCHandler(GC originalGC, Control control) {
		super(originalGC);
		GC originalGc = Objects.requireNonNullElseGet(originalGC, () -> new GC(control.getDisplay()));

		initializeGC(originalGc, control);

		gc = new SkijaGC(originalGc, getBackgroundColor());
	}

	@Override
	public IGraphicsContext getGraphicsContext() {
		return gc;
	}

	@Override
	public void close() {
		gc.commit();
		gc.dispose();
	}
}

