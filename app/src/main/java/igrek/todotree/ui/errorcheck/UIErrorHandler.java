package igrek.todotree.ui.errorcheck;


import javax.inject.Inject;

import igrek.todotree.dagger.DaggerIoc;
import igrek.todotree.exceptions.DatabaseLockedException;
import igrek.todotree.info.logger.Logger;
import igrek.todotree.service.resources.UserInfoService;

public class UIErrorHandler {
	
	@Inject
	UserInfoService userInfoService;
	
	@Inject
	Logger logger;
	
	private void _handleError(Throwable t) {
		DaggerIoc.factoryComponent.inject(this);
		// database locked
		if (t instanceof DatabaseLockedException) {
			logger.warn(t.getMessage());
			userInfoService.showInfo(t.getMessage());
			return;
		}
		logger.error(t);
		userInfoService.showInfo("Error occurred: " + t.getMessage());
	}
	
	public static void showError(Throwable t) {
		new UIErrorHandler()._handleError(t);
	}
	
}
