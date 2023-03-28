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

  func resolvePlaceholder(_ expressionResolver: ExpressionResolver) -> ImagePlaceholder {
    if
      let base64 = resolvePreview(expressionResolver),
      let image = makeImage(base64) {
      return .image(image)
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

private let makeImage: (String) -> Image? = memoize(
  sizeLimit: 10 * 1024 * 1024,
  keyMapper: { $0 },
  sizeByKey: { $0.count * 3 / 4 },
  _makeImage
)

private func _makeImage(base64: String) -> Image? {
  decode(base64: base64).flatMap(Image.init(data:))
}

fileprivate func decode(base64: String) -> Data? {
  if let data = Data(base64Encoded: base64) {
    return data
  }
  if let url = URL(string: base64),
     let dataHoldingURL = try? Data(contentsOf: url) {
    return dataHoldingURL
  }
  return nil
}
