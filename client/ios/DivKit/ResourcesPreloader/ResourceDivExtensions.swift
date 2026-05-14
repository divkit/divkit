import Foundation

extension Div {
  /// Extracts image URLs that have static (non-expression) values.
  /// Does not require `ExpressionResolver` — safe to call before card context is set up.
  func makeStaticImageURLs() -> [URL] {
    var urls: [URL] = []

    if let backgrounds = value.background {
      for background in backgrounds {
        if case .divImageBackground(let bg) = background,
           case .value(let url) = bg.imageUrl {
          urls.append(url)
        }
      }
    }

    switch self {
    case .divImage(let d):
      if case .value(let url) = d.imageUrl { urls.append(url) }
    case .divGifImage(let d):
      if case .value(let url) = d.gifUrl { urls.append(url) }
    case .divText(let d):
      d.images?.forEach {
        if case .value(let url) = $0.url { urls.append(url) }
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

  func makeImageURLs(
    with expressionResolver: ExpressionResolver,
    filter: ResourcePreloadFilter = .all
  ) -> [URL] {
    var urls: [URL] = []

    if let backgrounds = value.background {
      for background in backgrounds {
        urls.append(contentsOf: background.preloadURLs(expressionResolver, filter: filter))
      }
    }

    switch self {
    case let .divImage(divImage):
      urls.append(contentsOf: divImage.preloadURLs(expressionResolver, filter: filter))
    case let .divGifImage(divGifImage):
      urls.append(contentsOf: divGifImage.preloadURLs(expressionResolver, filter: filter))
    case let .divText(divText):
      divText.images?.forEach {
        urls.append(contentsOf: $0.preloadURLs(expressionResolver, filter: filter))
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

  func makeVideoURLs(
    with expressionResolver: ExpressionResolver,
    filter: ResourcePreloadFilter = .all
  ) -> [URL] {
    switch self {
    case let .divVideo(divVideo):
      divVideo.preloadURLs(expressionResolver, filter: filter)
    case .divImage,
         .divGifImage,
         .divText,
         .divContainer,
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
         .divTabs:
      []
    }
  }

  func makeExtensionPreloadURLs(
    extensionHandlers: [String: DivExtensionHandler],
    expressionResolver: ExpressionResolver
  ) -> [URL] {
    guard !extensionHandlers.isEmpty else { return [] }
    return value.extensions?.compactMap {
      extensionHandlers[$0.id]?.getPreloadURLs(div: value, expressionResolver: expressionResolver)
    }.flatMap { $0 } ?? []
  }
}

private protocol ResourceDiv {
  func resolvePreloadURLs(_ resolver: ExpressionResolver) -> [URL?]
  func resolvePreloadRequired(_ resolver: ExpressionResolver) -> Bool
}

extension ResourceDiv {
  fileprivate func preloadURLs(
    _ resolver: ExpressionResolver,
    filter: ResourcePreloadFilter
  ) -> [URL] {
    guard filter == .all || resolvePreloadRequired(resolver) else {
      return []
    }
    return resolvePreloadURLs(resolver).compactMap { $0 }
  }
}

extension DivBackground: ResourceDiv {
  func resolvePreloadURLs(_ resolver: ExpressionResolver) -> [URL?] {
    [
      resolveImageURL(resolver),
    ]
  }
}

extension DivImage: ResourceDiv {
  func resolvePreloadURLs(_ resolver: ExpressionResolver) -> [URL?] {
    [
      resolveImageUrl(resolver),
    ]
  }
}

extension DivGifImage: ResourceDiv {
  func resolvePreloadURLs(_ resolver: ExpressionResolver) -> [URL?] {
    [
      resolveGifUrl(resolver),
    ]
  }
}

extension DivText.Image: ResourceDiv {
  func resolvePreloadURLs(_ resolver: ExpressionResolver) -> [URL?] {
    [
      resolveUrl(resolver),
    ]
  }
}

extension DivVideo: ResourceDiv {
  func resolvePreloadURLs(_ resolver: ExpressionResolver) -> [URL?] {
    videoSources?.map { $0.resolveUrl(resolver) } ?? []
  }
}

extension DivBackground {
  func resolveImageURL(_ expressionResolver: ExpressionResolver) -> URL? {
    switch self {
    case let .divImageBackground(imageBackground):
      imageBackground.resolveImageUrl(expressionResolver)
    case let .divNinePatchBackground(ninePatchImage):
      ninePatchImage.resolveImageUrl(expressionResolver)
    case .divLinearGradient, .divRadialGradient, .divSolidBackground:
      nil
    }
  }

  func resolvePreloadRequired(_ expressionResolver: ExpressionResolver) -> Bool {
    switch self {
    case let .divImageBackground(imageBackground):
      imageBackground.resolvePreloadRequired(expressionResolver)
    case .divNinePatchBackground, .divLinearGradient, .divRadialGradient, .divSolidBackground:
      false
    }
  }
}

extension DivData {
  /// Traverses the full AST and returns all statically-resolvable image URLs.
  func staticImageURLs() -> [URL] {
    flatMap { $0.makeStaticImageURLs() }.flatMap { $0 }
  }
}
