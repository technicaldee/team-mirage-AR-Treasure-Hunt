package com.shegs.artreasurehunt.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.shegs.artreasurehunt.extension.hasLocationPermission
import com.shegs.artreasurehunt.ui.theme.ARTreasureHuntTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random


@AndroidEntryPoint
class MapActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationViewModel: MapActivityVM by viewModels()


        setContent {

            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )


            val viewState by locationViewModel.viewState.collectAsStateWithLifecycle()

            ARTreasureHuntTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val therandomMarkers = remember { mutableStateOf(mutableListOf<LatLng>()) }
                    LaunchedEffect(!hasLocationPermission()) {
                        permissionState.launchMultiplePermissionRequest()
                    }

                    when {
                        permissionState.allPermissionsGranted -> {
                            LaunchedEffect(Unit) {
                                locationViewModel.handle(PermissionEvent.Granted)
                            }
                        }

                        permissionState.shouldShowRationale -> {
                            RationaleAlert(onDismiss = { }) {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        }

                        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
                            LaunchedEffect(Unit) {
                                locationViewModel.handle(PermissionEvent.Revoked)
                            }
                        }
                    }

                    with(viewState) {
                        when (this) {
                            ViewState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            ViewState.RevokedPermissions -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("We need permissions to use this app")
                                    Button(
                                        onClick = {
                                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                        },
                                        enabled = !hasLocationPermission()
                                    ) {
                                        if (hasLocationPermission()) CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = Color.White
                                        )
                                        else Text("Settings")
                                    }
                                }
                            }

                            is ViewState.Success -> {
                                val currentLoc =
                                    LatLng(
                                        this.location?.latitude ?: 0.0,
                                        this.location?.longitude ?: 0.0
                                    )
                                val cameraState = rememberCameraPositionState()
                                val therandomMarkers = remember { mutableStateOf(generateRandomMarkers(currentLoc, 0)) }


                                LaunchedEffect(key1 = currentLoc) {
                                    cameraState.centerOnLocation(currentLoc)
                                }

                                MainScreen(
                                    currentPosition = LatLng(
                                        currentLoc.latitude,
                                        currentLoc.longitude
                                    ),
                                    cameraState = cameraState,
                                    randomMarkers   = therandomMarkers
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(currentPosition: LatLng, cameraState: CameraPositionState, randomMarkers: MutableState<List<LatLng>>) {
    val marker = LatLng(currentPosition.latitude, currentPosition.longitude)

    val newRandomMarkers = remember { mutableStateOf(generateRandomMarkers(currentPosition, 5)) }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        properties = MapProperties(
            isMyLocationEnabled = true,
            mapType = MapType.HYBRID,
            isTrafficEnabled = true
        )
    ) {
        Marker(
            state = MarkerState(position = marker),
            title = "MyPosition",
            snippet = "This is a description of this Marker",
            draggable = true
        )

        randomMarkers.value.forEachIndexed { index, randomMarker ->
            Marker(
                state = MarkerState(position = randomMarker),
                title = "Random Location $index",
                snippet = "This is a description of this Marker",
                draggable = true
            )
        }
    }



    Box(contentAlignment = Alignment.BottomCenter) {
        Button(
            onClick = {
                val newRandomMarkers = generateRandomMarkers(currentPosition, 5)
                randomMarkers.value = newRandomMarkers
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .height(40.dp)
                .zIndex(1f)

        ) {
            Text("Generate Game Locations")
        }
    }



}

fun generateRandomMarkers(center: LatLng, count: Int): List<LatLng> {
    val random = Random(System.currentTimeMillis())
    val radius = 0.01 // Adjust the radius as needed for your use case
    val randomMarkers = mutableListOf<LatLng>()

    for (i in 0 until count) {
        val lat = center.latitude + (random.nextDouble() - 0.5) * radius
        val lng = center.longitude + (random.nextDouble() - 0.5) * radius
        randomMarkers.add(LatLng(lat, lng))
    }

    Log.d("val","$randomMarkers")

    return randomMarkers
}

@Composable
fun RationaleAlert(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "We need location permissions to use this app",
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}

private suspend fun CameraPositionState.centerOnLocation(
    location: LatLng
) = animate(
    update = CameraUpdateFactory.newLatLngZoom(
        location,
        15f
    ),
    durationMs = 1500
)
