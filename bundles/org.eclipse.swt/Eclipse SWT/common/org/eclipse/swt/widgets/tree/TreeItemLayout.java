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

public record TreeItemLayout(Point size, int indent, ChildIndicator childIndicator, List<Rectangle> boundsList) {
	public enum ChildIndicator {
		NONE, OPEN, CLOSED
	}
}
