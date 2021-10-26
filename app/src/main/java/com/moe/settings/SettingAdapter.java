package com.moe.settings;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import android.widget.Filterable;
import android.widget.Filter;
import android.view.LayoutInflater;
import android.widget.TextView;
import java.util.ArrayList;
import android.text.TextUtils;
import java.util.Iterator;

public class SettingAdapter extends BaseAdapter implements Filterable{
	private List<String[]> data,filter;
	private Filter mFilter;
	public SettingAdapter(List<String[]> list){
		data=list;
		filter=new ArrayList<>();
		filter.addAll(data);
	}
	
	@Override
	public int getCount() {
		return filter.size();
	}

	@Override
	public Object getItem(int p1) {
		return filter.get(p1);
	}

	@Override
	public long getItemId(int p1) {
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3) {
		if(p2==null)
			p2=LayoutInflater.from(p3.getContext()).inflate(android.R.layout.simple_list_item_2,p3,false);
			String[] item=filter.get(p1);
			((TextView)p2.findViewById(android.R.id.text1)).setText(item[0]);
		((TextView)p2.findViewById(android.R.id.text2)).setText(item[1]);
		
		return p2;
	}

	@Override
	public Filter getFilter() {
		if(mFilter==null)
			mFilter=new MFilter();
		return mFilter;
	}

	class MFilter extends Filter {

		@Override
		protected Filter.FilterResults performFiltering(CharSequence p1) {
			FilterResults fr=new FilterResults();
			ArrayList al=new ArrayList();
			fr.values=al;
			if(TextUtils.isEmpty(p1))
				al.addAll(data);
				else{
					Iterator<String[]> i= data.iterator();
					while(i.hasNext()){
						String[] item=i.next();
						if(item[0].contains(p1)||(item[1]!=null&&item[1].contains(p1)))
							al.add(item);
					}
				}
			fr.count=al.size();
			return fr;
		}

		@Override
		protected void publishResults(CharSequence p1, Filter.FilterResults p2) {
			if(p2.values==null)
				performFiltering(p1);
			filter.clear();
			filter.addAll((List)p2.values);
			notifyDataSetChanged();
		}
		
		
	}
    
}
