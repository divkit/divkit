import Foundation

import CommonCorePublic
import LayoutKit

protocol DivImageProtocol: DivBase, DivImageContentMode {
  var aspect: DivAspect? { get }

  func resolvePreview(_ expressionResolver: ExpressionResolver) -> String?
  func resolvePlaceholderColor(_ expressionResolver: ExpressionResolver) -> Color
}

extension DivImageProtocol {
  func resolveHeight(_ context: DivBlockModelingContext) -> ImageBlockHeight {
    if let aspect = aspect {
      return .ratio(aspect.resolveRatio(context.expressionResolver) ?? 0)
    }
    return .trait(makeContentHeightTrait(with: context))
  }

  func resolvePlaceholder(
    _ expressionResolver: ExpressionResolver,
    highPriority: Bool = false
  ) -> ImagePlaceholder {
    if
      let base64 = resolvePreview(expressionResolver) {
      return .imageData(ImageData(base64: base64, highPriority: highPriority))
    } else {
      return .color(resolvePlaceholderColor(expressionResolver))
    }
  }

  func checkLayoutTraits(context: DivBlockModelingContext) throws {
    if case .intrinsic = makeContentWidthTrait(with: context) {
      throw DivBlockModelingError(
        "\(typeName) has wrap_content width",
        path: context.parentPath
      )
    }

    if case let .trait(heightTrait) = resolveHeight(context),
       case .intrinsic = heightTrait {
      throw DivBlockModelingError(
        "\(typeName) without aspect has wrap_content height",
        path: context.parentPath
      )
    }
  }

  private var typeName: String {
    guard let typeName = String(describing: self).split(separator: ".").last else {
      return "DivImage"
    }
    return String(typeName)
  }
}
