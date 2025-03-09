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

import org.eclipse.swt.graphics.*;

public record TreeLayout(Point size, Rectangle clientArea, List<TreeItemRecord> itemRecords) {
	public static record TreeItemRecord(int index, Rectangle bounds) {
	}

	public Rectangle bounds(int i) {
		return itemRecords.get(i).bounds();
	}

	public void dump() {
		System.out.println("Size: " + size);
		for (TreeItemRecord itemRecord : itemRecords) {
			System.out.println(itemRecord);
		}
	}
}
