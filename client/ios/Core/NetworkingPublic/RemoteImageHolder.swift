// Copyright 2016 Yandex LLC. All rights reserved.

import Foundation

import BasePublic

public final class RemoteImageHolder: ImageHolder {
  private typealias AsyncImageRequester = (@escaping ((Image, URLRequestResult.Source)?) -> Void)
    -> Cancellable?

  public let placeholder: ImagePlaceholder?
  public let url: URL
  public private(set) weak var image: Image?
  private let resourceRequester: AsyncImageRequester

  private init(
    url: URL,
    placeholder: ImagePlaceholder? = nil,
    resourceRequester: @escaping AsyncImageRequester
  ) {
    self.url = url
    self.placeholder = placeholder
    self.resourceRequester = resourceRequester
  }

  public convenience init(
    url: URL,
    placeholder: ImagePlaceholder? = nil,
    requester: URLResourceRequesting,
    imageProcessingQueue: OperationQueueType
  ) {
    weak var weakSelf: RemoteImageHolder?
    let shouldCallCompletionWithNil = placeholder == nil
    let resourceRequester = AsyncResourceRequester<(Image, URLRequestResult.Source)> { completion in
      requester.getDataWithSource(from: url, completion: { result in
        Thread.assertIsMain()
        guard let self = weakSelf else { return }
        if let image = self.image {
          completion((image, .cache))
          return
        }
        guard case let .success(value) = result else {
          if shouldCallCompletionWithNil {
            completion(nil)
          }
          return
        }
        imageProcessingQueue.addOperation {
          let image: Image?
          #if os(iOS)
          switch value.data.imageFormat {
          case .gif:
            image = Image.animatedImage(with: value.data as CFData)
          case .jpeg, .png, .tiff, .unknown:
            image = Image(data: value.data, scale: PlatformDescription.screenScale())
          }
          #else
          image = Image(data: value.data, scale: PlatformDescription.screenScale())
          #endif
          onMainThread {
            if let image = self.image ?? image {
              self.image = image
              completion((image, value.source))
            } else if shouldCallCompletionWithNil {
              completion(nil)
            }
          }
        }
      })
    }
    self.init(
      url: url,
      placeholder: placeholder,
      resourceRequester: resourceRequester.requestResource
    )
    weakSelf = self
  }

  @discardableResult
  public func requestImageWithCompletion(_ completion: @escaping ((Image?) -> Void))
    -> Cancellable? {
    requestImageWithSource {
      completion($0?.0)
    }
  }

  @discardableResult
  public func requestImageWithSource(_ completion: @escaping CompletionHandlerWithSource)
    -> Cancellable? {
    Thread.assertIsMain()

    if let image = self.image {
      completion((image, .cache))
      return nil
    }

    if case let .some(.image(placeholder)) = placeholder {
      completion((placeholder, .cache))
    }

    return resourceRequester(completion)
  }

  public func equals(_ other: ImageHolder) -> Bool {
    guard let other = other as? RemoteImageHolder else {
      return false
    }

    return url == other.url && placeholder == other.placeholder
  }
}

extension RemoteImageHolder: CustomDebugStringConvertible {
  public var debugDescription: String {
    "URL = \(dbgStr(url)), placeholder = \(dbgStr(placeholder?.debugDescription))"
  }
}

extension RemoteImageHolder {
  public func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    (self.placeholder === placeholder && url == remoteImageURL) ? self : nil
  }
}
