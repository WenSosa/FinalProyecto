package com.andrea.finalproyectott.listeners

import com.andrea.finalproyectott.models.Alimentos

abstract class RecyclerAlimentosListener {
    abstract fun onSelect(aliemntos: Alimentos, position : Int)

    abstract fun onClick(aliemntos: Alimentos, position : Int)

}