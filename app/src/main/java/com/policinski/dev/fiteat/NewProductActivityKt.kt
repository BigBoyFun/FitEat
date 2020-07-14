package com.policinski.dev.fiteat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.create_product_layout.*

class newProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_product)

        createNewProduct()

    }

    private fun createNewProduct(){

        this.onPause()

        val myDataBase: MyDatabaseHelper = MyDatabaseHelper(this)

        var exist:Boolean = false

        var name_new_product = name_new_product
        var kcal_new_product = kcal_new_product
        var protein_new_product = protein_new_product
        var carbo_new_product = carbo_new_product
        var fat_new_product = fa_new_product
        var weight_new_product = weight_new_product
        var favorite_chaek: CheckBox = favorite_new_product
        val register = register_new_product
        val cancel = cancel_new_product

        val productList = arrayListOf<Product>()
        val dbManager = MyDatabaseHelper(this)

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
        }

        register.setOnClickListener {

            if (name_new_product.length() == 0) name_new_product.setText("Item")
            if (kcal_new_product.length() == 0) kcal_new_product.setText("0")
            if (protein_new_product.length() == 0) protein_new_product.setText("0")
            if (carbo_new_product.length() == 0) carbo_new_product.setText("0")
            if (fat_new_product.length() == 0) fat_new_product.setText("0")
            if (weight_new_product.length() == 0) weight_new_product.setText("0")

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
                if (newProduct.name.equals(product.name)){
                    exist = true
                    break
                }else{
                    exist = false
                }
            }

            if (exist){
                Toast.makeText(this,getString(R.string.already_exist), Toast.LENGTH_SHORT).show()
            }else if (!exist){
                Toast.makeText(this,getString(R.string.product_registered), Toast.LENGTH_SHORT).show()
                val intent = Intent()
                myDataBase.insertData(newProduct)
                    .let { it -> if (it)
                        intent.putExtra("newProductCreated",it)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }

            }

        }

        //add function to cancel Button
        cancel.setOnClickListener {
            super.onBackPressed()
        }

    }
}