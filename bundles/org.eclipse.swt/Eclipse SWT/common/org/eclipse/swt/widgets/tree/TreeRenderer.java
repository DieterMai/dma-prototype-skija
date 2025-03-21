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
	public void render(GC gc, Point size, Point origin, List<TreeItem> flatItems) {
		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		TreeLayout layout = layoutGenerator.computeLayout(size, origin, flatItems);

//		System.out.println("TreeRenderer.render() items: " + flatItems);
//		layout.dump();

		for (int i = 0; i < flatItems.size(); i++) {
			flatItems.get(i).render(gc, layout.bounds(i));
		}
	}

	@Override
	public Point computeSize(Point sizeHint, List<TreeItem> items) {
		Point preferedSize = computeContentSize(sizeHint, items);

		int usedWidth = sizeHint.x == -1 ? preferedSize.x : sizeHint.x;
		int usedHeight = sizeHint.y == -1 ? preferedSize.y : sizeHint.y;

		return new Point(usedWidth, usedHeight);
	}

	@Override
	public Point computeContentSize(Point sizeHint, List<TreeItem> items) {
		TreeLayoutGenerator layoutGenerator = new TreeLayoutGenerator();
		cashedLayout = layoutGenerator.computeLayout(sizeHint, new Point(0, 0), items);

		return cashedLayout.size();
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
