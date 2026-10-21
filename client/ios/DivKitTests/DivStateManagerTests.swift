@testable import DivKit
import DivKitTestsSupport
import LayoutKit
import VGSL
import XCTest

final class DivStateManagerTests: XCTestCase {
  private let stateManager = DivStateManager()
  private let transition = DivAppearanceTransition.divFadeTransition(DivFadeTransition())
  private let visibilityExpression: DivKit.Expression<DivVisibility> = expression("@{visibility}")

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
    XCTAssertEqual(stateManager.get(stateBlockPath: statePath)?.previousState, .empty)
    registerBlock(id: "secondTitle", stateID: "second")
    XCTAssertFalse(stateManager.isBlockAdded("secondTitle", stateBlockPath: statePath))
  }

  func test_changingBoundStateAddsOnlyNewBlocks() {
    let stateBinding = Binding<String>(
      name: "binding",
      value: Property<String>(initialValue: "first")
    )
    registerBlock(id: "firstTitle", stateID: "first")
    registerBlock(id: "secondTitle", stateID: "second")
    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)

    stateBinding.value = "second"
    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)

    XCTAssertTrue(stateManager.isBlockAdded("secondTitle", stateBlockPath: statePath))
    XCTAssertFalse(stateManager.isBlockAdded("firstTitle", stateBlockPath: statePath))
    XCTAssertEqual(stateManager.get(stateBlockPath: statePath)?.previousState, .withID("first"))

    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)
    XCTAssertEqual(stateManager.get(stateBlockPath: statePath)?.previousState, .withID("first"))

    stateManager.setState(stateBlockPath: statePath, stateID: "second")
    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)
    XCTAssertFalse(stateManager.isBlockAdded("secondTitle", stateBlockPath: statePath))
  }

  func test_bindingPreservesExplicitStateTransition() {
    stateManager.setStateWithHistory(path: statePath, stateID: "second")
    let stateBinding = Binding<String>(
      name: "binding",
      value: Property<String>(initialValue: "second")
    )

    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)
    registerBlock(id: "secondTitle", stateID: "second")

    XCTAssertEqual(stateManager.get(stateBlockPath: statePath)?.previousState, .initial)
    XCTAssertTrue(stateManager.isBlockAdded("secondTitle", stateBlockPath: statePath))
  }

  func test_bindingPreservesRestoredStateTransition() {
    let stateManager = DivStateManager(items: [
      statePath: DivStateManager.Item(currentStateID: "second", previousState: .initial),
    ])
    let stateBinding = Binding<String>(
      name: "binding",
      value: Property<String>(initialValue: "second")
    )

    stateManager.setState(stateBlockPath: statePath, stateBinding: stateBinding)

    XCTAssertEqual(stateManager.get(stateBlockPath: statePath)?.previousState, .initial)
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

  private func registerBlock(id: String, stateID: DivStateID) {
    stateManager.updateBlockIdsWithStateChangeTransition(
      statePath: statePath + stateID,
      div: divSeparator(id: id, transitionIn: transition, transitionTriggers: [.stateChange])
    )
  }
}

private let statePath: DivStatePath = "0/state"
