package divkit.dsl.expression.generator

import divkit.dsl.expression.generator.model.SignatureSet
import java.io.File

interface Preprocessor<T> {
    fun preprocess(signatures: Map<File, SignatureSet>): T
}
