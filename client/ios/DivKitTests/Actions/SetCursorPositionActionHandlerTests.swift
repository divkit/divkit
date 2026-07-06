@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct SetCursorPositionActionHandlerTests {
  private let blockStateStorage = DivBlockStateStorage()
  private let idToPath = IdToPath()
  private var handler: DivActionHandler

  private var inputPath: UIElementPath {
    cardId.path + inputId
  }

  init() {
    idToPath.add(cardId.path + inputId, forId: cardId.path + inputId)
    handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath,
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
      idToPath: idToPath,
      updateCard: { _ in updateCount += 1 }
    )

    handleSetCursorPosition(start: 3, end: 7)

    #expect(updateCount == 1)
  }

  // MARK: – Scope id

  @Test
  func setCursorPosition_withScopeId_firstScope_setsCursorOnFirstInput() {
    let layout = makeScopedSetCursorPositionLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "first",
        typed: .divActionSetCursorPosition(
          DivActionSetCursorPosition(
            id: .value("input"),
            position: DivActionSetCursorPosition.Position(
              end: .value(7),
              start: .value(3)
            )
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError == nil)
    #expect(layout.blockStateStorage.isFocused(path: layout.firstInputPath))
    #expect(!layout.blockStateStorage.isFocused(path: layout.secondInputPath))
  }

  @Test
  func setCursorPosition_withScopeId_secondScope_setsCursorOnSecondInput() {
    let layout = makeScopedSetCursorPositionLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "second",
        typed: .divActionSetCursorPosition(
          DivActionSetCursorPosition(
            id: .value("input"),
            position: DivActionSetCursorPosition.Position(
              end: .value(7),
              start: .value(3)
            )
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError == nil)
    #expect(layout.blockStateStorage.isFocused(path: layout.secondInputPath))
    #expect(!layout.blockStateStorage.isFocused(path: layout.firstInputPath))
  }

  @Test
  func setCursorPosition_withoutScopeId_ambiguousId_reportsErrorAndDoesNotSetCursor() {
    let layout = makeScopedSetCursorPositionLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionSetCursorPosition(
          DivActionSetCursorPosition(
            id: .value("input"),
            position: DivActionSetCursorPosition.Position(
              end: .value(7),
              start: .value(3)
            )
          )
        )
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError != nil)
    #expect(!layout.blockStateStorage.isFocused(path: layout.firstInputPath))
    #expect(!layout.blockStateStorage.isFocused(path: layout.secondInputPath))
  }

  // MARK: – Helpers

  private func makeScopedSetCursorPositionLayout() -> ScopedSetCursorPositionLayout {
    let blockStateStorage = DivBlockStateStorage()
    let idToPath = IdToPath()
    let firstScopePath = cardId.path + "container" + "0" + "first"
    let secondScopePath = cardId.path + "container" + "1" + "second"
    let firstInputPath = firstScopePath + "0" + "input"
    let secondInputPath = secondScopePath + "0" + "input"

    idToPath.add(firstScopePath, forId: cardId.path + "first")
    idToPath.add(secondScopePath, forId: cardId.path + "second")
    idToPath.add(firstInputPath, forId: cardId.path + "input")
    idToPath.add(secondInputPath, forId: cardId.path + "input")

    let reporter = MockReporter()
    let handler = DivActionHandler(
      blockStateStorage: blockStateStorage,
      idToPath: idToPath,
      reporter: reporter
    )

    return ScopedSetCursorPositionLayout(
      blockStateStorage: blockStateStorage,
      handler: handler,
      reporter: reporter,
      firstInputPath: firstInputPath,
      secondInputPath: secondInputPath
    )
  }

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
    blockStateStorage.getState(inputPath)
  }
}

private let cardId = DivBlockModelingContext.testCardId
private let inputId = "input_1"

private struct ScopedSetCursorPositionLayout {
  let blockStateStorage: DivBlockStateStorage
  let handler: DivActionHandler
  let reporter: MockReporter
  let firstInputPath: UIElementPath
  let secondInputPath: UIElementPath
}
