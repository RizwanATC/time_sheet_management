package com.mockie.time_sheet_management;

import android.app.DatePickerDialog;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CardAdapter cardAdapter;
    private List<CardItem> cardItems;
    private List<CardItem> originalData; // Store the original data for resetting

    private ImageButton buttonCreate;

    private ImageButton searchButton;
    private EditText searchEditText;

    FirebaseDatabase db;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        cardItems = new ArrayList<>();
        originalData = new ArrayList<>(); // Initialize the original data list
        cardAdapter = new CardAdapter(this, cardItems, originalData); // Pass the original data list to the adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Project");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cardItems.clear(); // Clear the existing cardItems list
                originalData.clear(); // Clear the existing originalData list

                // Loop through the children of the "Project" node
                for (DataSnapshot projectSnapshot : dataSnapshot.getChildren()) {
                    // Exclude the unique ID from being retrieved as the project name
                    if (projectSnapshot.getKey() != null && !projectSnapshot.getKey().isEmpty()) {
                        // Retrieve other properties of the project
                        String projectName = projectSnapshot.child("nameproject").getValue(String.class);
                        String assignTo = projectSnapshot.child("assignto").getValue(String.class);
                        String dateFrom = projectSnapshot.child("datefrom").getValue(String.class);
                        String dateTo = projectSnapshot.child("dateto").getValue(String.class);
                        String status = projectSnapshot.child("status").getValue(String.class);
                        String taskName = projectSnapshot.child("taskName").getValue(String.class);

                        CardItem newItem = new CardItem(projectName, taskName, assignTo, dateFrom, dateTo, status);

                        // Add the new item to the cardItems and originalData lists
                        cardItems.add(newItem);
                        originalData.add(newItem);

                        // Do something with the retrieved data
                        // ...
                    }
                }
                cardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                // ...
            }
        });

        listView.setAdapter(cardAdapter);

        searchEditText = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.buttonSearch);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed in this case
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().trim();
                if (searchText.isEmpty()) {
                    // If the search text is empty, reset the data to show the full list
                    cardAdapter.resetData();
                } else {
                    performSearch(searchText);
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString().trim();
                if (searchText.isEmpty()) {
                    resetData(); // Reset the adapter data to the original list
                } else {
                    performSearch(searchText);
                }
            }
        });

        buttonCreate = findViewById(R.id.buttonCreate);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
    }

    private void performSearch(String searchText) {
        List<CardItem> searchResults = new ArrayList<>();
        for (CardItem cardItem : cardItems) {
            if (cardItem.getProjectName().toLowerCase().contains(searchText.toLowerCase()) ||
                    cardItem.getTaskName().toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(cardItem);
            }
        }

        // Update the adapter with search results
        cardAdapter.updateData(searchResults);
    }

    private void showBottomSheet() {
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);

        TextView editText = bottomSheetView.findViewById(R.id.tv_time_sheet_title);
        EditText editTextProject = bottomSheetView.findViewById(R.id.editTextProject);
        EditText editTextTask = bottomSheetView.findViewById(R.id.editTextTask);
        TextView editTextDateFrom = bottomSheetView.findViewById(R.id.editTextDateFrom);
        TextView editTextDateTo = bottomSheetView.findViewById(R.id.editTextDateTo);
        Spinner sp_status = bottomSheetView.findViewById(R.id.spinnerStatus);
        EditText editTextAssignTo = bottomSheetView.findViewById(R.id.editTextAssignTo);

        Button buttonSave = bottomSheetView.findViewById(R.id.save);
        Button buttonClose = bottomSheetView.findViewById(R.id.close);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        editTextDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(editTextDateFrom);
            }
        });

        editTextDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(editTextDateTo);
            }
        });

        String[] statusOptions = {"Closed", "Open", "In Progress"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_status.setAdapter(adapter);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = editTextProject.getText().toString();
                String taskName = editTextTask.getText().toString();
                String dateFrom = editTextDateFrom.getText().toString();
                String dateTo = editTextDateTo.getText().toString();
                String status = sp_status.getSelectedItem().toString();
                String assignTo = editTextAssignTo.getText().toString();

                // Perform save operation with the retrieved values
                if (!projectName.isEmpty() && !taskName.isEmpty() && !dateFrom.isEmpty() &&
                        !dateTo.isEmpty() && !status.isEmpty() && !assignTo.isEmpty()) {
                    Project project = new Project(projectName, taskName, dateFrom, dateTo, status, assignTo);
                    db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReference("Project");

                    String projectId = reference.push().getKey(); // Generate a unique ID for the project
                    reference.child(projectId).setValue(project).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editTextTask.setText("");
                            editTextDateFrom.setText("");
                            editTextDateTo.setText("");
                            sp_status.setSelection(0);
                            editTextAssignTo.setText("");
                            Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                bottomSheetDialog.dismiss();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void showDatePicker(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        textView.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // Method to reset adapter data to the original data
    public void resetData() {
        cardItems.clear();
        originalData.clear();

        // Retrieve the original data from the server
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot projectSnapshot : dataSnapshot.getChildren()) {
                        // Exclude the unique ID from being retrieved as the project name
                        if (projectSnapshot.getKey() != null && !projectSnapshot.getKey().isEmpty()) {
                            // Retrieve other properties of the project
                            String projectName = projectSnapshot.child("nameproject").getValue(String.class);
                            String assignTo = projectSnapshot.child("assignto").getValue(String.class);
                            String dateFrom = projectSnapshot.child("datefrom").getValue(String.class);
                            String dateTo = projectSnapshot.child("dateto").getValue(String.class);
                            String status = projectSnapshot.child("status").getValue(String.class);
                            String taskName = projectSnapshot.child("taskName").getValue(String.class);

                            CardItem newItem = new CardItem(projectName, taskName, assignTo, dateFrom, dateTo, status);

                            // Add the new item to the cardItems and originalData lists
                            cardItems.add(newItem);
                            originalData.add(newItem);

                            // Do something with the retrieved data
                            // ...
                        }
                    }

                    cardAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
                // ...
            }
        });
    }
}
