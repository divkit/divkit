@testable import DivKit
@testable import LayoutKit
import UIKit
import VGSL
import XCTest

final class DivBlockStateStorageTests: XCTestCase {
  private var storage: DivBlockStateStorage!
  private let disposePool = AutodisposePool()

  override func setUp() {
    super.setUp()

    storage = DivBlockStateStorage()
  }

  func test_GetState_ByPath_NotExists() {
    XCTAssertNil(storage.getStateUntyped(divStatePath("0/id")))
  }

  func test_SetState_WithPath_GetState_ByPath() {
    storage.setState(path: divStatePath("0/id"), state: state1)
    XCTAssertEqual(storage.getState(divStatePath("0/id")), state1)
  }

  func test_SetState_WithSamePath_OverridesState() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state2)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "0/id")), state2)
  }

  func test_ExtensionReadDoesNotExposeElementState() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    let context = makeExtensionContext(id: "first")

    let extensionState: State? = context.getState()

    XCTAssertNil(extensionState)
  }

  func test_ExtensionWritePreservesElementState() {
    let elementPath = path(cardId: "card_id", path: "0/id")
    storage.setState(path: elementPath, state: state1)
    let context = makeExtensionContext(id: "first")

    context.setState(state2)

    XCTAssertEqual(storage.getState(elementPath), state1)
  }

  func test_ExtensionStateIsIsolatedById() {
    let first = makeExtensionContext(id: "first")
    let second = makeExtensionContext(id: "second")
    first.setState(state1)

    second.setState(state2)

    XCTAssertEqual(first.getState(), state1)
    XCTAssertEqual(second.getState(), state2)
  }

  func test_ElementWritePreservesExtensionState() {
    let context = makeExtensionContext(id: "first")
    context.setState(state1)

    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state2)

    XCTAssertEqual(context.getState(), state1)
  }

  func test_ResetClearsExtensionState() {
    let context = makeExtensionContext(id: "first")
    context.setState(state1)

    storage.reset()

    XCTAssertNil(context.getState() as State?)
  }

  func test_ResetCardClearsExtensionState() {
    let context = makeExtensionContext(id: "first")
    context.setState(state1)

    storage.reset(cardId: "card_id")

    XCTAssertNil(context.getState() as State?)
  }

  func test_ResetCardPreservesOtherCardExtensionState() {
    let context = makeExtensionContext(id: "first", cardId: "other_card")
    context.setState(state1)

    storage.reset(cardId: "card_id")

    XCTAssertEqual(context.getState(), state1)
  }

  func test_SetState_WithTheSameId_InDifferentPaths_KeepsBothStates() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setState(path: path(cardId: "card_id", path: "1/id"), state: state2)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "0/id")), state1)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "1/id")), state2)
  }

  func test_SetState_WithTheSameId_InDifferentCards_KeepsBothStates() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setState(path: path(cardId: "other_card", path: "0/id"), state: state2)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "0/id")), state1)
    XCTAssertEqual(storage.getState(path(cardId: "other_card", path: "0/id")), state2)
  }

  func test_Reset_ResetsPaths() {
    storage.setState(path: divStatePath("0/id"), state: state1)
    storage.reset()
    XCTAssertNil(storage.getStateUntyped(divStatePath("0/id")))
  }

  func test_Reset_ResetsFocusedElement() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.reset()
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
  }

  func test_Reset_ResettingByCardId() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setState(path: path(cardId: "other_card", path: "0/id"), state: state2)
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.reset(cardId: "card_id")
    XCTAssertNil(storage.getStateUntyped(path(cardId: "card_id", path: "0/id")))
    XCTAssertEqual(storage.getState(path(cardId: "other_card", path: "0/id")), state2)
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_Peek_WhenEmpty_IsNil() {
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_Take_WhenEmpty_IsNil() {
    XCTAssertNil(storage.takePendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_SetByPath_ReadByPath() {
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state1)
    let value = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(value, state1)
  }

  func test_PendingState_Take_ClearsTheSlot() {
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state1)
    _ = storage.takePendingState(path(cardId: "card_id", path: "0/id"))
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_Set_AlsoUpdatesRegularState() {
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state2)
    XCTAssertEqual(storage.getState(path(cardId: "card_id", path: "0/id")), state2)
    let pending = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(pending, state2)
  }

  func test_PendingState_NotOverwritten_BySetStateByPath() {
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setState(path: path(cardId: "card_id", path: "0/id"), state: state2)
    let pending = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(pending, state1)
  }

  func test_PendingState_LastWriteWins() {
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state2)
    let pending = storage.peekPendingState(path(cardId: "card_id", path: "0/id")) as? State
    XCTAssertEqual(pending, state2)
  }

  func test_PendingState_Emits_StateUpdatesPipe() {
    var updatesCounter = 0
    storage.stateUpdates.addObserver { _ in
      updatesCounter += 1
    }.dispose(in: disposePool)

    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state1)

    XCTAssertEqual(updatesCounter, 1)
  }

  func test_PendingState_ClearedBy_Reset() {
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state1)
    storage.reset()
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
  }

  func test_PendingState_ClearedBy_ResetByCardId() {
    storage.setPendingState(path(cardId: "card_id", path: "0/id"), state: state1)
    storage.setPendingState(path(cardId: "other_card", path: "0/id"), state: state2)
    storage.reset(cardId: "card_id")
    XCTAssertNil(storage.peekPendingState(path(cardId: "card_id", path: "0/id")))
    let other = storage.peekPendingState(path(cardId: "other_card", path: "0/id")) as? State
    XCTAssertEqual(other, state2)
  }

  func test_PreventUpdatePipeWhenSettingSameState() {
    var updatesCounter = 0
    storage.stateUpdates.addObserver { _ in
      updatesCounter += 1
    }.dispose(in: disposePool)

    storage.setState(
      path: path(cardId: "card_id", path: "0/div_state/state1/id"),
      state: state1
    ) // Should update, new state for new path
    storage.setState(
      path: path(cardId: "card_id", path: "0/div_state/state1/id"),
      state: state1
    ) // Shouldn't update, same state by path

    storage.setState(
      path: path(cardId: "card_id2", path: "0/div_state/state1/id"),
      state: state1
    ) // Should update, new card

    storage.setState(
      path: path(cardId: "card_id", path: "0/div_state/state1/id2"),
      state: state1
    ) // Should update, new id

    storage.setState(
      path: path(cardId: "card_id", path: "0/div_state/state1/id"),
      state: state2
    ) // Should update, new state for existing path

    XCTAssertEqual(updatesCounter, 4)
  }

  func test_Reset_ResetsFocusedElementByPath() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.reset()
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
  }

  func test_Reset_ResetsByCardIdFocusedElementByPath() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.reset(cardId: "card_id")
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
  }

  func test_Reset_ByOtherCardId_KeepsFocusedElement() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.reset(cardId: "other_card")
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
  }

  func test_SetFocusedByPath_StoresLast() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_2"))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id_2")))
  }

  func test_SetFocusedByPath_WithTheSameId() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "1/id"))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "1/id")))
  }

  func test_IfElementByPathFocused_Unfocuses() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.setFocused(isFocused: false, path: path(cardId: "card_id", path: "0/id"))
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
  }

  func test_IfElementByPathNotFocused_DoesNothing() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id_1"))
    storage.setFocused(isFocused: false, path: path(cardId: "card_id", path: "0/id_2"))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id_1")))
  }

  func test_IfElementWithTheSameIdNotFocused_DoesNothing() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.setFocused(isFocused: false, path: path(cardId: "card_id", path: "1/id"))
    XCTAssertTrue(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
  }

  func test_ClearFocus_Unfocuses() {
    storage.setFocused(isFocused: true, path: path(cardId: "card_id", path: "0/id"))
    storage.clearFocus()
    XCTAssertFalse(storage.isFocused(path: path(cardId: "card_id", path: "0/id")))
  }

  func test_ElementStateChanged_WhenPagingGalleryScrollIsCancelled_KeepsVideoPlaying() {
    let galleryPath = path(cardId: "card_id", path: "0/gallery")
    let videoPath = path(cardId: "card_id", path: "0/gallery/video")
    storage.setState(path: videoPath, state: VideoBlockViewState(state: .playing))

    let model = makePagingGalleryModel(path: galleryPath)
    let galleryView = GalleryView(frame: CGRect(x: 0, y: 0, width: 100, height: 100))
    galleryView.configure(
      model: model,
      state: GalleryViewState(contentPageIndex: 0, itemsCount: model.items.count, animated: false),
      observer: storage,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
    galleryView.updateGalleryState(.paging(index: 0.1), offset: 10)
    galleryView.updateGalleryState(.paging(index: 0), offset: 0)
    galleryView.finishScrolling(scrollStartOffset: 0)

    let result: VideoBlockViewState? = storage.getState(videoPath)
    XCTAssertEqual(result?.state, .playing)
  }

  private func makeExtensionContext(
    id: String, cardId: DivCardID = "card_id"
  ) -> DivExtensionActionContext {
    let elementPath = path(cardId: cardId.rawValue, path: "0/id")
    return DivExtensionActionContext(
      path: elementPath,
      extensionId: id,
      info: DivActionInfo(
        path: elementPath, logId: "extension", url: nil, logUrl: nil,
        referer: nil, source: .tap, payload: nil
      ),
      expressionResolver: ExpressionResolver(
        functionsProvider: FunctionsProvider(
          persistentValuesStorage: DivPersistentValuesStorage(), cardId: cardId
        ),
        customFunctionsStorageProvider: { _ in nil },
        variableValueProvider: { _ in nil },
        errorTracker: { _ in }
      ),
      blockStateStorage: storage,
      updateCard: { _ in }
    )
  }
}

private let state1 = State(name: "State 1")
private let state2 = State(name: "State 2")

private struct State: ElementState, Equatable {
  public let name: String
}

private func makePagingGalleryModel(path: UIElementPath) -> GalleryViewModel {
  GalleryViewModel(
    blocks: [makeGalleryItem(), makeGalleryItem()],
    metrics: GalleryViewMetrics(gaps: [0, 0, 0]),
    scrollMode: .autoPaging(inertionEnabled: true),
    path: path
  )
}

private func makeGalleryItem() -> Block {
  TextBlock(
    widthTrait: .fixed(100),
    heightTrait: .fixed(100),
    text: NSAttributedString(string: "")
  )
}

private func path(cardId: String, path: String) -> UIElementPath {
  UIElementPath(cardId) + path.split(separator: "/").map(String.init)
}

private func divStatePath(_ path: String) -> UIElementPath {
  DivStatePath.makeDivStatePath(from: path).rawValue
}
