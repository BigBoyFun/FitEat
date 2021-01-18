package com.policinski.dev.fiteat

data class Product(
    var name: String = "",
    var manufaacturer: String ="",
    var kcal: Int = 0,
    var protein: Double= 0.0,
    var carbo: Double = 0.0,
    var fat: Double = 0.0,
    var weight: Int = 0,
    var lastAddedWeight : Int = 0,
    var favorite: Int = 0,
    var id: Int = 0,
    var breakfast: Int = 0,
    var secondBreakfast: Int = 0,
    var dinner: Int = 0,
    var dessert: Int = 0,
    var tea: Int = 0,
    var supper: Int = 0,
    var snacks: Int = 0,
    var training: Int = 0
)

class ProductV2(
    name_parm:String = "",
    kcal_parm:Int = 0,
    protein_parm:Double = 0.0,
    carbo_parm:Double = 0.0,
    fat_parm:Double = 0.0,
    weight_parm:Int = 0,
    favorite_parm: Int = 0,
    id_parm: Int = 0
){
    var Name = name_parm
        set(value) {
            field = if (value.isEmpty()) value else "Item"
        }

    var kcal = kcal_parm
        set(value) {
            field = value ?: 0
        }

    var protein = protein_parm
        set(value) {
            field = value ?: 0.0
        }

    var carbo = carbo_parm
        set(value) {
            field = value ?: 0.0
        }

    var fat = fat_parm
        set(value) {
            field = value ?: 0.0
        }

    var weight = weight_parm
        set(value) {
            field = value ?: 0
        }

    var favorite = favorite_parm
    var id = id_parm

}
