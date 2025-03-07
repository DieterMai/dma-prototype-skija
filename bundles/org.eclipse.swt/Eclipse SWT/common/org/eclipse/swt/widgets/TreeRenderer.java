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

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Tree.*;
import org.eclipse.swt.widgets.tree.*;

/**
 *
 */
public class TreeRenderer implements ITreeRenderer {
	private final Tree tree;

	public TreeRenderer(Tree tree) {
		this.tree = tree;
	}

	@Override
	public void render(GC gc, Rectangle bounds) {
		TreeItem[] items = tree.getItems();
		Point size = new Point(bounds.width, bounds.height);

		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		TreeLayout layout = layoutGenerator.computeLayout(size, items, tree.horizontalBar, tree.verticalBar);

		handleScrollBar(tree.horizontalBar, bounds.width, layout.size().x);
		handleScrollBar(tree.verticalBar, bounds.height, layout.size().y);
		layout.dump();

		for (int i = 0; i < tree.getItemCount(); i++) {
			items[i].render(gc, layout.bounds(i));
		}
	}

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
	public Point computeSize(Point sizeHint) {
		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		TreeLayout layout = layoutGenerator.computeLayout(sizeHint, tree.getItems(), tree.horizontalBar, tree.verticalBar);

		Point preferedSize = layout.size();
		int usedWidth = sizeHint.x == -1 ? preferedSize.x : sizeHint.x;
		int usedHeight = sizeHint.y == -1 ? preferedSize.y : sizeHint.y;

		return new Point(usedWidth, usedHeight);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
