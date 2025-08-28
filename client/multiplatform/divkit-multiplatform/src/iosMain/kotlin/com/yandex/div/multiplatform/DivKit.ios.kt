package com.yandex.div.multiplatform

import com.yandex.div.multiplatform.dependencies.DivKitDependencies

actual fun makeComposeDivKitFactory(
    dependencies: DivKitDependencies
): DivKitFactory {
    return DivKitFactoryImpl(dependencies)
}
