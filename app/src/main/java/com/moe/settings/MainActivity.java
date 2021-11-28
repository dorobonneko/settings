package com.moe.settings;
 
import android.app.Activity;
import android.os.Bundle;
import android.app.ListActivity;
import android.net.Uri;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.content.ContentValues;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.Adapter;
import java.util.Stack;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;

public class MainActivity extends ListActivity implements SearchView.OnQueryTextListener,SearchView.OnCloseListener,ListView.OnItemLongClickListener{ 
private List<String[]> data=new ArrayList<>();
private SettingAdapter sa;
private String mode;
private SearchView sv;
private MenuItem searchMenu,reply;
private Stack<String[]> delete=new Stack<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		getListView().setDivider(new ColorDrawable());
		getListView().setDividerHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16,getResources().getDisplayMetrics()));
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){

				@Override
				public void uncaughtException(Thread p1, Throwable p2) {
					ArrayList al=new ArrayList();
					al.add(p2.getMessage());
					for(StackTraceElement e:p2.getStackTrace()){
						al.add(e.toString());
					}
					startActivity(new Intent(getApplicationContext(),MainActivity.class).setAction("Crash").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("crash",al));
					Runtime.getRuntime().exit(1);
				}
			});
		if("Crash".equals(getIntent().getAction())){
			setListAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,getIntent().getParcelableArrayListExtra("crash")));
			return;
		}
        sa=new SettingAdapter(data);
		setListAdapter(sa);
		getListView().setOnItemLongClickListener(this);
		change("system");
    }
	private void change(String mode){
		if(mode==null)return;
		this.mode=mode;
		setTitle(mode);
		Cursor cursor=getContentResolver().query(Uri.parse("content://settings/"+mode),null,null,null,null);
		data.clear();
		while(cursor.moveToNext()){
			data.add(new String[]{cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("value"))});
		}
		sa.getFilter().filter(sv==null?null:sv.getQuery());
		if(reply!=null)
		reply.setEnabled(!delete.isEmpty());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if("Crash".equals(getIntent().getAction()))
			return false;
		getMenuInflater().inflate(R.menu.menu,menu);
		sv=(SearchView) (searchMenu=menu.findItem(R.id.filter)).getActionView();
		sv.setIconified(true);
		sv.setOnQueryTextListener(this);
		sv.setOnCloseListener(this);
		reply=menu.findItem(R.id.reply);
		reply.setEnabled(!delete.isEmpty());
	
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.global:
				change("global");
				break;
			case R.id.system:
				change("system");
				break;
		    case R.id.secure:
				change("secure");
				break;
		    case R.id.test:
				//startActivity(new Intent(this,AutoBootActivity.class));
				break;
		    case R.id.insert:
				View v=LayoutInflater.from(this).inflate(R.layout.insert_view,null);
				final EditText name=v.findViewById(R.id.name);
				final EditText value=v.findViewById(R.id.value);
				new AlertDialog.Builder(this).setTitle("插入数据").setView(v).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2) {
							String key=name.getText().toString().trim();
							if(TextUtils.isEmpty(key)){
								Toast.makeText(getApplicationContext(),"名称不能为空",Toast.LENGTH_SHORT).show();
								return;
							}
							ContentValues cv=new ContentValues();
							cv.put("name",key);
							String val=value.getText().toString().trim();
							if(!TextUtils.isEmpty(val))
								cv.put("value",val);
							getContentResolver().insert(Uri.parse("content://settings/"+mode),cv);
							//item[1]=cv.getAsString("value");
							change(mode);
						}
					}).show();
				break;
				case R.id.reply:
					//撤销删除
					String[] k_v=delete.pop();
				ContentValues cv=new ContentValues();
				cv.put("name",k_v[0]);
				cv.put("value",k_v[1]);
				getContentResolver().insert(Uri.parse("content://settings/"+mode),cv);
				change(mode);
				Toast.makeText(this,"已恢复："+k_v[0],Toast.LENGTH_SHORT).show();
					break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if(mode==null)return;
		final String[] item=(String[]) l.getAdapter().getItem(position);
		final EditText value=new EditText(this);
		value.setText(item[1]);
		new AlertDialog.Builder(this).setTitle(item[0]).setView(value).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					ContentValues cv=new ContentValues();
					cv.put("name",item[0]);
					String val=value.getText().toString().trim();
					if(!TextUtils.isEmpty(val))
					cv.put("value",val);
					getContentResolver().insert(Uri.parse("content://settings/"+mode),cv);
					item[1]=cv.getAsString("value");
					sa.notifyDataSetInvalidated();
				}
			}).show();
		
	}

	@Override
	public boolean onQueryTextChange(String p1) {
		if(sa!=null)
			sa.getFilter().filter(p1);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String p1) {
		return false;
	}

	@Override
	public boolean onClose() {
		if(sa!=null)
			sa.getFilter().filter(null);
			sv.onActionViewCollapsed();
		return true;
	}

	@Override
	public void finish() {
		moveTaskToBack(true);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> p1, View p2, int position, long p4) {
		if(mode==null)return true;
		final String[] item=(String[]) p1.getAdapter().getItem(position);
		
		new AlertDialog.Builder(this).setTitle("删除").setMessage(item[0]).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface p1, int p2) {
					getContentResolver().delete(Uri.parse("content://settings/"+mode),"name=?",new String[]{item[0]});
					delete.push(item);
					change(mode);
					}}).show();
		return true;
	}




	
} 
