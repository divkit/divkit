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

    guard case .factory = result.first?.viewSource else {
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

    guard case .block = result.first?.viewSource else {
      XCTFail("Expected a block view source")
      return
    }
    XCTAssertEqual(
      context.idToPath.paths(forId: "tooltip_content", cardId: "test_card_id"),
      [UIElementPath("test_card_id") + "tooltip#tooltip1" + "tooltip_content"]
    )
  }

  func test_FactoryTooltip_WithSameDiv_KeepsBlocksEqual() {
    let context = makeFactoryContext()
    let div = divText(text: "a")

    XCTAssertTrue(
      makeBlock(tooltipHost(div), context: context)
        .equals(makeBlock(tooltipHost(div), context: context))
    )
  }

  func test_FactoryTooltip_WithNewDiv_MakesBlocksDiffer() {
    let context = makeFactoryContext()

    XCTAssertFalse(
      makeBlock(tooltipHost(divText(text: "a")), context: context)
        .equals(makeBlock(tooltipHost(divText(text: "a")), context: context))
    )
  }

  func test_FactoryTooltip_WithNewSubstrate_MakesBlocksDiffer() {
    let context = makeFactoryContext()
    let div = divText(text: "a")

    XCTAssertFalse(
      makeBlock(tooltipHost(div, substrate: divText(text: "a")), context: context)
        .equals(makeBlock(tooltipHost(div, substrate: divText(text: "a")), context: context))
    )
  }

  func test_TwoAnchorsWithSameTooltipId_AreNotEqual() {
    let context = makeFactoryContext()
    let tooltips: [DivTooltip]? = [
      DivTooltip(div: divContainer(id: "content"), id: "hint", position: .value(.center)),
    ]

    XCTAssertNotEqual(
      try! tooltips.makeTooltips(context: context.modifying(pathSuffix: "anchorA")),
      try! tooltips.makeTooltips(context: context.modifying(pathSuffix: "anchorB"))
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
          BlockTooltip(
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
                accessibilityElement: .default,
                path: .root + 0 + "separator" + "tooltip#tooltip1" + "container"
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
          ),
        ],
        accessibilityElement: .default,
        path: .root + 0 + "separator"
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
          BlockTooltip(
            viewSource: .block(
              DecoratingBlock(
                child: SeparatorBlock(
                  color: color("#14000000")
                ),
                accessibilityElement: .default,
                path: .root + 0 + "separator" + "tooltip#tooltip1" + "separator"
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
          ),
        ],
        accessibilityElement: .default,
        path: .root + 0 + "separator"
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)

    let errors = context.errorsStorage.errors
    XCTAssertEqual(1, errors.count)
    XCTAssertEqual("Tooltip can not host another tooltips", errors.first?.message)
  }

  @MainActor
  func test_ShowTooltipInScope_FindsAnchorWithoutActions() async {
    let context = DivBlockModelingContext()

    let block = makeBlock(
      divContainer(
        id: "scope",
        items: [
          divContainer(
            items: [
              divSeparator(
                tooltips: [
                  DivTooltip(
                    div: divText(text: "Tooltip"),
                    id: "tooltip1",
                    position: .value(.center)
                  ),
                ]
              ),
            ]
          ),
        ]
      ),
      context: context
    )

    let hostView = TestTooltipHostView()
    let manager = DefaultTooltipManager(externalView: hostView)
    hostView.addBlockView(block.makeBlockView(renderingDelegate: manager))

    let scopePaths = context.idToPath.paths(forId: "scope", cardId: context.cardId)
    XCTAssertEqual(scopePaths, [.root + 0 + "scope"])

    var error: String?
    let shown = await manager.showTooltip(
      info: TooltipInfo(
        id: "tooltip1",
        showsOnStart: false,
        multiple: false,
        scopePath: scopePaths.first,
        onError: { error = $0 }
      )
    )

    XCTAssertNil(error)
    XCTAssertTrue(shown)
  }
}

private func makeFactoryContext() -> DivBlockModelingContext {
  DivKitComponents().makeContext(cardId: "test_card_id", cachedImageHolders: [])
}

private func tooltipHost(_ div: Div, substrate: Div? = nil) -> Div {
  divSeparator(
    tooltips: [
      DivTooltip(div: div, id: "tooltip1", position: .value(.center), substrateDiv: substrate),
    ]
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

private final class TestTooltipHostView: TooltipHostView {
  private let containerView = UIView(frame: CGRect(x: 0, y: 0, width: 320, height: 480))

  var tooltipContainerBounds: CGRect { containerView.bounds }

  var coordinateSpace: ViewType { containerView }

  func addTooltipView(_ view: ViewType) {
    containerView.addSubview(view)
  }

  func addBlockView(_ view: UIView) {
    containerView.addSubview(view)
  }
}
