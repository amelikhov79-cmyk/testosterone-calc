package com.example.testosteronecalc

enum class Category(val label: String) {
    MALE_ADULT("Мужчины 18+"),
    FEMALE_ADULT("Женщины 18+"),
    BOY("Мальчики (пубертат)"),
    GIRL("Девочки (пубертат)")
}

data class Range(val low: Double, val high: Double)

fun referenceRange(category: Category): Range = when (category) {
    Category.MALE_ADULT -> Range(12.0, 35.0)
    Category.FEMALE_ADULT -> Range(0.5, 2.6)
    Category.BOY -> Range(0.5, 25.0)
    Category.GIRL -> Range(0.5, 2.0)
}

enum class Status(val label: String, val color: Long) {
    LOW("Понижен", 0xFFE53935),
    NORMAL("Норма", 0xFF43A047),
    HIGH("Повышен", 0xFFFB8C00)
}

fun evaluate(valueNmol: Double, category: Category): Status {
    val r = referenceRange(category)
    return when {
        valueNmol < r.low -> Status.LOW
        valueNmol > r.high -> Status.HIGH
        else -> Status.NORMAL
    }
}

// ============================================================
// Возрастные группы для СВОБОДНОГО тестостерона (нмоль/л)
// Источник: unclinic.ru — таблица референсов
// ============================================================

enum class AgeGroup(
    val label: String,
    val low: Double,
    val high: Double
) {
    M_18_29("Мужчины 18–29 лет", 0.25, 0.65),
    M_30_39("Мужчины 30–39 лет", 0.22, 0.57),
    M_40_49("Мужчины 40–49 лет", 0.19, 0.52),
    M_50_59("Мужчины 50–59 лет", 0.15, 0.45),
    M_60_PLUS("Мужчины 60+ лет", 0.12, 0.40),

    F_REPRO("Женщины 18–45 лет (репродуктивный)", 0.07, 0.49),
    F_PERIMENO("Женщины 45–55 лет (перименопауза)", 0.05, 0.35),
    F_POSTMENO("Женщины 55+ лет (постменопауза)", 0.03, 0.25),

    TEEN_BOY("Мальчики 12–18 лет (пубертат)", 0.10, 0.60),
    TEEN_GIRL("Девочки 12–18 лет (пубертат)", 0.05, 0.30),

    CHILD_BOY("Мальчики до пубертата", 0.0, 0.10),
    CHILD_GIRL("Девочки до пубертата", 0.0, 0.05)
}

fun freeTestosteroneRangeNmol(group: AgeGroup): Range =
    Range(group.low, group.high)

fun evaluateFree(ftNmol: Double, group: AgeGroup): Status {
    return when {
        ftNmol < group.low -> Status.LOW
        ftNmol > group.high -> Status.HIGH
        else -> Status.NORMAL
    }
}

// ============================================================
// Доли фракций от общего тестостерона (%)
// Свободный: 1,5–3,5 %; Биодоступный: 30–60 %
// ============================================================

fun evaluateFreePercent(percent: Double): Status = when {
    percent < 1.5 -> Status.LOW
    percent > 3.5 -> Status.HIGH
    else -> Status.NORMAL
}

fun evaluateBioPercent(percent: Double): Status = when {
    percent < 30.0 -> Status.LOW
    percent > 60.0 -> Status.HIGH
    else -> Status.NORMAL
}
