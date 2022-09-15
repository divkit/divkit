import Foundation

import Base
import Networking

final class RemoteAnimationHolder: AnimationHolder {
  private typealias AsyncAnimationRequester = (@escaping (AnimationSourceType?) -> Void)
  -> Cancellable?
  
  let url: URL
  private(set) var animation: AnimationSourceType?
  private let resourceRequester: AsyncAnimationRequester
  
  init(url: URL, requester: URLResourceRequesting, localDataProvider: ((URL) -> Data?)?) {
    weak var weakSelf: RemoteAnimationHolder?
    self.url = url
    if let provider = localDataProvider, let data = provider(url) {
      self.animation = .data(data)
    }
    let requester = AsyncResourceRequester<AnimationSourceType> { completion in
      requester.getDataWithSource(from: url,  completion: { result in
        Thread.assertIsMain()
        guard let self = weakSelf else { return }
        switch result {
        case let .success(value):
          self.animation = .data(value.data)
          completion(self.animation)
        case .failure(_):
          completion(nil)
        }
      })
    }
    self.resourceRequester = requester.requestResource
    weakSelf = self
  }
  
  @discardableResult
  func requestAnimationWithCompletion(_ completion: @escaping (AnimationSourceType?) -> Void) -> Cancellable? {
    if let animation = self.animation {
      completion(animation)
      return nil
    }
    return resourceRequester(completion)
  }
  
  func equals(_ other: AnimationHolder) -> Bool {
    guard let other = other as? RemoteAnimationHolder else {
      return false
    }
    return url == other.url
  }
}

extension RemoteAnimationHolder: CustomDebugStringConvertible {
  var debugDescription: String {
    "URL = \(dbgStr(url)))"
  }
}
