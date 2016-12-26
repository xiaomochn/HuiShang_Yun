package com.huishangyun.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.huishangyun.yun.R;
import com.huishangyun.Adapter.ContactListAcapter;

public class SideBar extends View{
	private char[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private TextView mDialogText;
	private Context context;
	// Bitmap mbitmap;
	private int type = 1;
	private int color = Color.GRAY;

	public SideBar(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	private void init() {

		l = new char[] { '组','#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z' };
		/*
		 * mbitmap = BitmapFactory.decodeResource(getResources(),
		 * R.drawable.scroll_bar_search_icon);
		 */
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setListView(ListView _list) {
		list = _list;
		ListAdapter ha = (ListAdapter) _list.getAdapter();
		ContactListAcapter ad = (ContactListAcapter) ha;
		sectionIndexter = (SectionIndexer) ad;

	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);
		int i = (int) event.getY();

		int idx = i / (getMeasuredHeight() / l.length);
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			setBackgroundResource(R.drawable.scrollbar_bg);
			mDialogText.setVisibility(View.VISIBLE);
			mDialogText.setText(String.valueOf(l[idx]));
			mDialogText.setTextSize(34);
			if (sectionIndexter == null) {

				sectionIndexter = (SectionIndexer) list.getAdapter();

			}
			int position = sectionIndexter.getPositionForSection(l[idx]);

			if (position == -1) {
				return true;
			}
			list.setSelection(position);
		} else {
			mDialogText.setVisibility(View.INVISIBLE);

		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			setBackgroundDrawable(new ColorDrawable(0x00000000));
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_1));
		paint.setStyle(Style.FILL);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setFakeBoldText(true);
		float widthCenter = getMeasuredWidth() / 2;
		if (l.length > 0) {
			float height = getMeasuredHeight() / l.length;
			for (int i = 0; i < l.length; i++) {
				/*
				 * if (i == 0 && type != 2) { canvas.drawBitmap(mbitmap,
				 * widthCenter - 7, (i + 1) height - height / 2, paint); }
				 */// else
				canvas.drawText(String.valueOf(l[i]), widthCenter, (i + 1)
						* height, paint);
			}
		}
		this.invalidate();
		super.onDraw(canvas);
	}
}
