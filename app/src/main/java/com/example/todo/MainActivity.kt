package com.example.todo

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController

import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.todo.base.BaseActivity
import com.example.todo.common.Defines
import com.example.todo.databinding.ActivityMainBinding
import com.example.todo.model.domain.Todo
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.todo.vm.AViewModel
import android.os.PowerManager

import android.content.Intent
import android.net.Uri

import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog


class MainActivity : BaseActivity<ActivityMainBinding, AViewModel>(
    R.layout.activity_main
) {

    private lateinit var mNavController: NavController
    private lateinit var mToolbar: Toolbar;

    override val viewModel: AViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        batteryOptimization()
        setUpToolbar()
        setUpNavigation()

        if (savedInstanceState != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }


    private fun setUpNavigation() {
        mNavController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        setupActionBarWithNavController(mNavController, null)
    }

    private fun setUpToolbar() {
        mToolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(mToolbar)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mToolbar.inflateMenu(R.menu.main_toolbar)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_add -> {
            mNavController.navigate(R.id.action_AFragment_to_BFragment)
            true
        }

        R.id.action_remove -> {
            val tempArr = ArrayList<Int>()

            for (row in viewModel.todoList.value!!) {
                if (row.isCheck) {
                    tempArr.add(row.id!!.toInt())
                }
            }

            viewModel.removeList.value = tempArr
            true
        }

        R.id.success -> {

            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun clearToolbarMenu() {
        mToolbar.menu.clear()
    }

    override fun onSupportNavigateUp(): Boolean {

        this.clearToolbarMenu()
        mToolbar.inflateMenu(R.menu.main_toolbar)

        mNavController.navigateUp()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        mNavController.navigateUp()
    }

    private fun batteryOptimization(){
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