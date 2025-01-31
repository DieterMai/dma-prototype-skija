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

public class ToolItemDropDownRenderer implements ToolItemRenderer {
	private static final int ARROW_PADDING = 4;
	private static final int ARROW_WIDTH = 8;
	private static final int ARROW_HEIGHT = 4;

	private final ToolBar bar;
	private final ToolItemButtonRenderer button;

	ToolItemDropDownRenderer(ToolBar bar, ToolItem item) {
		this.bar = bar;
		this.button = new ToolItemButtonRenderer(bar, item);
	}

	@Override
	public Point render(IGraphicsContext gc, int pos, int maxSze) {
		Point buttonSize = button.render(gc, pos, maxSze);
		Point arrowSize = drawArrow(gc, pos + buttonSize.x, buttonSize);
		return new Point(buttonSize.x + arrowSize.x, buttonSize.y);
	}

	private Point drawArrow(IGraphicsContext gc, int position, Point buttonSize) {
		Point topLeft = new Point(position + ARROW_PADDING, 5);
		Point topRight = new Point(topLeft.x + ARROW_WIDTH, topLeft.y);
		Point bottom = new Point(topLeft.x + 3, topLeft.y + ARROW_HEIGHT);

		gc.setBackground(bar.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.fillPolygon(new int[] { topLeft.x, topLeft.y, topRight.x, topRight.y, bottom.x, bottom.y });

		return new Point(ARROW_PADDING * 2 + ARROW_WIDTH, buttonSize.y);
	}

	@Override
	public int getPreferedWidth() {
		return getPreferedDropDownWidth();
	}

	@Override
	public int getPreferedHeight() {
		return getPreferedDropDownHeight();
	}

	private int getPreferedDropDownWidth() {
		return button.getPreferedWidth() + getPreferedArrowWidth();
	}

	private int getPreferedDropDownHeight() {
		return button.getPreferedHeight();
	}

	private int getPreferedArrowWidth() {
		return ARROW_PADDING * 2 + ARROW_WIDTH;
	}
}
