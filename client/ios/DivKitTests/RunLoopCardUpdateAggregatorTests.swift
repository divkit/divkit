import BasePublic
@testable import DivKit
import XCTest

final class RunLoopCardUpdateAggregatorTests: XCTestCase {
  private var mainThreadBlock: Action?
  private var updateReasons: [DivActionURLHandler.UpdateReason]?

  private lazy var aggregator: RunLoopCardUpdateAggregator = .init(
    updateCardAction: { self.updateReasons = $0.asArray() },
    mainThreadAsyncRunner: { self.mainThreadBlock = $0 }
  )

  func test_WhenHasMultipleLocalVariablesChanges_MergesCardIds() {
    aggregator.aggregate(.variable(["first": []]))
    aggregator.aggregate(.variable(["second": []]))

    mainThreadBlock!()

    XCTAssertEqual([.variable(["first": [], "second": []])], updateReasons)
  }

  func test_WhenHasNoVariableChanges_DoesNotEmitVariableChange() {
    let change: DivActionURLHandler.UpdateReason = .state("cardId")
    aggregator.aggregate(change)

    mainThreadBlock!()

    XCTAssertEqual([change], updateReasons)
  }

  func test_WhenAggregateInMultipleRunLoops_SeparatesUpdateReasons() {
    let reasonBatches: [[DivActionURLHandler.UpdateReason]] = [
      [.state("cardId")],
      [.variable(["cardId": []])],
    ]

    for reasons in reasonBatches {
      reasons.forEach(aggregator.aggregate(_:))

      mainThreadBlock!()

      XCTAssertEqual(reasons, updateReasons)
    }
  }

  func test_FlushUpdateActions_RunActionsSynchronously() {
    aggregator.aggregate(.variable(["first": []]))
    aggregator.aggregate(.variable(["second": []]))

    aggregator.flushUpdateActions()

    XCTAssertEqual([.variable(["first": [], "second": []])], updateReasons)
  }

  func test_FlushUpdateActions_DoesNotRunActionsRepeatedly() {
    aggregator.aggregate(.variable(["cardId": []]))
    aggregator.flushUpdateActions()
    updateReasons = []

    mainThreadBlock!()

    XCTAssertEqual([], updateReasons)
  }

  func test_ForceUpdate_CallsUpdateCardAction() {
    updateReasons = []
    aggregator.forceUpdate()
    XCTAssertEqual([.external], updateReasons)
  }
}

extension DivActionURLHandler.UpdateReason: Equatable {
  public static func ==(
    lhs: DivActionURLHandler.UpdateReason,
    rhs: DivActionURLHandler.UpdateReason
  ) -> Bool {
    switch (lhs, rhs) {
    case let (.timer(lhsCardId), .timer(rhsCardId)):
      lhsCardId == rhsCardId
    case let (.variable(lhsVariables), .variable(rhsVariables)):
      lhsVariables == rhsVariables
    case let (.state(lhsCardId), .state(rhsCardId)):
      lhsCardId == rhsCardId
    case let (.patch(lhsCardId, lhsPatch), .patch(rhsCardId, rhsPatch)):
      lhsCardId == rhsCardId && lhsPatch == rhsPatch
    case (.external, .external):
      true
    case (.timer, _), (variable, _), (.state, _), (.patch, _), (.external, _):
      false
    }
  }
}
