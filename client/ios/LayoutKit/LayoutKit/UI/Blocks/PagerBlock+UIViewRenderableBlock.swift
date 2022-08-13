// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation
import UIKit

import CommonCore
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
  private var layout: PagerViewLayout!
  private var currentPageIndex: FractionalPageIndex = 0
  private var lastState: PagerViewState?
  private weak var observer: ElementStateObserver?
  private weak var overscrollDelegate: ScrollDelegate?

  private var isConfiguring = false

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
    guard !isConfiguring else { return }

    self.observer = observer
    self.overscrollDelegate = overscrollDelegate

    let galleryState = GalleryViewState(
      contentPageIndex: CGFloat(state.currentPage)
    )
    let layoutFactory: GalleryView.LayoutFactory = { [unowned self] model, boundsSize in
      let pagerLayout = PagerViewLayout(
        model: model,
        pageIndex: Int(round(state.currentPage)),
        layoutMode: layoutMode,
        boundsSize: boundsSize
      )
      self.layout = pagerLayout
      return pagerLayout
    }

    galleryView.configure(
      model: model,
      state: galleryState,
      layoutFactory: layoutFactory,
      observer: self,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )

    let oldModel: GalleryViewModel? = self.model
    self.model = model
    self.selectedActions = selectedActions

    onMainThreadAsync {
      if model != oldModel {
        self.setState(state.synchronized(with: model), notifyingObservers: true)
      }
    }
  }

  private func setState(_ state: PagerViewState, notifyingObservers: Bool) {
    let currentPage = state.currentPage
    currentPageIndex = FractionalPageIndex(rawValue: CGFloat(currentPage))
    let lastStateCurrentPage = lastState?.currentPage
    if lastState != state, lastStateCurrentPage != currentPage {
      lastState = state
      isConfiguring = true

      if notifyingObservers {
        observer?.elementStateChanged(state, forPath: model.path)
      }

      let pageIndex = Int(round(currentPage))

      PagerSelectedPageChangedEvent(
        path: model.path,
        selectedPageIndex: pageIndex
      ).sendFrom(self)

      selectedActions[pageIndex].perform(sendingFrom: self)

      isConfiguring = false
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

    let lastCurrentPage = lastState?.currentPage ?? 0
    let roundedPageIndex = pageIndex.rounded()
    let currentPage = abs(pageIndex - roundedPageIndex) < 0.1
      ? Int(roundedPageIndex)
      : Int(round(lastCurrentPage))
    setState(
      PagerViewState(
        numberOfPages: model.items.count,
        currentPage: currentPage
      ),
      notifyingObservers: true
    )
  }
}

extension PagerView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    [galleryView]
  }
}

private typealias CellType = GenericCollectionViewCell
private typealias FractionalPageIndex = Tagged<PagerView, CGFloat>
