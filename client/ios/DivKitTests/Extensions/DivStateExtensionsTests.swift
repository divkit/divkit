@testable import DivKit
@testable import LayoutKit
import XCTest

final class DivStateExtensionsTests: XCTestCase {
  private let timerScheduler = TestTimerScheduler()

  func test_WhenVisibilityWasTriggeredAndStateChanges_ReportsVisibilityAgain() throws {
    let context = DivBlockModelingContext(scheduler: timerScheduler)

    let block = try makeBlock(fromFile: "states_visibility", context: context)
    let rect = CGRect(
      origin: .zero,
      size: CGSize(width: 100, height: block.intrinsicContentHeight(forWidth: 100))
    )
    let view = block.makeBlockView()
    XCTAssertEqual(
      getViewVisibilityCallCount(view: view, rect: rect, timerScheduler: timerScheduler),
      1
    )

    context.stateManager.setState(stateBlockPath: "mystate", stateID: "second")

    let block2 = try makeBlock(fromFile: "states_visibility", context: context)
    let view2 = block2.reuse(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: nil
    )
    XCTAssertEqual(
      getViewVisibilityCallCount(view: view2, rect: rect, timerScheduler: timerScheduler),
      1
    )
  }

  func test_WhenVisibilityWasTriggeredAndStateDoesNotChange_DoesNotReportVisibilityAgain() throws {
    let context = DivBlockModelingContext(scheduler: timerScheduler)

    let block = try makeBlock(fromFile: "states_visibility", context: context)
    let rect = CGRect(
      origin: .zero,
      size: CGSize(width: 100, height: block.intrinsicContentHeight(forWidth: 100))
    )
    let view = block.makeBlockView()
    XCTAssertEqual(
      getViewVisibilityCallCount(view: view, rect: rect, timerScheduler: timerScheduler),
      1
    )

    let block2 = try makeBlock(fromFile: "states_visibility", context: context)
    let view2 = block2.reuse(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: nil
    )
    XCTAssertEqual(
      getViewVisibilityCallCount(view: view2, rect: rect, timerScheduler: timerScheduler),
      0
    )
  }

  func test_Path_withDivId() throws {
    let block = makeBlock(
      divState(
        divId: "test_div_id",
        id: "test_id",
        states: defaultStates()
      )
    )

    let expectedPath = UIElementPath.root + 0 + "test_div_id" + "state_item_1" + "text"

    assertEqual(
      try block.firstStateElementPath(),
      expectedPath
    )
  }

  func test_Path_withId() throws {
    let block = makeBlock(
      divState(
        divId: nil,
        id: "test_id",
        states: defaultStates()
      )
    )

    assertEqual(
      try block.firstStateElementPath(),
      .root + 0 + "test_id" + "state_item_1" + "text"
    )
  }

  func test_Path_WithCustomContext() throws {
    let context = DivBlockModelingContext(
      cardId: "test_card_id"
    ).modifying(
      overridenId: "arbitrary_id"
    )

    let block = makeBlock(
      divState(
        divId: "test_div_id",
        id: "arbitrary_id",
        defaultStateId: .value("state_item_2"),
        states: defaultStates()
      ),
      context: context
    )

    assertEqual(
      try block.firstStateElementPath(),
      .root + 0 + "test_div_id" + "state_item_2" + "text"
    )
  }

  func test_Path_InsidePrototype() throws {
    let overridenPrototypeId = "test_prototype_id"
    let block = makeBlock(
      divContainer(
        itemBuilder: DivCollectionItemBuilder(
          data: .value([
            ["text": "Item 1"],
            ["text": "Item 2"],
          ]),
          prototypes: [
            DivCollectionItemBuilder.Prototype(
              div: divState(
                divId: "arbitrary_id",
                id: "arbitrary_id",
                states: defaultStates(textExpression: "@{getStringFromDict(it, 'text')}")
              ),
              id: .value(overridenPrototypeId)
            ),
          ]
        )
      )
    )
    let containerBlock: ContainerBlock = try block.child.unwrap()

    func getPathForText(index: Int) throws -> UIElementPath? {
      let wrapperBlock: StateBlock = try containerBlock.children[index].content.unwrap()
      let layeredBlock = try XCTUnwrap(wrapperBlock.child as? LayeredBlock)
      let textBlock: TextBlock = try layeredBlock.children[0].content.unwrap()
      return textBlock.path
    }

    assertEqual(
      try getPathForText(index: 0),
      .root + 0 + "container" + 0 + "test_prototype_id" + "state_item_1" + "text"
    )
    assertEqual(
      try getPathForText(index: 1),
      .root + 0 + "container" + 1 + "test_prototype_id" + "state_item_1" + "text"
    )
  }
}

private func defaultStates(
  textExpression: String? = nil
) -> [DivState.State] {
  [
    divStateState(
      div: divText(
        textExpression: textExpression,
        height: .divFixedSize(.init(value: .value(100)))
      ),
      stateId: "state_item_1"
    ),
    divStateState(
      div: divText(
        textExpression: textExpression
      ),
      stateId: "state_item_2"
    ),
  ]
}

extension StateBlock {
  fileprivate func firstStateElementPath() throws -> UIElementPath? {
    let stateBlock: StateBlock = try child.unwrap()
    let layeredBlock = try XCTUnwrap(stateBlock.child as? LayeredBlock)
    let textBlock: TextBlock = try layeredBlock.children[0].content.unwrap()
    return textBlock.path
  }
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext = .default
) throws -> Block {
  try DivStateTemplate.make(
    fromFile: filename,
    subdirectory: "div-state",
    context: context
  )
}
