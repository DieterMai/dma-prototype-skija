package org.eclipse.swt.widgets;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

/**
 * Temporary interface. Delete later.
 */
public interface ITree {

	Composite _composite();

	boolean isDisposed();

	void addTreeListener(TreeListener treeListener);

	Rectangle getClientArea();

	int getColumnCount();

	ITreeColumn getColumn(int column);

	void removeTreeListener(TreeListener treeListener);

	Display getDisplay();

}
