#if os(iOS)
import Foundation
import UIKit
import VGSL

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
      alignment: alignment,
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

  func configure(
    model: GalleryViewModel,
    selectedActions: [[UserInterfaceAction]],
    alignment: Alignment,
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
        alignment: alignment,
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

      if selectedActions.count > pageIndex, pageIndex >= 0 {
        selectedActions[pageIndex].perform(sendingFrom: self)
      }
    }
  }
}

extension PagerView: ElementStateObserver {
  func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    guard let galleryState = state as? GalleryViewState,
          let pageIndex = galleryState.contentPosition.pageIndex else {
      observer?.elementStateChanged(state, forPath: path)
      return
    }

    let currentPage = Int(pageIndex.rounded()) - model.infiniteCorrection

    setState(
      path: model.path,
      state: PagerViewState(
        numberOfPages: model.itemsCountWithoutInfinite,
        currentPage: currentPage,
        animated: galleryState.animated,
        isInfiniteScrollable: model.infiniteScroll
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
  let position: GalleryViewState.Position = if let oldState, oldState.isScrolling {
    oldState.contentPosition
  } else {
    .paging(index: CGFloat(pagerPosition))
  }

  return GalleryViewState(
    contentPosition: position,
    itemsCount: model.items.count,
    isScrolling: oldState?.isScrolling ?? false,
    scrollRange: oldState?.scrollRange,
    animated: state.animated
  ).resetToModelIfInconsistent(model)
}
#endif
