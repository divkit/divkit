@testable @_spi(Internal) @_spi(Legacy) import DivKit
import DivKitTestsSupport
import LayoutKit
import Serialization
import UIKit
import VGSL
import XCTest

@MainActor
final class ExtensionActionTests: XCTestCase {
  private let handler = RecordingExtensionActionHandler()
  private let reporter = MockReporter()

  func test_TypedActionDeliversPayloadAndRuntimePath() throws {
    let card = makeCard(root: target())

    try card.send("start")

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/target")
    XCTAssertEqual(handler.payload?["type"] as? String, "start")
    XCTAssertNil(reporter.lastError)
  }

  func test_VisibilityActionUsesExtensionRouting() throws {
    let card = makeCard(root: target())

    try card.send("start", source: .visibility)

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/target")
    XCTAssertEqual(handler.payload?["type"] as? String, "start")
  }

  func test_HandlerReplacementKeepsModeledHandlerUntilRemodeling() throws {
    let card = makeCard(root: target())
    let replacement = RecordingExtensionActionHandler()
    card.components.extensionHandlers = [replacement]

    try card.send("start")

    XCTAssertEqual(handler.commands, ["start"])
    XCTAssertEqual(replacement.commands, [])
  }

  func test_HandlerReplacementAppliesAfterRemodeling() throws {
    let card = makeCard(root: target())
    let replacement = RecordingExtensionActionHandler()
    card.components.extensionHandlers = [replacement]
    card.components.forceUpdate()

    try card.send("start")

    XCTAssertEqual(replacement.commands, ["start"])
    XCTAssertEqual(handler.commands, [])
  }

  func test_HandlerRemovalKeepsModeledHandlerUntilRemodeling() throws {
    let card = makeCard(root: target())
    card.components.extensionHandlers = []

    try card.send("start")

    XCTAssertEqual(handler.commands, ["start"])
    XCTAssertNil(reporter.lastError)
  }

  func test_ActionAfterHandlerRemovalAndRemodelingReportsError() throws {
    let card = makeCard(root: target())
    card.components.extensionHandlers = []
    card.components.forceUpdate()

    try card.send("start")

    XCTAssertNil(handler.context)
    XCTAssertEqual(reporter.lastError?.message, "No action handler for extension 'test'")
  }

  func test_DisappearActionUsesExtensionRouting() throws {
    let card = makeCard(root: target())

    try card.send("stop", source: .disappear)

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/target")
    XCTAssertEqual(handler.payload?["type"] as? String, "stop")
  }

  func test_HiddenDescendantWaitsForModeling() throws {
    let card = makeCard(root: [
      "type": "container", "id": "hidden", "visibility": "gone", "items": [target()],
    ])

    try card.send("start")

    XCTAssertNil(handler.context)
    XCTAssertFalse(handler.acceptedPaths.contains("extension_card/0/hidden/0/target"))
  }

  func test_DisabledExtensionIsAddressable() throws {
    var root = target()
    root["extensions"] = [["id": "test", "is_enabled": false]]
    let card = makeCard(root: root)

    try card.send("start")

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/target")
    XCTAssertTrue(handler.acceptedPaths.isEmpty)
  }

  func test_ScopeSelectsItsOwnTarget() throws {
    let card = makeCard(root: ["type": "container", "id": "root", "items": [
      ["type": "container", "id": "left", "items": [target()]],
      ["type": "container", "id": "right", "items": [target()]],
    ]])

    try card.send("start", scopeId: "right")

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/root/1/right/0/target")
  }

  func test_AmbiguousTargetsAreRejected() throws {
    let card = makeCard(root: ["type": "container", "items": [target(), target()]])

    try card.send("start")

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is ambiguous"
    )
  }

  func test_AmbiguousScopesAreRejected() throws {
    let scope: [String: Any] = ["type": "container", "id": "scope", "items": [target()]]
    let card = makeCard(root: ["type": "container", "items": [scope, scope]])

    try card.send("start", scopeId: "scope")

    XCTAssertNil(handler.context)
    XCTAssertEqual(reporter.lastError?.message, "Scope with id 'scope' is ambiguous")
  }

  func test_WrapContainerUsesRenderedChildIndex() throws {
    var validTarget = target()
    validTarget["width"] = ["type": "fixed", "value": 100]
    let card = makeCard(root: [
      "type": "container", "id": "root", "orientation": "vertical", "layout_mode": "wrap",
      "items": [["type": "text", "text": "Invalid match_parent width"], validTarget],
    ])

    try card.send("start")

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/root/0/target")
    XCTAssertTrue(handler.acceptedPaths.contains("extension_card/0/root/0/target"))
  }

  func test_MissingScopeFallsBackToCardWideTarget() throws {
    let card = makeCard(root: target())

    try card.send("start", scopeId: "missing")

    XCTAssertEqual(handler.paths, ["extension_card/0/target"])
    XCTAssertEqual(reporter.lastError?.message, "Scope with id 'missing' not found")
  }

  func test_OnlyActiveStateParticipatesInLookup() throws {
    let card = makeCard(root: [
      "type": "state", "id": "state", "default_state_id": "second", "states": [
        ["state_id": "first", "div": target()],
        ["state_id": "second", "div": target()],
      ],
    ])

    try card.send("start")

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/state/second/target")
    XCTAssertNil(reporter.lastError)
  }

  func test_DuplicateDynamicScopesAreAmbiguous() throws {
    let card = makeCard(root: ["type": "container", "id": "root", "item_builder": [
      "data": [["name": "left"], ["name": "right"]],
      "prototypes": [["id": "scope", "div": [
        "type": "container", "id": "scope", "items": [target()],
      ]]],
    ]])

    try card.send("start", scopeId: "scope", sourcePath: "extension_card/0/root/1/scope")

    XCTAssertNil(handler.context)
    XCTAssertEqual(reporter.lastError?.message, "Scope with id 'scope' is ambiguous")
  }

  func test_NearbyActionSourceDoesNotDisambiguateTargets() throws {
    let card = makeCard(root: ["type": "container", "id": "root", "items": [
      target(), target(),
    ]])

    try card.send("start", sourcePath: "extension_card/0/root/0/target")

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is ambiguous"
    )
  }

  func test_InactiveStateTargetWaitsForModeling() throws {
    // Arrange
    let card = makeCard(root: [
      "type": "state", "id": "state", "default_state_id": "first", "states": [
        ["state_id": "first", "div": ["type": "text", "text": "Active"]],
        ["state_id": "second", "div": target()],
      ],
    ])

    // Act
    try card.send("start")

    // Assert
    XCTAssertNil(handler.context)
    XCTAssertFalse(handler.acceptedPaths.contains("extension_card/0/state/second/target"))
  }

  func test_HiddenTargetLookupDoesNotInitializeLocalVariable() throws {
    // Arrange
    var hiddenTarget = target()
    hiddenTarget["variables"] = [["name": "local", "type": "string", "value": "target_value"]]
    let card = makeCard(root: [
      "type": "container", "id": "hidden", "visibility": "gone", "items": [hiddenTarget],
    ])
    let path = UIElementPath.parse("extension_card/0/hidden/0/target")

    // Act
    try card.send("start")

    // Assert
    XCTAssertNil(handler.context)
    let local: String? = card.components.variablesStorage.getVariableValue(
      path: path,
      name: DivVariableName(rawValue: "local")
    )
    XCTAssertNil(local)
  }

  func test_HiddenTargetLookupDoesNotRegisterLocalFunction() throws {
    // Arrange
    var hiddenTarget = target()
    hiddenTarget["functions"] = [[
      "name": "localCommand", "body": "@{'stop'}", "return_type": "string", "arguments": [],
    ]]
    let card = makeCard(root: [
      "type": "container", "id": "hidden", "visibility": "gone", "items": [hiddenTarget],
    ])
    try card.send("start")

    // Act
    try card.send("@{localCommand()}", sourcePath: "extension_card/0/hidden/0/target")

    // Assert
    XCTAssertNil(handler.payload)
    let context = card.components.makeContext(cardId: card.cardId, cachedImageHolders: [])
    XCTAssertNil(context.expressionResolver.resolveString("@{localCommand()}"))
  }

  func test_TargetInHiddenDynamicContainerWaitsForModeling() throws {
    let card = makeCard(root: dynamicCollection(type: "container"))

    try card.send("start")

    card.components.flushUpdateActions()

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is not modeled yet, the action is deferred"
    )
  }

  func test_TargetInHiddenDynamicGalleryWaitsForModeling() throws {
    let card = makeCard(root: dynamicCollection(type: "gallery"))

    try card.send("start")

    card.components.flushUpdateActions()

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is not modeled yet, the action is deferred"
    )
  }

  func test_TargetInHiddenDynamicPagerWaitsForModeling() throws {
    let card = makeCard(root: dynamicCollection(type: "pager"))

    try card.send("start")

    card.components.flushUpdateActions()

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is not modeled yet, the action is deferred"
    )
  }

  func test_DynamicCollectionNodeIsAddressableWithoutExpandingChildren() throws {
    var collection = dynamicCollection(type: "container")
    collection["extensions"] = [["id": "test"]]
    let card = makeCard(root: collection)

    try card.send("start", targetId: "collection", scopeId: "collection")

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/collection")
    XCTAssertNil(reporter.lastError)
  }

  func test_StaticSiblingIsAddressableAfterDynamicSubtree() throws {
    let card = makeCard(root: ["type": "container", "id": "root", "items": [
      dynamicCollection(type: "container"), target(),
    ]])

    try card.send("start")

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/root/1/target")
    XCTAssertNil(reporter.lastError)
  }

  func test_PayloadStaysRawAndResolverUsesSourceVariables() throws {
    var target = target()
    target["variables"] = [["name": "command", "type": "string", "value": "stop"]]
    let card = makeCard(root: ["type": "container", "id": "root", "items": [
      target,
      ["type": "text", "id": "source", "text": "Source", "variables": [
        ["name": "command", "type": "string", "value": "start"],
      ]],
    ]])

    try card.send("ignored", sourcePath: "extension_card/0/root/1/source", payload: [
      "type": "@{command}", "nested": ["array": ["@{command}", "@{1 + 2}"]],
    ])

    XCTAssertEqual(handler.payload?["type"] as? String, "@{command}")
    XCTAssertEqual(handler.context?.expressionResolver.resolveString("@{command}"), "start")
    let nested = handler.payload?["nested"] as? [String: Any]
    XCTAssertEqual(nested?["array"] as? [String], ["@{command}", "@{1 + 2}"])
  }

  func test_UndeclaredExtensionWaitsForMatchingTarget() throws {
    let card = makeCard(root: target())

    try card.send("start", extensionId: "other")

    card.components.flushUpdateActions()

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is not modeled yet, the action is deferred"
    )
  }

  func test_UnresolvedPayloadIsPassedToHandler() throws {
    let card = makeCard(root: target())

    try card.send("@{missing}")

    XCTAssertEqual(handler.payload?["type"] as? String, "@{missing}")
    XCTAssertNil(reporter.lastError)
  }

  func test_NestedPayloadIsPassedWithoutEvaluation() throws {
    let card = makeCard(root: target())

    try card.send("start", payload: ["type": "start", "nested": ["array": ["@{missing}"]]])

    let nested = handler.payload?["nested"] as? [String: Any]
    XCTAssertEqual(nested?["array"] as? [String], ["@{missing}"])
    XCTAssertNil(reporter.lastError)
  }

  func test_HandlerRejectingCommandReportsUnsupportedAction() throws {
    handler.handlesAction = false
    let card = makeCard(root: target())

    try card.send("unsupported")

    XCTAssertEqual(handler.payload?["type"] as? String, "unsupported")
    XCTAssertEqual(reporter.lastError?.message, "Unsupported action for extension 'test'")
  }

  func test_DisabledActionDoesNotEvaluatePayload() throws {
    let card = makeCard(root: target())

    try card.send("@{missing}", isEnabled: false)

    XCTAssertNil(handler.context)
    XCTAssertNil(reporter.lastError)
  }

  func test_ReplacingDataRemovesOldTargets() throws {
    let card = makeCard(root: target())
    card.setRoot(["type": "text", "text": "Replaced"])

    try card.send("start")

    card.components.flushUpdateActions()

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is not modeled yet, the action is deferred"
    )
  }

  func test_PatchCallbackAddressesNewTargetAfterModeling() throws {
    let card = makeCard(root: ["type": "text", "id": "placeholder", "text": "Old"])
    let patch = try DivPatch(dictionary: [
      "changes": [["id": "placeholder", "items": [target()]]],
      "on_applied_actions": [[
        "log_id": "start_new_target",
        "typed": ["type": "extension_action", "extension_id": "test", "div_id": "target"],
        "payload": ["type": "start"],
      ]],
    ], context: ParsingContext())

    card.view.applyPatch(patch, cardId: card.cardId)
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.context?.path.description, "extension_card/0/target")
    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_TooltipTargetUsesItsRuntimePath() async throws {
    let card = makeCard(root: ["type": "text", "id": "anchor", "text": "Host"])
    let tooltip = try await makeTooltip(card: card)

    try card.send("start")

    XCTAssertEqual(
      handler.context?.path.description,
      "extension_card/0/anchor/tooltip#hint/0/target"
    )
    withExtendedLifetime(tooltip) {}
  }

  func test_ClosedTooltipIsNoLongerAddressable() async throws {
    let card = makeCard(root: ["type": "text", "id": "anchor", "text": "Host"])
    let tooltip = try await makeTooltip(card: card)
    tooltip.tooltipDidClose()

    try card.send("start")

    card.components.flushUpdateActions()

    XCTAssertNil(handler.context)
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is not modeled yet, the action is deferred"
    )
  }

  func test_DirectModelingAddressesNestedContainerTarget() throws {
    try assertDirectModelingDelivers(root: ["type": "container", "id": "root", "items": [
      ["type": "text", "text": "Sibling"],
      ["type": "container", "items": [target()]],
    ]])
  }

  func test_DirectModelingAddressesGridTarget() throws {
    try assertDirectModelingDelivers(root: ["type": "grid", "column_count": 2, "items": [
      ["type": "text", "text": "Sibling"], target(),
    ]])
  }

  func test_DirectModelingAddressesGalleryTarget() throws {
    try assertDirectModelingDelivers(root: ["type": "gallery", "items": [
      ["type": "text", "text": "Sibling"], target(),
    ]])
  }

  func test_DirectModelingAddressesPagerTarget() throws {
    try assertDirectModelingDelivers(root: [
      "type": "pager",
      "layout_mode": ["type": "percentage", "page_width": ["type": "percentage", "value": 100.0]],
      "items": [["type": "text", "text": "Sibling"], target()],
    ])
  }

  func test_DirectModelingAddressesTabTarget() throws {
    try assertDirectModelingDelivers(root: ["type": "tabs", "items": [
      ["title": "First", "div": ["type": "text", "text": "Sibling"]],
      ["title": "Second", "div": ["type": "container", "items": [target()]]],
    ]])
  }

  func test_DirectModelingAddressesActiveStateTarget() throws {
    try assertDirectModelingDelivers(root: ["type": "state", "id": "switch", "states": [
      ["state_id": "active", "div": ["type": "container", "items": [target()]]],
      ["state_id": "inactive", "div": ["type": "text", "id": "unmodeled", "text": "Inactive"]],
    ]])
  }

  func test_DirectModelingAddressesTargetAfterFilteringWrapItems() throws {
    var fixedTarget = target()
    fixedTarget["width"] = ["type": "fixed", "value": 20]
    try assertDirectModelingDelivers(root: [
      "type": "container", "orientation": "vertical", "layout_mode": "wrap", "items": [
        ["type": "text", "text": "Filtered", "width": ["type": "match_parent"]], fixedTarget,
      ],
    ])
  }

  func test_DirectModelingAddressesTooltipTarget() throws {
    try assertDirectModelingDelivers(
      root: ["type": "container", "items": [target()]],
      viewId: DivViewId(cardId: "extension_card", tooltip: DivViewId.Tooltip(
        id: "hint", anchorPath: UIElementPath.parse("extension_card/0/anchor")
      ))
    )
  }

  func test_DirectModelingAddressesDynamicCollectionItself() throws {
    var collection = dynamicCollection(type: "container")
    collection["visibility"] = "visible"
    collection["extensions"] = [["id": "test"]]
    try assertDirectModelingDelivers(root: collection, targetId: "collection")
  }

  func test_LastDeferredCommandIsDeliveredWhenRevealed() throws {
    // Arrange
    let card = makeDeferredCard()
    try card.send("start")
    try card.send("stop")
    try card.send("start")

    // Act
    reveal(card)

    // Assert
    XCTAssertEqual(handler.commands, ["start"])
    XCTAssertEqual(handler.paths, ["extension_card/0/hidden/0/target"])
  }

  func test_DeferredPayloadKeepsExpressionAndLiveSourceResolver() throws {
    // Arrange
    let card = makeDeferredCard()
    card.components.variablesStorage.append(
      variables: ["command": .string("start")],
      for: card.cardId
    )
    try card.send("@{command}")
    card.components.variablesStorage.update(cardId: card.cardId, name: "command", value: "stop")

    // Act
    reveal(card)

    // Assert
    XCTAssertEqual(handler.commands, ["@{command}"])
    XCTAssertEqual(handler.context?.expressionResolver.resolveString("@{command}"), "stop")
  }

  func test_MissingAddressDoesNotBlockAnotherTarget() throws {
    let card = makeCard(root: target())
    try card.send("missing", targetId: "absent")
    card.components.flushUpdateActions()

    try card.send("start")

    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_MissingScopeFindsAlreadyModeledTarget() throws {
    let card = makeDeferredCard(siblings: [target()])

    try card.send("start", scopeId: "late_scope")

    XCTAssertEqual(handler.paths, ["extension_card/0/container/1/target"])
    XCTAssertEqual(reporter.lastError?.message, "Scope with id 'late_scope' not found")
  }

  func test_NewCommandReplacesQueuedCommandsDuringReveal() throws {
    let card = makeDeferredCard()
    try card.send("first")
    try card.send("second")

    card.components.variablesStorage.update(cardId: card.cardId, name: "show", value: "1")
    try card.send("third")
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["third"])
  }

  func test_AvailableTargetDoesNotWarnWhileUpdateIsPending() throws {
    let card = makeDeferredCard(siblings: [target()])
    card.components.variablesStorage.update(cardId: card.cardId, name: "show", value: "2")

    try card.send("start")
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["start"])
    XCTAssertNil(reporter.lastError)
  }

  func test_MissingTargetWarnsAfterPendingUpdateCompletes() throws {
    let card = makeDeferredCard()
    card.components.variablesStorage.update(cardId: card.cardId, name: "show", value: "1")

    try card.send("start", targetId: "absent")
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, [])
    XCTAssertEqual(reporter.errors.map(\.message), [
      "Element with id 'absent' is not modeled yet, the action is deferred",
    ])
  }

  func test_MissingTargetDoesNotScheduleAnEndlessUpdateLoop() async throws {
    // Arrange
    let card = makeCard(root: target())
    var updates = 0
    let extraUpdate = expectation(description: "No repeated update for a missing target")
    extraUpdate.isInverted = true
    let subscription = card.components.updateCardSignal.addObserver { reasons in
      guard !reasons.isEmpty else {
        return
      }
      updates += 1
      if updates > 1 {
        extraUpdate.fulfill()
      }
    }
    try card.send("start", targetId: "absent")

    // Act
    card.components.flushUpdateActions()
    await fulfillment(of: [extraUpdate], timeout: 0.1)

    // Assert
    XCTAssertEqual(updates, 1)
    XCTAssertEqual(reporter.errors.filter { $0.message.contains("is deferred") }.count, 1)
    withExtendedLifetime(subscription) {}
  }

  func test_ResetDuringDeliveryReceivesOnlyLatestDeferredCommand() throws {
    // Arrange
    let card = makeDeferredCard()
    try card.send("start")
    try card.send("stop")
    handler.onAction = { [weak components = card.components] in
      components?.reset(cardId: "extension_card")
    }

    // Act
    reveal(card)

    // Assert
    XCTAssertEqual(handler.commands, ["stop"])
  }

  func test_ReplacementDataReceivesRetainedCommand() throws {
    let card = makeDeferredCard()
    try card.send("old")

    card.setRoot(target())
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["old"])
  }

  func test_ResetAllCancelsDeferredCommands() throws {
    let card = makeDeferredCard()
    try card.send("old")

    card.components.reset()
    reveal(card)

    XCTAssertEqual(handler.commands, [])
  }

  func test_ReentrantCommandIsDeliveredSynchronously() throws {
    // Arrange
    let card = makeDeferredCard()
    try card.send("start")
    try card.send("stop")
    handler.onAction = {
      self.handler.onAction = nil
      try? card.send("restart")
    }

    // Act
    reveal(card)

    // Assert
    XCTAssertEqual(handler.commands, ["stop", "restart"])
  }

  func test_ImmediateCommandDeliversReentrantCommandBeforeReturning() throws {
    let card = makeCard(root: target())
    handler.onAction = {
      self.handler.onAction = nil
      try? card.send("second")
    }

    try card.send("first")

    XCTAssertEqual(handler.commands, ["first", "second"])
  }

  func test_StateSwitchCommandUsesNewModel() throws {
    // Arrange
    let card = makeCard(root: [
      "type": "state", "id": "switch", "default_state_id": "first", "states": [
        ["state_id": "first", "div": target()],
        ["state_id": "second", "div": target()],
      ],
    ])
    let action = try DivAction(dictionary: [
      "log_id": "switch", "typed": ["type": "set_state", "state_id": "0/switch/second"],
    ], context: ParsingContext())

    // Act
    card.components.actionHandler.handle(action, path: card.cardId.path, source: .tap, sender: nil)
    try card.send("start")
    card.components.flushUpdateActions()

    // Assert
    XCTAssertEqual(handler.paths, ["extension_card/0/switch/second/target"])
  }

  func test_PatchCallbackWithSameIdUsesReplacementPath() throws {
    // Arrange
    let card = makeCard(root: target())
    let patch = try DivPatch(dictionary: [
      "changes": [["id": "target", "items": [[
        "type": "container", "id": "replacement", "items": [target()],
      ]]]],
      "on_applied_actions": [[
        "log_id": "patched", "typed": [
          "type": "extension_action", "extension_id": "test", "div_id": "target",
        ], "payload": ["type": "start"],
      ]],
    ], context: ParsingContext())

    // Act
    card.view.applyPatch(patch, cardId: card.cardId)
    card.components.flushUpdateActions()

    // Assert
    XCTAssertEqual(handler.paths, ["extension_card/0/replacement/0/target"])
  }

  func test_DeferredCommandsUseReplacementHandler() throws {
    let card = makeDeferredCard()
    try card.send("start")
    let replacement = RecordingExtensionActionHandler()

    card.components.extensionHandlers = [replacement]
    reveal(card)

    XCTAssertEqual(replacement.commands, ["start"])
    XCTAssertEqual(handler.commands, [])
  }

  func test_DynamicContainerTargetIsAddressable() throws {
    try assertDynamicTargetIsAddressable(type: "container")
  }

  func test_DynamicGalleryTargetIsAddressable() throws {
    try assertDynamicTargetIsAddressable(type: "gallery")
  }

  func test_DynamicPagerTargetIsAddressable() throws {
    try assertDynamicTargetIsAddressable(type: "pager")
  }

  func test_DirectModelingDeliversPreviouslyQueuedCommand() async throws {
    // Arrange
    let components = DivKitComponents(extensionHandlers: [handler], reporter: reporter)
    try sendDirectCommand(components: components)
    components.flushUpdateActions()
    let delivered = expectation(description: "Direct model delivers command")
    handler.onAction = { delivered.fulfill() }

    // Act
    _ = try makeData(root: target()).makeBlock(context: components.makeContext(
      cardId: "extension_card", cachedImageHolders: []
    ))
    await fulfillment(of: [delivered], timeout: 2)

    // Assert
    XCTAssertEqual(handler.paths, ["extension_card/0/target"])
  }

  func test_FailedModelDoesNotLeavePreviousTargetAddressable() throws {
    // Arrange
    let components = DivKitComponents(extensionHandlers: [handler], reporter: reporter)
    let context = components.makeContext(cardId: "extension_card", cachedImageHolders: [])
    _ = try makeData(root: target()).makeBlock(context: context)
    let invalid = DivData(
      functions: nil, logId: "invalid", states: [], timers: nil,
      transitionAnimationSelector: nil, variableTriggers: nil, variables: nil
    )
    XCTAssertThrowsError(try invalid.makeBlock(context: context))

    // Act
    try sendDirectCommand(components: components)

    // Assert
    XCTAssertEqual(handler.commands, [])
  }

  func test_DirectModelingUsesComponentsExtensionHandlers() throws {
    // Arrange
    let components = DivKitComponents(extensionHandlers: [handler], reporter: reporter)
    let context = components.makeContext(cardId: "extension_card", cachedImageHolders: [])
    _ = try makeData(root: target()).makeBlock(context: context)

    // Act
    try sendDirectCommand(components: components)

    // Assert
    XCTAssertEqual(handler.paths, ["extension_card/0/target"])
  }

  func test_InitialTriggerWithImmediateInitialization() throws {
    try assertInitialTriggerDelivers(initializeOnSet: true)
  }

  func test_InitialTriggerWithDelayedInitialization() throws {
    try assertInitialTriggerDelivers(initializeOnSet: false)
  }

  func test_EarlyTriggerUsesRegistrationsAvailableDuringModeling() throws {
    // Arrange
    var early = target()
    early["variable_triggers"] = [["condition": "@{true}", "actions": [commandDictionary()]]]

    // Act
    let card = makeCard(root: ["type": "container", "items": [early, target()]])
    card.components.flushUpdateActions()

    // Assert
    XCTAssertEqual(handler.paths, ["extension_card/0/container/0/target"])
  }

  func test_TooltipFirstUpdateStillDetectsCrossViewAmbiguity() throws {
    try assertCrossViewAmbiguity(tooltipFirst: true)
  }

  func test_HostFirstUpdateStillDetectsCrossViewAmbiguity() throws {
    try assertCrossViewAmbiguity(tooltipFirst: false)
  }

  func test_ReopenedTooltipReceivesRetainedCommand() async throws {
    // Arrange
    let card = makeCard(root: ["type": "text", "id": "anchor", "text": "Host"])
    let tooltip = try await makeTooltip(card: card, root: [
      "type": "container", "id": "scope", "visibility": "gone", "items": [target()],
    ])
    try card.send("old", scopeId: "scope")
    card.components.flushUpdateActions()

    // Act
    tooltip.tooltipDidClose()
    let replacement = try await makeTooltip(card: card, root: [
      "type": "container", "id": "scope", "items": [target()],
    ])
    card.components.flushUpdateActions()

    // Assert
    XCTAssertEqual(handler.commands, ["old"])
    replacement.tooltipDidClose()
  }

  func test_ClosingOneTooltipSessionKeepsAnotherSessionAddressable() async throws {
    // Arrange
    let card = makeCard(root: ["type": "text", "id": "anchor", "text": "Host"])
    let first = try await makeTooltip(card: card)
    let second = try await makeTooltip(card: card)

    // Act
    first.tooltipDidClose()
    try card.send("start")

    // Assert
    XCTAssertEqual(handler.paths, ["extension_card/0/anchor/tooltip#hint/0/target"])
    second.tooltipDidClose()
  }

  func test_ClosingSourceTooltipPreservesCommandToHost() async throws {
    // Arrange
    let card = makeDeferredCard()
    let tooltip = try await makeTooltip(card: card, root: ["type": "text", "text": "Source"])
    try card.send("start", sourcePath: "extension_card/0/anchor/tooltip#hint/0/text")

    // Act
    tooltip.tooltipDidClose()
    reveal(card)

    // Assert
    XCTAssertEqual(handler.paths, ["extension_card/0/hidden/0/target"])
  }

  func test_InvalidScopeExpressionFallsBackToCardLookup() throws {
    let card = makeCard(root: target())

    try card.send("start", scopeId: "@{undefined_scope}")

    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_ResetOtherCardDoesNotCancelPendingCommand() throws {
    let card = makeDeferredCard()
    try card.send("start")

    card.components.reset(cardId: "other")
    reveal(card)

    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_InvalidDataReplacementKeepsPreviousRegistration() throws {
    let card = makeCard(root: target())
    card.view.setSource(DivViewSource(kind: .data(Data("invalid".utf8)), cardId: card.cardId))

    try card.send("start")
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_InvalidJsonReplacementKeepsPreviousRegistration() throws {
    let card = makeCard(root: target())
    card.view.setSource(DivViewSource(kind: .json([:]), cardId: card.cardId))

    try card.send("start")
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_InvalidAsyncDataReplacementKeepsPreviousRegistration() async throws {
    let card = makeCard(root: target())
    await card.view.setSource(DivViewSource(kind: .data(Data("invalid".utf8)), cardId: card.cardId))

    try card.send("start")
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_InvalidAsyncJsonReplacementKeepsPreviousRegistration() async throws {
    let card = makeCard(root: target())
    await card.view.setSource(DivViewSource(kind: .json([:]), cardId: card.cardId))

    try card.send("start")
    card.components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["start"])
  }

  func test_ActionBurstRoutesToModeledItemBuilderTarget() throws {
    // Arrange
    let card = makeCard(root: ["type": "container", "id": "collection", "item_builder": [
      "data": (0..<500).map { ["number": $0] },
      "prototypes": [["id": "@{index}", "div": target()]],
    ]])
    card.components.flushUpdateActions()
    let modeledCount = handler.acceptCount

    // Act
    try (0..<100).forEach { _ in try card.send("start", targetId: "0") }

    // Assert
    XCTAssertEqual(handler.commands, Array(repeating: "start", times: 100))
    XCTAssertEqual(handler.acceptCount, modeledCount)
  }

  func test_UnresolvedCommandDoesNotRetainActionHandler() throws {
    var card: ExtensionActionTestCard? = makeDeferredCard()
    weak var actionHandler: DivActionHandler?
    actionHandler = card?.components.actionHandler
    try card?.send("start")
    card?.components.flushUpdateActions()

    card = nil

    XCTAssertNil(actionHandler)
  }

  func test_RevealTriggerCommandSupersedesRetainedCommand() throws {
    // Arrange
    let components = DivKitComponents(
      extensionHandlers: [handler],
      flagsInfo: DivFlagsInfo(initializeTriggerOnSet: true),
      reporter: reporter
    )
    components.variablesStorage.set(cardId: "extension_card", variables: ["show": .integer(0)])
    var revealedTarget = target()
    var stop = commandDictionary()
    stop["payload"] = ["type": "stop"]
    revealedTarget["variable_triggers"] = [["condition": "@{show == 1}", "actions": [stop]]]
    let card = ExtensionActionTestCard(
      root: [
        "type": "container",
        "visibility": "@{show == 1 ? 'visible' : 'gone'}",
        "items": [revealedTarget],
      ],
      handlers: [handler], reporter: reporter, components: components
    )
    try card.send("start")
    components.flushUpdateActions()

    // Act
    components.variablesStorage.update(cardId: card.cardId, name: "show", value: "1")
    components.flushUpdateActions()

    // Assert
    XCTAssertEqual(handler.commands, ["stop"])
  }

  private func assertDirectModelingDelivers(
    root: [String: Any],
    targetId: String = "target",
    viewId: DivViewId = DivViewId(cardId: "extension_card"),
    file: StaticString = #filePath,
    line: UInt = #line
  ) throws {
    let components = DivKitComponents(extensionHandlers: [handler], reporter: reporter)
    let data = try DivData(dictionary: [
      "log_id": "paths", "states": [["state_id": 0, "div": root]],
    ], context: ParsingContext())
    let context = components.makeContext(viewId: viewId, cachedImageHolders: [])
    _ = try data.makeBlock(context: context)
    let action = try DivAction(dictionary: [
      "log_id": "direct",
      "typed": ["type": "extension_action", "extension_id": "test", "div_id": targetId],
      "payload": ["type": "start"],
    ], context: ParsingContext())

    components.actionHandler.handle(action, path: viewId.path, source: .tap, sender: nil)

    XCTAssertEqual(handler.commands, ["start"], file: file, line: line)
    XCTAssertTrue(
      handler.acceptedPaths.contains(handler.context?.path.description ?? ""), file: file,
      line: line
    )
  }

  private func assertInitialTriggerDelivers(initializeOnSet: Bool) throws {
    let components = DivKitComponents(
      extensionHandlers: [handler],
      flagsInfo: DivFlagsInfo(initializeTriggerOnSet: initializeOnSet),
      reporter: reporter
    )
    let data = try DivData(dictionary: [
      "log_id": "initial", "states": [["state_id": 0, "div": target()]],
      "variable_triggers": [["condition": "@{true}", "actions": [commandDictionary()]]],
    ], context: ParsingContext())
    let view = DivView(divKitComponents: components)

    view.setSource(DivViewSource(kind: .divData(data), cardId: "extension_card"))
    components.flushUpdateActions()

    XCTAssertEqual(handler.commands, ["start"])
    withExtendedLifetime(view) {}
  }

  private func assertCrossViewAmbiguity(tooltipFirst: Bool) throws {
    let components = DivKitComponents(extensionHandlers: [handler], reporter: reporter)
    components.variablesStorage.set(cardId: "extension_card", variables: ["show": .integer(0)])
    let tooltipId = DivViewId(cardId: "extension_card", tooltip: .init(
      id: "hint", anchorPath: UIElementPath.parse("extension_card/0/anchor")
    ))
    let firstId = tooltipFirst ? tooltipId : DivViewId(cardId: "extension_card")
    let secondId = tooltipFirst ? DivViewId(cardId: "extension_card") : tooltipId
    let first = DivBlockProvider(
      id: firstId, divKitComponents: components, onCardSizeChanged: { _, _ in }
    )
    let second = DivBlockProvider(
      id: secondId, divKitComponents: components, onCardSizeChanged: { _, _ in }
    )
    let data = try makeData(root: [
      "type": "container", "id": "hidden", "items": [target()],
      "visibility": "@{show == 1 ? 'visible' : 'gone'}",
    ])
    let host = tooltipFirst ? second : first
    let tooltip = tooltipFirst ? first : second
    host.setSource(
      DivViewSource(kind: .divData(data), cardId: "extension_card"), debugParams: DebugParams()
    )
    tooltip.setSource(
      DivViewSource(kind: .divData(data), cardId: "extension_card", tooltip: tooltipId.tooltip),
      debugParams: DebugParams()
    )
    components.flushUpdateActions()

    components.variablesStorage.update(cardId: "extension_card", name: "show", value: "1")
    try sendDirectCommand(components: components)
    components.flushUpdateActions()

    XCTAssertEqual(handler.commands, [])
    XCTAssertEqual(
      reporter.lastError?.message,
      "Element with id 'target' is ambiguous"
    )
    withExtendedLifetime((first, second)) {}
  }

  private func makeData(root: [String: Any]) throws -> DivData {
    try DivData(
      dictionary: ["log_id": "direct", "states": [["state_id": 0, "div": root]]],
      context: ParsingContext()
    )
  }

  private func commandDictionary() -> [String: Any] {
    [
      "log_id": "command",
      "typed": ["type": "extension_action", "extension_id": "test", "div_id": "target"],
      "payload": ["type": "start"],
    ]
  }

  private func sendDirectCommand(components: DivKitComponents) throws {
    let action = try DivAction(dictionary: commandDictionary(), context: ParsingContext())
    components.actionHandler.handle(
      action, path: UIElementPath("extension_card"), source: .tap, sender: nil
    )
  }

  private func assertDynamicTargetIsAddressable(type: String) throws {
    var root = dynamicCollection(type: type)
    root["visibility"] = "visible"
    let card = makeCard(root: root)

    try card.send("start")

    XCTAssertEqual(handler.commands, ["start"])
    XCTAssertEqual(handler.paths, ["extension_card/0/collection/0/target"])
  }

  private func makeDeferredCard(siblings: [[String: Any]] = []) -> ExtensionActionTestCard {
    let components = DivKitComponents(extensionHandlers: [handler], reporter: reporter)
    components.variablesStorage.set(cardId: "extension_card", variables: ["show": .integer(0)])
    let hiddenChild: [String: Any] = siblings.isEmpty ? target() : [
      "type": "container", "id": "late_scope", "items": [target()],
    ]
    let hidden: [String: Any] = [
      "type": "container", "id": "hidden", "items": [hiddenChild],
      "visibility": "@{show == 1 ? 'visible' : 'gone'}",
    ]
    let root: [String: Any] = siblings.isEmpty ? hidden : [
      "type": "container", "items": [hidden] + siblings,
    ]
    let card = ExtensionActionTestCard(
      root: root, handlers: [handler], reporter: reporter, components: components
    )
    components.flushUpdateActions()
    return card
  }

  private func reveal(_ card: ExtensionActionTestCard) {
    card.components.variablesStorage.update(cardId: card.cardId, name: "show", value: "1")
    card.components.flushUpdateActions()
  }

  private func makeTooltip(
    card: ExtensionActionTestCard,
    root: [String: Any]? = nil
  ) async throws -> DivView {
    let div = try Div(dictionary: root ?? target(), context: ParsingContext())
    let view = await DivTooltipViewFactory(
      divKitComponents: card.components,
      cardId: card.cardId
    ).makeView(
      div: div,
      tooltip: DivViewId.Tooltip(
        id: "hint",
        anchorPath: UIElementPath.parse("extension_card/0/anchor")
      )
    )
    return try XCTUnwrap(view as? DivView)
  }

  private func makeCard(root: [String: Any]) -> ExtensionActionTestCard {
    ExtensionActionTestCard(root: root, handlers: [handler], reporter: reporter)
  }

  private func target() -> [String: Any] {
    ["type": "text", "id": "target", "text": "Target", "extensions": [["id": "test"]]]
  }

  private func dynamicCollection(type: String) -> [String: Any] {
    var collection: [String: Any] = [
      "type": type, "id": "collection", "visibility": "gone", "item_builder": [
        "data": [["name": "dynamic"]],
        "prototypes": [["id": "target", "div": target()]],
      ],
    ]
    if type == "pager" {
      collection["layout_mode"] = [
        "type": "percentage", "page_width": ["type": "percentage", "value": 100.0],
      ]
    }
    return collection
  }
}
