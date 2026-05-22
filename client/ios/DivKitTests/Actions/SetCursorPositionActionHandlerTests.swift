@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct SetCursorPositionActionHandlerTests {
  private let blockStateStorage = DivBlockStateStorage()
  private var handler: DivActionHandler

  init() {
    handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      updateCard: { _ in }
    )
  }

  // MARK: – State values

  @Test
  func stateHasCorrectStartAndEnd() {
    handleSetCursorPosition(start: 3, end: 7)
    let state = getState()
    #expect(state?.pendingSelection == .init(start: 3, end: 7))
  }

  @Test
  func endOmitted_DefaultsToStart() {
    handleSetCursorPosition(start: 5)
    let state = getState()
    #expect(state?.pendingSelection == .init(start: 5, end: 5))
  }

  @Test
  func startAtBeginning() {
    handleSetCursorPosition(start: 0)
    let state = getState()
    #expect(state?.pendingSelection == .init(start: 0, end: 0))
  }

  @Test
  func negativeOnePassedThrough() {
    handleSetCursorPosition(start: -1, end: -1)
    let state = getState()
    #expect(state?.pendingSelection == .init(start: -1, end: -1))
  }

  @Test
  func selectionRange() {
    handleSetCursorPosition(start: 7, end: 13)
    let state = getState()
    #expect(state?.pendingSelection == .init(start: 7, end: 13))
  }

  // MARK: – Card update

  @Test
  mutating func triggersCardUpdate() {
    var updateCount = 0
    handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      updateCard: { _ in updateCount += 1 }
    )

    handleSetCursorPosition(start: 3, end: 7)

    #expect(updateCount == 1)
  }

  // MARK: – Helpers

  private func handleSetCursorPosition(start: Int, end: Int? = nil) {
    handler.handle(
      divAction(
        typed: .divActionSetCursorPosition(
          DivActionSetCursorPosition(
            id: .value(inputId),
            position: DivActionSetCursorPosition.Position(
              end: end.map { .value($0) },
              start: .value(start)
            )
          )
        )
      ),
      path: cardId.path,
      source: .callback,
      sender: nil
    )
  }

  private func getState() -> TextInputViewState? {
    blockStateStorage.getState(inputId, cardId: cardId)
  }
}

private let cardId = DivBlockModelingContext.testCardId
private let inputId = "input_1"
