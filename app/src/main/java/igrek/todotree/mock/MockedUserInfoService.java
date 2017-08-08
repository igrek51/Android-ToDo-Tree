package igrek.todotree.mock;


import android.app.Activity;

import igrek.todotree.logger.Logs;
import igrek.todotree.services.resources.InfoBarClickAction;
import igrek.todotree.services.resources.UserInfoService;
import igrek.todotree.ui.GUI;

public class MockedUserInfoService extends UserInfoService {
	
	public MockedUserInfoService(Activity activity, GUI gui) {
		super(activity, gui);
	}
	
	@Override
	public void showInfo(String info) {
		Logs.info("info: " + info);
	}
	
	@Override
	public void showInfoCancellable(String info, InfoBarClickAction cancelCallback) {
		Logs.info("cancellable info: " + info);
	}
}
