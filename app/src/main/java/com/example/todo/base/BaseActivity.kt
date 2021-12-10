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
import android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
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
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.fragment_a.*
import java.util.function.Consumer


abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int
) : AppCompatActivity() { 

    lateinit var binding: B
    abstract val viewModel: VM
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    //처음 권한..
    private val permissionlistener : PermissionListener = object: PermissionListener {
        override fun onPermissionGranted() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                TedPermission.with(applicationContext)
                    .setPermissionListener(permissionLocation)
                    .setRationaleMessage("백그라운드에서의 위치권한을 위하여 항상 허용으로 해주세요.")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다.\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION

                    ).check()
            } else Defines.log("권한 허용함.")
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Defines.log("권한 허용하지 않았음...")
            locationCheckPermissions()
        }
    }
    /*
                Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_PHONE_NUMBERS,
                        Manifest.permission.ACTIVITY_RECOGNITION
                 */
    //두번째 위치 항상 허용권한
    val permissionLocation : PermissionListener = object: PermissionListener {
        override fun onPermissionGranted() {
            Defines.log("Location 권한 허용함.")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                TedPermission.with(applicationContext)
                    .setPermissionListener(permissionBodyInfo)
                    .setRationaleMessage("정확한 위치 계산을 위하여 신체정보를 필요로합니다.")
                    .setDeniedMessage("선택적 권한입니다.\n필요시 [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(
                        Manifest.permission.ACTIVITY_RECOGNITION

                    ).check()
            }
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Defines.log("권한 허용하지 않았음...")
            locationCheckPermissions()
        }
    }

    //세번째 권한 허용
    val permissionBodyInfo : PermissionListener = object: PermissionListener {
        override fun onPermissionGranted() {
            Defines.log("permissionBodyInfo 권한 허용함.")
            batteryOptimization()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Defines.log("permissionBodyInfo 권한 허용하지 않았음...")
            batteryOptimization()
        }
    }

    private fun locationCheckPermissions() {

        TedPermission.with(applicationContext)
            .setPermissionListener(permissionlistener)
            .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
            .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다.\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
            .setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION

            ).check()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.setVariable(BR.vm, viewModel)
        binding.lifecycleOwner = this

        locationCheckPermissions()

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


    fun showToast(message: String) =
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

    private fun batteryOptimization() {

        val pm = getSystemService(PowerManager::class.java)
        if (pm != null && !pm.isIgnoringBatteryOptimizations(packageName)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("배터리 사용량 최적화 제외")
                    .setCancelable(true)
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


}