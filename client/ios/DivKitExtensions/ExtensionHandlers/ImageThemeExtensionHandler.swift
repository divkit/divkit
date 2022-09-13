import Foundation

import CommonCore
import DivKit
import LayoutKit

public final class ImageThemeExtensionHandler: DivExtensionHandler {
  public let id = extensionID
  private let theme: Variable<Theme>

  public init(theme: Variable<Theme>) {
    self.theme = theme
  }

  public func applyBeforeBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    guard
      let block = block as? ImageBlock,
      let div = div as? DivImage
    else {
      return block
    }

    switch theme.value {
    case .dark:
      return makeDarkImageBlock(
        initialBlock: block,
        divImage: div,
        context: context
      )
    case .light:
      return block
    }
  }

  private func makeDarkImageBlock(
    initialBlock: ImageBlock,
    divImage: DivImage,
    context: DivBlockModelingContext
  ) -> ImageBlock {
    let imageURL = divImage.resolveDarkThemeImageURL(context.expressionResolver)
    let placeholder = divImage.resolveDarkThemePlaceholder(context.expressionResolver)
    if imageURL == nil, placeholder == nil {
      return initialBlock
    }

    let imageHolder = context.imageHolderFactory.make(imageURL, placeholder)
    return initialBlock.makeCopy(with: imageHolder)
  }
}

extension DivImage {
  func resolveDarkThemeImageURL(
    _ expressionResolver: ExpressionResolver
  ) -> URL? {
    guard
      let imageThemeExtension = extensions?.first(where: { $0.id == extensionID }),
      let urlExpression = imageThemeExtension.params?[darkUrlKey] as? String
    else {
      return nil
    }

    return expressionResolver.resolveUrl(expression: urlExpression)
  }

  fileprivate func resolveDarkThemePlaceholder(
    _ expressionResolver: ExpressionResolver
  ) -> ImagePlaceholder? {
    guard
      let imageThemeExtension = extensions?.first(where: { $0.id == extensionID }),
      let previewExpression = imageThemeExtension.params?[darkPreviewKey] as? String
    else {
      return nil
    }

    let preview = expressionResolver.resolveString(expression: previewExpression)
    if let data = Data(base64Encoded: preview),
       let image = Image(data: data) {
      return .image(image)
    }

    return nil
  }
}

private let extensionID = "theme_support"
private let darkUrlKey = "dark_url"
private let darkPreviewKey = "dark_preview"
