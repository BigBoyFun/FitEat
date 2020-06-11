package com.policinski.dev.fiteat

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.add_dialog_layout.*
import kotlinx.android.synthetic.main.add_product_to_day_layout.*
import kotlinx.android.synthetic.main.fragment_products_fragment.view.*
import kotlinx.android.synthetic.main.fragment_products_fragment.view.current_kcal
import java.time.LocalDate

class products_fragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var adapterProduct: MyAdapter
    private lateinit var productList: MutableList<Product>
    private val MAIN_PREF = "MAIN_PREF"
    private val PREF_CARBO = "PREF_CARBO"
    private val PREF_FAT = "PREF_FAT"
    private val PREF_KCAL = "PREF_KCAL"
    private val PREF_PRO = "PREF_PRO"
    private var current_kcal = 0
    private var current_pro = 0.0
    private var current_fat = 0.0
    private var current_carbo = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {

        }

        productList = mutableListOf()
        val dbManager = MyDatabaseHelper(requireContext())

        val cursor = dbManager.readAllData()

        if (cursor.moveToNext()){

            val name: String
            val kcal: Double
            val fat: Double
            val protein: Double
            val carbo: Double
            val favorite: Int
            val id: Int

            do{

                val name = cursor.getString(cursor.getColumnIndex("Name"))                   //
                val kcal = cursor.getInt(cursor.getColumnIndex("Kcal"))                         //
                val fat = cursor.getDouble(cursor.getColumnIndex("Fat"))                    //
                val protein = cursor.getDouble(cursor.getColumnIndex("Protein"))            // wyciągnietę z bloku do w celu sprawdzenia czy będzie działąć i czy poprawi siewydajmość
                val carbo = cursor.getDouble(cursor.getColumnIndex("Carbohydrates"))        //
                val weight = cursor.getInt(cursor.getColumnIndex("Weight"))
                val favorite = cursor.getInt(cursor.getColumnIndex("Favorite"))                 //
                val id = cursor.getInt(cursor.getColumnIndex("ID_pro"))                         //

                productList.add(Product(name,kcal,protein,carbo,fat,weight,favorite, id))

            }while (cursor.moveToNext())
        }

        var groupSort = this.productList.groupBy { it.favorite }
        var map1 = groupSort[1]?.sortedBy { it.Name }
        var map2 = groupSort[0]?.sortedBy { it.Name }
        productList.clear()
        if (map1 != null) {
            productList.addAll(map1)
        }
        if (map2 != null) {
            productList.addAll(map2)
        }

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_products_fragment, container, false)

        //read any product added to current day and calculate all nutrients
        val nutrientsSumArray = dbManager.readDayTable(data.toString())

        view.current_kcal.text = nutrientsSumArray[0].toInt().toString()
        view.current_pro.text = nutrientsSumArray[1].toString()
        view.current_fat.text = nutrientsSumArray[2].toString()
        view.current_carbo.text = nutrientsSumArray[3].toString()

        //show dialog in you can add new product to data base
        view.floatingActionButton.setOnClickListener { addNewProductDialog() }

        //searching product recycle list view
        view.searchView.setOnQueryTextListener(this)

        view.products_recycle_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapterProduct = MyAdapter()
            adapter = adapterProduct

        }

        adapterProduct.submitList(productList)

        val kcalUserPref = view.kcal_user_pref
        val fatUserPref = view.fat_uset_pref
        val proUserPref = view.pro_user_pref
        val carboUserPref = view.carbo_user_pref

        view.settings_nutrients.setOnClickListener{
            nutrientsSettingsDialog(kcalUserPref, fatUserPref, proUserPref,carboUserPref)
        }

        readData(kcalUserPref,fatUserPref,proUserPref,carboUserPref)

        return view
    }

    private fun nutrientsSettingsDialog(
        kcalUserPref: TextView,
        fatUserPref: TextView,
        proUserPref: TextView,
        carboUserPref: TextView
    ) {
        val nutrientsSettingsDialog = Dialog(requireContext())
        nutrientsSettingsDialog.setContentView(R.layout.nutrients_settings_dialog)
        nutrientsSettingsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var kcal = nutrientsSettingsDialog.findViewById<EditText>(R.id.kcal_user_pref_et)
        var carbo = nutrientsSettingsDialog.findViewById<EditText>(R.id.carbo_user_pref_et)
        var fat = nutrientsSettingsDialog.findViewById<EditText>(R.id.fat_user_pref_et)
        var pro = nutrientsSettingsDialog.findViewById<EditText>(R.id.pro_user_pref_et)

        val saveBt = nutrientsSettingsDialog.findViewById<Button>(R.id.nutrients_save_data_bt)
        saveBt.setOnClickListener{

            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(MAIN_PREF, 0 )
            val edit = sharedPreferences.edit()

            edit.putLong(PREF_KCAL, kcal.text.toString().toLong())
            edit.putLong(PREF_CARBO, carbo.text.toString().toLong())
            edit.putLong(PREF_FAT,fat.text.toString().toLong())
            edit.putLong(PREF_PRO,pro.text.toString().toLong())
            edit.apply()

            nutrientsSettingsDialog.let {
                it.dismiss()
                readData(kcalUserPref,fatUserPref,proUserPref,carboUserPref)
            }
        }

        nutrientsSettingsDialog.show()
    }

    private fun readData(
        kcalUserPref: TextView,
        fatUserPref: TextView,
        proUserPref: TextView,
        carboUserPref: TextView
    ) {

        val shader = requireContext().getSharedPreferences(MAIN_PREF,0)
        kcalUserPref.text = shader?.getLong(PREF_KCAL,0).toString()
        fatUserPref.text = shader?.getLong(PREF_FAT,0).toString()
        carboUserPref.text = shader?.getLong(PREF_CARBO,0).toString()
        proUserPref.text = shader?.getLong(PREF_PRO,0).toString()

    }
    private fun addNewProductDialog(){

        this.onPause()

        val myDataBase: MyDatabaseHelper = MyDatabaseHelper(requireContext())

        var addDialog: Dialog = Dialog(requireContext())

        var exist:Boolean = false

        addDialog.setContentView(R.layout.add_dialog_layout)
            addDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var name_new_product = addDialog.findViewById<EditText>(R.id.name_new_product)
        var kcal_new_product = addDialog.findViewById<EditText>(R.id.kcal_new_product)
        var protein_new_product = addDialog.findViewById<EditText>(R.id.protein_new_product)
        var carbo_new_product = addDialog.findViewById<EditText>(R.id.carbo_new_product)
        var fat_new_product = addDialog.findViewById<EditText>(R.id.fa_new_product)
        var weight_new_product = addDialog.weight_new_product
        var favorite_chaek: CheckBox = addDialog.findViewById(R.id.favorite_new_product)
        val register = addDialog.findViewById<Button>(R.id.register_new_product)
        val cancel = addDialog.findViewById<Button>(R.id.cancel_new_product)

        register.setOnClickListener {

            val newProduct = Product(
                name_new_product.text.toString().capitalize(),
                kcal_new_product.text.toString().toInt(),
                protein_new_product.text.toString().toDouble(),
                carbo_new_product.text.toString().toDouble(),
                fat_new_product.text.toString().toDouble(),
                weight_new_product.text.toString().toInt(),
                if (favorite_chaek.isChecked) 1 else 0,
                0
            )

            for (product in productList) {
                if (newProduct.Name.equals(product.Name)){
                    exist = true
                    break
                }else{
                    exist = false
                }
            }

            if (exist){
                Toast.makeText(requireContext(),"Already exist!",Toast.LENGTH_SHORT).show()
            }else if (!exist){
                myDataBase.insertData(newProduct).let { it -> if (it) addDialog.dismiss();productList.let {  it.add(newProduct); it.groupBy { it.Name }}; refreshFragment(); onStart()}

            }
        }

        //add function to cancel Button
        cancel.setOnClickListener {v -> addDialog.dismiss() }

        addDialog.show()

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        adapterProduct.filter.filter(newText)

        return true
    }

    fun refreshFragment(){

        adapterProduct!!.notifyDataSetChanged()
    }


}
