package com.alex_jo_abraham.project2;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductAdapter extends FirebaseRecyclerAdapter<Product,ProductAdapter.ProductViewHolder> {
    public ProductAdapter(@NonNull FirebaseRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder productViewHolder, int i, @NonNull Product product) {
        Log.d("ProductAdapterLogCheck", "onBindViewHolder: Position " + i);

        productViewHolder.productName.setText(product.getName());
        productViewHolder.productPrice.setText(product.getPrice());
        productViewHolder.productDescription.setText(product.getDescription());
        Glide.with(productViewHolder.productImage.getContext())
                .load(product.getImageUrl())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(productViewHolder.productImage);

        productViewHolder.productAddToCartButton.setOnClickListener(v -> {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("cart")
                    .child(product.getId());

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Product already in the cart, update the quantity
                        CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                        int newQuantity = cartItem.getQuantity() + 1;
                        cartItem.setQuantity(newQuantity);
                        cartRef.setValue(cartItem);
                    } else {
                        // Product not in the cart, add it with quantity 1
                        CartItem cartItem = new CartItem(
                                product.getId(),
                                product.getName(),
                                product.getPrice(),
                                1,
                                product.getImageUrl()
                        );
                        cartRef.setValue(cartItem);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ProductAdapter", "Failed to read cart item.", databaseError.toException());
                }
            });

            Toast.makeText(productViewHolder.itemView.getContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
        });

        // Changes to implement ProductDetailsActivity
        if (product != null) {
            productViewHolder.productCardView.setOnClickListener(v -> {
                //Launch ProductDetailActivity and pass Product Details
                Intent intent = new Intent(productViewHolder.itemView.getContext(), ProductDetailActivity.class);
                intent.putExtra("product", product);
                productViewHolder.itemView.getContext().startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductAdapter.ProductViewHolder(view);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productName, productPrice, productDescription;
        Button productAddToCartButton;
        CardView productCardView;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);

            productCardView = itemView.findViewById(R.id.productCardView);
            productAddToCartButton = itemView.findViewById(R.id.buttonCart);
        }
    }
}
