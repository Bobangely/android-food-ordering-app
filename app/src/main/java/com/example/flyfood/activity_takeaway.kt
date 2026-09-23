package com.example.flyfood

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.database.sqlite.SQLiteDatabase

class activity_takeaway : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_takeaway)

        val listView: ListView = findViewById(R.id.takeaway_list)
        val btnBack: ImageButton = findViewById(R.id.btn_back)
        val btnGoCart: TextView? = findViewById(R.id.btn_go_cart)
        val btnViewCart: TextView? = findViewById(R.id.btn_view_cart)


        val items = arrayListOf(
            TakeawayItem("Savory Mushroom Pizza", 12.99, R.drawable.pizza1),
            TakeawayItem("Classic Smash Burger", 10.99, R.drawable.burger1),
            TakeawayItem("Miso Ramen", 8.99, R.drawable.ramen),
            TakeawayItem("Tom Yum Kung", 9.99, R.drawable.tomyumkung),
            TakeawayItem("Sushi Platter", 11.49, R.drawable.shushi),
            TakeawayItem("Spaghetti Carbonara", 8.49, R.drawable.spaghetti)
        )

        val adapter = TakeawayAdapter(this, items)
        listView.adapter = adapter


        listView.setOnItemClickListener { _, view, position, _ ->
            val selected = items[position]
            addToCart(selected.name, selected.price)
        }

        btnBack.isClickable = true
        btnBack.setOnClickListener {
            Toast.makeText(this, "กลับ", Toast.LENGTH_SHORT).show()
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        btnGoCart?.setOnClickListener {
            startActivity(Intent(this, activity_cart::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        btnViewCart?.setOnClickListener {
            startActivity(Intent(this, activity_cart::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        updateTotals()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun addToCart(itemName: String, itemPrice: Double) {
        val dbHelper = SQLiteHelper(this)
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(SQLiteHelper.COLUMN_NAME, itemName)
            put(SQLiteHelper.COLUMN_PRICE, itemPrice)
            put(SQLiteHelper.COLUMN_QTY, 1)
        }

        db.insert(SQLiteHelper.TABLE_NAME, null, values)

        Toast.makeText(this, "เพิ่ม $itemName ในตะกร้า", Toast.LENGTH_SHORT).show()
        updateTotals()
    }

    fun updateTotals() {
        val dbHelper = SQLiteHelper(this)
        val db: SQLiteDatabase = dbHelper.readableDatabase
        var subtotal = 0.0
        val cursor = try {
            db.rawQuery("SELECT ${SQLiteHelper.COLUMN_PRICE}, ${SQLiteHelper.COLUMN_QTY} FROM ${SQLiteHelper.TABLE_NAME}", null)
        } catch (e: Exception) {
            null
        }

        cursor?.use {
            while (it.moveToNext()) {
                val price = try { it.getDouble(0) } catch (e: Exception) { 0.0 }
                val qty = try { it.getInt(1) } catch (e: Exception) { 1 }
                subtotal += price * qty
            }
        }

        val tax = subtotal * 0.05
        val total = subtotal + tax

        try {
            val subtotalText: TextView = findViewById(R.id.subtotal_text)
            val taxText: TextView = findViewById(R.id.tax_text)
            val totalText: TextView = findViewById(R.id.total_text)
            subtotalText.text = String.format("%.2f", subtotal)
            taxText.text = String.format("%.2f", tax)
            totalText.text = String.format("%.2f", total)
        } catch (e: Exception) {

        }
    }


    data class TakeawayItem(val name: String, val price: Double, val imageRes: Int)

    class TakeawayAdapter(context: android.content.Context, private val items: List<TakeawayItem>) : ArrayAdapter<TakeawayItem>(context, 0, items) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_with_image, parent, false)
            val item = items[position]

            val iv: ImageView = view.findViewById(R.id.item_image)
            val nameTv: TextView = view.findViewById(R.id.item_name)
            val priceTv: TextView = view.findViewById(R.id.item_price)
            val qtyValue: TextView? = view.findViewById(R.id.qty_value)
            val plusBtn: TextView? = view.findViewById(R.id.qty_plus)
            val minusBtn: TextView? = view.findViewById(R.id.qty_minus)

            try { iv.setImageResource(item.imageRes) } catch (e: Exception) { }
            nameTv.text = item.name
            priceTv.text = String.format("฿%.2f", item.price)


            var currentQty = 0
            try {
                val dbHelper = SQLiteHelper(context)
                val db = dbHelper.readableDatabase
                val cursor = db.rawQuery("SELECT ${SQLiteHelper.COLUMN_QTY} FROM ${SQLiteHelper.TABLE_NAME} WHERE ${SQLiteHelper.COLUMN_NAME} = ? LIMIT 1", arrayOf(item.name))
                cursor.use {
                    if (it.moveToFirst()) {
                        currentQty = try { it.getInt(0) } catch (e: Exception) { 0 }
                    }
                }
            } catch (e: Exception) {
                currentQty = 0
            }

            qtyValue?.text = currentQty.toString()

            plusBtn?.setOnClickListener {
                try {
                    val dbHelper = SQLiteHelper(context)
                    val db = dbHelper.writableDatabase
                    val cursor = db.rawQuery("SELECT ${SQLiteHelper.COLUMN_QTY} FROM ${SQLiteHelper.TABLE_NAME} WHERE ${SQLiteHelper.COLUMN_NAME} = ? LIMIT 1", arrayOf(item.name))
                    var qty = 0
                    cursor.use {
                        if (it.moveToFirst()) qty = try { it.getInt(0) } catch (e: Exception) { 0 }
                    }

                    if (qty > 0) {
                        val values = android.content.ContentValues().apply { put(SQLiteHelper.COLUMN_QTY, qty + 1) }
                        db.update(SQLiteHelper.TABLE_NAME, values, "${SQLiteHelper.COLUMN_NAME} = ?", arrayOf(item.name))
                        qty += 1
                    } else {
                        val values = android.content.ContentValues().apply {
                            put(SQLiteHelper.COLUMN_NAME, item.name)
                            put(SQLiteHelper.COLUMN_PRICE, item.price)
                            put(SQLiteHelper.COLUMN_QTY, 1)
                        }
                        db.insert(SQLiteHelper.TABLE_NAME, null, values)
                        qty = 1
                    }

                    qtyValue?.text = qty.toString()

                    (context as? activity_takeaway)?.updateTotals()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            minusBtn?.setOnClickListener {
                try {
                    val dbHelper = SQLiteHelper(context)
                    val db = dbHelper.writableDatabase
                    val cursor = db.rawQuery("SELECT ${SQLiteHelper.COLUMN_QTY} FROM ${SQLiteHelper.TABLE_NAME} WHERE ${SQLiteHelper.COLUMN_NAME} = ? LIMIT 1", arrayOf(item.name))
                    var qty = 0
                    cursor.use {
                        if (it.moveToFirst()) qty = try { it.getInt(0) } catch (e: Exception) { 0 }
                    }

                    if (qty > 1) {
                        val values = android.content.ContentValues().apply { put(SQLiteHelper.COLUMN_QTY, qty - 1) }
                        db.update(SQLiteHelper.TABLE_NAME, values, "${SQLiteHelper.COLUMN_NAME} = ?", arrayOf(item.name))
                        qty -= 1
                    } else if (qty == 1) {
                        db.delete(SQLiteHelper.TABLE_NAME, "${SQLiteHelper.COLUMN_NAME} = ?", arrayOf(item.name))
                        qty = 0
                    }

                    qtyValue?.text = qty.toString()
                    (context as? activity_takeaway)?.updateTotals()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return view
        }
    }
}
