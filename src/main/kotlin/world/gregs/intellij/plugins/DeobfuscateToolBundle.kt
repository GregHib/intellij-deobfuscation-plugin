package world.gregs.intellij.plugins

import com.intellij.AbstractBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.util.*

object DeobfuscateToolBundle {

    private var ourBundle: Reference<ResourceBundle>? = null
    @NonNls
    const val BUNDLE = "world.gregs.intellij.plugins.DeobfuscateToolBundle"

    private val bundle: ResourceBundle
        get() {
            var bundle = com.intellij.reference.SoftReference.dereference(ourBundle)
            if (bundle == null) {
                bundle = ResourceBundle.getBundle(BUNDLE)
                ourBundle = SoftReference(bundle)
            }
            return bundle!!
        }

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String {
        return AbstractBundle.message(bundle, key, *params)
    }
}