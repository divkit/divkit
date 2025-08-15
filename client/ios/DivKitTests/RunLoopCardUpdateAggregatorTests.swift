@testable import DivKit
import VGSL
import XCTest

final class RunLoopCardUpdateAggregatorTests: XCTestCase {
  private var mainThreadBlock: (@MainActor () -> Void)!
  private var updateReasons: [DivCardUpdateReason] = []

  private lazy var aggregator = RunLoopCardUpdateAggregator(
    updateCardAction: { self.updateReasons = $0 },
    mainThreadAsyncRunner: { self.mainThreadBlock = $0 }
  )

  func test_WhenHasMultipleLocalVariablesChanges_MergesCardIds() {
    aggregator.aggregate(.variable(["first": []]))
    aggregator.aggregate(.variable(["second": []]))

    onMainThread {
      self.mainThreadBlock()
    }

    XCTAssertEqual([.variable(["first": [], "second": []])], updateReasons)
  }

  func test_WhenHasNoVariableChanges_DoesNotEmitVariableChange() {
    let change: DivCardUpdateReason = .state("cardId")
    aggregator.aggregate(change)

    onMainThread {
      self.mainThreadBlock()
    }

    XCTAssertEqual([change], updateReasons)
  }

  func test_WhenAggregateInMultipleRunLoops_SeparatesUpdateReasons() {
    let reasonBatches: [[DivCardUpdateReason]] = [
      [.state("cardId")],
      [.variable(["cardId": []])],
    ]

    for reasons in reasonBatches {
      reasons.forEach(aggregator.aggregate(_:))

      onMainThread {
        self.mainThreadBlock()
      }

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

    onMainThread {
      self.mainThreadBlock()
    }

    XCTAssertEqual([], updateReasons)
  }

  func test_ForceUpdate_CallsUpdateCardAction() {
    updateReasons = []
    aggregator.forceUpdate()
    XCTAssertEqual([.external], updateReasons)
  }
}

extension DivCardUpdateReason: Swift.Equatable {
  public static func ==(lhs: DivCardUpdateReason, rhs: DivCardUpdateReason) -> Bool {
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
