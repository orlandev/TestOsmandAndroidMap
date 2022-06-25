package com.orlandev.testosmandandroidmap

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.orlandev.testosmandandroidmap.ui.theme.TestOsmandAndroidMapTheme
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme
import java.io.File


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestOsmandAndroidMapTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapScreen()
                }
            }
        }
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current
    Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    Configuration.getInstance().tileFileSystemCacheMaxBytes = 50L * 1024 * 1024
    Configuration.getInstance().osmdroidTileCache =
        File(context.cacheDir, "osmdroid").also { it.mkdir() }

    val map = remember { MapView(context) }

    val points = ArrayList<IGeoPoint>()
    for (i in 0..9999) {
        points.add(
            LabelledGeoPoint(
                18 + Math.random() * 5, -79 + Math.random() * 5, "Point #$i"
            )
        )
    }

    val textStyle = Paint();
    textStyle.style = Paint.Style.FILL
    textStyle.color = Color.parseColor("#FF00ff")
    textStyle.textAlign = Paint.Align.CENTER
    textStyle.textSize = 24F

    val pt = SimplePointTheme(points, true);

// set some visual options for the overlay
// we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
    val opt = SimpleFastPointOverlayOptions.getDefaultStyle()
        .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
        .setRadius(7f)
        .setIsClickable(true)
        .setCellSize(15)
        .setTextStyle(textStyle)
        .setRadius(20f)


// create the overlay with the theme
    val sfpo = SimpleFastPointOverlay(pt, opt)



    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Random Position") })
        }
    ) { paddingValues ->


        Column(Modifier.padding(paddingValues)) {
            Box {
                AndroidView(
                    factory = {
                        map
                    }, modifier = Modifier
                        .fillMaxHeight()
                        .testTag("This is a Tag")
                ) { map ->
                    map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
                    map.setMultiTouchControls(true)

                    map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

                    val mapController = map.controller
                    mapController.setZoom(15.0)

                    val startPoint = GeoPoint(20.0243223, -75.8961366)
                    mapController.setCenter(startPoint)
                    map.overlays.add(sfpo)
                }
            }
        }
    }
}