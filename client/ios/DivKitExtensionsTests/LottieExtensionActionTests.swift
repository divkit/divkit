@preconcurrency @testable import DivKit
import DivKitExtensions
import DivKitTestsSupport
import LayoutKit
import Serialization
import XCTest

@MainActor
final class LottieExtensionActionTests: XCTestCase {
  private let factory = RecordingLottieFactory()
  private let reporter = MockReporter()

  func test_StartRewindsConfiguredPlayback() async throws {
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    view.animationProgress = 0.5

    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertTrue(view.isPlaying)
  }

  func test_RepeatedStartRewindsActionPlayback() async throws {
    let card = makeCard()
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    view.animationProgress = 0.5

    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertTrue(view.isPlaying)
  }

  func test_StopRewindsPlayingAnimation() async throws {
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    view.animationProgress = 0.5

    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertFalse(view.isPlaying)
  }

  func test_RepeatedStopKeepsFirstFrameStopped() async throws {
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertFalse(view.isPlaying)
  }

  func test_StartAfterStopBeginsAtFirstFrame() async throws {
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertTrue(view.isPlaying)
  }

  func test_LastCommandBeforeLoadingWins() async throws {
    factory.holdSource = true
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.sourceReceived], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    view.completeSource()
    await fulfillment(of: [view.configured], timeout: 2)

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertFalse(view.isPlaying)
  }

  func test_DeferredStartPlaysWhenSourceIsReady() async throws {
    factory.holdSource = true
    let card = makeCard()
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.sourceReceived], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    view.completeSource()
    await fulfillment(of: [view.configured], timeout: 2)

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertTrue(view.isPlaying)
  }

  func test_CompletedStartReplaysAfterViewRecreation() async throws {
    let card = makeCard()
    let first = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [first.configured], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    first.finish(progress: 1)
    card.setRoot(animation(visibility: "gone"))

    card.setRoot(animation())
    let recreated = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [recreated.configured], timeout: 2)

    XCTAssertFalse(recreated === first)
    XCTAssertEqual(recreated.animationProgress, 0)
    XCTAssertTrue(recreated.isPlaying)
  }

  func test_ConfiguredPlaybackCanRestartAfterNaturalCompletion() async throws {
    let card = makeCard(isPlaying: true, repeatCount: 1)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    view.finish(progress: 1)

    card.setRoot(animation(isPlaying: false, repeatCount: 1))
    card.setRoot(animation(isPlaying: true, repeatCount: 1))

    XCTAssertTrue(view.isPlaying)
  }

  func test_NewSourcePlaysAfterConfiguredPlaybackCompletes() async throws {
    let card = makeCard(isPlaying: true, repeatCount: 1)
    let first = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [first.configured], timeout: 2)
    first.finish(progress: 1)

    card.setRoot(animation(isPlaying: true, repeatCount: 1, frameRate: 60))
    let replacement = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [replacement.configured], timeout: 2)

    XCTAssertFalse(replacement === first)
    XCTAssertTrue(replacement.isPlaying)
  }

  func test_StopSurvivesViewRecreationAndConfiguredAutoplay() async throws {
    let card = makeCard(isPlaying: true)
    let first = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [first.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    card.setRoot(animation(visibility: "gone"))

    card.setRoot(animation(isPlaying: true))
    let recreated = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [recreated.configured], timeout: 2)

    XCTAssertFalse(recreated === first)
    XCTAssertEqual(recreated.animationProgress, 0)
    XCTAssertFalse(recreated.isPlaying)
  }

  func test_HiddenTargetStartsWhenRevealed() async throws {
    let card = makeCard(visibility: "gone")
    XCTAssertTrue(factory.views.isEmpty)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    card.setRoot(animation())
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)

    XCTAssertTrue(view.isPlaying)
    XCTAssertEqual(view.animationProgress, 0)
  }

  func test_QueuedCommandsWaitForTargetAndResourceBeforeOverridingAutoplay() async throws {
    factory.holdSource = true
    let lottieHandler = LottieExtensionHandler(
      factory: factory, requester: MockURLResourceRequester()
    )
    let components = DivKitComponents(extensionHandlers: [lottieHandler], reporter: reporter)
    components.variablesStorage.set(cardId: "extension_card", variables: ["show": .integer(0)])
    let card = ExtensionActionTestCard(
      root: [
        "type": "container", "id": "container",
        "visibility": "@{show == 1 ? 'visible' : 'gone'}",
        "items": [animation(isPlaying: true)],
      ],
      handlers: [lottieHandler],
      reporter: reporter,
      components: components
    )
    components.flushUpdateActions()
    XCTAssertTrue(factory.views.isEmpty)

    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    components.variablesStorage.update(cardId: card.cardId, name: "show", value: "1")
    components.flushUpdateActions()
    card.view.layoutIfNeeded()
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.sourceReceived], timeout: 2)
    view.completeSource()
    await fulfillment(of: [view.configured], timeout: 2)

    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertFalse(view.isPlaying)
    XCTAssertFalse(reporter.errors.contains { $0.message.contains("Unsupported action") })
  }

  func test_UnknownCommandDoesNotStartAnimation() async throws {
    let card = makeCard()
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)

    try card.send("resume", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertFalse(view.isPlaying)
    XCTAssertEqual(reporter.lastError?.message, "Unsupported action for extension 'lottie'")
  }

  func test_LegacyViewKeepsConfiguredPlaybackAfterStopAction() async throws {
    factory.supportsActions = false
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [view.configured], timeout: 2)

    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertTrue(view.isPlaying)
  }

  func test_LegacyViewReportsEachStopAction() async throws {
    // Arrange
    factory.supportsActions = false
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [view.configured], timeout: 2)
    let wasLoggingEnabled = DivKitLogger.isEnabled
    var errors: [String] = []
    DivKitLogger.isEnabled = true
    DivKitLogger.setLogger { message in errors.append(message) }
    defer {
      DivKitLogger.isEnabled = wasLoggingEnabled
      DivKitLogger.setLogger { _ in }
    }

    // Act
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    // Assert
    XCTAssertEqual(errors, [
      "Lottie view must implement LottiePlaybackControlling for extension actions",
      "Lottie view must implement LottiePlaybackControlling for extension actions",
    ])
  }

  func test_HandlerReplacementStillControlsVisibleAnimation() async throws {
    // Arrange
    let card = makeCard()
    let view = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [view.configured], timeout: 2)
    card.components.extensionHandlers = [LottieExtensionHandler(
      factory: factory, requester: MockURLResourceRequester()
    )]

    // Act
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    // Assert
    XCTAssertTrue(view.isPlaying)
  }

  func test_ResetCardClearsRemovedHandlerState() async throws {
    // Arrange
    let card = makeCard()
    let originalHandlers = card.components.extensionHandlers
    let view = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    card.components.extensionHandlers = []

    // Act
    card.components.reset(cardId: card.cardId)
    card.components.extensionHandlers = originalHandlers
    card.setRoot(animation(isPlaying: true))

    // Assert
    XCTAssertTrue(view.isPlaying)
  }

  func test_ResetAllClearsHandlerFromPreviousModel() async throws {
    // Arrange
    let card = makeCard()
    let originalHandlers = card.components.extensionHandlers
    let view = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    card.components.extensionHandlers = []
    card.setRoot(["type": "text", "text": "Replacement"])

    // Act
    card.components.reset()
    card.components.extensionHandlers = originalHandlers
    card.setRoot(animation(isPlaying: true))
    let recreated = try XCTUnwrap(factory.views.last)
    await fulfillment(of: [recreated.configured], timeout: 2)

    // Assert
    XCTAssertTrue(recreated.isPlaying)
  }

  func test_ClosingTooltipPreservesStoppedStateOfTooltipAndHost() async throws {
    // Arrange
    let card = makeCard()
    let originalHandlers = card.components.extensionHandlers
    var root = animation(isPlaying: true)
    root["id"] = "host_animation"
    card.setRoot(root)
    let host = try XCTUnwrap(factory.views.last)
    await fulfillment(of: [host.configured], timeout: 2)
    let tooltip = try await makeAnimationTooltip(card: card)
    let first = try XCTUnwrap(factory.views.last)
    await fulfillment(of: [first.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie", targetId: "host_animation")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    card.components.extensionHandlers = []

    // Act
    tooltip.tooltipDidClose()
    card.components.extensionHandlers = originalHandlers
    let reopened = try await makeAnimationTooltip(card: card)
    let current = try XCTUnwrap(factory.views.last)
    await fulfillment(of: [current.configured], timeout: 2)

    // Assert
    XCTAssertFalse(current.isPlaying)
    XCTAssertFalse(host.isPlaying)
    withExtendedLifetime((tooltip, reopened)) {}
  }

  func test_ResetCardClearsStoppedState() async throws {
    let card = makeCard()
    let first = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [first.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    card.components.reset(cardId: card.cardId)
    card.setRoot(animation(isPlaying: true))

    XCTAssertTrue(first.isPlaying)
  }

  func test_DisabledExtensionAppliesCommandWhenEnabled() async throws {
    var root = animation()
    var extensions = try XCTUnwrap(root["extensions"] as? [[String: Any]])
    extensions[0]["is_enabled"] = false
    root["extensions"] = extensions
    let card = ExtensionActionTestCard(
      root: root,
      handlers: [LottieExtensionHandler(factory: factory, requester: MockURLResourceRequester())],
      reporter: reporter
    )
    XCTAssertTrue(factory.views.isEmpty)

    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    card.setRoot(animation())
    let view = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)

    XCTAssertTrue(view.isPlaying)
    XCTAssertEqual(view.animationProgress, 0)
  }

  func test_CommandDoesNotAffectAnotherCard() async throws {
    let firstCard = makeCard(isPlaying: true)
    let otherCard = makeCard(isPlaying: true, cardId: "other", components: firstCard.components)
    let first = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    let other = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [first.configured, other.configured], timeout: 2)

    try firstCard.send("stop", extensionId: "lottie")
    firstCard.components.flushUpdateActions()
    firstCard.view.layoutIfNeeded()

    XCTAssertFalse(first.isPlaying)
    XCTAssertTrue(other.isPlaying)
    withExtendedLifetime(otherCard) {}
  }

  func test_CommandDoesNotAffectAnotherRuntimePath() async throws {
    let card = makeCard()
    card.setRoot(["type": "container", "id": "root", "items": [
      ["type": "container", "id": "left", "items": [animation(isPlaying: true)]],
      ["type": "container", "id": "right", "items": [animation(isPlaying: true)]],
    ]])
    let views = factory.views.suffix(2)
    let first = try XCTUnwrap(views.first as? RecordingLottieView)
    let other = try XCTUnwrap(views.last as? RecordingLottieView)
    await fulfillment(of: [first.configured, other.configured], timeout: 2)

    try card.send("stop", extensionId: "lottie", scopeId: "left")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertFalse(first.isPlaying)
    XCTAssertTrue(other.isPlaying)
  }

  func test_LateSourceCannotChangeRecreatedStoppedView() async throws {
    factory.holdSource = true
    let card = makeCard(isPlaying: true)
    let oldView = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [oldView.sourceReceived], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    card.setRoot(animation(visibility: "gone"))
    factory.holdSource = false
    card.setRoot(animation(isPlaying: true))
    let currentView = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [currentView.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    oldView.completeSource()
    await fulfillment(of: [oldView.sourceFinished], timeout: 2)

    XCTAssertFalse(currentView === oldView)
    XCTAssertFalse(currentView.isPlaying)
    XCTAssertEqual(currentView.animationProgress, 0)
  }

  func test_CompletedAnimationDoesNotRestartAfterBlockUpdate() async throws {
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    view.finish(progress: 1)

    card.setRoot(animation(isPlaying: false))
    card.setRoot(animation(isPlaying: true))

    XCTAssertEqual(view.animationProgress, 1)
    XCTAssertFalse(view.isPlaying)
  }

  func test_ResetAllClearsStoppedState() async throws {
    let card = makeCard()
    let first = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [first.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    card.components.reset()
    card.setRoot(animation(isPlaying: true))

    XCTAssertTrue(first.isPlaying)
  }

  func test_StartRewindsReversePlaybackWithOriginalRepeatConfiguration() async throws {
    let card = makeCard(repeatMode: "reverse", repeatCount: 3)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    view.animationProgress = 0.5

    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    XCTAssertEqual(factory.repeatModes, [.reverse])
    XCTAssertEqual(factory.repeatCounts, [3])
    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertTrue(view.isPlaying)
  }

  func test_CompletedReverseStartReplaysAfterViewRecreation() async throws {
    let card = makeCard(repeatMode: "reverse", repeatCount: 1)
    let first = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [first.configured], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    first.finish(progress: 0)
    card.setRoot(animation(visibility: "gone"))

    card.setRoot(animation(isPlaying: true, repeatMode: "reverse", repeatCount: 1))
    let recreated = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [recreated.configured], timeout: 2)

    XCTAssertEqual(recreated.animationProgress, 0)
    XCTAssertTrue(recreated.isPlaying)
  }

  func test_ResetPreservesStoppedStateOfOtherCard() async throws {
    // Arrange
    let card = makeCard(isPlaying: true)
    let otherCard = makeCard(isPlaying: true, cardId: "other", components: card.components)
    let other = try XCTUnwrap(factory.views.last as? RecordingLottieView)
    await fulfillment(of: [other.configured], timeout: 2)
    try otherCard.send("stop", extensionId: "lottie")
    otherCard.components.flushUpdateActions()
    otherCard.view.layoutIfNeeded()

    // Act
    card.components.reset(cardId: card.cardId)
    otherCard.setRoot(animation(isPlaying: true))

    // Assert
    XCTAssertFalse(other.isPlaying)
  }

  func test_ClosingTooltipPreservesStoppedStateBeforeReopening() async throws {
    // Arrange
    let card = makeCard()
    card.setRoot(["type": "text", "id": "anchor", "text": "Host"])
    let tooltip = try await makeAnimationTooltip(card: card)
    let first = try XCTUnwrap(factory.views.last)
    await fulfillment(of: [first.configured], timeout: 2)
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    // Act
    tooltip.tooltipDidClose()
    let reopened = try await makeAnimationTooltip(card: card)
    let current = try XCTUnwrap(factory.views.last)
    await fulfillment(of: [current.configured], timeout: 2)

    // Assert
    XCTAssertFalse(current.isPlaying)
    withExtendedLifetime((tooltip, reopened)) {}
  }

  func test_CommandExpressionUsesSourceVariables() async throws {
    // Arrange
    let card = makeCard()
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    card.components.variablesStorage.append(
      variables: ["command": .string("start")], for: card.cardId
    )
    card.components.flushUpdateActions()

    // Act
    try card.send("@{command}", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    // Assert
    XCTAssertTrue(view.isPlaying)
    XCTAssertEqual(view.animationProgress, 0)
  }

  func test_UnresolvedCommandExpressionDoesNotStartAnimation() async throws {
    let card = makeCard()
    let view = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [view.configured], timeout: 2)

    try card.send("@{missing}", extensionId: "lottie")

    XCTAssertFalse(view.isPlaying)
    XCTAssertEqual(reporter.lastError?.message, "Unsupported action for extension 'lottie'")
  }

  func test_CommandBurstAppliesLastCommandWithoutReloadingSource() async throws {
    // Arrange
    let card = makeCard(isPlaying: true)
    let view = try XCTUnwrap(factory.views.first as? RecordingLottieView)
    await fulfillment(of: [view.configured], timeout: 2)
    view.animationProgress = 0.5

    // Act
    try card.send("start", extensionId: "lottie")
    try card.send("start", extensionId: "lottie")
    try card.send("stop", extensionId: "lottie")
    card.components.flushUpdateActions()
    // Delivering the retained command schedules the state update that applies it to the view.
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()

    // Assert
    XCTAssertFalse(view.isPlaying)
    XCTAssertEqual(view.animationProgress, 0)
    XCTAssertEqual(view.sourceCount, 1)
  }

  func test_ResetRestoresConfiguredPauseOnExistingView() async throws {
    // Arrange
    let card = makeCard()
    let view = try XCTUnwrap(factory.views.first)
    await fulfillment(of: [view.configured], timeout: 2)
    try card.send("start", extensionId: "lottie")
    card.components.flushUpdateActions()
    card.view.layoutIfNeeded()
    XCTAssertTrue(view.isPlaying)

    // Act
    card.components.reset(cardId: card.cardId)
    card.setRoot(animation(isPlaying: false))

    // Assert
    XCTAssertFalse(view.isPlaying)
  }

  private func makeAnimationTooltip(card: ExtensionActionTestCard) async throws -> DivView {
    let div = try Div(dictionary: animation(isPlaying: true), context: ParsingContext())
    let view = await DivTooltipViewFactory(
      divKitComponents: card.components,
      cardId: card.cardId
    ).makeView(
      div: div,
      tooltip: DivViewId.Tooltip(
        id: "hint", anchorPath: UIElementPath.parse("extension_card/0/anchor")
      )
    )
    return try XCTUnwrap(view as? DivView)
  }

  private func makeCard(
    isPlaying: Bool = false,
    visibility: String = "visible",
    repeatMode: String = "restart",
    repeatCount: Int = 0,
    cardId: String = "extension_card",
    components: DivKitComponents? = nil
  ) -> ExtensionActionTestCard {
    ExtensionActionTestCard(
      root: animation(
        isPlaying: isPlaying,
        visibility: visibility,
        repeatMode: repeatMode,
        repeatCount: repeatCount
      ),
      handlers: [LottieExtensionHandler(factory: factory, requester: MockURLResourceRequester())],
      reporter: reporter,
      cardId: cardId,
      components: components
    )
  }

  private func animation(
    isPlaying: Bool = false,
    visibility: String = "visible",
    repeatMode: String = "restart",
    repeatCount: Int = 0,
    frameRate: Int = 30
  ) -> [String: Any] {
    [
      "type": "gif", "id": "target", "visibility": visibility,
      "width": ["type": "fixed", "value": 100],
      "height": ["type": "fixed", "value": 100],
      "extensions": [["id": "lottie", "params": [
        "lottie_json": ["v": "5.5.7", "fr": frameRate, "ip": 0, "op": 120, "w": 100, "h": 100],
        "is_playing": isPlaying,
        "repeat_mode": repeatMode,
        "repeat_count": repeatCount,
      ]]],
    ]
  }
}
