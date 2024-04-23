@testable import LayoutKit

import XCTest

import CommonCorePublic

final class Array_UIViewRenderableTests: XCTestCase {
  func test_AttachesViewsToParent() {
    let parent = UIView(frame: .zero)

    let views = [].reused(
      with: [TestBlock(), TestBlock(), TestBlock()],
      attachTo: parent,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual(views, parent.subviews)
  }

  func test_BlocksOrderDefinesViewsOrder() {
    let blocks = [TestBlock(), TestBlock(), TestBlock()]

    let views = [].reused(
      with: blocks,
      attachTo: UIView(frame: .zero),
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    let viewsBlocks = (views as! [TestView]).compactMap(\.block)
    XCTAssertTrue(viewsBlocks.elementsEqual(blocks, by: ===))
  }

  func test_ReusesSuitableViews() {
    let oldView0 = TestView(frame: .zero)
    let oldView1 = TestView(frame: .zero)

    let views = [oldView0, oldView1].reused(
      with: [TestBlock(), OtherTestBlock(), TestBlock()],
      attachTo: UIView(frame: .zero),
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual(3, views.count)
    XCTAssertEqual(oldView0, views[0])
    XCTAssertEqual(oldView1, views[2])
  }

  func test_NotReusesUnsuitableViews() {
    let oldView0 = OtherTestView(frame: .zero)
    let oldView1 = TestView(frame: .zero)
    let oldView2 = TestView(frame: .zero)

    let views = [oldView0, oldView1, oldView2].reused(
      with: [TestBlock(), TestBlock(), TestBlock()],
      attachTo: UIView(frame: .zero),
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual(3, views.count)
    XCTAssertEqual(oldView1, views[0])
    XCTAssertEqual(oldView2, views[1])
  }

  func test_ReordersReusedViews() {
    let oldView0 = OtherTestView(frame: .zero)
    let oldView1 = TestView(frame: .zero)
    let oldView2 = TestView(frame: .zero)

    let views = [oldView0, oldView1, oldView2].reused(
      with: [TestBlock(), OtherTestBlock(), TestBlock()],
      attachTo: UIView(frame: .zero),
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual([oldView1, oldView0, oldView2], views)
  }

  func test_ReconfiguresReusedViews() {
    let block0 = TestBlock()
    let block1 = TestBlock()
    let block2 = TestBlock()
    let oldView0 = TestView(frame: .zero)
    oldView0.block = TestBlock()
    let oldView1 = TestView(frame: .zero)
    oldView1.block = TestBlock()
    let oldView2 = TestView(frame: .zero)
    oldView2.block = TestBlock()

    let views = [oldView0, oldView1, oldView2].reused(
      with: [block0, block1, block2],
      attachTo: UIView(frame: .zero),
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual([oldView0, oldView1, oldView2], views)
    XCTAssertTrue(block0 === oldView0.block)
    XCTAssertTrue(block1 === oldView1.block)
    XCTAssertTrue(block2 === oldView2.block)
  }

  func test_ReusesBestViewForReuse() {
    let block0 = TestBlock()
    let oldView0 = TestView(frame: .zero)
    let oldView1 = TestView(frame: .zero)
    let oldView2 = TestView(frame: .zero)
    oldView2.block = block0

    let views = [oldView0, oldView1, oldView2].reused(
      with: [block0, TestBlock(), TestBlock()],
      attachTo: UIView(frame: .zero),
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    XCTAssertEqual([oldView2, oldView0, oldView1], views)
  }
}

private final class TestBlock: UIViewRenderable {
  static func makeBlockView() -> BlockView { TestView() }

  func isBestViewForReuse(_ view: BlockView) -> Bool {
    (view as? TestView)?.block === self
  }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TestView
  }

  func configureBlockView(
    _ view: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {
    (view as! TestView).block = self
  }

  init() {}
}

private final class TestView: BlockView, VisibleBoundsTrackingLeaf {
  var block: TestBlock?
  var effectiveBackgroundColor: UIColor? { backgroundColor }
}

private final class OtherTestBlock: UIViewRenderable {
  static func makeBlockView() -> BlockView { OtherTestView() }

  func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is OtherTestView
  }

  func configureBlockView(
    _: BlockView,
    observer _: ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: RenderingDelegate?
  ) {}

  init() {}
}

private final class OtherTestView: BlockView, VisibleBoundsTrackingLeaf {
  var effectiveBackgroundColor: UIColor? { backgroundColor }
}
