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


abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(
    @LayoutRes private val layoutResId: Int
) : AppCompatActivity() {

    lateinit var binding: B
    abstract val viewModel: VM;
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.setVariable(BR.vm, viewModel)
        binding.lifecycleOwner = this

        if (requestPermissions()) {
            batteryOptimization()
        }

    }

    override fun onResume() {
        super.onResume()
        Defines.log("onResume")

    }

    override fun onPause() {
        super.onPause()
        Defines.log("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Defines.log("onDestroy")

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

        val permissions: Array<String> = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION
        )

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
        }
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

}