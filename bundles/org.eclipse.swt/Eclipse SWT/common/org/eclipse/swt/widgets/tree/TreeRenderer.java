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
package org.eclipse.swt.widgets.tree;

import java.util.List;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Tree.*;

/**
 *
 */
public class TreeRenderer implements ITreeRenderer {
	private final Tree tree;

	private TreeLayout cashedLayout;

	public TreeRenderer(Tree tree) {
		this.tree = tree;
	}

	@Override
	public void render(GC gc, Rectangle bounds, List<TreeItem> flatItems) {
		Point size = new Point(bounds.width, bounds.height);

		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		TreeLayout layout = layoutGenerator.computeLayout(size, flatItems);

		handleScrollBar(tree.getHorizontalBar(), bounds.width, layout.size().x);
		handleScrollBar(tree.getVerticalBar(), bounds.height, layout.size().y);
//		layout.dump();

		for (int i = 0; i < flatItems.size(); i++) {
			flatItems.get(i).render(gc, layout.bounds(i));
		}
	}

	// TODO maybe the scrollbar handling should be in the widget. ???
	private void handleScrollBar(ScrollBar scrollbar, int available, int required) {
		if (scrollbar == null) {
			return;
		}
		if (available < required) {
			scrollbar.setVisible(true);
		}else {
			scrollbar.setVisible(false);
		}
	}


	@Override
	public Point computeSize(Point sizeHint, List<TreeItem> items) {
		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		cashedLayout = layoutGenerator.computeLayout(sizeHint, items);

		Point preferedSize = cashedLayout.size();

		int usedWidth = sizeHint.x == -1 ? preferedSize.x : sizeHint.x;
		int usedHeight = sizeHint.y == -1 ? preferedSize.y : sizeHint.y;

		return new Point(usedWidth, usedHeight);
	}

	@Override
	public Rectangle getClientArea() {
		if (cashedLayout != null) {
			return cashedLayout.clientArea();
		} else {
			return new Rectangle(0, 0, 0, 0);
		}
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
