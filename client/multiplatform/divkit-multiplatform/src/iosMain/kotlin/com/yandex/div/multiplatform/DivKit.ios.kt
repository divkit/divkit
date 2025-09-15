package com.yandex.div.multiplatform

import com.yandex.div.multiplatform.dependencies.DivKitDependencies

actual fun makeDivKitFactory(
    dependencies: DivKitDependencies
): DivKitFactory {
    return DivKitFactoryImpl(dependencies)
}
