#if os(iOS)
import CoreGraphics
@testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct SliderViewLayoutTests {
  @Test(
    arguments: [UserInterfaceLayoutDirection.leftToRight, .rightToLeft],
    [4, 3, -996]
  )
  func degenerateRangeKeepsFiniteFramesAndLeadingThumb(
    direction: UserInterfaceLayoutDirection,
    maxValue: Int
  ) {
    let model = makeDegenerateSliderModel(direction: direction, maxValue: maxValue)
    #expect(model.ranges.isEmpty)
    let view = SliderView()
    view.frame = CGRect(x: 0, y: 0, width: 200, height: 40)
    view.setSliderModel(model)
    view.layoutIfNeeded()

    expectFiniteFrames(in: view)

    let rangeViews = view.subviews
      .filter { !($0 is BlockView) && !($0 is MarksView) }
      .flatMap(\.subviews)
    #expect(rangeViews.isEmpty)
    #expect(model.firstThumb.value.value == 4)
    #expect(model.secondThumb?.value.value == 4)

    let thumbs = view.subviews.compactMap { $0 as? BlockView }
    #expect(thumbs.count == 2)
    for thumb in thumbs {
      switch direction {
      case .leftToRight:
        #expect(thumb.frame.minX.isApproximatelyEqualTo(0))
      case .rightToLeft:
        #expect(thumb.frame.maxX.isApproximatelyEqualTo(view.bounds.width))
      @unknown default:
        Issue.record("Unknown layout direction")
      }
    }

    if let marksView = view.subviews.first(where: { $0 is MarksView }),
       marksView.bounds.width > 0,
       marksView.bounds.height > 0 {
      let renderer = UIGraphicsImageRenderer(bounds: marksView.bounds)
      _ = renderer.image { context in
        marksView.layer.render(in: context.cgContext)
      }
    }
  }
}

@MainActor
private func expectFiniteFrames(in view: UIView) {
  for subview in view.subviews {
    #expect(subview.frame.origin.x.isFinite)
    #expect(subview.frame.origin.y.isFinite)
    #expect(subview.frame.size.width.isFinite)
    #expect(subview.frame.size.height.isFinite)
    expectFiniteFrames(in: subview)
  }
}

private func makeDegenerateSliderModel(
  direction: UserInterfaceLayoutDirection,
  maxValue: Int
) -> SliderModel {
  let thumbSize = CGSize(width: 24, height: 24)
  let thumb = { (name: String, value: Int) in
    SliderModel.ThumbModel(
      block: SeparatorBlock(),
      value: Binding(name: name, value: Property(initialValue: value)),
      size: thumbSize,
      offset: .zero
    )
  }
  let track = SeparatorBlock()
  return SliderModel(
    firstThumb: thumb("first", 15),
    secondThumb: thumb("second", -3),
    minValue: 4,
    maxValue: maxValue,
    marksConfiguration: MarksConfigurationModel(
      minValue: 4,
      maxValue: CGFloat(maxValue),
      activeMark: MarksConfigurationModel.RoundedRectangle(
        size: CGSize(width: 4, height: 4),
        cornerRadius: 2,
        color: .black,
        borderWidth: 0,
        borderColor: .clear
      ),
      inactiveMark: MarksConfigurationModel.RoundedRectangle(
        size: CGSize(width: 4, height: 4),
        cornerRadius: 2,
        color: .gray,
        borderWidth: 0,
        borderColor: .clear
      ),
      layoutDirection: direction
    ),
    ranges: [
      SliderModel.RangeModel(
        start: 0,
        end: 10,
        margins: .zero,
        activeTrack: track,
        inactiveTrack: track
      ),
    ],
    layoutDirection: direction,
    path: UIElementPath("slider"),
    isEnabled: true,
    pressStartActions: [],
    pressEndActions: []
  )
}
#endif
