// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum DivVideoData {
  case divVideoDataVideo(DivVideoDataVideo)
  case divVideoDataStream(DivVideoDataStream)

  public var value: Serializable {
    switch self {
    case let .divVideoDataVideo(value):
      return value
    case let .divVideoDataStream(value):
      return value
    }
  }
}

#if DEBUG
extension DivVideoData: Equatable {
  public static func ==(lhs: DivVideoData, rhs: DivVideoData) -> Bool {
    switch (lhs, rhs) {
    case let (.divVideoDataVideo(l), .divVideoDataVideo(r)):
      return l == r
    case let (.divVideoDataStream(l), .divVideoDataStream(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension DivVideoData: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
