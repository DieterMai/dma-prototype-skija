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

	public static GCHandler of(Event event, Control control) {
		GC nativeGC = GCUtil.toNativeGC(event.gc, control);
		return of(nativeGC, control);
	}

	public static GCHandler of(GC nativeGC, Control control) {
		if (SWT.USE_SKIJA) {
			return new SkijaGCHandler(nativeGC, new SkijaGC(nativeGC));
		} else {
			return new NativeGCHandler(nativeGC, control);
		}
	}


	public abstract IGraphicsContext getGraphicsContext();

	@Override
	public abstract void close();
}
