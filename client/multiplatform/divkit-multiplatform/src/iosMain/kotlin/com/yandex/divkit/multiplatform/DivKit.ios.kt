package com.yandex.divkit.multiplatform

import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies

actual fun makeDivKitFactory(
    dependencies: DivKitDependencies
): DivKitFactory {
    return DivKitFactoryImpl(dependencies)
}
