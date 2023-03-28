// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public struct ImageHolderFactory {
  private let _make: (URL?, ImagePlaceholder?) -> ImageHolder

  public func make(_ url: URL?, _ placeholder: Image?) -> ImageHolder {
    _make(url, placeholder.map { .image($0) })
  }

  public func make(_ url: URL?, _ placeholder: ImagePlaceholder? = nil) -> ImageHolder {
    _make(url, placeholder)
  }

  public init(make: @escaping (URL?, ImagePlaceholder?) -> ImageHolder) {
    _make = make
  }

  public init(
    requester: URLResourceRequesting,
    localImageProvider: LocalImageProviding? = nil,
    imageProcessingQueue: OperationQueueType
  ) {
    _make = { url, placeholder in
      guard let url = url else {
        switch placeholder {
        case let .image(image)?:
          return image
        case let .color(color)?:
          return ColorHolder(color: color)
        case let .view(view)?:
          return ViewImageHolder(view: view)
        case .none:
          return NilImageHolder()
        }
      }
      if let localImage = localImageProvider?.localImage(for: url) {
        return localImage
      }
      return RemoteImageHolder(
        url: url,
        placeholder: placeholder,
        requester: requester,
        imageProcessingQueue: imageProcessingQueue
      )
    }
  }

  public init(
    localImageProvider: LocalImageProviding?,
    imageProcessingQueueLabel: String = "ru.yandex.commonCore.image-processing",
    requestPerformer: URLRequestPerforming
  ) {
    let networkRequester = NetworkURLResourceRequester(performer: requestPerformer)
    let imageProcessingQueue = OperationQueue(name: imageProcessingQueueLabel, qos: .utility)

    self.init(
      requester: networkRequester,
      localImageProvider: localImageProvider,
      imageProcessingQueue: imageProcessingQueue
    )
  }

  public func withInMemoryCache(cachedImageHolders: [ImageHolder]) -> ImageHolderFactory {
    guard !cachedImageHolders.isEmpty else { return self }
    return ImageHolderFactory(
      make: { url, image in
        cachedImageHolders.first { $0.reused(with: image, remoteImageURL: url) != nil }
          ?? self.make(url, image)
      }
    )
  }
}
