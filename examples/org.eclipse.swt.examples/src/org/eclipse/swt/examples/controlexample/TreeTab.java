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
package org.eclipse.swt.examples.controlexample;


import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ITree;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

class TreeTab extends ScrollableTab {
	WrapperTee exampleWrapper_old;

	/* Example widgets and groups that contain them */
	Group treeGroup, imageTreeGroup;

	/* Size widgets added to the "Size" group */
	Button packColumnsButton;

	/* Style widgets added to the "Style" group */
	Button noScrollButton, checkButton, fullSelectionButton;

	/* Other widgets added to the "Other" group */
	Button multipleColumns, moveableColumns, resizableColumns, headerVisibleButton, sortIndicatorButton, headerImagesButton, subImagesButton, linesVisibleButton, editableButton;



	Point menuMouseCoords;

	/**
	 * Creates the Tab within a given instance of ControlExample.
	 */
	TreeTab(ControlExample instance) {
		super(instance);
	}

	/**
	 * Creates the "Colors and Fonts" group.
	 */
	@Override
	void createColorAndFontGroup () {
		super.createColorAndFontGroup();

		TableItem item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Foreground_Color"));
		item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Background_Color"));
		item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Item_Font"));
		item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Cell_Foreground_Color"));
		item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Cell_Background_Color"));
		item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Cell_Font"));
		item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Header_Foreground_Color"));
		item = new TableItem(colorAndFontTable, SWT.None);
		item.setText(ControlExample.getResourceString ("Header_Background_Color"));

		shell.addDisposeListener(event -> {
			exampleWrapper_old.addDisposeListener();
		});
	}

	@Override
	void changeFontOrColor(int index) {
		exampleWrapper_old.changeFontOrColor(index);

	}

	/**
	 * Creates the "Other" group.
	 */
	@Override
	void createOtherGroup () {
		super.createOtherGroup ();

		/* Create display controls specific to this example */
		linesVisibleButton = new Button (otherGroup, SWT.CHECK);
		linesVisibleButton.setText (ControlExample.getResourceString("Lines_Visible"));
		multipleColumns = new Button (otherGroup, SWT.CHECK);
		multipleColumns.setText (ControlExample.getResourceString("Multiple_Columns"));
		headerVisibleButton = new Button (otherGroup, SWT.CHECK);
		headerVisibleButton.setText (ControlExample.getResourceString("Header_Visible"));
		sortIndicatorButton = new Button (otherGroup, SWT.CHECK);
		sortIndicatorButton.setText (ControlExample.getResourceString("Sort_Indicator"));
		moveableColumns = new Button (otherGroup, SWT.CHECK);
		moveableColumns.setText (ControlExample.getResourceString("Moveable_Columns"));
		resizableColumns = new Button (otherGroup, SWT.CHECK);
		resizableColumns.setText (ControlExample.getResourceString("Resizable_Columns"));
		headerImagesButton = new Button (otherGroup, SWT.CHECK);
		headerImagesButton.setText (ControlExample.getResourceString("Header_Images"));
		subImagesButton = new Button (otherGroup, SWT.CHECK);
		subImagesButton.setText (ControlExample.getResourceString("Sub_Images"));
		editableButton = new Button(otherGroup, SWT.CHECK);
		editableButton.setText(ControlExample.getResourceString("Editable"));

		/* Add the listeners */
		linesVisibleButton.addSelectionListener (widgetSelectedAdapter(event -> setWidgetLinesVisible ()));
		multipleColumns.addSelectionListener (widgetSelectedAdapter(event -> recreateExampleWidgets ()));
		headerVisibleButton.addSelectionListener (widgetSelectedAdapter(event -> setWidgetHeaderVisible ()));
		sortIndicatorButton.addSelectionListener (widgetSelectedAdapter(event -> setWidgetSortIndicator ()));
		moveableColumns.addSelectionListener (widgetSelectedAdapter(event -> setColumnsMoveable ()));
		resizableColumns.addSelectionListener (widgetSelectedAdapter(event -> setColumnsResizable ()));
		headerImagesButton.addSelectionListener (widgetSelectedAdapter(event -> recreateExampleWidgets ()));
		subImagesButton.addSelectionListener (widgetSelectedAdapter(event -> recreateExampleWidgets ()));
		editableButton.addSelectionListener(widgetSelectedAdapter(event -> makeTreeContentEditable()));
	}

	void makeTreeContentEditable() {
		exampleWrapper_old.makeTreesEditable();
	}

	/**
	 * Creates the "Example" group.
	 */
	@Override
	void createExampleGroup () {
		super.createExampleGroup ();

		/* Create a group for the text tree */
		treeGroup = new Group (exampleGroup, SWT.NONE);
		treeGroup.setLayout (new GridLayout (2, false));
		treeGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		treeGroup.setText ("Tree");

		/* Create a group for the image tree */
		imageTreeGroup = new Group (exampleGroup, SWT.NONE);
		imageTreeGroup.setLayout (new GridLayout (2, false));
		imageTreeGroup.setLayoutData (new GridData (SWT.FILL, SWT.FILL, true, true));
		imageTreeGroup.setText (ControlExample.getResourceString("Tree_With_Images"));
	}

	/**
	 * Creates the "Example" widgets.
	 */
	@Override
	void createExampleWidgets () {
		/* Compute the widget style */
		int style = getDefaultStyle();
		if (singleButton.getSelection()) style |= SWT.SINGLE;
		if (multiButton.getSelection()) style |= SWT.MULTI;
		if (horizontalButton.getSelection ()) style |= SWT.H_SCROLL;
		if (verticalButton.getSelection ()) style |= SWT.V_SCROLL;
		if (noScrollButton.getSelection()) style |= SWT.NO_SCROLL;
		if (checkButton.getSelection()) style |= SWT.CHECK;
		if (fullSelectionButton.getSelection ()) style |= SWT.FULL_SELECTION;
		if (borderButton.getSelection()) style |= SWT.BORDER;

		exampleWrapper_old = new WrapperTee(style, this, treeGroup, imageTreeGroup);
	}


	/**
	 * Creates the "Size" group.  The "Size" group contains
	 * controls that allow the user to change the size of
	 * the example widgets.
	 */
	@Override
	void createSizeGroup () {
		super.createSizeGroup();

		packColumnsButton = new Button (sizeGroup, SWT.PUSH);
		packColumnsButton.setText (ControlExample.getResourceString("Pack_Columns"));
		packColumnsButton.addSelectionListener(widgetSelectedAdapter(event -> {
			exampleWrapper_old.packColumns();
			setExampleWidgetSize ();
		}));
	}

	/**
	 * Creates the "Style" group.
	 */
	@Override
	void createStyleGroup() {
		super.createStyleGroup();

		/* Create the extra widgets */
		noScrollButton = new Button (styleGroup, SWT.CHECK);
		noScrollButton.setText ("SWT.NO_SCROLL");
		noScrollButton.moveAbove(borderButton);
		checkButton = new Button (styleGroup, SWT.CHECK);
		checkButton.setText ("SWT.CHECK");
		fullSelectionButton = new Button (styleGroup, SWT.CHECK);
		fullSelectionButton.setText ("SWT.FULL_SELECTION");
	}

	/**
	 * Gets the "Example" widget children's items, if any.
	 *
	 * @return an array containing the example widget children's items
	 */
	@Override
	Item [] getExampleWidgetItems () {
		/* Note: We do not bother collecting the tree items
		 * because tree items don't have any events. If events
		 * are ever added to TreeItem, then this needs to change.
		 */
		List<Item> allItems = new ArrayList<>(exampleWrapper_old.getExampleWidgetItems());
		return allItems.toArray(Item[]::new);
	}

	/**
	 * Gets the "Example" widget children.
	 */
	@Override
	Widget [] getExampleWidgets () {
		List<ITree> allTrees = new ArrayList<>(exampleWrapper_old.getExampleWidgets());
		return allTrees.stream().map(tree -> (Widget)tree).toArray(Widget[]::new);
	}

	/**
	 * Returns a list of set/get API method names (without the set/get prefix)
	 * that can be used to set/get values in the example control(s).
	 */
	@Override
	String[] getMethodNames() {
		return new String[] {"ColumnOrder", "Selection", "ToolTipText", "TopItem"};
	}

	@Override
	Object[] parameterForType(String typeName, String value, Widget widget) {
		if (typeName.equals("org.eclipse.swt.widgets.TreeItem")) {
			TreeItem item = findItem(value, ((Tree) widget).getItems());
			if (item != null) return new Object[] {item};
		}
		if (typeName.equals("[Lorg.eclipse.swt.widgets.TreeItem;")) {
			String[] values = split(value, ',');
			TreeItem[] items = new TreeItem[values.length];
			for (int i = 0; i < values.length; i++) {
				TreeItem item = findItem(values[i], ((Tree) widget).getItems());
				if (item == null) break;
				items[i] = item;
			}
			return new Object[] {items};
		}
		return super.parameterForType(typeName, value, widget);
	}

	TreeItem findItem(String value, TreeItem[] items) {
		for (TreeItem item : items) {
			if (item.getText().equals(value)) return item;
			item = findItem(value, item.getItems());
			if (item != null) return item;
		}
		return null;
	}

	/**
	 * Gets the text for the tab folder item.
	 */
	@Override
	String getTabText () {
		return "Tree";
	}

	void setHeaderBackground () {
		exampleWrapper_old.setHeaderBackground(colorAndFontTable);
	}

	void setHeaderForeground () {
		exampleWrapper_old.setHeaderForeground(colorAndFontTable);
	}

	/**
	 * Sets the moveable columns state of the "Example" widgets.
	 */
	void setColumnsMoveable () {
		exampleWrapper_old.setColumnsMoveable();
	}

	/**
	 * Sets the resizable columns state of the "Example" widgets.
	 */
	void setColumnsResizable () {
		exampleWrapper_old.setColumnsResizable();
	}

	/**
	 * Sets the foreground color, background color, and font
	 * of the "Example" widgets to their default settings.
	 * Also sets foreground and background color of the Node 1
	 * TreeItems to default settings.
	 */
	@Override
	void resetColorsAndFonts () {
		super.resetColorsAndFonts ();
		exampleWrapper_old.resetColorsAndFonts(colorAndFontTable, font);
	}

	/**
	 * Sets the state of the "Example" widgets.
	 */
	@Override
	void setExampleWidgetState () {
		setItemBackground ();
		setItemForeground ();
		setItemFont ();
		setCellBackground ();
		setCellForeground ();
		setCellFont ();
		setHeaderBackground ();
		setHeaderForeground ();
		if (!instance.startup) {
			setColumnsMoveable ();
			setColumnsResizable ();
			setWidgetHeaderVisible ();
			setWidgetSortIndicator ();
			setWidgetLinesVisible ();
		}
		super.setExampleWidgetState ();

		int style = exampleWrapper_old.getStyle();
		noScrollButton.setSelection ((style & SWT.NO_SCROLL) != 0);
		checkButton.setSelection ((style & SWT.CHECK) != 0);
		fullSelectionButton.setSelection ((style & SWT.FULL_SELECTION) != 0);
		try {
			moveableColumns.setSelection (exampleWrapper_old.columnMovable());
			resizableColumns.setSelection (exampleWrapper_old.columnResizable());

		} catch (IllegalArgumentException ex) {}
		headerVisibleButton.setSelection (exampleWrapper_old.headerVisible());
		linesVisibleButton.setSelection (exampleWrapper_old.linesVisible());
	}

	/**
	 * Sets the background color of the Node 1 TreeItems in column 1.
	 */
	void setCellBackground () {
		exampleWrapper_old.setCellBackground(colorAndFontTable);

	}

	/**
	 * Sets the foreground color of the Node 1 TreeItems in column 1.
	 */
	void setCellForeground () {
		exampleWrapper_old.setCellForeground(colorAndFontTable);
	}

	/**
	 * Sets the font of the Node 1 TreeItems in column 1.
	 */
	void setCellFont () {
		exampleWrapper_old.setCellFont(colorAndFontTable);
	}

	/**
	 * Sets the background color of the Node 1 TreeItems.
	 */
	void setItemBackground () {
		exampleWrapper_old.setItemBackground(colorAndFontTable);
	}

	/**
	 * Sets the foreground color of the Node 1 TreeItems.
	 */
	void setItemForeground () {
		exampleWrapper_old.setItemForeground(colorAndFontTable);
	}

	/**
	 * Sets the font of the Node 1 TreeItems.
	 */
	void setItemFont () {
		exampleWrapper_old.setItemFont(colorAndFontTable);
	}

	/**
	 * Sets the header visible state of the "Example" widgets.
	 */
	void setWidgetHeaderVisible () {
		exampleWrapper_old.setWidgetHeaderVisible(headerVisibleButton.getSelection ());

	}

	/**
	 * Sets the sort indicator state of the "Example" widgets.
	 */
	void setWidgetSortIndicator () {
		exampleWrapper_old.setWidgetSortIndicator(sortIndicatorButton.getSelection ());
	}



	/**
	 * Sets the lines visible state of the "Example" widgets.
	 */
	void setWidgetLinesVisible () {
		exampleWrapper_old.setWidgetLinesVisible(linesVisibleButton.getSelection ());
	}

	@Override
	protected void specialPopupMenuItems(Menu menu, Event event) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("getItem(Point) on mouse coordinates");
		final Tree t = (Tree) event.widget;
		menuMouseCoords = t.toControl(new Point(event.x, event.y));
		item.addSelectionListener(widgetSelectedAdapter(e -> {
			eventConsole.append ("getItem(Point(" + menuMouseCoords + ")) returned: " + t.getItem(menuMouseCoords));
			eventConsole.append ("\n");
		}));
	}
}
