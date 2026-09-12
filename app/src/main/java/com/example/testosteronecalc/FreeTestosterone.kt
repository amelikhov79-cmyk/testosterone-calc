package com.example.testosteronecalc

import kotlin.math.sqrt

/**
 * Результат расчёта фракций тестостерона по методу Vermeulen (1999).
 *
 * @param freeNmolL         свободный тестостерон, нмоль/л
 * @param bioavailableNmolL биодоступный (свободный + альбумин-связанный), нмоль/л
 */
data class TestosteroneFractions(
    val freeNmolL: Double,
    val bioavailableNmolL: Double
)

/**
 * Расчёт свободного и биодоступного тестостерона по Vermeulen et al. (1999).
 *
 * @param totalT_nmol_l  общий тестостерон, нмоль/л
 * @param shbg_nmol_l    ГСПГ (SHBG), нмоль/л
 * @param albumin_g_l    альбумин, г/л (по умолчанию 43 г/л = 4.3 г/дл)
 */
fun calculateFractions(
    totalT_nmol_l: Double,
    shbg_nmol_l: Double,
    albumin_g_l: Double = 43.0
): TestosteroneFractions {
    if (totalT_nmol_l <= 0 || shbg_nmol_l <= 0 || albumin_g_l <= 0) {
        return TestosteroneFractions(0.0, 0.0)
    }

    val KaT_alb = 3.6e4
    val KaT_shbg = 1.0e9

    val T = totalT_nmol_l * 1e-9
    val S = shbg_nmol_l * 1e-9
    val A = albumin_g_l / 66430.0

    // Свободный T (моль/л)
    val aa = KaT_shbg * (KaT_alb * A + 1.0)
    val bb = KaT_shbg * (S - T) + KaT_alb * A + 1.0
    val cc = -T

    val disc = bb * bb - 4.0 * aa * cc
    if (disc < 0) return TestosteroneFractions(0.0, 0.0)

    val FT_mol = (-bb + sqrt(disc)) / (2.0 * aa)
    val FT_nmol = FT_mol * 1e9

    // Тестостерон, связанный с альбумином:
    // T_alb = KaT_alb * A * FT
    val TAlb_mol = KaT_alb * A * FT_mol
    val TAlb_nmol = TAlb_mol * 1e9

    val bioNmol = FT_nmol + TAlb_nmol

    return TestosteroneFractions(
        freeNmolL = FT_nmol,
        bioavailableNmolL = bioNmol
    )
}

/**
 * Старая функция — оставлена для совместимости, возвращает пг/мл.
 * В новом коде используйте calculateFractions().
 */
fun calculateFreeTestosterone(
    totalT_nmol_l: Double,
    shbg_nmol_l: Double,
    albumin_g_l: Double = 43.0
): Double {
    val f = calculateFractions(totalT_nmol_l, shbg_nmol_l, albumin_g_l)
    return f.freeNmolL * 288.42
}
