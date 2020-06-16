package com.policinski.dev.fiteat

class Meal (productList: MutableList<Product>, name: String) {

    val name = name

    fun nutrientsMeal(nutrientsList: MutableList<Product>): Product{

        var mealNutrients = Product()
        mealNutrients.name = name

        for (item in nutrientsList){

            mealNutrients.apply {
                kcal += item.kcal
                protein += item.protein
                fat += item.fat
                carbo += item.carbo
                weight += item.weight
            }

        }

        return mealNutrients
    }

    fun returnProductsList(productList: MutableList<Product>): MutableList<Product> = productList
}
