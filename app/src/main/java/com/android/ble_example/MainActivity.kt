package com.android.ble_example

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.ble_example.presentation.permission.PermissionHandler
import com.android.ble_example.presentation.screen.BLEScreen
import com.android.ble_example.presentation.viewmodel.BLEViewModel
import com.android.ble_example.ui.theme.BLEExampleTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionHandler: PermissionHandler

    private val viewModel: BLEViewModel by viewModels()

    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private val requestBluetoothEnable = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            checkAndRequestPermissions()
        } else {
            Toast.makeText(this, "Bluetooth is required for this app", Toast.LENGTH_LONG).show()
        }
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some permissions were denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BLEExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        Column(
                            modifier = Modifier.padding(it)
                        ) {
                            BLEScreen(
                                viewModel = viewModel,
                                onRequestPermissions = { checkAndRequestPermissions() }
                            )
                        }
                    }
                }
            }
        }

        // Check Bluetooth and permissions on startup
        checkBluetoothAndPermissions()
    }

    private fun checkBluetoothAndPermissions() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG)
                .show()
            return
        }

        if (!bluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetoothEnable.launch(enableBtIntent)
        } else {
            checkAndRequestPermissions()
        }
    }

    private fun checkAndRequestPermissions() {
        if (permissionHandler.hasRequiredPermissions()) {
            return
        }

        if (permissionHandler.shouldShowPermissionRationale(this)) {
            // Show rationale dialog
            showPermissionRationaleDialog()
        } else {
            // Request permissions directly
            requestPermissions.launch(permissionHandler.getRequiredPermissions())
        }
    }

    private fun showPermissionRationaleDialog() {
        // Using a simple approach - in a real app, you might want to use Compose AlertDialog
        // For now, we'll just request permissions directly
        requestPermissions.launch(permissionHandler.getRequiredPermissions())
    }

    override fun onResume() {
        super.onResume()
        // Check if Bluetooth is still enabled
        if (bluetoothAdapter?.isEnabled == false) {
            Toast.makeText(this, "Bluetooth has been disabled", Toast.LENGTH_LONG).show()
        }
    }
}