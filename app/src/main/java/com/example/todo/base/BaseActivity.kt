package com.example.todo.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.todo.BR
import com.example.todo.BuildConfig
import com.example.todo.common.Defines
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_a.*
import java.util.function.Consumer


abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int
) : AppCompatActivity() {

    lateinit var binding: B
    abstract val viewModel: VM
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.setVariable(BR.vm, viewModel)
        binding.lifecycleOwner = this

        if (requestPermissions()) {
            //batteryOptimization()
            checkBatteryOptimization {
                if (it) {
                    Defines.log("abc")
                    requestPermissionReadPhone()

                } else {
                    Defines.log("cbd")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    return
                }
            }
            Defines.log("phoneNum-> ${getPhoneNumber()}")
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val REQUEST_PERMISSION_READ_PHONE_STATE: Int = 200

    fun requestPermissionReadPhone() {
        var permissions = arrayOf(Manifest.permission.READ_PHONE_NUMBERS)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            permissions += arrayOf(Manifest.permission.READ_PHONE_STATE)
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_READ_PHONE_STATE);
    }

    private fun requestPermissions(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
                    Defines.log("denied!!!!!!!")
                    permissionDialog(this)
                    return false
                }
                return true
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }

        var permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION

        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions += Manifest.permission.ACTIVITY_RECOGNITION
        }


        ActivityCompat.requestPermissions(this, permissions, 0)

        return false
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty()) {
                    var isAllGranted = true // 요청한 권한 허용/거부 상태 한번에 체크
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }

                    if (isAllGranted) {
                        // 다음 step으로 ~
                        //backgroundPermission()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            permissionDialog(this)
                        } else {
                            batteryOptimization()
                        }

                        Defines.log("isAll success")

                    } // 허용하지 않은 권한이 있음. 필수권한/선택권한 여부에 따라서 별도 처리를 해주어야 함.
                    else {
                        Defines.log("else~")
                         if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                            || !ActivityCompat.shouldShowRequestPermissionRationale(
                                this, Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                             permissionGrant()
                        } else {
                            Defines.log("접근 권한 거부하였음.")
                             permissionGrant()
                            // 접근 권한 거부하였음.
                        }
                    }

                } // 요청한 권한을 모두 허용했음.
                Defines.log("요청한 권한을 모두 허용했음.")

            }
            REQUEST_PERMISSION_READ_PHONE_STATE-> {
                if (grantResults.isNotEmpty()) {
                    val locationPermissions = grantResults[0] === PackageManager.PERMISSION_GRANTED
                    if (locationPermissions) {
                        val phoneNumber = getPhoneNumber()
                        Defines.log("number : $phoneNumber")
                    } else {
                        Defines.log("onRequestPermissionsResult() _ 전화번호 권한 거부")

                        // 이벤트 처리
                        requestPermissionReadPhone()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getPhoneNumber(): String
    {
        var teleManager: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (teleManager.getLine1Number() == null) {
            return ""
        }
        return teleManager.getLine1Number().toString()
    }

    private fun permissionGrant() {

        var builder = AlertDialog.Builder(this)
        builder.setTitle("거부된 권한을 허용하여 주세요.")
        builder.setCancelable(false)

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when (p1) {
                DialogInterface.BUTTON_POSITIVE -> {
                    // 다시 묻지 않기 체크하면서 권한 거부 되었음.
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                    startActivity(intent)
                    finish()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    Defines.log("취소.")
                    finish()
                }
            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", listener)

        builder.show()
    }


    // 백그라운드 권한 요청
    private fun permissionDialog(context: Context) {
        var builder = AlertDialog.Builder(context)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")
        builder.setCancelable(false)

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when (p1) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID))
                    startActivity(intent)
                    finish()
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    Defines.log("취소.")
                    finish()
                }
            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", listener)

        builder.show()
    }

    private fun backgroundPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ), 2
        )
    }

    fun showToast(message: String) =
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

    private fun batteryOptimization() {

        val pm = getSystemService(PowerManager::class.java)
        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("배터리 사용량 최적화 제외")
                    .setMessage("안정적인 어플 사용을 위해서 해당 어플을 \"배터리 사용량 최적화\" 목록에서 제외하는 권한이 필요합니다. 계속하시겠습니까?")
                    .setPositiveButton("예") { dialog, which ->
                        val intent = Intent()
                        val packageName = packageName
                        val pm = getSystemService(POWER_SERVICE) as PowerManager
                        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                            @SuppressLint("BatteryLife")
                            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                            intent.data = Uri.parse("package:$packageName")
                            startActivity(intent)
                        }
                    }
                    .setNeutralButton("취소", null)
                    .create()

                alertDialog.show()
            }
        }
    }


    @SuppressLint("BatteryLife", "InlinedApi")
    private fun checkBatteryOptimization(callback: Consumer<Boolean>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // 버전 체크
            Log.d("TAG 1", "checkBatteryOptimization: The version is too low to be checked.")
            callback.accept(true)
            return
        }
        val packageName = application.packageName
        // since REQUEST_IGNORE_BATTERY_OPTIMIZATIONS is **not** dangerous permission,
        // but we need to check that app has `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` permission.
        if (PackageManager.PERMISSION_GRANTED != application.packageManager
                .checkPermission(
                    Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    application.packageName
                )
        ) { // 권한 체크
            Log.d(
                "TAG2",
                "checkBatteryOptimization: application hasn't REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission"
            )
            return
        }
        val powerManager = application.getSystemService(POWER_SERVICE) as PowerManager
        val ignoringBatteryOptimizations = powerManager.isIgnoringBatteryOptimizations(packageName)
        if (ignoringBatteryOptimizations) { // 예외사항에 이미 추가되었는지 확인
            Log.d("TAG3", "checkBatteryOptimization: Already ignored Battery Optimizations.")
            callback.accept(true)
            return
        }
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse(String.format("package:%s", packageName))
        startActivity(intent)
    }


}