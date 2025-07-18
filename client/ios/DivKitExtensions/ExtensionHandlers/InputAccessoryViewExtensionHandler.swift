#if os(iOS)
import DivKit
import LayoutKit
import UIKit
import VGSL

public protocol InputAccessoryViewProvider {
  func getView(actions: [String: Action]) -> UIView
}

public final class InputAccessoryViewExtensionHandler: DivExtensionHandler {
  public let id = "input_accessory_view"
  private let viewProvider: InputAccessoryViewProvider

  public init(viewProvider: InputAccessoryViewProvider) {
    self.viewProvider = viewProvider
  }

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard let textInputBlock = block as? TextInputBlock else {
      DivKitLogger.error("Invalid block for InputAccessoryViewExtension")
      return block
    }
    var uiActions = [String: Action]()
    let params = getExtensionParams(div)

    if let actions = params["actions"] as? [String: [String: Any]] {
      uiActions = actions.compactMapValues {
        let action = DivTemplates
          .empty
          .parseValue(
            type: DivActionTemplate.self, from: $0
          ).value?.uiAction(context: context)
        if case let .divAction(params) = action?.payload {
          return { [weak self] in
            context.actionHandler?.handle(
              params: params,
              sender: self
            )
          }
        }
        return nil
      }
    }

    let view = viewProvider.getView(actions: uiActions)
    textInputBlock.accessoryView = view
    return textInputBlock
  }
}
#endif
