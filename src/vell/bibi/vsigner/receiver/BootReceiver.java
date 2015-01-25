package vell.bibi.vsigner.receiver;

import vell.bibi.vsigner.service.TimerService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, TimerService.class)); // 开机启动 TimerService
//		context.startService(new Intent(context, RealTimeDataService.class)); // 开机启动 RealTimeDataService
	}

}
