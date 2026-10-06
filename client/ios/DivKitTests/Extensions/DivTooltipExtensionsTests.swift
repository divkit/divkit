@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import VGSL
import XCTest

final class DivTooltipExtensionsTests: XCTestCase {
  func test_TooltipWithViewFactory_DoesNotModelContent() {
    let extensionHandler = CountingExtensionHandler()
    let context = DivKitComponents(extensionHandlers: [extensionHandler]).makeContext(
      cardId: "test_card_id",
      cachedImageHolders: []
    )
    let tooltips: [DivTooltip]? = [
      DivTooltip(
        div: divContainer(
          id: "tooltip_content",
          extensions: [DivExtension(id: extensionHandler.id, params: [:])]
        ),
        id: "tooltip1",
        position: .value(.center)
      ),
    ]

    let result = try! tooltips.makeTooltips(context: context)

    guard case .factory = result.first?.tooltip.viewSource else {
      XCTFail("Expected a factory view source")
      return
    }
    XCTAssertTrue(
      context.idToPath.paths(forId: "tooltip_content", cardId: "test_card_id").isEmpty
    )
    XCTAssertEqual(extensionHandler.acceptCallCount, 0)
  }

  func test_TooltipWithViewFactory_DoesNotExecuteVariableTriggers() {
    var triggerCount = 0
    let cardId = DivBlockModelingContext.testCardId
    let components = DivKitComponents(
      urlHandler: DivUrlHandlerDelegate { _, _ in
        triggerCount += 1
      }
    )
    components.variablesStorage.set(
      cardId: cardId,
      variables: ["should_trigger": .bool(true)]
    )
    let context = components.makeContext(cardId: cardId, cachedImageHolders: [])

    _ = makeBlock(
      divSeparator(
        tooltips: [
          DivTooltip(
            div: divContainer(
              id: "tooltip_content",
              variableTriggers: [
                DivTrigger(
                  actions: [
                    divAction(
                      logId: "tooltip_trigger",
                      url: "action://tooltip-trigger"
                    ),
                  ],
                  condition: expression("@{should_trigger}"),
                  mode: .value(.onCondition)
                ),
              ]
            ),
            id: "tooltip1",
            position: .value(.center)
          ),
        ]
      ),
      context: context
    )

    XCTAssertEqual(triggerCount, 0)
  }

  func test_TooltipWithoutViewFactory_ModelsContentAtLegacyPath() {
    let context = DivBlockModelingContext()
    let tooltips: [DivTooltip]? = [
      DivTooltip(
        div: divContainer(id: "tooltip_content"),
        id: "tooltip1",
        position: .value(.center)
      ),
    ]

    let result = try! tooltips.makeTooltips(context: context)

    guard case .block = result.first?.tooltip.viewSource else {
      XCTFail("Expected a block view source")
      return
    }
    XCTAssertEqual(
      context.idToPath.paths(forId: "tooltip_content", cardId: "test_card_id"),
      [UIElementPath("test_card_id") + "tooltip#tooltip1" + "tooltip_content"]
    )
  }

  func test_TooltipContentRefresh_KeepsHolderIdentity_AndDecoratingBlocksEqual() {
    let context = DivBlockModelingContext()
    func host(offset: DivPoint) -> Div {
      divSeparator(
        tooltips: [
          DivTooltip(
            div: divContainer(id: "tooltip_content"),
            id: "tooltip1",
            offset: offset,
            position: .value(.center)
          ),
        ]
      )
    }

    let block1 = makeBlock(host(offset: point(x: 0, y: 0)), context: context)
    let block2 = makeBlock(host(offset: point(x: 50, y: 50)), context: context)

    guard let holder1 = (block1.child as? DecoratingBlock)?.tooltips.first,
          let holder2 = (block2.child as? DecoratingBlock)?.tooltips.first else {
      XCTFail("Expected a DecoratingBlock with a tooltip")
      return
    }

    XCTAssertTrue(
      holder1 === holder2,
      "Remodeling the same tooltip anchor must reuse its content holder, not replace it"
    )
    XCTAssertEqual(holder2.tooltip.offset, CGPoint(x: 50, y: 50))
    XCTAssertTrue(
      block1.equals(block2),
      "A tooltip's content changing alone must not make DecoratingBlock.equals see a " +
        "difference - that's the whole point of routing tooltip content through a holder"
    )
  }

  func test_ResetCardId_DropsOnlyMatchingCardHolders() {
    let storage = DivTooltipContentStorage()
    let viewIdA = tooltipViewId(cardId: "card_a")
    let viewIdB = tooltipViewId(cardId: "card_b")

    let holderA = storage.holder(for: viewIdA) { makeMinimalTooltip() }
    let holderB = storage.holder(for: viewIdB) { makeMinimalTooltip() }

    storage.reset(cardId: "card_a")

    let holderAAfterReset = storage.holder(for: viewIdA) { makeMinimalTooltip() }
    let holderBAfterReset = storage.holder(for: viewIdB) { makeMinimalTooltip() }

    XCTAssertFalse(
      holderAAfterReset === holderA,
      "card_a's holder should have been dropped by reset(cardId: \"card_a\")"
    )
    XCTAssertTrue(
      holderBAfterReset === holderB,
      "card_b's holder must survive resetting card_a"
    )
  }

  func test_TwoAnchorsWithSameTooltipId_GetDistinctHolders() {
    let context = DivBlockModelingContext()
    let tooltips: [DivTooltip]? = [
      DivTooltip(div: divContainer(id: "content"), id: "hint", position: .value(.center)),
    ]

    let contextA = context.modifying(pathSuffix: "anchorA")
    let contextB = context.modifying(pathSuffix: "anchorB")

    let holderA = try! tooltips.makeTooltips(context: contextA).first
    let holderB = try! tooltips.makeTooltips(context: contextB).first

    XCTAssertFalse(
      holderA === holderB,
      "Different anchors must not share a tooltip holder just because their tooltip ids match " +
        "- div_tooltip_id is only required to be unique within one anchor"
    )
  }

  func test_Storage_DoesNotKeepHolderAliveOnItsOwn() {
    let storage = DivTooltipContentStorage()
    let viewId = tooltipViewId(cardId: "card")

    weak var weakHolder: TooltipContentHolder?
    do {
      let holder = storage.holder(for: viewId) { makeMinimalTooltip() }
      weakHolder = holder
    }

    // If the storage held its values strongly, this would still be non-nil here - that's exactly
    // the regression this test exists to catch (see DivTooltipContentStorage's retain-cycle note).
    XCTAssertNil(
      weakHolder,
      "The storage must not be the thing keeping a tooltip's content holder alive"
    )
  }

  func test_DivKitComponentsDoesNotLeakThroughTooltipContent() {
    weak var weakComponents: DivKitComponents?

    do {
      let components = DivKitComponents()
      weakComponents = components
      let cardId = DivBlockModelingContext.testCardId
      let context = components.makeContext(cardId: cardId, cachedImageHolders: [])
      _ = makeBlock(
        divSeparator(
          tooltips: [
            DivTooltip(
              div: divContainer(id: "tooltip_content"),
              id: "tooltip1",
              position: .value(.center)
            ),
          ]
        ),
        context: context
      )
    }

    XCTAssertNil(
      weakComponents,
      "DivKitComponents must not be kept alive by tooltip content it modeled - a tooltip's " +
        ".factory view source captures DivTooltipViewFactory, which holds DivKitComponents back"
    )
  }

  func test_SimpleTooltip() {
    let block = makeBlock(
      divSeparator(
        tooltips: [
          DivTooltip(
            div: divContainer(),
            duration: .value(1000),
            id: "tooltip1",
            offset: point(x: 10, y: 20),
            position: .value(.center)
          ),
        ]
      )
    )

    let expectedBlock = try! StateBlock(
      child: DecoratingBlock(
        child: SeparatorBlock(
          color: color("#14000000")
        ),
        tooltips: [
          TooltipContentHolder(tooltip: BlockTooltip(
            viewSource: .block(
              DecoratingBlock(
                child: ContainerBlock(
                  layoutDirection: .vertical,
                  children: [],
                  // The tooltip now hangs off its anchor, so its content sits below the
                  // separator that declares it rather than directly under the card.
                  path: UIElementPath("test_card_id") + "0" + "separator"
                    + "tooltip#tooltip1" + "container"
                ),
                accessibilityElement: .default
              ),
            ),
            params: BlockTooltipParams(
              id: "tooltip1",
              mode: .modal,
              duration: TimeInterval(milliseconds: 1000),
              closeByTapOutside: true,
              tapOutsideActions: []
            ),
            offset: CGPoint(x: 10, y: 20),
            position: .center,
            useLegacyWidth: false
          )),
        ],
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_NestedTooltip_IsIgnored() {
    let context = DivBlockModelingContext()

    let block = makeBlock(
      divSeparator(
        tooltips: [
          DivTooltip(
            div: divSeparator(
              tooltips: [
                DivTooltip(
                  div: divContainer(),
                  id: "nested_tooltip",
                  position: .value(.center)
                ),
              ]
            ),
            id: "tooltip1",
            position: .value(.center)
          ),
        ]
      ),
      context: context,
      ignoreErrors: true
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: SeparatorBlock(
          color: color("#14000000")
        ),
        tooltips: [
          TooltipContentHolder(tooltip: BlockTooltip(
            viewSource: .block(
              DecoratingBlock(
                child: SeparatorBlock(
                  color: color("#14000000")
                ),
                accessibilityElement: .default
              ),
            ),
            params: BlockTooltipParams(
              id: "tooltip1",
              mode: .modal,
              duration: TimeInterval(milliseconds: 5000),
              closeByTapOutside: true
            ),
            offset: CGPoint(x: 0, y: 0),
            position: .center,
            useLegacyWidth: false
          )),
        ],
        accessibilityElement: .default
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)

    let errors = context.errorsStorage.errors
    XCTAssertEqual(1, errors.count)
    XCTAssertEqual("Tooltip can not host another tooltips", errors.first?.message)
  }
}

/// Anchor path is arbitrary in these storage tests - they vary the card, not the anchor.
private func tooltipViewId(cardId: DivCardID) -> DivViewId {
  DivViewId(
    cardId: cardId,
    tooltip: DivViewId.Tooltip(id: "tooltip1", anchorPath: cardId.path + "anchor")
  )
}

private func makeMinimalTooltip(id: String = "tooltip") -> BlockTooltip {
  BlockTooltip(
    viewSource: .block(EmptyBlock.zeroSized),
    params: BlockTooltipParams(id: id, mode: .modal, duration: 0, closeByTapOutside: true),
    offset: .zero,
    position: .center
  )
}

private final class CountingExtensionHandler: DivExtensionHandler {
  let id = "counting_extension"
  private(set) var acceptCallCount = 0

  func accept(div _: DivBase, context _: DivBlockModelingContext) {
    acceptCallCount += 1
  }

  func applyBeforeBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }

  func applyAfterBaseProperties(
    to block: Block,
    div _: DivBase,
    context _: DivBlockModelingContext
  ) -> Block {
    block
  }
}
