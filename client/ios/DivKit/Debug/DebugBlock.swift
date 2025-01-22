import Foundation
import LayoutKit
import VGSL

final class DebugBlock: BlockWithTraits, LayoutCachingDefaultImpl {
  let widthTrait = LayoutTrait.fixed(buttonSize)
  let heightTrait = LayoutTrait.fixed(buttonSize)

  var intrinsicContentWidth: CGFloat {
    widthTrait.fixedValue ?? 0.0
  }

  func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    heightTrait.fixedValue ?? 0.0
  }

  let errorCollector: DebugErrorCollector
  let showDebugInfo: (ViewType) -> Void

  init(
    errorCollector: DebugErrorCollector,
    showDebugInfo: @escaping (ViewType) -> Void
  ) {
    self.errorCollector = errorCollector
    self.showDebugInfo = showDebugInfo
  }

  func equals(_ other: any LayoutKit.Block) -> Bool {
    if self === other { return true }
    guard let other = other as? DebugBlock else { return false }
    return errorCollector === other.errorCollector
  }

  var debugDescription: String {
    "DebugBlock errors: \(errorCollector.debugDescription))"
  }

  func getImageHolders() -> [any VGSLUI.ImageHolder] {
    []
  }

  func updated(withStates _: LayoutKit.BlocksState) throws -> Self {
    self
  }
}

private let buttonSize = 50.0
