package com.example.jairosofttimesheet.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jairosofttimesheet.R
import com.example.jairosofttimesheet.ui.theme.gradientDBlue

@Composable
fun TimesheetScreen(navController: NavController) {
    val daysOfWeek = listOf("M", "T", "W", "Th", "F", "S", "S")
    val workedHours = mapOf(
        //(hours, mins)
        "M" to Pair(8, 5),
        "T" to Pair(8, 2),
        "W" to Pair(8, 0),
        "Th" to Pair(8, 10),
        "F" to Pair(1, 0),
        "S" to null,
        "S" to null
    )

    val poppins = FontFamily(
        Font(R.font.poppinsregular, FontWeight.Normal)
    )

    val afacad = FontFamily(
        Font(R.font.afacad, FontWeight.Normal),
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(gradientDBlue)
        .padding(16.dp))
        {
        // Header Text
        Text(text = "Timesheet", fontFamily = poppins, fontSize = 20.sp, color = Color(color = 0xFFFFFFFF))

        Spacer(modifier = Modifier.height(16.dp))

        // Days of the week
        daysOfWeek.forEach { day ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day,
                    fontFamily = afacad,
                    color = Color(color = 0xFFFFFFFF),
                    modifier = Modifier.weight(0.5f),
                    fontSize = 14.sp
                )

                if (workedHours[day] != null) {
                    val (hours, minutes) = workedHours[day]!!

                    Box(
                        modifier = Modifier
                            .weight(2f)  // Ensure the Box expands correctly
                            .height(30.dp)
                            .background(Color(0xFF203859), RoundedCornerShape(4.dp))
                            .padding(start = 8.dp)
                    ) {
                        val progress = (hours + minutes / 60f) / 8f
                        val progressWidth = 200.dp * progress

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(progressWidth)
                                .background(Color(0xFF61C0BF), RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "${hours}hrs ${minutes}mins",
                                fontFamily = afacad,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    // Placeholder for no work on some days
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(30.dp)
                            .background(Color(0xFF9E9E9E), RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}





