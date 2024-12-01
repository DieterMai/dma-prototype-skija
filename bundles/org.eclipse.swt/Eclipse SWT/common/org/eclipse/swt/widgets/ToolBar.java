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

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;

/**
 * Instances of this class support the layout of selectable tool bar items.
 * <p>
 * The item children that may be added to instances of this class must be of
 * type <code>ToolItem</code>.
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it
 * does not make sense to add <code>Control</code> children to it, or set a
 * layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>FLAT, WRAP, RIGHT, HORIZONTAL, VERTICAL, SHADOW_OUT</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles HORIZONTAL and VERTICAL may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#toolbar">ToolBar, ToolItem
 *      snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      ControlExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ToolBar extends Composite implements ICustomWidget {
	private java.util.List<ToolItem> items = new ArrayList<>();

	private Listener listener;

	private final IToolBarRenderer renderer;
	private final boolean flat;
	private final boolean wrap;
	private final boolean shadowOut;
	private final boolean right;
	private final boolean border;
	private final boolean vertical;
	private final boolean rightToLeft;

	public static interface IToolBarRenderer {

		/**
		 * Renders the handle.
		 *
		 * @param gc
		 * @param bounds
		 */
		void render(GC gc, Rectangle bounds);

		int computeWidth();

		int computeHeight();
	}

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
	 * @see SWT#FLAT
	 * @see SWT#WRAP
	 * @see SWT#RIGHT
	 * @see SWT#HORIZONTAL
	 * @see SWT#SHADOW_OUT
	 * @see SWT#VERTICAL
	 * @see Widget#checkSubclass()
	 * @see Widget#getStyle()
	 */
	public ToolBar(Composite parent, int style) {
		super(parent, checkStyle(style));
		renderer = new ToolBarRenderer(this);

		listener = event -> {
			switch (event.type) {
			case SWT.MouseDown -> onMouseDown(event);
			case SWT.MouseExit -> onMouseExit(event);
			case SWT.MouseMove -> onMouseMove(event);
			case SWT.MouseUp -> onMouseUp(event);
			case SWT.Paint -> onPaint(event);

			}
		};

		addListener(SWT.MouseDown, listener);
		addListener(SWT.MouseExit, listener);
		addListener(SWT.MouseMove, listener);
		addListener(SWT.MouseUp, listener);
		addListener(SWT.Paint, listener);

		// cash flags for later use.
		flat = isFlag(style, SWT.FLAT);
		wrap = isFlag(style, SWT.WRAP);
		shadowOut = isFlag(style, SWT.SHADOW_OUT);
		right = isFlag(style, SWT.RIGHT);
		border = isFlag(style, SWT.BORDER);
		vertical = isFlag(style, SWT.VERTICAL);
		rightToLeft = isFlag(style, SWT.RIGHT_TO_LEFT);

	}

	private boolean isFlag(int style, int flag) {
		return (style & flag) == flag;
	}

	@Override
	protected boolean isScrolled() {
		return false;
	}

	static int checkStyle(int style) {
		// The result of checkStyle() is passed to the super classes of ToolBar.
		// Any style flags that are handled by ToolBar itself are pulled down
		// to prevent interference.
		int styleOut = style;
		styleOut &= ~SWT.FLAT;
		styleOut &= ~SWT.WRAP;
		styleOut &= ~SWT.SHADOW_OUT;
		styleOut &= ~SWT.RIGHT;
		styleOut &= ~SWT.HORIZONTAL;
		styleOut &= ~SWT.VERTICAL;

		return styleOut;
	}

	public boolean isFlat() {
		return flat;
	}

	public boolean isWrap() {
		return wrap;
	}

	public boolean isShadowOut() {
		return shadowOut;
	}

	public boolean isRight() {
		return right;
	}

	public boolean isBorder() {
		return border;
	}

	public boolean isHorizontal() {
		return !isVertical();
	}

	public boolean isVertical() {
		return vertical;
	}

	public boolean isRightToLeft() {
		return rightToLeft;
	}

	public boolean isLeftToRight() {
		return !isRightToLeft();
	}

	private void onMouseExit(Event event) {
		boolean paintRequested = false;
		for (ToolItem item : items) {
			paintRequested |= item.notifyMouseExit();
		}

		if (paintRequested) {
			redraw();
		}
	}

	private void onMouseMove(Event event) {
		boolean paintRequested = false;
		for (ToolItem item : items) {
			paintRequested |= item.notifyMouseMove(toPoint(event));
		}

		if (paintRequested) {
			redraw();
		}
	}

	private void onMouseDown(Event event) {
		boolean paintRequested = false;
		for (ToolItem item : items) {
			paintRequested |= item.notifyMouseDown(toPoint(event));
		}

		if (paintRequested) {
			redraw();
		}
	}

	private void onMouseUp(Event event) {
		boolean paintRequested = false;
		for (ToolItem item : items) {
			paintRequested |= item.notifyMouseUp(toPoint(event));
		}

		if (paintRequested) {
			redraw();
		}
	}

	private Point toPoint(Event e) {
		Point p = e.getLocation();
		if (isLeftToRight()) {
			return p;
		} else {
			return isLeftToRight() ? p : new Point(getBounds().width - p.x, p.y);
		}
	}


	private void onPaint(Event event) {
		if (!isVisible()) {
			return;
		}

		Rectangle bounds = getBounds();
		if (bounds.width == 0 && bounds.height == 0) {
			return;
		}

		Drawing.drawWithGC(this, event.gc, gc -> renderer.render(gc, bounds));
	}

	@Override
	Point computeSizeInPixels(int wHint, int hHint, boolean changed) {
		int computedWidth;
		int computedHeight;

		// TODO optimize
		computedWidth = (wHint == SWT.DEFAULT) ? renderer.computeWidth() : wHint;
		computedHeight = (hHint == SWT.DEFAULT) ? renderer.computeHeight() : hHint;

		if (isBorder()) {
			computedWidth += 4;
			computedHeight += 4;
		}
		return new Point(computedWidth, computedHeight);
	}

	@Override
	Rectangle computeTrimInPixels(int x, int y, int width, int height) {
		NOT_IMPLEMENTED();
		return super.computeTrimInPixels(x, y, width, height);
	}

	@Override
	Widget computeTabGroup() {
		NOT_IMPLEMENTED();
		return super.computeTabGroup();
	}

	@Override
	Widget[] computeTabList() {
		NOT_IMPLEMENTED();
		return super.computeTabList();
	}

	void createItem(ToolItem item, int index) {
		items.add(index, item);
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
	 */
	public ToolItem getItem(int index) {
		return items.get(index);
	}

	public int getItemIndex(ToolItem item) {
		return items.indexOf(item);
	}

	/**
	 * Returns the item at the given point in the receiver or null if no such item
	 * exists. The point is in the coordinate system of the receiver.
	 *
	 * @param point the point used to locate the item
	 * @return the item at the given point
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
	public ToolItem getItem(Point point) {
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Returns the number of items contained in the receiver.
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
		return items.size();
	}

	/**
	 * Returns an array of <code>ToolItem</code>s which are the items in the
	 * receiver.
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
	 */
	public ToolItem[] getItems() {
		checkWidget();
		NOT_IMPLEMENTED();
		return items.toArray(ToolItem[]::new); // TODO optimize
	}

	/**
	 * Returns the number of rows in the receiver. When the receiver has the
	 * <code>WRAP</code> style, the number of rows can be greater than one.
	 * Otherwise, the number of rows is always one.
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
	public int getRowCount() {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
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
	 *                                     <li>ERROR_NULL_ARGUMENT - if the tool
	 *                                     item is null</li>
	 *                                     <li>ERROR_INVALID_ARGUMENT - if the tool
	 *                                     item has been disposed</li>
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
	public int indexOf(ToolItem item) {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
	}

	@Override
	boolean mnemonicHit(char ch) {
		NOT_IMPLEMENTED();
		return false;
	}

	@Override
	boolean mnemonicMatch(char ch) {
		NOT_IMPLEMENTED();
		return false;
	}

	@Override
	void releaseChildren(boolean destroy) {
		NOT_IMPLEMENTED();
		super.releaseChildren(destroy);
	}

	@Override
	void releaseWidget() {
		NOT_IMPLEMENTED();
		super.releaseWidget();
	}

	@Override
	void removeControl(Control control) {
		NOT_IMPLEMENTED();
		super.removeControl(control);
	}

	@Override
	void reskinChildren(int flags) {
		NOT_IMPLEMENTED();
		super.reskinChildren(flags);
	}

	@Override
	void setBackgroundImage(long hBitmap) {
		redraw();
	}

	@Override
	void setBackgroundPixel(int pixel) {
		NOT_IMPLEMENTED();
		super.setBackgroundPixel(pixel);
	}

	@Override
	void setBoundsInPixels(int x, int y, int width, int height, int flags) {
		NOT_IMPLEMENTED();
		super.setBoundsInPixels(x, y, width, height, flags);
	}

	@Override
	public void setFont(Font font) {
		checkWidget();
		NOT_IMPLEMENTED();
		super.setFont(font);
	}

	@Override
	public boolean setParent(Composite parent) {
		checkWidget();
		NOT_IMPLEMENTED();
		return true;
	}

	@Override
	public void setRedraw(boolean redraw) {
		checkWidget();
		NOT_IMPLEMENTED();
		super.setRedraw(redraw);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		redraw();
	}

	private void NOT_IMPLEMENTED() {
		System.err.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}

}
