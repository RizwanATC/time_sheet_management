package com.mockie.time_sheet_management;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.firebase.database.Query;
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
    private List<CardItem> originalData;

    private ImageButton buttonCreate;

    private ImageButton searchButton;
    private EditText searchEditText;

    FirebaseDatabase db;
    DatabaseReference databaseReference;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listView = findViewById(R.id.listView);
        cardItems = new ArrayList<>();
        originalData = new ArrayList<>();
        cardAdapter = new CardAdapter(this, cardItems, originalData);

        fetchFIreBaseData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CardItem selectedCardItem = cardItems.get(position);
                String cardItemId = selectedCardItem.getId();
                selectedCardItem.setId(String.valueOf(position));


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

                    fetchFIreBaseData();
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
                    fetchFIreBaseData(); // Reset the adapter data to the original list
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
                    cardItem.getTaskName().toLowerCase().contains(searchText.toLowerCase()) ||
                    cardItem.getStatus().toLowerCase().contains(searchText.toLowerCase())) {
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
        LinearLayout ly_from = bottomSheetView.findViewById(R.id.lyFrom);
        LinearLayout ly_to = bottomSheetView.findViewById(R.id.lyTo);
        TextView editTextDateFrom = bottomSheetView.findViewById(R.id.editTextDateFrom);
        TextView editTextDateTo = bottomSheetView.findViewById(R.id.editTextDateTo);
        Spinner sp_status = bottomSheetView.findViewById(R.id.spinnerStatus);
        Spinner sp_user = bottomSheetView.findViewById(R.id.SpinnerUser);

        Button buttonSave = bottomSheetView.findViewById(R.id.save);
        Button buttonClose = bottomSheetView.findViewById(R.id.close);
        ImageButton createUser = bottomSheetView.findViewById(R.id.createUser);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);


                LayoutInflater inflater = LayoutInflater.from(dialogBuilder.getContext());
                View dialogView = inflater.inflate(R.layout.popup_create_user, null);
                dialogBuilder.setView(dialogView);


                Button createUser = dialogView.findViewById(R.id.createButton);
                EditText user = dialogView.findViewById(R.id._userName);




                AlertDialog dialog = dialogBuilder.create();


                createUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

                        String userName = user.getText().toString().trim();

                        User user = new User(userName);
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                int nextUserId = (int) dataSnapshot.getChildrenCount() + 1;
                                String userId = "user" + nextUserId;
                                databaseReference.child(userId).setValue(user);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any database error here
                            }
                        });
                    }

                });

// Show the dialog
                dialog.show();


            }
        });

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

        String[] statusOptions = {"Closed", "Open", "In Progress"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_status.setAdapter(adapter);


        DatabaseReference userRetrieve = FirebaseDatabase.getInstance().getReference("users");

        userRetrieve.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> userNames = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        String userName = user.getName();
                        userNames.add(userName);
                    }
                }

                String[] userList = userNames.toArray(new String[0]);

                ArrayAdapter<String> userAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, userList);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp_user.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database error here
            }
        });





        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = editTextProject.getText().toString();
                String taskName = editTextTask.getText().toString();
                String datefrom = editTextDateFrom.getText().toString();
                String dateto = editTextDateTo.getText().toString();
                String status = sp_status.getSelectedItem().toString();
                String assignto = sp_user.getSelectedItem().toString();

                // Perform save operation with the retrieved values
                if (!projectName.isEmpty() && !taskName.isEmpty() && !datefrom.isEmpty() &&
                        !dateto.isEmpty() && !status.isEmpty() && !assignto.isEmpty()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Project");

                    reference.orderByChild("nameproject").equalTo(projectName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // A project with the same name already exists
                                Toast.makeText(MainActivity.this, "Project with the same name already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                Project project = new Project(projectName, taskName, datefrom, dateto, status, assignto);
                                String projectId = reference.push().getKey(); // Generate a unique ID for the project
                                reference.child(projectId).setValue(project).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        editTextTask.setText("");
                                        editTextDateFrom.setText("");
                                        editTextDateTo.setText("");
                                        sp_status.setSelection(0);
                                        sp_user.setSelection(0);
                                        Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
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

    }

    public void fetchFIreBaseData(){
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

                        String projectKey = projectSnapshot.getKey();
                        CardItem cardItem = new CardItem();
                        cardItem.setProjectKey(projectKey);



                        String projectName = projectSnapshot.child("nameproject").getValue(String.class);
                        String assignTo = projectSnapshot.child("assignto").getValue(String.class);
                        String dateFrom = projectSnapshot.child("datefrom").getValue(String.class);
                        String dateTo = projectSnapshot.child("dateto").getValue(String.class);
                        String status = projectSnapshot.child("status").getValue(String.class);
                        String taskName = projectSnapshot.child("taskName").getValue(String.class);

                        CardItem newItem = new CardItem(projectKey,projectName, taskName, assignTo, dateFrom, dateTo, status);

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
    }


}

