import CoreGraphics

import CommonCorePublic

public final class SliderBlock: BlockWithTraits {
  // MARK: - BlockWithTraits

  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  // MARK: - SliderBlock

  let sliderModel: SliderModel

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let width = sliderModel.sliderIntrinsicWidth
      return clamp(width, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, maxSize):
      let height = sliderModel.sliderHeight
      return clamp(height, min: minSize, max: maxSize)
    case .weighted:
      return 0
    }
  }

  public init(
    sliderModel: SliderModel,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) {
    self.sliderModel = sliderModel
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
  }

  public func equals(_ other: Block) -> Bool {
    (other as? SliderBlock)?.sliderModel == sliderModel
  }

  public func getImageHolders() -> [ImageHolder] {
    []
  }

  public var debugDescription: String {
    """
    Slider min value: \(sliderModel.minValue)
    Slider max value: \(sliderModel.maxValue)
    First thumb value: \(sliderModel.firstThumb.value)
    """ + (sliderModel.secondThumb.flatMap { "Second thumb value: \($0.value)" } ?? "")
  }
}

extension SliderBlock: LayoutCachingDefaultImpl {}
extension SliderBlock: ElementStateUpdatingDefaultImpl {}
