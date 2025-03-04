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
		for(TreeItem item : tree.getItems()) {
			item.render(gc, bounds);
		}
		NOT_IMPLEMENTED();

	}

	@Override
	public Point computeSize(Point size) {
		return new Point(150, 150);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
