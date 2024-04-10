import Foundation

extension [String: Function] {
  mutating func addGetMethods() {
    addMethod("getArray", [ArrayFunctions.getArray, DictFunctions.getArray])
    addMethod("getBoolean", [ArrayFunctions.getBoolean, DictFunctions.getBoolean])
    addMethod("getColor", [ArrayFunctions.getColor, DictFunctions.getColor])
    addMethod("getDict", [ArrayFunctions.getDict, DictFunctions.getDict])
    addMethod("getInteger", [ArrayFunctions.getInteger, DictFunctions.getInteger])
    addMethod("getNumber", [ArrayFunctions.getNumber, DictFunctions.getNumber])
    addMethod("getString", [ArrayFunctions.getString, DictFunctions.getString])
    addMethod("getUrl", [ArrayFunctions.getUrl, DictFunctions.getUrl])
  }

  private mutating func addMethod(
    _ name: String,
    _ functionFactories: [() -> SimpleFunction]
  ) {
    self[name] = OverloadedFunction(
      functions: functionFactories.map { $0() }
    )
  }
}
