package com.example.flyfood

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

data class OrderItem(
    val id: Int,
    val name: String,
    val price: Double,
    var qty: Int,
    val imageRes: Int
)

class activity_cart : AppCompatActivity() {

    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val cartListView: ListView = findViewById(R.id.cart_list)
        val emptyView: TextView = findViewById(R.id.empty_view)
        cartListView.emptyView = emptyView

        val backBtn: ImageButton? = try { findViewById(R.id.btn_back) } catch (e: Exception) { null }
        backBtn?.isClickable = true
        backBtn?.setOnClickListener {
            Toast.makeText(this, "กลับ", Toast.LENGTH_SHORT).show()
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        val orders = fetchOrders()
        adapter = OrderAdapter(orders)
        cartListView.adapter = adapter

        updateTotals()

        val paymentCard: View? = try { findViewById(R.id.payment_card) } catch (e: Exception) { null }
        val listViewAnimTarget: View? = try { findViewById(R.id.cart_list) } catch (e: Exception) { null }
        val animPulse = AnimationUtils.loadAnimation(this, R.anim.scale_pulse)
        val animFadeDown = AnimationUtils.loadAnimation(this, R.anim.fade_out_translate_down)
        val animShake = AnimationUtils.loadAnimation(this, R.anim.shake)


        val deleteBtn: Button = findViewById(R.id.delete_order_button)
        deleteBtn.setOnClickListener {
            if (adapter.items.isEmpty()) {
                paymentCard?.startAnimation(animShake)
                Toast.makeText(this, "ไม่มีรายการให้ลบ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            deleteBtn.startAnimation(animPulse)

            AlertDialog.Builder(this)
                .setTitle("ยืนยันการลบ")
                .setMessage("ต้องการลบรายการทั้งหมดหรือไม่?")
                .setPositiveButton("ลบ") { d, _ ->
                    // animate list and payment card out, then clear
                    val onEnd = object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            deleteAllOrders()
                            adapter.items.clear()
                            adapter.notifyDataSetChanged()
                            updateTotals()
                        }
                    }
                    animFadeDown.setAnimationListener(onEnd)
                    listViewAnimTarget?.startAnimation(animFadeDown)
                    paymentCard?.startAnimation(animFadeDown)

                    d.dismiss()
                }
                .setNegativeButton("ยกเลิก") { d, _ -> d.dismiss() }
                .show()
        }

        val processBtn: Button = findViewById(R.id.process_order_button)
        processBtn.setOnClickListener {
            if (adapter.items.isEmpty()) {
                processBtn.startAnimation(animPulse)
                Toast.makeText(this, "ไม่มีรายการในตะกร้า", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            processBtn.startAnimation(animPulse)

            AlertDialog.Builder(this)
                .setTitle("ยืนยัน")
                .setMessage("ยืนยันการสั่งซื้อหรือไม่?")
                .setPositiveButton("ตกลง") { d, _ ->

                    val onEnd = object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            deleteAllOrders()
                            adapter.items.clear()
                            adapter.notifyDataSetChanged()
                            updateTotals()
                            Toast.makeText(this@activity_cart, "สั่งซื้อเสร็จสิ้น", Toast.LENGTH_SHORT).show()
                        }
                    }
                    animFadeDown.setAnimationListener(onEnd)
                    listViewAnimTarget?.startAnimation(animFadeDown)
                    paymentCard?.startAnimation(animFadeDown)

                    d.dismiss()
                }
                .setNegativeButton("ยกเลิก") { d, _ -> d.dismiss() }
                .show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun fetchOrders(): MutableList<OrderItem> {
        val dbHelper = SQLiteHelper(this)
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.query(
            SQLiteHelper.TABLE_NAME,
            arrayOf(SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_PRICE, SQLiteHelper.COLUMN_QTY),
            null,
            null,
            null,
            null,
            null
        )

        val orders = ArrayList<OrderItem>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_NAME))
            val price = cursor.getDouble(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_PRICE))
            val qty = try { cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_QTY)) } catch (e: Exception) { 1 }

            val imageResId = when (name) {
                "Savory Mushroom Pizza" -> R.drawable.pizza1
                "Classic Smash Burger" -> R.drawable.burger1
                "Miso Ramen" -> R.drawable.ramen
                "Tom Yum Kung" -> R.drawable.tomyumkung
                "Sushi Platter" -> R.drawable.shushi
                "Spaghetti Carbonara" -> R.drawable.spaghetti
                else -> R.drawable.logo
            }

            orders.add(OrderItem(id, name, price, qty, imageResId))
        }
        cursor.close()
        return orders
    }

    private fun updateTotals() {
        val subtotalView: TextView = findViewById(R.id.subtotal_text)
        val taxView: TextView = findViewById(R.id.tax_text)
        val totalView: TextView = findViewById(R.id.total_text)
        val processBtn: Button = findViewById(R.id.process_order_button)

        var subtotal = 0.0
        var itemCount = 0
        for (it in adapter.items) {
            subtotal += it.price * it.qty
            itemCount += it.qty
        }

        val tax = subtotal * 0.05
        val total = subtotal + tax

        subtotalView.text = String.format("ยอดรวมย่อย: ฿%.2f", subtotal)
        taxView.text = String.format("ภาษี (5%%): ฿%.2f", tax)
        totalView.text = String.format("รวมทั้งหมด: ฿%.2f", total)

        processBtn.text = String.format("สั่งซื้อ (%d รายการ)", itemCount)
    }

    private fun updateQtyInDb(id: Int, qty: Int) {
        val dbHelper = SQLiteHelper(this)
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply { put(SQLiteHelper.COLUMN_QTY, qty) }
        db.update(SQLiteHelper.TABLE_NAME, values, "${SQLiteHelper.COLUMN_ID}=?", arrayOf(id.toString()))
    }

    private fun deleteAllOrders() {
        val dbHelper = SQLiteHelper(this)
        val db = dbHelper.writableDatabase
        db.delete(SQLiteHelper.TABLE_NAME, null, null)
    }

    inner class OrderAdapter(val items: MutableList<OrderItem>) : BaseAdapter() {
        override fun getCount(): Int = items.size
        override fun getItem(position: Int): Any = items[position]
        override fun getItemId(position: Int): Long = items[position].id.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(this@activity_cart).inflate(R.layout.list_item_with_image, parent, false)

            val item = items[position]

            val img: ImageView = view.findViewById(R.id.item_image)
            val nameTv: TextView = view.findViewById(R.id.item_name)
            val priceTv: TextView = view.findViewById(R.id.item_price)
            val qtyVal: TextView = view.findViewById(R.id.qty_value)
            val plus: TextView = view.findViewById(R.id.qty_plus)
            val minus: TextView = view.findViewById(R.id.qty_minus)

            img.setImageResource(item.imageRes)
            nameTv.text = item.name
            priceTv.text = String.format("฿%.2f", item.price)
            qtyVal.text = item.qty.toString()

            plus.setOnClickListener {
                item.qty += 1
                qtyVal.text = item.qty.toString()
                updateQtyInDb(item.id, item.qty)
                updateTotals()
            }

            minus.setOnClickListener {
                if (item.qty > 1) {
                    item.qty -= 1
                    qtyVal.text = item.qty.toString()
                    updateQtyInDb(item.id, item.qty)
                    updateTotals()
                }
            }

            return view
        }
    }
}