package info.mx.tracks.util

import android.content.res.Resources
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <Response> Task<Response>.suspended(): Response = suspendCoroutine { continuation ->
    addOnSuccessListener { continuation.resume(it) }
    addOnFailureListener { continuation.resumeWithException(it) }
}

/**
 * Safe extension function to get drawable resource identifier by name.
 * Wraps the deprecated getIdentifier() method with proper suppression.
 * Returns 0 if resource is not found.
 *
 * @param name The resource name
 * @param packageName The package name
 * @return The resource identifier, or 0 if not found
 */
@Suppress("DiscouragedApi")
fun Resources.getDrawableIdentifier(name: String, packageName: String): Int {
    return try {
        getIdentifier(name, "drawable", packageName)
    } catch (e: Exception) {
        0
    }
}

