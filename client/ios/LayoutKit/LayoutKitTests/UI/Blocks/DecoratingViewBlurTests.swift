@testable import LayoutKit
import UIKit
import XCTest

final class DecoratingViewBlurTests: XCTestCase {
  func test_intensityChange_reusesBlurView() throws {
    let initialBlock = makeBlock(blurEffect: .regular, blurIntensity: 0.2)
    let view = initialBlock.makeBlockView()
    let initialBlurView = try XCTUnwrap(view.subviews.first { $0 is UIVisualEffectView })

    makeBlock(blurEffect: .regular, blurIntensity: 0.8).configureBlockView(view)

    let updatedBlurView = try XCTUnwrap(view.subviews.first { $0 is UIVisualEffectView })
    XCTAssertIdentical(updatedBlurView, initialBlurView)
  }

  func test_effectChange_replacesBlurView() throws {
    let initialBlock = makeBlock(blurEffect: .regular, blurIntensity: 0.2)
    let view = initialBlock.makeBlockView()
    let initialBlurView = try XCTUnwrap(view.subviews.first { $0 is UIVisualEffectView })

    makeBlock(blurEffect: .dark, blurIntensity: 0.2).configureBlockView(view)

    let updatedBlurView = try XCTUnwrap(view.subviews.first { $0 is UIVisualEffectView })
    XCTAssertNotIdentical(updatedBlurView, initialBlurView)
  }

  private func makeBlock(blurEffect: BlurEffect, blurIntensity: CGFloat) -> DecoratingBlock {
    DecoratingBlock(
      child: EmptyBlock.zeroSized,
      blurEffect: blurEffect,
      blurIntensity: blurIntensity
    )
  }
}
