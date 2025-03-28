/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
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
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

import java.util.List;

/**
 * Instances of this class represent a column in a tree widget.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT, RIGHT, CENTER</dd>
 * <dt><b>Events:</b></dt>
 * <dd>Move, Resize, Selection</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles LEFT, RIGHT and CENTER may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 *
 * @see <a href="http://www.eclipse.org/swt/snippets/#tree">Tree, TreeItem,
 *      TreeColumn snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 *
 * @since 3.1
 * @noextend This class is not intended to be subclassed by clients.
 */
public class TreeColumn extends Item implements ITreeColumn {
	private String toolTipText;
	private boolean resizable;
	private Tree tree;

	private int sortDirection;
	private int width;

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>Tree</code>) and a style value describing its behavior and appearance.
	 * The item is added to the end of the items maintained by its parent.
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
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#CENTER
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public TreeColumn(Tree parent, int style) {
		super(parent, checkStyle(style));

		resizable = true;
		this.tree = parent;
		parent.createItem(this, parent.getColumnCount());
	}

	/**
	 * Constructs a new instance of this class given its parent (which must be a
	 * <code>Tree</code>), a style value describing its behavior and appearance, and
	 * the index at which to place it in the items maintained by its parent.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are also inherited from superclasses.
	 * </p>
	 * <p>
	 * Note that due to a restriction on some platforms, the first column is always
	 * left aligned.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *               instance (cannot be null)
	 * @param style  the style of control to construct
	 * @param index  the zero-relative index to store the receiver in its parent
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
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#CENTER
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public TreeColumn(Tree parent, int style, int index) {
		super(parent, checkStyle(style));
		resizable = true;
		this.tree = parent;
		parent.createItem(this, index);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is moved or resized, by sending it one of the messages defined in
	 * the <code>ControlListener</code> interface.
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
	 * @see ControlListener
	 * @see #removeControlListener
	 */
	@Override
	public void addControlListener(ControlListener listener) {
		addTypedListener(listener, SWT.Resize, SWT.Move);
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified when
	 * the control is selected by the user, by sending it one of the messages
	 * defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the column header is selected.
	 * <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 *
	 * @param listener the listener which should be notified when the control is
	 *                 selected by the user
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
	@Override
	public void addSelectionListener(SelectionListener listener) {
		addTypedListener(listener, SWT.Selection, SWT.DefaultSelection);
	}

	static int checkStyle(int style) {
		return checkBits(style, SWT.LEFT, SWT.CENTER, SWT.RIGHT, 0, 0, 0);
	}

	/**
	 * Returns a value which describes the position of the text or image in the
	 * receiver. The value will be one of <code>LEFT</code>, <code>RIGHT</code> or
	 * <code>CENTER</code>.
	 *
	 * @return the alignment
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getAlignment() {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
	}

	/**
	 * Gets the moveable attribute. A column that is not moveable cannot be
	 * reordered by the user by dragging the header but may be reordered by the
	 * programmer.
	 *
	 * @return the moveable attribute
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
	 * @see TreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 *
	 * @since 3.2
	 */
	@Override
	public boolean getMoveable() {
		checkWidget();
		NOT_IMPLEMENTED();
		return false;
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
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Gets the resizable attribute. A column that is not resizable cannot be
	 * dragged by the user but may be resized by the programmer.
	 *
	 * @return the resizable attribute
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
	public boolean getResizable() {
		checkWidget();
		NOT_IMPLEMENTED();
		return false;
	}

	/**
	 * Returns the receiver's tool tip text, or null if it has not been set.
	 *
	 * @return the receiver's tool tip text
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
	public String getToolTipText() {
		checkWidget();
		NOT_IMPLEMENTED();
		return null;
	}

	/**
	 * Gets the width of the receiver.
	 *
	 * @return the width
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public int getWidth() {
		checkWidget();
		NOT_IMPLEMENTED();
		return 0;
	}


	/**
	 * Causes the receiver to be resized to its preferred size. For a composite,
	 * this involves computing the preferred size from its layout, if there is one.
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
	public void pack() {
		checkWidget();
		List<TreeItem> items = tree.getFlatItems();
		int maxWidth = 0;
		for (TreeItem item : items) {
			maxWidth = Math.max(maxWidth, item.getSize().x);
		}
		setWidth(maxWidth);
		tree.redraw();
	}


	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is moved or resized.
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
	 * @see ControlListener
	 * @see #addControlListener
	 */
	@Override
	public void removeControlListener(ControlListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Move, listener);
		eventTable.unhook(SWT.Resize, listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be notified
	 * when the control is selected by the user.
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
	@Override
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null)
			error(SWT.ERROR_NULL_ARGUMENT);
		if (eventTable == null)
			return;
		eventTable.unhook(SWT.Selection, listener);
		eventTable.unhook(SWT.DefaultSelection, listener);
	}

	/**
	 * Controls how text and images will be displayed in the receiver. The argument
	 * should be one of <code>LEFT</code>, <code>RIGHT</code> or
	 * <code>CENTER</code>.
	 * <p>
	 * Note that due to a restriction on some platforms, the first column is always
	 * left aligned.
	 * </p>
	 *
	 * @param alignment the new alignment
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setAlignment(int alignment) {
		checkWidget();
		NOT_IMPLEMENTED();

	}

	@Override
	public void setImage(Image image) {
		checkWidget();
		NOT_IMPLEMENTED();
	}



	/**
	 * Sets the moveable attribute. A column that is moveable can be reordered by
	 * the user by dragging the header. A column that is not moveable cannot be
	 * dragged by the user but may be reordered by the programmer.
	 *
	 * @param moveable the moveable attribute
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
	 * @see Tree#getColumnOrder()
	 * @see TreeColumn#getMoveable()
	 * @see SWT#Move
	 *
	 * @since 3.2
	 */
	@Override
	public void setMoveable(boolean moveable) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the resizable attribute. A column that is not resizable cannot be
	 * dragged by the user but may be resized by the programmer.
	 *
	 * @param resizable the resize attribute
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
	public void setResizable(boolean resizable) {
		checkWidget();
		NOT_IMPLEMENTED();
	}

	/**
	 * Sets the receiver's tool tip text to the argument, which may be null
	 * indicating that the default tool tip for the control will be shown. For a
	 * control that has a default tool tip, such as the Tree control on Windows,
	 * setting the tool tip text to an empty string replaces the default, causing no
	 * tool tip text to be shown.
	 * <p>
	 * The mnemonic indicator (character '&amp;') is not displayed in a tool tip. To
	 * display a single '&amp;' in the tool tip, the character '&amp;' can be
	 * escaped by doubling it in the string.
	 * </p>
	 * <p>
	 * NOTE: This operation is a hint and behavior is platform specific, on Windows
	 * for CJK-style mnemonics of the form " (&amp;C)" at the end of the tooltip
	 * text are not shown in tooltip.
	 * </p>
	 *
	 * @param string the new tool tip text (or null)
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
	public void setToolTipText(String string) {
		checkWidget();
		this.toolTipText = string;
	}

	/**
	 * Sets the width of the receiver.
	 *
	 * @param width the new width
	 *
	 * @exception SWTException
	 *                         <ul>
	 *                         <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                         disposed</li>
	 *                         <li>ERROR_THREAD_INVALID_ACCESS - if not called from
	 *                         the thread that created the receiver</li>
	 *                         </ul>
	 */
	public void setWidth(int width) {
		checkWidget();
		this.width = width;
	}

	void setSortDirection(int sortDirection) {
		this.sortDirection = sortDirection;
	}

	private void NOT_IMPLEMENTED() {
		System.out.println(Thread.currentThread().getStackTrace()[2] + " not implemented yet!");
	}


}
