package com.kfc.onlinestore.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.kfc.onlinestore.ui.theme.BlackText
import com.kfc.onlinestore.ui.theme.FilterButtonColor
import com.kfc.onlinestore.ui.theme.PinkBack

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) PinkBack else FilterButtonColor,
            contentColor = if (isSelected) FilterButtonColor else PinkBack
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .height(35.dp)
            .padding(horizontal = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Text(text, fontSize = 14.sp)
    }
}