package com.policinski.dev.fiteat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.activity_new_product.*

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

        var nameNewProduct = name_new_product
        var kcalNewProduct = kcal_new_product
        var proteinNewProduct = protein_new_product
        var carboNewProduct = carbo_new_product
        var fatNewProduct = fa_new_product
        var weightNewProduct = weight_new_product
        var favoriteChaek: CheckBox = favorite_new_product
        var sumKcalSample = sum_kcal_sample
        val register = register_new_product
        val cancel = cancel_new_product
        var leftKcal = 0.0
        var leftWeight = 0.0

//        weightNewProduct.setSelection(weightNewProduct.text.length)
        weightNewProduct.append("100")

        if (kcalNewProduct.length() <= 0){
            fatNewProduct.isEnabled = false
            carboNewProduct.isEnabled = false
            proteinNewProduct.isEnabled = false
        }else{
            fatNewProduct.isEnabled = true
            carboNewProduct.isEnabled = true
            proteinNewProduct.isEnabled = true
        }

        kcalNewProduct.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                leftKcal = 0.0
                leftWeight = 0.0

                fatNewProduct?.setText("")
                carboNewProduct?.setText("")
                proteinNewProduct?.setText("")

                if (kcalNewProduct.length() <= 0){
                    fatNewProduct.isEnabled = false
                    carboNewProduct.isEnabled = false
                    proteinNewProduct.isEnabled = false
                }else{
                    fatNewProduct.isEnabled = true
                    carboNewProduct.isEnabled = true
                    proteinNewProduct.isEnabled = true
                }

            }

        })

        weightNewProduct.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                leftKcal = 0.0
                leftWeight = 0.0

                fatNewProduct?.setText("")
                carboNewProduct?.setText("")
                proteinNewProduct?.setText("")
                kcalNewProduct?.setText("")

            }

        })

        carboNewProduct.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                leftKcal = (if (carboNewProduct.text?.isEmpty()!!) {0.0 * 4.0} else carboNewProduct.text.toString().toDouble() * 4.0) +
                        (if (fatNewProduct.text?.isEmpty()!!){0.0 * 9.0} else fatNewProduct.text.toString().toDouble() * 9.0) +
                        (if (proteinNewProduct.text?.isEmpty()!!){0.0 * 4.0} else proteinNewProduct.text.toString().toDouble() * 4.0)

                leftWeight = if (carboNewProduct.text?.isEmpty()!!) {0.0} else(carboNewProduct.text.toString().toDouble()) +
                        if (fatNewProduct.text?.isEmpty()!!) { 0.0 } else fatNewProduct.text.toString().toDouble() +
                        if (proteinNewProduct.text?.isEmpty()!!) { 0.0 } else proteinNewProduct.text.toString().toDouble()

                if (leftKcal > if (kcalNewProduct.length() <= 0){0.0} else kcalNewProduct.text.toString().toDouble()){
                    if (carboNewProduct.isFocused) {
                        carboNewProduct.setText("")
                    }
                    Toast.makeText(this@newProductActivity, "The sum of calories from carbohydrates, fats and proteins may not exceed the declared amount of calories in the product.", Toast.LENGTH_SHORT).show()
                }
                if (leftWeight > if (weightNewProduct.length() <= 0) { 0.0 } else weightNewProduct.text.toString().toDouble()){
                    if (carboNewProduct.isFocused) {
                        carboNewProduct.setText("")
                    }
                    Toast.makeText(this@newProductActivity, "the sum of carbohydrates, fats and proteins must not exceed the declared weight.", Toast.LENGTH_SHORT).show()
                }

                sumKcalSample.setText("$leftKcal")

            }

        })

        fatNewProduct.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                leftKcal = (if (carboNewProduct.text?.isEmpty()!!) {0.0 * 4.0} else carboNewProduct.text.toString().toDouble() * 4.0) +
                        (if (fatNewProduct.text?.isEmpty()!!){0.0 * 9.0} else fatNewProduct.text.toString().toDouble() * 9.0) +
                        (if (proteinNewProduct.text?.isEmpty()!!){0.0 * 4.0} else proteinNewProduct.text.toString().toDouble() * 4.0)

                leftWeight = if (carboNewProduct.text?.isEmpty()!!) {0.0} else(carboNewProduct.text.toString().toDouble()) +
                        if (fatNewProduct.text?.isEmpty()!!) { 0.0 } else fatNewProduct.text.toString().toDouble() +
                                if (proteinNewProduct.text?.isEmpty()!!) { 0.0 } else proteinNewProduct.text.toString().toDouble()

                if (leftKcal > if (kcalNewProduct.length() <= 0){0.0} else kcalNewProduct.text.toString().toDouble()){
                    if (fatNewProduct.isFocused) {
                        fatNewProduct.setText("")
                    }
                    Toast.makeText(this@newProductActivity, "The sum of calories from carbohydrates, fats and proteins may not exceed the declared amount of calories in the product.", Toast.LENGTH_SHORT).show()
                }
                if (leftWeight > if (weightNewProduct.length() <= 0) { 0.0 } else weightNewProduct.text.toString().toDouble()){
                    if (fatNewProduct.isFocused) {
                        fatNewProduct.setText("")
                    }
                    Toast.makeText(this@newProductActivity, "the sum of carbohydrates, fats and proteins must not exceed the declared weight.", Toast.LENGTH_SHORT).show()
                }

                sumKcalSample.setText("$leftKcal")

            }

        })

        proteinNewProduct.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                leftKcal = (if (carboNewProduct.text?.isEmpty()!!) {0.0 * 4.0} else carboNewProduct.text.toString().toDouble() * 4.0) +
                        (if (fatNewProduct.text?.isEmpty()!!){0.0 * 9.0} else fatNewProduct.text.toString().toDouble() * 9.0) +
                        (if (proteinNewProduct.text?.isEmpty()!!){0.0 * 4.0} else proteinNewProduct.text.toString().toDouble() * 4.0)

                leftWeight = if (carboNewProduct.text?.isEmpty()!!) {0.0} else(carboNewProduct.text.toString().toDouble()) +
                        if (fatNewProduct.text?.isEmpty()!!) { 0.0 } else fatNewProduct.text.toString().toDouble() +
                                if (proteinNewProduct.text?.isEmpty()!!) { 0.0 } else proteinNewProduct.text.toString().toDouble()

                if (leftKcal > if (kcalNewProduct.length() <= 0) { 0.0 } else kcalNewProduct.text.toString().toDouble()){
                    if (proteinNewProduct.isFocused) {
                        proteinNewProduct.setText("")
                    }
                    Toast.makeText(this@newProductActivity, "The sum of calories from carbohydrates, fats and proteins may not exceed the declared amount of calories in the product.", Toast.LENGTH_SHORT).show()
                }
                if (leftWeight > if (weightNewProduct.length() <= 0) { 0.0 } else weightNewProduct.text.toString().toDouble()){
                    if (proteinNewProduct.isFocused) {
                        proteinNewProduct.setText("")
                    }
                    Toast.makeText(this@newProductActivity, "the sum of carbohydrates, fats and proteins must not exceed the declared weight.", Toast.LENGTH_SHORT).show()
                }

                sumKcalSample.setText("$leftKcal")

            }

        })


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

            if (nameNewProduct.length() == 0) nameNewProduct.setText("Item")
            if (kcalNewProduct.length() == 0) kcalNewProduct.setText("0")
            if (proteinNewProduct.length() == 0) proteinNewProduct.setText("0")
            if (carboNewProduct.length() == 0) carboNewProduct.setText("0")
            if (fatNewProduct.length() == 0) fatNewProduct.setText("0")
            if (weightNewProduct.length() == 0) weightNewProduct.setText("0")

            val newProduct = Product(
                nameNewProduct.text.toString().capitalize(),
                kcalNewProduct.text.toString().toInt(),
                proteinNewProduct.text.toString().toDouble(),
                carboNewProduct.text.toString().toDouble(),
                fatNewProduct.text.toString().toDouble(),
                weightNewProduct.text.toString().toInt(),
                if (favoriteChaek.isChecked) 1 else 0,
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