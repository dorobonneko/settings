package com.moe.settings;
import android.service.quicksettings.TileService;
import android.content.ComponentName;
import android.provider.Settings;
import android.service.quicksettings.Tile;

public class PerformanceTile extends TileService {

	@Override
	public void onCreate() {
		super.onCreate();
		Tile tile=getQsTile();
		if(tile!=null){
			tile.setLabel("性能模式");
			tile.updateTile();
		}
	}

	@Override
	public void onTileAdded() {
		super.onTileAdded();
		onStartListening();
	requestListeningState(this,new ComponentName(this,PerformanceTile.class));
	}

	@Override
	public void onStartListening() {
		super.onStartListening();
		int state=Settings.System.getInt(getContentResolver(),"performance_mode_enable",0);
		Tile tile=getQsTile();
		if(tile!=null){
			tile.setState(state==1?Tile.STATE_ACTIVE:Tile.STATE_INACTIVE);
			tile.updateTile();
		}
	}

	@Override
	public void onStopListening() {
		super.onStopListening();
		requestListeningState(this,new ComponentName(this,PerformanceTile.class));
		
	}

	@Override
	public void onTileRemoved() {
		super.onTileRemoved();
		Tile tile=getQsTile();
		if(tile!=null){
			tile.setState(Tile.STATE_UNAVAILABLE);
			tile.updateTile();
		}
	}

	@Override
	public void onClick() {
		super.onClick();
		int state=Settings.System.getInt(getContentResolver(),"performance_mode_enable",0);
		//Settings.System.putInt(getContentResolver(),"high_performance_mode_on",state==1?0:1);
		Settings.System.putInt(getContentResolver(),"performance_mode_enable",state==1?0:1);
		onStartListening();
	}
	
}
