import CoreGraphics
import Foundation

import CommonCorePublic

public struct SliderModel: Equatable {
  public struct ThumbModel: Equatable {
    public let block: Block
    @Binding public var value: Int
    public let size: CGSize
    public let offsetX: CGFloat
    public let offsetY: CGFloat

    public init(
      block: Block,
      value: Binding<Int>,
      size: CGSize,
      offsetX: CGFloat,
      offsetY: CGFloat
    ) {
      self.block = block
      self._value = value
      self.size = size
      self.offsetX = offsetX
      self.offsetY = offsetY
    }

    static var empty: Self {
      ThumbModel(
        block: EmptyBlock.zeroSized,
        value: Binding(name: "", getValue: { _ in 0 }, userInterfaceActionFactory: { _, _ in nil }),
        size: .zero,
        offsetX: 0,
        offsetY: 0
      )
    }

    public static func ==(lhs: SliderModel.ThumbModel, rhs: SliderModel.ThumbModel) -> Bool {
      lhs.value == rhs.value &&
        lhs.size == rhs.size &&
        lhs.block == rhs.block
    }
  }

  public struct MarkModel: Equatable {
    public let block: Block
    public let size: CGSize

    public init(
      block: Block,
      size: CGSize
    ) {
      self.block = block
      self.size = size
    }

    public static func ==(lhs: SliderModel.MarkModel, rhs: SliderModel.MarkModel) -> Bool {
      lhs.block == rhs.block &&
        lhs.size == rhs.size
    }
  }

  public var firstThumb: ThumbModel
  public var secondThumb: ThumbModel?

  public let minValue: Int
  public let maxValue: Int
  public let activeMarkModel: MarkModel?
  public let inactiveMarkModel: MarkModel?
  public let activeTrack: Block
  public let inactiveTrack: Block

  public var sliderHeight: CGFloat {
    max(
      activeMarkModel?.size.height ?? 0,
      inactiveMarkModel?.size.height ?? 0,
      firstThumb.size.height,
      secondThumb?.size.height ?? 0,
      activeTrack.intrinsicContentHeight(forWidth: .infinity),
      inactiveTrack.intrinsicContentHeight(forWidth: .infinity)
    )
  }

  public var sliderIntrinsicWidth: CGFloat {
    let maxMarkWidth = max(
      activeMarkModel?.size.width ?? 0,
      inactiveMarkModel?.size.width ?? 0
    )
    let maxThumbWidth = max(firstThumb.size.width, secondThumb?.size.width ?? 0)
    return CGFloat(maxValue - minValue + 1) * maxMarkWidth + max(0, maxThumbWidth - maxMarkWidth)
  }

  public var sliderTopTextPadding: CGFloat {
    max(0, -firstThumb.offsetY, -(secondThumb?.offsetY ?? 0))
  }

  public var sliderBottomTextPadding: CGFloat {
    max(0, firstThumb.offsetY, secondThumb?.offsetY ?? 0)
  }

  public var horizontalInset: CGFloat {
    max(
      activeMarkModel?.size.width ?? 0,
      inactiveMarkModel?.size.width ?? 0,
      firstThumb.size.width,
      secondThumb?.size.width ?? 0
    )
  }

  public init(
    firstThumb: ThumbModel,
    secondThumb: ThumbModel? = nil,
    activeMarkModel: MarkModel?,
    inactiveMarkModel: MarkModel?,
    minValue: Int,
    maxValue: Int,
    activeTrack: Block,
    inactiveTrack: Block
  ) {
    self.firstThumb = firstThumb
    self.secondThumb = secondThumb
    self.minValue = minValue
    self.maxValue = maxValue
    self.activeMarkModel = activeMarkModel
    self.inactiveMarkModel = inactiveMarkModel
    self.activeTrack = activeTrack
    self.inactiveTrack = inactiveTrack
  }

  static var empty: Self {
    SliderModel(
      firstThumb: .empty,
      secondThumb: nil,
      activeMarkModel: nil,
      inactiveMarkModel: nil,
      minValue: 0,
      maxValue: 0,
      activeTrack: EmptyBlock.zeroSized,
      inactiveTrack: EmptyBlock.zeroSized
    )
  }

  public static func ==(lhs: SliderModel, rhs: SliderModel) -> Bool {
    lhs.firstThumb == rhs.firstThumb &&
      lhs.secondThumb == rhs.secondThumb &&
      lhs.maxValue == rhs.maxValue &&
      lhs.minValue == rhs.minValue &&
      lhs.inactiveMarkModel == rhs.inactiveMarkModel &&
      lhs.activeMarkModel == rhs.activeMarkModel
  }
}
