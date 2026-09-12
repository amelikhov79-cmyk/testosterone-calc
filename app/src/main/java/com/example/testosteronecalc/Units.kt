package com.example.testosteronecalc

const val NG_ML_TO_NMOL_L = 3.467
const val NG_DL_TO_NMOL_L = 0.03467
const val PG_ML_TO_NMOL_L = 0.003467

enum class TUnit(val label: String) {
    NMOL_L("нмоль/л"),
    NG_ML("нг/мл"),
    NG_DL("нг/дл"),
    PG_ML("пг/мл")
}

fun toNmolL(value: Double, from: TUnit): Double = when (from) {
    TUnit.NMOL_L -> value
    TUnit.NG_ML -> value * NG_ML_TO_NMOL_L
    TUnit.NG_DL -> value * NG_DL_TO_NMOL_L
    TUnit.PG_ML -> value * PG_ML_TO_NMOL_L
}

fun fromNmolL(value: Double, to: TUnit): Double = when (to) {
    TUnit.NMOL_L -> value
    TUnit.NG_ML -> value / NG_ML_TO_NMOL_L
    TUnit.NG_DL -> value / NG_DL_TO_NMOL_L
    TUnit.PG_ML -> value / PG_ML_TO_NMOL_L
}

fun convert(value: Double, from: TUnit, to: TUnit): Double =
    fromNmolL(toNmolL(value, from), to)
