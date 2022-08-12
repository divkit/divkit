// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import CommonCore

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
    case .intrinsic:
      return imageHolder.placeholder!.size.width
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch height {
    case let .trait(.fixed(value)):
      return value
    case .trait(.intrinsic):
      return imageHolder.placeholder!.size.height
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
      fatalError()
    }
    return intrinsicContentHeight(forWidth: width)
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    guard case let .trait(.weighted(value)) = height else {
      fatalError()
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
    case .color:
      fatalError()
    }
  }
}
