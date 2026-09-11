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

data class FreeRange(val low: Double, val high: Double)

fun freeTestosteroneRange(category: Category): FreeRange = when (category) {
    Category.MALE_ADULT -> FreeRange(9.0, 30.0)
    Category.FEMALE_ADULT -> FreeRange(0.1, 6.4)
    Category.BOY -> FreeRange(0.1, 25.0)
    Category.GIRL -> FreeRange(0.1, 5.0)
}
