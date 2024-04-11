package com.alex_jo_abraham.project2;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

//    public CartAdapter() {
//        this.cartItemList = new ArrayList<>();
//    }

    public void setCartItemsList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
    }


    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
        private ImageView productImageView;
        private TextView productNameTextView;
        private TextView productPriceTextView;
        private TextView quantityTextView;
        private Button increaseQuantityButton;
        private Button decreaseQuantityButton;
        private ImageButton removeButton;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }

        public void bind(CartItem cartItem) {
            Glide.with(context).load(cartItem.getImageUrl()).into(productImageView);
            productNameTextView.setText(cartItem.getName());
            productPriceTextView.setText(cartItem.getPrice());
            quantityTextView.setText("Quantity: " + cartItem.getQuantity());

            increaseQuantityButton.setOnClickListener(v -> {
                int quantity = cartItem.getQuantity() + 1;
                cartItem.setQuantity(quantity);
                quantityTextView.setText("Quantity: " + quantity);

                // Update quantity in the Firebase Realtime Database
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("cart")
                        .child(cartItem.getProductId());
                cartRef.child("quantity").setValue(quantity);
            });

            decreaseQuantityButton.setOnClickListener(v -> {
                int quantity = cartItem.getQuantity() - 1;
                if (quantity >= 0) {
                    cartItem.setQuantity(quantity);
                    quantityTextView.setText("Quantity: " + quantity);

                    // Update quantity in the Firebase Realtime Database
                    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("cart")
                            .child(cartItem.getProductId());
                    cartRef.child("quantity").setValue(quantity);
                } else {
                    Toast.makeText(context, "Quantity cannot be negative", Toast.LENGTH_SHORT).show();
                }
            });

            removeButton.setOnClickListener(v -> {
                // Remove the item from the cart
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("cart")
                        .child(cartItem.getProductId());
                Log.d("CartAdapterLogCheck", "CartRef: " + cartRef.toString());
                Log.d("CartAdapterLogCheck", "CartItem ID: " + cartItem.getProductId());
                cartRef.removeValue();

                // Remove the item from the cartItemList
                int position = getAdapterPosition();
                cartItemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItemList.size());
            });
        }
    }
}
