import LayoutKit

public protocol DivBlockModeling {
  var id: String? { get }
  static var type: String { get }
  func makeBlock(context: DivBlockModelingContext) throws -> Block
}

extension DivBlockModeling {
  func modifiedContextParentPath(_ parentContext: DivBlockModelingContext)
    -> DivBlockModelingContext {
    let currentDivId = parentContext.overridenId ?? id
    return parentContext.modifying(
      currentDivId: currentDivId,
      pathSuffix: currentDivId ?? Self.type
    )
  }
}
