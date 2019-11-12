package igrek.todotree.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import java.util.List;
import java.util.Set;

import igrek.todotree.R;
import igrek.todotree.commands.ExitCommand;
import igrek.todotree.commands.NavigationCommand;
import igrek.todotree.domain.treeitem.AbstractTreeItem;
import igrek.todotree.ui.edititem.EditItemGUI;
import igrek.todotree.ui.errorcheck.SafeClickListener;
import igrek.todotree.ui.treelist.TreeListView;

public class GUI extends BaseGUI {
	
	private ActionBar actionBar;
	private TreeListView itemsListView;
	private EditItemGUI editItemGUI;
	
	public GUI(AppCompatActivity activity) {
		super(activity);
	}
	
	public void lazyInit() {
		activity.setContentView(R.layout.activity_main);
		
		//toolbar
		Toolbar toolbar1 = activity.findViewById(R.id.toolbar1);
		activity.setSupportActionBar(toolbar1);
		actionBar = activity.getSupportActionBar();
		showBackButton(true);
		toolbar1.setNavigationOnClickListener(new SafeClickListener() {
			@Override
			public void onClick() {
				new NavigationCommand().backClicked();
			}
		});
		
		ImageButton save2Button = activity.findViewById(R.id.save2Button);
		save2Button.setOnClickListener(v -> {
			new ExitCommand().optionSaveAndExit();
		});
		
		mainContent = activity.findViewById(R.id.mainContent);
	}
	
	private void showBackButton(boolean show) {
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(show);
			actionBar.setDisplayShowHomeEnabled(show);
		}
	}
	
	public void showItemsList(final AbstractTreeItem currentItem) {
		setOrientationPortrait();
		
		View itemsListLayout = setMainContentLayout(R.layout.items_list);
		
		itemsListView = itemsListLayout.findViewById(R.id.treeItemsList);
		itemsListView.init(activity);
		updateItemsList(currentItem, currentItem.getChildren(), null);
	}
	
	public void showEditItemPanel(final AbstractTreeItem item, AbstractTreeItem parent) {
		showBackButton(true);
		// TODO redirect to dedicated views
		editItemGUI = new EditItemGUI(this, item, parent);
	}
	
	public View showExitScreen() {
		return setMainContentLayout(R.layout.exit_screen);
	}
	
	public void updateItemsList(AbstractTreeItem currentItem, List<AbstractTreeItem> items, Set<Integer> selectedPositions) {
		if (items == null)
			items = currentItem.getChildren();
		
		//tytuł gałęzi
		StringBuilder sb = new StringBuilder(currentItem.getDisplayName());
		if (!currentItem.isEmpty()) {
			sb.append(" [");
			sb.append(currentItem.size());
			sb.append("]");
		}
		setTitle(sb.toString());
		
		// back button visiblity
		showBackButton(currentItem.getParent() != null);
		
		//lista elementów
		itemsListView.setItemsAndSelected(items, selectedPositions);
	}
	
	public void scrollToItem(int itemIndex) {
		itemsListView.scrollToItem(itemIndex);
	}
	
	public void scrollToItem(Integer y, int itemIndex) {
		if (y != null)
			itemsListView.scrollToPosition(y);
		itemsListView.scrollToItem(itemIndex);
	}
	
	public void scrollToPosition(int y) {
		itemsListView.scrollToPosition(y);
	}
	
	public void scrollToBottom() {
		itemsListView.scrollToBottom();
	}
	
	public void hideSoftKeyboard() {
		editItemGUI.hideKeyboards();
	}
	
	public boolean editItemBackClicked() {
		return editItemGUI.editItemBackClicked();
	}
	
	public void setTitle(String title) {
		actionBar.setTitle(title);
	}
	
	public Integer getCurrentScrollPos() {
		return itemsListView.getCurrentScrollPosition();
	}
	
	public void requestSaveEditedItem() {
		editItemGUI.requestSaveEditedItem();
	}
	
	public void rotateScreen() {
		int orientation = activity.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
	}
	
	private void setOrientationPortrait() {
		int orientation = activity.getResources().getConfiguration().orientation;
		if (orientation != Configuration.ORIENTATION_PORTRAIT) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	public void quickInsertRange() {
		if (editItemGUI != null) {
			editItemGUI.quickInsertRange();
		}
	}
	
	public void forceKeyboardShow() {
		editItemGUI.forceKeyboardShow();
	}
}
