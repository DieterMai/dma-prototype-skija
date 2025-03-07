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

		layout.dump();

		for (int i = 0; i < tree.getItemCount(); i++) {
			items[i].render(gc, layout.bounds(i));
		}
	}

	@Override
	public Point computeSize(Point size) {
		return new Point(150, 150);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
