package com.example.flyfood

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase

class activity_home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val cartButton: ImageButton = findViewById(R.id.cart)
        cartButton.setOnClickListener {
            val intent = Intent(this, activity_cart::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val logoutButton: ImageButton? = try { findViewById(R.id.btn_logout) } catch (e: Exception) { null }
        logoutButton?.setOnClickListener {
            val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            try {
                val dbHelper = SQLiteHelper(this)
                val db = dbHelper.writableDatabase
                db.delete(SQLiteHelper.TABLE_NAME, null, null)
                db.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val loginIntent = Intent(this, activity_Login::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
        }


        val promoButton: ImageButton? = try { findViewById(R.id.ic_promo) } catch (e: Exception) { null }
        promoButton?.setOnClickListener {
            val intent = Intent(this, activity_promotion::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val addToCartButton1: Button = findViewById(R.id.add_to_cart1)
        val addToCartButton2: Button = findViewById(R.id.add_to_cart2)
        val addToCartButton3: Button = findViewById(R.id.add_to_cart3)
        val addToCartButton4: Button = findViewById(R.id.add_to_cart4)
        val addToCartButton5: Button = findViewById(R.id.add_to_cart5)
        val addToCartButton6: Button = findViewById(R.id.add_to_cart6)

        addToCartButton1.setOnClickListener {
            addToCart("Savory Mushroom Pizza", 12.99)
        }

        addToCartButton2.setOnClickListener {
            addToCart("Classic Smash Burger", 10.99)
        }

        addToCartButton3.setOnClickListener {
            addToCart("Miso Ramen", 8.99)
        }

        addToCartButton4.setOnClickListener {
            addToCart("Tom Yum Kung", 9.99)
        }

        addToCartButton5.setOnClickListener {
            addToCart("Sushi Platter", 11.49)
        }

        addToCartButton6.setOnClickListener {
            addToCart("Spaghetti Carbonara", 8.49)
        }


        val takeAwayTab: TextView? = try { findViewById(R.id.tab_take_away) } catch (e: Exception) { null }
        takeAwayTab?.setOnClickListener {
            val intent = Intent(this, activity_takeaway::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun addToCart(itemName: String, itemPrice: Double) {
        val dbHelper = SQLiteHelper(this)
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(SQLiteHelper.COLUMN_NAME, itemName)
            put(SQLiteHelper.COLUMN_PRICE, itemPrice)
        }

        db.insert(SQLiteHelper.TABLE_NAME, null, values)

        Toast.makeText(this, "Added $itemName to Cart", Toast.LENGTH_SHORT).show()
    }
}