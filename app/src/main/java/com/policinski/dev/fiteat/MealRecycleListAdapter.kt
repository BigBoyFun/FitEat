package com.policinski.dev.fiteat

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.meal_row_layout.view.*
import java.time.LocalDate

class MealRecycleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var mealNutrientsSumList: MutableList<Product> = mutableListOf()

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){

        val expandableList = itemView.expandable_list

        val expandableListAdapter = ExpandableListAdapter()
        val mealRowKcal = itemView.meal_row_kcal_tv
        val mealRowPro = itemView.meal_row_pro_tv
        val mealRowFat = itemView.meal_row_fat_tv
        val mealRowCarbo = itemView.meal_row_carbo_tv


        fun bind(product: Product){

            mealRowKcal.text = product.Kcal.toString()
            mealRowCarbo.text = product.Carbo.toString()
            mealRowFat.text = product.Fat.toString()
            mealRowPro.text = product.Protein.toString()


        }

    }

    fun submitList(list: MutableList<Product>){
        mealNutrientsSumList = list

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.meal_row_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return mealNutrientsSumList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){

            is ViewHolder -> { holder.bind(mealNutrientsSumList[position]) }

        }
    }
}