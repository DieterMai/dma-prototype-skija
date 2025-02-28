package org.eclipse.swt.widgets;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;

public interface ITreeColumn {

	void removeControlListener(ControlListener columnListener);

	void addControlListener(ControlListener columnListener);

	void setText(String columnTitle);

	void setToolTipText(String resourceString);

	boolean getResizable();

	void setImage(Image image);

	void pack();

	void setMoveable(boolean selection);

	void setResizable(boolean selection);

	boolean getMoveable();

	void addSelectionListener(SelectionListener listener);

	void setData(String string, Object listener);

	Object getData(String string);

	void removeSelectionListener(SelectionListener listener);

}
