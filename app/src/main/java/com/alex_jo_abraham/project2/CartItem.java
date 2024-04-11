package com.alex_jo_abraham.project2;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CartItem implements Parcelable {
    private String productId;
    private String name;
    private String price;
    private String imageUrl;
    private int quantity;

    public CartItem() {
    }

    public CartItem(String productId, String name, String price, int quantity, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Parcelable implementation
    protected CartItem(Parcel in) {
        productId = in.readString();
        name = in.readString();
        price = in.readString();
        quantity = in.readInt();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeInt(quantity);
        dest.writeString(imageUrl);
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
}
