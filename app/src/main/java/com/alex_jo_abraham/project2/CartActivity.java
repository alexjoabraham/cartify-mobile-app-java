package com.alex_jo_abraham.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    TextView totalPriceTextView;
    Button checkOutButton;
    TextView taxesTextView;
    TextView amountToPayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        recyclerView = findViewById(R.id.cartRecyclerView);
        checkOutButton = findViewById(R.id.checkoutButton);
        taxesTextView = findViewById(R.id.taxesTextView);
        amountToPayTextView = findViewById(R.id.amountToPayTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(cartAdapter);

        // Retrieve cart items from Firebase and populate RecyclerView
        retrieveCartItems();

        checkOutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            startActivity(intent);
        });
    }
    private void retrieveCartItems() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("cart");

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CartItem> cartItems = new ArrayList<>();
                double totalPrice = 0.0;
                double totalTaxes = 0.0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = snapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        cartItems.add(cartItem);
                        // Calculate subtotal for each item and add to total price
                        String priceString = cartItem.getPrice().replaceAll(",", "");
                        double price = Double.parseDouble(priceString.substring(1));
                        double subtotal = price * cartItem.getQuantity();
                        totalPrice += subtotal;
                        // Calculate taxes for each item
                        double taxes = subtotal * 0.15; // HST is 15%
                        totalTaxes += taxes;
                    }
                }
                // Calculate total amount to pay (including price and taxes)
                double amountToPay = totalPrice + totalTaxes;

                // Set total price, taxes, and amount to pay to TextViews
                totalPriceTextView.setText("Price: $" + String.format("%.2f", totalPrice));
                taxesTextView.setText("Taxes (HST 15%): $" + String.format("%.2f", totalTaxes));
                amountToPayTextView.setText("Amount to Pay: $" + String.format("%.2f", amountToPay));

                cartAdapter.setCartItemsList(cartItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}