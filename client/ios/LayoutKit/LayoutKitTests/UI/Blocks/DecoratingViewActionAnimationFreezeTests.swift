@testable import LayoutKit
import UIKit
import VGSL
import XCTest

final class DecoratingViewActionAnimationFreezeTests: XCTestCase {
  func test_orphanedHighlight_doesNotFreeze() {
    let fade = { (start: Double, end: Double) in
      TransitioningAnimation(
        kind: .fade, start: start, end: end, duration: 0.05, delay: 0, timingFunction: .linear
      )
    }
    let block = DecoratingBlock(
      child: EmptyBlock.zeroSized,
      actionAnimation: ActionAnimation(touchDown: [fade(1, 0)], touchUp: [fade(0, 1)])
    )
    let view = block.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 50, height: 50)
    block.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    let window = UIWindow(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    window.addSubview(view)
    window.makeKeyAndVisible()

    view.touchesBegan([], with: nil)
    view.touchesEnded([], with: nil)
    view.touchesBegan([], with: nil)
    pump(0.08)
    view.touchesEnded([], with: nil)
    pump(0.3)

    XCTAssertEqual(view.alpha, 1, accuracy: 0.01, "action-animation view stuck at alpha \(view.alpha)")
  }

  private func pump(_ seconds: TimeInterval) {
    let end = Date(timeIntervalSinceNow: seconds)
    while Date() < end {
      RunLoop.current.run(mode: .default, before: end)
    }
  }
}
