import Foundation

import CommonCore
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

  func resolvePlaceholder(_ expressionResolver: ExpressionResolver) -> ImagePlaceholder {
    if
      let base64 = resolvePreview(expressionResolver),
      let data = decode(base64: base64),
      let image = Image(data: data) {
      return .image(image)
    } else {
      return .color(resolvePlaceholderColor(expressionResolver))
    }
  }
  
  fileprivate func decode(base64: String) -> Data? {
    if let data = Data(base64Encoded: base64) {
      return data
    }
    if let url = URL(string: base64),
       let data = try? Data(contentsOf: url) {
      return data
    }
    return nil
  }

  func checkLayoutTraits(context: DivBlockModelingContext) throws {
    let expressionResolver = context.expressionResolver
    guard width.makeLayoutTrait(with: expressionResolver) != .intrinsic else {
      throw DivBlockModelingError(
        "\(typeName) has wrap_content width",
        path: context.parentPath
      )
    }

    guard
      height.makeLayoutTrait(with: expressionResolver) != .intrinsic || aspect != nil
    else {
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
