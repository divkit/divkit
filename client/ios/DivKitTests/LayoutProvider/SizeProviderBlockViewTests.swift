#if os(iOS)
@testable import DivKit
import LayoutKit
import Testing
import UIKit

@MainActor
struct SizeProviderBlockViewTests {
  @Test
  func hitTest_PassiveContentDoesNotInterceptParentTouches() {
    let view = makeView()
    let parent = UIView(frame: view.frame)
    parent.addSubview(view)

    #expect(view.hitTest(CGPoint(x: 50, y: 50), with: nil) == nil)
    #expect(parent.hitTest(CGPoint(x: 50, y: 50), with: nil) === parent)
  }

  @Test
  func hitTest_PreservesInteractiveDescendants() {
    let view = makeView()
    let child = UIView(frame: CGRect(x: 20, y: 20, width: 60, height: 60))
    let button = UIButton(frame: CGRect(x: 10, y: 10, width: 20, height: 20))
    child.addSubview(button)
    view.addSubview(child)

    #expect(view.hitTest(CGPoint(x: 35, y: 35), with: nil) === button)
    #expect(view.hitTest(CGPoint(x: 70, y: 70), with: nil) === child)
    #expect(view.hitTest(CGPoint(x: 10, y: 10), with: nil) == nil)
  }

  @Test
  func hitTest_PreservesUIKitExclusions() {
    let view = makeView()
    let child = UIButton(frame: view.bounds)
    view.addSubview(child)
    let point = CGPoint(x: 50, y: 50)

    child.isHidden = true
    #expect(view.hitTest(point, with: nil) == nil)
    child.isHidden = false
    child.isUserInteractionEnabled = false
    #expect(view.hitTest(point, with: nil) == nil)
    child.isUserInteractionEnabled = true
    #expect(view.hitTest(point, with: nil) === child)

    view.isHidden = true
    #expect(view.hitTest(point, with: nil) == nil)
    view.isHidden = false
    view.isUserInteractionEnabled = false
    #expect(view.hitTest(point, with: nil) == nil)
    view.isUserInteractionEnabled = true
    view.alpha = 0
    #expect(view.hitTest(point, with: nil) == nil)
    view.alpha = 1
    #expect(view.hitTest(CGPoint(x: 101, y: 50), with: nil) == nil)
  }

  @Test
  func hitTest_PassiveOverlayDoesNotBlockUnderlyingSibling() {
    let view = makeView()
    let parent = UIView(frame: view.frame)
    let button = UIButton(frame: parent.bounds)
    parent.addSubview(button)
    parent.addSubview(view)

    #expect(parent.hitTest(CGPoint(x: 50, y: 50), with: nil) === button)
  }

  @Test
  func layout_StillReportsDimensions() {
    var width: Int?
    var height: Int?
    let block = SizeProviderBlock(
      child: EmptyBlock(),
      widthUpdater: { width = $0 },
      heightUpdater: { height = $0 }
    )
    let view = SizeProviderBlock.makeBlockView()
    block.configureBlockView(
      view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil
    )
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 60)
    view.layoutIfNeeded()

    #expect(width == 100)
    #expect(height == 60)
  }

  private func makeView() -> UIView {
    let block = SizeProviderBlock(
      child: EmptyBlock(), widthUpdater: nil, heightUpdater: nil
    )
    let view = SizeProviderBlock.makeBlockView()
    block.configureBlockView(
      view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil
    )
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    view.layoutIfNeeded()
    return view
  }
}
#endif
