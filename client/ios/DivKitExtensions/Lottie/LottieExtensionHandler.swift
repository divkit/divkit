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
    let extensionParams = div.extensions?
      .first(where: { $0.id == self.id })
    guard let extensionParams = extensionParams?.params,
            let lottieValue = LottieValues(
              params: extensionParams
            ) else { return block }
    
    let animationHolder: AnimationHolder
    switch lottieValue.source {
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
            withMode: lottieValue.repeatMode.repeateMode,
            repeatCount: lottieValue.repeatCount
          )
        }
      ),
      animationHolder: animationHolder,
      widthTrait: .resizable,
      heightTrait: .resizable
    )
  }
}

extension LottieValues.RepeatMode {
  var repeateMode: AnimationRepeatMode {
    switch self {
    case .reverse:
      return .reverse
    case .restart:
      return .restart
    }
  }
}

private class JSONAnimationHolder: AnimationHolder {
  let animation: AnimationSourceType?

  init(json: [String: Any]) {
    self.animation = .json(json)
  }
  
  func requestAnimationWithCompletion(_ completion: @escaping (AnimationSourceType?) -> Void) -> Cancellable? {
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

private struct LottieValues {
  enum ValuesError: String, Error {
    case notValidJSONOrURL
    case notValidRepeatCount
    case notValidRepeatMode
  }
  
  enum Source {
    case json([String: Any])
    case url(URL)
  }
  
  enum RepeatMode: String {
    case reverse
    case restart
  }
  
  var source: Source
  var repeatCount: Float
  var repeatMode: RepeatMode
  
  init?(params: [String: Any]) {
    if let json = params["lottie_json"] as? [String: Any] {
      source = .json(json)
    } else if let urlString = params["lottie_url"] as? String, let url = URL(string: urlString) {
      source = .url(url)
    } else {
      DivKitLogger.error("Not valid lottie_json or lottie_url")
      return nil
    }
    
    if let repeatCount = params["repeat_count"] as? Float {
      self.repeatCount = repeatCount
    } else {
      DivKitLogger.error("Not valid repeat_count")
      return nil
    }
    
    let repeatModeString = params["repeat_mode"] as? String
    switch repeatModeString {
    case "reverse":
      self.repeatMode = .reverse
    case "restart":
      self.repeatMode = .restart
    default:
      DivKitLogger.error("Not valid repeat_mode")
      return nil
    }
  }
}
