import Foundation
import VGSL

public final class RemoteAnimationHolder: AnimationHolder {
  public enum AnimationType {
    case lottie
    case rive
  }

  private typealias AsyncAnimationRequester = (@escaping (AnimationSourceType?) -> Void)
    -> Cancellable?

  public private(set) var animation: AnimationSourceType?

  let url: URL

  private let animationType: AnimationType
  private let resourceRequester: AsyncAnimationRequester

  public init(
    url: URL,
    animationType: AnimationType,
    requester: URLResourceRequesting,
    localDataProvider: ((URL) -> Data?)?
  ) {
    weak var weakSelf: RemoteAnimationHolder?
    self.url = url
    self.animationType = animationType
    if let provider = localDataProvider, let data = provider(url) {
      self.animation = animationType == .lottie ? LottieAnimationSourceType
        .data(data) : RiveAnimationSourceType.data(data)
    }
    let requester = AsyncResourceRequester<AnimationSourceType> { completion in
      requester.getDataWithSource(from: url, completion: { result in
        Thread.assertIsMain()
        guard let self = weakSelf else { return }
        switch result {
        case let .success(value):
          self.animation = animationType == .lottie ? LottieAnimationSourceType
            .data(value.data) : RiveAnimationSourceType.data(value.data)
          completion(self.animation)
        case .failure:
          completion(nil)
        }
      })
    }
    self.resourceRequester = requester.requestResource
    weakSelf = self
  }

  @discardableResult
  public func requestAnimationWithCompletion(
    _ completion: @escaping (AnimationSourceType?)
      -> Void
  ) -> Cancellable? {
    if let animation = self.animation {
      completion(animation)
      return nil
    }
    return resourceRequester(completion)
  }

  public func equals(_ other: AnimationHolder) -> Bool {
    guard let other = other as? RemoteAnimationHolder else {
      return false
    }
    return url == other.url
  }
}

extension RemoteAnimationHolder: CustomDebugStringConvertible {
  public var debugDescription: String {
    "URL = \(dbgStr(url)))"
  }
}
