package com.example.smd_assignment_4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<ShoppingItem, ShoppingListAdapter.ViewHolder> adapter;
    private FloatingActionButton fab;
    private Button buttonLogout;
    private FirebaseAuth auth;

    private FirebaseFirestore firestore;
    private CollectionReference shoppingListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        fab = findViewById(R.id.fab_add_item);
        auth = FirebaseAuth.getInstance();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        shoppingListRef = firestore.collection("ShoppingList");
        buttonLogout = findViewById(R.id.button_logout);

        // Set up RecyclerView
        setUpRecyclerView();

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });

        // Logout button listener
        buttonLogout.setOnClickListener(view -> {
            auth.signOut(); // Log out the user
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity to prevent returning with the back button
        });
    }

    private void setUpRecyclerView() {
        Query query = shoppingListRef.orderBy("id", Query.Direction.ASCENDING); // Ensure ordering by ID

        FirestoreRecyclerOptions<ShoppingItem> options = new FirestoreRecyclerOptions.Builder<ShoppingItem>()
                .setQuery(query, ShoppingItem.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ShoppingItem, ShoppingListAdapter.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ShoppingListAdapter.ViewHolder holder, int position, @NonNull ShoppingItem model) {
                holder.textName.setText(model.getName());
                holder.textQuantity.setText("Quantity: " + model.getQuantity());
                holder.textPrice.setText("Price: $" + model.getPrice());

                // Delete item from both Firestore and Realtime Database
                holder.buttonDelete.setOnClickListener(view -> {
                    String docId = getSnapshots().getSnapshot(position).getId(); // Firestore document ID

                    // Delete from Firestore
                    getSnapshots().getSnapshot(position).getReference().delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Delete from Realtime Database
                                    FirebaseDatabase.getInstance().getReference("ShoppingList")
                                            .child(docId).removeValue()
                                            .addOnCompleteListener(dbTask -> {
                                                if (dbTask.isSuccessful()) {
                                                    // Reassign IDs after deletion
                                                    decrementIDs();
                                                } else {
                                                    // Handle Realtime Database delete failure
                                                    showToast("Failed to delete from Realtime Database.");
                                                }
                                            });
                                } else {
                                    // Handle Firestore delete failure
                                    showToast("Failed to delete from Firestore.");
                                }
                            });
                });
            }

            @NonNull
            @Override
            public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
                return new ShoppingListAdapter.ViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void decrementIDs() {
        // Pause adapter listening
        adapter.stopListening();

        shoppingListRef.orderBy("id", Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                int id = 1;

                for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                    String docId = doc.getId();

                    // Update Firestore ID
                    shoppingListRef.document(docId).update("id", id);

                    // Update Realtime Database ID
                    FirebaseDatabase.getInstance().getReference("ShoppingList")
                            .child(docId).child("id").setValue(id);

                    id++;
                }

                // Resume adapter listening after changes
                adapter.startListening();
            }
        });
    }


    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}
