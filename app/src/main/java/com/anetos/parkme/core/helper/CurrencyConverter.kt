package com.anetos.parkme.core.helper

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

fun formatExchangeRate(exchangeRate: Float): String {
    val decimalFormat = DecimalFormat("0.00")
    decimalFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.CANADA)
    val amountFormat = decimalFormat.format(exchangeRate.toDouble())
    return String.format("%s", amountFormat)
}

fun formatAmount(currency: Currency, value: Double): String {
    val instance = NumberFormat.getInstance(Locale.CANADA) as DecimalFormat
    instance.maximumFractionDigits = currency.defaultFractionDigits
    instance.minimumFractionDigits = currency.defaultFractionDigits
    instance.roundingMode = RoundingMode.FLOOR
    val format = instance.format(value)
    return format.plus(" ") + currency.currencyCode.uppercase(Locale.getDefault())
}