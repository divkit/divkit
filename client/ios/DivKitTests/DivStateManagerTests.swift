@testable import DivKit

import XCTest

import LayoutKit
import VGSL

final class DivStateManagerTests: XCTestCase {
  private let stateManager = DivStateManager()
  private let transition = DivAppearanceTransition.divFadeTransition(DivFadeTransition())
  private let visibilityExpression: Expression<DivVisibility> = expression("@{visibility}")

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
      div: divSeparator(
        id: "blockId",
        transitionIn: transition,
        visibility: visibilityExpression
      ).value,
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
      div: divSeparator(
        id: "blockId",
        transitionIn: transition
      ).value,
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
      div: divSeparator(
        id: "blockId",
        visibility: visibilityExpression
      ).value,
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
      div: divSeparator(
        id: "blockId",
        transitionIn: transition,
        transitionTriggers: [.stateChange],
        visibility: visibilityExpression
      ).value,
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
      div: divSeparator(
        id: "blockId",
        transitionIn: transition,
        visibility: visibilityExpression
      ).value,
      isVisible: false
    )

    XCTAssertTrue(
      stateManager.shouldBlockAppearWithTransition(
        path: statePath + "blockId"
      )
    )
  }

  func test_settingStateToDivStateManager() {
    stateManager.setState(stateBlockPath: statePath, stateID: "first")
    XCTAssertEqual(stateManager.get(stateBlockPath: statePath)?.currentStateID, "first")
  }

  func test_settingStateToDivStateManagerByBinding() {
    let stateBinding = Binding<String>(
      name: "binding",
      value: Property<String>(initialValue: "second")
    )
    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)
    XCTAssertEqual(stateManager.get(stateBlockPath: statePath)?.currentStateID, "second")
  }

  func test_checkBindingChangedAfterSettingStateManually() {
    let stateBinding = Binding<String>(
      name: "binding",
      value: Property<String>(initialValue: "second")
    )
    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)
    stateManager.setState(stateBlockPath: statePath, stateID: "first")
    XCTAssertEqual(stateBinding.value, "first")
  }
}

private let statePath: DivStatePath = "0/state"
