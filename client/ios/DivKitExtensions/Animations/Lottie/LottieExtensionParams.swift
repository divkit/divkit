import DivKit
import LayoutKitInterface
import Foundation
import VGSL

struct LottieExtensionParams {
  enum Source {
    case json([String: Any])
    case url(URL)
  }

  enum Defaults {
    public static let defaultRepeatCount: Float = 0
    public static let defaultRepeatMode: AnimationRepeatMode = .restart
  }

  var source: Source
  var repeatCount: Float
  var repeatMode: AnimationRepeatMode

  init?(
    paramsDictionary: [String: Any],
    expressionResolver: ExpressionResolver
  ) {
    if let json = paramsDictionary["lottie_json"] as? [String: Any] {
      source = .json(json)
    } else if let urlValue = paramsDictionary["lottie_url"] as? String,
              let url = expressionResolver.resolveUrl(urlValue) {
      source = .url(url)
    } else {
      DivKitLogger.error("Not valid lottie_json or lottie_url")
      return nil
    }

    self.repeatCount = (try? paramsDictionary.getOptionalFloat("repeat_count", expressionResolver: expressionResolver)).map(Float.init)
      ?? Defaults.defaultRepeatCount

    self.repeatMode = (paramsDictionary["repeat_mode"] as? String)
        .flatMap(expressionResolver.resolveString)
        .flatMap(AnimationRepeatMode.init)
        ?? Defaults.defaultRepeatMode
  }

  init(source: Source, repeatCount: Float, repeatMode: AnimationRepeatMode) {
    self.source = source
    self.repeatCount = repeatCount
    self.repeatMode = repeatMode
  }
}

private extension AnimationRepeatMode {
  init?(repeatModeString: String) {
    switch repeatModeString {
    case "reverse":
      self = .reverse
    case "restart":
      self = .restart
    default:
      return nil
    }
  }
}

extension LottieExtensionParams.Source {
  var url: URL? {
    switch self {
    case let .url(url): url
    case .json: nil
    }
  }
}
