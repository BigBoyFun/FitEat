package com.policinski.dev.fiteat

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import androidx.collection.arrayMapOf
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.delate_layout.*
import kotlinx.android.synthetic.main.product_row_view.view.add_to_day_button_row
import kotlinx.android.synthetic.main.product_row_view.view.delate_product_fomr_db
import kotlinx.android.synthetic.main.product_row_view.view.favorite_status_imsge_row
import kotlinx.android.synthetic.main.product_row_view.view.product_carbo_row
import kotlinx.android.synthetic.main.product_row_view.view.product_fat_row
import kotlinx.android.synthetic.main.product_row_view.view.product_kcal_row
import kotlinx.android.synthetic.main.product_row_view.view.product_name_row
import kotlinx.android.synthetic.main.product_row_view.view.product_protein_row
import kotlinx.android.synthetic.main.product_row_view.view.product_row_weight_tv
import kotlinx.android.synthetic.main.product_row_view_copy.view.*

class MyAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var itemList = mutableListOf<Product>()
    var filterItem = mutableListOf<Product>()

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val viewBackground = itemView.view_background_cardView
        val name: TextView = itemView.product_name_row
        val manufacturer: TextView = itemView.product_manufacturer_row
        val kcal: TextView = itemView.product_kcal_row
        val carbo: TextView = itemView.product_carbo_row
        val fat: TextView = itemView.product_fat_row
        val pro: TextView = itemView.product_protein_row
        val weight: TextView = itemView.product_row_weight_tv
        val favorite: ImageView = itemView.favorite_status_imsge_row
        val delate: Button = itemView.delate_product_fomr_db
        val addToDay: Button = itemView.add_to_day_button_row
        val editProduct: Button = itemView.edit_product_bt
        val lastAddedWeight: TextView = itemView.last_added_weight
        var meal = 1
        lateinit var array: ArrayList<Button>
        private val MAIN_PREF = "MAIN_PREF"
        private val SUM_KCAL = "SumKcal"
        private val SUM_PRO = "SumPro"
        private val SUM_FAT = "SumFat"
        private val SUM_CARBO = "SumCarbo"
        private val PREF_MEAL_BREAKFAST = "PREF_BREAKFAST"
        private val PREF_MEAL_SECOND_BREAKFAST = "PREF_MEAL_SECOND_BREAKFAST"
        private val PREF_MEAL_DINNER = "PREF_MEAL_DINNER"
        private val PREF_MEAL_DESSERT = "PREF_MEAL_DESSERT"
        private val PREF_MEAL_TEA = "PREF_MEAL_TEA"
        private val PREF_MEAL_SUPPER = "PREF_MEAL_SUPPER"
        private val PREF_MEAL_SNACKS = "PREF_MEAL_SNACKS"
        private val PREF_MEAL_TRAINING = "PREF_MEAL_TRAINING"

        private val PREF_CURRENTLY_VIEWED_LIST = "PREF_CURRENTLY_VIEWED_LIST" //created for deleting product from specific meal(not from DB)

        private val deleteDialog = Dialog(itemView.context)

        fun bind(product: Product){

            val db = MyDatabaseHelper(itemView.context)
            val sharedPreferences = itemView.context.getSharedPreferences(MAIN_PREF,0)

            //show nutrients of current product
            name.text = product.name
            manufacturer.text = product.manufaacturer
            weight.text = "${product.weight}g."
            lastAddedWeight.text = "${product.lastAddedWeight}g."
            kcal.text = "Kc: ${product.kcal}"
            carbo.text = "Ca: ${product.carbo}"
            fat.text = "Fa: ${product.fat}"
            pro.text = "Pr: ${product.protein}"
            favorite.setImageResource(if (product.favorite == 1) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24) //set background for favorite status

            //find biggest nutrient and set background for view -> created for show user what kind off meal is this product
            val nutrientListOf = listOf<Double>(product.fat,product.carbo,product.protein)
//            when(nutrientListOf.indexOf(nutrientListOf.max())){
//                0 -> viewBackground.setBackgroundResource(R.drawable.rounded_frame_yelow)
//                1 -> viewBackground.setBackgroundResource(R.drawable.rounded_frame_purple)
//                2 -> viewBackground.setBackgroundResource(R.drawable.rounded_frame_blue)
//            }
            when(nutrientListOf.indexOf(nutrientListOf.max())){
                0 -> addToDay.setBackgroundResource(R.drawable.yelow_bt_shape)
                1 -> addToDay.setBackgroundResource(R.drawable.purple_bt_shape)
                2 -> addToDay.setBackgroundResource(R.drawable.blue_bt_shape)
            }

            //make product favorite or not
            favorite.setOnClickListener(){

                db.updateFavorite(product.id,product.favorite)

                if (product.favorite == 1){
                    favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    product.favorite = 0
                }else if (product.favorite == 0){
                    favorite.setImageResource(R.drawable.ic_baseline_favorite_24)
                    product.favorite = 1
                }
                db.updateFavorite(product.id,product.favorite)

            }

            //dialog - need permission for delete selected product from list
            delate.setOnClickListener{

                val deleteDialog = Dialog(itemView.context)
                deleteDialog.setContentView(R.layout.delate_layout)
                deleteDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                deleteDialog.delate_dialog_product_name_tv.text = product.name
                deleteDialog.delate_dialog_delate_but.setOnClickListener {
                    db.deleteProduct(product.id)
                    deleteDialog.dismiss()
                    itemList.remove(product)
                    notifyDataSetChanged()
                }
                deleteDialog.delate_but_cancel.setOnClickListener { deleteDialog.dismiss() }

                deleteDialog.show()

            }

            //remove from the currently viewed list
            delate.setOnLongClickListener{
                sharedPreferences.getString(PREF_CURRENTLY_VIEWED_LIST,"All")?.let { it1 ->
                    if (db.deleteProductFromList(it1, product.id)) {
                        itemList.remove(product)
                        notifyDataSetChanged()
                        Toast.makeText(itemView.context,"${product.name} is deleted from $it1",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(itemView.context,"${product.name} ca't be deleted from $it1 by long press.",Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            addToDay.setOnClickListener{

                val intent = Intent(itemView.context,AddProductToDayActivity::class.java)
                intent.putExtra("PRODUCT_NAME",product.name)
                ContextCompat.startActivity(itemView.context,intent,null)

            }

            editProduct.setOnClickListener {
                val intent = Intent(itemView.context, newProductActivity::class.java)
                intent.putExtra("EditProductName",product.name)
                ContextCompat.startActivities(itemView.context, arrayOf(intent),null)
            }

        }

        private fun readSelectedMealsByUser(): Map<Int, String> {

            val mealSelectedArray = arrayMapOf<Int, String>()

            val sharedPreferences = itemView.context.getSharedPreferences(MAIN_PREF,0)
            val breakfast = sharedPreferences.getBoolean(PREF_MEAL_BREAKFAST,true)
            val secondBreakfast = sharedPreferences.getBoolean(PREF_MEAL_SECOND_BREAKFAST,true)
            val dinner = sharedPreferences.getBoolean(PREF_MEAL_DINNER,true)
            val dessert = sharedPreferences.getBoolean(PREF_MEAL_DESSERT,true)
            val tea = sharedPreferences.getBoolean(PREF_MEAL_TEA,true)
            val supper = sharedPreferences.getBoolean(PREF_MEAL_SUPPER,true)
            val snacks = sharedPreferences.getBoolean(PREF_MEAL_SNACKS,true)
            val training = sharedPreferences.getBoolean(PREF_MEAL_TRAINING,true)

            if (breakfast) mealSelectedArray[1] = "${R.string.breakfast_1}"
            if (secondBreakfast) mealSelectedArray[2] = "${R.string.second_breakfast_2}"
            if (dinner) mealSelectedArray[3] = "${R.string.dinner_3}"
            if (dessert) mealSelectedArray[4] = "${R.string.dessert_4}"
            if (tea) mealSelectedArray[5] = "${R.string.tea_5}"
            if (supper) mealSelectedArray[6] = "${R.string.supper_6}"
            if (snacks) mealSelectedArray[7] = "${R.string.snacks_7}"
            if (training) mealSelectedArray[8] = "${R.string.training_8}"

            return mealSelectedArray
        }
    }

    fun submitList(list: MutableList<Product>){
        itemList = list
        filterItem = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_row_view_copy,parent,false))
    }

    override fun getItemCount(): Int {
        return filterItem.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){

            is ViewHolder -> {holder.bind(filterItem.get(position))}

        }
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var charString: String = constraint.toString()
                if (charString.isEmpty()){
                    filterItem = itemList
                }
                else{
                    var filterList: MutableList<Product> = mutableListOf()

                    for (s: Product in itemList){

                        if (s.name.toLowerCase().contains(charString.toLowerCase())){
                            filterList.add(s)
                        }
                    }
                    filterItem = filterList
                }

                var filterResult = FilterResults()
                filterResult.values = filterItem
                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterItem = results!!.values as MutableList<Product>
                notifyDataSetChanged()
            }

        }
    }
}