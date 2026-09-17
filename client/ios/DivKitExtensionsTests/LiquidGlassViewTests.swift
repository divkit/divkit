@testable import DivKit
@testable import DivKitExtensions
import DivKitTestsSupport
@testable import LayoutKit
import Testing
import UIKit
import VGSL

// `LiquidGlassBlock` is available on iOS 26 and newer, so every test starts with an availability
// guard. Swift Testing does not allow `@available` on `@Suite` and `@Test`.
@MainActor
@Suite
struct LiquidGlassViewTests {
  @Test
  func appliesEffectParamsOnFirstConfigure() {
    guard #available(iOS 26, *) else { return }
    let view = LiquidGlassBlock.makeBlockView()

    configure(view, isInteractive: true, tintColor: color("#FFFF1744"))

    #expect(glassEffect(of: view)?.isInteractive == true)
    #expect(glassEffect(of: view)?.tintColor == color("#FFFF1744").systemColor)
  }

  @Test
  func updatesEffectWhenTintColorChangesOnTheSameView() {
    guard #available(iOS 26, *) else { return }
    let view = LiquidGlassBlock.makeBlockView()

    configure(view, tintColor: color("#FFFF1744"))
    configure(view, tintColor: color("#FF2979FF"))

    #expect(glassEffect(of: view)?.tintColor == color("#FF2979FF").systemColor)
  }

  @Test
  func updatesEffectWhenInteractivityChangesOnTheSameView() {
    guard #available(iOS 26, *) else { return }
    let view = LiquidGlassBlock.makeBlockView()

    configure(view, isInteractive: false)
    configure(view, isInteractive: true)

    #expect(glassEffect(of: view)?.isInteractive == true)
  }

  @Test
  func dropsTintColorWhenItIsRemovedOnTheSameView() {
    guard #available(iOS 26, *) else { return }
    let view = LiquidGlassBlock.makeBlockView()

    configure(view, tintColor: color("#FFFF1744"))
    configure(view, tintColor: nil)

    #expect(glassEffect(of: view)?.tintColor == nil)
  }

  @Test
  func appliesCornerStyleOnTheSameView() {
    guard #available(iOS 26, *) else { return }
    let view = LiquidGlassBlock.makeBlockView()

    configure(view, cornerStyle: .capsule(maximumRadius: 16))

    #expect(cornerConfiguration(of: view) == .capsule(maximumRadius: 16))
  }

  @Test
  func restoresSystemCornersWhenCornerStyleIsRemovedOnTheSameView() {
    guard #available(iOS 26, *) else { return }
    let view = LiquidGlassBlock.makeBlockView()
    let systemConfiguration = cornerConfiguration(of: view)

    configure(view, cornerStyle: .corners(topLeft: 4, topRight: 24, bottomLeft: 24, bottomRight: 4))
    let roundedConfiguration = cornerConfiguration(of: view)
    configure(view, cornerStyle: nil)

    #expect(roundedConfiguration != systemConfiguration)
    #expect(cornerConfiguration(of: view) == systemConfiguration)
  }

  @available(iOS 26, *)
  private func configure(
    _ view: BlockView,
    isInteractive: Bool? = nil,
    tintColor: Color? = nil,
    cornerStyle: LiquidGlassBlock.CornerStyle? = nil
  ) {
    LiquidGlassBlock(
      child: EmptyBlock.zeroSized,
      effectStyle: .regular,
      isInteractive: isInteractive,
      tintColor: tintColor,
      cornerStyle: cornerStyle
    ).configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
  }

  @available(iOS 26, *)
  private func glassEffect(of view: BlockView) -> UIGlassEffect? {
    (view as? UIVisualEffectView)?.effect as? UIGlassEffect
  }

  @available(iOS 26, *)
  private func cornerConfiguration(of view: BlockView) -> UICornerConfiguration? {
    (view as? UIVisualEffectView)?.cornerConfiguration
  }
}
