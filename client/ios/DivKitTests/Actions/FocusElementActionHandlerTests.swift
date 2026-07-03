@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct FocusElementActionHandlerTests {
  @Test
  func focusElement_withScopeId_firstScope_setsFocusOnFirstInput() {
    let layout = makeScopedFocusLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "first",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
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
  func focusElement_withScopeId_secondScope_setsFocusOnSecondInput() {
    let layout = makeScopedFocusLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "second",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
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
  func focusElement_withoutScopeId_ambiguousId_reportsErrorAndDoesNotFocus() {
    let layout = makeScopedFocusLayout()

    layout.handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionFocusElement(DivActionFocusElement(elementId: .value("input")))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError != nil)
    #expect(!layout.blockStateStorage.isFocused(path: layout.firstInputPath))
    #expect(!layout.blockStateStorage.isFocused(path: layout.secondInputPath))
  }

  private func makeScopedFocusLayout() -> ScopedFocusLayout {
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

    return ScopedFocusLayout(
      blockStateStorage: blockStateStorage,
      handler: handler,
      reporter: reporter,
      firstInputPath: firstInputPath,
      secondInputPath: secondInputPath
    )
  }
}

private let cardId = DivBlockModelingContext.testCardId

private struct ScopedFocusLayout {
  let blockStateStorage: DivBlockStateStorage
  let handler: DivActionHandler
  let reporter: MockReporter
  let firstInputPath: UIElementPath
  let secondInputPath: UIElementPath
}
