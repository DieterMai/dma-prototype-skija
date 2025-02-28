package org.eclipse.swt.widgets;

import org.eclipse.swt.events.*;

public interface ITreeColumn {

	void removeControlListener(ControlListener columnListener);

	void addControlListener(ControlListener columnListener);

}
