package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.*;

public interface ITreeItem {

	boolean isDisposed();

	Rectangle getBounds(int column);

	Rectangle getImageBounds(int column);

}
