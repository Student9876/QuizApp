package com.example.quizapp.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

@Composable
fun LandingScreen(
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var networkStatus by remember { mutableStateOf("Checking network...") }
    var isSystemConnected by remember { mutableStateOf(false) }
    var canReachInternet by remember { mutableStateOf<Boolean?>(null) }
    var imageLoadStatus by remember { mutableStateOf("Not tested yet") }

    // Check system network status
    LaunchedEffect(Unit) {
        isSystemConnected = checkNetworkConnectivity(context)
        networkStatus = if (isSystemConnected) {
            "System reports: Connected"
        } else {
            "System reports: No connection"
        }

        // Test actual internet connectivity
        if (isSystemConnected) {
            scope.launch {
                networkStatus = "Testing internet access..."
                canReachInternet = testInternetConnection()
                networkStatus = if (canReachInternet == true) {
                    "✅ Internet is working!"
                } else {
                    "❌ Cannot reach internet"
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Network Status Card
//        Card(
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(
//                containerColor = when {
//                    canReachInternet == true -> Color(0xFF4CAF50).copy(alpha = 0.1f)
//                    canReachInternet == false -> Color(0xFFF44336).copy(alpha = 0.1f)
//                    else -> MaterialTheme.colorScheme.surfaceVariant
//                }
//            )
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Network Diagnostics",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                // System Network Status
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("WiFi/Mobile Data:")
//                    Text(
//                        if (isSystemConnected) "ON ✓" else "OFF ✗",
//                        color = if (isSystemConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Internet Connectivity
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("Internet Access:")
//                    Text(
//                        when (canReachInternet) {
//                            true -> "Working ✓"
//                            false -> "Failed ✗"
//                            null -> "Testing..."
//                        },
//                        color = when (canReachInternet) {
//                            true -> Color(0xFF4CAF50)
//                            false -> Color(0xFFF44336)
//                            null -> Color.Gray
//                        }
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                // Image Load Status
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("Image Loading:")
//                    Text(imageLoadStatus)
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Test Image
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp)
//                        .background(
//                            Color.LightGray.copy(alpha = 0.3f),
//                            RoundedCornerShape(8.dp)
//                        ),
//                    contentAlignment = Alignment.Center
//                ) {
//                    AsyncImage(
//                        model = ImageRequest.Builder(context)
//                            .data("https://placehold.co/400x200/3498db/ffffff?text=Internet+OK")
//                            .listener(
//                                onStart = {
//                                    imageLoadStatus = "Loading..."
//                                },
//                                onSuccess = { _, _ ->
//                                    imageLoadStatus = "Success ✓"
//                                },
//                                onError = { _, result ->
//                                    imageLoadStatus = "Failed: ${result.throwable.message}"
//                                }
//                            )
//                            .build(),
//                        contentDescription = "Test Image",
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                // Retry Button
//                Button(
//                    onClick = {
//                        scope.launch {
//                            networkStatus = "Retesting..."
//                            isSystemConnected = checkNetworkConnectivity(context)
//                            canReachInternet = testInternetConnection()
//                            networkStatus = if (canReachInternet == true) {
//                                "✅ Internet is working!"
//                            } else {
//                                "❌ Cannot reach internet"
//                            }
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Retry Connection Test")
//                }
//            }
//        }

        Spacer(modifier = Modifier.height(32.dp))

        // App Title
        Text(
            text = "Quiz Pro",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Create, share, and take quizzes with ease.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Login Button
        Button(
            onClick = onLoginClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            enabled = canReachInternet == true
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sign Up Button
        OutlinedButton(
            onClick = onSignUpClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            enabled = canReachInternet == true
        ) {
            Text(text = "Sign Up")
        }

        // Troubleshooting Tips
        if (canReachInternet == false) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Troubleshooting Tips:\n" +
                            "• Check if WiFi/Mobile data is enabled\n" +
                            "• Try turning Airplane mode on/off\n" +
                            "• Restart the app\n" +
                            "• Check if VPN is blocking connection\n" +
                            "• Verify other apps can access internet",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// Check if device has network connectivity
fun checkNetworkConnectivity(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}

// Test actual internet connectivity
suspend fun testInternetConnection(): Boolean = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("https://www.google.com")
            .head() // Just check headers, don't download content
            .build()

        val response = client.newCall(request).execute()
        response.isSuccessful
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}