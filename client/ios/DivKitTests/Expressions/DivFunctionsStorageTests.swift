@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct DivFunctionsStorageTests {
  private let hostViewId = DivViewId(cardId: "card")
  private let otherViewId = DivViewId(cardId: "other_card")
  private let tooltipViewId = DivViewId(
    cardId: "card",
    tooltip: DivViewId.Tooltip(id: "hint", anchorPath: UIElementPath("card") + "anchor")
  )

  @Test
  func resetViewId_host_leavesTooltipAndOtherCardUntouched() {
    let storage = makeStorage()

    storage.reset(viewId: hostViewId)

    #expect(!isFound("host_function", in: storage, viewId: hostViewId))
    #expect(isFound("tooltip_function", in: storage, viewId: tooltipViewId))
    #expect(isFound("other_function", in: storage, viewId: otherViewId))
  }

  @Test
  func resetViewId_tooltip_leavesHostAndOtherCardUntouched() {
    let storage = makeStorage()

    storage.reset(viewId: tooltipViewId)

    #expect(isFound("host_function", in: storage, viewId: hostViewId))
    #expect(!isFound("tooltip_function", in: storage, viewId: tooltipViewId))
    #expect(isFound("other_function", in: storage, viewId: otherViewId))
  }

  @Test
  func resetCardId_dropsHostAndItsTooltips_leavesOtherCardUntouched() {
    let storage = makeStorage()

    storage.reset(cardId: "card")

    #expect(!isFound("host_function", in: storage, viewId: hostViewId))
    #expect(!isFound("tooltip_function", in: storage, viewId: tooltipViewId))
    #expect(isFound("other_function", in: storage, viewId: otherViewId))
  }

  @Test
  func resetViewId_host_doesNotClearFunctionNamesOfOtherViewsOfTheCard() {
    let storage = makeStorage()

    storage.reset(viewId: hostViewId)

    // The tooltip's function is still found through the card-wide name pre-filter, which
    // `setIfNeeded` would never refill for a path it already knows.
    #expect(isFound("tooltip_function", in: storage, viewId: tooltipViewId))
  }

  @Test
  func setCardId_replacesHostFunctions_keepsOpenTooltipFunctions() {
    let storage = makeStorage()

    // A new DivData of the host carries a different set of functions.
    storage.set(cardId: "card", functions: [makeFunction(name: "new_host_function")])

    #expect(!isFound("host_function", in: storage, viewId: hostViewId))
    #expect(isFound("new_host_function", in: storage, viewId: hostViewId))
    #expect(isFound("tooltip_function", in: storage, viewId: tooltipViewId))
  }

  private func makeStorage() -> DivFunctionsStorage {
    let storage = DivFunctionsStorage()
    storage.setIfNeeded(
      path: hostViewId.path,
      functions: [makeFunction(name: "host_function")]
    )
    storage.setIfNeeded(
      path: tooltipViewId.path,
      functions: [makeFunction(name: "tooltip_function")]
    )
    storage.setIfNeeded(
      path: otherViewId.path,
      functions: [makeFunction(name: "other_function")]
    )
    return storage
  }

  private func makeFunction(name: String) -> DivFunction {
    DivFunction(
      arguments: [],
      body: "@{1}",
      name: name,
      returnType: .integer
    )
  }

  private func isFound(
    _ name: String,
    in storage: DivFunctionsStorage,
    viewId: DivViewId
  ) -> Bool {
    storage.getStorage(path: viewId.path, contains: name) != nil
  }
}
