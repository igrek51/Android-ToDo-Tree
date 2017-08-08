package igrek.todotree.dagger;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import igrek.todotree.app.App;
import igrek.todotree.app.AppData;
import igrek.todotree.services.backup.BackupManager;
import igrek.todotree.services.clipboard.SystemClipboardManager;
import igrek.todotree.services.clipboard.TreeClipboardManager;
import igrek.todotree.services.filesystem.FilesystemService;
import igrek.todotree.services.history.ChangesHistory;
import igrek.todotree.services.lock.DatabaseLock;
import igrek.todotree.services.preferences.Preferences;
import igrek.todotree.services.resources.UserInfoService;
import igrek.todotree.services.tree.ContentTrimmer;
import igrek.todotree.services.tree.TreeManager;
import igrek.todotree.services.tree.TreeMover;
import igrek.todotree.services.tree.TreeScrollCache;
import igrek.todotree.services.tree.TreeSelectionManager;
import igrek.todotree.services.tree.serializer.JsonTreeSerializer;
import igrek.todotree.ui.GUI;

@Module
public class AppFactoryModule {
	
	protected App app;
	protected Activity activity;
	
	public AppFactoryModule(App app, Activity activity) {
		this.app = app;
		this.activity = activity;
	}
	
	@Provides
	@Singleton
	protected App provideApp() {
		return app;
	}
	
	@Provides
	@Singleton
	protected Activity provideActivity() {
		return activity;
	}
	
	@Provides
	@Singleton
	protected AppCompatActivity provideAppCompatActivity() {
		return (AppCompatActivity) activity;
	}
	
	@Provides
	@Singleton
	protected FilesystemService provideFilesystemService(Activity activity) {
		return new FilesystemService(activity);
	}
	
	@Provides
	@Singleton
	protected TreeManager provideTreeManager(ChangesHistory changesHistory) {
		return new TreeManager(changesHistory);
	}
	
	@Provides
	@Singleton
	protected Preferences providePreferences(Activity activity) {
		return new Preferences(activity);
	}
	
	@Provides
	@Singleton
	protected BackupManager provideBackupManager(Preferences preferences, FilesystemService filesystem) {
		return new BackupManager(preferences, filesystem);
	}
	
	@Provides
	@Singleton
	protected UserInfoService provideUserInfoService(Activity activity, GUI gui) {
		return new UserInfoService(activity, gui);
	}
	
	@Provides
	@Singleton
	protected JsonTreeSerializer provideTreeSerializer() {
		return new JsonTreeSerializer();
	}
	
	@Provides
	@Singleton
	protected GUI provideGUI(AppCompatActivity activity) {
		return new GUI(activity);
	}
	
	@Provides
	@Singleton
	protected SystemClipboardManager provideSystemClipboardManager(Activity activity) {
		return new SystemClipboardManager(activity);
	}
	
	@Provides
	@Singleton
	protected AppData provideAppData() {
		return new AppData();
	}
	
	@Provides
	@Singleton
	protected DatabaseLock provideDatabaseLock() {
		return new DatabaseLock();
	}
	
	@Provides
	@Singleton
	protected ChangesHistory provideChangesHistory() {
		return new ChangesHistory();
	}
	
	@Provides
	@Singleton
	protected ContentTrimmer provideContentTrimmer() {
		return new ContentTrimmer();
	}
	
	@Provides
	@Singleton
	protected TreeScrollCache provideTreeScrollCache() {
		return new TreeScrollCache();
	}
	
	@Provides
	@Singleton
	protected TreeClipboardManager provideTreeClipboardManager() {
		return new TreeClipboardManager();
	}
	
	@Provides
	@Singleton
	protected TreeSelectionManager provideTreeSelectionManager() {
		return new TreeSelectionManager();
	}
	
	@Provides
	@Singleton
	protected TreeMover provideTreeMover() {
		return new TreeMover();
	}
	
}
