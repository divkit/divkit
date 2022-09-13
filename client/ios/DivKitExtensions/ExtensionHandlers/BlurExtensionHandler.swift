import Foundation

import DivKit
import LayoutKit

public final class BlurExtensionHandler: DivExtensionHandler {
  public let id = extensionID

  public init() {}

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    block.addingDecorations(blurEffect: div.resolveBlurEffect(context.expressionResolver))
  }
}

extension DivBase {
  fileprivate func resolveBlurEffect(
    _ expressionResolver: ExpressionResolver
  ) -> BlurEffect? {
    guard
      let blurThemeExtension = extensions?.first(where: { $0.id == extensionID }),
      let styleExpression = blurThemeExtension.params?[styleKey] as? String
    else {
      return nil
    }

    if let blur: InternalBlurEffect = expressionResolver.resolveEnum(expression: styleExpression) {
      return BlurEffect(internalBlurEffect: blur)
    }

    return nil
  }
}

private enum InternalBlurEffect: String {
  case light
  case dark
  case disabled
}

extension BlurEffect {
  fileprivate init?(internalBlurEffect: InternalBlurEffect) {
    switch internalBlurEffect {
    case .light:
      self = .light
    case .dark:
      self = .dark
    case .disabled:
      return nil
    }
  }
}

private let styleKey = "style"
private let extensionID = "blur"
