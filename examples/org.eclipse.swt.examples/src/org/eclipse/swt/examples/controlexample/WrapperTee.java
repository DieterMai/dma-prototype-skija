package org.eclipse.swt.examples.controlexample;

import java.util.List;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class WrapperTee {
	TreeTabExampleWrapper old;
	TreeTabExampleWrapper neo;

	public WrapperTee(int style, TreeTab host, Composite treeGroup, Composite imageTreeGroup) {
		old = new TreeTabExampleWrapper(style, host, treeGroup, imageTreeGroup);
		neo = new TreeTabExampleWrapper(style, host, treeGroup, imageTreeGroup);
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
		return old.getExampleWidgetItems();
	}

	public List<Tree> getExampleWidgets() {
		return old.getExampleWidgets();
	}

	public int getStyle() {
		return old.getStyle();
	}

	public boolean columnResizable() {
		return old.columnResizable();
	}

	public boolean columnMovable() {
		return old.columnMovable();
	}

	public boolean headerVisible() {
		return old.headerVisible();
	}

	public boolean linesVisible() {
		return old.linesVisible();
	}
}
