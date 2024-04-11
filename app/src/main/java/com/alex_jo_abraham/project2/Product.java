package com.alex_jo_abraham.project2;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    String name, description, price, imageUrl, detailedDescription, id, quantity;

    Product () {

    }



    public Product(String name, String description, String price, String imageUrl, String detailedDescription, String id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.detailedDescription = detailedDescription;
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    // Parcelable implementation done for implementing ProductDetailActivity
    protected Product(Parcel in) {
        // Read values from Parcel and set them to your fields
        name = in.readString();
        description = in.readString();
        price = in.readString();
        imageUrl = in.readString();
        detailedDescription = in.readString();
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write values to Parcel
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(imageUrl);
        dest.writeString(detailedDescription);
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
