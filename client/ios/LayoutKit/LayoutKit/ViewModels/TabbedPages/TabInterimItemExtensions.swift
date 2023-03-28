import CoreGraphics

import CommonCorePublic

extension Array where Element == CGFloat {
  func interim(at index: CGFloat) -> CGFloat {
    let leftIndex = floorIndex(index)
    let leftItem = self[leftIndex]
    let rightIndex = ceilIndex(index)
    let rightItem = self[rightIndex]

    let indexOffset = abs(CGFloat(leftIndex) - index)
    let leftWeight = leftItem * (1 - indexOffset)
    let rightWeight = rightItem * indexOffset

    return leftWeight + rightWeight
  }
}
