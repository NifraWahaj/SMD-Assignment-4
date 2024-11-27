package com.example.smd_assignment_4;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AddItemActivity extends AppCompatActivity {

    private EditText inputName, inputQuantity, inputPrice;
    private Button buttonAddItem;
    private ProgressBar progressBar;

    private DatabaseReference realtimeDbRef;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        inputName = findViewById(R.id.input_name);
        inputQuantity = findViewById(R.id.input_quantity);
        inputPrice = findViewById(R.id.input_price);
        buttonAddItem = findViewById(R.id.button_add_item);
        progressBar = findViewById(R.id.progress_bar);

        // Initialize Firebase components
        realtimeDbRef = FirebaseDatabase.getInstance().getReference("ShoppingList");
        firestore = FirebaseFirestore.getInstance();

        buttonAddItem.setOnClickListener(view -> addItem());
    }

    private void addItem() {
        String name = inputName.getText().toString().trim();
        String quantityStr = inputQuantity.getText().toString().trim();
        String priceStr = inputPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        progressBar.setVisibility(View.VISIBLE);

        // Generate a unique ID for Realtime Database
        String itemId = realtimeDbRef.push().getKey();

        // Create a new item without an ID yet
        ShoppingItem item = new ShoppingItem(name, quantity, price, 0); // ID is set to 0 temporarily

        // Add to Realtime Database
        if (itemId != null) {
            realtimeDbRef.child(itemId).setValue(item)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sync to Firestore and assign sequential ID
                            syncToFirestore(item, itemId);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Failed to add item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void syncToFirestore(ShoppingItem item, String itemId) {
        // Fetch the highest current ID in Firestore
        firestore.collection("ShoppingList")
                .orderBy("id", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the highest ID and assign the next one
                        int maxId = task.getResult().getDocuments().get(0).getLong("id").intValue();
                        item.setId(maxId + 1);
                    } else {
                        // If no items exist, start with ID 1
                        item.setId(1);
                    }

                    // Add the item to Firestore with the new ID
                    firestore.collection("ShoppingList").document(itemId).set(item)
                            .addOnCompleteListener(addTask -> {
                                if (addTask.isSuccessful()) {
                                    // Update the item in Realtime Database with the correct ID
                                    realtimeDbRef.child(itemId).setValue(item)
                                            .addOnCompleteListener(rtDbTask -> {
                                                progressBar.setVisibility(View.GONE);
                                                if (rtDbTask.isSuccessful()) {
                                                    Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(this, "Failed to update Realtime DB: " + rtDbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Firestore sync failed: " + addTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                });
    }

}
