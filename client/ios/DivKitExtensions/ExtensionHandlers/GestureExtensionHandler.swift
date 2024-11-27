import DivKit
import LayoutKit

public final class GestureExtensionHandler: DivExtensionHandler {
  public let id = "gesture"

  public init() {}

  public func applyAfterBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let params = GestureExtensionParams(params: getExtensionParams(div)) else {
      return block
    }

    return GestureBlock(
      child: block,
      upActions: params.upActions?.uiActions(context: context),
      downActions: params.downActions?.uiActions(context: context),
      leftActions: params.leftActions?.uiActions(context: context),
      rightActions: params.rightActions?.uiActions(context: context)
    )
  }
}

private struct GestureExtensionParams {
  let upActions: [DivAction]?
  let downActions: [DivAction]?
  let leftActions: [DivAction]?
  let rightActions: [DivAction]?

  init?(params: [String: Any]) {
    self.upActions = params.makeDivActions(for: "swipe_up")
    self.downActions = params.makeDivActions(for: "swipe_down")
    self.leftActions = params.makeDivActions(for: "swipe_left")
    self.rightActions = params.makeDivActions(for: "swipe_right")

    if upActions == nil, downActions == nil, leftActions == nil, rightActions == nil {
      DivKitLogger.warning("No actions provided for gesture")
      return nil
    }
  }
}
