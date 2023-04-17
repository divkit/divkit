// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

public final class DivVideoDataVideo {
  public static let type: String = "video"
  public let videoSources: [DivVideoDataVideoSource] // at least 1 elements

  static let videoSourcesValidator: AnyArrayValueValidator<DivVideoDataVideoSource> =
    makeArrayValidator(minItems: 1)

  init(
    videoSources: [DivVideoDataVideoSource]
  ) {
    self.videoSources = videoSources
  }
}

#if DEBUG
extension DivVideoDataVideo: Equatable {
  public static func ==(lhs: DivVideoDataVideo, rhs: DivVideoDataVideo) -> Bool {
    guard
      lhs.videoSources == rhs.videoSources
    else {
      return false
    }
    return true
  }
}
#endif

extension DivVideoDataVideo: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    var result: [String: ValidSerializationValue] = [:]
    result["type"] = Self.type
    result["video_sources"] = videoSources.map { $0.toDictionary() }
    return result
  }
}
