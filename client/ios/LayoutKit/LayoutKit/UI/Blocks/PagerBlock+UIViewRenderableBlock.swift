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

    let galleryState = GalleryViewState(
      contentPageIndex: CGFloat(state.currentPage),
      itemsCount: model.items.count
    )
    let layoutFactory: GalleryView.LayoutFactory = { model, boundsSize in
      PagerViewLayout(
        model: model,
        pageIndex: Int(round(state.currentPage)),
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
        // do not update state if model has been changed already
        if model == self.model {
          self.setState(state.synchronized(with: model))
        }
      }
    }
  }

  private func setState(_ state: PagerViewState) {
    let path = model.path
    if lastState?.0 != path || lastState?.1 != state {
      lastState = (path, state)

      observer?.elementStateChanged(state, forPath: path)

      let pageIndex = Int(round(state.currentPage))

      PagerSelectedPageChangedEvent(
        path: path,
        selectedPageIndex: pageIndex
      ).sendFrom(self)

      selectedActions[pageIndex].perform(sendingFrom: self)
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
  func elementStateChanged(_ state: ElementState, forPath _: UIElementPath) {
    guard let galleryState = state as? GalleryViewState,
          let pageIndex = galleryState.contentPosition.pageIndex else {
      assertionFailure()
      return
    }

    if !galleryState.isScrolling {
      setState(
        PagerViewState(
          numberOfPages: model.items.count,
          currentPage: Int(pageIndex.rounded())
        )
      )
    }
  }
}

extension PagerView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [galleryView]
  }
}
