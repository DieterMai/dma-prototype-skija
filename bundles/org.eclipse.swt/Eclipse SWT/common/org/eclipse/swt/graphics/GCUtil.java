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

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

public class GCUtil {
	public static IGraphicsContext toGraphicsContext(Control control) {
		return SWT.USE_SKIJA ? new SkijaGC(new GC(control), null) : new GC(control);
	}

	public static GC toNativeGC(Event event) {
		return Objects.requireNonNullElseGet(event.gc, () -> new GC(event.display));
	}

	public static GC toNativeGC(GC gc, Control control) {
		return Objects.requireNonNullElseGet(gc, () -> new GC(control.getDisplay()));
	}
}
