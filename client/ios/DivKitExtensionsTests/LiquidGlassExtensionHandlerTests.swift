@testable import DivKit
@testable import DivKitExtensions
import DivKitTestsSupport
@testable import LayoutKit
import Testing
import VGSL

// `LiquidGlassBlock` is available on iOS 26 and newer, so every test that touches it starts with an
// availability guard. Swift Testing does not allow `@available` on `@Suite` and `@Test`.
@MainActor
@Suite
struct LiquidGlassExtensionHandlerTests {
  private let handler = LiquidGlassExtensionHandler()
  private let block = EmptyBlock.zeroSized

  @Test
  func wrapsBlockIntoLiquidGlassBlock() {
    guard #available(iOS 26, *) else { return }

    let glassBlock = applyExtension(["style": "regular"]) as? LiquidGlassBlock

    #expect(glassBlock?.effectStyle == .regular)
    #expect(glassBlock?.child === block)
  }

  @Test
  func resolvesStyleExpression() {
    guard #available(iOS 26, *) else { return }
    let context = makeContext(["style": .string("clear")])

    let glassBlock = applyExtension(["style": "@{style}"], context: context) as? LiquidGlassBlock

    #expect(glassBlock?.effectStyle == .clear)
  }

  @Test
  func resolvesInteractivityAndTintColor() {
    guard #available(iOS 26, *) else { return }

    let glassBlock = applyExtension([
      "style": "clear",
      "is_interactive": true,
      "tint_color": "#7FFF0000",
    ]) as? LiquidGlassBlock

    #expect(glassBlock?.isInteractive == true)
    #expect(glassBlock?.tintColor == color("#7FFF0000"))
  }

  @Test
  func resolvesInteractivityAndTintColorExpressions() {
    guard #available(iOS 26, *) else { return }
    let context = makeContext([
      "is_interactive": .bool(true),
      "tint_color": .color(color("#7FFF0000")),
    ])

    let glassBlock = applyExtension([
      "style": "clear",
      "is_interactive": "@{is_interactive}",
      "tint_color": "@{tint_color}",
    ], context: context) as? LiquidGlassBlock

    #expect(glassBlock?.isInteractive == true)
    #expect(glassBlock?.tintColor == color("#7FFF0000"))
  }

  @Test
  func leavesBlockUnchangedWhenDisabled() {
    let context = DivBlockModelingContext()

    let result = applyExtension(
      ["style": "regular", "is_enabled": false],
      context: context
    )

    #expect(result === block)
    #expect(context.errorsStorage.errors.isEmpty)
  }

  @Test
  func resolvesIsEnabledExpression() {
    let context = makeContext(["is_enabled": .bool(false)])

    let result = applyExtension(
      ["style": "regular", "is_enabled": "@{is_enabled}"],
      context: context
    )

    #expect(result === block)
  }

  @Test
  func appliesEffectWhenIsEnabledIsNotSpecified() {
    guard #available(iOS 26, *) else { return }

    #expect(applyExtension(["style": "regular"]) is LiquidGlassBlock)
  }

  @Test
  func resolvesCapsuleCornerStyle() {
    guard #available(iOS 26, *) else { return }

    let glassBlock = applyExtension([
      "style": "regular",
      "corner_style": [
        "type": "capsule",
        "max_radius": 16,
      ],
    ]) as? LiquidGlassBlock

    #expect(glassBlock?.cornerStyle == .capsule(maximumRadius: 16))
  }

  @Test
  func resolvesCapsuleCornerStyleWithoutMaximumRadius() {
    guard #available(iOS 26, *) else { return }

    let glassBlock = applyExtension([
      "style": "regular",
      "corner_style": ["type": "capsule"],
    ]) as? LiquidGlassBlock

    #expect(glassBlock?.cornerStyle == .capsule(maximumRadius: nil))
  }

  @Test
  func resolvesLegacyMaximumRadiusKey() {
    guard #available(iOS 26, *) else { return }

    let glassBlock = applyExtension([
      "style": "regular",
      "corner_style": [
        "type": "capsule",
        "maxRadius": 16,
      ],
    ]) as? LiquidGlassBlock

    #expect(glassBlock?.cornerStyle == .capsule(maximumRadius: 16))
  }

  @Test
  func resolvesCornersCornerStyle() {
    guard #available(iOS 26, *) else { return }
    let context = makeContext(["bottom_right": .integer(4)])

    let glassBlock = applyExtension([
      "style": "regular",
      "corner_style": [
        "type": "corners",
        "top_left": 1,
        "top_right": 2.5,
        "bottom_right": "@{bottom_right}",
      ],
    ], context: context) as? LiquidGlassBlock

    #expect(glassBlock?.cornerStyle == .corners(
      topLeft: 1,
      topRight: 2.5,
      bottomLeft: nil,
      bottomRight: 4
    ))
  }

  @Test
  func writesResolvedInterfaceStyleIntoVariable() {
    guard #available(iOS 26, *) else { return }
    let variableStorage = DivVariableStorage()
    variableStorage.put(name: "ui_style", value: .string("light"))
    let context = DivBlockModelingContext(variableStorage: variableStorage)

    let glassBlock = applyExtension([
      "style": "regular",
      "ui_style_variable": "ui_style",
    ], context: context) as? LiquidGlassBlock
    glassBlock?.uiStyleUpdater?(.dark)

    let uiStyle: String? = variableStorage.getValue("ui_style")
    #expect(glassBlock?.uiStyleVariableName == "ui_style")
    #expect(uiStyle == "dark")
  }

  @Test
  func doesNotMakeUpdaterWithoutVariableName() {
    guard #available(iOS 26, *) else { return }

    let glassBlock = applyExtension(["style": "regular"]) as? LiquidGlassBlock

    #expect(glassBlock?.uiStyleUpdater == nil)
  }

  @Test
  func reportsErrorForMissingStyle() {
    expectHandlerRejection(
      params: [:],
      message: "Failed to resolve liquid glass extension params: "
        + "'style' must resolve to 'regular' or 'clear'"
    )
  }

  @Test
  func reportsErrorForInvalidStyle() {
    expectHandlerRejection(
      params: ["style": "frosted"],
      message: "Failed to resolve liquid glass extension params: "
        + "'style' must resolve to 'regular' or 'clear'"
    )
  }

  @Test
  func reportsErrorForInvalidCornerStyleType() {
    expectHandlerRejection(
      params: [
        "style": "regular",
        "corner_style": ["type": "rounded"],
      ],
      message: "Failed to resolve liquid glass extension params: "
        + "'corner_style.type' must resolve to 'capsule' or 'corners'"
    )
  }

  private func applyExtension(
    _ params: [String: Any],
    context: DivBlockModelingContext = DivBlockModelingContext()
  ) -> Block {
    handler.applyAfterBaseProperties(
      to: block,
      div: divContainer(
        extensions: [DivExtension(id: handler.id, params: params)],
        items: []
      ).value,
      context: context
    )
  }

  private func makeContext(_ variables: DivVariables) -> DivBlockModelingContext {
    let variableStorage = DivVariableStorage()
    variableStorage.replaceAll(variables)
    return DivBlockModelingContext(variableStorage: variableStorage)
  }

  private func expectHandlerRejection(params: [String: Any], message: String) {
    guard #available(iOS 26, *) else { return }
    let context = DivBlockModelingContext()

    let result = applyExtension(params, context: context)

    #expect(result === block)
    #expect(context.errorsStorage.errors.map(\.message) == [message])
  }
}
