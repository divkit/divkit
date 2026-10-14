@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import XCTest

final class DivDataExtensionsTests: XCTestCase {
  func test_WhenStateIsNil_TakesFirstState() throws {
    let block = try data
      .makeBlock(context: .default)
      .withoutStateBlock()
    let expectedBlock = try data.states[0].div.value
      .makeBlock(context: .default.modifying(pathSuffix: "0"))
    XCTAssertTrue(block == expectedBlock)
  }

  func test_WhenStateIsPresent_TakesCorrespondingState() throws {
    let context = DivBlockModelingContext()
    context.stateManager.setStateWithHistory(
      path: DivData.rootPath,
      stateID: DivStateID(rawValue: "1")
    )
    let block = try data
      .makeBlock(context: context)
      .withoutStateBlock()
    let expectedBlock = try data.states[1].div.value
      .makeBlock(context: .default.modifying(pathSuffix: "1"))
    XCTAssertTrue(block == expectedBlock)
  }

  func test_WhenStateIsNotFound_TakesFirstState() throws {
    let context = DivBlockModelingContext()
    context.stateManager.setStateWithHistory(
      path: DivData.rootPath,
      stateID: DivStateID(rawValue: "100500")
    )
    let block = try data
      .makeBlock(context: context)
      .withoutStateBlock()
    let expectedBlock = try data.states[0].div.value
      .makeBlock(context: .default.modifying(pathSuffix: "0"))
    XCTAssertTrue(block == expectedBlock)
  }

  func test_ActionPathContainsCardLogId() throws {
    let block = try divData(
      divText(
        actions: [divAction(logId: "action_log_id")],
        text: "0"
      ),
      logId: "card_log_id"
    )
    .makeBlock(context: .default)
    .withoutStateBlock()

    XCTAssertEqual(block.actions?.first.path, UIElementPath("card_log_id") + "action_log_id")
  }

  func test_GalleryPathContainsRoot() throws {
    let block = try divData(
      divGallery(
        items: [
          divText(
            text: "0",
            width: fixedSize(10)
          ),
        ]
      )
    )
    .makeBlock(context: .default)
    .withoutStateBlock()
    let galleryBlock = block.child as! GalleryBlock

    XCTAssertEqual(galleryBlock.model.path, UIElementPath.root + 0 + "gallery")
  }

  @MainActor
  func test_WhenStateChanges_ReportsVisibilityForNewState() async throws {
    let timerScheduler = TestTimerScheduler()
    let context = DivBlockModelingContext(scheduler: timerScheduler)

    for rootStateId in [nil, "1", "0", "1"] {
      if let rootStateId {
        context.stateManager.setStateWithHistory(
          path: DivData.rootPath,
          stateID: DivStateID(rawValue: rootStateId)
        )
      }

      let block = try makeBlock(fromFile: "root_states_visibility", context: context)

      let rect = CGRect(
        origin: .zero,
        size: CGSize(width: 100, height: block.intrinsicContentHeight(forWidth: 100))
      )
      let view = block.makeBlockView()
      let result = try await getViewVisibilityCallCount(
        view: view,
        rect: rect,
        timerScheduler: timerScheduler
      )

      XCTAssertEqual(result, 1)
    }
  }

  func test_WhenScopeElementMovesBetweenModels_ScopeResolvesToNewPath() throws {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    try makeBlocksWithMovingTarget(movingTarget(), components: components)

    components.actionHandler.handle(
      divAction(
        logId: "set_value",
        scopeId: "target",
        typed: .divActionSetVariable(DivActionSetVariable(
          value: .stringValue(StringValue(value: .value("updated"))),
          variableName: .value("value")
        ))
      ),
      path: movingTargetCardId.path,
      source: .tap,
      sender: nil
    )

    XCTAssertNil(reporter.lastError)
    XCTAssertEqual(components.targetValue(at: newTargetPath), "updated")
    XCTAssertNotEqual(components.targetValue(at: oldTargetPath), "updated")
  }

  func test_WhenTriggerIsScopedToItsMovedElement_ActionUsesNewPath() throws {
    let reporter = MockReporter()
    let components = DivKitComponents(reporter: reporter)
    var target = movingTarget()
    target["variable_triggers"] = [[
      "condition": "@{value == 'initial'}",
      "actions": [[
        "log_id": "set_value",
        "scope_id": "target",
        "typed": [
          "type": "set_variable",
          "variable_name": "value",
          "value": ["type": "string", "value": "triggered"],
        ],
      ]],
    ]]

    try makeBlocksWithMovingTarget(target, components: components)

    XCTAssertNil(reporter.lastError)
    XCTAssertEqual(components.targetValue(at: newTargetPath), "triggered")
  }

  func test_WhenModelingFails_IdsOfPreviousModelAreDropped() throws {
    let components = DivKitComponents()
    try makeBlocksWithMovingTarget(movingTarget(), components: components)
    let context = components.makeContext(cardId: movingTargetCardId, cachedImageHolders: [])

    XCTAssertThrowsError(try divData(states: []).makeBlock(context: context))

    XCTAssertTrue(context.idToPath.paths(forId: "target", cardId: movingTargetCardId).isEmpty)
  }
}

private let movingTargetCardId: DivCardID = "moving_target_card"
private let oldTargetPath = movingTargetCardId.path + 0 + "container" + 0 + "target"
private let newTargetPath = movingTargetCardId.path + 0 + "container" + 1 + "target"

private func movingTarget() -> [String: Any] {
  [
    "type": "text",
    "id": "target",
    "text": "target",
    "variables": [["type": "string", "name": "value", "value": "initial"]],
  ]
}

/// Models the card twice, the way hosts without DivView do, moving `target` to the second item.
private func makeBlocksWithMovingTarget(
  _ target: [String: Any],
  components: DivKitComponents
) throws {
  let other: [String: Any] = ["type": "text", "id": "other", "text": "other"]
  for items in [[target], [other, target]] {
    let card: [String: Any] = [
      "log_id": movingTargetCardId.rawValue,
      "states": [["state_id": 0, "div": ["type": "container", "items": items]]],
    ]
    let divData = try XCTUnwrap(DivData.resolve(card: card, templates: nil).value)
    _ = try divData.makeBlock(
      context: components.makeContext(cardId: movingTargetCardId, cachedImageHolders: [])
    )
  }
}

extension DivKitComponents {
  fileprivate func targetValue(at path: UIElementPath) -> String? {
    variablesStorage.getVariableValue(path: path, name: "value")
  }
}

private let data = divData(
  states: [
    .init(div: divText(text: "0"), stateId: 0),
    .init(div: divText(text: "1"), stateId: 1),
    .init(div: divText(text: "2"), stateId: 2),
  ]
)

extension Block {
  fileprivate func withoutStateBlock() throws -> DecoratingBlock {
    let stateBlock = try XCTUnwrap(self as? StateBlock)
    return try XCTUnwrap(stateBlock.child as? DecoratingBlock)
  }
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext = .default
) throws -> Block {
  try DivData.make(
    fromFile: filename,
    subdirectory: "div-data",
    context: context
  )
}
