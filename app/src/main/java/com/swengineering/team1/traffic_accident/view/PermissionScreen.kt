package com.swengineering.team1.traffic_accident.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.swengineering.team1.traffic_accident.R

@Composable
fun PermissionAwareScreen(
    onPermissionsGranted: @Composable () -> Unit,  // 권한 있을 때 보여줄 화면
    onPermissionsDenied: @Composable (request: () -> Unit) -> Unit,  // 권한 없을 때 보여줄 화면, 버튼 클릭 콜백 파라미터로 전달
    permissions: Array<String>,
    msgToSetting: String = stringResource(R.string.request_permission_msg_default)
) {
    val context = LocalContext.current

    // 1) 상태 변수: 권한이 있는지 없는지
    var hasPermissions by remember { mutableStateOf(false) }


    // 3) 퍼미션 요청 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        // 모든 퍼미션이 허용되면 true
        hasPermissions = resultMap.values.all { it }
        if (!hasPermissions) {
            Toast.makeText(context, msgToSetting, Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                // Activity가 아닐 수도 있으니 FLAG 추가
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    // 4) 최초 컴포즈 시, 현재 퍼미션 상태 읽어오기
    LaunchedEffect(Unit) {
        hasPermissions = permissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 5) UI 분기
    if (hasPermissions) {
        // 권한이 있을 때
        onPermissionsGranted()
    } else {
        // 권한이 없을 때
        onPermissionsDenied {
            launcher.launch(permissions)
        }
    }
}