import CoreGraphics
import Foundation
import VGSL

/// Image holder that can be created off the main thread.
///
/// It keeps only the URL and the placeholder and creates the real host holder on the main thread
/// on first use. Everything DivKit needs during block modeling (`placeholder`, `displaySize`,
/// `reused`) is answered without touching the host holder when the main thread is not available.
final class BackgroundModelingImageHolder {
  let url: URL?
  let modelPlaceholder: ImagePlaceholder?

  private let makeHost: () -> ImageHolder
  /// Created, read and written on the main thread only.
  private var host: ImageHolder?
  private let lastKnownDisplaySize = Atomic<CGSize?>(initialValue: nil)

  init(
    url: URL?,
    placeholder: ImagePlaceholder?,
    makeHost: @escaping () -> ImageHolder
  ) {
    self.url = url
    self.modelPlaceholder = placeholder
    self.makeHost = makeHost
    if Thread.isMainThread {
      host = makeHost()
    }
  }

  private func resolvedHost() -> ImageHolder {
    Thread.assertIsMain()
    let host: ImageHolder
    if let existingHost = self.host {
      host = existingHost
    } else {
      host = makeHost()
      self.host = host
    }
    updateLastKnownDisplaySize(host)
    return host
  }

  private func updateLastKnownDisplaySize(_ host: ImageHolder) {
    let displaySize = host.displaySize
    lastKnownDisplaySize.accessWrite { $0 = displaySize }
  }
}

extension BackgroundModelingImageHolder: ImageHolder {
  var image: Image? {
    Thread.isMainThread ? resolvedHost().image : nil
  }

  var placeholder: ImagePlaceholder? {
    modelPlaceholder
  }

  var displaySize: CGSize? {
    if Thread.isMainThread {
      return resolvedHost().displaySize
    }
    return lastKnownDisplaySize.accessRead { $0 }
  }

  @discardableResult
  func requestImageWithCompletion(
    _ completion: @escaping @MainActor (Image?) -> Void
  ) -> Cancellable? {
    let host = resolvedHost()
    return host.requestImageWithCompletion { [weak self] image in
      self?.updateLastKnownDisplaySize(host)
      completion(image)
    }
  }

  @discardableResult
  func requestImageWithSource(
    _ completion: @escaping CompletionHandlerWithSource
  ) -> Cancellable? {
    let host = resolvedHost()
    return host.requestImageWithSource { [weak self] result in
      self?.updateLastKnownDisplaySize(host)
      completion(result)
    }
  }

  func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    if Thread.isMainThread {
      return resolvedHost().reused(
        with: placeholder,
        remoteImageURL: remoteImageURL
      ) == nil ? nil : self
    }
    return (url == remoteImageURL && modelPlaceholder === placeholder) ? self : nil
  }

  func equals(_ other: ImageHolder) -> Bool {
    guard let other = other as? BackgroundModelingImageHolder else {
      return false
    }

    return resolvedHost().equals(other.resolvedHost())
  }

  nonisolated var debugDescription: String {
    "BackgroundModelingImageHolder(url: \(dbgStr(url)))"
  }
}

struct BackgroundModelingImageHolderFactory: DivImageHolderFactory {
  private let wrapped: DivImageHolderFactory

  init(wrapped: DivImageHolderFactory) {
    self.wrapped = wrapped
  }

  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    let wrapped = wrapped
    return BackgroundModelingImageHolder(url: url, placeholder: placeholder) {
      wrapped.make(url, placeholder)
    }
  }
}
