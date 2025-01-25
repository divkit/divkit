package com.yandex.div.core.resources

import android.content.res.*
import android.content.res.loader.ResourcesLoader
import android.graphics.Movie
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.RequiresApi
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

/**
 * Resources wrapper that forwards all calls to internal wrapped object.
 */
@Suppress("DEPRECATION")
internal open class ResourcesWrapper(
    private val resources: Resources
) : Resources(resources.assets, resources.displayMetrics, resources.configuration) {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun addLoaders(vararg loaders: ResourcesLoader?) {
        resources.addLoaders(*loaders)
    }

    @Throws(NotFoundException::class)
    override fun getAnimation(id: Int): XmlResourceParser {
        return resources.getAnimation(id)
    }

    @Throws(NotFoundException::class)
    override fun getBoolean(id: Int): Boolean {
        return resources.getBoolean(id)
    }

    @Deprecated("Deprecated in Java")
    @Throws(NotFoundException::class)
    override fun getColor(id: Int): Int {
        return resources.getColor(id)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(NotFoundException::class)
    override fun getColor(id: Int, theme: Theme?): Int {
        return resources.getColor(id, theme)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(NotFoundException::class)
    override fun getColorStateList(id: Int, theme: Theme?): ColorStateList {
        return resources.getColorStateList(id, theme)
    }

    @Deprecated("Deprecated in Java")
    @Throws(NotFoundException::class)
    override fun getColorStateList(id: Int): ColorStateList {
        return resources.getColorStateList(id)
    }

    override fun getConfiguration(): Configuration? {
        return resources.configuration
    }

    @Throws(NotFoundException::class)
    override fun getDimension(id: Int): Float {
        return resources.getDimension(id)
    }

    @Throws(NotFoundException::class)
    override fun getDimensionPixelOffset(id: Int): Int {
        return resources.getDimensionPixelOffset(id)
    }

    @Throws(NotFoundException::class)
    override fun getDimensionPixelSize(id: Int): Int {
        return resources.getDimensionPixelSize(id)
    }

    override fun getDisplayMetrics(): DisplayMetrics? {
        return resources.displayMetrics
    }

    @Throws(NotFoundException::class)
    override fun getDrawable(id: Int, theme: Theme?): Drawable? {
        return resources.getDrawable(id, theme)
    }

    @Deprecated("Deprecated in Java")
    @Throws(NotFoundException::class)
    override fun getDrawable(id: Int): Drawable? {
        return resources.getDrawable(id)
    }

    @Deprecated("Deprecated in Java")
    @Throws(NotFoundException::class)
    override fun getDrawableForDensity(id: Int, density: Int): Drawable? {
        return resources.getDrawableForDensity(id, density)
    }

    override fun getDrawableForDensity(id: Int, density: Int, theme: Theme?): Drawable? {
        return resources.getDrawableForDensity(id, density, theme)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Throws(NotFoundException::class)
    override fun getFloat(id: Int): Float {
        return resources.getFloat(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(NotFoundException::class)
    override fun getFont(id: Int): Typeface {
        return resources.getFont(id)
    }

    @Throws(NotFoundException::class)
    override fun getFraction(id: Int, base: Int, pbase: Int): Float {
        return resources.getFraction(id, base, pbase)
    }

    override fun getIdentifier(name: String?, defType: String?, defPackage: String?): Int {
        return resources.getIdentifier(name, defType, defPackage)
    }

    @Throws(NotFoundException::class)
    override fun getIntArray(id: Int): IntArray {
        return resources.getIntArray(id)
    }

    @Throws(NotFoundException::class)
    override fun getInteger(id: Int): Int {
        return resources.getInteger(id)
    }

    @Throws(NotFoundException::class)
    override fun getLayout(id: Int): XmlResourceParser {
        return resources.getLayout(id)
    }

    @Deprecated("Deprecated in Java")
    @Throws(NotFoundException::class)
    override fun getMovie(id: Int): Movie? {
        return resources.getMovie(id)
    }

    @Throws(NotFoundException::class)
    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any?): String {
        return resources.getQuantityString(id, quantity, *formatArgs)
    }

    @Throws(NotFoundException::class)
    override fun getQuantityString(id: Int, quantity: Int): String {
        return resources.getQuantityString(id, quantity)
    }

    @Throws(NotFoundException::class)
    override fun getQuantityText(id: Int, quantity: Int): CharSequence {
        return resources.getQuantityText(id, quantity)
    }

    @Throws(NotFoundException::class)
    override fun getResourceEntryName(resid: Int): String? {
        return resources.getResourceEntryName(resid)
    }

    @Throws(NotFoundException::class)
    override fun getResourceName(resid: Int): String? {
        return resources.getResourceName(resid)
    }

    @Throws(NotFoundException::class)
    override fun getResourcePackageName(resid: Int): String? {
        return resources.getResourcePackageName(resid)
    }

    @Throws(NotFoundException::class)
    override fun getResourceTypeName(resid: Int): String? {
        return resources.getResourceTypeName(resid)
    }

    @Throws(NotFoundException::class)
    override fun getString(id: Int, vararg formatArgs: Any?): String {
        return resources.getString(id, *formatArgs)
    }

    @Throws(NotFoundException::class)
    override fun getString(id: Int): String {
        return resources.getString(id)
    }

    @Throws(NotFoundException::class)
    override fun getStringArray(id: Int): Array<out String> {
        return resources.getStringArray(id)
    }

    override fun getText(id: Int, def: CharSequence?): CharSequence? {
        return resources.getText(id, def)
    }

    @Throws(NotFoundException::class)
    override fun getText(id: Int): CharSequence {
        return resources.getText(id)
    }

    @Throws(NotFoundException::class)
    override fun getTextArray(id: Int): Array<out CharSequence> {
        return resources.getTextArray(id)
    }

    @Throws(NotFoundException::class)
    override fun getValue(name: String?, outValue: TypedValue?, resolveRefs: Boolean) {
        resources.getValue(name, outValue, resolveRefs)
    }

    @Throws(NotFoundException::class)
    override fun getValue(id: Int, outValue: TypedValue?, resolveRefs: Boolean) {
        resources.getValue(id, outValue, resolveRefs)
    }

    @Throws(NotFoundException::class)
    override fun getValueForDensity(id: Int, density: Int, outValue: TypedValue?,
                                    resolveRefs: Boolean) {
        resources.getValueForDensity(id, density, outValue, resolveRefs)
    }

    @Throws(NotFoundException::class)
    override fun getXml(id: Int): XmlResourceParser {
        return resources.getXml(id)
    }

    override fun obtainAttributes(set: AttributeSet?, attrs: IntArray?): TypedArray? {
        return resources.obtainAttributes(set, attrs)
    }

    @Throws(NotFoundException::class)
    override fun obtainTypedArray(id: Int): TypedArray {
        return resources.obtainTypedArray(id)
    }

    @Throws(NotFoundException::class)
    override fun openRawResource(id: Int, value: TypedValue?): InputStream {
        return resources.openRawResource(id, value)
    }

    @Throws(NotFoundException::class)
    override fun openRawResource(id: Int): InputStream {
        return resources.openRawResource(id)
    }

    @Throws(NotFoundException::class)
    override fun openRawResourceFd(id: Int): AssetFileDescriptor? {
        return resources.openRawResourceFd(id)
    }

    @Throws(XmlPullParserException::class)
    override fun parseBundleExtra(tagName: String?, attrs: AttributeSet?, outBundle: Bundle?) {
        resources.parseBundleExtra(tagName, attrs, outBundle)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    override fun parseBundleExtras(parser: XmlResourceParser?, outBundle: Bundle?) {
        resources.parseBundleExtras(parser, outBundle)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun removeLoaders(vararg loaders: ResourcesLoader?) {
        resources.removeLoaders(*loaders)
    }

    @Deprecated("Deprecated in Java")
    override fun updateConfiguration(config: Configuration?, metrics: DisplayMetrics?) {
        super.updateConfiguration(config, metrics)
        // May be called from super's constructor (before Android 7), so have to check for null.
        if (resources != null) {
            resources.updateConfiguration(config, metrics)
        }
    }
}
