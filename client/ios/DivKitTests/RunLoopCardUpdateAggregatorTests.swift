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

  func test_WhenHasLocalAndGlobalVariablesChanges_ReturnsAllAffectedCards() {
    aggregator.aggregate(.variable(.specific(["cardId"])))
    aggregator.aggregate(.variable(.all))

    mainThreadBlock!()

    XCTAssertEqual([.variable(.all)], updateReasons)
  }

  func test_WhenHasMultipleLocalVariablesChanges_MergesCardIds() {
    aggregator.aggregate(.variable(.specific(["first"])))
    aggregator.aggregate(.variable(.specific(["second"])))

    mainThreadBlock!()

    XCTAssertEqual([.variable(.specific(["first", "second"]))], updateReasons)
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
      [.variable(.specific(["cardId"]))],
    ]

    reasonBatches.forEach { reasons in
      reasons.forEach(aggregator.aggregate(_:))

      mainThreadBlock!()

      XCTAssertEqual(reasons, updateReasons)
    }
  }
}

extension DivActionURLHandler.UpdateReason: Equatable {
  public static func ==(
    lhs: DivActionURLHandler.UpdateReason,
    rhs: DivActionURLHandler.UpdateReason
  ) -> Bool {
    switch (lhs, rhs) {
    case let (.timer(lhsCardId), .timer(rhsCardId)):
      return lhsCardId == rhsCardId
    case let (.variable(lhsVariables), .variable(rhsVariables)):
      return lhsVariables == rhsVariables
    case let (.state(lhsCardId), .state(rhsCardId)):
      return lhsCardId == rhsCardId
    case let (.patch(lhsCardId, lhsPatch), .patch(rhsCardId, rhsPatch)):
      return lhsCardId == rhsCardId && lhsPatch == rhsPatch
    case (.timer, _), (variable, _), (.state, _), (.patch, _):
      return false
    }
  }
}
