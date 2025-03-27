package com.example.jairosofttimesheet.ui.screens

import android.content.ContentValues
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jairosofttimesheet.R
import com.example.jairosofttimesheet.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import com.example.jairosofttimesheet.ui.theme.gradientDBlue
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: AttendanceViewModel = viewModel()) {

    val locationText = "Davao City" // Placeholder for location

    // Font setup
    val afacad = FontFamily(Font(R.font.afacad, FontWeight.Normal))
    val poppins = FontFamily(Font(R.font.poppinsregular, FontWeight.Normal))

    // Collect state from ViewModel
    val isClockedIn by viewModel.isClockedIn.collectAsState()

    //for download context
    val context = LocalContext.current

    // Fake attendance list with placeholder data
    val fakeAttendanceList = remember {
        mutableStateListOf(
            Triple("01/15/2025", "08:00 AM", "05:00 PM"),
            Triple("02/01/2025", "08:00 AM", "05:00 PM"),
            Triple("03/10/2025", "08:00 AM", "05:00 PM"),
            Triple("03/20/2025", "08:00 AM", "05:00 PM"),
            Triple("03/22/2025", "08:00 AM", "05:00 PM"),
            Triple("01/20/2025", "08:00 AM", "05:00 PM"),
            Triple("01/25/2025", "08:00 AM", "05:00 PM"),
            Triple("02/10/2025", "08:00 AM", "05:00 PM"),
            Triple("02/14/2025", "08:00 AM", "05:00 PM"),
            Triple("02/18/2025", "08:00 AM", "05:00 PM"),
            Triple("03/01/2025", "08:00 AM", "05:00 PM"),
            Triple("03/05/2025", "08:00 AM", "05:00 PM"),
            Triple("03/08/2025", "08:00 AM", "05:00 PM"),
            Triple("03/12/2025", "08:00 AM", "05:00 PM"),
            Triple("03/15/2025", "08:00 AM", "05:00 PM"),
            Triple("03/18/2025", "08:00 AM", "05:00 PM"),
            Triple("03/21/2025", "08:00 AM", "05:00 PM"),
            Triple("03/23/2025", "08:00 AM", "05:00 PM"),
            Triple("03/26/2025", "08:00 AM", "05:00 PM"),
            Triple("03/29/2025", "08:00 AM", "05:00 PM"),
            Triple("03/12/2025", "08:00 AM", "05:00 PM"),
            Triple("03/15/2025", "08:00 AM", "05:00 PM"),
            Triple("03/18/2025", "08:00 AM", "05:00 PM"),
            Triple("03/21/2025", "08:00 AM", "05:00 PM"),
            Triple("03/23/2025", "08:00 AM", "05:00 PM"),
            Triple("03/26/2025", "08:00 AM", "05:00 PM"),
            Triple("03/29/2025", "08:00 AM", "05:00 PM"),
        )
    }

    // For adding attendance and updating time out
    LaunchedEffect(isClockedIn) {
        if (isClockedIn) {
            val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date()) // Placeholder date
            val timeIn = "08:00 AM" // Placeholder time in
            fakeAttendanceList.add(Triple(date, timeIn, "--")) // Add a new record with placeholder data
        } else if (fakeAttendanceList.isNotEmpty() && fakeAttendanceList.last().third == "--") {
            val timeOut = "05:00 PM" // Placeholder time out
            val lastIndex = fakeAttendanceList.lastIndex
            fakeAttendanceList[lastIndex] = fakeAttendanceList[lastIndex].copy(third = timeOut) // Update last record with time out
        }
    }


        Box(modifier = Modifier.fillMaxSize().background(gradientDBlue)) {

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 10.dp, top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Attendance", fontFamily = afacad, color = Color.White)

                Image(
                    painter = painterResource(id = R.drawable.calendar),
                    contentDescription = "Calendar Icon",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.download),
                    contentDescription = "Download Attendance",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            saveAttendanceToDownloads(fakeAttendanceList, context)
                        }
                )
            }

            Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
                var showDatePicker by remember { mutableStateOf(false) }

                // Date and Time Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF666666))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Location",
                        fontFamily = poppins,
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f) // Takes 1 fraction of the space
                    )

                    Row(
                        modifier = Modifier
                            .clickable { showDatePicker = true }
                            .weight(1f), // Takes 1 fraction of the space
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date",
                            fontFamily = poppins,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Arrow",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 4.dp)
                        )
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = rememberDatePickerState())
                        }
                    }

                    Row(
                        modifier = Modifier.weight(2f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Time In",
                            fontFamily = poppins,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Time Out",
                            fontFamily = poppins,
                            fontSize = 11.sp,
                            color = Color.White,
                            modifier = Modifier.padding(end = 15.dp)
                        )
                    }
                }

                // Attendance List Scrollable Section
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    fakeAttendanceList.forEach { (date, timeIn, timeOut) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = locationText.take(10) + if (locationText.length > 10) "..." else "",
                                fontFamily = afacad,
                                color = Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 5.dp),
                                fontSize = 11.sp
                            )

                            Text(
                                text = date, // Placeholder for date
                                fontFamily = afacad,
                                color = Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 5.dp),
                                fontSize = 11.sp
                            )

                            Text(
                                text = timeIn, // Placeholder for time in
                                fontFamily = afacad,
                                color = Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 5.dp),
                                fontSize = 11.sp
                            )

                            Text(
                                text = if (timeOut == "--") " -- " else timeOut, // Placeholder for time out
                                fontFamily = afacad,
                                color = Color.White,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 5.dp, end = 6.dp)
                                    .run {
                                        if (timeOut == "--") {
                                            this.then(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp))
                                        } else {
                                            this
                                        }
                                    },
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }

@RequiresApi(Build.VERSION_CODES.Q)
fun saveAttendanceToDownloads(attendanceList: List<Triple<String, String, String>>, context: Context) {
    val fileName = "Attendance_${System.currentTimeMillis()}.txt"
    val fileContents = buildString {
        append("Attendance Record\n\n")
        append("Date\t\tTime In\t\tTime Out\n")
        append("=================================\n")
        attendanceList.forEach { (date, timeIn, timeOut) ->
            append("$date\t$timeIn\t$timeOut\n")
        }
    }

    try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream: OutputStream ->
                outputStream.write(fileContents.toByteArray())
            }
            Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_LONG).show()
        } ?: Toast.makeText(context, "Failed to save file", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving file", Toast.LENGTH_LONG).show()
    }
}






