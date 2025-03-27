package com.example.jairosofttimesheet.ui.screens

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jairosofttimesheet.R
import com.example.jairosofttimesheet.data.model.Attendance
import com.example.jairosofttimesheet.ui.theme.gradientDBlue
import com.example.jairosofttimesheet.viewmodel.AttendanceViewModel
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(viewModel: AttendanceViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val attendanceList by viewModel.attendanceList.collectAsState()
    val isClockedIn by viewModel.isClockedIn.collectAsState()

    val afacad = FontFamily(Font(R.font.afacad, FontWeight.Normal))
    val poppins = FontFamily(Font(R.font.poppinsregular, FontWeight.Normal))

    LaunchedEffect(Unit) {
        viewModel.fetchAttendanceFromApi()
    }

    LaunchedEffect(isClockedIn) {
        if (isClockedIn) {
            val date = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
            val timeIn = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            viewModel.addAttendance("Davao City", date, timeIn)
        } else {
            viewModel.updateTimeOut()
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
                modifier = Modifier.size(24.dp).padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.download),
                contentDescription = "Download Attendance",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        saveAttendanceToDownloads(attendanceList, context)
                    }
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp)) {
            var showDatePicker by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF666666)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Location", fontFamily = poppins, fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.clickable { showDatePicker = true }.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Date", fontFamily = poppins, fontSize = 11.sp, color = Color.White)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White, modifier = Modifier.padding(start = 4.dp))
                }

                if (showDatePicker) {
                    DatePickerDialog(onDismissRequest = { showDatePicker = false },
                        confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
                        dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }) {
                        DatePicker(state = rememberDatePickerState())
                    }
                }

                Row(modifier = Modifier.weight(2f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Time In", fontFamily = poppins, fontSize = 11.sp, color = Color.White)
                    Text("Time Out", fontFamily = poppins, fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(end = 15.dp))
                }
            }

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f)
            ) {
                attendanceList.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.location.take(10) + if (item.location.length > 10) "..." else "", fontFamily = afacad, color = Color.White, modifier = Modifier.weight(1f).padding(end = 5.dp), fontSize = 11.sp)
                        Text(item.date, fontFamily = afacad, color = Color.White, modifier = Modifier.weight(1f).padding(end = 5.dp), fontSize = 11.sp)
                        Text(item.timeIn, fontFamily = afacad, color = Color.White, modifier = Modifier.weight(1f).padding(end = 5.dp), fontSize = 11.sp)
                        Text(
                            if (item.timeOut == "--") " -- " else item.timeOut,
                            fontFamily = afacad,
                            color = Color.White,
                            modifier = Modifier.weight(1f).padding(start = 5.dp, end = 6.dp).run {
                                if (item.timeOut == "--") {
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
fun saveAttendanceToDownloads(attendanceList: List<Attendance>, context: Context) {
    val fileName = "Attendance_${System.currentTimeMillis()}.txt"
    val fileContents = buildString {
        append("Attendance Record\n\n")
        append("Date\t\tTime In\t\tTime Out\n")
        append("=================================\n")
        attendanceList.forEach {
            append("${it.date}\t${it.timeIn}\t${it.timeOut}\n")
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
