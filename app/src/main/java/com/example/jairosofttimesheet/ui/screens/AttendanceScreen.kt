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
import com.example.jairosofttimesheet.data.remote.LogEntry
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
    val attendanceLogs by viewModel.attendanceLogs.collectAsState()

    val afacad = FontFamily(Font(R.font.afacad, FontWeight.Normal))
    val poppins = FontFamily(Font(R.font.poppinsregular, FontWeight.Normal))

    LaunchedEffect(Unit) {
        viewModel.fetchAttendanceFromApi()
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
                        saveAttendanceToDownloads(attendanceLogs, context)
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
                Text("User ID", fontFamily = poppins, fontSize = 11.sp, color = Color.White, modifier = Modifier.weight(1f))

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
                attendanceLogs.forEach { log ->
                    val formattedTimeIn = formatUnixTime(log.timeIn)
                    val formattedTimeOut = if (log.timeOut != 0L) formatUnixTime(log.timeOut) else "--"

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.userId.take(10) + if (log.userId.length > 10) "..." else "", fontFamily = afacad, color = Color.White, modifier = Modifier.weight(1f).padding(end = 5.dp), fontSize = 11.sp)
                        Text(log.date, fontFamily = afacad, color = Color.White, modifier = Modifier.weight(1f).padding(end = 5.dp), fontSize = 11.sp)
                        Text(formattedTimeIn, fontFamily = afacad, color = Color.White, modifier = Modifier.weight(1f).padding(end = 5.dp), fontSize = 11.sp)
                        Text(formattedTimeOut, fontFamily = afacad, color = Color.White, modifier = Modifier.weight(1f).padding(start = 5.dp, end = 6.dp), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

fun formatUnixTime(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(date)
    } catch (e: Exception) {
        "--"
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun saveAttendanceToDownloads(logs: List<LogEntry>, context: Context) {
    val fileName = "Attendance_${System.currentTimeMillis()}.txt"
    val fileContents = buildString {
        append("Attendance Record\n\n")
        append("User ID\t\tDate\t\tTime In\t\tTime Out\n")
        append("===========================================\n")
        logs.forEach {
            val timeIn = if (it.timeIn == 0L) "--" else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it.timeIn))
            val timeOut = if (it.timeOut == 0L) "--" else SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(it.timeOut))
            append("${it.userId}\t${it.date}\t$timeIn\t$timeOut\n")
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
