package org.eclipse.swt.examples.controlexample;

import java.util.List;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class WrapperTee {
	TreeTabExampleWrapper old;

	public WrapperTee(int style, TreeTab treeTab) {
		old = new TreeTabExampleWrapper(style, treeTab);
	}


	public void addDisposeListener() {
		old.addDisposeListener();
	}

	public void changeFontOrColor(int index) {
		old.changeFontOrColor(index);
	}

	public void makeTreesEditable() {
		old.makeTreesEditable();
	}

	public void packColumns() {
		old.packColumns();
	}

	public List<Item> getExampleWidgetItems() {
		return old.getExampleWidgetItems();
	}

	public List<Tree> getExampleWidgets() {
		return old.getExampleWidgets();
	}

	public void setHeaderBackground(Table colorAndFontTable) {
		old.setHeaderBackground(colorAndFontTable);
	}

	public void setHeaderForeground(Table colorAndFontTable) {
		old.setHeaderForeground(colorAndFontTable);
	}

	public void setColumnsResizable() {
		old.setColumnsResizable();
	}

	public void resetColorsAndFonts(Table colorAndFontTable, Font font) {
		old.resetColorsAndFonts(colorAndFontTable, font);
	}

	public void setColumnsMoveable() {
		old.setColumnsMoveable();
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

	public void setCellForeground(Table colorAndFontTable) {
		old.setCellForeground(colorAndFontTable);
	}

	public void setCellBackground(Table colorAndFontTable) {
		old.setCellBackground(colorAndFontTable);
	}

	public void setCellFont(Table colorAndFontTable) {
		old.setCellFont(colorAndFontTable);
	}

	public void setItemBackground(Table colorAndFontTable) {
		old.setItemBackground(colorAndFontTable);
	}

	public void setItemForeground(Table colorAndFontTable) {
		old.setItemForeground(colorAndFontTable);
	}

	public void setItemFont(Table colorAndFontTable) {
		old.setItemFont(colorAndFontTable);
	}

	public void setWidgetHeaderVisible(boolean selection) {
		old.setWidgetHeaderVisible(selection);
	}

	public void setWidgetSortIndicator(boolean selection) {
		old.setWidgetSortIndicator(selection);
	}

	public void setWidgetLinesVisible(boolean selection) {
		old.setWidgetLinesVisible(selection);
	}
}
