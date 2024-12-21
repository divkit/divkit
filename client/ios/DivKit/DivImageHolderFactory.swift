import Foundation
import VGSL

/// Loads images for `DivKit` views.
public protocol DivImageHolderFactory {
  /// Provides `ImageHolder` for an image with given `URL`.
  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder
}

extension DivImageHolderFactory {
  public func make(_ url: URL?) -> ImageHolder {
    make(url, nil)
  }

  public func withCache(_ cachedImageHolders: [ImageHolder]) -> DivImageHolderFactory {
    if cachedImageHolders.isEmpty {
      return self
    }
    return CachedImageHolderFactory(
      imageHolderFactory: self,
      cachedImageHolders: cachedImageHolders
    )
  }

  public func withAssets() -> DivImageHolderFactory {
    AssetsImageProvider(
      imageHolderFactory: self
    )
  }
}

extension ImageHolderFactory: DivImageHolderFactory {}

final class DefaultImageHolderFactory: DivImageHolderFactory {
  private let requester: URLResourceRequesting
  private let imageLoadingOptimizationEnabled: Bool

  private let imageProcessingQueue = OperationQueue(
    name: "tech.divkit.image-processing",
    qos: .userInitiated
  )

  init(
    requestPerformer: URLRequestPerforming,
    imageLoadingOptimizationEnabled: Bool = true
  ) {
    self.requester = NetworkURLResourceRequester(performer: requestPerformer)
    self.imageLoadingOptimizationEnabled = imageLoadingOptimizationEnabled
  }

  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    guard let url else {
      return placeholder?.toImageHolder() ?? NilImageHolder()
    }
    return RemoteImageHolder(
      url: url,
      placeholder: placeholder,
      requester: requester,
      imageProcessingQueue: imageProcessingQueue,
      imageLoadingOptimizationEnabled: imageLoadingOptimizationEnabled
    )
  }
}

private final class CachedImageHolderFactory: DivImageHolderFactory {
  private let imageHolderFactory: DivImageHolderFactory
  private let cachedImageHolders: [ImageHolder]

  init(
    imageHolderFactory: DivImageHolderFactory,
    cachedImageHolders: [ImageHolder]
  ) {
    self.imageHolderFactory = imageHolderFactory
    self.cachedImageHolders = cachedImageHolders
  }

  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    let cachedImageHolder = cachedImageHolders.first {
      $0.reused(with: placeholder, remoteImageURL: url) != nil
    }
    return cachedImageHolder ?? imageHolderFactory.make(url, placeholder)
  }
}

private struct AssetsImageProvider: DivImageHolderFactory {
  private let imageHolderFactory: DivImageHolderFactory

  init(
    imageHolderFactory: DivImageHolderFactory
  ) {
    self.imageHolderFactory = imageHolderFactory
  }

  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    var localImage: ImageHolder?
    if url?.scheme == "divkit-asset", let name = url?.host {
      localImage = Image(named: "divkit/\(name)")
    }
    return localImage ?? imageHolderFactory.make(url, placeholder)
  }
}
