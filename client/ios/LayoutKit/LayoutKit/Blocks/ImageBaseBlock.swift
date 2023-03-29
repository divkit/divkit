import CoreGraphics
import Foundation

import CommonCorePublic

public protocol ImageBaseBlock: BlockWithWidthTrait {
  var height: ImageBlockHeight { get }
  var imageHolder: ImageHolder { get }
}

public enum ImageBlockHeight: Equatable {
  case trait(LayoutTrait)
  case ratio(Double)
}

extension ImageBaseBlock {
  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let width = imageHolder.placeholder!.size.width
      return clamp(width, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch height {
    case let .trait(.fixed(value)):
      return value
    case .trait(let .intrinsic(_, minSize, maxSize)):
      let height = imageHolder.placeholder!.size.height
      return clamp(height, min: minSize, max: maxSize)
    case .trait(.weighted):
      return 0
    case let .ratio(ratio):
      return width / CGFloat(ratio)
    }
  }

  public var isVerticallyResizable: Bool {
    switch height {
    case let .trait(trait):
      return trait.isResizable
    case .ratio:
      return false
    }
  }

  public var isVerticallyConstrained: Bool {
    switch height {
    case let .trait(trait):
      return trait.isConstrained
    case .ratio:
      return false
    }
  }

  public func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    if case .trait(.weighted) = height {
      assertionFailure("cannot get heightOfVerticallyNonResizableBlock for resizable block")
      return 0
    }
    return intrinsicContentHeight(forWidth: width)
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    guard case let .trait(.weighted(value)) = height else {
      assertionFailure("cannot get weightOfVerticallyResizableBlock for non resizable block")
      return .default
    }
    return value
  }

  public func getImageHolders() -> [ImageHolder] {
    [imageHolder]
  }
}

extension ImagePlaceholder {
  fileprivate var size: CGSize {
    switch self {
    case let .image(image):
      return image.size
    case .color, .view:
      assertionFailure("cannot get size of color or view")
      return .zero
    @unknown default:
      assertionFailure("cannot get size of unknown image placeholder")
      return .zero
    }
  }
}
