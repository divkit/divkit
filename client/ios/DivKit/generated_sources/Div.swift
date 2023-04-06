// Generated code. Do not modify.

import CommonCorePublic
import Foundation
import Serialization

@frozen
public enum Div {
  case divImage(DivImage)
  case divGifImage(DivGifImage)
  case divText(DivText)
  case divSeparator(DivSeparator)
  case divContainer(DivContainer)
  case divGrid(DivGrid)
  case divGallery(DivGallery)
  case divPager(DivPager)
  case divTabs(DivTabs)
  case divState(DivState)
  case divCustom(DivCustom)
  case divIndicator(DivIndicator)
  case divSlider(DivSlider)
  case divInput(DivInput)
  case divSelect(DivSelect)

  public var value: Serializable & DivBase {
    switch self {
    case let .divImage(value):
      return value
    case let .divGifImage(value):
      return value
    case let .divText(value):
      return value
    case let .divSeparator(value):
      return value
    case let .divContainer(value):
      return value
    case let .divGrid(value):
      return value
    case let .divGallery(value):
      return value
    case let .divPager(value):
      return value
    case let .divTabs(value):
      return value
    case let .divState(value):
      return value
    case let .divCustom(value):
      return value
    case let .divIndicator(value):
      return value
    case let .divSlider(value):
      return value
    case let .divInput(value):
      return value
    case let .divSelect(value):
      return value
    }
  }

  public var id: String? {
    switch self {
    case let .divImage(value):
      return value.id
    case let .divGifImage(value):
      return value.id
    case let .divText(value):
      return value.id
    case let .divSeparator(value):
      return value.id
    case let .divContainer(value):
      return value.id
    case let .divGrid(value):
      return value.id
    case let .divGallery(value):
      return value.id
    case let .divPager(value):
      return value.id
    case let .divTabs(value):
      return value.id
    case let .divState(value):
      return value.id
    case let .divCustom(value):
      return value.id
    case let .divIndicator(value):
      return value.id
    case let .divSlider(value):
      return value.id
    case let .divInput(value):
      return value.id
    case let .divSelect(value):
      return value.id
    }
  }
}

#if DEBUG
extension Div: Equatable {
  public static func ==(lhs: Div, rhs: Div) -> Bool {
    switch (lhs, rhs) {
    case let (.divImage(l), .divImage(r)):
      return l == r
    case let (.divGifImage(l), .divGifImage(r)):
      return l == r
    case let (.divText(l), .divText(r)):
      return l == r
    case let (.divSeparator(l), .divSeparator(r)):
      return l == r
    case let (.divContainer(l), .divContainer(r)):
      return l == r
    case let (.divGrid(l), .divGrid(r)):
      return l == r
    case let (.divGallery(l), .divGallery(r)):
      return l == r
    case let (.divPager(l), .divPager(r)):
      return l == r
    case let (.divTabs(l), .divTabs(r)):
      return l == r
    case let (.divState(l), .divState(r)):
      return l == r
    case let (.divCustom(l), .divCustom(r)):
      return l == r
    case let (.divIndicator(l), .divIndicator(r)):
      return l == r
    case let (.divSlider(l), .divSlider(r)):
      return l == r
    case let (.divInput(l), .divInput(r)):
      return l == r
    case let (.divSelect(l), .divSelect(r)):
      return l == r
    default:
      return false
    }
  }
}
#endif

extension Div: Serializable {
  public func toDictionary() -> [String: ValidSerializationValue] {
    return value.toDictionary()
  }
}
