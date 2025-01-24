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

import org.eclipse.swt.widgets.*;

class SkijaGCHandler extends GCHandler {
	private final GC nativeGC;
	private final IGraphicsContext skijaGC;

	SkijaGCHandler(GC originalGC, IGraphicsContext skijaGC) {
		this.nativeGC = originalGC;
		this.skijaGC = skijaGC;
	}

	@Override
	public void close() {
		getGraphicsContext().commit();
		getGraphicsContext().dispose();
	}

	@Override
	public IGraphicsContext getGraphicsContext() {
		return skijaGC;
	}
}

