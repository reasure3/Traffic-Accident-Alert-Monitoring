package com.swengineering.team1.traffic_accident.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.InputStream

@Preview
@Composable
fun AdminScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Text("Admin Screen", modifier)

    UploadButton()
}

@Composable
fun UploadButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Button(onClick = {
        scope.launch {
            try {
                val assetManager = context.assets
                Log.d("csv", "open")
                val inputStream = assetManager.open("US_Accidents_March23_sampled_500k.csv")
                Log.d("csv", "done")
                updateAccidentHashesFromCsv(context, inputStream)
                Toast.makeText(context, "업로드 완료", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("csv", "업로드 실패", e)
                Toast.makeText(context, "오류: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }) {
        Text("위험 지점 업로드")
    }
}

suspend fun updateAccidentHashesFromCsv(context: Context, inputStream: InputStream) {
    val db = FirebaseFirestore.getInstance("traffic-data")
    val collectionRef = db.collection("accident")
    csvReader().openAsync(inputStream) {
        readAllWithHeaderAsSequence().chunked(500).forEachIndexed { index, chunk ->
            var batch: WriteBatch = db.batch()
            Log.d("csv", "Start Batch #${index + 1}, size=${chunk.size}")
            chunk.forEach { row ->
                val id = row["ID"]?.toString()!!
                val startLat = row["Start_Lat"]?.toDouble()!!
                val startLng = row["Start_Lng"]?.toDouble()!!
                val endLat = row["End_Lat"]?.toDouble()!!
                val endLng = row["End_Lng"]?.toDouble()!!

                val startHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(startLat, startLng))
                val endHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(endLat, endLng))

                val updates = mapOf(
                    "position" to mapOf(
                        "start_hash" to startHash,
                        "end_hash" to endHash
                    ),
                    "start_hash" to FieldValue.delete(),
                    "end_hash" to FieldValue.delete(),
                )

                batch = batch.set(collectionRef.document(id), updates, SetOptions.merge())
            }
            batch.commit().await()
            Log.d("csv", "End Batch #${index + 1} commit, size=${chunk.size}")
        }
        Log.d("csv", "All done")
    }
}
