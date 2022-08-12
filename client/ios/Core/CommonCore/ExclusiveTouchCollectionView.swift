// Copyright 2016 Yandex LLC. All rights reserved.

import UIKit

// swiftlint:disable use_exclusive_touch_collection_view
@available(tvOS, unavailable)
public func ExclusiveTouchCollectionView(
  frame: CGRect,
  collectionViewLayout: UICollectionViewLayout
) -> UICollectionView {
  let result = UICollectionView(frame: frame, collectionViewLayout: collectionViewLayout)
  result.isExclusiveTouch = true
  return result
}

// swiftlint:enable use_exclusive_touch_collection_view
