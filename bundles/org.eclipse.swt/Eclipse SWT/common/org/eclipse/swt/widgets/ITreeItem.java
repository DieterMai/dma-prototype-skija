package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.*;

public interface ITreeItem {

	boolean isDisposed();

	Rectangle getBounds(int column);

	Rectangle getImageBounds(int column);

	void setImage(int j, Image image);

	void setImage(Image image);

	void setText(String[] strings);

	void setText(String node);

	String getText();

	void setBackground(int i, Color cellBackgroundColor);

	void setForeground(int i, Color cellForegroundColor);

	Color getBackground(int i);

	Color getForeground(int i);

	void setBackground(Color itemBackgroundColor);

	Color getBackground();

	void setForeground(Color itemForegroundColor);

	void setFont(Font itemFont);

	Font getFont();

	Color getForeground();

	Font getFont(int i);

	void setFont(int i, Font cellFont);

}
