import DivKit
import Foundation

// TODO: Remove this func on next major release, clients should preload image urls with `DivDataResourcesPreloader`
extension Div {
  public func makeImageURLs(with expressionResolver: ExpressionResolver) -> [URL] {
    var urls: [URL] = value.background?.compactMap {
      $0.resolveImageURL(expressionResolver)
    } ?? []
    if let url = LottieExtensionHandler.getPreloadURL(
      div: value,
      expressionResolver: expressionResolver
    ) {
      urls.append(url)
    }
    switch self {
    case let .divImage(divImage):
      if let url = divImage.resolveImageUrl(expressionResolver) {
        urls.append(url)
      }
      divImage.resolveDarkThemeImageURL(expressionResolver).map { urls.append($0) }
    case let .divGifImage(divGifImage):
      if let url = divGifImage.resolveGifUrl(expressionResolver) {
        urls.append(url)
      }
    case let .divText(divText):
      if let images = divText.images {
        urls.append(contentsOf: images.compactMap { $0.resolveUrl(expressionResolver) })
      }
    case .divContainer,
         .divCustom,
         .divGallery,
         .divGrid,
         .divIndicator,
         .divInput,
         .divPager,
         .divSelect,
         .divSeparator,
         .divSlider,
         .divState,
         .divSwitch,
         .divVideo,
         .divTabs:
      break
    }
    return urls
  }
}
