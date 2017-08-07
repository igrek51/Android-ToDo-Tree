package igrek.todotree.dagger;


import javax.inject.Singleton;

import dagger.Component;
import igrek.todotree.controller.ClipboardController;
import igrek.todotree.controller.ExitController;
import igrek.todotree.controller.GUIController;
import igrek.todotree.controller.ItemActionController;
import igrek.todotree.controller.ItemEditorController;
import igrek.todotree.controller.ItemSelectionController;
import igrek.todotree.controller.ItemTrashController;
import igrek.todotree.controller.MainController;
import igrek.todotree.controller.PersistenceController;
import igrek.todotree.controller.TreeController;

@Singleton
@Component(modules = {TestFactoryModule.class})
public interface TestFactoryComponent extends AppFactoryComponent {
	
	void inject(IDaggerInjectionTest there);
	
	// Controllers
	
	void inject(MainController there);
	
	void inject(ExitController there);
	
	void inject(PersistenceController there);
	
	void inject(ClipboardController there);
	
	void inject(GUIController there);
	
	void inject(TreeController there);
	
	void inject(ItemEditorController there);
	
	void inject(ItemTrashController there);
	
	void inject(ItemSelectionController there);
	
	void inject(ItemActionController there);
	
}