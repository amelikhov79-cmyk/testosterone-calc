package com.example.testosteronecalc

import kotlin.math.sqrt

/**
 * Расчёт свободного тестостерона по формуле Vermeulen et al. (1999).
 *
 * Входные данные:
 * @param totalT_nmol_l  общий тестостерон, нмоль/л
 * @param shbg_nmol_l    ГСПГ (SHBG), нмоль/л
 * @param albumin_g_l    альбумин, г/л (по умолчанию 43 г/л = 4.3 г/дл)
 *
 * @return свободный тестостерон в пг/мл
 */
fun calculateFreeTestosterone(
    totalT_nmol_l: Double,
    shbg_nmol_l: Double,
    albumin_g_l: Double = 43.0
): Double {
    if (totalT_nmol_l <= 0 || shbg_nmol_l <= 0 || albumin_g_l <= 0) return 0.0

    // Константы ассоциации (Vermeulen 1999)
    val KaT_alb = 3.6e4   // л/моль
    val KaT_shbg = 1.0e9  // л/моль

    // Переводим в моль/л
    val T = totalT_nmol_l * 1e-9
    val S = shbg_nmol_l * 1e-9
    val A = albumin_g_l / 66430.0

    // Квадратичное уравнение относительно свободного T
    val aa = KaT_shbg * (KaT_alb * A + 1.0)
    val bb = KaT_shbg * (S - T) + KaT_alb * A + 1.0
    val cc = -T

    val disc = bb * bb - 4.0 * aa * cc
    if (disc < 0) return 0.0

    val FT_mol = (-bb + sqrt(disc)) / (2.0 * aa)
    val FT_nmol = FT_mol * 1e9
    val FT_pg_ml = FT_nmol * 288.42

    return FT_pg_ml
}
