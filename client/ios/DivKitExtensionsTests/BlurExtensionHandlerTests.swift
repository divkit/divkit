@testable import DivKit
@testable import DivKitExtensions
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct BlurExtensionHandlerTests {
  private let expressionResolver = DivBlockModelingContext.default.expressionResolver
  private let handler = BlurExtensionHandler()

  @Test
  func usesFullIntensityForStyle() throws {
    let params = try makeParams(["style": "regular"])

    #expect(params?.blurEffect == .regular)
    #expect(params?.intensity == 1)
  }

  @Test
  func resolvesNumericIntensityAsRegularBlur() throws {
    let params = try makeParams([
      "intensity": 0.18,
    ])

    #expect(params?.blurEffect == .regular)
    #expect(params?.intensity == 0.18)
  }

  @Test
  func resolvesIntensityExpression() throws {
    let params = try makeParams([
      "intensity": "@{0.25}",
    ])

    #expect(params?.intensity == 0.25)
  }

  @Test
  func clampsIntensityToSupportedRange() throws {
    #expect(try makeParams(["intensity": -1])?.intensity == 0)
    #expect(try makeParams(["intensity": 2])?.intensity == 1)
  }

  @Test
  func disablesBlurForDisabledStyle() throws {
    #expect(try makeParams(["style": "disabled"]) == nil)
  }

  @Test
  func rejectsStyleAndIntensityTogether() {
    #expect(throws: BlurExtensionParamsError.self) {
      try makeParams(["style": "regular", "intensity": 0.5])
    }
  }

  @Test
  func rejectsMissingStyleAndIntensity() {
    #expect(throws: BlurExtensionParamsError.self) {
      try makeParams([:])
    }
  }

  @Test
  func reportsErrorAndLeavesBlockUnchangedWhenStyleAndIntensityAreSpecified() {
    expectHandlerRejection(params: ["style": "regular", "intensity": 0.5])
  }

  @Test
  func reportsErrorAndLeavesBlockUnchangedWhenStyleAndIntensityAreMissing() {
    expectHandlerRejection(params: [:])
  }

  private func makeParams(_ params: [String: Any]) throws -> BlurExtensionParams? {
    try BlurExtensionParams(params: params, expressionResolver: expressionResolver)
  }

  private func expectHandlerRejection(params: [String: Any]) {
    let context = DivBlockModelingContext()
    let block = EmptyBlock.zeroSized
    let div = divContainer(
      extensions: [DivExtension(id: handler.id, params: params)],
      items: []
    ).value

    let result = handler.applyBeforeBaseProperties(
      to: block,
      div: div,
      context: context
    )

    #expect(result === block)
    #expect(context.errorsStorage.errors.map(\.message) == [
      "Failed to resolve blur extension params: "
        + "Exactly one of 'style' or 'intensity' must be specified",
    ])
  }
}
