package com.policinski.dev.fiteat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_new_product.*
import java.util.*

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

        var titleTv = title_tv
        var nameNewProduct = name_new_product
        var manufacturerNewProduct = manufacturer_new_product
        var kcalNewProduct = kcal_new_product
        var proteinNewProduct = protein_new_product
        var carboNewProduct = carbo_new_product
        var fatNewProduct = fat_new_product
        var weightNewProduct = weight_new_product
        var favoriteChaek: CheckBox = favorite_new_product
        var sumKcalSample = sum_kcal_sample
        val register = register_new_product
        val cancel = cancel_new_product
        var leftKcal = 0.0
        var leftWeight = 0.0
        val but100 = but_100g
        val portion = but_portion
        val weightTv = textView13
        val cb_calculate_portion = checkBox_calculate_portion
        val et_porion_value = et_portion_value
        val breakfast_but_suggest_1 = breakfast_but_suggestion_1
        val second_breakfastbut_suggestion_2 = seccond_breakfast_but_suggestion_2
        val dinner_but_suggestion_3 = dinner_but_suggestion_3
        val dessert_but_suggestion_4 = dessert_but_suggestion_4
        val tea_but_suggestion_5 = tea_but_suggestion_5
        val supper_but_suggestion_6 = supper_but_suggestion_6
        val snacks_but_suggestion_7 = snacks_but_suggestion_7
        val training_but_suggestion_8 = training_but_suggestion_8

        val listOfSuggestionButtons = listOf<Button>(breakfast_but_suggest_1,
        second_breakfastbut_suggestion_2,dinner_but_suggestion_3,dessert_but_suggestion_4,
        tea_but_suggestion_5,supper_but_suggestion_6,snacks_but_suggestion_7,training_but_suggestion_8)

        val listOfSuggestionButtonState = mutableListOf<Int>(0,0,0,0,0,0,0,0)

        var productForEdit = Product()
        val editProduct = intent.getStringExtra("EditProductName")

        if (editProduct != null && editProduct.isNotEmpty()){

            but_portion.setBackgroundResource(R.drawable.selected_frame_shape_but)
            but_100g.setBackgroundResource(R.drawable.frame_shape_but)
            weightNewProduct.visibility = View.VISIBLE
            weightTv.visibility = View.VISIBLE
            cb_calculate_portion.isEnabled = false

            productForEdit = myDataBase.findEditedProductByName(editProduct)

            titleTv.text = "${productForEdit.name}"
            nameNewProduct.setText(productForEdit.name)
            manufacturerNewProduct.setText(productForEdit.manufaacturer)
            weightNewProduct.setText(productForEdit.weight.toString())
            kcalNewProduct.setText(productForEdit.kcal.toString())
            fatNewProduct.setText(productForEdit.fat.toString())
            carboNewProduct.setText(productForEdit.carbo.toString())
            proteinNewProduct.setText(productForEdit.protein.toString())
            favoriteChaek.isChecked = if(productForEdit.favorite == 1) true else false
            listOfSuggestionButtonState[0] = productForEdit.breakfast
            listOfSuggestionButtonState[1] = productForEdit.secondBreakfast
            listOfSuggestionButtonState[2] = productForEdit.dinner
            listOfSuggestionButtonState[3] = productForEdit.dessert
            listOfSuggestionButtonState[4] = productForEdit.tea
            listOfSuggestionButtonState[5] = productForEdit.supper
            listOfSuggestionButtonState[6] = productForEdit.snacks
            listOfSuggestionButtonState[7] = productForEdit.training

            listOfSuggestionButtonState.forEachIndexed { index, i -> if (i > 0) listOfSuggestionButtons[index].setBackgroundResource(R.drawable.selected_frame_shape_but) }

            register.text = getString(R.string.save)

            println("======================================== ${productForEdit.id} ========================================")
        } else {
            Toast.makeText(this,"NOT FOUND",Toast.LENGTH_SHORT).show()
            weightTv.visibility = View.GONE
            weightNewProduct.visibility = View.INVISIBLE

            weightNewProduct.append("100")
        }

        listOfSuggestionButtons.forEach { it.setOnClickListener { v ->
            if (Objects.equals(v.background.constantState, this.resources.getDrawable(R.drawable.frame_shape_but,null).constantState)) {
                v.setBackgroundResource(R.drawable.selected_frame_shape_but)
                listOfSuggestionButtonState[listOfSuggestionButtons.indexOf(it)] = 1
            } else {
                v.setBackgroundResource(R.drawable.frame_shape_but)
                listOfSuggestionButtonState[listOfSuggestionButtons.indexOf(it)] = 0
            }
        }
        }

        but100.setOnClickListener {
            it.setBackgroundResource(R.drawable.selected_frame_shape_but)
            portion.setBackgroundResource(R.drawable.frame_shape_but)
            weightNewProduct.setText("100")
            weightNewProduct.visibility = View.INVISIBLE
            weightTv.visibility = View.GONE
            cb_calculate_portion.isEnabled = true
        }

        but_portion.setOnClickListener {
            it.setBackgroundResource(R.drawable.selected_frame_shape_but)
            but_100g.setBackgroundResource(R.drawable.frame_shape_but)
//            weightNewProduct.setText("")
            weightNewProduct.visibility = View.VISIBLE
            weightNewProduct.setText("")
            weightTv.visibility = View.VISIBLE
            cb_calculate_portion.isEnabled = false
        }

        if (kcalNewProduct.length() <= 0){
            fatNewProduct.isEnabled = false
            carboNewProduct.isEnabled = false
            proteinNewProduct.isEnabled = false
        }else{
            fatNewProduct.isEnabled = true
            carboNewProduct.isEnabled = true
            proteinNewProduct.isEnabled = true
        }

        fatNewProduct.setOnFocusChangeListener {view, b ->
            if (fatNewProduct.length() == 0 && carboNewProduct.length() > 0 && proteinNewProduct.length() > 0){
                val calcFat = (kcalNewProduct.text.toString().toDouble() - leftKcal) / 9.0
                fatNewProduct.setText("$calcFat")
        }}

        carboNewProduct.setOnFocusChangeListener {view, b ->
            if (carboNewProduct.length() == 0 && proteinNewProduct.length() > 0 && fatNewProduct.length() > 0){
                val calcCarbo = (kcalNewProduct.text.toString().toDouble() - leftKcal) / 4.0
                carboNewProduct.setText("$calcCarbo")
            }
        }

        proteinNewProduct.setOnFocusChangeListener { view, b ->

            if (proteinNewProduct.length() == 0 && carboNewProduct.length() > 0 && fatNewProduct.length() > 0){
                val calcPro = (kcalNewProduct.text.toString().toDouble() - leftKcal) / 4.0
                proteinNewProduct.setText("$calcPro")
            }

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

        cb_calculate_portion.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                et_porion_value.isEnabled = true
            }else{
                et_porion_value.isEnabled = false
                et_porion_value.setText("")
            }
        }

        val productList = arrayListOf<Product>()
        val dbManager = MyDatabaseHelper(this)

        val cursor = dbManager.readAllData()

        if (cursor.moveToNext()){

            var name: String
            var manufacturer: String
            var kcal: Int
            var fat: Double
            var protein: Double
            var carbo: Double
            var weight: Int
            var favorite: Int
            var id: Int

            do{

                name = cursor.getString(cursor.getColumnIndex("Name"))
                manufacturer = cursor.getString(cursor.getColumnIndex("Manufacturer"))
                kcal = cursor.getInt(cursor.getColumnIndex("Kcal"))
                fat = cursor.getDouble(cursor.getColumnIndex("Fat"))
                protein = cursor.getDouble(cursor.getColumnIndex("Protein"))
                carbo = cursor.getDouble(cursor.getColumnIndex("Carbohydrates"))
                weight = cursor.getInt(cursor.getColumnIndex("Weight"))
                favorite = cursor.getInt(cursor.getColumnIndex("Favorite"))
                id = cursor.getInt(cursor.getColumnIndex("ID_pro"))

                productList.add(Product(name,manufacturer,kcal,protein,carbo,fat,weight,favorite, id))

            }while (cursor.moveToNext())
        }

        register.setOnClickListener {

            if (nameNewProduct.length() == 0) nameNewProduct.setText("Item")
            if (manufacturerNewProduct.length() == 0) manufacturerNewProduct.setText("Manufacturer")
            if (kcalNewProduct.length() == 0) kcalNewProduct.setText("0")
            if (proteinNewProduct.length() == 0) proteinNewProduct.setText("0")
            if (carboNewProduct.length() == 0) carboNewProduct.setText("0")
            if (fatNewProduct.length() == 0) fatNewProduct.setText("0")
            if (weightNewProduct.length() == 0) weightNewProduct.setText("0")
            val portion:Double? = if (cb_calculate_portion.isChecked) et_porion_value.text.toString().toDouble() / 100 else null

            val newProduct = Product(
                nameNewProduct.text.toString().capitalize(),
                manufacturerNewProduct.text.toString().capitalize(),
                if (cb_calculate_portion.isChecked && portion != null) (kcalNewProduct.text.toString().toDouble() * portion).toInt() else kcalNewProduct.text.toString().toInt(),
                "%.2f".format(if (cb_calculate_portion.isChecked && portion != null) proteinNewProduct.text.toString().toDouble() * portion else proteinNewProduct.text.toString().toDouble()).replace(',','.').toDouble(),
                "%.2f".format(if (cb_calculate_portion.isChecked && portion != null) carboNewProduct.text.toString().toDouble() * portion else carboNewProduct.text.toString().toDouble()).replace(',','.').toDouble(),
                "%.2f".format(if (cb_calculate_portion.isChecked && portion != null) fatNewProduct.text.toString().toDouble() * portion else fatNewProduct.text.toString().toDouble()).replace(',','.').toDouble(),
                if (cb_calculate_portion.isChecked && portion != null) (portion * 100).toInt() else weightNewProduct.text.toString().toInt(),
                if (cb_calculate_portion.isChecked && portion != null) (portion * 100).toInt() else weightNewProduct.text.toString().toInt(),
                if (favoriteChaek.isChecked) 1 else 0,
                0,
                listOfSuggestionButtonState[0],
                listOfSuggestionButtonState[1],
                listOfSuggestionButtonState[2],
                listOfSuggestionButtonState[3],
                listOfSuggestionButtonState[4],
                listOfSuggestionButtonState[5],
                listOfSuggestionButtonState[6],
                listOfSuggestionButtonState[7]
            )

            if (editProduct != null && editProduct.isNotEmpty()){

                dbManager.setChangeToEditedProduct(newProduct,productForEdit.id)
                val intent = Intent()
                setResult(Activity.RESULT_OK,intent) // FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK
                finish()

            } else {
                for (product in productList) {
                    if (newProduct.name == product.name){
                        exist = true
                        break
                    }else{
                        exist = false
                    }
                }

                if (exist){
                    Toast.makeText(this,getString(R.string.already_exist), Toast.LENGTH_SHORT).show()
                } else if (!exist){
                    Toast.makeText(this,getString(R.string.product_registered), Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    myDataBase.insertData(newProduct)
                        .let {
                            if (it)
                                intent.putExtra("newProductCreated",it)
                            setResult(Activity.RESULT_OK,intent) // FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK FOR CHECK
                            finish()
                        }

                }
            }

        }

        //add function to cancel Button
        cancel.setOnClickListener {
            super.onBackPressed()
        }
    }
}