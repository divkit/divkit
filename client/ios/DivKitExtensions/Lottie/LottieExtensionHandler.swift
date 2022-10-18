import Base
import DivKit
import LayoutKit
import Foundation
import Network
import Networking

public final class LottieExtensionHandler: DivExtensionHandler {
  public let id = "lottie"
  
  private let factory: AnimatableViewFactory
  private let requester: URLResourceRequesting
  private let localAnimationDataProvider: ((URL) -> Data?)?
  
  public init(
    factory: AnimatableViewFactory,
    requester: URLResourceRequesting,
    localAnimationDataProvider: ((URL) -> Data?)? = nil
  ) {
    self.factory = factory
    self.requester = requester
    self.localAnimationDataProvider = localAnimationDataProvider
  }
  
  public func applyAfterBaseProperties(
    to block: Block,
    div: DivBase,
    context: DivBlockModelingContext
  ) -> Block {
    let extensionData = div.extensions?.first { $0.id == id }
    guard let paramsDict = extensionData?.params,
          let params = LottieExtensionParams(params: paramsDict) else {
      return block
    }
    
    let animationHolder: AnimationHolder
    switch params.source {
    case let .url(url):
      animationHolder = RemoteAnimationHolder(
        url: url,
        requester: requester,
        localDataProvider: localAnimationDataProvider
      )
    case let .json(json):
      animationHolder = JSONAnimationHolder(json: json)
    }
    return AnimationBlock(
      animatableView: Lazy(
        getter: {
          self.factory.createAnimatableView(
            withMode: params.repeatMode,
            repeatCount: params.repeatCount
          )
        }
      ),
      animationHolder: animationHolder,
      sizeProvider: block
    )
  }
}

private class JSONAnimationHolder: AnimationHolder {
  let animation: AnimationSourceType?

  init(json: [String: Any]) {
    self.animation = .json(json)
  }
  
  func requestAnimationWithCompletion(
    _ completion: @escaping (AnimationSourceType?) -> Void
  ) -> Cancellable? {
    completion(animation)
    return nil
  }
  
  func equals(_ other: AnimationHolder) -> Bool {
    other is JSONAnimationHolder && animation == other.animation
  }
  
  var debugDescription: String {
    guard case let .json(json) = animation else {
      assertionFailure("JSONAnimation holder can hold only json ")
      return ""
    }
    return "JSON Animation holder with json \(json)"
  }
}

private struct LottieExtensionParams {
  enum Source {
    case json([String: Any])
    case url(URL)
  }
  
  var source: Source
  var repeatCount: Float
  var repeatMode: AnimationRepeatMode
  
  init?(params: [String: Any]) {
    if let json = params["lottie_json"] as? [String: Any] {
      source = .json(json)
    } else if let urlString = params["lottie_url"] as? String,
              let url = URL(string: urlString) {
      source = .url(url)
    } else {
      DivKitLogger.error("Not valid lottie_json or lottie_url")
      return nil
    }
    
    if let repeatCount = params["repeat_count"] {
      if let repeatCountFloat = repeatCount as? Float {
        self.repeatCount = repeatCountFloat
      } else {
        DivKitLogger.error("Not valid repeat_count")
        return nil
      }
    } else {
      self.repeatCount = 0
    }

    if let repeatMode = params["repeat_mode"] {
      switch repeatMode as? String {
      case "reverse":
        self.repeatMode = .reverse
      case "restart":
        self.repeatMode = .restart
      default:
        DivKitLogger.error("Not valid repeat_mode")
        return nil
      }
    } else {
      self.repeatMode = .restart
    }
  }
}
