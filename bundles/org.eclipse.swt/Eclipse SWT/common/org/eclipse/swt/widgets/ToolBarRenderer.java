package org.eclipse.swt.widgets;

import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.ToolBar.*;

import java.util.List;

class ToolBarRenderer implements IToolBarRenderer {
	private final ToolBar bar;

	private record ItemRecord(ToolItem item, Rectangle bounds, boolean isSeprator) {
	}

	private class Row {
		final List<ItemRecord> items = new ArrayList<>();
		final Point totalSize;
		int offset;
		int maxItemSize;
		int position;
		boolean containsSeparator;
		boolean hasRowSeparator;


		Row(Point availableSize, int offset) {
			this.totalSize = availableSize;
			this.offset = offset;
		}

		public boolean hasSpaceFor(ItemRecord itemRecord) {
			if (bar.isHorizontal()) {
				int freeSpace = totalSize.x - offset;
				return freeSpace >= itemRecord.bounds.width;
			} else {
				return true;
			}
		}

		public void add(ItemRecord itemRecord) {
			items.add(itemRecord);
			containsSeparator |= itemRecord.isSeprator;
			if (bar.isHorizontal()) {
				itemRecord.bounds.x = offset;
				offset += itemRecord.bounds().width + 1;
				maxItemSize = Math.max(maxItemSize, itemRecord.bounds.height);
			} else {
				itemRecord.bounds.y = offset;
				offset += itemRecord.bounds().height;
				maxItemSize = Math.max(maxItemSize, itemRecord.bounds.width);
			}
		}

		void normalizeHeight() {
			int maxPossibleSize = Math.min(maxItemSize, totalSize.x);
			for (ItemRecord itemRecord : items) {
				itemRecord.bounds.height = maxPossibleSize;
			}
		}

		void normalizeWidth() {
			int maxPossibleSize = Math.min(maxItemSize, totalSize.x);
			for (ItemRecord itemRecord : items) {
				itemRecord.bounds.width = maxPossibleSize;
			}
		}

		void setY(int y) {
			position = y;
			for (ItemRecord itemRecord : items) {
				itemRecord.bounds.y = y;
			}
		}

		void setX(int x) {
			for (ItemRecord itemRecord : items) {
				itemRecord.bounds.x = x;
			}
		}

		void mirrow() {
			for (ItemRecord itemRecord : items) {
				itemRecord.bounds.x = totalSize.x - itemRecord.bounds.x - itemRecord.bounds.width;
			}
		}
	}

	ToolBarRenderer(ToolBar toolbar) {
		this.bar = toolbar;
	}

	@Override
	public void render(GC gc, Rectangle bounds) {
		Point size = new Point(bounds.width, bounds.height);
		List<Row> rows = computeRows(size);

		render(gc, bounds, rows);
	}

	private void render(GC gc, Rectangle bounds, List<Row> rows) {
		gc.setBackground(bar.getBackground());
		gc.fillRectangle(0, 0, bounds.width, bounds.height);

		if (bar.isShadowOut()) {
			gc.setForeground(new Color(160, 160, 160));
			gc.drawLine(0, 0, bounds.width, 0);
		}

//		dumpRows(rows);
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			for (ItemRecord itemRecord : row.items) {
				itemRecord.item.render(gc, itemRecord.bounds);
			}
			if (row.hasRowSeparator) {
				drawHorizontalSeparator(gc, row);
			}
		}
	}

	private void drawHorizontalSeparator(GC gc, Row row) {
		int pos = row.position + row.maxItemSize + 3;
		gc.setForeground(bar.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		gc.drawLine(0, pos, row.totalSize.y, pos);
	}

	private List<Row> computeRows(Point size) {
		// Collect all item sizes
		List<ItemRecord> itemRecords = new ArrayList<>();
		for (int i = 0; i < bar.getItemCount(); i++) {
			ToolItem item = bar.getItem(i);
			Point preferedSize = item.getSize();
			Rectangle initialBounds = new Rectangle(0, 0, preferedSize.x, preferedSize.y);
			ItemRecord itemRecord = new ItemRecord(item, initialBounds, item.isSeparator());
			itemRecords.add(itemRecord);
		}

		Point offset = new Point(0, 0);

		if (bar.isShadowOut()) {
			offset.y++;
		}


		if (bar.isHorizontal()) {
			if (bar.isWrap()) {
				return computeMultipleHorizontalRows(size, itemRecords, offset);
			} else {
				return computeSingleHorizontalRow(size, itemRecords, offset);
			}
		} else {
			return computeSingleVerticalRow(size, itemRecords, offset);
		}
	}

	private List<Row> computeSingleHorizontalRow(Point barSize, List<ItemRecord> itemRecords, Point initialOffset) {
		Row row = new Row(barSize, initialOffset.x);

		for (ItemRecord itemRecord : itemRecords) {
			row.add(itemRecord);
		}

		row.normalizeHeight();
		row.setY(initialOffset.y);

		if (bar.isRightToLeft()) {
			row.mirrow();
		}

		return List.of(row);
	}

	private List<Row> computeSingleVerticalRow(Point barSize, List<ItemRecord> itemRecords, Point initialOffset) {
		Row row = new Row(barSize, initialOffset.y);

		for (ItemRecord itemRecord : itemRecords) {
			row.add(itemRecord);
		}

		row.normalizeWidth();
		row.setX(initialOffset.x);

		return List.of(row);
	}

	private List<Row> computeMultipleHorizontalRows(Point barSize, List<ItemRecord> itemRecords, Point initialOffset) {
		List<Row> rows = sortItemsIntoRows(barSize, itemRecords, initialOffset);

		int yOffset = initialOffset.y;
		Iterator<Row> iter = rows.iterator();
		while (iter.hasNext()) {
			Row row = iter.next();
			row.normalizeHeight();
			row.setY(yOffset);

			yOffset += row.maxItemSize;

			if (requiresRowSeparator(row, !iter.hasNext())) {
				row.hasRowSeparator = true;
				yOffset += 3;
			}

			if (bar.isRightToLeft()) {
				row.mirrow();
			}
		}

		return rows;
	}

	private boolean requiresRowSeparator(Row row, boolean isLastRow) {
		if (isLastRow) {
			return false;
		}
		if (row.containsSeparator && bar.isFlat() && bar.isWrap() && bar.isHorizontal()) {
			return true;
		}
		return false;
	}

	private List<Row> sortItemsIntoRows(Point barSize, List<ItemRecord> itemRecords, Point initialOffset) {
		List<Row> rows = new ArrayList<>();
		Row row = new Row(barSize, initialOffset.x);
		rows.add(row);

		for (ItemRecord itemRecord : itemRecords) {
			if (row.items.isEmpty() || row.hasSpaceFor(itemRecord)) {
				row.add(itemRecord);
			} else {
				row = new Row(barSize, initialOffset.x);
				rows.add(row);
				row.add(itemRecord);
			}
		}
		return rows;
	}

	@Override
	public int computeWidth() {
		if (bar.isHorizontal()) {
			int totalWidth = 0;
			for (int i = 0; i < bar.getItemCount(); i++) {
				totalWidth += bar.getItem(i).getWidth() + 1;
			}
			return totalWidth;
		} else {
			int max = 1;
			for (int i = 0; i < bar.getItemCount(); i++) {
				max = Math.max(max, bar.getItem(i).getWidth());
			}
			return max;
		}
	}

	@Override
	public int computeHeight() {
		if (bar.isVertical()) {
			int totalWidth = 0;
			for (int i = 0; i < bar.getItemCount(); i++) {
				totalWidth += bar.getItem(i).getHeight() + 1;
			}
			return totalWidth;
		} else {
			int max = 1;
			for (int i = 0; i < bar.getItemCount(); i++) {
				max = Math.max(max, bar.getItem(i).getHeight());
			}
			return max;
		}
	}

	private void dumpRows(List<Row> rows) {
		System.out.println("Dump rows:");
		for (Row row : rows) {
			dumpRow(row);
		}
	}

	private void dumpRow(Row row) {
		System.out.println("Row");
		for (ItemRecord item : row.items) {
			System.out.println("    Item " + item.bounds + " - " + item.item);
		}
	}
}
