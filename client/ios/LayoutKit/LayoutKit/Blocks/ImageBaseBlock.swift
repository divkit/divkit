import CoreGraphics
import Foundation
import VGSL

public protocol ImageBaseBlock: BlockWithWidthTrait {
  var path: UIElementPath? { get }
  var widthTrait: LayoutTrait { get }
  var height: ImageBlockHeight { get }
  var imageHolder: ImageHolder { get }
  var state: ImageBaseBlockState { get }

  func makeCopy() -> Self
}

public enum ImageBlockHeight: Equatable {
  case trait(LayoutTrait)
  case ratio(Double)
}

public struct ImageBaseBlockState: ElementState, Equatable {
  public let intrinsicContentSize: CGSize?
  public init(widthTrait: LayoutTrait, height: ImageBlockHeight, imageHolder: ImageHolder) {
    let hasIntrinsicSize = widthTrait == .intrinsic || height == .trait(.intrinsic)
    self.intrinsicContentSize = hasIntrinsicSize ? imageHolder.currentImageSize : nil
  }
}

extension ImageBaseBlock {
  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minWidth, maxWidth):
      let intrinsicWidth: CGFloat
      switch height {
      case let .trait(.fixed(height)):
        let aspectRatio = imageHolder.currentImageSize.aspectRatio ?? 0
        intrinsicWidth = aspectRatio * height
      case .trait(.intrinsic), .trait(.weighted), .ratio:
        intrinsicWidth = imageHolder.currentImageSize.width
      }

      return clamp(intrinsicWidth, min: minWidth, max: maxWidth)
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch height {
    case let .trait(.fixed(value)):
      return value
    case .trait(let .intrinsic(_, minHeight, maxHeight)):
      let intrinsicHeight: CGFloat
      switch widthTrait {
      case let .fixed(width):
        if let aspectRatio = imageHolder.currentImageSize.aspectRatio {
          intrinsicHeight = width / aspectRatio
        } else {
          intrinsicHeight = 0
        }
      case .intrinsic, .weighted:
        intrinsicHeight = imageHolder.currentImageSize.height
      }

      return clamp(intrinsicHeight, min: minHeight, max: maxHeight)
    case .trait(.weighted):
      return 0
    case let .ratio(ratio):
      return width / CGFloat(ratio)
    }
  }

  public var isVerticallyResizable: Bool {
    switch height {
    case let .trait(trait):
      trait.isResizable
    case .ratio:
      false
    }
  }

  public var isVerticallyConstrained: Bool {
    switch height {
    case let .trait(trait):
      trait.isConstrained
    case .ratio:
      false
    }
  }

  public func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    if case .trait(.weighted) = height {
      assertionFailure("cannot get heightOfVerticallyNonResizableBlock for resizable block")
      return 0
    }
    return intrinsicContentHeight(forWidth: width)
  }

  func updateStateIfNeeded(observer: ElementStateObserver?) {
    guard let observer, let path else { return }
    let updatedState = ImageBaseBlockState(
      widthTrait: widthTrait,
      height: height,
      imageHolder: imageHolder
    )
    guard updatedState != state else { return }
    observer.elementStateChanged(updatedState, forPath: path)
  }

  public func updated(withStates states: BlocksState) throws -> Self {
    guard let path, let newState: ImageBaseBlockState = states.getState(at: path),
          newState != self.state else {
      return self
    }
    return self.makeCopy()
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

extension ImageHolder {
  fileprivate var currentImageSize: CGSize {
    if let image {
      image.size
    } else if let placeholder {
      placeholder.size
    } else {
      .zero
    }
  }
}

extension ImagePlaceholder {
  fileprivate var size: CGSize {
    switch self {
    case let .image(image):
      return image.size
    case .color:
      return .zero
    case .view, .imageData:
      assertionFailure("cannot get size of view or imageData")
      return .zero
    }
  }
}
