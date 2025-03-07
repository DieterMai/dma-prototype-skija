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
		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		TreeLayout layout = layoutGenerator.computeLayout(new Point(bounds.width, bounds.height), items);


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
	public Point computeSize(Point size) {
		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		TreeLayout layout = layoutGenerator.computeLayout(size, tree.getItems());

		int height = layout.size().x;
		int width = layout.size().y;
		// add ScrollBar size
		if (tree.horizontalBar != null) {
			height += tree.horizontalBar.getSize().y;
		}
		if (tree.verticalBar != null) {
			width += tree.verticalBar.getSize().x;
		}

		return new Point(height, width);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
