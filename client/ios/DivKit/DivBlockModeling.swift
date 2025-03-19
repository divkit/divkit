import LayoutKit

public protocol DivBlockModeling {
  var id: String? { get }
  static var type: String { get }
  func makeBlock(context: DivBlockModelingContext) throws -> Block
  func pathSuffix(parentContext: DivBlockModelingContext) -> String?
}

extension DivBlockModeling {
  public func pathSuffix(parentContext: DivBlockModelingContext) -> String? {
    parentContext.overridenId ?? id
  }
}

extension DivBlockModeling {
  func modifiedContextParentPath(_ parentContext: DivBlockModelingContext)
    -> DivBlockModelingContext {
    let pathSuffix = pathSuffix(parentContext: parentContext)
    return parentContext.modifying(
      currentDivId: pathSuffix,
      pathSuffix: pathSuffix ?? Self.type
    )
  }
}
