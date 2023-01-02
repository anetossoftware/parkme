package com.anetos.parkme.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.AppColor


private val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.nunito_regular_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.nunito_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.nunito_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.nunito_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.nunito_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.nunito_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.nunito_bold_italic, FontWeight.Bold, FontStyle.Italic),
)

private val initialTypography = Typography()

val typography
    @Composable
    get() = Typography(
        initialTypography.displayLarge.copy(fontFamily = NunitoFontFamily),
        initialTypography.displayMedium.copy(fontFamily = NunitoFontFamily),
        initialTypography.displaySmall.copy(fontFamily = NunitoFontFamily),
        initialTypography.headlineLarge.copy(fontFamily = NunitoFontFamily),
        initialTypography.headlineMedium.copy(
            // Note Title TextField, Placeholder
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        ),
        initialTypography.headlineSmall.copy(fontFamily = NunitoFontFamily),
        initialTypography.titleLarge.copy(
            // Toolbar,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        ),
        initialTypography.titleMedium.copy(
            // Dialog Title, Widget Title, Button
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold, // Or SemiBold
            fontSize = 20.sp,
        ),
        initialTypography.titleSmall.copy(
            // Folder Title, Note Title, Label Title,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
        ),
        initialTypography.bodyLarge.copy(
            // Dialog Section, Dialog RadioButton Item, Icon Item, Slider Label, Settings Item
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        ),
        initialTypography.bodyMedium.copy(
            // Note Body TextField, Tab Item,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
        initialTypography.bodySmall.copy(
            // Folder Notes Count, Note Body, Settings Item Value, ClickableView,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        ),
        initialTypography.labelLarge.copy(
            // SubTitle in Toolbar, Dialog Item,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        ),
        initialTypography.labelMedium.copy(
            // Note Label, Note Reminder,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        ),
        initialTypography.labelSmall.copy(
            // Note Date
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp
        ),
    )

@Composable
fun AppColor.toColor() = when (this) {
    AppColor.Gray -> if (isSystemInDarkTheme()) Color(0xFFBDBDBD) else Color(0xFF757575)
    AppColor.Blue -> Color(0xFF42A5F5)
    AppColor.Pink -> Color(0xFFEC407A)
    AppColor.Cyan -> Color(0xFF26C6DA)
    AppColor.Purple -> Color(0xFFAB47BC)
    AppColor.Red -> Color(0xFFEF5350)
    AppColor.Yellow -> Color(0xFFFFA726)
    AppColor.Orange -> Color(0xFFD59E17)
    AppColor.Green -> Color(0xFF66BB6A)
    AppColor.Brown -> Color(0xFF8D6E63)
    AppColor.BlueGray -> Color(0xFF78909C)
    AppColor.Teal -> Color(0xFF26A69A)
    AppColor.Indigo -> Color(0xFF5C6BC0)
    AppColor.DeepPurple -> Color(0xFF7E57C2)
    AppColor.DeepOrange -> Color(0xFFFF7043)
    AppColor.DeepGreen -> Color(0xFF00C853)
    AppColor.LightBlue -> Color(0xFF40C4FF)
    AppColor.LightGreen -> Color(0xFF8BC34A)
    AppColor.LightRed -> Color(0xFFFF8A80)
    AppColor.LightPink -> Color(0xFFFF80AB)
    AppColor.Black -> if (isSystemInDarkTheme()) Color.White else Color.Black
    AppColor.Error -> ERROR
    AppColor.Success -> SUCCESS
    AppColor.Warning -> WARNING
}

val ColorScheme.warning: Color
    get() = Color(0xFFFFA726)
// Set of Material typography styles to start with
/*val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)*/