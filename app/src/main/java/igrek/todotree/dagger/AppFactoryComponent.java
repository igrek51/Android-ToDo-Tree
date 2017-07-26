package igrek.todotree.dagger;


import javax.inject.Singleton;

import dagger.Component;
import igrek.todotree.gui.treelist.TreeListView;
import igrek.todotree.logic.app.App;

@Singleton
@Component(modules = {AppFactoryModule.class})
public interface AppFactoryComponent {
	
	void inject(IDaggerInjectionTest there);
	
	void inject(App there);
	
	void inject(TreeListView there);
	
}