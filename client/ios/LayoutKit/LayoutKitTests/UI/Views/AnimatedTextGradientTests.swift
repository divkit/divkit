import CoreText
@testable import LayoutKit
import UIKit
import VGSL
import XCTest

@MainActor
final class AnimatedTextGradientTests: XCTestCase {
  private var window: UIWindow!

  private var sampleText: NSAttributedString {
    NSAttributedString(string: "Gradient", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      .foregroundColor: UIColor.green,
      .animatedTextGradient: true,
    ])
  }

  override func setUp() {
    super.setUp()
    window = UIWindow(frame: CGRect(x: 0, y: 0, width: 300, height: 300))
  }

  override func tearDown() {
    window = nil
    super.tearDown()
  }

  func test_LinearHorizontal_MiddlePhaseMatchesStaticGradient() {
    let (start, end) = AnimatedTextGradientGeometry.linearPoints(
      direction: .angle(0),
      bounds: CGRect(x: 10, y: 20, width: 100, height: 40),
      phase: 0.5
    )

    XCTAssertEqual(start, CGPoint(x: 10, y: 40))
    XCTAssertEqual(end, CGPoint(x: 110, y: 40))
  }

  func test_LinearVertical_StartPhaseMovesDownByTextHeight() {
    let (start, end) = AnimatedTextGradientGeometry.linearPoints(
      direction: .angle(90),
      bounds: CGRect(x: 0, y: 0, width: 100, height: 40),
      phase: 0
    )

    XCTAssertEqual(start.y, 80, accuracy: 0.001)
    XCTAssertEqual(end.y, 40, accuracy: 0.001)
  }

  func test_LinearDiagonal_UsesProjectionOfBothDimensions() {
    let (start, end) = AnimatedTextGradientGeometry.linearPoints(
      direction: .angle(45),
      bounds: CGRect(x: 0, y: 0, width: 100, height: 40),
      phase: 0.5
    )

    XCTAssertEqual(start.x, 15, accuracy: 0.001)
    XCTAssertEqual(start.y, 55, accuracy: 0.001)
    XCTAssertEqual(end.x, 85, accuracy: 0.001)
  }

  func test_RadialOffCenter_FarthestCornerRadiusUsesTextBounds() {
    let radial = Gradient.Radial(
      centerX: .relative(0.25),
      centerY: .absolute(10),
      end: .relativeToBorders(.farthestCorner),
      centerColor: .white
    )
    let bounds = CGRect(x: 10, y: 20, width: 100, height: 40)

    let center = AnimatedTextGradientGeometry.radialCenter(radial, bounds: bounds)

    XCTAssertEqual(center, CGPoint(x: 35, y: 30))
    XCTAssertEqual(
      AnimatedTextGradientGeometry.radialRadius(radial, bounds: bounds, center: center),
      hypot(75, 30),
      accuracy: 0.001
    )
  }

  func test_Layout_InstallsLinearAnimation() throws {
    let view = makeGradientView()
    window.addSubview(view)

    view.layoutIfNeeded()

    let animations = try gradientAnimations(in: view)
    XCTAssertEqual(animations.group.duration, 1.6)
    XCTAssertEqual(animations.start.duration, 1.6)
    XCTAssertEqual(animations.end.duration, 1.6)
    XCTAssertEqual(animations.group.repeatCount, .infinity)
    XCTAssertFalse(animations.group.autoreverses)
    XCTAssertFalse(animations.group.isRemovedOnCompletion)
    XCTAssertEqual(point(from: animations.start.fromValue), CGPoint(x: -2, y: 0.5))
    XCTAssertEqual(point(from: animations.start.toValue), CGPoint(x: 0, y: 0.5))
    XCTAssertEqual(point(from: animations.end.fromValue), CGPoint(x: 1, y: 0.5))
    XCTAssertEqual(point(from: animations.end.toValue), CGPoint(x: 3, y: 0.5))
  }

  func test_ViewsCreatedInDifferentCycles_KeepTheSamePhase() throws {
    let duration = 1.6
    var now = CACurrentMediaTime()
    let firstView = makeGradientView(clock: { now })
    window.addSubview(firstView)
    firstView.layoutIfNeeded()
    let firstBeginTime = try gradientAnimations(in: firstView).group.beginTime

    now += duration + 0.1
    let secondView = makeGradientView(clock: { now })
    window.addSubview(secondView)
    secondView.layoutIfNeeded()
    let secondBeginTime = try gradientAnimations(in: secondView).group.beginTime

    let cycleDifference = secondBeginTime - firstBeginTime
    XCTAssertGreaterThan(cycleDifference, 0)
    XCTAssertEqual(
      cycleDifference / duration,
      (cycleDifference / duration).rounded(),
      accuracy: 0.000_001
    )
  }

  func test_IdenticalReconfiguration_KeepsInstalledAnimation() throws {
    var now = CACurrentMediaTime()
    let view = makeGradientView(clock: { now })
    window.addSubview(view)
    view.layoutIfNeeded()
    let initialBeginTime = try gradientAnimations(in: view).group.beginTime

    now += 3.2
    view.model = gradientModel()

    XCTAssertEqual(try gradientAnimations(in: view).group.beginTime, initialBeginTime)
  }

  func test_ModelChange_UpdatesRadialGradientAndDuration() throws {
    let view = makeGradientView()
    window.addSubview(view)
    view.layoutIfNeeded()
    let radial = Gradient.Radial(
      centerX: .relative(0.5),
      centerY: .relative(0.5),
      end: .relativeToBorders(.farthestCorner),
      centerColor: .red,
      intermediatePoints: [(.green, 0.5)],
      outerColor: .blue
    )

    view.model = TextBlock.GradientModel(
      gradient: .radial(radial),
      rangedTextWithColor: sampleText,
      animation: .init(duration: 800)
    )

    let layer = view.layer as! CAGradientLayer
    XCTAssertEqual(layer.type, .radial)
    assertColors(
      layer.colors,
      equalTo: [UIColor.red.cgColor, UIColor.green.cgColor, UIColor.blue.cgColor]
    )
    XCTAssertEqual(layer.locations, [0, 0.5, 1])
    XCTAssertEqual(try gradientAnimations(in: view).group.duration, 0.8)
  }

  func test_DetachingFromWindow_StopsAnimationAtMiddlePhase() {
    let view = makeGradientView()
    window.addSubview(view)
    view.layoutIfNeeded()

    view.removeFromSuperview()

    assertAnimationStoppedAtMiddlePhase(view)
  }

  func test_InvisibleBounds_StopAnimationAtMiddlePhase() {
    let view = makeGradientView()
    window.addSubview(view)
    view.layoutIfNeeded()

    view.isWithinVisibleBounds = false

    assertAnimationStoppedAtMiddlePhase(view)
  }

  func test_BackgroundLifecycle_ReinstallsPhaseLockedAnimation() throws {
    let center = NotificationCenter()
    let view = makeGradientView(notificationCenter: center)
    window.addSubview(view)
    view.layoutIfNeeded()

    center.post(name: UIApplication.didEnterBackgroundNotification, object: nil)

    XCTAssertFalse(view.isAnimationRunning)
    XCTAssertNil(view.layer.animation(forKey: AnimatedTextGradientView.animationKey))

    center.post(name: UIApplication.didBecomeActiveNotification, object: nil)

    XCTAssertTrue(view.isAnimationRunning)
    _ = try gradientAnimations(in: view)
  }

  func test_ZeroDuration_UsesStaticMiddlePhase() {
    let view = makeGradientView(duration: 0)
    window.addSubview(view)

    view.layoutIfNeeded()

    assertAnimationStoppedAtMiddlePhase(view)
  }

  func test_EmptyBounds_StopAnimation() {
    let view = makeGradientView()
    window.addSubview(view)
    view.layoutIfNeeded()

    view.bounds = .zero
    view.layoutIfNeeded()

    XCTAssertFalse(view.isAnimationRunning)
    XCTAssertNil(view.layer.animation(forKey: AnimatedTextGradientView.animationKey))
  }

  func test_ReusingAnimatedTextView_ForPlainTextRestoresOrdinaryRendering() {
    let view = makeTextBlockView()
    _ = snapshot(view)
    let plain = TextBlock(widthTrait: .fixed(200), text: sampleText)

    plain.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    let fresh = plain.makeBlockView()
    fresh.frame = view.frame
    XCTAssertEqual(snapshot(view).pngData(), snapshot(fresh).pngData())
    XCTAssertNil(gradientView(in: view))
  }

  func test_ReusingTextView_AfterMissingGradientRestoresAnimatedMask() {
    // Arrange
    let reused = makeTextBlockView()
    _ = snapshot(reused)
    let plain = TextBlock(widthTrait: .fixed(200), text: sampleText)
    plain.configureBlockView(
      reused,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    let animated = TextBlock(
      widthTrait: .fixed(200),
      text: sampleText,
      gradientModel: gradientModel()
    )

    // Act
    animated.configureBlockView(
      reused,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    let fresh = makeTextBlockView()
    fresh.frame = reused.frame

    // Assert
    XCTAssertEqual(snapshot(reused).pngData(), snapshot(fresh).pngData())
    XCTAssertNotNil(gradientView(in: reused))
  }

  func test_ChangingContentScale_RebuildsAnimatedTextRaster() throws {
    // Arrange
    let view = makeTextBlockView()
    let textView = try XCTUnwrap(textBlockView(in: view))
    textView.contentScaleFactor = 1
    textView.setNeedsLayout()
    _ = snapshot(view)
    XCTAssertEqual(textView.animatedTextImage?.scale, 1)

    // Act
    textView.contentScaleFactor = 2
    textView.setNeedsLayout()
    _ = snapshot(view)

    // Assert
    XCTAssertEqual(textView.animatedTextImage?.scale, 2)
  }

  func test_AnimatedText_PreservesAccessibilityLabel() {
    XCTAssertEqual(makeTextBlockView().accessibilityLabel, sampleText.string)
  }

  func test_AnimatedUniformFill_PreservesMixedTextPixels() {
    let text = NSMutableAttributedString(string: "First GREEN last\nשלום world", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      .foregroundColor: UIColor.white,
      .animatedTextGradient: true,
    ])
    text.addAttribute(
      .foregroundColor,
      value: UIColor.green,
      range: NSRange(location: 6, length: 5)
    )
    text.removeAttribute(.animatedTextGradient, range: NSRange(location: 6, length: 5))
    let gradient = TextBlock.GradientModel(
      gradient: .linear(.init(startColor: .white, endColor: .white, direction: .angle(0))),
      rangedTextWithColor: text,
      animation: .init()
    )
    let animated = TextBlock(widthTrait: .fixed(250), text: text, gradientModel: gradient)
      .makeBlockView()
    let regular = TextBlock(widthTrait: .fixed(250), text: text).makeBlockView()
    animated.frame = CGRect(x: 0, y: 0, width: 250, height: 100)
    regular.frame = animated.frame

    XCTAssertEqual(snapshot(animated).pngData(), snapshot(regular).pngData())
  }

  func test_AnimatedUniformFill_PreservesSelectionPixels() throws {
    let text = NSAttributedString(string: "Select this text", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      .foregroundColor: UIColor.white,
      .animatedTextGradient: true,
    ])
    let gradient = TextBlock.GradientModel(
      gradient: .linear(.init(startColor: .white, endColor: .white, direction: .angle(0))),
      rangedTextWithColor: text,
      animation: .init()
    )
    let animated = TextBlock(
      widthTrait: .fixed(250),
      text: text,
      gradientModel: gradient,
      canSelect: true
    ).makeBlockView()
    let regular = TextBlock(widthTrait: .fixed(250), text: text, canSelect: true).makeBlockView()
    animated.frame = CGRect(x: 0, y: 0, width: 250, height: 60)
    regular.frame = animated.frame
    _ = snapshot(animated)
    _ = snapshot(regular)

    let point = CGPoint(x: 20, y: 30)
    let animatedTextView = try XCTUnwrap(textBlockView(in: animated))
    let regularTextView = try XCTUnwrap(textBlockView(in: regular))
    animatedTextView.handleSelectionTapBegan(FixedLocationGestureRecognizer(point: point))
    regularTextView.handleSelectionTapBegan(FixedLocationGestureRecognizer(point: point))

    XCTAssertEqual(animatedTextView.selection?.range, regularTextView.selection?.range)
    assertImagesEqual(snapshot(animated), snapshot(regular), accuracy: 1)
  }

  func test_AnimatedSelectionPan_UpdatesOverlayWithSelectionRange() throws {
    // Arrange
    let text = NSAttributedString(string: "Select this text", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      .foregroundColor: UIColor.white,
      .animatedTextGradient: true,
    ])
    let gradient = TextBlock.GradientModel(
      gradient: .linear(.init(startColor: .white, endColor: .white, direction: .angle(0))),
      rangedTextWithColor: text,
      animation: .init()
    )
    let animated = TextBlock(
      widthTrait: .fixed(250),
      text: text,
      gradientModel: gradient,
      canSelect: true
    ).makeBlockView()
    let regular = TextBlock(widthTrait: .fixed(250), text: text, canSelect: true).makeBlockView()
    animated.frame = CGRect(x: 0, y: 0, width: 250, height: 60)
    regular.frame = animated.frame
    _ = snapshot(animated)
    _ = snapshot(regular)

    let selectionPoint = CGPoint(x: 20, y: 30)
    let trailingHandlePoint = CGPoint(x: 65, y: 30)
    let extendedSelectionPoint = CGPoint(x: 145, y: 30)
    let animatedTextView = try XCTUnwrap(textBlockView(in: animated))
    let regularTextView = try XCTUnwrap(textBlockView(in: regular))
    animatedTextView.handleSelectionTapBegan(FixedLocationGestureRecognizer(point: selectionPoint))
    regularTextView.handleSelectionTapBegan(FixedLocationGestureRecognizer(point: selectionPoint))
    let initialRange = animatedTextView.selection?.range

    // Act
    for textView in [animatedTextView, regularTextView] {
      textView.updateSelection(forPanAt: trailingHandlePoint, state: .began)
      textView.updateSelection(forPanAt: extendedSelectionPoint, state: .changed)
    }

    // Assert
    XCTAssertNotEqual(animatedTextView.selection?.range, initialRange)
    XCTAssertEqual(animatedTextView.selection?.range, regularTextView.selection?.range)
    assertImagesEqual(snapshot(animated), snapshot(regular), accuracy: 1)
  }

  func test_VGSLDeclinedGlyphRenderer_PreservesOriginalPixelsAndLayout() {
    let text = NSAttributedString(string: "Hello שלום 👋", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      .foregroundColor: UIColor.red,
      .underlineStyle: NSUnderlineStyle.single.rawValue,
    ])
    let regular = renderText(text, glyphRenderer: nil)
    let intercepted = renderText(
      text,
      glyphRenderer: TestGlyphRenderer { _, _, _ in false }
    )

    XCTAssertEqual(regular.pngData(), intercepted.pngData())
  }

  func test_VGSLGlyphRenderer_RendersShapedTextAtOriginalPosition() {
    let text = NSAttributedString(string: "Hello שלום", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      .foregroundColor: UIColor.white,
    ])
    let regular = renderText(text, glyphRenderer: nil)
    let intercepted = renderText(
      text,
      glyphRenderer: TestGlyphRenderer { run, position, context in
        context.setFillColor(UIColor.white.cgColor)
        run.drawGlyphs(at: position, in: context)
        return true
      }
    )

    XCTAssertEqual(regular.pngData(), intercepted.pngData())
  }

  func test_VGSLGlyphRenderer_DrawingStateDoesNotLeakIntoUnhandledRun() {
    let handledKey = NSAttributedString.Key("handledGlyphRun")
    let text = NSMutableAttributedString(string: "AB", attributes: [
      .font: UIFont.systemFont(ofSize: 36),
      .foregroundColor: UIColor.green,
    ])
    text.addAttribute(handledKey, value: true, range: NSRange(location: 0, length: 1))
    let drawHandledRun: GlyphDrawing = { run, position, context in
      guard (CTRunGetAttributes(run) as NSDictionary)[handledKey] as? Bool == true else {
        return false
      }
      context.setTextDrawingMode(.stroke)
      context.setStrokeColor(UIColor.magenta.cgColor)
      context.setLineWidth(3)
      run.drawGlyphs(at: position, in: context)
      return true
    }
    let selfIsolated = renderText(
      text,
      glyphRenderer: TestGlyphRenderer { run, position, context in
        context.saveGState()
        let handled = drawHandledRun(run, position, context)
        context.restoreGState()
        return handled
      }
    )

    let isolatedByAPI = renderText(text, glyphRenderer: TestGlyphRenderer(drawHandledRun))

    XCTAssertEqual(isolatedByAPI.pngData(), selfIsolated.pngData())
  }

  func test_GlyphReplacement_PreservesRangeAndEllipsisActionBounds() {
    let key = NSAttributedString.Key("testAction")
    let text = NSAttributedString(string: "Tap this long text that must be truncated", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      key: "range",
    ])
    let token = NSAttributedString(string: "more", attributes: [
      .font: UIFont.systemFont(ofSize: 24),
      key: "ellipsis",
    ])
    let bounds = CGRect(x: 0, y: 0, width: 180, height: 30)
    let original = layoutText(
      text,
      truncationToken: token,
      key: key,
      bounds: bounds,
      glyphRenderer: nil
    )
    let replaced = layoutText(
      text,
      truncationToken: token,
      key: key,
      bounds: bounds,
      glyphRenderer: TestGlyphRenderer { _, _, _ in true }
    )

    XCTAssertEqual(Set(replaced.runsWithAction.map(\.action)), ["range", "ellipsis"])
    XCTAssertEqual(original.runsWithAction.map(\.rect), replaced.runsWithAction.map(\.rect))
    XCTAssertEqual(original.runsWithAction.map(\.action), replaced.runsWithAction.map(\.action))
  }

  private func makeGradientView(
    duration: Int = 1600,
    clock: @escaping () -> TimeInterval = CACurrentMediaTime,
    notificationCenter: NotificationCenter = NotificationCenter()
  ) -> AnimatedTextGradientView {
    let view = AnimatedTextGradientView(
      model: gradientModel(duration: duration),
      clock: clock,
      notificationCenter: notificationCenter
    )
    view.frame = CGRect(x: 0, y: 0, width: 100, height: 40)
    let mask = UIGraphicsImageRenderer(size: view.bounds.size).image { context in
      UIColor.white.setFill()
      context.fill(view.bounds)
    }
    view.setGlyphMask(mask)
    return view
  }

  private func makeTextBlockView() -> BlockView {
    let block = TextBlock(widthTrait: .fixed(200), text: sampleText, gradientModel: gradientModel())
    let view = block.makeBlockView()
    view.frame = CGRect(x: 0, y: 0, width: 200, height: 60)
    return view
  }

  private func gradientView(in view: UIView) -> AnimatedTextGradientView? {
    (view as? AnimatedTextGradientView) ?? view.subviews.compactMap { gradientView(in: $0) }.first
  }

  private func textBlockView(in view: UIView) -> TextBlockView? {
    (view as? TextBlockView) ?? view.subviews.compactMap { textBlockView(in: $0) }.first
  }

  private func gradientModel(duration: Int = 1600) -> TextBlock.GradientModel {
    TextBlock.GradientModel(
      gradient: .linear(.init(startColor: .red, endColor: .blue, direction: .angle(0))),
      rangedTextWithColor: sampleText,
      animation: .init(duration: duration)
    )
  }

  private func gradientAnimations(
    in view: AnimatedTextGradientView,
    file: StaticString = #filePath,
    line: UInt = #line
  ) throws -> GradientAnimations {
    let group = try XCTUnwrap(
      view.layer.animation(forKey: AnimatedTextGradientView.animationKey) as? CAAnimationGroup,
      file: file,
      line: line
    )
    let animations = try XCTUnwrap(group.animations, file: file, line: line)
    let start = try XCTUnwrap(
      animations.first {
        ($0 as? CABasicAnimation)?.keyPath == #keyPath(CAGradientLayer.startPoint)
      } as? CABasicAnimation,
      file: file,
      line: line
    )
    let end = try XCTUnwrap(
      animations.first {
        ($0 as? CABasicAnimation)?.keyPath == #keyPath(CAGradientLayer.endPoint)
      } as? CABasicAnimation,
      file: file,
      line: line
    )
    return GradientAnimations(group: group, start: start, end: end)
  }

  private func point(from value: Any?) -> CGPoint? {
    (value as? NSValue)?.cgPointValue
  }

  private func assertAnimationStoppedAtMiddlePhase(
    _ view: AnimatedTextGradientView,
    file: StaticString = #filePath,
    line: UInt = #line
  ) {
    let layer = view.layer as! CAGradientLayer
    XCTAssertFalse(view.isAnimationRunning, file: file, line: line)
    XCTAssertNil(
      layer.animation(forKey: AnimatedTextGradientView.animationKey),
      file: file,
      line: line
    )
    XCTAssertEqual(layer.startPoint, CGPoint(x: -1, y: 0.5), file: file, line: line)
    XCTAssertEqual(layer.endPoint, CGPoint(x: 2, y: 0.5), file: file, line: line)
  }

  private func snapshot(_ view: UIView) -> UIImage {
    view.layoutIfNeeded()
    view.layer.displayIfNeeded()
    return UIGraphicsImageRenderer(size: view.bounds.size).image { context in
      view.layer.render(in: context.cgContext)
    }
  }

  private func renderText(
    _ text: NSAttributedString,
    glyphRenderer: GlyphRenderer?
  ) -> UIImage {
    let bounds = CGRect(x: 0, y: 0, width: 200, height: 60)
    return UIGraphicsImageRenderer(size: bounds.size).image { context in
      let _: AttributedStringLayout<Void> = text.drawAndGetLayout(
        inContext: context.cgContext,
        rect: bounds,
        glyphRenderer: glyphRenderer
      )
    }
  }

  private func layoutText(
    _ text: NSAttributedString,
    truncationToken: NSAttributedString,
    key: NSAttributedString.Key,
    bounds: CGRect,
    glyphRenderer: GlyphRenderer?
  ) -> AttributedStringLayout<String> {
    var result: AttributedStringLayout<String>!
    _ = UIGraphicsImageRenderer(size: bounds.size).image { context in
      result = text.drawAndGetLayout(
        inContext: context.cgContext,
        rect: bounds,
        truncationToken: truncationToken,
        actionKey: key,
        glyphRenderer: glyphRenderer
      )
    }
    return result
  }

  private func assertColors(
    _ actual: [Any]?,
    equalTo expected: [CGColor],
    file: StaticString = #filePath,
    line: UInt = #line
  ) {
    let actual = (actual ?? []).map { $0 as! CGColor }
    XCTAssertEqual(actual.count, expected.count, file: file, line: line)
    for (actualColor, expectedColor) in zip(actual, expected) {
      let actualComponents = deviceRGBComponents(of: actualColor)
      let expectedComponents = deviceRGBComponents(of: expectedColor)
      XCTAssertEqual(actualComponents.count, expectedComponents.count, file: file, line: line)
      for (actualComponent, expectedComponent) in zip(actualComponents, expectedComponents) {
        XCTAssertEqual(
          actualComponent,
          expectedComponent,
          accuracy: 0.000_001,
          file: file,
          line: line
        )
      }
    }
  }

  private func deviceRGBComponents(of color: CGColor) -> [CGFloat] {
    let converted = color.converted(
      to: CGColorSpaceCreateDeviceRGB(),
      intent: .defaultIntent,
      options: nil
    )
    return converted?.components ?? []
  }

  private func assertImagesEqual(_ lhs: UIImage, _ rhs: UIImage, accuracy: Int) {
    let lhsBytes = rgbaBytes(lhs)
    let rhsBytes = rgbaBytes(rhs)
    XCTAssertEqual(lhsBytes.count, rhsBytes.count)
    let maxDifference = zip(lhsBytes, rhsBytes).reduce(0) {
      max($0, abs(Int($1.0) - Int($1.1)))
    }
    XCTAssertLessThanOrEqual(maxDifference, accuracy)
  }

  private func rgbaBytes(_ image: UIImage) -> [UInt8] {
    let cgImage = image.cgImage!
    let bytesPerRow = cgImage.width * 4
    var bytes = [UInt8](repeating: 0, times: UInt(bytesPerRow * cgImage.height))
    bytes.withUnsafeMutableBytes { buffer in
      let context = CGContext(
        data: buffer.baseAddress,
        width: cgImage.width,
        height: cgImage.height,
        bitsPerComponent: 8,
        bytesPerRow: bytesPerRow,
        space: CGColorSpaceCreateDeviceRGB(),
        bitmapInfo: CGBitmapInfo.byteOrder32Big.rawValue
          | CGImageAlphaInfo.premultipliedLast.rawValue
      )!
      context.draw(cgImage, in: CGRect(x: 0, y: 0, width: cgImage.width, height: cgImage.height))
    }
    return bytes
  }
}

private struct GradientAnimations {
  let group: CAAnimationGroup
  let start: CABasicAnimation
  let end: CABasicAnimation
}

private typealias GlyphDrawing = (CTRun, CGPoint, CGContext) -> Bool

private final class TestGlyphRenderer: GlyphRenderer {
  private let drawing: GlyphDrawing

  init(_ drawing: @escaping GlyphDrawing) {
    self.drawing = drawing
  }

  func drawGlyphs(of run: CTRun, at lineOrigin: CGPoint, in context: CGContext) -> Bool {
    drawing(run, lineOrigin, context)
  }
}

private final class FixedLocationGestureRecognizer: UIGestureRecognizer {
  private let point: CGPoint

  init(point: CGPoint) {
    self.point = point
    super.init(target: nil, action: nil)
  }

  override func location(in _: UIView?) -> CGPoint {
    point
  }
}
