package igrek.todotree.gui.treelist;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import igrek.todotree.R;
import igrek.todotree.gui.GUIListener;
import igrek.todotree.logic.datatree.TreeItem;

public class TreeItemAdapter extends ArrayAdapter<TreeItem> {

    Context context;
    List<TreeItem> dataSource;
    GUIListener guiListener;

    public TreeItemAdapter(Context context, List<TreeItem> dataSource, GUIListener guiListener) {
        super(context, 0, new ArrayList<TreeItem>());
        this.context = context;
        if(dataSource == null) dataSource = new ArrayList<>();
        this.dataSource = dataSource;
        this.guiListener = guiListener;
    }

    public void setDataSource(List<TreeItem> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    public TreeItem getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getCount() {
        return dataSource.size() + 1;
    }

    @Override
    public long getItemId(int position){
        if(position < 0) return -1;
        if(position >= dataSource.size()) return -1;
        return (long) position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position == dataSource.size()) {
            //plusik
            View itemPlus = inflater.inflate(R.layout.item_plus, parent, false);

            ImageButton plusButton = (ImageButton) itemPlus.findViewById(R.id.button_add);
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

            TextView textView = (TextView) itemView.findViewById(R.id.firstLine);
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
            ImageButton editButton = (ImageButton) itemView.findViewById(R.id.button_edit);

            editButton.setFocusableInTouchMode(false);
            editButton.setFocusable(false);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guiListener.onItemEditClicked(position, item);
                }
            });

            //usuwanie elementu
            ImageButton removeButton = (ImageButton) itemView.findViewById(R.id.button_remove);

            removeButton.setFocusableInTouchMode(false);
            removeButton.setFocusable(false);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guiListener.onItemRemoveClicked(position, item);
                }
            });

            //przesuwanie
            ImageButton moveButton = (ImageButton) itemView.findViewById(R.id.button_move);

            moveButton.setFocusableInTouchMode(false);
            moveButton.setFocusable(false);

            moveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //guiListener.onItemMoveClicked(position, item);
                }
            });

            moveButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            guiListener.onItemMoveButtonPressed(position, item, itemView);
                            break;
                        case MotionEvent.ACTION_UP:
                            guiListener.onItemMoveButtonReleased(position, item, itemView);
                            break;
                    }
                    return false;
                }
            });

            return itemView;
        }
    }

}