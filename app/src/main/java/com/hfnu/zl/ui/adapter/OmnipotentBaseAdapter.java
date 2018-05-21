package com.hfnu.zl.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 万能适配器
 * @param <T> 泛型参数
 */
public abstract class OmnipotentBaseAdapter<T> extends BaseAdapter {
	protected Context context;
	protected List<T> data;
	protected LayoutInflater inflater;
	protected int layout;

	public OmnipotentBaseAdapter(Context context, List<T> data, int layout) {
		this.context = context;
		this.data = data;
		this.layout = layout;
		inflater = LayoutInflater.from(context);
	}
	public void setFlushData(List<T> data){
		this.data=data;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListViewHolder viewHolder = ListViewHolder.get(context, convertView, parent, layout, position);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();
	}

	/**
	 * 转换器 实现此接口后在此方法下将数据与View绑定
	 * @param viewHolder
	 * @param t
     */
	public abstract void convert(ListViewHolder viewHolder, T t);

}
