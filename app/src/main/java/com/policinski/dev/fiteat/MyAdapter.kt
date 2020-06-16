package com.policinski.dev.fiteat

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_product_to_day_layout.*
import kotlinx.android.synthetic.main.delate_layout.*
import kotlinx.android.synthetic.main.product_row_view.view.*
import java.time.LocalDate

class MyAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var itemList = mutableListOf<Product>()
    var filterItem = mutableListOf<Product>()

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        val name: TextView = itemView.product_name_row
        val kcal: TextView = itemView.product_kcal_row
        val carbo: TextView = itemView.product_carbo_row
        val fat: TextView = itemView.product_fat_row
        val pro: TextView = itemView.product_protein_row
        val weight: TextView = itemView.product_row_weight_tv
        val favorite: ImageView = itemView.favorite_status_imsge_row
        val delate: Button = itemView.delate_product_fomr_db
        val addToDay: Button = itemView.add_to_day_button_row
        var meal = 1
        lateinit var array: ArrayList<Button>
        val MAIN_PREF = "MAIN_PREF"
        val SUM_KCAL = "SumKcal"
        val SUM_PRO = "SumPro"
        val SUM_FAT = "SumFat"
        val SUM_CARBO = "SumCarbo"

        private val deleteDialog = Dialog(itemView.context)

        fun bind(product: Product){

            val db = MyDatabaseHelper(itemView.context)

            //show nutrients of current product
            name.text = product.name
            weight.text = "${product.weight}g."
            kcal.text = "Kc: ${product.kcal}"
            carbo.text = "Ca: ${product.carbo}"
            fat.text = "Fa: ${product.fat}"
            pro.text = "Pr: ${product.protein}"
            favorite.setImageResource(if (product.favorite == 1) R.drawable.ic_baseline_favorite_24 else R.drawable.ic_baseline_favorite_border_24) //set background for favorite status

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
                deleteDialog.delate_dialog_delate_but.setOnClickListener { db.deleteProduct(product.id); deleteDialog.dismiss()}
                deleteDialog.delate_but_cancel.setOnClickListener { deleteDialog.dismiss() }

                deleteDialog.show()

            }

            addToDay.setOnClickListener{
                val dialog = Dialog(itemView.context)
                dialog.setContentView(R.layout.add_product_to_day_layout)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                //init views
                val productName = dialog.product_name
                var input_weight = dialog.input_weight
                var calculateKcal = dialog.calculate_kcal
                var calculatePro = dialog.calculate_pro
                var calculateFat = dialog.calculate_fat
                var calculateCarbo = dialog.calculate_carbo
                var read_weight_tv = dialog.read_weight_tv
                val seekBar = dialog.seekBar
                val onBt = dialog.ok_bt
                val cancelBt = dialog.cancel_bt
                array = arrayListOf<Button>(dialog.button_1,
                    dialog.button_2,
                    dialog.button_3,
                    dialog.button_4,
                    dialog.button_5,
                    dialog.button_6,
                    dialog.button_7,
                    dialog.button_8)


                for (item in array) item.setOnClickListener(this)

                //set text on all textViews and progress on seekBar, by current product specification
                productName.text = product.name
                calculateKcal.text = product.kcal.toString()
                calculatePro.text = product.protein.toString()
                calculateFat.text = product.fat.toString()
                calculateCarbo.text = product.carbo.toString()
                input_weight.setText("${product.weight}")
                read_weight_tv.text = "${product.weight}"
                seekBar.progress = product.weight

                // setting progress on seekBar by writing in editText
                input_weight.addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s.toString().isEmpty()){
                            seekBar.progress = 0
                            read_weight_tv.text = "0 g"
                        }else {
                            seekBar.progress = s.toString().toInt()
                            if (s.toString().toLong() > 300){
                                read_weight_tv.text = "300 g"
                            }else {
                                read_weight_tv.text = "$s g"
                            }
                        }

                    }

                })

                //calculate nutrients by change seekBar state
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        read_weight_tv.setText("$progress g")
                        calculateKcal.text = ((product.kcal * progress) / product.weight).toString()
                        calculatePro.text = "%.2f".format((product.protein * progress) / product.weight).replace(',','.')
                        calculateFat.text = "%.2f".format((product.fat * progress) / product.weight).replace(',','.')
                        calculateCarbo.text = "%.2f".format((product.carbo * progress) / product.weight).replace(',','.')
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }

                })

                val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.now().toString()
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

                //add meal to current day and close dialog
                dialog.ok_bt.setOnClickListener{
                    db.addProductToDay(data,
                        product.name,
                        calculateKcal.text.toString().toInt(),
                        calculatePro.text.toString().replace(',','.').toDouble(),
                        calculateCarbo.text.toString().replace(',','.').toDouble(),
                        calculateFat.text.toString().replace(',','.').toDouble(),
                        meal,seekBar.progress)
                    dialog.dismiss()}

                //close dialog with no adding meal to current day
                dialog.cancel_bt.setOnClickListener{dialog.dismiss()}

                dialog.show()
            }

        }

        override fun onClick(v: View?) {
            v as Button
            meal = v.text.toString().toInt()

            for (item in array){
                if (item.text.toString().toInt() != meal){
                    item.setBackgroundResource(R.drawable.gray_bt_shape)
                }else{
                    item.setBackgroundResource(R.drawable.green_bt_shape)
                }
            }

        }
    }

    fun submitList(list: MutableList<Product>){
        itemList = list
        filterItem = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.product_row_view,parent,false))
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