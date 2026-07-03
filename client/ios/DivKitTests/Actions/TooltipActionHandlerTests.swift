@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing

@Suite
struct TooltipActionHandlerTests {
  @Test
  func showTooltip_withScopeId_firstScope_passesFirstScopePathInTooltipInfo() {
    var capturedInfo: TooltipInfo?
    let layout = makeScopedTooltipLayout { capturedInfo = $0 }

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "first",
        typed: .divActionShowTooltip(DivActionShowTooltip(
          id: .value("my_tooltip"),
          multiple: .value(false)
        ))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError == nil)
    #expect(capturedInfo != nil)
    #expect(capturedInfo?.scopePath == layout.firstScopePath)
  }

  @Test
  func showTooltip_withScopeId_secondScope_passesSecondScopePathInTooltipInfo() {
    var capturedInfo: TooltipInfo?
    let layout = makeScopedTooltipLayout { capturedInfo = $0 }

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "second",
        typed: .divActionShowTooltip(DivActionShowTooltip(
          id: .value("my_tooltip"),
          multiple: .value(false)
        ))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError == nil)
    #expect(capturedInfo != nil)
    #expect(capturedInfo?.scopePath == layout.secondScopePath)
  }

  @Test
  func showTooltip_withoutScopeId_passesNilScopePathInTooltipInfo() {
    var capturedInfo: TooltipInfo?
    let layout = makeScopedTooltipLayout { capturedInfo = $0 }

    layout.handler.handle(
      divAction(
        logId: "action_id",
        typed: .divActionShowTooltip(DivActionShowTooltip(
          id: .value("my_tooltip"),
          multiple: .value(false)
        ))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError == nil)
    #expect(capturedInfo != nil)
    #expect(capturedInfo?.scopePath == nil)
  }

  @Test
  func showTooltip_withAmbiguousScopeId_reportsErrorAndDoesNotShowTooltip() {
    var capturedInfo: TooltipInfo?
    let layout = makeScopedTooltipLayout { capturedInfo = $0 }

    layout.handler.handle(
      divAction(
        logId: "action_id",
        scopeId: "duplicate",
        typed: .divActionShowTooltip(DivActionShowTooltip(
          id: .value("my_tooltip"),
          multiple: .value(false)
        ))
      ),
      path: cardId.path + "button",
      source: .tap,
      sender: nil
    )

    #expect(layout.reporter.lastError != nil)
    #expect(capturedInfo == nil)
  }

  private func makeScopedTooltipLayout(
    onShowTooltip: @escaping (TooltipInfo) -> Void
  ) -> ScopedTooltipLayout {
    let idToPath = IdToPath()
    let firstScopePath = cardId.path + "container" + "0" + "first"
    let secondScopePath = cardId.path + "container" + "1" + "second"
    let duplicateScopePathA = cardId.path + "container" + "2" + "duplicate"
    let duplicateScopePathB = cardId.path + "container" + "3" + "duplicate"

    idToPath.add(firstScopePath, forId: cardId.path + "first")
    idToPath.add(secondScopePath, forId: cardId.path + "second")
    idToPath.add(duplicateScopePathA, forId: cardId.path + "duplicate")
    idToPath.add(duplicateScopePathB, forId: cardId.path + "duplicate")

    let reporter = MockReporter()
    let handler = DivActionHandler(
      idToPath: idToPath,
      reporter: reporter,
      showTooltip: onShowTooltip
    )

    return ScopedTooltipLayout(
      handler: handler,
      reporter: reporter,
      firstScopePath: firstScopePath,
      secondScopePath: secondScopePath
    )
  }
}

private let cardId = DivBlockModelingContext.testCardId

private struct ScopedTooltipLayout {
  let handler: DivActionHandler
  let reporter: MockReporter
  let firstScopePath: UIElementPath
  let secondScopePath: UIElementPath
}
