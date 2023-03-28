import Foundation
import UIKit

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

extension SwitchableContainerBlock {
  public static func makeBlockView() -> BlockView { SwitchableContainerView() }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let containerView = view as! SwitchableContainerView
    containerView.observer = observer
    containerView.overscrollDelegate = overscrollDelegate
    containerView.renderingDelegate = renderingDelegate
    containerView.model = SwitchableContainerView.Model(
      selectedItem: selectedItem,
      titles: (items.0.title, items.1.title),
      titleGaps: titleGaps,
      content: (items.0.content, items.1.content),
      titleContentGap: titleContentGap,
      selectorSideGaps: selectorSideGaps,
      path: path,
      backgroundColor: backgroundColor,
      selectedBackgroundColor: selectedBackgroundColor,
      switchAction: switchAction
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is SwitchableContainerView
  }
}

private final class SwitchableContainerView: BlockView, VisibleBoundsTrackingContainer {
  struct Model {
    var selectedItem: SwitchableContainerBlock.Selection
    let titles: SwitchableContainerBlock.Titles
    let titleGaps: CGFloat
    let content: SwitchableContainerBlock.Pair<Block>
    let titleContentGap: CGFloat
    let selectorSideGaps: CGFloat
    let path: UIElementPath
    let backgroundColor: Color
    let selectedBackgroundColor: Color
    let switchAction: UserInterfaceAction?
  }

  var model: Model! {
    didSet {
      selectorView.model = SegmentedSelectorView.Model(
        selectedItem: model.selectedItem,
        titles: model.titles,
        backgroundColor: model.backgroundColor,
        selectedBackgroundColor: model.selectedBackgroundColor,
        titleGaps: model.titleGaps,
        selectorSideGaps: model.selectorSideGaps,
        switchAction: model.switchAction
      )
      blockViews = blockViews.reused(
        with: [model.content.0, model.content.1],
        attachTo: self,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
      for (index, view) in blockViews.enumerated() {
        view.alpha = index == model.selectedItem.rawValue ? 1 : 0
      }
      lastLayoutSize = .zero
      setNeedsLayout()
    }
  }

  var effectiveBackgroundColor: UIColor? { selectorView.backgroundColor }

  private var lastLayoutSize = CGRect.zero

  var observer: ElementStateObserver?
  weak var overscrollDelegate: ScrollDelegate?
  weak var renderingDelegate: RenderingDelegate?

  private let selectorView = SegmentedSelectorView()
  private var blockViews: [BlockView] = []
  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { blockViews }

  init() {
    super.init(frame: .zero)

    addSubview(selectorView)

    selectorView.onTap = { [unowned self] in
      self.toggleSelection()
    }
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    // NOTE: prevent relayout with same bounds
    guard bounds != lastLayoutSize else {
      return
    }

    let layout = SwitchableContainerBlockLayout(
      width: bounds.width,
      titles: model.titles,
      titleGaps: model.titleGaps,
      selectorSideGaps: model.selectorSideGaps
    )
    selectorView.frame = CGRect(
      x: model.selectorSideGaps,
      y: 0,
      width: bounds.width - 2 * model.selectorSideGaps,
      height: layout.selectorIntrinsicHeight
    )

    for (index, view) in blockViews.enumerated() {
      let y = index == model.selectedItem.rawValue
        ? selectorView.frame.maxY + model.titleContentGap
        : bounds.height

      view.frame = modified(bounds) {
        $0.origin.y = y
        $0.size.height -= y
      }
    }

    lastLayoutSize = bounds
  }

  private func toggleSelection() {
    let newItem = model.selectedItem.toggled

    forceLayout()
    observer?.elementStateChanged(
      SwitchableContainerBlockState(selectedItem: newItem),
      forPath: model.path
    )
    AnalyticsTouchEvent(
      touchType: .click,
      path: newItem == .left ? model.path + "left" : model.path + "right"
    ).sendFrom(self)

    UIView.animate(
      withDuration: 0.15,
      animations: {
        self.model.selectedItem = newItem
        self.selectorView.model.selectedItem = newItem
        self.forceLayout()
      }
    )
  }
}

private final class SegmentedSelectorView: UIView {
  struct Model {
    var selectedItem: SwitchableContainerBlock.Selection
    let titles: SwitchableContainerBlock.Titles
    let backgroundColor: Color
    let selectedBackgroundColor: Color
    let titleGaps: CGFloat
    let selectorSideGaps: CGFloat
    let switchAction: UserInterfaceAction?
  }

  var model: Model! {
    didSet {
      leftLabel.attributedText = model.selectedItem == .left
        ? model.titles.0.selected
        : model.titles.0.deselected

      rightLabel.attributedText = model.selectedItem == .right
        ? model.titles.1.selected
        : model.titles.1.deselected

      backgroundColor = model.backgroundColor.systemColor
      selectorView.backgroundColor = model.selectedBackgroundColor.systemColor

      setNeedsLayout()
    }
  }

  var onTap: (() -> Void)?

  private let leftLabel = Label(frame: .zero)
  private let rightLabel = Label(frame: .zero)
  private let selectorView = UIView(frame: .zero)

  init() {
    super.init(frame: .zero)

    leftLabel.numberOfLines = 1
    rightLabel.numberOfLines = 1

    addSubviews([selectorView, leftLabel, rightLabel])

    let recognizer = UITapGestureRecognizer(target: self, action: #selector(didTap))
    addGestureRecognizer(recognizer)
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    let segmentWidth = 0.5 * bounds.width
    let layout = SwitchableContainerBlockLayout(
      width: bounds.width,
      titles: model.titles,
      titleGaps: model.titleGaps,
      selectorSideGaps: model.selectorSideGaps
    )
    let height = layout.titlesHeight

    leftLabel.frame = CGRect(
      x: bounds.minX,
      y: bounds.minY + model.titleGaps,
      width: segmentWidth,
      height: height
    )
    rightLabel.frame = CGRect(
      x: bounds.minX + segmentWidth,
      y: bounds.minY + model.titleGaps,
      width: segmentWidth,
      height: height
    )
    selectorView.frame = CGRect(
      x: bounds.minX + CGFloat(model.selectedItem.rawValue) * segmentWidth,
      y: bounds.minY,
      width: segmentWidth,
      height: height + 2 * model.titleGaps
    )

    selectorView.layer.cornerRadius = 0.5 * selectorView.frame.height
    layer.cornerRadius = selectorView.layer.cornerRadius
  }

  @objc private func didTap(_ recoginizer: UITapGestureRecognizer) {
    let location = recoginizer.location(in: self)
    guard bounds.contains(location), !selectorView.frame.contains(location) else {
      return
    }

    model.switchAction?.perform(sendingFrom: self)
    onTap?()
  }
}

extension SwitchableContainerBlock.Selection {
  fileprivate var toggled: SwitchableContainerBlock.Selection {
    switch self {
    case .left: return .right
    case .right: return .left
    }
  }
}
