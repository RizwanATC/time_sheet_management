package com.mockie.time_sheet_management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends ArrayAdapter<CardItem> {
    private Context context;
    private List<CardItem> cardItems;
    private List<CardItem> originalData; // Original data before filtering

    public CardAdapter(Context context, List<CardItem> cardItems) {
        super(context, 0, cardItems);
        this.context = context;
        this.cardItems = cardItems;
        this.originalData = new ArrayList<>(cardItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the card item layout if necessary
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.card_listview_item, parent, false);
        }

        // Get the current card item
        CardItem cardItem = cardItems.get(position);

        // Set the card item data to the views
        TextView projectTextView = convertView.findViewById(R.id._project);
        TextView taskNameTextView = convertView.findViewById(R.id._taskName);
        TextView taskAssignTextView = convertView.findViewById(R.id._taskAssign);
        TextView fromTextView = convertView.findViewById(R.id._from);
        TextView toTextView = convertView.findViewById(R.id._to);
        TextView statusTextView = convertView.findViewById(R.id._status);

        projectTextView.setText(cardItem.getProject());
        taskNameTextView.setText(cardItem.getTask());
        taskAssignTextView.setText(cardItem.getAssignedTo());
        fromTextView.setText(cardItem.getFrom());
        toTextView.setText(cardItem.getTo());
        statusTextView.setText(cardItem.getStatus());

        return convertView;
    }

    @Override
    public int getCount() {
        return cardItems.size();
    }

    @Override
    public CardItem getItem(int position) {
        return cardItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Method to update adapter data with search results
    public void updateData(List<CardItem> searchResults) {
        cardItems.clear();
        cardItems.addAll(searchResults);
        notifyDataSetChanged();
    }

    // Method to reset adapter data to the original data
    public void resetData() {
        cardItems.clear();
        cardItems.addAll(originalData);
        notifyDataSetChanged();
    }
}

