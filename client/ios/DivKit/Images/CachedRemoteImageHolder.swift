import Foundation
import VGSL

final class CachedRemoteImageHolder: ImageHolder {
  private(set) var image: Image?

  private let wrapped: ImageHolder

  var placeholder: ImagePlaceholder? { wrapped.placeholder }

  init(wrapped: ImageHolder) {
    self.wrapped = wrapped
  }

  @discardableResult
  @MainActor
  func requestImageWithCompletion(
    _ completion: @escaping @MainActor (Image?) -> Void
  ) -> Cancellable? {
    if image != nil {
      completion(image)
      return nil
    }

    return wrapped.requestImageWithCompletion { [weak self] image in
      self?.image = self?.wrapped.image
      completion(image)
    }
  }

  @discardableResult
  @MainActor
  func requestImageWithSource(
    _ completion: @escaping CompletionHandlerWithSource
  ) -> Cancellable? {
    if let image {
      completion((image, .cache))
      return nil
    }

    return wrapped.requestImageWithSource { [weak self] result in
      self?.image = self?.wrapped.image
      completion(result)
    }
  }

  func equals(_ other: ImageHolder) -> Bool {
    guard let other = other as? Self else {
      return false
    }

    return wrapped.equals(other.wrapped)
  }
}

extension CachedRemoteImageHolder {
  func reused(with placeholder: ImagePlaceholder?, remoteImageURL: URL?) -> ImageHolder? {
    wrapped.reused(
      with: placeholder,
      remoteImageURL: remoteImageURL
    ) == nil ? nil : self
  }
}

extension CachedRemoteImageHolder {
  nonisolated var debugDescription: String {
    onMainThreadSync {
      "CachedRemoteImageHolder, placeholder = \(dbgStr(placeholder?.debugDescription))"
    }
  }
}
