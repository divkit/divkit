import DivKit
import Foundation
import LayoutKit
import Network
import VGSL

public final class LottieExtensionHandler: DivExtensionHandler {
  public let id = "lottie"

  private let factory: AsyncSourceAnimatableViewFactory
  private let requester: URLResourceRequesting
  private let localAnimationDataProvider: ((URL) -> Data?)?

  public init(
    factory: AsyncSourceAnimatableViewFactory,
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
          let params = LottieExtensionParams(
            paramsDictionary: paramsDict,
            expressionResolver: context.expressionResolver
          ) else { return block }
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
          self.factory.createAsyncSourceAnimatableView(
            withMode: params.repeatMode,
            repeatCount: params.repeatCount
          )
        }
      ),
      animationHolder: animationHolder,
      sizeProvider: block,
      scale: scale,
      isPlaying: params.isPlaying
    )
  }

  public func getPreloadURLs(div: DivBase, expressionResolver: ExpressionResolver) -> [URL] {
    [Self.getPreloadURL(div: div, expressionResolver: expressionResolver)].compactMap { $0 }
  }

  static func getPreloadURL(div: DivBase, expressionResolver: ExpressionResolver) -> URL? {
    let extensionData = div.extensions?.first { $0.id == "lottie" }
    guard let paramsDict = extensionData?.params,
          let params = LottieExtensionParams(
            paramsDictionary: paramsDict,
            expressionResolver: expressionResolver
          ) else {
      return nil
    }
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
