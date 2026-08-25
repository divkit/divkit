#if os(iOS)
@testable @_spi(Internal) import DivKit
import LayoutKit
import Testing
import VGSL

/// Tests that `DivBlockProvider.update(reasons:)` resets the `DivLayoutProviderHandler` counter
/// only when the batch contains at least one non-variable reason.
///
/// The `SizeProviderBlock` circular-update guard inside `DivLayoutProviderHandler` counts how many
/// times the same variable has been written during one layout cycle.  After the cap is reached the
/// handler blocks further writes.  Calling `resetUpdatedVariables()` starts a fresh cycle —  so the
/// guard must be reset by every update that is **not** purely variable-driven, and must **not** be
/// reset when the only source of the update is `layout_provider` itself (`.variable`).
@MainActor
@Suite(.serialized)
struct DivBlockProviderTests {
  private let cardId: DivCardID = "test_card"
  private let widthVarName = DivVariableName(rawValue: "width_var")

  private let variablesStorage: DivVariablesStorage
  private let components: DivKitComponents
  private let provider: DivBlockProvider

  init() {
    variablesStorage = DivVariablesStorage()
    variablesStorage.set(
      cardId: "test_card",
      variables: ["width_var": .integer(0)]
    )
    components = DivKitComponents(variablesStorage: variablesStorage)
    provider = DivBlockProvider(
      id: DivViewId(cardId: "test_card", additionalId: nil),
      divKitComponents: components,
      onCardSizeChanged: { _, _ in }
    )
    provider.setSource(
      DivViewSource(kind: .divData(makeLayoutProviderCard()), cardId: "test_card"),
      debugParams: DebugParams()
    )
  }

  // MARK: - Mixed-reason update resets the counter

  @Test
  func mixedReasonUpdate_ResetsCounterAndAllowsWrite() {
    // Exhaust the circular-update counter by writing the variable 3 times (the cap).
    exhaustCounter()

    // A mixed-reason batch (non-variable reason present) must reset the counter.
    provider.update(reasons: [
      .state(cardId),
      .variable([cardId: []]),
    ])

    // After the reset the new SizeProviderBlock should be able to write the variable again.
    guard let sizeProviderBlock = findSizeProviderBlock(provider.block) else {
      Issue.record("Expected a SizeProviderBlock in the rendered block tree")
      return
    }

    let valueBefore = widthVarValue()
    sizeProviderBlock.widthUpdater?(valueBefore + 100)
    #expect(widthVarValue() == valueBefore + 100)
  }

  // MARK: - Pure variable-only update does NOT reset the counter

  @Test
  func pureVariableUpdate_DoesNotResetCounter() {
    // Exhaust the circular-update counter.
    exhaustCounter()
    let valueAfterExhaust = widthVarValue()

    // A pure variable-only batch must NOT reset the counter.
    provider.update(reasons: [.variable([cardId: []])])

    // The handler's counter is still exhausted; the new block must not be able to write.
    guard let sizeProviderBlock = findSizeProviderBlock(provider.block) else {
      Issue.record("Expected a SizeProviderBlock in the rendered block tree")
      return
    }

    sizeProviderBlock.widthUpdater?(valueAfterExhaust + 100)
    #expect(widthVarValue() == valueAfterExhaust)
  }

  // MARK: - Helpers

  /// Calls the current block's `widthUpdater` enough times to exhaust the counter cap.
  private func exhaustCounter() {
    guard let sizeProviderBlock = findSizeProviderBlock(provider.block) else {
      Issue.record("Expected a SizeProviderBlock in the rendered block tree")
      return
    }
    // Three successful writes reach the cap; a fourth call is blocked.
    let base = widthVarValue()
    sizeProviderBlock.widthUpdater?(base + 100)
    sizeProviderBlock.widthUpdater?(base + 200)
    sizeProviderBlock.widthUpdater?(base + 300)
    // Verify that the counter is now exhausted: a further write is blocked.
    sizeProviderBlock.widthUpdater?(base + 400)
    #expect(widthVarValue() == base + 300)
  }

  private func widthVarValue() -> Int {
    variablesStorage.getVariableValue(cardId: cardId, name: widthVarName) ?? 0
  }
}

// MARK: - DivData builder

/// A minimal DivData containing a single text element with `layout_provider` for `width_var`.
private func makeLayoutProviderCard() -> DivData {
  let layoutProvider = DivLayoutProvider(widthVariableName: "width_var")
  let div = Div.divText(DivText(
    layoutProvider: layoutProvider,
    text: .value("test")
  ))
  return DivData(
    functions: nil,
    logId: "test_card",
    states: [DivData.State(div: div, stateId: 0)],
    timers: nil,
    transitionAnimationSelector: nil,
    variableTriggers: nil,
    variables: nil
  )
}

// MARK: - Block tree traversal

/// Recursively searches for the first `SizeProviderBlock` in the block tree.
private func findSizeProviderBlock(_ block: Block) -> SizeProviderBlock? {
  if let provider = block as? SizeProviderBlock {
    return provider
  }
  if let wrapper = block as? WrapperBlock {
    return findSizeProviderBlock(wrapper.child)
  }
  return nil
}
#endif
