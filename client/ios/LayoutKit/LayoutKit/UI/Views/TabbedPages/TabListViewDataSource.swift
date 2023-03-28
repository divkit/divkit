import UIKit

import BaseUIPublic

final class TabListViewDataSource: NSObject, UICollectionViewDataSource {
  private let tabs: [TabTitleViewModel]

  init(tabs: [TabTitleViewModel]) {
    self.tabs = tabs
  }

  func collectionView(_: UICollectionView, numberOfItemsInSection _: Int) -> Int {
    tabs.count
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(
      withReuseIdentifier: TabListItemCell.reuseID,
      for: indexPath
    ) as! TabListItemCell
    cell.model = tabs[indexPath.item]
    return cell
  }
}

final class TabListItemCell: UICollectionViewCell {
  static let reuseID = "TabListItemCell"

  private var textLabel: Label!

  var model: TabTitleViewModel! {
    didSet {
      if textLabel.attributedText !== model.text {
        textLabel.attributedText = model.text
      }

      if textLabel.textColor != model.color {
        textLabel.textColor = model.color
      }
    }
  }

  override init(frame: CGRect) {
    super.init(frame: frame)
    textLabel = makeTextLabel()
    contentView.addSubview(textLabel)
  }

  @available(*, unavailable)
  required init(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  private func makeTextLabel() -> Label {
    let label = Label(frame: .zero)
    label.textAlignment = .center
    return label
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    textLabel.frame = bounds.inset(by: model.insets)
    let boundary = model.cornerRadius.map(BoundaryTrait.clipCorner) ??
      BoundaryTrait.cornerRadius(bounds.size.height / 2)
    layer.apply(boundary: boundary)
    self.backgroundColor = self.model.backgroundColor.systemColor
  }

  class func sizeForModel(_ model: TabTitleViewModel) -> CGSize {
    model.totalSize.ceiled()
  }
}
