import Foundation
import UIKit

import CommonCorePublic
import LayoutKitInterface

extension PagerBlock {
  public static func makeBlockView() -> BlockView {
    PagerView(frame: .zero)
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let pagerView = view as! PagerView
    pagerView.configure(
      model: gallery,
      selectedActions: selectedActions,
      layoutMode: layoutMode,
      state: state,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is PagerView
  }
}

private final class PagerView: BlockView {
  private let galleryView: GalleryView

  private var model: GalleryViewModel!
  private var selectedActions: [[UserInterfaceAction]]!
  private var lastState: (UIElementPath, PagerViewState)?
  private weak var observer: ElementStateObserver?
  private weak var overscrollDelegate: ScrollDelegate?

  var effectiveBackgroundColor: UIColor? { backgroundColor }

  func configure(
    model: GalleryViewModel,
    selectedActions: [[UserInterfaceAction]],
    layoutMode: PagerBlock.LayoutMode,
    state: PagerViewState,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.observer = observer
    self.overscrollDelegate = overscrollDelegate

    let galleryState = makeGalleryViewState(oldState: galleryView.state, model: model, state: state)
    let layoutFactory: GalleryView.LayoutFactory = { model, boundsSize in
      PagerViewLayout(
        model: model,
        pageIndex: Int(round(galleryState.contentPosition.pageIndex ?? 1)),
        layoutMode: layoutMode,
        boundsSize: boundsSize
      )
    }
    galleryView.configure(
      model: model,
      state: galleryState,
      layoutFactory: layoutFactory,
      observer: self,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )

    let isNewModel = model != self.model
    self.model = model
    self.selectedActions = selectedActions

    if isNewModel {
      onMainThreadAsync {
        self.setState(
          path: model.path,
          state: state.synchronized(with: model),
          selectedActions: selectedActions
        )
      }
    }
  }

  private func setState(
    path: UIElementPath,
    state: PagerViewState,
    selectedActions: [[UserInterfaceAction]]
  ) {
    if lastState?.0 != path || lastState?.1 != state {
      lastState = (path, state)

      observer?.elementStateChanged(state, forPath: path)

      let pageIndex = Int(round(state.currentPage))

      PagerSelectedPageChangedEvent(
        path: path,
        selectedPageIndex: pageIndex
      ).sendFrom(self)

      if selectedActions.count > pageIndex {
        selectedActions[pageIndex].perform(sendingFrom: self)
      }
    }
  }

  override init(frame: CGRect) {
    galleryView = GalleryView(frame: frame)
    super.init(frame: frame)
    addSubview(galleryView)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    galleryView.frame = bounds
  }
}

extension PagerView: ElementStateObserver {
  func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    guard let galleryState = state as? GalleryViewState,
          let pageIndex = galleryState.contentPosition.pageIndex else {
      observer?.elementStateChanged(state, forPath: path)
      return
    }

    setState(
      path: model.path,
      state: PagerViewState(
        numberOfPages: model.itemsCountWithoutInfinite,
        currentPage: Int(pageIndex.rounded()) - model.infiniteCorrection
      ),
      selectedActions: selectedActions
    )
  }

  func focusedElementChanged(isFocused: Bool, forPath path: UIElementPath) {
    observer?.focusedElementChanged(isFocused: isFocused, forPath: path)
  }
}

extension PagerView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [galleryView]
  }
}

private func makeGalleryViewState(
  oldState: GalleryViewState?,
  model: GalleryViewModel,
  state: PagerViewState
) -> GalleryViewState {
  let pagerPosition = state.currentPage + CGFloat(model.infiniteCorrection)
  let position: GalleryViewState.Position
  if let oldState, oldState.isScrolling {
    position = oldState.contentPosition
  } else {
    position = .paging(index: CGFloat(pagerPosition))
  }

  return GalleryViewState(
    contentPosition: position,
    itemsCount: model.items.count,
    isScrolling: oldState?.isScrolling ?? false
  )
}
