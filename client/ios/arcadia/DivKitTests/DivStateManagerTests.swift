// Copyright 2022 Yandex LLC. All rights reserved.

@testable import DivKit

import XCTest

import CommonCore

final class DivStateManagerTests: XCTestCase {
  private let stateManager = DivStateManager()

  private let transition = DivAppearanceTransition.divFadeTransition(DivFadeTransition())

  private let visibilityExpression = Expression<DivVisibility>.link(
    ExpressionLink(rawValue: "@{visibility}", validator: nil)!
  )

  func test_shouldBlockAppearWithTransition_isFalseForNewBlock() {
    XCTAssertFalse(
      stateManager.shouldBlockAppearWithTransition(
        path: DivBlockPath(rawValue: "root/blockId")
      )
    )
  }

  func test_shouldBlockAppearWithTransition_isFalseForVisibleBlock() {
    let statePath = DivStatePath(rawValue: "root")
    stateManager.setBlockVisibility(
      statePath: statePath,
      div: makeDivSeparator(
        id: "blockId",
        transitionIn: transition,
        visibility: visibilityExpression
      ),
      isVisible: true
    )

    XCTAssertFalse(
      stateManager.shouldBlockAppearWithTransition(
        path: statePath + "blockId"
      )
    )
  }

  func test_shouldBlockAppearWithTransition_isFalseForInvisibleBlockWithConstantVisibility() {
    let statePath = DivStatePath(rawValue: "root")
    stateManager.setBlockVisibility(
      statePath: statePath,
      div: makeDivSeparator(
        id: "blockId",
        transitionIn: transition
      ),
      isVisible: false
    )

    XCTAssertFalse(
      stateManager.shouldBlockAppearWithTransition(
        path: statePath + "blockId"
      )
    )
  }

  func test_shouldBlockAppearWithTransition_isFalseForBlockWithoutTransitions() {
    let statePath = DivStatePath(rawValue: "root")
    stateManager.setBlockVisibility(
      statePath: statePath,
      div: makeDivSeparator(
        id: "blockId",
        visibility: visibilityExpression
      ),
      isVisible: false
    )

    XCTAssertFalse(
      stateManager.shouldBlockAppearWithTransition(
        path: statePath + "blockId"
      )
    )
  }

  func test_shouldBlockAppearWithTransition_isFalseForBlockWithWrongTransitionTrigger() {
    let statePath = DivStatePath(rawValue: "root")
    stateManager.setBlockVisibility(
      statePath: statePath,
      div: makeDivSeparator(
        id: "blockId",
        transitionIn: transition,
        transitionTriggers: [.stateChange],
        visibility: visibilityExpression
      ),
      isVisible: false
    )

    XCTAssertFalse(
      stateManager.shouldBlockAppearWithTransition(
        path: statePath + "blockId"
      )
    )
  }

  func test_shouldBlockAppearWithTransition_isTrueForInvisibleBlock() {
    let statePath = DivStatePath(rawValue: "root")
    stateManager.setBlockVisibility(
      statePath: statePath,
      div: makeDivSeparator(
        id: "blockId",
        transitionIn: transition,
        visibility: visibilityExpression
      ),
      isVisible: false
    )

    XCTAssertTrue(
      stateManager.shouldBlockAppearWithTransition(
        path: statePath + "blockId"
      )
    )
  }
}
