package com.example.flyfood

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class activity_promotion : AppCompatActivity() {
    data class Promo(val title: String, val merchant: String, val priceText: String, val imageRes: Int, val time: String)

    private val selected = mutableSetOf<Int>()
    private lateinit var promos: List<Promo>
    private lateinit var promoList: RecyclerView
    private lateinit var newDealsList: androidx.recyclerview.widget.RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion)

        val backBtn: ImageButton = findViewById(R.id.btn_back_promo)
        backBtn.setOnClickListener { finish(); overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right) }

        promoList = findViewById(R.id.promo_list)


        promos = listOf(
            Promo("Savory Mushroom Pizza", "House Kitchen", "฿8.99", R.drawable.pizza1, "ใช้ได้ทุกเวลา"),
            Promo("Classic Smash Burger", "House Kitchen", "฿4.99", R.drawable.burger1, "ใช้ได้ทุกเวลา"),
            Promo("Miso Ramen", "House Kitchen", "฿6.99", R.drawable.ramen, "ใช้ได้ทุกเวลา"),
            Promo("Tom Yum Kung", "House Kitchen", "฿5.99", R.drawable.tomyumkung, "ใช้ได้ทุกเวลา"),
            Promo("Sushi Platter", "House Kitchen", "฿7.99", R.drawable.shushi, "ใช้ได้ทุกเวลา"),
            Promo("Spaghetti Carbonara", "House Kitchen", "฿4.99", R.drawable.spaghetti, "ใช้ได้ทุกเวลา")
        )


        promoList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = PromoAdapter(promos)
        promoList.adapter = adapter


        newDealsList = findViewById(R.id.new_deals_list)
        val newDeals = listOf(
            Promo("Savory Mushroom Pizza", "House Kitchen", "฿8.99", R.drawable.pizza1, ""),
            Promo("Classic Smash Burger", "House Kitchen", "฿4.99", R.drawable.burger1, ""),
            Promo("Miso Ramen", "House Kitchen", "฿6.99", R.drawable.ramen, ""),
            Promo("Tom Yum Kung", "House Kitchen", "฿5.99", R.drawable.tomyumkung, ""),
            Promo("Sushi Platter", "House Kitchen", "฿7.99", R.drawable.shushi, ""),
            Promo("Spaghetti Carbonara", "House Kitchen", "฿4.99", R.drawable.spaghetti, "")
        )
        newDealsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        newDealsList.adapter = NewDealAdapter(newDeals)


        val addBtn: Button = findViewById(R.id.add_selected_button)
        addBtn.setOnClickListener { addSelectedToCart() }

        updateActionBar()
    }

    private fun updateActionBar() {
        val subtotalTv: TextView = findViewById(R.id.selected_subtotal)
        val addBtn: Button = findViewById(R.id.add_selected_button)

        var subtotal = 0.0
        for (i in selected) {
            val p = promos.getOrNull(i) ?: continue
            subtotal += parsePrice(p.priceText)
        }

        subtotalTv.text = String.format("฿%.2f (%d)", subtotal, selected.size)
        addBtn.text = if (selected.isEmpty()) "เพิ่มทั้งหมดไปตะกร้า" else "เพิ่ม ${selected.size} รายการ"
    }

    private fun addSelectedToCart() {
        if (selected.isEmpty()) {
            Toast.makeText(this, "ยังไม่ได้เลือกรายการ", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = SQLiteHelper(this)
        val db = dbHelper.writableDatabase

        for (i in selected.toList()) {
            val p = promos.getOrNull(i) ?: continue
            val values = ContentValues().apply {
                put(SQLiteHelper.COLUMN_NAME, p.title)
                put(SQLiteHelper.COLUMN_PRICE, parsePrice(p.priceText))
                put(SQLiteHelper.COLUMN_QTY, 1)
            }
            db.insert(SQLiteHelper.TABLE_NAME, null, values)
        }

        Toast.makeText(this, "เพิ่ม ${selected.size} รายการลงตะกร้า", Toast.LENGTH_SHORT).show()


        selected.clear()
        promoList.adapter?.notifyDataSetChanged()
        updateActionBar()
    }

    private fun parsePrice(priceText: String): Double {
        return try {
            priceText.replace("฿", "").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }


    inner class PromoAdapter(private val items: List<Promo>) : RecyclerView.Adapter<PromoAdapter.PromoVH>() {
        inner class PromoVH(view: View) : RecyclerView.ViewHolder(view) {
            val img: ImageView = view.findViewById(R.id.promo_image)
            val title: TextView = view.findViewById(R.id.promo_title)
            val merchant: TextView = view.findViewById(R.id.promo_merchant)
            val price: TextView = view.findViewById(R.id.promo_price)
            val time: TextView = view.findViewById(R.id.promo_time)
            val checkbox: CheckBox = view.findViewById(R.id.promo_select)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoVH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_promo, parent, false)
            return PromoVH(v)
        }

        override fun onBindViewHolder(holder: PromoVH, position: Int) {
            val p = items[position]
            holder.img.setImageResource(p.imageRes)
            holder.title.text = p.title
            holder.merchant.text = p.merchant
            holder.price.text = p.priceText
            holder.time.text = p.time


            holder.checkbox.setOnCheckedChangeListener(null)
            holder.checkbox.isChecked = selected.contains(position)
            holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selected.add(position) else selected.remove(position)
                updateActionBar()
            }

            holder.itemView.setOnClickListener {
                holder.checkbox.isChecked = !holder.checkbox.isChecked
            }
        }

        override fun getItemCount(): Int = items.size
    }


    inner class NewDealAdapter(private val items: List<Promo>) : RecyclerView.Adapter<NewDealAdapter.DealVH>() {
        inner class DealVH(view: View) : RecyclerView.ViewHolder(view) {
            val img: ImageView = view.findViewById(R.id.menu_image)
            val title: TextView = view.findViewById(R.id.menu_title)
            val price: TextView = view.findViewById(R.id.menu_price)
            val addBtn: ImageButton = view.findViewById(R.id.menu_add)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealVH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_menu, parent, false)
            return DealVH(v)
        }

        override fun onBindViewHolder(holder: DealVH, position: Int) {
            val p = items[position]
            holder.img.setImageResource(p.imageRes)
            holder.title.text = p.title
            holder.price.text = p.priceText

            holder.addBtn.setOnClickListener {

                val dbHelper = SQLiteHelper(this@activity_promotion)
                val db = dbHelper.writableDatabase
                val values = android.content.ContentValues().apply {
                    put(SQLiteHelper.COLUMN_NAME, p.title)
                    put(SQLiteHelper.COLUMN_PRICE, parsePrice(p.priceText))
                    put(SQLiteHelper.COLUMN_QTY, 1)
                }
                db.insert(SQLiteHelper.TABLE_NAME, null, values)
                Toast.makeText(this@activity_promotion, "Added ${p.title} to cart", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
