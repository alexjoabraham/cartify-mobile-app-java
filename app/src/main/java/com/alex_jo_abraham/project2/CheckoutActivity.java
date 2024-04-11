package com.alex_jo_abraham.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CheckoutActivity extends AppCompatActivity {
    TextInputEditText firstNameEditText;
    TextInputEditText lastNameEditText;
    TextInputEditText addressEditText;
    TextInputEditText phoneEditText;
    TextInputEditText cardNumberLayout;
    TextInputEditText expiryDateLayout;
    TextInputEditText cvvLayout;
    TextInputEditText nameOnCardLayout;
    Button placeOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        addressEditText = findViewById(R.id.address);
        phoneEditText = findViewById(R.id.mobile);
        cardNumberLayout = findViewById(R.id.cardNumber);
        expiryDateLayout = findViewById(R.id.expiryDate);
        cvvLayout = findViewById(R.id.cvv);
        nameOnCardLayout = findViewById(R.id.nameOnCard);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        placeOrderButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // After order submission is successful, navigate to Thank You page
                Intent intent = new Intent(CheckoutActivity.this, ThankyouActivity.class);
                startActivity(intent);

                // Clear cart items from the Firebase Realtime Database
                clearCartItems();
            }
        });
    }
    private void clearCartItems() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("cart");

        cartRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Cart items cleared successfully
                    Toast.makeText(CheckoutActivity.this, "Cart items cleared", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to clear cart items
                    Log.e("CheckoutActivity", "Failed to clear cart items: " + e.getMessage());
                    Toast.makeText(CheckoutActivity.this, "Failed to clear cart items", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate first name
        String firstName = firstNameEditText.getText().toString().trim();
        if (firstName.isEmpty()) {
            firstNameEditText.setError("First name is required");
            isValid = false;
        } else {
            firstNameEditText.setError(null);
        }

        // Validate last name
        String lastName = lastNameEditText.getText().toString().trim();
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Last name is required");
            isValid = false;
        } else {
            lastNameEditText.setError(null);
        }

        // Validate address
        String address = addressEditText.getText().toString().trim();
        if (address.isEmpty()) {
            addressEditText.setError("Address is required");
            isValid = false;
        } else {
            addressEditText.setError(null);
        }

        // Validate phone number
        String phone = phoneEditText.getText().toString().trim();
        if (phone.isEmpty() || phone.length() != 10) {
            phoneEditText.setError("Enter a valid phone number");
            isValid = false;
        } else {
            phoneEditText.setError(null);
        }

        // Validate card number
        String cardNumber = cardNumberLayout.getText().toString().trim();
        if (!isValidCardNumber(cardNumber)) {
            cardNumberLayout.setError("Enter a valid card number");
            isValid = false;
        } else {
            cardNumberLayout.setError(null);
        }

        // Validate expiry date
        String expiryDate = expiryDateLayout.getText().toString().trim();
        if (!isValidExpiryDate(expiryDate)) {
            expiryDateLayout.setError("Enter a valid expiry date (MM/YY)");
            isValid = false;
        } else {
            expiryDateLayout.setError(null);
        }

        // Validate CVV
        String cvv = cvvLayout.getText().toString().trim();
        if (!isValidCVV(cvv)) {
            cvvLayout.setError("Enter a valid CVV");
            isValid = false;
        } else {
            cvvLayout.setError(null);
        }

        // Validate name on card
        String nameOnCard = nameOnCardLayout.getText().toString().trim();
        if (nameOnCard.isEmpty()) {
            nameOnCardLayout.setError("Name on card is required");
            isValid = false;
        } else {
            nameOnCardLayout.setError(null);
        }

        return isValid;
    }
    private boolean isValidCardNumber(String cardNumber) {
        // Validation using Luhn algorithm
        // Remove any spaces or dashes from the card number
        cardNumber = cardNumber.replaceAll("[\\s-]+", "");

        // Check if the card number is numeric and has a valid length
        if (!cardNumber.matches("\\d{13,19}")) {
            return false;
        }

        // Convert the card number to an array of integers
        int[] digits = new int[cardNumber.length()];
        for (int i = 0; i < cardNumber.length(); i++) {
            digits[i] = Character.getNumericValue(cardNumber.charAt(i));
        }

        // Double every second digit from the right (starting from the second-to-last digit)
        for (int i = digits.length - 2; i >= 0; i -= 2) {
            int doubledDigit = digits[i] * 2;
            if (doubledDigit > 9) {
                // If the doubled digit is greater than 9, subtract 9
                doubledDigit -= 9;
            }
            digits[i] = doubledDigit;
        }

        // Sum all the digits
        int sum = 0;
        for (int digit : digits) {
            sum += digit;
        }

        // The card number is valid if the sum is a multiple of 10
        return sum % 10 == 0;
    }

    private boolean isValidExpiryDate(String expiryDate) {
        // Check if the expiry date is in the format "MM/YY"
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        // Split the expiry date into month and year
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        // Get the current month and year
        int currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1; // Adding 1 because Calendar.MONTH returns 0-based index
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100; // Getting the last two digits of the current year

        // Check if the expiry date is in the future
        if (year > currentYear || (year == currentYear && month >= currentMonth)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidCVV(String cvv) {
        // Check if CVV contains only digits and has a length of 3 or 4
        if (!cvv.matches("\\d{3,4}")) {
            return false;
        }
        return true;
    }
}