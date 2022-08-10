import UIKit

open class NoContentTouchDelaysCollectionView: UICollectionView {
  public override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout) {
    super.init(frame: frame, collectionViewLayout: layout)
    delaysContentTouches = false
    isExclusiveTouch = true
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func touchesShouldCancel(in _: UIView) -> Bool {
    true
  }
}
