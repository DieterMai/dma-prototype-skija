package org.eclipse.swt.examples.controlexample;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ITree;
import org.eclipse.swt.widgets.ITreeColumn;
import org.eclipse.swt.widgets.ITreeItem;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeColumn_old;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TreeItem_old;
import org.eclipse.swt.widgets.Tree_old;

public class WrapperTee {
	interface IFactory {
		ITree createTree(Composite tree, int style);

		ITreeColumn createColumn(ITree parent, int style);

		ITreeItem createItem(ITree parent, int style);

		ITreeItem createItem(ITreeItem iTreeItem, int none);
	}

	class OldFactory implements IFactory {
		@Override
		public ITree createTree(Composite tree, int style) {
			return new Tree_old(tree, style);
		}

		@Override
		public ITreeColumn createColumn(ITree parent, int style) {
			return new TreeColumn_old((Tree_old) parent, style);
		}

		@Override
		public ITreeItem createItem(ITree parent, int style) {
			return new TreeItem_old((Tree_old) parent, style);
		}

		@Override
		public ITreeItem createItem(ITreeItem item, int style) {
			return new TreeItem_old((TreeItem_old) item, style);
		}

	}

	class NewFactory implements IFactory {
		@Override
		public ITree createTree(Composite tree, int style) {
			return new Tree(tree, style);
		}

		@Override
		public ITreeColumn createColumn(ITree parent, int style) {
			return new TreeColumn((Tree) parent, style);
		}

		@Override
		public ITreeItem createItem(ITree parent, int style) {
			return new TreeItem((Tree) parent, style);
		}

		@Override
		public ITreeItem createItem(ITreeItem item, int style) {
			return new TreeItem((TreeItem) item, style);
		}

	}

	TreeTabExampleWrapper old;
	TreeTabExampleWrapper neo;

	public WrapperTee(int style, TreeTab host, Composite treeGroup, Composite imageTreeGroup) {
		old = new TreeTabExampleWrapper(style, host, treeGroup, imageTreeGroup, new OldFactory());
		neo = new TreeTabExampleWrapper(style, host, treeGroup, imageTreeGroup, new NewFactory());
	}

	public void addDisposeListener() {
		old.addDisposeListener();
		neo.addDisposeListener();
	}

	public void changeFontOrColor(int index) {
		old.changeFontOrColor(index);
		neo.changeFontOrColor(index);
	}

	public void makeTreesEditable() {
		old.makeTreesEditable();
		neo.makeTreesEditable();
	}

	public void packColumns() {
		old.packColumns();
		neo.packColumns();
	}

	public void setHeaderBackground(Table colorAndFontTable) {
		old.setHeaderBackground(colorAndFontTable);
		neo.setHeaderBackground(colorAndFontTable);
	}

	public void setHeaderForeground(Table colorAndFontTable) {
		old.setHeaderForeground(colorAndFontTable);
		neo.setHeaderForeground(colorAndFontTable);
	}

	public void setColumnsResizable() {
		old.setColumnsResizable();
		neo.setColumnsResizable();
	}

	public void resetColorsAndFonts(Table colorAndFontTable, Font font) {
		old.resetColorsAndFonts(colorAndFontTable, font);
		neo.resetColorsAndFonts(colorAndFontTable, font);
	}

	public void setColumnsMoveable() {
		old.setColumnsMoveable();
		neo.setColumnsMoveable();
	}

	public void setCellForeground(Table colorAndFontTable) {
		old.setCellForeground(colorAndFontTable);
		neo.setCellForeground(colorAndFontTable);
	}

	public void setCellBackground(Table colorAndFontTable) {
		old.setCellBackground(colorAndFontTable);
		neo.setCellBackground(colorAndFontTable);
	}

	public void setCellFont(Table colorAndFontTable) {
		old.setCellFont(colorAndFontTable);
		neo.setCellFont(colorAndFontTable);
	}

	public void setItemBackground(Table colorAndFontTable) {
		old.setItemBackground(colorAndFontTable);
		neo.setItemBackground(colorAndFontTable);
	}

	public void setItemForeground(Table colorAndFontTable) {
		old.setItemForeground(colorAndFontTable);
		neo.setItemForeground(colorAndFontTable);
	}

	public void setItemFont(Table colorAndFontTable) {
		old.setItemFont(colorAndFontTable);
		neo.setItemFont(colorAndFontTable);
	}

	public void setWidgetHeaderVisible(boolean selection) {
		old.setWidgetHeaderVisible(selection);
		neo.setWidgetHeaderVisible(selection);
	}

	public void setWidgetSortIndicator(boolean selection) {
		old.setWidgetSortIndicator(selection);
		neo.setWidgetSortIndicator(selection);
	}

	public void setWidgetLinesVisible(boolean selection) {
		old.setWidgetLinesVisible(selection);
		neo.setWidgetLinesVisible(selection);
	}

	public List<Item> getExampleWidgetItems() {
		List<Item> allItems = new ArrayList<>();
		allItems.addAll(old.getExampleWidgetItems());
		allItems.addAll(neo.getExampleWidgetItems());
		return allItems;
	}

	public List<ITree> getExampleWidgets() {
		List<ITree> allItems = new ArrayList<>();
		allItems.addAll(old.getExampleWidgets());
		allItems.addAll(neo.getExampleWidgets());
		return allItems;
	}

	public int getStyle() {
		int oldValue = old.getStyle();
		int newValue = neo.getStyle();
		if (oldValue != newValue) {
			System.err.println("WrapperTee.getStyle() old: " + styleToString(oldValue));
			System.err.println("                      new: " + styleToString(newValue));
		}
		return oldValue;
	}

	String styleToString(int style) {
		List<String> flags = new ArrayList<>();

		if((style & SWT.SINGLE) != 0) {
			flags.add("SINGLE");
			style &= ~SWT.SINGLE;
		}
		if((style & SWT.MULTI) != 0) {
			flags.add("MULTI");
			style &= ~SWT.MULTI;
		}
		if((style & SWT.CHECK) != 0) {
			flags.add("CHECK");
			style &= ~SWT.CHECK;
		}
		if((style & SWT.FULL_SELECTION) != 0) {
			flags.add("FULL_SELECTION");
			style &= ~SWT.FULL_SELECTION;
		}
		if((style & SWT.VIRTUAL) != 0) {
			flags.add("VIRTUAL");
			style &= ~SWT.VIRTUAL;
		}
		if((style & SWT.NO_SCROLL) != 0) {
			flags.add("NO_SCROLL");
			style &= ~SWT.NO_SCROLL;
		}
		if((style & SWT.H_SCROLL) != 0) {
			flags.add("H_SCROLL");
			style &= ~SWT.H_SCROLL;
		}
		if((style & SWT.V_SCROLL) != 0) {
			flags.add("V_SCROLL");
			style &= ~SWT.V_SCROLL;
		}
		if((style & SWT.NO_BACKGROUND) != 0) {
			flags.add("NO_BACKGROUND");
			style &= ~SWT.NO_BACKGROUND;
		}
		if((style & SWT.NO_MERGE_PAINTS) != 0) {
			flags.add("NO_MERGE_PAINTS");
			style &= ~SWT.NO_MERGE_PAINTS;
		}
		if((style & SWT.NO_FOCUS) != 0) {
			flags.add("NO_FOCUS");
			style &= ~SWT.NO_FOCUS;
		}
		if((style & SWT.NO_REDRAW_RESIZE) != 0) {
			flags.add("NO_REDRAW_RESIZE");
			style &= ~SWT.NO_REDRAW_RESIZE;
		}
		if((style & SWT.NO_RADIO_GROUP) != 0) {
			flags.add("NO_RADIO_GROUP");
			style &= ~SWT.NO_RADIO_GROUP;
		}
		if((style & SWT.EMBEDDED) != 0) {
			flags.add("EMBEDDED");
			style &= ~SWT.EMBEDDED;
		}
		if((style & SWT.DOUBLE_BUFFERED) != 0) {
			flags.add("DOUBLE_BUFFERED");
			style &= ~SWT.DOUBLE_BUFFERED;
		}
		if((style & SWT.BORDER) != 0) {
			flags.add("BORDER");
			style &= ~SWT.BORDER;
		}
		if((style & SWT.LEFT_TO_RIGHT) != 0) {
			flags.add("LEFT_TO_RIGHT");
			style &= ~SWT.LEFT_TO_RIGHT;
		}
		if((style & SWT.RIGHT_TO_LEFT) != 0) {
			flags.add("RIGHT_TO_LEFT");
			style &= ~SWT.RIGHT_TO_LEFT;
		}
		if((style & SWT.FLIP_TEXT_DIRECTION) != 0) {
			flags.add("FLIP_TEXT_DIRECTION");
			style &= ~SWT.FLIP_TEXT_DIRECTION;
		}


		if(style != 0) {
			System.out.println("WrapperTee.styleToString() not checked flags: "+Integer.toBinaryString(style));
		}

		return String.join(" ", flags);
	}

	public boolean columnResizable() {
		boolean oldValue = old.columnResizable();
		boolean newValue = neo.columnResizable();
		if (oldValue != newValue) {
			System.err.println("WrapperTee.columnResizable() old: " + oldValue + ", new: " + newValue);
		}
		return oldValue;
	}

	public boolean columnMovable() {
		boolean oldValue = old.columnMovable();
		boolean newValue = neo.columnMovable();
		if (oldValue != newValue) {
			System.err.println("WrapperTee.columnMovable() old: " + oldValue + ", new: " + newValue);
		}
		return oldValue;
	}

	public boolean headerVisible() {
		boolean oldValue = old.headerVisible();
		boolean newValue = neo.headerVisible();
		if (oldValue != newValue) {
			System.err.println("WrapperTee.headerVisible() old: " + oldValue + ", new: " + newValue);
		}
		return oldValue;
	}

	public boolean linesVisible() {
		boolean oldValue = old.linesVisible();
		boolean newValue = neo.linesVisible();
		if (oldValue != newValue) {
			System.err.println("WrapperTee.linesVisible() old: " + oldValue + ", new: " + newValue);
		}
		return oldValue;
	}
}
