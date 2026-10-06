@testable import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing
import VGSL

@Suite
struct DivAnimatorControllerTests {
  private let cardId: DivCardID = "card"
  private let hostViewId = DivViewId(cardId: "card")
  private let otherViewId = DivViewId(cardId: "other_card")
  private let tooltipViewId = DivViewId(
    cardId: "card",
    tooltip: DivViewId.Tooltip(id: "hint", anchorPath: UIElementPath("card") + "anchor")
  )

  @Test
  func resetViewId_stopsOnlyRunningAnimatorsAndRemovesAllEntries() {
    let controller = DivAnimatorController()
    let running = MockAnimator(id: "running", isRunning: true)
    let idle = MockAnimator(id: "idle", isRunning: false)
    register(controller, viewId: tooltipViewId, id: "running", animator: running)
    register(controller, viewId: tooltipViewId, id: "idle", animator: idle)

    controller.reset(viewId: tooltipViewId)

    #expect(running.stopCount == 1)
    #expect(idle.stopCount == 0)
    #expect(controller.definition(path: tooltipViewId.path, id: "running") == nil)
    #expect(controller.definition(path: tooltipViewId.path, id: "idle") == nil)
  }

  @Test
  func resetViewId_host_leavesTooltipAndOtherCardUntouched() {
    let controller = DivAnimatorController()
    let fixture = registerHostTooltipAndOtherCard(in: controller)

    controller.reset(viewId: hostViewId)

    #expect(fixture.host.stopCount == 1)
    #expect(fixture.tooltip.stopCount == 0)
    #expect(fixture.other.stopCount == 0)
    #expect(controller.definition(path: hostViewId.path, id: "host") == nil)
    #expect(controller.definition(path: tooltipViewId.path, id: "tooltip") != nil)
    #expect(controller.definition(path: otherViewId.path, id: "other") != nil)
  }

  @Test
  func resetViewId_tooltip_leavesHostAndOtherCardUntouched() {
    let controller = DivAnimatorController()
    let fixture = registerHostTooltipAndOtherCard(in: controller)

    controller.reset(viewId: tooltipViewId)

    #expect(fixture.host.stopCount == 0)
    #expect(fixture.tooltip.stopCount == 1)
    #expect(fixture.other.stopCount == 0)
    #expect(controller.definition(path: hostViewId.path, id: "host") != nil)
    #expect(controller.definition(path: tooltipViewId.path, id: "tooltip") == nil)
    #expect(controller.definition(path: otherViewId.path, id: "other") != nil)
  }

  @Test
  func resetCardId_dropsHostAndItsTooltips_leavesOtherCardUntouched() {
    let controller = DivAnimatorController()
    let fixture = registerHostTooltipAndOtherCard(in: controller)

    controller.reset(cardId: cardId)

    #expect(fixture.host.stopCount == 1)
    #expect(fixture.tooltip.stopCount == 1)
    #expect(fixture.other.stopCount == 0)
    #expect(controller.definition(path: hostViewId.path, id: "host") == nil)
    #expect(controller.definition(path: tooltipViewId.path, id: "tooltip") == nil)
    #expect(controller.definition(path: otherViewId.path, id: "other") != nil)
  }

  @Test
  func resetViewId_hostAnimatorWithTooltipPrefixedId_belongsToHost() {
    let controller = DivAnimatorController()
    let hostAnimator = MockAnimator(id: "tooltip#hint", isRunning: true)
    let tooltipAnimator = MockAnimator(id: "tooltip", isRunning: true)
    register(controller, viewId: hostViewId, id: "tooltip#hint", animator: hostAnimator)
    register(controller, viewId: tooltipViewId, id: "tooltip", animator: tooltipAnimator)

    controller.reset(viewId: hostViewId)

    // The animator's own id is free-form: it must not make the host's entry look like a
    // tooltip's, nor pull the host's reset onto the tooltip.
    #expect(hostAnimator.stopCount == 1)
    #expect(tooltipAnimator.stopCount == 0)
    #expect(controller.definition(path: hostViewId.path, id: "tooltip#hint") == nil)
    #expect(controller.definition(path: tooltipViewId.path, id: "tooltip") != nil)
  }

  @Test
  func reset_stopsOnlyRunningAnimatorsAndRemovesAllEntries() {
    let controller = DivAnimatorController()
    let running = MockAnimator(id: "running", isRunning: true)
    let idle = MockAnimator(id: "idle", isRunning: false)
    register(controller, viewId: hostViewId, id: "running", animator: running)
    register(controller, viewId: tooltipViewId, id: "idle", animator: idle)

    controller.reset()

    #expect(running.stopCount == 1)
    #expect(idle.stopCount == 0)
    #expect(controller.definition(path: hostViewId.path, id: "running") == nil)
    #expect(controller.definition(path: tooltipViewId.path, id: "idle") == nil)
  }

  @Test
  func resetViewId_reentrantStartFromStop_doesNotDeadlock() {
    let controller = DivAnimatorController()
    let hostAnimator = MockAnimator(id: "host", isRunning: false)
    register(controller, viewId: hostViewId, id: "host", animator: hostAnimator)

    let tooltipAnimator = MockAnimator(id: "tooltip", isRunning: true)
    tooltipAnimator.onStop = { [controller] in
      _ = controller.startAnimator(
        path: hostViewId.path,
        id: "host",
        startValue: nil,
        endValue: nil,
        duration: nil,
        startDelay: nil,
        direction: nil,
        progressInterpolator: nil,
        repeatCount: nil
      )
    }
    register(controller, viewId: tooltipViewId, id: "tooltip", animator: tooltipAnimator)

    controller.reset(viewId: tooltipViewId)

    #expect(tooltipAnimator.stopCount == 1)
    #expect(hostAnimator.startCount == 1)
  }

  @Test
  func stopAnimator_reentrantStopFromOnStop_doesNotDeadlock() {
    let controller = DivAnimatorController()
    let second = MockAnimator(id: "second", isRunning: true)
    register(controller, viewId: hostViewId, id: "second", animator: second)

    let first = MockAnimator(id: "first", isRunning: true)
    first.onStop = { [controller] in
      _ = controller.stopAnimator(path: hostViewId.path, id: "second")
    }
    register(controller, viewId: tooltipViewId, id: "first", animator: first)

    #expect(controller.stopAnimator(path: tooltipViewId.path, id: "first") == .success)
    #expect(first.stopCount == 1)
    #expect(second.stopCount == 1)
  }

  @Test
  func stopAnimator_forNotRunningAnimator_stillCallsStop() {
    let controller = DivAnimatorController()
    let idle = MockAnimator(id: "idle", isRunning: false)
    register(controller, viewId: hostViewId, id: "idle", animator: idle)

    #expect(controller.stopAnimator(path: hostViewId.path, id: "idle") == .success)
    #expect(idle.stopCount == 1)
  }

  private func registerHostTooltipAndOtherCard(
    in controller: DivAnimatorController
  ) -> (host: MockAnimator, tooltip: MockAnimator, other: MockAnimator) {
    let host = MockAnimator(id: "host", isRunning: true)
    let tooltip = MockAnimator(id: "tooltip", isRunning: true)
    let other = MockAnimator(id: "other", isRunning: true)
    register(controller, viewId: hostViewId, id: "host", animator: host)
    register(controller, viewId: tooltipViewId, id: "tooltip", animator: tooltip)
    register(controller, viewId: otherViewId, id: "other", animator: other)
    return (host, tooltip, other)
  }

  private func register(
    _ controller: DivAnimatorController,
    viewId: DivViewId,
    id: String,
    animator: MockAnimator
  ) {
    controller.initializeIfNeeded(
      path: viewId.path,
      id: id,
      definition: .divNumberAnimator(
        DivNumberAnimator(
          duration: .value(100),
          endValue: .value(1),
          id: id,
          variableName: "value"
        )
      ),
      animator: Variable.constant(animator as Animator)
    )
  }
}

private final class MockAnimator: Animator {
  let id: String
  var isRunning: Bool
  private(set) var stopCount = 0
  private(set) var startCount = 0
  var onStop: (() -> Void)?

  init(id: String, isRunning: Bool) {
    self.id = id
    self.isRunning = isRunning
  }

  func start(
    startValue _: DivVariableValue?,
    endValue _: DivVariableValue?,
    duration _: Int?,
    startDelay _: Int?,
    direction _: AnimationDirection?,
    progressInterpolator _: ProgressInterpolator?,
    repeatCount _: RepeatCount?
  ) {
    startCount += 1
    isRunning = true
  }

  func stop() {
    stopCount += 1
    onStop?()
    isRunning = false
  }
}
