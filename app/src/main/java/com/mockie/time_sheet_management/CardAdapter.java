package com.mockie.time_sheet_management;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CardAdapter extends ArrayAdapter<CardItem> {
    private Context context;
    private List<CardItem> cardItems;
    private List<CardItem> originalData; // Original data before filtering

    public CardAdapter(Context context, List<CardItem> cardItems, List<CardItem> originalData) {
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
        final CardItem cardItem = cardItems.get(position);

        // Set the card item data to the views
        TextView projectTextView = convertView.findViewById(R.id._project);
        TextView taskNameTextView = convertView.findViewById(R.id._taskName);
        TextView taskAssignTextView = convertView.findViewById(R.id._taskAssign);
        TextView fromTextView = convertView.findViewById(R.id._from);
        TextView toTextView = convertView.findViewById(R.id._to);
        TextView statusTextView = convertView.findViewById(R.id._status);
        ImageView deleteImageView = convertView.findViewById(R.id._delete);
        ImageView editImageView = convertView.findViewById(R.id._edit);

        projectTextView.setText(cardItem.getProjectName());
        taskNameTextView.setText(cardItem.getTaskName());
        taskAssignTextView.setText(cardItem.getAssignee());
        fromTextView.setText(cardItem.getStartDate());
        toTextView.setText(cardItem.getEndDate());
        statusTextView.setText(cardItem.getStatus());

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform delete operation here
                deleteCardItem(cardItem);
            }
        });

        editImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform edit operation here
                editCardItem(cardItem);
            }
        });

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

    // Method to delete a card item
    private void deleteCardItem(CardItem cardItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Project");
        Query query = databaseReference.orderByChild("nameproject").equalTo(cardItem.getProjectName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Delete operation successful
                                    Toast.makeText(context, "Card item deleted", Toast.LENGTH_SHORT).show();
                                    Log.d("DeleteCardItem", "Card item deleted: " + cardItem.getProjectName());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // An error occurred while deleting the card item
                                    Toast.makeText(context, "Failed to delete card item", Toast.LENGTH_SHORT).show();
                                    Log.e("DeleteCardItem", "Failed to delete card item: " + cardItem.getProjectName(), e);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
                Toast.makeText(context, "Failed to delete card item", Toast.LENGTH_SHORT).show();
                Log.e("DeleteCardItem", "Failed to delete card item: " + cardItem.getProjectName(), databaseError.toException());
            }
        });
    }

    // Method to edit a card item
    // Method to edit a card item


        private void editCardItem(CardItem cardItem) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.popup_edit_card_item, null);
            dialogBuilder.setView(dialogView);

            // Find the views inside the dialog
            EditText editTextProject = dialogView.findViewById(R.id.editTextProject);
            EditText editTextTask = dialogView.findViewById(R.id.editTextTask);
            TextView editTextDateFrom = dialogView.findViewById(R.id.editTextDateFrom);
            TextView editTextDateTo = dialogView.findViewById(R.id.editTextDateTo);
            Spinner spinnerStatus = dialogView.findViewById(R.id.spinnerStatus);
            EditText editTextAssignTo = dialogView.findViewById(R.id.editTextAssignTo);
            Button saveButton = dialogView.findViewById(R.id.save);
            Button closeButton = dialogView.findViewById(R.id.close);
            LinearLayout ly_from = dialogView.findViewById(R.id.lyFrom);
            LinearLayout ly_to = dialogView.findViewById(R.id.lyTo);
            ly_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker(editTextDateFrom);
                }
            });

            ly_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePicker(editTextDateTo);
                }
            });

            // Set the initial values for the views
            editTextProject.setText(cardItem.getProjectName());
            editTextTask.setText(cardItem.getTaskName());
            editTextDateFrom.setText(cardItem.getStartDate());
            editTextDateTo.setText(cardItem.getEndDate());
            editTextAssignTo.setText(cardItem.getAssignee());
            String[] statusOptions = {"Closed", "Open", "In Progress"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statusOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(adapter);

            AlertDialog dialog = dialogBuilder.create();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String projectName = editTextProject.getText().toString();
                    String taskName = editTextTask.getText().toString();
                    String dateFrom = editTextDateFrom.getText().toString();
                    String dateTo = editTextDateTo.getText().toString();
                    String status = spinnerStatus.getSelectedItem().toString();
                    String assignTo = editTextAssignTo.getText().toString();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context); // Replace 'context' with your actual context
                    String storedProjectKey = preferences.getString("projectKey", "");


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Project");
                    Query query = databaseReference.orderByKey().equalTo(storedProjectKey); // Replace uniqueId with the actual unique ID of the project
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean projectFound = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                projectFound = true;
                                // Update the fields of the card item
                                snapshot.getRef().child("nameproject").setValue(projectName);
                                snapshot.getRef().child("taskName").setValue(taskName);
                                snapshot.getRef().child("assignto").setValue(assignTo);
                                snapshot.getRef().child("datefrom").setValue(dateFrom);
                                snapshot.getRef().child("dateto").setValue(dateTo);
                                snapshot.getRef().child("status").setValue(status)
                                        .addOnSuccessListener(aVoid -> {
                                            // Edit operation successful
                                            Toast.makeText(context, "Card item edited", Toast.LENGTH_SHORT).show();
                                            Log.d("EditCardItem", "Card item edited: ");
                                        })
                                        .addOnFailureListener(e -> {
                                            // An error occurred while editing the card item
                                            Toast.makeText(context, "Failed to edit card item", Toast.LENGTH_SHORT).show();
                                            Log.e("EditCardItem", "Failed to edit card item: ", e);
                                        });
                            }
                            if (!projectFound) {
                                // Project not found in the database
                                Toast.makeText(context, "Project not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Error occurred while accessing the database
                            Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("EditCardItem", "Database error: " + databaseError.getMessage());
                        }
                    });

                    dialog.dismiss();

                }

            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the close operation here
                    // ...

                    // Dismiss the dialog when the close button is clicked
                    dialog.dismiss();
                }
            });

            // Show the dialog
            dialog.show();
        }

    private void showDatePicker(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        textView.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }
    }


        // Create an
