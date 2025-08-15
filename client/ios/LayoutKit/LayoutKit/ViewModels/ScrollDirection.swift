import Foundation

@frozen
public enum ScrollDirection: Equatable, CaseIterable {
  case horizontal
  case vertical

  public var isHorizontal: Bool {
    switch self {
    case .horizontal:
      true
    case .vertical:
      false
    }
  }
}
