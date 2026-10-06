#if os(iOS)
import Foundation
@testable import LayoutKit
import Testing
import UIKit
import VGSL

@MainActor
@Suite
struct TooltipStatesStoreTests {
  private let identity = TooltipIdentity(id: "tooltip_id", scopePath: nil)

  // MARK: show

  @Test
  func show_noRecord_startsShow() {
    let storage = Storage()

    #expect(storage.states.requestShow(storage.makeRequest().request) == .start)
    #expect(storage.states.phase(of: identity) == .showing)
  }

  @Test
  func show_whileShowing_isRejected() {
    let storage = Storage()
    storage.enterShowing()

    #expect(storage.states.requestShow(storage.makeRequest().request) == .rejected)
    #expect(storage.states.phase(of: identity) == .showing)
  }

  @Test
  func show_whileHidingWithoutNext_isQueued() {
    let storage = Storage()
    storage.enterHiding()

    #expect(storage.states.requestShow(storage.makeRequest().request) == .queued)
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: true))
  }

  @Test
  func show_whileHidingWithNext_isRejectedAndKeepsTheQueuedRequest() {
    let storage = Storage()
    let queued = storage.enterHiding(withNext: true)

    #expect(storage.states.requestShow(storage.makeRequest().request) == .rejected)
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: true))

    let outcome = storage.states.requestHide(identity)
    outcome.cancelledRequest?.completion(false)
    #expect(queued?.results == [false])
  }

  @Test
  func show_whileVisible_isRejected() {
    let storage = Storage()
    storage.enterVisible()

    #expect(storage.states.requestShow(storage.makeRequest().request) == .rejected)
    #expect(storage.states.phase(of: identity) == .visible)
  }

  // MARK: hide

  @Test
  func hide_noRecord_doesNothing() {
    let storage = Storage()

    let outcome = storage.states.requestHide(identity)

    #expect(outcome.view == nil)
    #expect(outcome.cancelledRequest == nil)
    #expect(storage.states.phase(of: identity) == nil)
  }

  @Test
  func hide_whileShowing_discardsTheRunningShow() {
    let storage = Storage()
    storage.enterShowing()

    let outcome = storage.states.requestHide(identity)

    #expect(outcome.view == nil)
    #expect(outcome.cancelledRequest == nil)
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: false))
  }

  @Test
  func hide_whileHidingWithoutNext_doesNothing() {
    let storage = Storage()
    storage.enterHiding()

    let outcome = storage.states.requestHide(identity)

    #expect(outcome.view == nil)
    #expect(outcome.cancelledRequest == nil)
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: false))
  }

  @Test
  func hide_whileHidingWithNext_cancelsTheRequest() {
    let storage = Storage()
    let queued = storage.enterHiding(withNext: true)

    let outcome = storage.states.requestHide(identity)
    outcome.cancelledRequest?.completion(false)

    #expect(outcome.view == nil)
    #expect(queued?.results == [false])
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: false))
  }

  @Test
  func hide_whileVisible_removesRecordAndReturnsViewToClose() {
    let storage = Storage()
    let view = storage.enterVisible()

    let outcome = storage.states.requestHide(identity)

    #expect(outcome.view === view)
    #expect(outcome.cancelledRequest == nil)
    #expect(storage.states.phase(of: identity) == nil)
  }

  // MARK: reset

  @Test
  func reset_noRecord_doesNothing() {
    let storage = Storage()

    let outcome = storage.states.beginReset()

    #expect(outcome.views.isEmpty)
    #expect(outcome.cancelledRequests.isEmpty)
  }

  @Test
  func reset_whileShowing_discardsTheRunningShow() {
    let storage = Storage()
    storage.enterShowing()

    let outcome = storage.states.beginReset()

    #expect(outcome.views.isEmpty)
    #expect(outcome.cancelledRequests.isEmpty)
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: false))
  }

  @Test
  func reset_whileHidingWithoutNext_doesNothing() {
    let storage = Storage()
    storage.enterHiding()

    let outcome = storage.states.beginReset()

    #expect(outcome.views.isEmpty)
    #expect(outcome.cancelledRequests.isEmpty)
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: false))
  }

  @Test
  func reset_whileHidingWithNext_cancelsTheRequest() {
    let storage = Storage()
    let queued = storage.enterHiding(withNext: true)

    storage.states.reset()

    #expect(queued?.results == [false])
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: false))
  }

  @Test
  func reset_whileVisible_removesRecordAndReturnsViewToClose() {
    let storage = Storage()
    let view = storage.enterVisible()

    let outcome = storage.states.beginReset()

    #expect(outcome.views.count == 1)
    #expect(outcome.views.first === view)
    #expect(storage.states.phase(of: identity) == nil)
  }

  // MARK: a show returned a view

  @Test
  func finishShow_whileShowing_displaysAndKeepsShowing() {
    let storage = Storage()
    storage.enterShowing()

    guard case .display = storage.states.finishShow(identity) else {
      Issue.record("Expected the view to be displayed")
      return
    }
    #expect(storage.states.phase(of: identity) == .showing)
  }

  @Test
  func finishShow_whileHidingWithoutNext_discardsAndClearsRecord() {
    let storage = Storage()
    storage.enterHiding()

    guard case let .discard(next) = storage.states.finishShow(identity) else {
      Issue.record("Expected the content to be discarded")
      return
    }
    #expect(next == nil)
    #expect(storage.states.phase(of: identity) == nil)
  }

  @Test
  func finishShow_whileHidingWithNext_discardsAndHandsOverNext() {
    let storage = Storage()
    let queued = storage.enterHiding(withNext: true)

    guard case let .discard(next) = storage.states.finishShow(identity) else {
      Issue.record("Expected the content to be discarded")
      return
    }
    next?.completion(true)
    #expect(queued?.results == [true])
    #expect(storage.states.phase(of: identity) == .showing)
  }

  @Test
  func markVisible_whileShowing_becomesVisible() {
    let storage = Storage()
    storage.enterShowing()

    guard case .display = storage.states.markVisible(identity, view: makeContainer()) else {
      Issue.record("Expected the view to become visible")
      return
    }
    #expect(storage.states.phase(of: identity) == .visible)
  }

  @Test
  func markVisible_whenShowIsNoLongerWanted_discardsLikeFinishShow() {
    let storage = Storage()
    let queued = storage.enterHiding(withNext: true)

    guard case let .discard(next) = storage.states.markVisible(identity, view: makeContainer())
    else {
      Issue.record("Expected the content to be discarded")
      return
    }
    next?.completion(true)
    #expect(queued?.results == [true])
    #expect(storage.states.phase(of: identity) == .showing)
  }

  @Test
  func markVisible_withoutShow_discardsAndChangesNothing() {
    let storage = Storage()

    guard case let .discard(next) = storage.states.markVisible(identity, view: makeContainer())
    else {
      Issue.record("Expected the content to be discarded")
      return
    }
    #expect(next == nil)
    #expect(storage.states.phase(of: identity) == nil)

    storage.enterVisible()
    guard case .discard = storage.states.markVisible(identity, view: makeContainer()) else {
      Issue.record("Expected the content to be discarded")
      return
    }
    #expect(storage.states.phase(of: identity) == .visible)
  }

  // MARK: a show failed

  @Test
  func failShow_whileShowing_clearsRecord() {
    let storage = Storage()
    storage.enterShowing()

    #expect(storage.states.failShow(identity) == nil)
    #expect(storage.states.phase(of: identity) == nil)
  }

  @Test
  func failShow_whileHidingWithoutNext_clearsRecord() {
    let storage = Storage()
    storage.enterHiding()

    #expect(storage.states.failShow(identity) == nil)
    #expect(storage.states.phase(of: identity) == nil)
  }

  @Test
  func failShow_whileHidingWithNext_handsOverNext() {
    let storage = Storage()
    let queued = storage.enterHiding(withNext: true)

    let next = storage.states.failShow(identity)
    next?.completion(true)

    #expect(queued?.results == [true])
    #expect(storage.states.phase(of: identity) == .showing)
  }

  // MARK: remove(ifShowing:)

  @Test
  func removeIfShowing_noRecord_acceptsTheClosingView() {
    let storage = Storage()
    let view = makeContainer()

    #expect(storage.states.remove(identity, ifShowing: view) === view)
    #expect(storage.states.phase(of: identity) == nil)
  }

  @Test
  func removeIfShowing_whileShowingOrHiding_acceptsTheClosingViewWithoutTouchingTheShow() {
    let storage = Storage()
    let closingView = makeContainer()

    storage.enterShowing()
    #expect(storage.states.remove(identity, ifShowing: closingView) === closingView)
    #expect(storage.states.phase(of: identity) == .showing)

    _ = storage.states.requestHide(identity)
    #expect(storage.states.remove(identity, ifShowing: closingView) === closingView)
    #expect(storage.states.phase(of: identity) == .hiding(hasNext: false))
  }

  @Test
  func removeIfShowing_doesNotRemoveReplacementView() {
    let storage = Storage()
    let first = makeContainer()
    storage.enterVisible(first)
    _ = storage.states.requestHide(identity)
    let replacement = storage.enterVisible()

    #expect(storage.states.remove(identity, ifShowing: first) == nil)
    #expect(storage.states.phase(of: identity) == .visible)
    #expect(storage.states.remove(identity, ifShowing: replacement) === replacement)
    #expect(storage.states.phase(of: identity) == nil)
  }

  @Test
  func hasOpenModals_countsOnlyVisibleTooltips() {
    let storage = Storage()
    storage.enterShowing()
    #expect(!storage.states.hasOpenModals)

    storage.states.reset()
    storage.states.reset()
    #expect(!storage.states.hasOpenModals)

    _ = storage.states.failShow(identity)
    storage.enterVisible()
    #expect(storage.states.hasOpenModals)
  }
}

@Suite
struct TooltipStatesConcurrencyTests {
  @Test
  func concurrentShows_onlyOneStarts() {
    let states = DefaultTooltipManager.TooltipStates()
    let identity = TooltipIdentity(id: "tooltip_id", scopePath: nil)
    let request = DefaultTooltipManager.TooltipStates.ShowRequest(
      info: TooltipInfo(id: identity.id, showsOnStart: false, multiple: false),
      completion: { _ in }
    )

    var results = [Bool](repeating: false, times: 10)
    DispatchQueue.concurrentPerform(iterations: 10) { index in
      results[index] = states.requestShow(request) == .start
    }

    #expect(results.filter(\.self).count == 1)
    #expect(states.phase(of: identity) == .showing)
  }
}

@MainActor
private final class Storage {
  typealias States = DefaultTooltipManager.TooltipStates

  final class Recorder {
    private(set) var results = [Bool]()

    func record(_ result: Bool) {
      results.append(result)
    }
  }

  let states = States()

  private let identity = TooltipIdentity(id: "tooltip_id", scopePath: nil)

  func makeRequest() -> (request: States.ShowRequest, recorder: Recorder) {
    let recorder = Recorder()
    let request = States.ShowRequest(
      info: TooltipInfo(id: identity.id, showsOnStart: false, multiple: false),
      completion: { recorder.record($0) }
    )
    return (request, recorder)
  }

  func enterShowing() {
    #expect(states.requestShow(makeRequest().request) == .start)
  }

  @discardableResult
  func enterHiding(withNext: Bool = false) -> Recorder? {
    enterShowing()
    _ = states.requestHide(identity)
    guard withNext else {
      return nil
    }
    let queued = makeRequest()
    #expect(states.requestShow(queued.request) == .queued)
    return queued.recorder
  }

  @discardableResult
  func enterVisible(_ view: TooltipContainerView? = nil) -> TooltipContainerView {
    let view = view ?? makeContainer()
    enterShowing()
    guard case .display = states.finishShow(identity),
          case .display = states.markVisible(identity, view: view) else {
      Issue.record("Expected the show to be displayed")
      return view
    }
    return view
  }
}

@MainActor
private func makeContainer() -> TooltipContainerView {
  TooltipContainerView(
    tooltip: DefaultTooltipManager.Tooltip(
      params: BlockTooltipParams(
        id: "tooltip_id",
        mode: .modal,
        duration: 0,
        closeByTapOutside: true
      ),
      view: MockTooltipView(),
      substrateView: nil,
      bringToTopId: nil
    ),
    handleAction: { _ in },
    onCloseAction: {},
    getViewById: { _ in nil }
  )
}

private final class MockTooltipView: UIView, VisibleBoundsTrackingLeaf {}
#endif
