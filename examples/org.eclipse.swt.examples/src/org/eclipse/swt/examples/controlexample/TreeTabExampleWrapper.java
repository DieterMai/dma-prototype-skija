package org.eclipse.swt.examples.controlexample;

import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class TreeTabExampleWrapper {
	static final int ITEM_FOREGROUND_COLOR = 3;
	static final int ITEM_BACKGROUND_COLOR = 4;
	static final int ITEM_FONT = 5;
	static final int CELL_FOREGROUND_COLOR = 6;
	static final int CELL_BACKGROUND_COLOR = 7;
	static final int CELL_FONT = 8;
	static final int HEADER_FOREGROUND_COLOR = 9;
	static final int HEADER_BACKGROUND_COLOR = 10;

	Tree tree1, tree2;
	TreeItem textNode1, imageNode1;

	TreeTab host;

	/* Controls and resources added to the "Colors and Fonts" group */
	Color itemForegroundColor, itemBackgroundColor, cellForegroundColor, cellBackgroundColor, headerForegroundColor, headerBackgroundColor;
	Font itemFont, cellFont;

	static String[] columnTitles = { ControlExample.getResourceString("TableTitle_0"),
			ControlExample.getResourceString("TableTitle_1"), ControlExample.getResourceString("TableTitle_2"),
			ControlExample.getResourceString("TableTitle_3") };

	static String[][] tableData = {
			{ ControlExample.getResourceString("TableLine0_0"), ControlExample.getResourceString("TableLine0_1"),
					ControlExample.getResourceString("TableLine0_2"),
					ControlExample.getResourceString("TableLine0_3") },
			{ ControlExample.getResourceString("TableLine1_0"), ControlExample.getResourceString("TableLine1_1"),
					ControlExample.getResourceString("TableLine1_2"),
					ControlExample.getResourceString("TableLine1_3") },
			{ ControlExample.getResourceString("TableLine2_0"), ControlExample.getResourceString("TableLine2_1"),
					ControlExample.getResourceString("TableLine2_2"),
					ControlExample.getResourceString("TableLine2_3") } };

	public TreeTabExampleWrapper(int style, TreeTab host, Composite treeGroup, Composite imageTreeGroup) {
		this.host = host;
		/* Create the text tree */
		tree1 = new Tree(treeGroup, style);

		if (host.multipleColumns.getSelection()) {
			for (String columnTitle : columnTitles) {
				TreeColumn treeColumn = new TreeColumn(tree1, SWT.NONE);
				treeColumn.setText(columnTitle);
				treeColumn.setToolTipText(ControlExample.getResourceString("Tooltip", columnTitle));
			}
			tree1.setSortColumn(tree1.getColumn(0));
		}
		for (int i = 0; i < 4; i++) {
			TreeItem item = new TreeItem(tree1, SWT.NONE);
			setItemText(item, i, ControlExample.getResourceString("Node_" + (i + 1)));
			if (i < 3) {
				TreeItem subitem = new TreeItem(item, SWT.NONE);
				setItemText(subitem, i, ControlExample.getResourceString("Node_" + (i + 1) + "_1"));
			}
		}
		TreeItem treeRoots[] = tree1.getItems();
		TreeItem item = new TreeItem(treeRoots[1], SWT.NONE);
		setItemText(item, 1, ControlExample.getResourceString("Node_2_2"));
		item = new TreeItem(item, SWT.NONE);
		setItemText(item, 1, ControlExample.getResourceString("Node_2_2_1"));
		textNode1 = treeRoots[0];
		packColumns(tree1);
		try {
			TreeColumn column = tree1.getColumn(0);
			host.resizableColumns.setSelection(column.getResizable());
		} catch (IllegalArgumentException ex) {
		}

		/* Create the image tree */
		tree2 = new Tree(imageTreeGroup, style);
		Image image = host.instance.images[ControlExample.ciClosedFolder];
		if (host.multipleColumns.getSelection()) {
			for (int i = 0; i < columnTitles.length; i++) {
				TreeColumn treeColumn = new TreeColumn(tree2, SWT.NONE);
				treeColumn.setText(columnTitles[i]);
				treeColumn.setToolTipText(ControlExample.getResourceString("Tooltip", columnTitles[i]));
				if (host.headerImagesButton.getSelection())
					treeColumn.setImage(host.instance.images[i % 3]);
			}
		}
		for (int i = 0; i < 4; i++) {
			item = new TreeItem(tree2, SWT.NONE);
			setItemText(item, i, ControlExample.getResourceString("Node_" + (i + 1)));
			if (host.multipleColumns.getSelection() && host.subImagesButton.getSelection()) {
				for (int j = 0; j < columnTitles.length; j++) {
					item.setImage(j, image);
				}
			} else {
				item.setImage(image);
			}
			if (i < 3) {
				TreeItem subitem = new TreeItem(item, SWT.NONE);
				setItemText(subitem, i, ControlExample.getResourceString("Node_" + (i + 1) + "_1"));
				if (host.multipleColumns.getSelection() && host.subImagesButton.getSelection()) {
					for (int j = 0; j < columnTitles.length; j++) {
						subitem.setImage(j, image);
					}
				} else {
					subitem.setImage(image);
				}
			}
		}
		treeRoots = tree2.getItems();
		item = new TreeItem(treeRoots[1], SWT.NONE);
		setItemText(item, 1, ControlExample.getResourceString("Node_2_2"));
		if (host.multipleColumns.getSelection() && host.subImagesButton.getSelection()) {
			for (int j = 0; j < columnTitles.length; j++) {
				item.setImage(j, image);
			}
		} else {
			item.setImage(image);
		}
		item = new TreeItem(item, SWT.NONE);
		setItemText(item, 1, ControlExample.getResourceString("Node_2_2_1"));
		if (host.multipleColumns.getSelection() && host.subImagesButton.getSelection()) {
			for (int j = 0; j < columnTitles.length; j++) {
				item.setImage(j, image);
			}
		} else {
			item.setImage(image);
		}
		imageNode1 = treeRoots[0];
		packColumns(tree2);
	}

	void setItemText(TreeItem item, int i, String node) {
		int index = i % 3;
		if (host.multipleColumns.getSelection()) {
			tableData[index][0] = node;
			item.setText(tableData[index]);
		} else {
			item.setText(node);
		}
	}

	void packColumns(Tree tree) {
		if (host.multipleColumns.getSelection()) {
			int columnCount = tree.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				TreeColumn treeColumn = tree.getColumn(i);
				treeColumn.pack();
			}
		}
	}

	public void makeTreesEditable() {
		makeTreeEditable(tree1);
		makeTreeEditable(tree2);
	}

	private void makeTreeEditable(Tree tree) {
		final TreeEditor editor = new TreeEditor(tree);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		tree.addListener(SWT.MouseDoubleClick, event -> {
			treeDoubleClickListener(tree, editor, event);
		});
	}

	private void treeDoubleClickListener(Tree tree, final TreeEditor editor, Event event) {
		if (!host.editableButton.getSelection()) {
			return;
		}
		Point point = new Point(event.x, event.y);
		TreeItem item = tree.getItem(point);
		if (item == null) {
			return;
		}
		// Get the item text
		final String oldText = item.getText();

		// Create a text field to edit the item text
		final Text text = new Text(tree, SWT.NONE);
		text.setText(oldText);
		text.selectAll();
		text.setFocus();

		// Add a focus out listener to commit changes on focus lost
		text.addListener(SWT.FocusOut, e -> {
			item.setText(text.getText());
			text.dispose(); // Dispose the text field after editing
		});

		// Add a key listener to commit changes on Enter key pressed
		text.addListener(SWT.KeyDown, e -> {
			if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
				item.setText(text.getText());
				text.dispose(); // Dispose the text field after editing
			}
		});

		// Edit the text field on double click
		editor.setEditor(text, item);
	}

	public void packColumns() {
		packColumns(tree1);
		packColumns(tree2);
	}

	public List<Tree> getExampleWidgets() {
		return List.of(tree1, tree2);
	}

	public List<Item> getExampleWidgetItems() {
		Item[] columns1 = tree1.getColumns();
		Item[] columns2 = tree2.getColumns();
		Item[] allItems = new Item[columns1.length + columns2.length];
		System.arraycopy(columns1, 0, allItems, 0, columns1.length);
		System.arraycopy(columns2, 0, allItems, columns1.length, columns2.length);
		return List.of(allItems);
	}

	public void setHeaderBackground(Table colorAndFontTable) {
		if (!host.instance.startup) {
			tree1.setHeaderBackground(headerBackgroundColor);
			tree2.setHeaderBackground(headerBackgroundColor);
		}
		/*
		 * Set the header background color item's image to match the header background
		 * color.
		 */
		Color color = headerBackgroundColor;
		if (color == null)
			color = tree1.getHeaderBackground();
		TableItem item = colorAndFontTable.getItem(HEADER_BACKGROUND_COLOR);
		Image oldImage1 = item.getImage();
		if (oldImage1 != null)
			oldImage1.dispose();
		item.setImage(host.colorImage(color));
	}

	public void setHeaderForeground(Table colorAndFontTable) {
		if (!host.instance.startup) {
			tree1.setHeaderForeground(headerForegroundColor);
			tree2.setHeaderForeground(headerForegroundColor);
		}
		/*
		 * Set the header foreground color item's image to match the header foreground
		 * color.
		 */
		Color color = headerForegroundColor;
		if (color == null)
			color = tree1.getHeaderForeground();
		TableItem item = colorAndFontTable.getItem(HEADER_FOREGROUND_COLOR);
		Image oldImage1 = item.getImage();
		if (oldImage1 != null)
			oldImage1.dispose();
		item.setImage(host.colorImage(color));

	}

	public void setColumnsMoveable() {
		boolean selection = host.moveableColumns.getSelection();
		TreeColumn[] columns1 = tree1.getColumns();
		for (TreeColumn column : columns1) {
			column.setMoveable(selection);
		}
		TreeColumn[] columns2 = tree2.getColumns();
		for (TreeColumn column : columns2) {
			column.setMoveable(selection);
		}
	}

	public void setColumnsResizable() {
		boolean selection = host.resizableColumns.getSelection();
		TreeColumn[] columns1 = tree1.getColumns();
		for (TreeColumn column : columns1) {
			column.setResizable(selection);
		}
		TreeColumn[] columns2 = tree2.getColumns();
		for (TreeColumn column : columns2) {
			column.setResizable(selection);
		}
	}

	public int getStyle() {
		return tree1.getStyle();
	}

	public boolean columnMovable() {
		return tree1.getColumn(0).getMoveable();
	}

	public boolean columnResizable() {
		return tree1.getColumn(0).getResizable();
	}

	public boolean headerVisible() {
		return tree1.getHeaderVisible();
	}

	public boolean linesVisible() {
		return tree1.getLinesVisible();
	}

	public void setCellBackground(Table colorAndFontTable) {
		if (!host.instance.startup) {
			textNode1.setBackground (1, cellBackgroundColor);
			imageNode1.setBackground (1, cellBackgroundColor);
		}
		/* Set the background color item's image to match the background color of the cell. */
		Color color = cellBackgroundColor;
		if (color == null) color = textNode1.getBackground (1);
		TableItem item = colorAndFontTable.getItem(CELL_BACKGROUND_COLOR);
		Image oldImage = item.getImage();
		if (oldImage != null) oldImage.dispose();
		item.setImage (host.colorImage(color));
	}

	public void setCellForeground(Table colorAndFontTable) {
		if (!host.instance.startup) {
			textNode1.setForeground (1, cellForegroundColor);
			imageNode1.setForeground (1, cellForegroundColor);
		}
		/* Set the foreground color item's image to match the foreground color of the cell. */
		Color color = cellForegroundColor;
		if (color == null) color = textNode1.getForeground (1);
		TableItem item = colorAndFontTable.getItem(CELL_FOREGROUND_COLOR);
		Image oldImage = item.getImage();
		if (oldImage != null) oldImage.dispose();
		item.setImage (host.colorImage(color));

	}

	public void setCellFont(Table colorAndFontTable) {
		if (!host.instance.startup) {
			textNode1.setFont (1, cellFont);
			imageNode1.setFont (1, cellFont);
		}
		/* Set the font item's image to match the font of the item. */
		Font ft = cellFont;
		if (ft == null) ft = textNode1.getFont (1);
		TableItem item = colorAndFontTable.getItem(CELL_FONT);
		Image oldImage = item.getImage();
		if (oldImage != null) oldImage.dispose();
		item.setImage (host.fontImage(ft));
		item.setFont(ft);
		colorAndFontTable.layout ();

	}

	public void setItemBackground(Table colorAndFontTable) {
		if (!host.instance.startup) {
			textNode1.setBackground (itemBackgroundColor);
			imageNode1.setBackground (itemBackgroundColor);
		}
		/* Set the background button's color to match the background color of the item. */
		Color color = itemBackgroundColor;
		if (color == null) color = textNode1.getBackground ();
		TableItem item = colorAndFontTable.getItem(ITEM_BACKGROUND_COLOR);
		Image oldImage = item.getImage();
		if (oldImage != null) oldImage.dispose();
		item.setImage (host.colorImage(color));

	}

	public void setItemForeground(Table colorAndFontTable) {
		if (!host.instance.startup) {
			textNode1.setForeground (itemForegroundColor);
			imageNode1.setForeground (itemForegroundColor);
		}
		/* Set the foreground button's color to match the foreground color of the item. */
		Color color = itemForegroundColor;
		if (color == null) color = textNode1.getForeground ();
		TableItem item = colorAndFontTable.getItem(ITEM_FOREGROUND_COLOR);
		Image oldImage = item.getImage();
		if (oldImage != null) oldImage.dispose();
		item.setImage (host.colorImage(color));

	}

	public void setItemFont(Table colorAndFontTable) {
		if (!host.instance.startup) {
			textNode1.setFont (itemFont);
			imageNode1.setFont (itemFont);
		}
		/* Set the font item's image to match the font of the item. */
		Font ft = itemFont;
		if (ft == null) ft = textNode1.getFont ();
		TableItem item = colorAndFontTable.getItem(ITEM_FONT);
		Image oldImage = item.getImage();
		if (oldImage != null) oldImage.dispose();
		item.setImage (host.fontImage(ft));
		item.setFont(ft);
		colorAndFontTable.layout ();

	}

	public void setWidgetHeaderVisible(boolean value) {
		tree1.setHeaderVisible (value);
		tree2.setHeaderVisible (value);
	}

	public void setWidgetSortIndicator(boolean selection) {
		if (selection) {
			initializeSortState (tree1);
			initializeSortState (tree2);
		} else {
			resetSortState (tree1);
			resetSortState (tree2);
		}
	}

	/**
	 * Sets the initial sort indicator state and adds a listener
	 * to cycle through sort states and columns.
	 */
	void initializeSortState (final Tree tree) {
		/* Reset to known state: 'down' on column 0. */
		tree.setSortDirection (SWT.DOWN);
		TreeColumn [] columns = tree.getColumns();
		for (int i = 0; i < columns.length; i++) {
			TreeColumn column = columns[i];
			if (i == 0) tree.setSortColumn(column);
			SelectionListener listener = widgetSelectedAdapter(e -> {
				int sortDirection = SWT.DOWN;
				if (e.widget == tree.getSortColumn()) {
					/* If the sort column hasn't changed, cycle down -> up -> none. */
					switch (tree.getSortDirection ()) {
					case SWT.DOWN: sortDirection = SWT.UP; break;
					case SWT.UP: sortDirection = SWT.NONE; break;
					}
				} else {
					tree.setSortColumn((TreeColumn)e.widget);
				}
				tree.setSortDirection (sortDirection);
			});
			column.addSelectionListener(listener);
			column.setData("SortListener", listener);	//$NON-NLS-1$
		}
	}

	void resetSortState (final Tree tree) {
		tree.setSortDirection (SWT.NONE);
		TreeColumn [] columns = tree.getColumns();
		for (TreeColumn column : columns) {
			SelectionListener listener = (SelectionListener)column.getData("SortListener");	//$NON-NLS-1$
			if (listener != null) column.removeSelectionListener(listener);
		}
	}

	public void setWidgetLinesVisible(boolean selection) {
		tree1.setLinesVisible (selection);
		tree2.setLinesVisible (selection);
	}

	public void changeFontOrColor(int index) {
		switch (index) {
		case ITEM_FOREGROUND_COLOR: {
			Color oldColor = itemForegroundColor;
			if (oldColor == null) oldColor = textNode1.getForeground ();
			host.colorDialog.setRGB(oldColor.getRGB());
			RGB rgb = host.colorDialog.open();
			if (rgb == null) return;
			itemForegroundColor = new Color (rgb);
			host.setItemForeground ();
		}
		break;
		case ITEM_BACKGROUND_COLOR: {
			Color oldColor = itemBackgroundColor;
			if (oldColor == null) oldColor = textNode1.getBackground ();
			host.colorDialog.setRGB(oldColor.getRGB());
			RGB rgb = host.colorDialog.open();
			if (rgb == null) return;
			itemBackgroundColor = new Color (rgb);
			host.setItemBackground ();
		}
		break;
		case ITEM_FONT: {
			Font oldFont = itemFont;
			if (oldFont == null) oldFont = textNode1.getFont ();
			host.fontDialog.setFontList(oldFont.getFontData());
			FontData fontData = host.fontDialog.open ();
			if (fontData == null) return;
			oldFont = itemFont;
			itemFont = new Font (host.display, fontData);
			host.setItemFont ();
			host.setExampleWidgetSize ();
			if (oldFont != null) oldFont.dispose ();
		}
		break;
		case CELL_FOREGROUND_COLOR: {
			Color oldColor = cellForegroundColor;
			if (oldColor == null) oldColor = textNode1.getForeground (1);
			host.colorDialog.setRGB(oldColor.getRGB());
			RGB rgb = host.colorDialog.open();
			if (rgb == null) return;
			cellForegroundColor = new Color (rgb);
			host.setCellForeground ();
		}
		break;
		case CELL_BACKGROUND_COLOR: {
			Color oldColor = cellBackgroundColor;
			if (oldColor == null) oldColor = textNode1.getBackground (1);
			host.colorDialog.setRGB(oldColor.getRGB());
			RGB rgb = host.colorDialog.open();
			if (rgb == null) return;
			cellBackgroundColor = new Color (rgb);
			host.setCellBackground ();
		}
		break;
		case CELL_FONT: {
			Font oldFont = cellFont;
			if (oldFont == null) oldFont = textNode1.getFont (1);
			host.fontDialog.setFontList(oldFont.getFontData());
			FontData fontData = host.fontDialog.open ();
			if (fontData == null) return;
			oldFont = cellFont;
			cellFont = new Font (host.display, fontData);
			host.setCellFont ();
			host.setExampleWidgetSize ();
			if (oldFont != null) oldFont.dispose ();
		}
		break;
		case HEADER_FOREGROUND_COLOR: {
			Color oldColor = headerForegroundColor;
			if (oldColor == null) oldColor = tree1.getHeaderForeground();
			host.colorDialog.setRGB(oldColor.getRGB());
			RGB rgb = host.colorDialog.open();
			if (rgb == null) return;
			headerForegroundColor = new Color (rgb);
			host.setHeaderForeground ();
		}
		break;
		case HEADER_BACKGROUND_COLOR: {
			Color oldColor = headerBackgroundColor;
			if (oldColor == null) oldColor = tree1.getHeaderBackground();
			host.colorDialog.setRGB(oldColor.getRGB());
			RGB rgb = host.colorDialog.open();
			if (rgb == null) return;
			headerBackgroundColor = new Color (rgb);
			host.setHeaderBackground ();
		}
		default:
			host.changeFontOrColor(index);
		}

	}

	public void resetColorsAndFonts(Table colorAndFontTable, Font font) {
		itemForegroundColor = null;
		setItemForeground (colorAndFontTable);
		itemBackgroundColor = null;
		setItemBackground (colorAndFontTable);
		Font oldFont = font;
		itemFont = null;
		setItemFont (colorAndFontTable);
		if (oldFont != null) oldFont.dispose();
		cellForegroundColor = null;
		setCellForeground (colorAndFontTable);
		cellBackgroundColor = null;
		setCellBackground (colorAndFontTable);
		oldFont = font;
		cellFont = null;
		host.setCellFont ();
		if (oldFont != null) oldFont.dispose();
		headerBackgroundColor = null;
		setHeaderBackground (colorAndFontTable);
		headerForegroundColor = null;
		setHeaderForeground (colorAndFontTable);

	}

	public void addDisposeListener() {
		if (itemFont != null) itemFont.dispose();
		if (cellFont != null) cellFont.dispose();
		itemBackgroundColor = null;
		itemForegroundColor = null;
		itemFont = null;
		cellBackgroundColor = null;
		cellForegroundColor = null;
		cellFont = null;
		headerForegroundColor = null;
		headerBackgroundColor = null;
	}
}
