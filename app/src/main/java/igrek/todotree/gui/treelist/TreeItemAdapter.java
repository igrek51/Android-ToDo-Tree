package igrek.todotree.gui.treelist;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import igrek.todotree.R;
import igrek.todotree.gui.GUIListener;
import igrek.todotree.logic.datatree.TreeItem;

public class TreeItemAdapter extends ArrayAdapter<TreeItem> {

    Context context;
    List<TreeItem> dataSource;
    List<Integer> selections = null;
    GUIListener guiListener;
    TreeListView listView;

    HashMap<Integer, View> storedViews;

    View convertView = null;
    ViewGroup parent = null;

    public TreeItemAdapter(Context context, List<TreeItem> dataSource, GUIListener guiListener, TreeListView listView) {
        super(context, 0, new ArrayList<TreeItem>());
        this.context = context;
        if (dataSource == null) dataSource = new ArrayList<>();
        this.dataSource = dataSource;
        this.guiListener = guiListener;
        this.listView = listView;
        storedViews = new HashMap<>();
    }

    public void setDataSource(List<TreeItem> dataSource) {
        this.dataSource = dataSource;
        storedViews = new HashMap<>();
        notifyDataSetChanged();
    }

    public TreeItem getItem(int position) {
        return dataSource.get(position);
    }

    public void setSelections(List<Integer> selections) {
        this.selections = selections;
    }

    public View getStoredView(int position) {
        if (position >= dataSource.size()) return null;
        if (!storedViews.containsKey(position)) return null;
        return storedViews.get(position);
    }

    @Override
    public int getCount() {
        return dataSource.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0) return -1;
        if (position >= dataSource.size()) return -1;
        return (long) position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        this.convertView = convertView;
        this.parent = parent;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position == dataSource.size()) {
            //plusik
            View itemPlus = inflater.inflate(R.layout.item_plus, parent, false);

            ImageButton plusButton = (ImageButton) itemPlus.findViewById(R.id.buttonAddNewItem);
            plusButton.setFocusableInTouchMode(false);
            plusButton.setFocusable(false);
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guiListener.onAddItemClicked();
                }
            });

            return itemPlus;
        } else {
            final View itemView = inflater.inflate(R.layout.tree_item, parent, false);
            final TreeItem item = dataSource.get(position);

            //zawartość tekstowa elementu
            TextView textView = (TextView) itemView.findViewById(R.id.tvItemContent);
            StringBuilder contentBuilder = new StringBuilder(item.getContent());
            if (!item.isEmpty()) {
                contentBuilder.append(" [");
                contentBuilder.append(item.size());
                contentBuilder.append("]");
                textView.setTypeface(null, Typeface.BOLD);
            } else {
                textView.setTypeface(null, Typeface.NORMAL);
            }
            textView.setText(contentBuilder.toString());

            //edycja elementu
            ImageButton editButton = (ImageButton) itemView.findViewById(R.id.buttonItemEdit);
            editButton.setFocusableInTouchMode(false);
            editButton.setFocusable(false);
            if (selections == null && !item.isEmpty()) {
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guiListener.onItemEditClicked(position, item);
                    }
                });
            } else {
                editButton.setVisibility(View.GONE);
            }

            //wejście w element
            ImageButton goIntoButton = (ImageButton) itemView.findViewById(R.id.buttonItemGoInto);
            goIntoButton.setFocusableInTouchMode(false);
            goIntoButton.setFocusable(false);
            if (selections == null && item.isEmpty()) {
                goIntoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guiListener.onItemGoIntoClicked(position, item);
                    }
                });
            } else {
                goIntoButton.setVisibility(View.GONE);
            }

            //usuwanie elementu
            ImageButton removeButton = (ImageButton) itemView.findViewById(R.id.buttonItemRemove);
            removeButton.setFocusableInTouchMode(false);
            removeButton.setFocusable(false);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guiListener.onItemRemoveClicked(position, item);
                }
            });

            //przesuwanie
            final ImageButton moveButton = (ImageButton) itemView.findViewById(R.id.buttonItemMove);
            moveButton.setFocusableInTouchMode(false);
            moveButton.setFocusable(false);
            if (selections == null) {
                moveButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                listView.onItemMoveButtonPressed(position, item, itemView, event.getX(), event.getY() + moveButton.getTop());
                                return false;
                            case MotionEvent.ACTION_UP:
                                listView.onItemMoveButtonReleased(position, item, itemView, event.getX(), event.getY() + moveButton.getTop());
                                return true;
                        }
                        return false;
                    }
                });
                moveButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return listView.onItemMoveLongPressed(position, item);
                    }
                });
            } else {
                moveButton.setVisibility(View.INVISIBLE);
                moveButton.setLayoutParams(new RelativeLayout.LayoutParams(0, 0));
            }

            //dodawanie nowego elementu
            ImageButton addButton = (ImageButton) itemView.findViewById(R.id.buttonItemAddHere);
            addButton.setFocusableInTouchMode(false);
            addButton.setFocusable(false);
            if (selections == null) {
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        guiListener.onAddItemClicked(position);
                    }
                });
            } else {
                addButton.setVisibility(View.GONE);
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
                cbItemSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        guiListener.onSelectedClicked(position, item, isChecked);
                    }
                });
            }

            //zapisanie rozmiaru widoku
            storedViews.put(position, itemView);

            return itemView;
        }
    }

}