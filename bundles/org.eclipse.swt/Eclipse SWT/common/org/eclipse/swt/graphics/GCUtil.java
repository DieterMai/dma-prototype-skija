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
	public static IGraphicsContext toGraphicsContext(GC nativeGC) {
		if (SWT.USE_SKIJA) {
			return new SkijaGC(nativeGC);
		} else {
			return nativeGC;
		}
	}

	public static IGraphicsContext toGraphicsContext(GC nativeGC, Color bgColor) {
		if (SWT.USE_SKIJA) {
			return new SkijaGC(nativeGC, bgColor);
		} else {
			return nativeGC;
		}
	}

	public static IGraphicsContext toGraphicsContext(Control control) {
		if (SWT.USE_SKIJA) {
			GC nativeGC = new GC(control);
			return new SkijaGC(nativeGC, null);
		} else {
			return new GC(control);
		}
	}

	public static GC toNativeGC(GC gc, Control control) {
		return Objects.requireNonNullElseGet(gc, () -> new GC(control));
	}
}
