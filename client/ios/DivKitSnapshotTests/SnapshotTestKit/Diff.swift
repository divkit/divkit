import UIKit

extension UIImage {
  func makeDiff(with other: UIImage) -> UIImage {
    let resultSize = CGSize(
      width: max(size.width, other.size.width),
      height: max(size.height, other.size.height)
    )
    UIGraphicsBeginImageContextWithOptions(resultSize, true, 0)
    let context = UIGraphicsGetCurrentContext()!
    draw(in: CGRect(origin: .zero, size: size))
    context.setAlpha(0.5)
    context.beginTransparencyLayer(auxiliaryInfo: nil)
    other.draw(in: CGRect(origin: .zero, size: other.size))
    context.setBlendMode(.difference)
    context.setFillColor(UIColor.white.cgColor)
    context.fill(CGRect(origin: .zero, size: size))
    context.endTransparencyLayer()
    let other = UIGraphicsGetImageFromCurrentImageContext()!
    UIGraphicsEndImageContext()
    return other
  }

  func compare(with other: UIImage, tolerance: Double = 0.003) -> Bool {
    let size = self.size
    guard size == other.size else {
      return false
    }

    let cgImage = self.cgImage!
    let otherCGImage = other.cgImage!

    assert(cgImage.bitsPerPixel == otherCGImage.bitsPerPixel)

    let data = cgImage.makeData(withSize: size)
    let otherData = otherCGImage.makeData(withSize: size)

    let bytesPerPixel = cgImage.bitsPerPixel / 8
    let intWidth = Int(size.width)
    let intHeight = Int(size.height)
    let effectiveBytesPerRow = intWidth * bytesPerPixel

    return compareImages(
      data1: data,
      data2: otherData,
      bytesPerRow1: cgImage.bytesPerRow,
      bytesPerRow2: otherCGImage.bytesPerRow,
      effectiveBytesPerRow: effectiveBytesPerRow,
      intHeight: intHeight
    ) < tolerance
  }
}

extension CGImage {
  fileprivate func makeData(withSize size: CGSize) -> [UInt8] {
    let intHeight = Int(size.height)
    let context = CGContext(
      data: nil,
      width: Int(size.width),
      height: intHeight,
      bitsPerComponent: bitsPerComponent,
      bytesPerRow: bytesPerRow,
      space: CGColorSpaceCreateDeviceRGB(),
      bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue
    )
    context?.draw(self, in: CGRect(origin: .zero, size: size))
    guard let data = context?.data else { fatalError() }
    let dataPtr = data.assumingMemoryBound(to: UInt8.self)
    let capacity = bytesPerRow * intHeight
    return Array(UnsafeBufferPointer(start: dataPtr, count: capacity))
  }
}

private func compareImages(
  data1: [UInt8],
  data2: [UInt8],
  bytesPerRow1: Int,
  bytesPerRow2: Int,
  effectiveBytesPerRow: Int,
  intHeight: Int
) -> Double {
  var sum = 0.0
  var iterator1 = 0
  var iterator2 = 0
  let delta1 = bytesPerRow1 - effectiveBytesPerRow
  let delta2 = bytesPerRow2 - effectiveBytesPerRow
  var i = 0
  while i < intHeight {
    var j = 0
    while j < effectiveBytesPerRow {
      let componentDiff = Double(data1[iterator1]) - Double(data2[iterator2])
      sum += componentDiff * componentDiff
      j += 1
      iterator1 += 1
      iterator2 += 1
    }
    i += 1
    iterator1 += delta1
    iterator2 += delta2
  }
  let count = effectiveBytesPerRow * intHeight
  return (sum / ((255.0 * 255.0) * Double(count))).squareRoot()
}

private func printDiff(lhs: [UInt8], rhs: [UInt8], bytesPerRow: Int, bytesPerPixel: Int) {
  assert(lhs.count == rhs.count)
  assert(lhs.count.isMultiple(of: bytesPerPixel))
  var hasDiffInCurrentPixel = false
  for i in 0..<lhs.count {
    if lhs[i] != rhs[i] {
      hasDiffInCurrentPixel = true
    }
    if i % bytesPerPixel == bytesPerPixel - 1 {
      print(hasDiffInCurrentPixel ? "x" : "·", terminator: "")
      hasDiffInCurrentPixel = false
    }
    if i % bytesPerRow == bytesPerRow - 1 {
      print("")
    }
  }
}
