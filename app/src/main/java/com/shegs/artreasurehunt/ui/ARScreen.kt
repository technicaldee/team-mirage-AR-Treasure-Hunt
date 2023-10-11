package com.shegs.artreasurehunt.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale


@Composable
fun ARCameraScreen() {
    val arModelNodes = remember { mutableStateOf<ArModelNode?>(null) }
    val nodes = remember { mutableListOf<ArNode>() }


    ARScene(
        modifier = Modifier.fillMaxSize(),
        nodes = nodes,
        planeRenderer = true,
        onCreate = { arSceneView ->
            // Apply AR configuration here
            arModelNodes.value =
                ArModelNode(arSceneView.engine, PlacementMode.BEST_AVAILABLE).apply {
                    followHitPosition = true
                    instantAnchor = false
                    onHitResult = {arModelNodes,hitResult->

                    }
                    scale = Scale(0.1f)
                    position = Position(x = 0.0f, y = 0.0f, z = -2.0f)
                }
            nodes.add(arModelNodes.value!!)
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
