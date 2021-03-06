package com.policinski.dev.fiteat

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

private val MAIN_PREF = "MAIN_PREF"
private val PREF_CARBO = "PREF_CARBO"
private val PREF_FAT = "PREF_FAT"
private val PREF_KCAL = "PREF_KCAL"
private val PREF_PRO = "PREF_PRO"

const val COL_PREF_CARBO = "PREF_CARBO"
const val COL_PREF_FAT = "PREF_FAT"
const val COL_PREF_KCAL = "PREF_KCAL"
const val COL_PREF_PRO = "PREF_PRO"

const val DATA_BASE_NAME = "PRODUCTS_SQLite_DB"

const val TABLE_NAME = "Products"
const val COL_ID_ING = "ID_pro"
const val COL_NAME = "Name"
const val COL_MANUFACTURER = "Manufacturer"
const val COL_WEIGHT = "Weight"
const val COL_LAST_WEIGHT = "Portion"
const val COL_KCAL = "Kcal"
const val COL_PROTEIN = "Protein"
const val COL_FAT = "Fat"
const val COL_CARBO = "Carbohydrates"
const val COL_FAVORITE = "Favorite"
const val COL_BREAKFAST = "Breakfast"
const val COL_SECOND_BREAKFAST = "SecondBreakfast"
const val COL_DINNER = "Dinner"
const val COL_DESSERT = "Dessert"
const val COL_TEA = "Tea"
const val COL_SUPPER = "Supper"
const val COL_SNACKS = "Snacks"
const val COL_TRAINING = "Training"


const val DISH_TABLE = "Dish"
const val DISH_NAME = "Dish Name"
const val COL_ID_DISH = "ID_dish"


const val DAY_TABLE_NAME = "DayTable"
const val COL_DATE = "Date"
const val COL_ID_DATE = "ID_date"
const val COL_MEAL = "Meal"

const val SHOPPING_LIST_TABLE = "ShoppingList"

const val DAY_NUTRIENTS_GOAL_TABLE_NAME = "DayNutrientsGoal"

class MyDatabaseHelper(var context: Context): SQLiteOpenHelper(context, DATA_BASE_NAME, null, 4) {

    private val dbWrite: SQLiteDatabase = this.writableDatabase
    private val dbRead: SQLiteDatabase = this.readableDatabase

    private var DB_NAME = "PRODUCTS_SQLite_DB.db"
    private var DB_SUB_PATH = "/databases/$DB_NAME"
    private val APP_DATA_PATH = ""

    override fun onCreate(db: SQLiteDatabase?) {

        createProductsTable(db)

        createDayTable(db)

        createDishTable(db)

        createDayNutrientsGoalTable(db)

        createShoppingListTable(db)

    }


    private fun createProductsTable(db: SQLiteDatabase?){
        val createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_ID_ING + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME + " VARCHAR(256), " +
                COL_MANUFACTURER + " VARCHAR(256), " +
                COL_KCAL + " INTEGER, " +
                COL_PROTEIN + " DOUBLE, " +
                COL_CARBO + " DOUBLE, " +
                COL_FAT + " DOUBLE, " +
                COL_WEIGHT + " INTEGER, " +
                COL_LAST_WEIGHT + " INTEGER, " +
                COL_FAVORITE + " BOOLEAN, " +
                COL_BREAKFAST + " INTEGER, " +
                COL_SECOND_BREAKFAST + " INTEGER, " +
                COL_DINNER + " INTEGER, " +
                COL_DESSERT + " INTEGER, " +
                COL_TEA + " INTEGER, " +
                COL_SUPPER + " INTEGER, " +
                COL_SNACKS + " INTEGER, " +
                COL_TRAINING + " INTEGER)"

        db?.execSQL(createTable)
    }

    private fun createDayTable(db: SQLiteDatabase?){
        val createDayTable = "CREATE TABLE IF NOT EXISTS $DAY_TABLE_NAME " +
                "($COL_ID_DATE INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $COL_DATE STRING," +
                " $COL_NAME VARCHAR(256)," +
                " $COL_MANUFACTURER VARCHAR(256), " +
                " $COL_KCAL INTEGER," +
                " $COL_PROTEIN DOUBLE," +
                " $COL_FAT DOUBLE," +
                " $COL_CARBO DOUBLE," +
                " $COL_WEIGHT INTEGER," +
                " $COL_MEAL INTEGER)"

        db?.execSQL(createDayTable)
    }

    private fun createDishTable(db: SQLiteDatabase?){
    val createMealsTable = "CREATE TABLE IF NOT EXISTS $DISH_TABLE " +
            "($COL_ID_DISH INTEGER PRIMARY KEY AUTOINCREMENT," +
            " $DISH_NAME STRING," +
            " $COL_WEIGHT INTEGER," +
            " $COL_ID_ING INTEGER)" //save product id from table Products

    db?.execSQL(createMealsTable)
    }

    private fun createDayNutrientsGoalTable(db: SQLiteDatabase?){
        val createDayNutrientsGoalTable =
            "CREATE TABLE IF NOT EXISTS $DAY_NUTRIENTS_GOAL_TABLE_NAME ($COL_ID_DATE INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COL_DATE STRING, $COL_KCAL INTEGER, " +
                    "$COL_FAT INTEGER, $COL_CARBO INTEGER, " +
                    "$COL_PROTEIN INTEGER)"

        db?.execSQL(createDayNutrientsGoalTable)
    }

    private fun createShoppingListTable(db: SQLiteDatabase?) {
        val createShoppingListTable = "CREATE TABLE IF NOT EXISTS $SHOPPING_LIST_TABLE ($COL_ID_ING INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_NAME STRING, $COL_WEIGHT INTEGER, $COL_FAVORITE INTEGER)"

        db?.execSQL(createShoppingListTable)
    }

    @Throws(SQLException::class)
    fun openDataBase(): Boolean {
        val mPath: String = APP_DATA_PATH + DB_SUB_PATH
        //Note that this method assumes that the db file is already copied in place
        val dataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE)
        return dataBase != null
    }

    fun readShoppingList(): MutableList<Product>{

        var productList = mutableListOf<Product>()

        var query = "SELECT * FROM $SHOPPING_LIST_TABLE"
        var cursor = dbRead.rawQuery(query,null)

        if (cursor.moveToNext()){

            do {

                productList.add(Product(
                    id = cursor.getInt(cursor.getColumnIndex(COL_ID_ING)),
                    name = cursor.getString(cursor.getColumnIndex(COL_NAME)),
                    weight = cursor.getInt(cursor.getColumnIndex(COL_WEIGHT)),
                    favorite = cursor.getInt(cursor.getColumnIndex(COL_FAVORITE)))
                )

            }while (cursor.moveToNext())
        }
        return productList
    }

    fun addProductToShoppingList(name: String, weight: Int){

        var cv = ContentValues()

    }

    fun updateProductInShoppingList(name: String,weight: Int){

        var cv = ContentValues()

    }


    fun addProductToDay(
        data: String,
        name: String,
        kcal: Int,
        protein: Double,
        carbo: Double,
        fat: Double,
        meal: Int,
        weight: Int,
        id: Int
    ){

        var cv = ContentValues()
        cv.put(COL_DATE, data)
        cv.put(COL_NAME, name)
        cv.put(COL_KCAL, kcal)
        cv.put(COL_CARBO, carbo)
        cv.put(COL_PROTEIN, protein)
        cv.put(COL_FAT, fat)
        cv.put(COL_MEAL, meal)
        cv.put(COL_WEIGHT, weight)

        val result = dbWrite?.insert(DAY_TABLE_NAME, null, cv)
        if (result == -1.toLong())
            Toast.makeText(
                context,
                context.getString(R.string.cant_add_pro_to_day),
                Toast.LENGTH_SHORT
            ).show()
        else
            Toast.makeText(context, context.getString(R.string.added_to_day), Toast.LENGTH_SHORT).show()

        //update information about user choices for auto suggestion function
        fun findMealByNum(num: Int): String{

            return when(num){
                1 -> "Breakfast"
                2 -> "SecondBreakfast"
                3 -> "Dinner"
                4 -> "Dessert"
                5 -> "Tea"
                6 -> "Supper"
                7 -> "Snacks"
                8 -> "Training"
                else -> "All"
            }
        }
        updateProductAutoSuggestionInfo(id, findMealByNum(meal), weight)
    }

    private fun updateProductAutoSuggestionInfo(id: Int, mealName: String, lastWeight: Int) {
        val query = "UPDATE $TABLE_NAME SET $mealName = 1, Portion = $lastWeight WHERE $COL_ID_ING = $id"
        dbWrite?.execSQL(query)
    }

    fun editProductNutrients(
        kcal: Int,
        protein: Double,
        fat: Double,
        carbo: Double,
        weight: Int,
        id: Int
    ){

        val query = "update $DAY_TABLE_NAME set $COL_KCAL = $kcal, $COL_PROTEIN = $protein, $COL_FAT = $fat, $COL_CARBO = $carbo, $COL_WEIGHT = $weight  where $COL_ID_DATE = $id"
        dbWrite?.execSQL(query)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) onCreate(db)
    }

    fun insertData(product: Product): Boolean{

        var cv = ContentValues()
        cv.put(COL_NAME, product.name)
        cv.put(COL_MANUFACTURER, product.manufaacturer)
        cv.put(COL_KCAL, product.kcal)
        cv.put(COL_CARBO, product.carbo)
        cv.put(COL_PROTEIN, product.protein)
        cv.put(COL_FAT, product.fat)
        cv.put(COL_WEIGHT, product.weight)
        cv.put(COL_LAST_WEIGHT, product.lastAddedWeight)
        cv.put(COL_FAVORITE, product.favorite)
        cv.put(COL_BREAKFAST, product.breakfast)
        cv.put(COL_SECOND_BREAKFAST, product.secondBreakfast)
        cv.put(COL_DINNER, product.dinner)
        cv.put(COL_DESSERT, product.dessert)
        cv.put(COL_TEA, product.tea)
        cv.put(COL_SUPPER, product.supper)
        cv.put(COL_SNACKS, product.snacks)
        cv.put(COL_TRAINING, product.training)

        var result = dbWrite?.insert(TABLE_NAME, null, cv)
        if (result == -1.toLong()) {
            Toast.makeText(
                context,
                context.getString(R.string.cant_insert_product),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }else{
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show()
            return true
        }
    }

    fun setChangeToEditedProduct(product: Product, id: Int) {
        val query = "UPDATE $TABLE_NAME SET $COL_NAME = '${product.name}'," +
                " $COL_MANUFACTURER = '${product.manufaacturer}'," +
                " $COL_WEIGHT = ${product.weight}," +
                " $COL_KCAL = ${product.kcal}," +
                " $COL_FAT = ${product.fat}," +
                " $COL_CARBO = ${product.carbo}," +
                " $COL_PROTEIN = ${product.protein}," +
                " $COL_FAVORITE = ${product.favorite}," +
                " $COL_BREAKFAST = ${product.breakfast}," +
                " $COL_SECOND_BREAKFAST = ${product.secondBreakfast}," +
                " $COL_DINNER = ${product.dinner}," +
                " $COL_DESSERT = ${product.dessert}," +
                " $COL_TEA = ${product.tea}," +
                " $COL_SUPPER = ${product.supper}," +
                " $COL_SNACKS = ${product.snacks}," +
                " $COL_TRAINING = ${product.training} WHERE $COL_ID_ING = $id"

        dbWrite?.execSQL(query)
    }

    fun readAllData(): Cursor{

        val query = "SELECT * FROM $TABLE_NAME"
        return dbRead.rawQuery(query, null)
    }

    fun  readDayTable(date: String): Array<Double>{

        //calculate nutrients from current day
        val kcalQuery = "SELECT sum(Kcal) FROM $DAY_TABLE_NAME WHERE $COL_DATE = '$date'"
        val proQuery = "SELECT sum(Protein) FROM $DAY_TABLE_NAME WHERE $COL_DATE = '$date'"
        val fatQuery = "SELECT sum(Fat) FROM $DAY_TABLE_NAME WHERE $COL_DATE = '$date'"
        val carboQuery = "SELECT sum(Carbohydrates) FROM $DAY_TABLE_NAME WHERE $COL_DATE = '$date'"

        //create cursors
        val kcalSum = dbRead.rawQuery(kcalQuery, null)
        val proSum = dbRead.rawQuery(proQuery, null)
        val fatSum = dbRead.rawQuery(fatQuery, null)
        val carboSum = dbRead.rawQuery(carboQuery, null)

        //variables for storing the accumulated nutritional values of the current day
        var kcal = 0.0
        var pro = 0.0
        var fat = 0.0
        var carbo = 0.0

        //read data by cursor
        if (kcalSum.moveToFirst()) kcal = kcalSum.getInt(kcalSum.getColumnIndex("sum(Kcal)")).toDouble()
        if (proSum.moveToFirst()) pro = proSum.getDouble(proSum.getColumnIndex("sum(Protein)"))
        if (fatSum.moveToFirst()) fat = fatSum.getDouble(fatSum.getColumnIndex("sum(Fat)"))
        if (carboSum.moveToFirst()) carbo = carboSum.getDouble(carboSum.getColumnIndex("sum(Carbohydrates)"))

        //insert accumulated nutritional values in array
        val array = arrayOf(
            kcal,
            BigDecimal(pro).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
            BigDecimal(fat).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
            BigDecimal(carbo).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        )
        //return array
        return array

    }

    //read meals from selected day
    fun readMealsFromSelectedDay(date: String): ArrayList<Int>{
        val mealsFromDay = arrayListOf<Int>()
        val query = "SELECT $COL_MEAL FROM $DAY_TABLE_NAME WHERE $COL_DATE = '$date'"
        val cursor = dbRead.rawQuery(query, null)

        if (cursor.moveToNext()){
            do{
                mealsFromDay.add(cursor.getInt(cursor.getColumnIndex(COL_MEAL)))
            }while (cursor.moveToNext())
        }
        return mealsFromDay
    }

    //reading products from a specific meal
    fun readMealFromDay(date: String, meal: Int) : MutableList<Product>{

        val productList: MutableList<Product> = mutableListOf()

        val query = "SELECT * FROM $DAY_TABLE_NAME WHERE $COL_MEAL = $meal AND $COL_DATE = '$date'"
        val cursor = dbRead.rawQuery(query, null)

        if (cursor.moveToNext()){

            do {

                val name = cursor.getString(cursor.getColumnIndex("Name"))
                val manufacturer = cursor.getString(cursor.getColumnIndex("Manufacturer"))
                val kcal = cursor.getInt(cursor.getColumnIndex("Kcal"))
                val fat = cursor.getDouble(cursor.getColumnIndex("Fat"))
                val protein = cursor.getDouble(cursor.getColumnIndex("Protein"))
                val carbo = cursor.getDouble(cursor.getColumnIndex("Carbohydrates"))
                val weight = cursor.getInt(cursor.getColumnIndex("Weight"))
                val id = cursor.getInt(cursor.getColumnIndex("ID_date"))

                productList.add(
                    Product(
                        name,
                        manufacturer ?: "Manufacturer",
                        kcal,
                        protein,
                        carbo,
                        fat,
                        weight,
                        id = id
                    )
                )

            }while (cursor.moveToNext())
        }

        return productList
    }

    fun readDataFromDayTAble(): Cursor{

        val quer = "SELECT * FROM $DAY_TABLE_NAME"

        return dbRead.rawQuery(quer, null)
    }

    fun updateFavorite(id: Int, favorite: Int) {
        val query = "update $TABLE_NAME set $COL_FAVORITE = $favorite where $COL_ID_ING = $id"
        dbWrite?.execSQL(query)
    }

    fun deleteProduct(id: Int) {
        val query = "delete from $TABLE_NAME where $COL_ID_ING = $id"
        dbWrite.execSQL(query)
    }

    fun deleteProductFromMeal(id: Int) {
        val query = "delete from $DAY_TABLE_NAME where $COL_ID_DATE = $id"
        dbWrite.execSQL(query)
    }

    fun saveDailyGoalNutrients(kcal: Int, protein: Int, carbo: Int, fat: Int, date: String){

        val cv = ContentValues()
        cv.put(COL_KCAL, kcal)
        cv.put(COL_FAT, fat)
        cv.put(COL_CARBO, carbo)
        cv.put(COL_PROTEIN, protein)
        cv.put(COL_DATE, date)

        val result = dbWrite?.insert(DAY_NUTRIENTS_GOAL_TABLE_NAME, null, cv)

        if (result == -1.toLong()){
            Log.i("DayGoal.", "Cant update table with day nutrients goal.")
        }else{
            Log.i("DayGoal.", "Day table with nutrients goal is updated.")
        }
    }

    fun readDailyGoalNutrients(date: String): Cursor{

        val query = "SELECT $COL_KCAL, $COL_PROTEIN, $COL_CARBO, $COL_FAT FROM $DAY_NUTRIENTS_GOAL_TABLE_NAME WHERE $COL_DATE = '$date'"
        val result = dbRead.rawQuery(query, null)

        if (result.moveToFirst()){
            //return cursor
        } else {

            //save to db nutrients goal if selected day is today
            //this is to prevent you from saving your daily goals
            // for the days that have just arrived and which have already ended
            // you cannot create nutrition plans in advance
            val dateNow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate.now().toString()
            } else {
                TODO("VERSION.SDK_INT < O")
            }
            if (date == dateNow) {
                //read nutrients goal from shared pref and save to DB
                val sharedPref = context.getSharedPreferences(MAIN_PREF, 0)
                val kcal = sharedPref.getInt(PREF_KCAL, 0)
                val fat = sharedPref.getInt(PREF_FAT, 0)
                val carbo = sharedPref.getInt(PREF_CARBO, 0)
                val protein = sharedPref.getInt(PREF_PRO, 0)
                saveDailyGoalNutrients(kcal, protein, carbo, fat, date)
            }

        }
        return dbRead.rawQuery(query, null)

    }

    fun updateDailyGoalNutrients(date: String, kcal: Int, fat: Int, carbo: Int, protein: Int){

        val query ="UPDATE $DAY_NUTRIENTS_GOAL_TABLE_NAME" +
                " SET $COL_KCAL = $kcal," +
                " $COL_FAT = $fat," +
                " $COL_CARBO = $carbo," +
                " $COL_PROTEIN = $protein" +
                " WHERE $COL_DATE = '$date'"
        dbWrite.execSQL(query)

    }

    fun addDayToDayTable(data: String): Boolean{

        var cv = ContentValues()
        cv.put(COL_DATE, data)
        cv.put(COL_NAME, "")
        cv.put(COL_KCAL, 0)
        cv.put(COL_PROTEIN, 0)
        cv.put(COL_FAT, 0)
        cv.put(COL_CARBO, 0)
        val result = dbWrite?.insert(DAY_TABLE_NAME, null, cv)

        if (result == -1.toLong()) {
            Toast.makeText(
                context,
                context.getString(R.string.cant_insert_product),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }else {
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_SHORT).show()
            return true
        }
    }
    fun readDayData(): String{
        var readData = ""
        var data = dbRead.rawQuery("SELECT * FROM $DAY_TABLE_NAME ORDER BY ID DESC LIMIT 1", null)
        if (data.moveToLast()){
            readData = data.getString(data.getColumnIndex(COL_DATE))
        }
        return readData
    }

    fun addRowWeight(){
        val wuery1 = "ALTER TABLE $DAY_TABLE_NAME ADD $COL_PREF_KCAL INTEGER"
        val wuery2 = "ALTER TABLE $DAY_TABLE_NAME ADD $COL_PREF_PRO DOUBLE"
        val wuery3 = "ALTER TABLE $DAY_TABLE_NAME ADD $COL_PREF_CARBO DOUBLE"
        val wuery4 = "ALTER TABLE $DAY_TABLE_NAME ADD $COL_PREF_FAT DOUBLE"

        dbWrite.execSQL(wuery1)
        dbWrite.execSQL(wuery2)
        dbWrite.execSQL(wuery3)
        dbWrite.execSQL(wuery4)
    }

    fun findEditedProductByName(name: String): Product {
        
        var baseProduct = Product()
        
        val date = dbRead.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = '$name'", null)
        
        if (date.moveToFirst()){
            baseProduct.apply {
                this.name = date.getString(date.getColumnIndex(COL_NAME))
                id = date.getInt(date.getColumnIndex(COL_ID_ING))
                manufaacturer = date.getString(date.getColumnIndex(COL_MANUFACTURER))
                kcal = date.getInt(date.getColumnIndex(COL_KCAL))
                protein = date.getDouble(date.getColumnIndex(COL_PROTEIN))
                fat = date.getDouble(date.getColumnIndex(COL_FAT))
                carbo = date.getDouble(date.getColumnIndex(COL_CARBO))
                weight = date.getInt(date.getColumnIndex(COL_WEIGHT))
                favorite = date.getInt(date.getColumnIndex(COL_FAVORITE))
                breakfast = date.getInt(date.getColumnIndex(COL_BREAKFAST))
                secondBreakfast = date.getInt(date.getColumnIndex(COL_SECOND_BREAKFAST))
                dinner = date.getInt(date.getColumnIndex(COL_DINNER))
                dessert = date.getInt(date.getColumnIndex(COL_DESSERT))
                tea = date.getInt(date.getColumnIndex(COL_TEA))
                supper = date.getInt(date.getColumnIndex(COL_SUPPER))
                snacks = date.getInt(date.getColumnIndex(COL_SNACKS))
                training = date.getInt(date.getColumnIndex(COL_TRAINING))
            }
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.product_not_found),
                Toast.LENGTH_SHORT
            ).show()}

        return baseProduct
    }

    fun deleteProductFromList(meal: String, productId: Int): Boolean{

        return try {
            val query = "UPDATE $TABLE_NAME SET $meal = 0 WHERE $COL_ID_ING = $productId"
            dbWrite.execSQL(query)
            true
        } catch (s: SQLiteException){
            false
        }
    }

    fun getSelectedProduct(productName: String): Product? {

        var readProduct = Product()

        val query = "SELECT * FROM ${TABLE_NAME} WHERE ${COL_NAME} = '${productName}'"

        val result = dbRead.rawQuery(query, null)

        if (result.moveToFirst()){
            readProduct.apply {
                name = result.getString(result.getColumnIndex(COL_NAME))
                kcal = result.getInt(result.getColumnIndex(COL_KCAL))
                fat = result.getDouble(result.getColumnIndex(COL_FAT))
                carbo = result.getDouble(result.getColumnIndex(COL_CARBO))
                protein = result.getDouble(result.getColumnIndex(COL_PROTEIN))
                weight = result.getInt(result.getColumnIndex(COL_WEIGHT))
                lastAddedWeight = result.getInt(result.getColumnIndex(COL_LAST_WEIGHT))
                id = result.getInt(result.getColumnIndex(COL_ID_ING))
                breakfast = result.getInt(result.getColumnIndex(COL_BREAKFAST))
                secondBreakfast = result.getInt(result.getColumnIndex(COL_SECOND_BREAKFAST))
                dinner = result.getInt(result.getColumnIndex(COL_DINNER))
                dessert = result.getInt(result.getColumnIndex(COL_DESSERT))
                tea = result.getInt(result.getColumnIndex(COL_TEA))
                supper = result.getInt(result.getColumnIndex(COL_SUPPER))
                snacks = result.getInt(result.getColumnIndex(COL_SNACKS))
                training = result.getInt(result.getColumnIndex(COL_TRAINING))
            }
        }

        return readProduct

    }

    fun deleteUserHoses(): Boolean {
        val query = "UPDATE Products SET Breakfast = 0, SecondBreakfast = 0, Dinner = 0, Dessert = 0, Tea = 0, Supper = 0, Snacks = 0, Training = 0"

        try {
            dbWrite.execSQL(query)
            Toast.makeText(context,"User hoses cleared",Toast.LENGTH_SHORT).show()
        } catch (e: SQLException){
            Toast.makeText(context,"Can't clear user hoses.",Toast.LENGTH_SHORT).show()
        }
        return true
    }

    fun deleteAllProducts() {
        val query = "UPDATE Products SET $COL_BREAKFAST = 0, $COL_SECOND_BREAKFAST = 0, $COL_DINNER = 0, $COL_DESSERT = 0, $COL_TEA = 0, $COL_SUPPER = 0, $COL_SNACKS = 0, $COL_TRAINING = 0"
        dbWrite.execSQL(query)
    }

    fun updateProduct(id: Int, meal: Int, frequensy: Int){
        val a = frequensy + 1
        val mealName = findMealByNum(meal)
        var query = "UPDATE $TABLE_NAME SET $mealName = $a WHERE $COL_ID_ING = $id"
        dbWrite.execSQL(query)
    }

    fun findMealByNum(num: Int): String{

        return when(num){
            1 -> "Breakfast"
            2 -> "SecondBreakfast"
            3 -> "Dinner"
            4 -> "Dessert"
            5 -> "Tea"
            6 -> "Supper"
            7 -> "Snacks"
            8 -> "Training"
            else -> "All"
        }
    }

    fun saveProductStateFromShoppingList(id: Int, favorite: Int) {
        val query = "UPDATE $SHOPPING_LIST_TABLE SET $COL_FAVORITE = $favorite WHERE $COL_ID_ING = $id"
        dbWrite.execSQL(query)
    }

    fun deleteSelectedProductFromShoppingList() {
        val query = "DELETE FROM $SHOPPING_LIST_TABLE WHERE $COL_FAVORITE = 1"
        dbWrite.execSQL(query)
    }

    fun addNewProductToShoppingList(productName: String, activity: String = "ShoppingList") {

        val query = "SELECT * FROM $SHOPPING_LIST_TABLE WHERE $COL_NAME = '$productName'"
        val res = dbRead.rawQuery(query,null) //checks that product exist on the shopping list

        if (res.moveToFirst()){
            if (activity == "ShoppingList") { //this message must be shown only when product is added to shopping list from ShoppingListFragment
                Toast.makeText(context, "This product already is on the list", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            val cv = ContentValues()
            cv.put(COL_NAME,productName)
            cv.put(COL_WEIGHT, 0)
            cv.put(COL_FAVORITE, 0)

            val result = dbWrite?.insert(SHOPPING_LIST_TABLE,null,cv)

            if (result == (-1).toLong()){
                Log.i("ShoppingList.", "Cant insert new product to shopping list.")
            }else{
                Log.i("ShoppingList.", "Shopping list is updated.")
            }
        }

    }

}