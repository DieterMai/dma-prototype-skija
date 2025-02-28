package org.eclipse.swt.widgets;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

/**
 * Temporary interface. Delete later.
 */
public interface ITree<C extends ITreeColumn, I extends ITreeItem> {

	Composite _composite();

	boolean isDisposed();

	void addTreeListener(TreeListener treeListener);

	Rectangle getClientArea();

	int getColumnCount();

	C getColumn(int column);

	void removeTreeListener(TreeListener treeListener);

	Display getDisplay();

	void setSortColumn(C column);

	I[] getItems();

	void addListener(int eventType, Listener listener);

	I getItem(Point point);

	C[] getColumns();

	void setHeaderBackground(Color headerBackgroundColor);

	Color getHeaderBackground();

	void setHeaderForeground(Color headerForegroundColor);

	Color getHeaderForeground();

	int getStyle();

	boolean getHeaderVisible();

	boolean getLinesVisible();

	void setHeaderVisible(boolean value);

	void setSortDirection(int down);

	C getSortColumn();

	int getSortDirection();

	void setLinesVisible(boolean selection);

}
