package igrek.todotree.dagger;


import android.app.Activity;

import igrek.todotree.logic.app.App;

public class DaggerIOC {
	
	private static AppFactoryComponent appComponent;
	
	private DaggerIOC() {}
	
	public static AppFactoryComponent getAppComponent() {
		return appComponent;
	}
	
	public static void init(App app, Activity activity) {
		appComponent = DaggerAppFactoryComponent.builder()
				.appFactoryModule(new AppFactoryModule(app, activity))
				.build();
	}
}