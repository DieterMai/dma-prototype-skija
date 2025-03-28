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
	public TreeLayout computeLayout(Point treeSize, Point origin, List<TreeItem> items) {
		Point[] sizeArray = collectSizes(items);
		Point[] positionArray = computePositions(origin, sizeArray);
		List<TreeItemRecord> itemRecords = createItemRecords(sizeArray, positionArray);

		int lastIndex = sizeArray.length - 1;
		int preferedHeight = sizeArray[lastIndex].y + positionArray[lastIndex].y;

		int preferedWidth = 0;
		for (Point size : sizeArray) {
			preferedWidth = Math.max(preferedWidth, size.x);
		}

		Rectangle clientArea = new Rectangle(0, 0, preferedWidth, preferedHeight); // TODO why do we need this?

		Point size = new Point(preferedWidth, preferedHeight);

		return new TreeLayout(size, clientArea, List.copyOf(itemRecords));
	}

	private Point[] collectSizes(List<TreeItem> items) {
		Point[] sizeArry = new Point[items.size()];
		for (int i = 0; i < items.size(); i++) {
			sizeArry[i] = items.get(i).getSize();
		}
		return sizeArry;
	}

	private Point[] computePositions(Point origin, Point[] sizeArray) {
		int yOffset = origin.y;
		int xOffset = origin.x;
		Point[] positionArray = new Point[sizeArray.length];

		for (int i = 0; i < sizeArray.length; i++) {
			positionArray[i] = new Point(xOffset, yOffset);
			yOffset += sizeArray[i].y;
		}
		return positionArray;
	}

	private List<TreeItemRecord> createItemRecords(Point[] sizeArray, Point[] positionArray) {
		List<TreeItemRecord> records = new ArrayList<>();
		for (int i = 0; i < sizeArray.length; i++) {
			var bounds = new Rectangle(positionArray[i].x, positionArray[i].y, sizeArray[i].x, sizeArray[i].y);
			records.add(new TreeItemRecord(i, bounds));
		}

		return records;
	}
}
