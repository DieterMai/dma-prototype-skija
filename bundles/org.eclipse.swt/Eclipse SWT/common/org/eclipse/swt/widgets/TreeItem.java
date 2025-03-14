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

import java.util.*;
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.tree.*;

/**
 * Instances of this class represent a selectable user interface object that
 * represents a hierarchy of tree items in a tree widget.
 *
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>(none)</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#tree">Tree, TreeItem,
 *      TreeColumn snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class TreeItem extends Item implements ITreeItem {
	public interface ITreeItemRenderer {
		/**
		 * Renders the {@link TreeItem}.
		 *
		 * @param gc     GC to render with.
		 * @param bounds Bounds of the rendering. x and y are always 0.
		 */
		void render(GC gc, Rectangle bounds, int depth, java.util.List<TreeCell> cells);

		/**
		 * Returns the size of the rendered {@link ToolItem}.
		 *
		 * @return The size as a {@link Point}.
		 */
		Point getSize(java.util.List<TreeCell> cells, int depth);

		boolean isOnChildIndicator(Point location);
	}

	private Tree tree;

	private TreeItem parentItem;

	private Color backgroundColor;
	private Color foregroundColor;
	private Font font;

	private boolean expanded;

	private ITreeItemRenderer renderer;

	private Rectangle bounds = new Rectangle(0, 0, 0, 0);

	/**
	 * The mouse state of the {@link ToolItem}
	 */
	public static enum MouseState {
		IDLE, HOVER
	}

	private List<TreeCell> cells = new ArrayList<>();

	private boolean selected;
	private boolean mouseHover;

	/**
	 * Constructs <code>TreeItem</code> and <em>inserts</em> it into
	 * <code>Tree</code>. Item is inserted as last direct child of the tree.
	 * <p>
	 * The fastest way to insert many items is documented in
	 * {@link TreeItem#TreeItem(Tree,int,int)} and {@link TreeItem#setItemCount}
	 *
	 * @param parent a tree control which will be the parent of the new instance
	 *               (cannot be null)
	 * @param style  no styles are currently supported, pass SWT.NONE
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public TreeItem(Tree parent, int style) {
		this(parent, null, style, 0);
	}

	/**
	 * Constructs <code>TreeItem</code> and <em>inserts</em> it into
	 * <code>Tree</code>. Item is inserted as <code>index</code> direct child of the
	 * tree.
	 * <p>
	 * The fastest way to insert many items is:
	 * <ol>
	 * <li>Use {@link Tree#setRedraw} to disable drawing during bulk insert</li>
	 * <li>Insert every item at index 0 (insert them in reverse to get the same
	 * result)</li>
	 * <li>Collapse the parent item before inserting (gives massive improvement on
	 * Windows)</li>
	 * </ol>
	 *
	 * @param tree  a tree control which will be the parent of the new instance
	 *              (cannot be null)
	 * @param style no styles are currently supported, pass SWT.NONE
	 * @param index the zero-relative index to store the receiver in its parent
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     <li>ERROR_INVALID_RANGE - if the index is
	 *                                     not between 0 and the number of elements
	 *                                     in the parent (inclusive)</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 * @see Tree#setRedraw
	 */
	public TreeItem(Tree tree, int style, int index) {
		this(tree, null, style, 0);
	}

	/**
	 * Constructs <code>TreeItem</code> and <em>inserts</em> it into
	 * <code>Tree</code>. Item is inserted as last direct child of the specified
	 * <code>TreeItem</code>.
	 * <p>
	 * The fastest way to insert many items is documented in
	 * {@link TreeItem#TreeItem(Tree,int,int)} and {@link TreeItem#setItemCount}
	 *
	 * @param parentItem a tree control which will be the parent of the new instance
	 *                   (cannot be null)
	 * @param style      no styles are currently supported, pass SWT.NONE
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public TreeItem(TreeItem parentItem, int style) {
		this(null, parentItem, style, 0);
	}

	/**
	 * Constructs <code>TreeItem</code> and <em>inserts</em> it into
	 * <code>Tree</code>. Item is inserted as <code>index</code> direct child of the
	 * specified <code>TreeItem</code>.
	 * <p>
	 * The fastest way to insert many items is documented in
	 * {@link TreeItem#TreeItem(Tree,int,int)} and {@link TreeItem#setItemCount}
	 *
	 * @param parentItem a tree control which will be the parent of the new instance
	 *                   (cannot be null)
	 * @param style      no styles are currently supported, pass SWT.NONE
	 * @param index      the zero-relative index to store the receiver in its parent
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     <li>ERROR_INVALID_RANGE - if the index is
	 *                                     not between 0 and the number of elements
	 *                                     in the parent (inclusive)</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 * @see SWT
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 * @see Tree#setRedraw
	 */
	public TreeItem(TreeItem parentItem, int style, int index) {
		this(null, parentItem, style, 0);

	}

	private TreeItem(Tree tree, TreeItem parentItem, int style, int index) {
		super(determinParent(tree, parentItem), style);
		this.tree = determinParent(tree, parentItem); // JEP 492 candidate
		this.parentItem = parentItem;
		cells.add(new TreeCell(this.tree, this));

		// TODO invalid subclass
		// TODO backup style

		this.renderer = new TreeItemRenderer(this.tree, this);

		this.tree.addItem(this, parentItem);
	}

	private static Tree determinParent(Tree tree, TreeItem item) {
		if (tree != null) {
			return tree;
		}
		if (item == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		if (item.tree == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		return item.tree;
	}

	static TreeItem checkNull(TreeItem item) {
		if (item == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		return item;
	}

	public void render(GC gc, Rectangle bounds) {
		renderer.render(gc, bounds, getDept(), cells);
		this.bounds = bounds;
	}

	/**
	 * Returns the rendered size of the {@link ToolItem}.
	 *
	 * @return The size as {@link Point}.
	 */
	public Point getSize() {
		return renderer.getSize(cells, getDept());
	}

	private int getDept() {
		int dept = 0;
		TreeItem p = parentItem;
		while (p != null) {
			dept++;
			p = p.parentItem;
		}
		return dept;
	}


	/**
	 * Clears the item at the given zero-relative index in the receiver. The text,
	 * icon and other attributes of the item are set to the default value. If the
	 * tree was created with the <code>SWT.VIRTUAL</code> style, these attributes
	 * are requested again as needed.
	 *
	 * @param index the index of the item to clear
	 * @param all   <code>true</code> if all child items of the indexed item should
	 *              be cleared recursively, and <code>false</code> otherwise
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_RANGE - if the index is
	 *                                     not between 0 and the number of elements
	 *                                     in the list minus 1 (inclusive)</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 *
	 * @since 3.2
	 */
	public void clear(int index, boolean all) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Clears all the items in the receiver. The text, icon and other attributes of
	 * the items are set to their default values. If the tree was created with the
	 * <code>SWT.VIRTUAL</code> style, these attributes are requested again as
	 * needed.
	 *
	 * @param all <code>true</code> if all child items should be cleared
	 *            recursively, and <code>false</code> otherwise
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 *
	 * @since 3.2
	 */
	public void clearAll(boolean all) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Returns the receiver's background color.
	 *
	 * @return the background color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 2.0
	 */
	@Override
	public Color getBackground() {
		checkWidget();
		if (backgroundColor != null) {
			return backgroundColor;
		} else {
			return tree.getBackground();
		}
	}

	/**
	 * Returns the background color at the given column index in the receiver.
	 *
	 * @param index the column index
	 * @return the background color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public Color getBackground(int index) {
		checkWidget();
		if (index < cellCount() && cell(index).backgroundColor() != null) {
			return cell(index).backgroundColor();
		} else {
			return getBackground();
		}
	}

	/**
	 * Returns a rectangle describing the size and location of the receiver's text
	 * relative to its parent.
	 *
	 * @return the bounding rectangle of the receiver's text
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Rectangle getBounds() {
		checkWidget();
		return bounds;
	}

	/**
	 * Returns a rectangle describing the receiver's size and location relative to
	 * its parent at a column in the tree.
	 *
	 * @param index the index that specifies the column
	 * @return the receiver's bounding column rectangle
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public Rectangle getBounds(int index) {
		checkWidget();
		if (index < cellCount()) {
			return cell(index).getBounds();
		}
		return null;
	}



	/**
	 * Returns <code>true</code> if the receiver is checked, and false otherwise.
	 * When the parent does not have the <code>CHECK</code> style, return false.
	 *
	 * @return the checked state
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public boolean getChecked() {
		checkWidget();
		NOT_IMPLEMENTED();
		return false;
	}

	/**
	 * Returns <code>true</code> if the receiver is expanded, and false otherwise.
	 *
	 * @return the expanded state
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public boolean getExpanded() {
		checkWidget();
		return expanded;
	}

	/**
	 * Returns the font that the receiver will use to paint textual information for
	 * this item.
	 *
	 * @return the receiver's font
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.0
	 */
	@Override
	public Font getFont() {
		checkWidget();
		if (font != null) {
			return font;
		} else {
			return tree.getFont();
		}
	}

	/**
	 * Returns the font that the receiver will use to paint textual information for
	 * the specified cell in this item.
	 *
	 * @param index the column index
	 * @return the receiver's font
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public Font getFont(int index) {
		checkWidget();
		if (index < cellCount() && cell(index).font() != null) {
			return cell(index).font();
		} else {
			return getFont();
		}
	}

	/**
	 * Returns the foreground color that the receiver will use to draw.
	 *
	 * @return the receiver's foreground color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 2.0
	 */
	@Override
	public Color getForeground() {
		checkWidget();
		if (foregroundColor != null) {
			return foregroundColor;
		} else {
			return tree.getForeground();
		}
	}

	/**
	 *
	 * Returns the foreground color at the given column index in the receiver.
	 *
	 * @param index the column index
	 * @return the foreground color
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public Color getForeground(int index) {
		checkWidget();
		if (index < cellCount() && cell(index).foregroundColor() != null) {
			return cell(index).foregroundColor();
		} else {
			return getForeground();
		}
	}

	/**
	 * Returns <code>true</code> if the receiver is grayed, and false otherwise.
	 * When the parent does not have the <code>CHECK</code> style, return false.
	 *
	 * @return the grayed state of the checkbox
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public boolean getGrayed() {
		checkWidget();
		NOT_IMPLEMENTED();
		return false;
	}

	/**
	 * Returns the item at the given, zero-relative index in the receiver. Throws an
	 * exception if the index is out of range.
	 *
	 * @param index the index of the item to return
	 * @return the item at the given index
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_RANGE - if the index is
	 *                                     not between 0 and the number of elements
	 *                                     in the list minus 1 (inclusive)</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	public TreeItem getItem(int index) {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Returns the number of items contained in the receiver that are direct item
	 * children of the receiver.
	 *
	 * @return the number of items
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getItemCount() {
		checkWidget();
		return tree.getItems(this).size();
	}

	/**
	 * Returns a (possibly empty) array of <code>TreeItem</code>s which are the
	 * direct item children of the receiver.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * list of items, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return the receiver's items
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public TreeItem[] getItems() {
		checkWidget();
		return tree.getItems(this).toArray(TreeItem[]::new);
	}

	@Override
	public Image getImage() {
		checkWidget();
		return super.getImage();
	}

	/**
	 * Returns the image stored at the given column index in the receiver, or null
	 * if the image has not been set or if the column does not exist.
	 *
	 * @param index the column index
	 * @return the image stored at the given column index in the receiver
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	public Image getImage(int index) {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Returns a rectangle describing the size and location relative to its parent
	 * of an image at a column in the tree.
	 *
	 * @param index the index that specifies the column
	 * @return the receiver's bounding image rectangle
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public Rectangle getImageBounds(int index) {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Returns the receiver's parent, which must be a <code>Tree</code>.
	 *
	 * @return the receiver's parent
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public Tree getParent() {
		checkWidget();
		return tree;
	}

	/**
	 * Returns the receiver's parent item, which must be a <code>TreeItem</code> or
	 * null when the receiver is a root.
	 *
	 * @return the receiver's parent item
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public TreeItem getParentItem() {
		checkWidget();
		return parentItem;
	}

	@Override
	public String getText() {
		checkWidget();
		return cell(0).text();
	}

	/**
	 * Returns the text stored at the given column index in the receiver, or empty
	 * string if the text has not been set.
	 *
	 * @param index the column index
	 * @return the text stored at the given column index in the receiver
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	public String getText(int index) {
		checkWidget();
		return cell(index).text();
	}

	/**
	 * Returns a rectangle describing the size and location relative to its parent
	 * of the text at a column in the tree.
	 *
	 * @param index the index that specifies the column
	 * @return the receiver's bounding text rectangle
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.3
	 */
	public Rectangle getTextBounds(int index) {
		checkWidget();
		return null;
	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until an
	 * item is found that is equal to the argument, and returns the index of that
	 * item. If no item is found, returns -1.
	 *
	 * @param item the search item
	 * @return the index of the item
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the item is
	 *                                     null</li>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the item
	 *                                     has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	public int indexOf(TreeItem item) {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
	}



	/**
	 * Removes all of the items from the receiver.
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.1
	 */
	public void removeAll() {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the receiver's background color to the color specified by the argument,
	 * or to the default system color for the item if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 2.0
	 */
	@Override
	public void setBackground(Color color) {
		checkWidget();
		this.backgroundColor = color;
	}

	/**
	 * Sets the background color at the given column index in the receiver to the
	 * color specified by the argument, or to the default system color for the item
	 * if the argument is null.
	 *
	 * @param index the column index
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public void setBackground(int index, Color color) {
		checkWidget();
		growCells(index);
		cell(index).backgroundColor(color);
	}

	/**
	 * Sets the checked state of the receiver.
	 *
	 * @param checked the new checked state
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setChecked(boolean checked) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the expanded state of the receiver.
	 *
	 * @param expanded the new expanded state
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setExpanded(boolean expanded) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the font that the receiver will use to paint textual information for
	 * this item to the font specified by the argument, or to the default font for
	 * that kind of control if the argument is null.
	 *
	 * @param font the new font (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.0
	 */
	@Override
	public void setFont(Font font) {
		checkWidget();
		this.font = font;
	}

	/**
	 * Sets the font that the receiver will use to paint textual information for the
	 * specified cell in this item to the font specified by the argument, or to the
	 * default font for that kind of control if the argument is null.
	 *
	 * @param index the column index
	 * @param font  the new font (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public void setFont(int index, Font font) {
		checkWidget();
		growCells(index);
		cell(index).font(font);
	}

	/**
	 * Sets the receiver's foreground color to the color specified by the argument,
	 * or to the default system color for the item if the argument is null.
	 *
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 2.0
	 */
	@Override
	public void setForeground(Color color) {
		checkWidget();
		this.foregroundColor = color;
	}

	/**
	 * Sets the foreground color at the given column index in the receiver to the
	 * color specified by the argument, or to the default system color for the item
	 * if the argument is null.
	 *
	 * @param index the column index
	 * @param color the new color (or null)
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     argument has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public void setForeground(int index, Color color) {
		checkWidget();
		growCells(index);
		cell(index).foregroundColor(color);
	}

	/**
	 * Sets the grayed state of the checkbox for this item. This state change only
	 * applies if the Tree was created with the SWT.CHECK style.
	 *
	 * @param grayed the new grayed state of the checkbox
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setGrayed(boolean grayed) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the image for multiple columns in the tree.
	 *
	 * @param images the array of new images
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the array of
	 *                                     images is null</li>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if one of
	 *                                     the images has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	public void setImage(Image[] images) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the receiver's image at a column.
	 *
	 * @param index the column index
	 * @param image the new image
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the image
	 *                                     has been disposed</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public void setImage(int index, Image image) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	@Override
	public void setImage(Image image) {
		checkWidget();
		super.setImage(image);
	}

	/**
	 * Sets the number of child items contained in the receiver.
	 * <p>
	 * The fastest way to insert many items is:
	 * <ol>
	 * <li>Use {@link Tree#setRedraw} to disable drawing during bulk insert</li>
	 * <li>Collapse the parent item before inserting (gives massive improvement on
	 * Windows)</li>
	 * </ol>
	 *
	 * @param count the number of items
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 3.2
	 */
	public void setItemCount(int count) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the text for multiple columns in the tree.
	 * <p>
	 * Note: If control characters like '\n', '\t' etc. are used in the string, then
	 * the behavior is platform dependent.
	 * </p>
	 *
	 * @param texts the array of new strings
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the text is
	 *                                     null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	@Override
	public void setText(String[] texts) {
		checkWidget();
		for (int i = 0; i < texts.length; i++) {
			setText(i, texts[i]);
		}
	}

	/**
	 * Sets the receiver's text at a column
	 * <p>
	 * Note: If control characters like '\n', '\t' etc. are used in the string, then
	 * the behavior is platform dependent.
	 * </p>
	 *
	 * @param index the column index
	 * @param text  the new text
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the text is
	 *                                     null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 *
	 * @since 3.1
	 */
	public void setText(int index, String text) {
		if (index == 0) {
			super.setText(text);
		}
		cell(index).text(text);
	}

	private TreeCell cell(int index) {
		return cells.get(index);
	}

	private void growCells(int size) {
		for (int i = cells.size(); i <= size; i++) {
			cells.add(new TreeCell(tree, this));
		}
	}

	private int cellCount() {
		return cells.size();
	}

	@Override
	public void setText(String text) {
		checkWidget();
		setText(0, text);
	}

	public boolean isHover() {
		return mouseHover;
	}

	void notifyMouseEnter() {
		this.mouseHover = true;
	}

	void notifyMouseExit() {
		this.mouseHover = false;
	}

	void unselect() {
		this.selected = false;
	}

	public boolean isSelected() {
		return selected;
	}

	boolean notifyMouseClick(Point location) {
		Rectangle bounds = getBounds();
		if (!bounds.contains(location)) {
			return false;
		}

		Point relative = new Point(location.x - bounds.x, location.y - bounds.y);
		if (getItemCount() > 0 && renderer.isOnChildIndicator(relative)) {
			expanded = !expanded;
			return true;
		}

		if (!selected && getBounds(0).contains(location)) {
			selected = true;
			return true;
		}

		return false;
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
