package igrek.todotree.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseGUI {
	
	protected AppCompatActivity activity;
	
	private InputMethodManager imm;
	
	RelativeLayout mainContent;
	
	BaseGUI(AppCompatActivity activity) {
		this.activity = activity;
		imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	public RelativeLayout getMainContent() {
		return mainContent;
	}
	
	public View setMainContentLayout(int layoutResource) {
		mainContent.removeAllViews();
		LayoutInflater inflater = activity.getLayoutInflater();
		View layout = inflater.inflate(layoutResource, null);
		layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mainContent.addView(layout);
		return layout;
	}
	
	
	public void hideSoftKeyboard(View window) {
		if (imm != null) {
			imm.hideSoftInputFromWindow(window.getWindowToken(), 0);
		}
	}
	
	public void showSoftKeyboard(View window) {
		if (imm != null) {
			imm.showSoftInput(window, 0);
		}
	}
	
}
