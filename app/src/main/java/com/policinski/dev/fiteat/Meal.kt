package com.policinski.dev.fiteat

class Meal (productList: MutableList<Product>, name: String) {

    val name = name

    fun nutrientsMeal(nutrientsList: MutableList<Product>): Product{

        var mealNutrients = Product()
        mealNutrients.Name = name

        for (item in nutrientsList){

            mealNutrients.apply {
                Kcal += item.Kcal
                Protein += item.Protein
                Fat += item.Fat
                Carbo += item.Carbo
                Weight += item.Weight
            }

        }

        return mealNutrients
    }

    fun returnProductsList(productList: MutableList<Product>): MutableList<Product> = productList
}
