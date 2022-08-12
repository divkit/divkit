// Copyright 2022 Yandex LLC. All rights reserved.

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
    let darkThemeImageURL = divImage.getDarkThemeImageURL(with: context.expressionResolver)
    let placeholder = divImage.makeDarkThemePlaceholder(with: context.expressionResolver)

    if darkThemeImageURL == nil, placeholder == nil {
      return initialBlock
    }

    let imageHolder = context.imageHolderFactory.make(
      darkThemeImageURL,
      placeholder
    )

    return initialBlock.makeCopy(with: imageHolder)
  }
}

extension DivImage {
  func getDarkThemeImageURL(
    with expressionResolver: ExpressionResolver
  ) -> URL? {
    guard
      let imageThemeExtension = extensions?.first(where: { $0.id == extensionID }),
      let string = imageThemeExtension.params?[darkUrlKey] as? String
    else {
      return nil
    }

    guard
      let expressionLink = ExpressionLink<URL>(rawValue: string, validator: nil)
    else {
      return URL(string: string)
    }

    return expressionResolver.resolveStringBasedValue(
      expression: .link(expressionLink),
      initializer: URL.init(string:)
    )
  }

  func makeDarkThemePlaceholder(
    with expressionResolver: ExpressionResolver
  ) -> ImagePlaceholder? {
    guard
      let imageThemeExtension = extensions?.first(where: { $0.id == extensionID }),
      let string = imageThemeExtension.params?[darkPreviewKey] as? String
    else {
      return nil
    }

    let base64EncodedString = ExpressionLink<String>(rawValue: string, validator: nil)
      .flatMap {
        expressionResolver.resolveStringBasedValue(expression: .link($0), initializer: { $0 })
      } ?? string

    if let data = Data(base64Encoded: base64EncodedString),
       let image = Image(data: data) {
      return .image(image)
    }

    return nil
  }
}

private let extensionID = "theme_support"
private let darkUrlKey = "dark_url"
private let darkPreviewKey = "dark_preview"
