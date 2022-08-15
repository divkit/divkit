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

  func compare(with other: UIImage) -> Bool {
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

    assert(data.count == otherData.count)
    assert(data.count == intWidth * intHeight * bytesPerPixel)

    let differentPixelCount = getDifferentPixelCount(
      lhs: data,
      rhs: otherData,
      bytesPerPixel: bytesPerPixel
    )
    let tolerance: Double = 0
    return Double(differentPixelCount) / Double(intWidth * intHeight) <= tolerance
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
    guard let data = context?.data else {
      return []
    }
    let dataPtr = data.assumingMemoryBound(to: UInt8.self)
    let capacity = bytesPerRow * intHeight
    let bytes = Array(UnsafeBufferPointer(start: dataPtr, count: capacity))
    let realBytesPerRow = Int(size.width) * bitsPerPixel / 8
    var bytesWithoutPadding = [UInt8]()
    for i in 0..<intHeight {
      let fromIdx = i * bytesPerRow
      bytesWithoutPadding.append(contentsOf: bytes[fromIdx..<(fromIdx + realBytesPerRow)])
    }
    return bytesWithoutPadding
  }
}

private func getDifferentPixelCount(lhs: [UInt8], rhs: [UInt8], bytesPerPixel: Int) -> Int {
  assert(lhs.count == rhs.count)
  assert(lhs.count.isMultiple(of: bytesPerPixel))
  var count = 0
  var diff: Double = 0
  var i = 0
  while i < lhs.count {
    let componentDiff = Double(lhs[i]) - Double(rhs[i])
    let normalizedComponentDiff = componentDiff / 255.0
    diff += normalizedComponentDiff * normalizedComponentDiff
    if i % bytesPerPixel == bytesPerPixel - 1 {
      let rootMeanSquare = (diff / Double(bytesPerPixel)).squareRoot()
      // 0.007 corresponds to 1.8 points diff between components
      count += rootMeanSquare > 0.007 ? 1 : 0
      diff = 0
    }
    i += 1
  }
  return count
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
