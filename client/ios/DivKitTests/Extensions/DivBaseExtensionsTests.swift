@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

final class DivBaseExtensionsTests: XCTestCase {
  private let timer = TestTimerScheduler()

  func test_WithId() {
    let block = makeBlock(
      divSeparator(
        id: "id1"
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: separatorBlock(),
        accessibilityElement: accessibility(identifier: "id1")
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithMarginsAndPaddings() {
    let block = makeBlock(
      divText(
        margins: DivEdgeInsets(bottom: .value(10), top: .value(10)),
        paddings: DivEdgeInsets(left: .value(20), right: .value(20)),
        text: "Hello!"
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: DecoratingBlock(
          child: textBlock(
            text: "Hello!",
            path: .root + 0 + "text"
          ),
          paddings: EdgeInsets(horizontal: 20),
          accessibilityElement: accessibility(
            traits: .staticText,
            label: "Hello!"
          )
        ),
        boundary: .noClip,
        paddings: EdgeInsets(vertical: 10)
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAccessibility() {
    let block = makeBlock(
      divSeparator(
        accessibility: DivAccessibility(
          description: .value("Accessibility description"),
          type: .button
        ),
        id: "id1"
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: separatorBlock(),
        accessibilityElement: accessibility(
          traits: .button,
          label: "Accessibility description",
          identifier: "id1"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_MarginsOverAccessibility() {
    let block = makeBlock(
      divSeparator(
        accessibility: DivAccessibility(
          description: .value("Accessibility description"),
          type: .button
        ),
        margins: DivEdgeInsets(bottom: .value(10), top: .value(10))
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: DecoratingBlock(
          child: separatorBlock(),
          accessibilityElement: accessibility(
            traits: .button,
            label: "Accessibility description"
          )
        ),
        boundary: .noClip,
        paddings: EdgeInsets(vertical: 10)
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithActions() throws {
    let context = DivBlockModelingContext.default
    let actions = [
      divAction(
        logId: "action1_log_id",
        url: "https://some.url"
      ),
      divAction(
        logId: "action2_log_id",
        typed: .divActionSetVariable(DivActionSetVariable(
          value: .integerValue(IntegerValue(value: .value(10))),
          variableName: .value("var1")
        ))
      ),
    ]

    let block = makeBlock(
      divContainer(actions: actions)
    )

    let expectedBlock = try StateBlock(
      child: DecoratingBlock(
        child: ContainerBlock(
          layoutDirection: .vertical,
          children: [],
          path: UIElementPath("test_card_id") + "0" + "container"
        ),
        actions: NonEmptyArray(actions.compactMap {
          $0.uiAction(
            context: context
              .modifying(pathSuffix: "0")
              .modifying(pathSuffix: "container")
          )
        }),
        actionAnimation: .default,
        accessibilityElement: .default,
        path: .root + 0 + "container"
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_MarginsOverActions() {
    let action = divAction(logId: "action_log_id")
    let block = makeBlock(
      divSeparator(
        actions: [action],
        margins: DivEdgeInsets(bottom: .value(10), top: .value(10))
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: DecoratingBlock(
          child: separatorBlock(),
          actions: NonEmptyArray(action.uiAction(pathSuffix: "0/separator")!),
          actionAnimation: .default,
          accessibilityElement: .default,
          path: .root + 0 + "separator"
        ),
        boundary: .noClip,
        paddings: EdgeInsets(vertical: 10),
        path: nil
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithBorderAndActions() {
    let action = divAction(logId: "action_log_id")
    let block = makeBlock(
      divSeparator(
        actions: [action],
        border: DivBorder(
          cornerRadius: .value(20),
          stroke: DivStroke(
            color: .value(color("#112233")),
            width: .value(2)
          )
        )
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: separatorBlock(),
        actions: NonEmptyArray(action.uiAction(pathSuffix: "0/separator")!),
        actionAnimation: .default,
        boundary: .clipCorner(CornerRadii(20)),
        border: BlockBorder(
          color: color("#112233"),
          width: 2
        ),
        accessibilityElement: .default,
        path: .root + 0 + "separator"
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithShadowActionsAndMargins() {
    let action = divAction(
      logId: "action_log_id",
      url: "https://some.url"
    )
    let block = makeBlock(
      divSeparator(
        actions: [action],
        border: DivBorder(
          hasShadow: .value(true),
          shadow: DivShadow(
            blur: .value(5),
            color: .value(color("#112233")),
            offset: DivPoint(
              x: DivDimension(value: .value(10)),
              y: DivDimension(value: .value(20))
            )
          )
        ),
        margins: DivEdgeInsets(bottom: .value(10), top: .value(10))
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: DecoratingBlock(
          child: ShadedBlock(
            block: DecoratingBlock(
              child: separatorBlock(),
              accessibilityElement: .default,
              path: nil
            ),
            shadow: BlockShadow(
              cornerRadii: CornerRadii(0),
              blurRadius: 5,
              offset: CGPoint(x: 10, y: 20),
              color: color("#112233")
            )
          ),
          actions: NonEmptyArray(action.uiAction(pathSuffix: "0/separator")!),
          actionAnimation: .default,
          boundary: .noClip,
          path: .root + 0 + "separator"
        ),
        boundary: .noClip,
        paddings: EdgeInsets(vertical: 10),
        path: nil
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithVisibility_Gone() {
    let block = makeBlock(
      divSeparator(
        visibility: .value(.gone)
      )
    )

    let expectedBlock = StateBlock(
      child: EmptyBlock.zeroSized,
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithVisibility_Invisible() {
    let block = makeBlock(
      divSeparator(
        visibility: .value(.invisible)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: separatorBlock(),
        childAlpha: 0.0
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAlpha() {
    let block = makeBlock(
      divSeparator(
        alpha: 0.3
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: separatorBlock(),
        childAlpha: 0.3,
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithBackground() {
    let block = makeBlock(
      divSeparator(
        background: solidBackground(RGBAColor.red)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: separatorBlock(),
        backgroundColor: RGBAColor.red,
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAlphaAndBackground() {
    let block = makeBlock(
      divSeparator(
        alpha: 0.3,
        background: solidBackground(RGBAColor.red)
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: DecoratingBlock(
          child: separatorBlock(),
          backgroundColor: RGBAColor.red,
          accessibilityElement: .default
        ),
        childAlpha: 0.3
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithBackgroundAndActions() {
    let action = divAction(logId: "action_log_id")
    let block = makeBlock(
      divSeparator(
        actions: [action],
        background: solidBackground(color("#112233"))
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: separatorBlock(),
        backgroundColor: color("#112233"),
        actions: NonEmptyArray(action.uiAction(pathSuffix: "0/separator")!),
        actionAnimation: .default,
        accessibilityElement: .default,
        path: .root + 0 + "separator"
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithLocalVariables() {
    let block = makeBlock(
      divText(
        textExpression: "@{local_var}",
        variables: [variable("local_var", "Hello!")]
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: TextBlock(
          widthTrait: .resizable,
          text: "Hello!".withTypo(),
          verticalAlignment: .leading,
          accessibilityElement: nil,
          path: .root + 0 + "text"
        ),
        accessibilityElement: accessibility(
          traits: .staticText,
          label: "Hello!"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WhenCreatesBlockAfterItBeingGone_ReportsVisibility() async throws {
    try await expectVisibilityActionsToRun(
      forVisibleBlockFile: "div-text-visibility-actions-visible",
      invisibleBlockFile: "div-text-visibility-actions-gone"
    )
  }

  func test_WhenCreatesBlockAfterItBeingInvisible_ReportsVisibility() async throws {
    try await expectVisibilityActionsToRun(
      forVisibleBlockFile: "div-text-visibility-actions-visible",
      invisibleBlockFile: "div-text-visibility-actions-invisible"
    )
  }

  func test_WhenReusesBlockWithAnotherDivCardID_ExpectToCancelPreviousBlockTimers() throws {
    let context = DivBlockModelingContext(scheduler: timer)

    let firstBlock = try makeBlock(
      fromFile: "div-text-visibility-actions-visible",
      context: context
    )

    // trigger visibility actions for first time
    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    let view = firstBlock.makeBlockView()
    view.frame = rect
    view.layoutIfNeeded()
    view.onVisibleBoundsChanged(from: rect, to: rect)

    let oldTimers = timer.timers

    let secondBlock = try makeBlock(
      fromFile: "div-text-visibility-actions-visible",
      context: DivBlockModelingContext(cardId: "another_card_id")
    )
    let view2 = secondBlock.reuse(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: nil
    )
    view2.frame = rect
    view2.layoutIfNeeded()
    view.onVisibleBoundsChanged(from: rect, to: rect)

    XCTAssertEqual(oldTimers.allSatisfy { !$0.isValid }, true)
  }

  @MainActor
  func test_WhenBlockHasVisibilityAndDisappearAction_DoNotShareVisibilityCounter() async throws {
    let context = DivBlockModelingContext(scheduler: timer)

    let blockVisibleFirst = try makeBlock(
      fromFile: "div-text-visibility-and-disappear-action",
      context: context
    )

    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    let view = blockVisibleFirst.makeBlockView()
    var result = try await getViewVisibilityCallCount(
      view: view,
      rect: rect,
      timerScheduler: timer
    )

    XCTAssertEqual(result, 1)

    let invisibleRect: CGRect = .zero
    result = try await getViewVisibilityCallCount(
      view: view,
      rect: rect,
      visibilityRect: invisibleRect,
      timerScheduler: timer
    )

    XCTAssertEqual(result, 1)
  }

  @MainActor
  func test_WhenBlockHasVisibilityAndDisappearAction_WithCommonIds_DoNotShareVisibilityCounter(
  ) async throws {
    let context = DivBlockModelingContext(scheduler: timer)
    let commonLogId = "common_id"

    let block = makeBlock(
      divText(
        disappearActions: [
          DivDisappearAction(
            disappearDuration: .value(0),
            logId: .value(commonLogId),
            logLimit: .value(4)
          ),
        ],
        text: "arbitary text",
        visibilityActions: [
          DivVisibilityAction(
            logId: .value(commonLogId),
            logLimit: .value(2),
            visibilityDuration: .value(0)
          ),
        ]
      ),
      context: context
    )

    let visibilityTester = VisibilityTester(block: block, timer: timer)
    try await visibilityTester.setViewVisibleAndDisappear(repeatCount: 10)

    XCTAssertEqual(visibilityTester.callsCount, 6)
    XCTAssertEqual(visibilityTester.callsCount(type: .appear), 2)
    XCTAssertEqual(visibilityTester.callsCount(type: .disappear), 4)
  }

  @MainActor
  func test_WhenBlockHasVisibilityActions_WithCommonIds_ShareCounterAndAddWarning() async throws {
    let context = DivBlockModelingContext(scheduler: timer)
    let appearId = "appear_id"

    let block = makeBlock(
      divText(
        text: "arbitary text",
        visibilityActions: [
          DivVisibilityAction(
            logId: .value(appearId),
            logLimit: .value(1),
            visibilityDuration: .value(0)
          ),
          DivVisibilityAction(
            logId: .value(appearId),
            logLimit: .value(2),
            url: .value(testURL),
            visibilityDuration: .value(0)
          ),
          DivVisibilityAction(
            logId: .value(appearId),
            logLimit: .value(3),
            visibilityDuration: .value(0)
          ),
        ]
      ),
      context: context,
      ignoreErrors: true
    )

    let visibilityTester = VisibilityTester(block: block, timer: timer)
    try await visibilityTester.setViewVisibleAndDisappear(repeatCount: 20)

    XCTAssertEqual(visibilityTester.callsCount(type: .appear), 3)

    let appearTestURLActionsCount = visibilityTester.callsCount(
      type: .appear,
      url: testURL
    )
    XCTAssertEqual(appearTestURLActionsCount, 1)

    assertEqual(
      context.errorsStorage.errors,
      [
        DivBlockModelingWarning(
          "appear actions array contains non-unique log_id values: [Optional(\"appear_id\")]",
          path: .root + 0 + "text"
        ),
      ]
    )
  }

  @MainActor
  func test_WhenBlockHasDisappearActions_WithCommonIds_ShareCounterAndAddWarning() async throws {
    let context = DivBlockModelingContext(scheduler: timer)
    let disappearId = "dissappear_id"

    let block = makeBlock(
      divText(
        disappearActions: [
          DivDisappearAction(
            disappearDuration: .value(0),
            logId: .value(disappearId),
            logLimit: .value(3),
            url: .value(testURL)
          ),
          DivDisappearAction(
            disappearDuration: .value(0),
            logId: .value(disappearId),
            logLimit: .value(1)
          ),
        ],
        text: "arbitary text"
      ),
      context: context,
      ignoreErrors: true
    )

    let visibilityTester = VisibilityTester(block: block, timer: timer)
    try await visibilityTester.setViewVisibleAndDisappear(repeatCount: 20)

    XCTAssertEqual(visibilityTester.callsCount(type: .disappear), 3)

    let disappearTestURLActionsCount = visibilityTester.callsCount(
      type: .disappear,
      url: testURL
    )
    XCTAssertEqual(disappearTestURLActionsCount, 3)

    assertEqual(
      context.errorsStorage.errors,
      [
        DivBlockModelingWarning(
          "disappear actions array contains non-unique log_id values: [Optional(\"dissappear_id\")]",
          path: .root + 0 + "text"
        ),
      ]
    )
  }

  func test_WithBackroundReuseId() throws {
    let context = DivBlockModelingContext()
    let block = try makeBlock(fromFile: "div-reuse-id-background-wrapper", context: context)

    XCTAssertEqual(block.reuseId, testReuseId)
  }

  func test_WithLottieExtensionReuseId() throws {
    let block = try makeBlock(
      fromFile: "div-reuse-id-lottie-wrapper",
      context: DivBlockModelingContext()
    )

    XCTAssertEqual(block.reuseId, testReuseId)
  }

  func test_WithPagerItemsReuseIds() throws {
    let block = try DivPagerTemplate.makeBlock(
      fromFile: "div-reuse-id-pager-items",
      context: DivBlockModelingContext()
    ) as! DecoratingBlock
    let pagerBlock = block.child as! PagerBlock

    let itemBlocks = pagerBlock.gallery.items.map(\.content)
    for (index, itemBlock) in itemBlocks.enumerated() {
      XCTAssertEqual(itemBlock.reuseId, testReuseId + String(index))
    }
  }

  @MainActor
  private func expectVisibilityActionsToRun(
    forVisibleBlockFile file: String,
    invisibleBlockFile: String
  ) async throws {
    let context = DivBlockModelingContext(scheduler: timer)

    let blockVisibleFirst = try makeBlock(fromFile: file, context: context)

    // trigger visibility actions for first time
    let rect = CGRect(origin: .zero, size: CGSize(squareDimension: 20))
    let view = blockVisibleFirst.makeBlockView()
    var result = try await getViewVisibilityCallCount(
      view: view,
      rect: rect,
      timerScheduler: timer
    )

    XCTAssertEqual(result, 1)

    // expect to drop lastVisibleBounds
    _ = try makeBlock(fromFile: invisibleBlockFile, context: context)
    let view2 = blockVisibleFirst.makeBlockView()
    result = try await getViewVisibilityCallCount(
      view: view2,
      rect: rect,
      timerScheduler: timer
    )

    XCTAssertEqual(result, 1)
  }
}

private func makeBlock(
  fromFile filename: String,
  context: DivBlockModelingContext
) throws -> Block {
  try DivTextTemplate.makeBlock(fromFile: filename, context: context)
}

private func assertEqual(_ actual: [DivError], _ expected: [DivError]) {
  assertEqual(
    actual.map(\.prettyMessage),
    expected.map(\.prettyMessage)
  )
}

extension TemplateValue where ResolvedValue: DivBlockModeling {
  fileprivate static func makeBlock(
    fromFile filename: String,
    context: DivBlockModelingContext
  ) throws -> Block {
    try make(
      fromFile: filename,
      subdirectory: "div-base",
      context: context
    )
  }
}

private let testReuseId = "test_reuse_id"
private let testURL = URL(string: "https://ya.ru")!
