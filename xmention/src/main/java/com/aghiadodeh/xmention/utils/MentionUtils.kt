package com.aghiadodeh.xmention.utils

import android.content.Context
import kotlin.reflect.KProperty1

object MentionUtils {
    @Suppress("UNCHECKED_CAST")
    fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
        val property = instance::class.members
            // don't cast here to <Any, R>, it would succeed silently
            .first { it.name == propertyName } as KProperty1<Any, *>
        // force a invalid cast exception if incorrect type here
        return property.get(instance) as R
    }

    fun pixelsToSp(context: Context, px: Float): Float {
        val scaledDensity: Float = context.resources.displayMetrics.scaledDensity
        return px / scaledDensity
    }
}