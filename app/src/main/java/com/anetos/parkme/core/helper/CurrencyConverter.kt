package com.anetos.parkme.core.helper

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

object CurrencyConverter {
}

private const val CURRENCY_CODE_USD = "USD"


fun formatExchangeRate(exchangeRate: Float): String {
    val decimalFormat = DecimalFormat("0.00")
    decimalFormat.decimalFormatSymbols = DecimalFormatSymbols(Locale.CANADA)
    val amountFormat = decimalFormat.format(exchangeRate.toDouble())
    return String.format("%s", amountFormat)
}

fun formatAmount(currency: Currency, value: Int): String {
    val instance = NumberFormat.getInstance(Locale.CANADA) as DecimalFormat
    val format = instance.format(value.toLong())
    return currency.currencyCode.uppercase(Locale.getDefault()) + " " + format
}

fun formatAmount(currency: Currency, value: Double): String {
    val instance = NumberFormat.getInstance(Locale.CANADA) as DecimalFormat
    instance.maximumFractionDigits = currency.defaultFractionDigits
    instance.minimumFractionDigits = currency.defaultFractionDigits
    instance.roundingMode = RoundingMode.FLOOR
    val format = instance.format(value)
    return format.plus(" ") + currency.currencyCode.uppercase(Locale.getDefault())
}

fun formatAmountWithoutCurrencyCode(
    currency: Currency?,
    value: Double,
    roundingMode: RoundingMode?
): String? {
    var currency = currency
    if (currency == null) {
        currency = Currency.getInstance(CURRENCY_CODE_USD)
    }
    val instance = NumberFormat.getInstance(Locale.CANADA) as DecimalFormat
    instance.maximumFractionDigits = currency!!.defaultFractionDigits
    instance.minimumFractionDigits = currency.defaultFractionDigits
    instance.roundingMode = roundingMode
    return instance.format(value)
}

fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toDouble()
}

fun formatValueWithTwoDecimal(value: Double?): String {
    val decimalFormat = DecimalFormat("0.00")
    val amountFormat = decimalFormat.format(value)
    return String.format("%s", amountFormat)
}