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

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.TreeItem.*;

/**
 *
 */
public class TreeItemRenderer implements ITreeItemRenderer {
	private enum ChildIndicator {
		NONE, OPEN, CLOSED
	}

	private record TreeItemLayout(Point size, ChildIndicator childIndicator, Image image, Rectangle imageBounds, String text,
			Rectangle textBounds) {
	}

	private static final int DRAW_FLAGS = SWT.DRAW_MNEMONIC;

	private static final int[] CLOSED_POLILINE = { 8, 7, 12, 11, 8, 15 };
	private static final int[] OPEN_POLILINE = { 7, 7, 11, 12, 15, 7 };

	private final Tree tree;
	private final TreeItem item;

	public TreeItemRenderer(Tree tree, TreeItem item) {
		this.tree = tree;
		this.item = item;
	}

	@Override
	public void render(GC gc, Rectangle bounds) {
		Point size = new Point(bounds.width, bounds.height);
		Point offset = new Point(bounds.x, bounds.y);

		TreeItemLayout layout = computeLayout(size);
		renderLayout(gc, offset, layout);
	}

	private void renderLayout(GC gc, Point offset, TreeItemLayout layout) {
		drawChildIndicator(gc, offset, layout.childIndicator);


		if (layout.image != null) {
			Rectangle imageBounds = layout.imageBounds();
			gc.drawImage(item.image, offset.x + imageBounds.x, offset.y + imageBounds.y);
		}

		if (layout.text != null) {
			Rectangle textBounds = layout.textBounds();
			gc.setForeground(getTextColor()); // TODO move into render data
			gc.drawText(layout.text, offset.x + textBounds.x, offset.y + textBounds.y);
		}
	}

	private void drawChildIndicator(GC gc, Point offset, ChildIndicator childIndicator) {
		int[] relativeLine = switch (childIndicator) {
		case NONE -> null;
		case OPEN -> OPEN_POLILINE;
		case CLOSED -> CLOSED_POLILINE;
		};

		if (relativeLine == null) {
			return;
		}

		int[] absoluteLine = new int[relativeLine.length];
		for (int i = 0; i + 1 < absoluteLine.length; i += 2) {
			absoluteLine[i] = offset.x + relativeLine[i];
			absoluteLine[i + 1] = offset.y + relativeLine[i + 1];
		}

		gc.setForeground(new Color(139, 139, 139));
		gc.setAntialias(SWT.ON);
		gc.setLineWidth(2);
		gc.drawPolyline(absoluteLine);
	}

	private TreeItemLayout computeLayout(Point treeSize) {
		final int PADDING_HORIZONTAL = 21;
		final int PADDING_TOP = 1;
		final int DEFAULT_HEIGHT = 18;

		Image image = null;
		String text = null;


		// 1. Collect elements
		if (hasImage()) {
			image = item.image;
		}
		if (hasText()) {
			text = item.getText();
		}

		// 2. Collect sizes
		Point imageSize = null;
		Point textSize = null;
		if (image != null) {
			imageSize = new Point(image.getBounds().width, image.getBounds().height);
		}
		if (text != null) {
			textSize = getTextSize();
		}

		// 3. Position elements
		Rectangle imageBounds = null;
		Rectangle textBounds = null;

		int xOffset = PADDING_HORIZONTAL;
		int yOffset = PADDING_TOP;
		int height = DEFAULT_HEIGHT;
		if (image != null) {
			imageBounds = new Rectangle(xOffset, yOffset, imageSize.x, imageSize.y);
			xOffset += imageSize.x;
		}

		if (text != null) {
			textBounds = new Rectangle(xOffset, PADDING_TOP, textSize.x, textSize.y);
			xOffset += textBounds.width;
		}

		Point size = new Point(xOffset, height);

		ChildIndicator state;
		if (item.getItemCount() <= 0) {
			state = ChildIndicator.NONE;
		} else if (item.getExpanded()) {
			state = ChildIndicator.OPEN;
		} else {
			state = ChildIndicator.CLOSED;
		}

		return new TreeItemLayout(size, state, image, imageBounds, text, textBounds);
	}

	private boolean hasImage() {
		return item.getImage() != null;
	}

	private boolean hasText() {
		return item.getText() != null && !item.getText().isBlank();
	}

	private Color getTextColor() {
		if (tree.isEnabled()) {
			return tree.getForeground();
		} else {
			return tree.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
		}
	}

	@Override
	public Point getSize() {
		TreeItemLayout layout = computeLayout(new Point(0, 0));
		return layout.size();
	}

	private Point getTextSize() {
		return Drawing.executeOnGC(tree, this::doMesureText);
	}

	private Point doMesureText(GC gc) {
		gc.setFont(item.getFont());
		return gc.textExtent(item.getText(), DRAW_FLAGS);
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}

}
