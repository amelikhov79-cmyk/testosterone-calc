package com.example.testosteronecalc

const val NG_ML_TO_NMOL_L = 3.467
const val NG_DL_TO_NMOL_L = 0.03467
const val PG_ML_TO_NMOL_L = 0.003467

enum class Unit(val label: String) {
    NMOL_L("нмоль/л"),
    NG_ML("нг/мл"),
    NG_DL("нг/дл"),
    PG_ML("пг/мл")
}

fun toNmolL(value: Double, from: Unit): Double = when (from) {
    Unit.NMOL_L -> value
    Unit.NG_ML -> value * NG_ML_TO_NMOL_L
    Unit.NG_DL -> value * NG_DL_TO_NMOL_L
    Unit.PG_ML -> value * PG_ML_TO_NMOL_L
}

fun fromNmolL(value: Double, to: Unit): Double = when (to) {
    Unit.NMOL_L -> value
    Unit.NG_ML -> value / NG_ML_TO_NMOL_L
    Unit.NG_DL -> value / NG_DL_TO_NMOL_L
    Unit.PG_ML -> value / PG_ML_TO_NMOL_L
}

fun convert(value: Double, from: Unit, to: Unit): Double =
    fromNmolL(toNmolL(value, from), to)
