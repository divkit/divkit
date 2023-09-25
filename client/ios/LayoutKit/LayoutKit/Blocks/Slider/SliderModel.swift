import CoreGraphics
import Foundation

import CommonCorePublic

#if canImport(UIKit)
import UIKit
#endif

public struct SliderModel: Equatable {
  public struct ThumbModel: Equatable {
    public let block: Block
    public var value: Binding<Int>
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
      self.value = value
      self.size = size
      self.offsetX = offsetX
      self.offsetY = offsetY
    }

    static var empty: Self {
      ThumbModel(
        block: EmptyBlock.zeroSized,
        value: .zero,
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

  public struct RangeModel: Equatable {
    public let start: Int
    public let end: Int
    public let margins: EdgeInsets
    public let activeTrack: Block
    public let inactiveTrack: Block

    public init(
      start: Int,
      end: Int,
      margins: EdgeInsets,
      activeTrack: Block,
      inactiveTrack: Block
    ) {
      self.start = start
      self.end = end
      self.margins = margins
      self.activeTrack = activeTrack
      self.inactiveTrack = inactiveTrack
    }

    public static func ==(lhs: SliderModel.RangeModel, rhs: SliderModel.RangeModel) -> Bool {
      lhs.start == rhs.start && lhs.end == rhs.end && lhs.activeTrack == rhs.activeTrack && lhs
        .inactiveTrack == rhs.inactiveTrack && lhs.margins == rhs.margins
    }
  }

  public var firstThumb: ThumbModel
  public var secondThumb: ThumbModel?

  public let minValue: Int
  public let maxValue: Int
  public let marksConfiguration: MarksConfiguration
  public let ranges: [RangeModel]
  public let layoutDirection: UserInterfaceLayoutDirection

  public var valueRange: Int {
    maxValue - minValue
  }

  public var sliderHeight: CGFloat {
    max(
      marksConfiguration.activeMark.size.height,
      marksConfiguration.inactiveMark.size.height,
      firstThumb.size.height,
      secondThumb?.size.height ?? 0,
      ranges.map { max(
        $0.activeTrack.intrinsicContentHeight(forWidth: .infinity),
        $0.inactiveTrack.intrinsicContentHeight(forWidth: .infinity)
      ) }.max() ?? 0
    )
  }

  public var sliderIntrinsicWidth: CGFloat {
    let maxMarkWidth = max(
      marksConfiguration.activeMark.size.width,
      marksConfiguration.inactiveMark.size.width
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
      marksConfiguration.activeMark.size.width,
      marksConfiguration.inactiveMark.size.width,
      firstThumb.size.width,
      secondThumb?.size.width ?? 0
    )
  }

  public init(
    firstThumb: ThumbModel,
    secondThumb: ThumbModel? = nil,
    minValue: Int,
    maxValue: Int,
    marksConfiguration: MarksConfiguration,
    ranges: [RangeModel],
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight
  ) {
    self.firstThumb = firstThumb
    self.secondThumb = secondThumb
    self.minValue = minValue
    self.maxValue = maxValue
    self.marksConfiguration = marksConfiguration
    self.ranges = ranges
    self.layoutDirection = layoutDirection
  }

  static var empty: Self {
    SliderModel(
      firstThumb: .empty,
      secondThumb: nil,
      minValue: 0,
      maxValue: 0,
      marksConfiguration: .empty,
      ranges: [],
      layoutDirection: .leftToRight
    )
  }

  public static func ==(lhs: SliderModel, rhs: SliderModel) -> Bool {
    lhs.firstThumb == rhs.firstThumb &&
      lhs.secondThumb == rhs.secondThumb &&
      lhs.maxValue == rhs.maxValue &&
      lhs.minValue == rhs.minValue &&
      lhs.marksConfiguration == rhs.marksConfiguration &&
      lhs.ranges == rhs.ranges
  }
}
