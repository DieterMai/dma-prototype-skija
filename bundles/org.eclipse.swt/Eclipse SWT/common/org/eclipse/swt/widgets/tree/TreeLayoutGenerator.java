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

import java.util.*;
import java.util.List;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.tree.TreeLayout.*;

public class TreeLayoutGenerator {
	public TreeLayout computeLayout(Point treeSize, TreeItem[] items, ScrollBar hScrollBar, ScrollBar vScrollBar) {
		Point[] sizeArray = collectSizes(items);
		int[] positionArray = computePositions(sizeArray);
		List<TreeItemRecord> itemRecords = createItemRecords(sizeArray, positionArray);

		int lastIndex = sizeArray.length - 1;
		int preferedHeight = sizeArray[lastIndex].y + positionArray[lastIndex];

		int preferedWidth = 0;
		for (Point size : sizeArray) {
			preferedWidth = Math.max(preferedWidth, size.x);
		}

		if (vScrollBar != null) {
			preferedWidth += vScrollBar.getSize().x;
		}
		if (hScrollBar != null) {
			preferedHeight += hScrollBar.getSize().y;
		}

		return new TreeLayout(new Point(preferedWidth, preferedHeight), List.copyOf(itemRecords));
	}

	private Point[] collectSizes(TreeItem[] items) {
		Point[] sizeArry = new Point[items.length];
		for (int i = 0; i < items.length; i++) {
			sizeArry[i] = items[i].getSize();
		}
		return sizeArry;
	}

	private int[] computePositions(Point[] sizeArray) {
		int yOffset = 0;
		int[] positionArray = new int[sizeArray.length];
		for (int i = 0; i < sizeArray.length; i++) {
			positionArray[i] = yOffset;
			yOffset += sizeArray[i].y;
		}
		return positionArray;
	}

	private List<TreeItemRecord> createItemRecords(Point[] sizeArray, int[] positionArray) {
		List<TreeItemRecord> records = new ArrayList<>();
		for (int i = 0; i < sizeArray.length; i++) {
			records.add(new TreeItemRecord(i, new Rectangle(0, positionArray[i], sizeArray[i].x, sizeArray[i].x)));
		}

		return records;
	}
}
