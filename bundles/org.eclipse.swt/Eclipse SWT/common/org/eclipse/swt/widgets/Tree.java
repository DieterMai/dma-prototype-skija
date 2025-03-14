/*******************************************************************************
 * Copyright (c) 2000, 2021 IBM Corporation and others.
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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.tree.*;

/**
 * Instances of this class provide a selectable user interface object that
 * displays a hierarchy of items and issues notification when an item in the
 * hierarchy is selected.
 * <p>
 * The item children that may be added to instances of this class must be of
 * type <code>TreeItem</code>.
 * </p>
 * <p>
 * Style <code>VIRTUAL</code> is used to create a <code>Tree</code> whose
 * <code>TreeItem</code>s are to be populated by the client on an on-demand
 * basis instead of up-front. This can provide significant performance
 * improvements for trees that are very large or for which <code>TreeItem</code>
 * population is expensive (for example, retrieving values from an external
 * source).
 * </p>
 * <p>
 * Here is an example of using a <code>Tree</code> with style
 * <code>VIRTUAL</code>:
 * </p>
 *
 * <pre>
 * <code>
 *  final Tree tree = new Tree(parent, SWT.VIRTUAL | SWT.BORDER);
 *  tree.setItemCount(20);
 *  tree.addListener(SWT.SetData, new Listener() {
 *      public void handleEvent(Event event) {
 *          TreeItem item = (TreeItem)event.item;
 *          TreeItem parentItem = item.getParentItem();
 *          String text = null;
 *          if (parentItem == null) {
 *              text = "node " + tree.indexOf(item);
 *          } else {
 *              text = parentItem.getText() + " - " + parentItem.indexOf(item);
 *          }
 *          item.setText(text);
 *          System.out.println(text);
 *          item.setItemCount(10);
 *      }
 *  });
 * </code>
 * </pre>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it
 * does not normally make sense to add <code>Control</code> children to it, or
 * set a layout on it, unless implementing something like a cell editor.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SINGLE, MULTI, CHECK, FULL_SELECTION, VIRTUAL, NO_SCROLL</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Selection, DefaultSelection, Collapse, Expand, SetData, MeasureItem,
 * EraseItem, PaintItem, EmptinessChanged</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles SINGLE and MULTI may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#tree">Tree, TreeItem,
 *      TreeColumn snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class Tree extends Composite implements ITree<TreeColumn, TreeItem> {
	/**
	 * Renderer interface for the {@link Tree} widget. All renderers have to
	 * implement this to work with the Tree.
	 */
	public static interface ITreeRenderer {

		/**
		 * Renders the handle.
		 *
		 * @param gc     GC to render with.
		 * @param bounds Bounds of the rendering. x and y are always 0.
		 */
		void render(GC gc, Rectangle bounds, List<TreeItem> items);

		/**
		 * Computes the size of the rendered ToolBar.
		 *
		 * @return The size as {@link Point}
		 */
		Point computeSize(Point size, List<TreeItem> items);

		Rectangle getClientArea();
	}

	private static final Color DEFAULT_HEADER_BACKGROUND_COLOR = new Color(255, 0, 0);
	private static final Color DEFAULT_HEADER_FOREGROUND_COLOR = new Color(0, 255, 0);

	private final List<TreeItem> rootItems = new ArrayList<>();
	private final Map<TreeItem, List<TreeItem>> itemsMap = new HashMap<>();
	private final List<TreeColumn> columns = new ArrayList<>();

	private TreeItem hoverItem;
	private TreeItem selectedItem;

	private Color headerBackgroundColor = DEFAULT_HEADER_BACKGROUND_COLOR;
	private Color headerForegroundColor = DEFAULT_HEADER_FOREGROUND_COLOR;

	private boolean headerVisible;
	private boolean linesVisible;
	private int sortDirection;
	private Font font;

	/** The renderer used to render to {@link ToolBar}. */
	private final ITreeRenderer renderer;

	private Listener listener;


	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *               instance (cannot be null)
	 * @param style  the style of control to construct
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
	 * @see SWT#SINGLE
	 * @see SWT#MULTI
	 * @see SWT#CHECK
	 * @see SWT#FULL_SELECTION
	 * @see SWT#VIRTUAL
	 * @see SWT#NO_SCROLL
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public Tree(Composite parent, int style) {
		super(parent, checkStyle(style));

		renderer = new TreeRenderer(this);

		listener = event -> {
			if (isDisposed()) {
				return;
			}
			switch (event.type) {
			case SWT.MouseDown -> onMouseDown(event);
//			case SWT.MouseExit -> onMouseExit(event);
			case SWT.MouseMove -> onMouseMove(event);
//			case SWT.MouseUp -> onMouseUp(event);
			case SWT.Paint -> onPaint(event);
			case SWT.Resize -> redraw();
			}
		};

		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseExit, listener);
		addListener(SWT.MouseMove, listener);
		addListener(SWT.MouseUp, listener);
		addListener(SWT.Paint, listener);
		addListener(SWT.Resize, listener);
	}

	static int checkStyle(int style) {
		int checkedStyle = checkBits(style, SWT.SINGLE, SWT.MULTI, 0, 0, 0, 0);
		checkedStyle |= SWT.DOUBLE_BUFFERED;
		return checkedStyle;
	}

	private void onPaint(Event event) {
		if (!isVisible()) {
			return;
		}

		Rectangle bounds = getBounds();
		if (bounds.width == 0 && bounds.height == 0) {
			return;
		}

		Rectangle rendererBounds = new Rectangle(0, 0, bounds.width, bounds.height);
		Drawing.drawWithGC(this, event.gc, gc -> renderer.render(gc, rendererBounds, getFlatItems()));
	}

	private void onMouseMove(Event e) {
		if (!isVisible()) {
			return;
		}

		boolean redrawRequired = false;

		Point location = e.getLocation();
		if (hoverItem != null) {
			if (hoverItem.getBounds().contains(location)) {
				return;
			} else {
				hoverItem.notifyMouseExit();
				hoverItem = null;
				redrawRequired = true;
			}
		}
		TreeItem item = getItem(location);
		if (item != null) {
			hoverItem = item;
			hoverItem.notifyMouseEnter();
			redrawRequired = true;
		}

		if (redrawRequired) {
			redraw();
		}
	}

	private void onMouseDown(Event e) {
		if (!isVisible()) {
			return;
		}

		boolean redrawRequired = false;

		Point location = e.getLocation();

		TreeItem item = getItem(location);

		if (item != selectedItem) {
			redrawRequired |= item.notifyMouseClick(location);
			if(item.isSelected()) {
				swapSelection(item);
			}
		}

		if (redrawRequired) {
			redraw();
		}
	}

	/**
	 * Returns true if anything has changed.
	 */
	private void swapSelection(TreeItem newSelection) {
		if (selectedItem == newSelection) {
			return;
		}
		if (selectedItem != null) {
			selectedItem.unselect();
		}
		selectedItem = newSelection;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the user changes the receiver's selection, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * When <code>widgetSelected</code> is called, the item field of the event
	 * object is valid. If the receiver has the <code>SWT.CHECK</code> style and the
	 * check selection changes, the event object detail field contains the value
	 * <code>SWT.CHECK</code>. <code>widgetDefaultSelected</code> is typically
	 * called when an item is double-clicked. The item field of the event object is
	 * valid for default selection, but the detail field is not used.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the user changes
	 *                 the receiver's selection
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
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
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		addTypedListener(listener, SWT.Selection, SWT.DefaultSelection);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when an
	 * item in the receiver is expanded or collapsed by sending it one of the
	 * messages defined in the <code>TreeListener</code> interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
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
	 * @see TreeListener
	 * @see #removeTreeListener
	 */
	@Override
	public void addTreeListener(TreeListener listener) {
		addTypedListener(listener, SWT.Expand, SWT.Collapse);
	}



	@Override
	protected void checkSubclass() {
		if (!isValidSubclass()) {
			error(SWT.ERROR_INVALID_SUBCLASS);
		}
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
		NOT_IMPLEMENTED();
	}


	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		return renderer.computeSize(new Point(wHint, hHint), getFlatItems());
	}


	/**
	 * Deselects an item in the receiver. If the item was already deselected, it
	 * remains deselected.
	 *
	 * @param item the item to be deselected
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
	 * @since 3.4
	 */
	public void deselect(TreeItem item) {
		NOT_IMPLEMENTED();
	}

	/**
	 * Deselects all selected items in the receiver.
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void deselectAll() {
		NOT_IMPLEMENTED();
	}



	/**
	 * Returns the width in points of a grid line.
	 *
	 * @return the width of a grid line in points
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
	public int getGridLineWidth() {
		checkWidget();
		NOT_IMPLEMENTED();
		return 1;
	}

	/**
	 * Returns the header background color.
	 *
	 * @return the receiver's header background color.
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 * @since 3.106
	 */
	@Override
	public Color getHeaderBackground() {
		checkWidget();
		return headerBackgroundColor;
	}


	/**
	 * Returns the header foreground color.
	 *
	 * @return the receiver's header foreground color.
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 * @since 3.106
	 */
	@Override
	public Color getHeaderForeground() {
		checkWidget();
		return headerForegroundColor;
	}


	/**
	 * Returns the height of the receiver's header
	 *
	 * @return the height of the header or zero if the header is not visible
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
	public int getHeaderHeight() {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
	}

	/**
	 * Returns <code>true</code> if the receiver's header is visible, and
	 * <code>false</code> otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, this method may still indicate that it is
	 * considered visible even though it may not actually be showing.
	 * </p>
	 *
	 * @return the receiver's header's visibility state
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
	public boolean getHeaderVisible() {
		checkWidget();
		return headerVisible;
	}

	/**
	 * Returns the column at the given, zero-relative index in the receiver. Throws
	 * an exception if the index is out of range. Columns are returned in the order
	 * that they were created. If no <code>TreeColumn</code>s were created by the
	 * programmer, this method will throw <code>ERROR_INVALID_RANGE</code> despite
	 * the fact that a single column of data may be visible in the tree. This occurs
	 * when the programmer uses the tree like a list, adding items but never
	 * creating a column.
	 *
	 * @param index the index of the column to return
	 * @return the column at the given index
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
	 * @see Tree#getColumnOrder()
	 * @see Tree#setColumnOrder(int[])
	 * @see TreeColumn#getMoveable()
	 * @see TreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 *
	 * @since 3.1
	 */
	@Override
	public TreeColumn getColumn(int index) {
		checkWidget();
		if (index < 0 || index >= columns.size()) {
			error (SWT.ERROR_INVALID_RANGE);
		}
		return columns.get(index);
	}

	/**
	 * Returns the number of columns contained in the receiver. If no
	 * <code>TreeColumn</code>s were created by the programmer, this value is zero,
	 * despite the fact that visually, one column of items may be visible. This
	 * occurs when the programmer uses the tree like a list, adding items but never
	 * creating a column.
	 *
	 * @return the number of columns
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
	public int getColumnCount() {
		checkWidget();
		return columns.size();
	}

	/**
	 * Returns an array of zero-relative integers that map the creation order of the
	 * receiver's items to the order in which they are currently being displayed.
	 * <p>
	 * Specifically, the indices of the returned array represent the current visual
	 * order of the items, and the contents of the array represent the creation
	 * order of the items.
	 * </p>
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * list of items, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return the current visual order of the receiver's items
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @see Tree#setColumnOrder(int[])
	 * @see TreeColumn#getMoveable()
	 * @see TreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 *
	 * @since 3.2
	 */
	public int[] getColumnOrder() {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Returns an array of <code>TreeColumn</code>s which are the columns in the
	 * receiver. Columns are returned in the order that they were created. If no
	 * <code>TreeColumn</code>s were created by the programmer, the array is empty,
	 * despite the fact that visually, one column of items may be visible. This
	 * occurs when the programmer uses the tree like a list, adding items but never
	 * creating a column.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * list of items, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return the items in the receiver
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @see Tree#getColumnOrder()
	 * @see Tree#setColumnOrder(int[])
	 * @see TreeColumn#getMoveable()
	 * @see TreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 *
	 * @since 3.1
	 */
	@Override
	public TreeColumn[] getColumns() {
		checkWidget();
		return columns.toArray(TreeColumn[]::new);
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
	 * Returns the item at the given point in the receiver or null if no such item
	 * exists. The point is in the coordinate system of the receiver.
	 * <p>
	 * The item that is returned represents an item that could be selected by the
	 * user. For example, if selection only occurs in items in the first column,
	 * then null is returned if the point is outside of the item. Note that the
	 * SWT.FULL_SELECTION style hint, which specifies the selection policy,
	 * determines the extent of the selection.
	 * </p>
	 *
	 * @param point the point used to locate the item
	 * @return the item at the given point, or null if the point is not in a
	 *         selectable item
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the point is
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
	 */
	@Override
	public TreeItem getItem(Point point) {
		checkWidget();
		return getItem(getFlatItems(), point);
	}

	private List<TreeItem> getFlatItems() { // TODO might be a good idea to cash this
		List<TreeItem> expandedItems = new ArrayList<>();
		collectExpandedItems(expandedItems, rootItems.toArray(TreeItem[]::new));
		return expandedItems;
	}

	private void collectExpandedItems(List<TreeItem> list, TreeItem[] items) {
		for (TreeItem item : items) {
			list.add(item);
			if (item.getExpanded()) {
				collectExpandedItems(list, item.getItems());
			}
		}
	}

	private TreeItem getItem(List<TreeItem> items, Point location) {
		if (items.isEmpty()) {
			return null;
		}

		Rectangle clientArea = renderer.getClientArea();
		if (!clientArea.contains(location)) {
			return null;
		}


		TreeItem prevItem = items.get(0);
		for (TreeItem item : items) {
			Rectangle bounds = item.getBounds();
			if (bounds.contains(location)) {
				return item;
			} else if (location.y < item.getBounds().y) {
				if (prevItem.getExpanded() && itemsMap.containsKey(prevItem)) {
					getItem(itemsMap.get(prevItem), location);
				} else {
					return null;
				}
			} else {
				prevItem = item;
			}
		}
		return null;
	}

	/**
	 * Returns the number of items contained in the receiver that are direct item
	 * children of the receiver. The number that is returned is the number of roots
	 * in the tree.
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
		return rootItems.size();
	}

	/**
	 * Returns the height of the area which would be used to display <em>one</em> of
	 * the items in the tree.
	 *
	 * @return the height of one item
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getItemHeight() {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
	}

	int getItemHeightInPixels() {
		NOT_IMPLEMENTED();
		return 0;
	}

	/**
	 * Returns a (possibly empty) array of items contained in the receiver that are
	 * direct item children of the receiver. These are the roots of the tree.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * list of items, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return the items
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	@Override
	public TreeItem[] getItems() {
		checkWidget();
		return rootItems.toArray(TreeItem[]::new);
	}


	/**
	 * Returns <code>true</code> if the receiver's lines are visible, and
	 * <code>false</code> otherwise. Note that some platforms draw grid lines while
	 * others may draw alternating row colors.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, this method may still indicate that it is
	 * considered visible even though it may not actually be showing.
	 * </p>
	 *
	 * @return the visibility state of the lines
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
	public boolean getLinesVisible() {
		checkWidget();
		return linesVisible;
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
		NOT_IMPLEMENTED();
		return null;
	}


	/**
	 * Returns an array of <code>TreeItem</code>s that are currently selected in the
	 * receiver. The order of the items is unspecified. An empty array indicates
	 * that no items are selected.
	 * <p>
	 * Note: This is not the actual structure used by the receiver to maintain its
	 * selection, so modifying the array will not affect the receiver.
	 * </p>
	 *
	 * @return an array representing the selection
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public TreeItem[] getSelection() {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Returns the number of selected items contained in the receiver.
	 *
	 * @return the number of selected items
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getSelectionCount() {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
	}

	/**
	 * Returns the column which shows the sort indicator for the receiver. The value
	 * may be null if no column shows the sort indicator.
	 *
	 * @return the sort indicator
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @see #setSortColumn(TreeColumn)
	 *
	 * @since 3.2
	 */
	@Override
	public TreeColumn getSortColumn() {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Returns the direction of the sort indicator for the receiver. The value will
	 * be one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
	 *
	 * @return the sort direction
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @see #setSortDirection(int)
	 *
	 * @since 3.2
	 */
	@Override
	public int getSortDirection() {
		checkWidget();
		return sortDirection;
	}

	/**
	 * Returns the item which is currently at the top of the receiver. This item can
	 * change when items are expanded, collapsed, scrolled or new items are added or
	 * removed.
	 *
	 * @return the item at the top of the receiver
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @since 2.1
	 */
	public TreeItem getTopItem() {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}




	/**
	 * Searches the receiver's list starting at the first column (index 0) until a
	 * column is found that is equal to the argument, and returns the index of that
	 * column. If no column is found, returns -1.
	 *
	 * @param column the search column
	 * @return the index of the column
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the column
	 *                                     is null</li>
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
	public int indexOf(TreeColumn column) {
		checkWidget();
		NOT_IMPLEMENTED();
		return -1;
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
	 */
	public void removeAll() {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the user changes the receiver's selection.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
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
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when items in the receiver are expanded or collapsed.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the listener
	 *                                     is null</li>
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
	 * @see TreeListener
	 * @see #addTreeListener
	 */
	@Override
	public void removeTreeListener(TreeListener listener) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Display a mark indicating the point at which an item will be inserted. The
	 * drop insert item has a visual hint to show where a dragged item will be
	 * inserted when dropped on the tree.
	 *
	 * @param item   the insert item. Null will clear the insertion mark.
	 * @param before true places the insert mark above 'item'. false places the
	 *               insert mark below 'item'.
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
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
	 */
	public void setInsertMark(TreeItem item, boolean before) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the number of root-level items contained in the receiver.
	 * <p>
	 * The fastest way to insert many items is documented in
	 * {@link TreeItem#TreeItem(Tree,int,int)} and {@link TreeItem#setItemCount}
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
	 * Marks the receiver's lines as visible if the argument is <code>true</code>,
	 * and marks it invisible otherwise. Note that some platforms draw grid lines
	 * while others may draw alternating row colors.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, marking it visible may not actually cause it
	 * to be displayed.
	 * </p>
	 *
	 * @param show the new visibility state
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
	public void setLinesVisible(boolean show) {
		checkWidget();
		this.linesVisible = show;
	}


	/**
	 * Selects an item in the receiver. If the item was already selected, it remains
	 * selected.
	 *
	 * @param item the item to be selected
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
	 * @since 3.4
	 */
	public void select(TreeItem item) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Selects all of the items in the receiver.
	 * <p>
	 * If the receiver is single-select, do nothing.
	 * </p>
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void selectAll() {
		checkWidget();
		NOT_IMPLEMENTED();
	}



	/**
	 * Sets the order that the items in the receiver should be displayed in to the
	 * given argument which is described in terms of the zero-relative ordering of
	 * when the items were added.
	 *
	 * @param order the new order to display the items
	 *
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_WIDGET_DISPOSED - if the
	 *                                     receiver has been disposed</li>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     receiver</li>
	 *                                     </ul>
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the item
	 *                                     order is null</li>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the item
	 *                                     order is not the same length as the
	 *                                     number of items</li>
	 *                                     </ul>
	 *
	 * @see Tree#getColumnOrder()
	 * @see TreeColumn#getMoveable()
	 * @see TreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 *
	 * @since 3.2
	 */
	public void setColumnOrder(int[] order) {
		checkWidget();
		NOT_IMPLEMENTED();
	}


	@Override
	public void setFont(Font font) {
		checkWidget();
		this.font = font;
	}

	/**
	 * Sets the header background color to the color specified by the argument, or
	 * to the default system color if the argument is null.
	 * <p>
	 * Note: This operation is a <em>HINT</em> and is not supported on all
	 * platforms. If the native header has a 3D look and feel (e.g. Windows 7), this
	 * method will cause the header to look FLAT irrespective of the state of the
	 * tree style.
	 * </p>
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
	 * @since 3.106
	 */
	@Override
	public void setHeaderBackground(Color color) {
		checkWidget();
		this.headerBackgroundColor = color != null ? color : DEFAULT_HEADER_BACKGROUND_COLOR;
	}

	/**
	 * Sets the header foreground color to the color specified by the argument, or
	 * to the default system color if the argument is null.
	 * <p>
	 * Note: This operation is a <em>HINT</em> and is not supported on all
	 * platforms. If the native header has a 3D look and feel (e.g. Windows 7), this
	 * method will cause the header to look FLAT irrespective of the state of the
	 * tree style.
	 * </p>
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
	 * @since 3.106
	 */
	@Override
	public void setHeaderForeground(Color color) {
		checkWidget();
		this.headerForegroundColor = color != null ? color : DEFAULT_HEADER_FOREGROUND_COLOR;
	}

	/**
	 * Marks the receiver's header as visible if the argument is <code>true</code>,
	 * and marks it invisible otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some other condition
	 * makes the receiver not visible, marking it visible may not actually cause it
	 * to be displayed.
	 * </p>
	 *
	 * @param show the new visibility state
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
	public void setHeaderVisible(boolean show) {
		checkWidget();
		this.headerVisible = show;
	}

	@Override
	public void setRedraw(boolean redraw) {
		checkWidget();
		NOT_IMPLEMENTED();
	}



	/**
	 * Sets the receiver's selection to the given item. The current selection is
	 * cleared before the new item is selected, and if necessary the receiver is
	 * scrolled to make the new selection visible.
	 * <p>
	 * If the item is not in the receiver, then it is ignored.
	 * </p>
	 *
	 * @param item the item to select
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
	 * @since 3.2
	 */
	public void setSelection(TreeItem item) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the receiver's selection to be the given array of items. The current
	 * selection is cleared before the new items are selected, and if necessary the
	 * receiver is scrolled to make the new selection visible.
	 * <p>
	 * Items that are not in the receiver are ignored. If the receiver is
	 * single-select and multiple items are specified, then all items are ignored.
	 * </p>
	 *
	 * @param items the array of items
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the array of
	 *                                     items is null</li>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if one of
	 *                                     the items has been disposed</li>
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
	 * @see Tree#deselectAll()
	 */
	public void setSelection(TreeItem[] items) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the column used by the sort indicator for the receiver. A null value
	 * will clear the sort indicator. The current sort column is cleared before the
	 * new column is set.
	 *
	 * @param column the column used by the sort indicator or <code>null</code>
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the
	 *                                     column is disposed</li>
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
	 * @since 3.2
	 */
	@Override
	public void setSortColumn(TreeColumn column) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the direction of the sort indicator for the receiver. The value can be
	 * one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
	 *
	 * @param direction the direction of the sort indicator
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
	@Override
	public void setSortDirection(int direction) {
		checkWidget();
		this.sortDirection = direction;
	}

	/**
	 * Sets the item which is currently at the top of the receiver. This item can
	 * change when items are expanded, collapsed, scrolled or new items are added or
	 * removed.
	 *
	 * @param item the item to be shown
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
	 * @see Tree#getTopItem()
	 *
	 * @since 2.1
	 */
	public void setTopItem(TreeItem item) {
		checkWidget();
		NOT_IMPLEMENTED();
	}




	/**
	 * Shows the column. If the column is already showing in the receiver, this
	 * method simply returns. Otherwise, the columns are scrolled until the column
	 * is visible.
	 *
	 * @param column the column to be shown
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
	public void showColumn(TreeColumn column) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Shows the item. If the item is already showing in the receiver, this method
	 * simply returns. Otherwise, the items are scrolled and expanded until the item
	 * is visible.
	 *
	 * @param item the item to be shown
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
	 * @see Tree#showSelection()
	 */
	public void showItem(TreeItem item) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Shows the selection. If the selection is already showing in the receiver,
	 * this method simply returns. Otherwise, the items are scrolled until the
	 * selection is visible.
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 *
	 * @see Tree#showItem(TreeItem)
	 */
	public void showSelection() {
		checkWidget();
		NOT_IMPLEMENTED();
	}



	@Override
	public Composite _composite() {
		NOT_IMPLEMENTED();
		return this;
	}



	protected void addItem(TreeItem item, TreeItem parentItem) {
		if (parentItem == null) {
			rootItems.add(item);
		} else {
			itemsMap.computeIfAbsent(parentItem, i -> new ArrayList<TreeItem>()).add(item);
		}
	}

	public List<TreeItem> getItems(TreeItem parentItem) {
		return itemsMap.getOrDefault(parentItem, List.of());
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}
}
