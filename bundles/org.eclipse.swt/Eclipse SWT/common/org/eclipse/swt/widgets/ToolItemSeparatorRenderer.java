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
package org.eclipse.swt.widgets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

public class ToolItemSeparatorRenderer implements ToolItemRenderer {
	private static final int SEPARATOR_PADDING_START = 2;
	private static final int SEPARATOR_PADDING_END = 5;

	private final ToolBar bar;

	ToolItemSeparatorRenderer(ToolBar bar) {
		this.bar = bar;
	}

	@Override
	public Point render(IGraphicsContext gc, int pos, int maxSze) {
		return drawSeparatorItem(gc, pos, maxSze);
	}


	private Point drawSeparatorItem(IGraphicsContext gc, int offset, int maxSze) {
		Point linePos = new Point(offset + SEPARATOR_PADDING_START, 0);
		Point size = new Point(getPreferedWidth(), maxSze);
		if (bar.isFlat()) {
			gc.setForeground(bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			gc.drawLine(linePos.x, linePos.y, linePos.x, maxSze);
		}

		return size;
	}

	@Override
	public int getPreferedWidth() {
		return SEPARATOR_PADDING_START + SEPARATOR_PADDING_END + 1;
	}

	@Override
	public int getPreferedHeight() {
		return -1;
	}
}
