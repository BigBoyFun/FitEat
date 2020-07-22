package com.policinski.dev.fiteat

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.delate_layout.*
import kotlinx.android.synthetic.main.delate_layout.view.*
import kotlinx.android.synthetic.main.fragment_products_fragment.view.*
import kotlinx.android.synthetic.main.fragment_products_fragment.view.current_kcal
import java.time.LocalDate

class products_fragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var adapterProduct: MyAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var dbManager: MyDatabaseHelper
    private val MAIN_PREF = "MAIN_PREF"
    private val PREF_CARBO = "PREF_CARBO"
    private val PREF_FAT = "PREF_FAT"
    private val PREF_KCAL = "PREF_KCAL"
    private val PREF_PRO = "PREF_PRO"
    private var current_kcal = 0
    private var current_pro = 0.0
    private var current_fat = 0.0
    private var current_carbo = 0.0
    lateinit var date: Any

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {

        }

        productList = mutableListOf()

        loadProdactsFromDataBase()

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_products_fragment, container, false)

        //read any product added to current day and calculate all nutrients
        val nutrientsSumArray = dbManager.readDayTable(date.toString())

        view.current_kcal.text = nutrientsSumArray[0].toInt().toString()
        view.current_pro.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
        view.current_fat.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
        view.current_carbo.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')

        //show dialog in you can add new product to data base
        view.floatingActionButton.setOnClickListener {
            //createProductDialog()
            val intent: Intent = Intent(requireContext(), newProductActivity::class.java) //FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK
            startActivityForResult(intent,100,null)

            }

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

        readData(kcalUserPref,fatUserPref,proUserPref,carboUserPref)

        return view
    }

    override fun onResume() {
        super.onResume()
        val view = this.view

        //read any product added to current day and calculate all nutrients
        val nutrientsSumArray = dbManager.readDayTable(date.toString())

        view?.current_kcal?.text = nutrientsSumArray[0].toInt().toString()
        view?.current_pro?.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
        view?.current_fat?.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
        view?.current_carbo?.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')

        Handler().postDelayed({
            productList.clear()
            Toast.makeText(requireContext(), "Fragment has beet refreshed", Toast.LENGTH_SHORT).show()
            loadProdactsFromDataBase()
            refreshFragment()
        },100)


    }

    private fun loadProdactsFromDataBase() {

        if(productList.size > 0) productList.clear()

        dbManager = MyDatabaseHelper(requireContext())
        val cursor = dbManager.readAllData()

        if (cursor.moveToNext()){

            var name: String
            var kcal: Int
            var fat: Double
            var protein: Double
            var carbo: Double
            var weight: Int
            var favorite: Int
            var id: Int

            do{

                name = cursor.getString(cursor.getColumnIndex("Name"))
                kcal = cursor.getInt(cursor.getColumnIndex("Kcal"))
                fat = cursor.getDouble(cursor.getColumnIndex("Fat"))
                protein = cursor.getDouble(cursor.getColumnIndex("Protein"))
                carbo = cursor.getDouble(cursor.getColumnIndex("Carbohydrates"))
                weight = cursor.getInt(cursor.getColumnIndex("Weight"))
                favorite = cursor.getInt(cursor.getColumnIndex("Favorite"))
                id = cursor.getInt(cursor.getColumnIndex("ID_pro"))

                productList.add(Product(name,kcal,protein,carbo,fat,weight,favorite, id))

            }while (cursor.moveToNext())

            var groupSort = this.productList.groupBy { it.favorite }
            var map1 = groupSort[1]?.sortedBy { it.name }
            var map2 = groupSort[0]?.sortedBy { it.name }
            productList.clear()
            if (map1 != null) {
                productList.addAll(map1)
            }
            if (map2 != null) {
                productList.addAll(map2)
            }

        }
    }

    private fun readData(
        kcalUserPref: TextView,
        fatUserPref: TextView,
        proUserPref: TextView,
        carboUserPref: TextView
    ) {

        val shader = requireContext().getSharedPreferences(MAIN_PREF,0)
        kcalUserPref.text = shader?.getInt(PREF_KCAL,0).toString()
        fatUserPref.text = shader?.getInt(PREF_FAT,0).toString()
        carboUserPref.text = shader?.getInt(PREF_CARBO,0).toString()
        proUserPref.text = shader?.getInt(PREF_PRO,0).toString()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data?.getBooleanExtra("newProductCreated",false) == true) {
                Handler().postDelayed({
                    productList.clear()
                    Toast.makeText(requireContext(), "Fragment has beet refreshed", Toast.LENGTH_SHORT).show()
                    loadProdactsFromDataBase()
                    refreshFragment()
                },100)
            }
            Handler().postDelayed({
                Toast.makeText(context,"REFRESH ACTIVITY", Toast.LENGTH_SHORT).show()
                //read any product added to current day and calculate all nutrients
                val nutrientsSumArray = dbManager.readDayTable(date.toString())

                view?.current_kcal?.text = nutrientsSumArray[0].toInt().toString()
                view?.current_pro?.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
                view?.current_fat?.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
                view?.current_carbo?.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')
            },2000)
        }

    }
}
