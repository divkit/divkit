import DivKit
import UIKit

final class DivCollectionViewCell: UICollectionViewCell {
  static let reuseIdentifier = "DivCollectionViewCell"

  private var divView: DivView? {
    didSet {
      if oldValue !== divView {
        oldValue?.removeFromSuperview()
      }
    }
  }

  override func prepareForReuse() {
    super.prepareForReuse()
    divView = nil
  }

  func configure(
    cardId: DivCardID,
    divKitComponents: DivKitComponents,
    preloader: DivViewPreloader
  ) {
    let divView = DivView(
      divKitComponents: divKitComponents,
      divViewPreloader: preloader
    )
    divView.frame = contentView.bounds
    divView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
    contentView.addSubview(divView)
    self.divView = divView

    divView.showCardId(cardId)
  }
}
