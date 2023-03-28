import UIKit

import CommonCorePublic

public final class TabbedPagesView: BlockView, VisibleBoundsTrackingContainer {
  private var selectionWireframe: TabSelectionWireframe?

  private let tabListView = TabListView()
  private let tabContentsView = TabContentsView()

  private var separatorView: UIView?

  private var model: TabViewModel! {
    didSet {
      if oldValue == model {
        return
      }
      if oldValue?.listModel !== model.listModel {
        tabDataSource = TabListSelectionDataSourceImpl(listModel: model.listModel)
      }
    }
  }

  private var tabDataSource: TabListSelectionDataSource!
  private var layout: TabViewLayout!
  public let visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView]

  public var effectiveBackgroundColor: UIColor? { tabContentsView.effectiveBackgroundColor }

  public func configure(
    model: TabViewModel,
    state: TabViewState,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.model = model

    let wireframe = TabSelectionWireframe(
      tabListView: tabListView,
      tabListSelectionDataSource: tabDataSource,
      tabContentsView: tabContentsView
    )

    tabListView.overscrollDelegate = overscrollDelegate
    tabListView.model = tabDataSource.modelForItemSelection(state.selectedPageIndex)

    selectionWireframe = wireframe
    tabContentsView.configure(
      model: model.contentsModel,
      state: state,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
    configureSeparatorView()
    setNeedsLayout()
  }

  public weak var updatesDelegate: TabbedPagesViewModelDelegate? {
    get { tabContentsView.updatesDelegate }
    set { tabContentsView.updatesDelegate = newValue }
  }

  override init(frame: CGRect) {
    visibleBoundsTrackingSubviews = [tabContentsView]
    super.init(frame: frame)
    attachViews()
  }

  private func attachViews() {
    addSubviews([tabListView, tabContentsView])
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func layoutSubviews() {
    let newLayout = TabViewLayout(
      model: model,
      selectedPageIndex: tabContentsView.selectedPageIndex,
      size: bounds.size
    )

    guard newLayout != layout else {
      return
    }

    layout = newLayout
    tabListView.frame = layout.listFrame
    tabContentsView.frame = layout.contentsFrame
    separatorView?.frame = layout.separatorFrame
  }

  private func configureSeparatorView() {
    guard let separatorStyle = model.separatorStyle else {
      separatorView?.removeFromSuperview()
      separatorView = nil
      return
    }

    let view: UIView

    if let separatorView = separatorView {
      view = separatorView
    } else {
      view = UIView()
      addSubview(view)
      separatorView = view
    }

    view.backgroundColor = separatorStyle.color.systemColor
  }
}
