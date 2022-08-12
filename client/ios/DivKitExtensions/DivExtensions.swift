// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

import DivKit

extension Div {
  public func makeImageURLs(with expressionResolver: ExpressionResolver) -> [URL] {
    var urls: [URL] = value.background?.compactMap { $0.makeImageURL(with: expressionResolver) }
      ?? []
    switch self {
    case let .divImage(divImage):
      if let url = divImage.resolveImageUrl(expressionResolver) {
        urls.append(url)
      }
      divImage.getDarkThemeImageURL(with: expressionResolver).map { urls.append($0) }
    case let .divGifImage(divGifImage):
      if let url = divGifImage.resolveGifUrl(expressionResolver) {
        urls.append(url)
      }
    case let .divText(divText):
      if let images = divText.images {
        urls.append(contentsOf: images.compactMap { $0.resolveUrl(expressionResolver) })
      }
    case .divContainer,
         .divGrid,
         .divGallery,
         .divPager,
         .divTabs,
         .divCustom,
         .divState,
         .divSlider,
         .divIndicator,
         .divSeparator,
         .divInput:
      break
    }
    return urls
  }
}
