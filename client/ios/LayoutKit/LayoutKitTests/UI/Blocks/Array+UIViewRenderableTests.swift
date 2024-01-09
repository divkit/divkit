@testable import LayoutKit

import XCTest

import CommonCorePublic

final class Array_UIViewRenderableTests: XCTestCase {
  func test_BlocksOrderDefinesViewsOrder() {
    let parent = UIView(frame: .zero)

    let views = [].reused(
      with: renderables,
      attachTo: parent,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual(views, parent.subviews)
    let viewsBlocks = (parent.subviews as! [TestView]).compactMap(\.renderable)
    XCTAssertTrue(viewsBlocks.elementsEqual(renderables, by: ===))
  }

  func test_BlocksOrderDefinesReusedViewsOrder() {
    let parent = UIView(frame: .zero)
    let renderedViews = [].reused(
      with: renderables,
      attachTo: parent,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    var newBlocks = Array(renderables.dropFirst()) as [UIViewRenderable]
    newBlocks.insert(otherRenderable, at: 1)

    let reusedViews = renderedViews.reused(
      with: newBlocks,
      attachTo: parent,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual(reusedViews, parent.subviews)
    XCTAssertTrue((parent.subviews[0] as? TestView)?.renderable === renderables[1])
    XCTAssertTrue((parent.subviews[1] as? OtherTestView)?.renderable === otherRenderable)
    XCTAssertTrue((parent.subviews[2] as? TestView)?.renderable === renderables[2])
  }
}

private final class TestRenderable: UIViewRenderable {
  static func makeBlockView() -> BlockView { TestView() }
  func canConfigureBlockView(_ view: BlockView) -> Bool { view is TestView }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    precondition(observer === nil)
    (view as! TestView).renderable = self
  }

  init() {}
}

private final class TestView: BlockView, VisibleBoundsTrackingLeaf {
  var renderable: TestRenderable?
  var effectiveBackgroundColor: UIColor? { backgroundColor }
}

private final class OtherTestRenderable: UIViewRenderable {
  static func makeBlockView() -> BlockView { OtherTestView() }
  func canConfigureBlockView(_ view: BlockView) -> Bool { view is OtherTestView }

  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    precondition(observer === nil)
    (view as! OtherTestView).renderable = self
  }

  init() {}
}

private final class OtherTestView: BlockView, VisibleBoundsTrackingLeaf {
  var renderable: OtherTestRenderable?
  var effectiveBackgroundColor: UIColor? { backgroundColor }
}

private let renderables = [TestRenderable(), TestRenderable(), TestRenderable()]

private let otherRenderable = OtherTestRenderable()
