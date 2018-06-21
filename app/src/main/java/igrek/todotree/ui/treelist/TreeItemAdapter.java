package igrek.todotree.ui.treelist;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import igrek.todotree.R;
import igrek.todotree.commands.ItemEditorCommand;
import igrek.todotree.commands.ItemSelectionCommand;
import igrek.todotree.model.treeitem.AbstractTreeItem;
import igrek.todotree.model.treeitem.LinkTreeItem;
import igrek.todotree.ui.errorcheck.SafeClickListener;

class TreeItemAdapter extends ArrayAdapter<AbstractTreeItem> {
	
	private Context context;
	private List<AbstractTreeItem> dataSource;
	private Set<Integer> selections = null; // selected indexes
	private TreeListView listView;
	private HashMap<Integer, View> storedViews;
	
	TreeItemAdapter(Context context, List<AbstractTreeItem> dataSource, TreeListView listView) {
		super(context, 0, new ArrayList<>());
		this.context = context;
		if (dataSource == null)
			dataSource = new ArrayList<>();
		this.dataSource = dataSource;
		this.listView = listView;
		storedViews = new HashMap<>();
	}
	
	void setDataSource(List<AbstractTreeItem> dataSource) {
		this.dataSource = dataSource;
		storedViews = new HashMap<>();
		notifyDataSetChanged();
	}
	
	public AbstractTreeItem getItem(int position) {
		return dataSource.get(position);
	}
	
	public List<AbstractTreeItem> getItems() {
		return dataSource;
	}
	
	void setSelections(Set<Integer> selections) {
		this.selections = selections;
	}
	
	View getStoredView(int position) {
		if (position >= dataSource.size())
			return null;
		if (!storedViews.containsKey(position))
			return null;
		return storedViews.get(position);
	}
	
	@Override
	public int getCount() {
		return dataSource.size() + 1;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	@Override
	public long getItemId(int position) {
		if (position < 0)
			return -1;
		if (position >= dataSource.size())
			return -1;
		return (long) position;
	}
	
	@NonNull
	@Override
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (position == dataSource.size()) {
			return getAddItemView(position, parent, inflater);
		} else {
			return getItemView(position, parent, inflater);
		}
	}
	
	@NonNull
	private View getItemView(int position, @NonNull ViewGroup parent, LayoutInflater inflater) {
		final View itemView = inflater.inflate(R.layout.tree_item, parent, false);
		final AbstractTreeItem item = dataSource.get(position);
		
		//zawartość tekstowa elementu
		TextView textView = (TextView) itemView.findViewById(R.id.tvItemContent);
		if (!item.isEmpty()) {
			textView.setTypeface(null, Typeface.BOLD);
		} else {
			textView.setTypeface(null, Typeface.NORMAL);
		}
		textView.setText(item.getDisplayName());
		if (item instanceof LinkTreeItem) {
			SpannableString content = new SpannableString(item.getDisplayName());
			content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
			textView.setText(content);
		}
		
		// ilość potomków
		TextView tvItemChildSize = (TextView) itemView.findViewById(R.id.tvItemChildSize);
		if (!item.isEmpty()) {
			String contentBuilder = "[" + item.size() + "]";
			tvItemChildSize.setText(contentBuilder);
		} else {
			tvItemChildSize.setText("");
		}
		
		//edycja elementu
		ImageButton editButton = (ImageButton) itemView.findViewById(R.id.buttonItemEdit);
		editButton.setFocusableInTouchMode(false);
		editButton.setFocusable(false);
		if (selections == null && !item.isEmpty()) {
			editButton.setOnClickListener(new SafeClickListener() {
				@Override
				public void onClick() {
					new ItemEditorCommand().itemEditClicked(item);
				}
			});
		} else {
			editButton.setVisibility(View.GONE);
		}
		
		//przesuwanie
		final ImageButton moveButton = (ImageButton) itemView.findViewById(R.id.buttonItemMove);
		moveButton.setFocusableInTouchMode(false);
		moveButton.setFocusable(false);
		if (selections == null) {
			moveButton.setOnTouchListener((v, event) -> {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						listView.getReorder()
								.onItemMoveButtonPressed(position, item, itemView, event.getX(), event
										.getY() + moveButton.getTop());
						return false;
					case MotionEvent.ACTION_UP:
						listView.getReorder()
								.onItemMoveButtonReleased(position, item, itemView, event.getX(), event
										.getY() + moveButton.getTop());
						return true;
				}
				return false;
			});
			moveButton.setOnLongClickListener(v -> listView.getReorder()
					.onItemMoveLongPressed(position, item));
		} else {
			moveButton.setVisibility(View.INVISIBLE);
			moveButton.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
		}
		
		//checkbox do zaznaczania wielu elementów
		CheckBox cbItemSelected = (CheckBox) itemView.findViewById(R.id.cbItemSelected);
		cbItemSelected.setFocusableInTouchMode(false);
		cbItemSelected.setFocusable(false);
		
		if (selections == null) {
			cbItemSelected.setVisibility(View.GONE);
		} else {
			cbItemSelected.setVisibility(View.VISIBLE);
			if (selections.contains(position)) {
				cbItemSelected.setChecked(true);
			} else {
				cbItemSelected.setChecked(false);
			}
			cbItemSelected.setOnCheckedChangeListener((buttonView, isChecked) -> new ItemSelectionCommand()
					.selectedItemClicked(position, isChecked));
		}
		
		itemView.setOnTouchListener(new TreeItemTouchListener(listView, position));
		
		// store view
		storedViews.put(position, itemView);
		
		return itemView;
	}
	
	@NonNull
	private View getAddItemView(int position, @NonNull ViewGroup parent, LayoutInflater inflater) {
		//plusik
		View itemPlus = inflater.inflate(R.layout.item_plus, parent, false);
		
		ImageButton plusButton = (ImageButton) itemPlus.findViewById(R.id.buttonAddNewItem);
		plusButton.setFocusableInTouchMode(false);
		plusButton.setFocusable(false);
		plusButton.setOnClickListener(new SafeClickListener() {
			@Override
			public void onClick() {
				new ItemEditorCommand().addItemClicked();
			}
		});
		// redirect long click to tree list view
		plusButton.setLongClickable(true);
		plusButton.setOnLongClickListener(v -> listView.onItemLongClick(null, null, position, 0));
		
		return itemPlus;
	}
	
}