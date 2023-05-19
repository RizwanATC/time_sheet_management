package com.mockie.time_sheet_management;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private CardAdapter cardAdapter;
    private List<CardItem> cardItems;

    private Button buttonCreate;

    private Button searchButton;
    private EditText searchEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        cardItems = new ArrayList<>();
        cardAdapter = new CardAdapter(this, cardItems);

        // Add some example card items
        cardItems.add(new CardItem("Project A", "Task 1", "John Doe", "2023-05-20", "2023-05-22", "In Progress"));
        cardItems.add(new CardItem("Project B", "Task 2", "Jane Smith", "2023-05-21", "2023-05-23", "Completed"));
        cardItems.add(new CardItem("Project B", "Task 2", "Jane Smith", "2023-05-21", "2023-05-23", "Completed"));
        cardItems.add(new CardItem("Project B", "Task 2", "Jane Smith", "2023-05-21", "2023-05-23", "Completed"));
        cardItems.add(new CardItem("Project B", "Task 2", "Jane Smith", "2023-05-21", "2023-05-23", "Completed"));
        cardItems.add(new CardItem("Project B", "Task 2", "Jane Smith", "2023-05-21", "2023-05-23", "Completed"));


        // Set the adapter to the ListView
        listView.setAdapter(cardAdapter);
        searchEditText = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.buttonSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString().trim();
                performSearch(searchText);
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
            if (cardItem.getProject().toLowerCase().contains(searchText.toLowerCase()) ||
                    cardItem.getProject().toLowerCase().contains(searchText.toLowerCase())) {
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
                String text = editText.getText().toString();
                String project = editTextProject.getText().toString();
                String task = editTextTask.getText().toString();
                String dateFrom = editTextDateFrom.getText().toString();
                String dateTo = editTextDateTo.getText().toString();
                String status = sp_status.getSelectedItem().toString();
                String assignTo = editTextAssignTo.getText().toString();

                // Perform save operation with the retrieved values

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

}
