package com.policinski.dev.fiteat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.policinski.dev.fiteat.databinding.ActivityNewProductBinding
import com.policinski.dev.fiteat.databinding.ProductSettingsDialogBinding
import kotlinx.android.synthetic.main.activity_new_product.*
import java.util.*

class ProductSettingsClass: AppCompatActivity() {

    private lateinit var binding: ActivityNewProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        createNewProduct()

    }

    private fun createNewProduct(){

        this.onPause()

        val myDataBase: MyDatabaseHelper = MyDatabaseHelper(this)

        var exist: Boolean = false

        var leftWeight = 0.0
        var leftKcal = 0.0

        val listOfSuggestionButtons = listOf<Button>(binding.breakfastButSuggestion1,
            binding.secondBreakfastButSuggestion2,dinner_but_suggestion_3,dessert_but_suggestion_4,
            tea_but_suggestion_5,supper_but_suggestion_6,snacks_but_suggestion_7,training_but_suggestion_8)

        val listOfSuggestionButtonState = mutableListOf<Int>(0,0,0,0,0,0,0,0)

        var productForEdit = Product()
        val editProduct = intent.getStringExtra("EditProductName") //used when this activity is open for editing product MUST BE REWORKED!!!!!!!!!!!!!!!!!!

        if (editProduct != null && editProduct.isNotEmpty()){

            binding.checkBoxCalculatePortion.isEnabled = false

            productForEdit = myDataBase.findEditedProductByName(editProduct)

            binding.titleTv.text = "${productForEdit.name}"
            binding.nameNewProduct.setText(productForEdit.name)
            binding.manufacturerNewProduct.setText(productForEdit.manufaacturer)
            binding.weightNewProduct.setText(productForEdit.weight.toString())
            binding.kcalNewProduct.setText(productForEdit.kcal.toString())
            binding.fatNewProduct.setText(productForEdit.fat.toString())
            binding.carboNewProduct.setText(productForEdit.carbo.toString())
            binding.proteinNewProduct.setText(productForEdit.protein.toString())
            binding.favoriteNewProduct.isChecked = productForEdit.favorite == 1
            listOfSuggestionButtonState[0] = productForEdit.breakfast
            listOfSuggestionButtonState[1] = productForEdit.secondBreakfast
            listOfSuggestionButtonState[2] = productForEdit.dinner
            listOfSuggestionButtonState[3] = productForEdit.dessert
            listOfSuggestionButtonState[4] = productForEdit.tea
            listOfSuggestionButtonState[5] = productForEdit.supper
            listOfSuggestionButtonState[6] = productForEdit.snacks
            listOfSuggestionButtonState[7] = productForEdit.training

            listOfSuggestionButtonState.forEachIndexed { index, i -> if (i > 0) listOfSuggestionButtons[index].setBackgroundResource(R.drawable.selected_frame_shape_but) }

            binding.titleTv.text = "${productForEdit.name} settings."

            binding.registerNewProduct.text = getString(R.string.save)

        } else {
            Toast.makeText(this,"NOT FOUND", Toast.LENGTH_SHORT).show()
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

        if (productForEdit.weight == 100) {
            binding.but100g.isChecked = true
        } else {
            binding.but100g.isChecked = false
            but_portion.isChecked = true
        }

        binding.but100g.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                but_portion.isChecked = false
                binding.weightNewProduct.setText("100")
                binding.kcalNewProduct.setText(((100.0 / productForEdit.weight) * productForEdit.kcal).toString())
                binding.fatNewProduct.setText(((100.0 / productForEdit.weight) * productForEdit.fat).toString())
                binding.carboNewProduct.setText(((100.0 / productForEdit.weight) * productForEdit.carbo).toString())
                binding.proteinNewProduct.setText(((100.0 / productForEdit.weight) * productForEdit.protein).toString())
                binding.weightNewProduct.isEnabled = false
                binding.checkBoxCalculatePortion.isEnabled = true
            } else {
                but_portion.isChecked = true
            }
        }

        but_portion.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.but100g.isChecked = false
                binding.weightNewProduct.isEnabled = true
                binding.weightNewProduct.setText("${productForEdit.weight}")
                binding.kcalNewProduct.setText(productForEdit.kcal.toString())
                binding.fatNewProduct.setText(productForEdit.fat.toString())
                binding.carboNewProduct.setText(productForEdit.carbo.toString())
                binding.proteinNewProduct.setText(productForEdit.protein.toString())
                binding.checkBoxCalculatePortion.isChecked = false
                binding.checkBoxCalculatePortion.isEnabled = false
            } else {
                binding.but100g.isChecked = true
            }
        }

        binding.kcalNewProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                leftKcal = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0 * 3.0} else binding.carboNewProduct.text.toString().toDouble() * 3.0) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!){0.0 * 7.0} else binding.fatNewProduct.text.toString().toDouble() * 7.0) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!){0.0 * 3.0} else binding.proteinNewProduct.text.toString().toDouble() * 3.0)

                leftWeight = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0} else binding.carboNewProduct.text.toString().toDouble()) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!) { 0.0 } else binding.fatNewProduct.text.toString().toDouble()) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!) { 0.0 } else binding.proteinNewProduct.text.toString().toDouble())

                if (leftKcal > if (binding.kcalNewProduct.text?.isNotEmpty() == true)binding.kcalNewProduct.text.toString().toDouble() else 0.0) {
                    leftKcal = 0.0
                    leftWeight = 0.0

                    binding.fatNewProduct.setText("")
                    binding.carboNewProduct.setText("")
                    binding.proteinNewProduct.setText("")
                }

                if (binding.kcalNewProduct.length() <= 0) {
                    binding.fatNewProduct.isEnabled = false
                    binding.carboNewProduct.isEnabled = false
                    binding.proteinNewProduct.isEnabled = false
                } else {
                    binding.fatNewProduct.isEnabled = true
                    binding.carboNewProduct.isEnabled = true
                    binding.proteinNewProduct.isEnabled = true
                }

            }

        })

        binding.weightNewProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                leftKcal = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0 * 3.0} else binding.carboNewProduct.text.toString().toDouble() * 3.0) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!){0.0 * 7.0} else binding.fatNewProduct.text.toString().toDouble() * 7.0) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!){0.0 * 3.0} else binding.proteinNewProduct.text.toString().toDouble() * 3.0)

                leftWeight = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0} else binding.carboNewProduct.text.toString().toDouble()) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!) { 0.0 } else binding.fatNewProduct.text.toString().toDouble()) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!) { 0.0 } else binding.proteinNewProduct.text.toString().toDouble())

                if (leftWeight > if (binding.weightNewProduct.text?.isNotEmpty()!!) binding.weightNewProduct.text.toString().toDouble() else 0.0) {
                    leftKcal = 0.0
                    leftWeight = 0.0

                    binding.fatNewProduct.setText("")
                    binding.carboNewProduct.setText("")
                    binding.proteinNewProduct.setText("")
                    binding.kcalNewProduct.setText("")
                }

            }

        })

        binding.carboNewProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                leftKcal = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0 * 3.0} else binding.carboNewProduct.text.toString().toDouble() * 3.0) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!){0.0 * 7.0} else binding.fatNewProduct.text.toString().toDouble() * 7.0) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!){0.0 * 3.0} else binding.proteinNewProduct.text.toString().toDouble() * 3.0)

                leftWeight = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0} else binding.carboNewProduct.text.toString().toDouble()) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!) { 0.0 } else binding.fatNewProduct.text.toString().toDouble()) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!) { 0.0 } else binding.proteinNewProduct.text.toString().toDouble())

                if (leftKcal > if (binding.kcalNewProduct.length() <= 0){0.0} else binding.kcalNewProduct.text.toString().toDouble()){
                    if (binding.carboNewProduct.isFocused) {
                        binding.carboNewProduct.setText("")
                    }
                    Toast.makeText(this@ProductSettingsClass, "The sum of calories from carbohydrates, fats and proteins may not exceed the declared amount of calories in the product.", Toast.LENGTH_SHORT).show()
                }
                if (leftWeight > if (binding.weightNewProduct.length() <= 0) { 0.0 } else binding.weightNewProduct.text.toString().toDouble()){
                    if (binding.carboNewProduct.isFocused) {
                        binding.carboNewProduct.setText("")
                    }
                    Toast.makeText(this@ProductSettingsClass, "the sum of carbohydrates, fats and proteins must not exceed the declared weight.", Toast.LENGTH_SHORT).show()
                }
            }

        })

        binding.fatNewProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                leftKcal = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0 * 3.0} else binding.carboNewProduct.text.toString().toDouble() * 3.0) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!){0.0 * 7.0} else binding.fatNewProduct.text.toString().toDouble() * 7.0) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!){0.0 * 3.0} else binding.proteinNewProduct.text.toString().toDouble() * 3.0)

                leftWeight = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0} else binding.carboNewProduct.text.toString().toDouble()) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!) { 0.0 } else binding.fatNewProduct.text.toString().toDouble()) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!) { 0.0 } else binding.proteinNewProduct.text.toString().toDouble())

                if (leftKcal > if (binding.kcalNewProduct.length() <= 0){0.0} else binding.kcalNewProduct.text.toString().toDouble()){
                    if (binding.fatNewProduct.isFocused) {
                        binding.fatNewProduct.setText("")
                    }
                    Toast.makeText(this@ProductSettingsClass, "The sum of calories from carbohydrates, fats and proteins may not exceed the declared amount of calories in the product.", Toast.LENGTH_SHORT).show()
                }
                if (leftWeight > if (binding.weightNewProduct.length() <= 0) { 0.0 } else binding.weightNewProduct.text.toString().toDouble()){
                    if (binding.fatNewProduct.isFocused) {
                        binding.fatNewProduct.setText("")
                    }
                    Toast.makeText(this@ProductSettingsClass, "the sum of carbohydrates, fats and proteins must not exceed the declared weight.", Toast.LENGTH_SHORT).show()
                }
            }

        })

        binding.proteinNewProduct.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                leftKcal = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0 * 3.0} else binding.carboNewProduct.text.toString().toDouble() * 3.0) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!){0.0 * 7.0} else binding.fatNewProduct.text.toString().toDouble() * 7.0) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!){0.0 * 3.0} else binding.proteinNewProduct.text.toString().toDouble() * 3.0)

                leftWeight = (if (binding.carboNewProduct.text?.isEmpty()!!) {0.0} else binding.carboNewProduct.text.toString().toDouble()) +
                        (if (binding.fatNewProduct.text?.isEmpty()!!) { 0.0 } else binding.fatNewProduct.text.toString().toDouble()) +
                        (if (binding.proteinNewProduct.text?.isEmpty()!!) { 0.0 } else binding.proteinNewProduct.text.toString().toDouble())

                if (leftKcal > if (binding.kcalNewProduct.length() <= 0) { 0.0 } else binding.kcalNewProduct.text.toString().toDouble()){
                    if (binding.proteinNewProduct.isFocused) {
                        binding.proteinNewProduct.setText("")
                    }
                    Toast.makeText(this@ProductSettingsClass, "The sum of calories from carbohydrates, fats and proteins may not exceed the declared amount of calories in the product.", Toast.LENGTH_SHORT).show()
                }
                if (leftWeight > if (binding.weightNewProduct.length() <= 0) { 0.0 } else binding.weightNewProduct.text.toString().toDouble()){
                    if (binding.proteinNewProduct.isFocused) {
                        binding.proteinNewProduct.setText("")
                    }
                    Toast.makeText(this@ProductSettingsClass, "the sum of carbohydrates, fats and proteins must not exceed the declared weight.", Toast.LENGTH_SHORT).show()
                }
            }

        })

        binding.checkBoxCalculatePortion.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && buttonView.isEnabled){
                binding.etPortionValue.isEnabled = true
                binding.etPortionValue.requestFocus()
                val mi = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                mi.showSoftInput(binding.etPortionValue,0)
            } else {
                binding.etPortionValue.isEnabled = false
                binding.etPortionValue.setText("")
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

        binding.registerNewProduct.setOnClickListener {

            if (binding.nameNewProduct.length() == 0) binding.nameNewProduct.setText("Item")
            if (binding.manufacturerNewProduct.length() == 0) binding.manufacturerNewProduct.setText("Manufacturer")
            if (binding.kcalNewProduct.length() == 0) binding.kcalNewProduct.setText("0")
            if (binding.proteinNewProduct.length() == 0) binding.proteinNewProduct.setText("0")
            if (binding.carboNewProduct.length() == 0) binding.carboNewProduct.setText("0")
            if (binding.fatNewProduct.length() == 0) binding.fatNewProduct.setText("0")
            if (binding.weightNewProduct.length() == 0) binding.weightNewProduct.setText("0")
            val portion:Double? = if (binding.checkBoxCalculatePortion.isChecked) binding.etPortionValue.text.toString().toDouble() / 100 else null

            val newProduct = Product(
                binding.nameNewProduct.text.toString().capitalize(),
                binding.manufacturerNewProduct.text.toString().capitalize(),
                if (binding.checkBoxCalculatePortion.isChecked && portion != null) (binding.kcalNewProduct.text.toString().toDouble() * portion).toInt() else binding.kcalNewProduct.text.toString().toInt(),
                "%.2f".format(if (binding.checkBoxCalculatePortion.isChecked && portion != null) binding.proteinNewProduct.text.toString().toDouble() * portion else binding.proteinNewProduct.text.toString().toDouble()).replace(',','.').toDouble(),
                "%.2f".format(if (binding.checkBoxCalculatePortion.isChecked && portion != null) binding.carboNewProduct.text.toString().toDouble() * portion else binding.carboNewProduct.text.toString().toDouble()).replace(',','.').toDouble(),
                "%.2f".format(if (binding.checkBoxCalculatePortion.isChecked && portion != null) binding.fatNewProduct.text.toString().toDouble() * portion else binding.fatNewProduct.text.toString().toDouble()).replace(',','.').toDouble(),
                if (binding.checkBoxCalculatePortion.isChecked && portion != null) (portion * 100).toInt() else binding.weightNewProduct.text.toString().toInt(),
                if (binding.checkBoxCalculatePortion.isChecked && portion != null) (portion * 100).toInt() else binding.weightNewProduct.text.toString().toInt(),
                if (binding.favoriteNewProduct.isChecked) 1 else 0,
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
        binding.cancelNewProduct.setOnClickListener {
            super.onBackPressed()
        }
    }

}