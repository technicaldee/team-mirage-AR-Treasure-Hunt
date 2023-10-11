package com.shegs.artreasurehunt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.maps.GoogleMap
import com.shegs.artreasurehunt.ui.theme.ARTreasureHuntTheme
import io.github.sceneview.Scene
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.node.Node

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARTreasureHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ARCameraScreen()
                }
            }
        }
    }
}

@Composable
fun ARCameraScreen() {
//    val arNodes = remember { mutableStateListOf<ArModelNode>() }
//
//    // Add gemstone node to AR
//    val gemstoneNode = ArModelNode(
//        position = Position(x = 0.0f, y = 0.0f, z = -2.0f),
//        scale = Scale(0.1f),
//        placementMode = PlacementMode.BEST_AVAILABLE,
//        hitPosition = Position(0.0f, 0.0f, -2.0f),
//        followHitPosition = true,
//        instantAnchor = false
//    )
//    arNodes.add(gemstoneNode)

    ARScene(
        modifier = Modifier.fillMaxSize(),
        planeRenderer = true,
        onCreate = { arSceneView ->
            // Apply AR configuration here
        },
        onSessionCreate = { session ->
            // Configure ARCore session
        },
        onFrame = { arFrame ->
            // Handle AR frame updates
        },
        onTap = { hitResult ->
            // Handle user interactions in AR
        }
    )
}
