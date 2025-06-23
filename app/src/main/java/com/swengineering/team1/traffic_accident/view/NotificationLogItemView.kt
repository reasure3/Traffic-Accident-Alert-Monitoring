package com.swengineering.team1.traffic_accident.view

import android.icu.text.DateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swengineering.team1.traffic_accident.R
import com.swengineering.team1.traffic_accident.model.notification.NotificationLogItem

@Composable
fun NotificationLogItemView(item: NotificationLogItem) {
    val dateFormat =
        remember { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT) }
    val timeString = dateFormat.format(item.timestamp)

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(timeString, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "${item.street}, ${item.city}, ${item.state}, ${item.country}",
            fontWeight = FontWeight.Bold
        )
        Text(stringResource(R.string.notification_log_item_severity, item.severity))
        Text(
            stringResource(
                R.string.notification_log_item_coord,
                item.location?.latitude ?: "null",
                item.location?.longitude ?: "null"
            )
        )
    }
}