package com.hfnu.zl.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * list ViewHolder
 */
public class ListViewHolder {
	private SparseArray<View> mViews;
	private View convertView;
	private int mPosition;
	private Context mContext;

	public ListViewHolder(Context context, ViewGroup parent, int LayoutId, int position) {
		this.mContext = context;
		mPosition = position;
		mViews = new SparseArray<View>();
		convertView = LayoutInflater.from(context).inflate(LayoutId, parent, false);
		convertView.setTag(this);
	}

	public static ListViewHolder get(Context context, View convertView, ViewGroup parent, int LayoutId, int position) {
		if (convertView == null) {
			return new ListViewHolder(context, parent, LayoutId, position);
		} else {
			ListViewHolder viewHolder = (ListViewHolder) convertView.getTag();
			viewHolder.mPosition = position;
			return viewHolder;
		}
	}
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}
	/****以下为辅助方法*****/

	/**
	 * 设置TextView的值
	 *
	 * @param viewId
	 * @param text
	 * @return
	 */
	public ListViewHolder setText(int viewId, String text)
	{
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}
	public ListViewHolder setText(int viewId, int textId)
	{
		TextView tv = getView(viewId);
		tv.setText(textId);
		return this;
	}

	public ListViewHolder setImageResource(int viewId, int resId)
	{
		ImageView view = getView(viewId);
		view.setImageResource(resId);
		return this;
	}

	public ListViewHolder setImageBitmap(int viewId, Bitmap bitmap)
	{
		ImageView view = getView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}

	public ListViewHolder setImageDrawable(int viewId, Drawable drawable)
	{
		ImageView view = getView(viewId);
		view.setImageDrawable(drawable);
		return this;
	}

	public ListViewHolder setBackgroundColor(int viewId, int color)
	{
		View view = getView(viewId);
		view.setBackgroundColor(color);
		return this;
	}

	public ListViewHolder setBackgroundRes(int viewId, int backgroundRes)
	{
		View view = getView(viewId);
		view.setBackgroundResource(backgroundRes);
		return this;
	}

	public ListViewHolder setTextColor(int viewId, int textColor)
	{
		TextView view = getView(viewId);
		view.setTextColor(textColor);
		return this;
	}

	public ListViewHolder setTextColorRes(int viewId, int textColorRes)
	{
		TextView view = getView(viewId);
		view.setTextColor(mContext.getResources().getColor(textColorRes));
		return this;
	}
	public ListViewHolder setViewVisibility(int viewId,int Visibility){
		View view = getView(viewId);
		view.setVisibility(Visibility);
		return this;
	}
	public int getPosition() {
		return mPosition;
	}

	public View getConvertView() {
		return convertView;
	}
}
