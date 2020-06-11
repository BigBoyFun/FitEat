package com.policinski.dev.fiteat

data class Product(
    var Name: String = "",
    var Kcal: Int = 0,
    var Protein: Double= 0.0,
    var Carbo: Double = 0.0,
    var Fat: Double = 0.0,
    var Weight: Int = 0,
    var favorite: Int = 0,
    val id: Int = 0
) {

}
