package com.alex_jo_abraham.project2;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    TextView productNameTextView;
    TextView productDescriptionTextView;
    TextView productPriceTextView;
    TextView quantityTextView;
    Button plusButton;
    Button minusButton;
    Button addToCartButton;
    ImageView productImage;
    Button goToCartButton;

    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ProductDetailActivityLogCheck", "onCreate: Started");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productImage = findViewById(R.id.productImageView);
        productNameTextView = findViewById(R.id.productNameTextView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        productPriceTextView = findViewById(R.id.productPriceTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        plusButton = findViewById(R.id.increaseQuantityButton);
        minusButton = findViewById(R.id.decreaseQuantityButton);
        addToCartButton = findViewById(R.id.addToCartButton);
        goToCartButton = findViewById(R.id.goToCartButton);

        // Get product details from intent
        Intent intent = getIntent();
        if (intent != null) {
            product = intent.getParcelableExtra("product");
            if (product != null) {
                Log.d("ProductDetailActivityLogCheck", "Product received: " + product.getName());
                // Display product details
                Glide.with(this)
                        .load(product.getImageUrl())
                        .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                        .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                        .into(productImage);

                productNameTextView.setText(product.getName());
                productDescriptionTextView.setText(product.getDetailedDescription());
                productPriceTextView.setText(product.getPrice());
            }
        }

        // Plus button click listener
        plusButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText("Quantity: " + String.valueOf(quantity));
        });

        // Minus button click listener
        minusButton.setOnClickListener(v -> {
            if (quantity > 0) {
                quantity--;
                quantityTextView.setText("Quantity: " + String.valueOf(quantity));
            }
        });

        addToCartButton.setOnClickListener(v -> {
            // Create CartItem object representing selected product with quantity
            if(product != null) {
                CartItem cartItem = new CartItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        quantity,
                        product.getImageUrl()
                );

                // Store the cart item in the Firebase Realtime Database
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("cart")
                        .child(product.getId());

                // Check if the product is already in the cart
                cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Product already in the cart, update the quantity
                            CartItem existingCartItem = dataSnapshot.getValue(CartItem.class);
                            int newQuantity = existingCartItem.getQuantity() + quantity;
                            existingCartItem.setQuantity(newQuantity);
                            cartRef.setValue(existingCartItem)
                                    .addOnSuccessListener(aVoid -> {
                                        // Cart item updated successfully
                                        Toast.makeText(ProductDetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed to update cart item
                                        Log.e("ProductDetailActivity", "Failed to update item quantity in cart: " + e.getMessage());
                                        Toast.makeText(ProductDetailActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Product not in the cart, add it with quantity
                            cartRef.setValue(cartItem)
                                    .addOnSuccessListener(aVoid -> {
                                        // Cart item added successfully
                                        Toast.makeText(ProductDetailActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed to add cart item
                                        Log.e("ProductDetailActivity", "Failed to add item to cart: " + e.getMessage());
                                        Toast.makeText(ProductDetailActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                // Handle the case where product is null
                Log.e("ProductDetailActivity", "Product is null");
                Toast.makeText(ProductDetailActivity.this, "Product details not found", Toast.LENGTH_SHORT).show();
            }
        });
        goToCartButton.setOnClickListener(v -> {
            // Navigate to the CartPageActivity
            Intent intentCart = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intentCart);
        });
    }
}