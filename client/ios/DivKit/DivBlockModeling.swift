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
    let context = parentContext.modifying(
      currentDivId: currentDivId,
      currentDivType: Self.type,
      pathSuffix: currentDivId ?? Self.type
    )
    if let currentDivId, currentDivId.hasPrefix(DivViewId.tooltipMarker) {
      context.addWarning(
        message: "Element id '\(currentDivId)' starts with reserved prefix '\(DivViewId.tooltipMarker)'"
      )
    }
    return context
  }
}
