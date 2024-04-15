import BasePublic
import DivKit
import Foundation
import LayoutKit
import Network
import NetworkingPublic

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
    var scale: DivImageScale = .fit
    if let divGifImage = div as? DivGifImage {
      scale = divGifImage.resolveScale(context.expressionResolver)
    }
    let animationHolder: AnimationHolder = switch params.source {
    case let .url(url):
      RemoteAnimationHolder(
        url: url,
        animationType: .lottie,
        requester: requester,
        localDataProvider: localAnimationDataProvider
      )
    case let .json(json):
      JSONAnimationHolder(json: json)
    }
    return LottieAnimationBlock(
      animatableView: Lazy(
        getter: {
          self.factory.createAnimatableView(
            withMode: params.repeatMode,
            repeatCount: params.repeatCount
          )
        }
      ),
      animationHolder: animationHolder,
      sizeProvider: block,
      scale: scale
    )
  }

  static func getPreloadURL(div: DivBase) -> URL? {
    let extensionData = div.extensions?.first { $0.id == "lottie" }
    guard let paramsDict = extensionData?.params,
          let params = LottieExtensionParams(params: paramsDict) else { return nil }
    return params.source.url
  }
}

private class JSONAnimationHolder: AnimationHolder {
  let animation: AnimationSourceType?

  init(json: [String: Any]) {
    self.animation = LottieAnimationSourceType.json(json)
  }

  func requestAnimationWithCompletion(
    _ completion: @escaping (AnimationSourceType?) -> Void
  ) -> Cancellable? {
    completion(animation)
    return nil
  }

  func equals(_ other: AnimationHolder) -> Bool {
    if let animation = animation as? LottieAnimationSourceType,
       let otherAnimation = other.animation as? LottieAnimationSourceType {
      return other is JSONAnimationHolder && animation == otherAnimation
    }
    return false
  }

  var debugDescription: String {
    guard let animation = animation as? LottieAnimationSourceType,
          case let .json(json) = animation else {
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

extension LottieExtensionParams.Source {
  fileprivate var url: URL? {
    switch self {
    case let .url(url): url
    case .json: nil
    }
  }
}
