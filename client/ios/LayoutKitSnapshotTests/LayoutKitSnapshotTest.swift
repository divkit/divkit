import CoreGraphics
import XCTest

import LayoutKit
import VGSL

open class LayoutKitSnapshotTest: XCTestCase {
  open override func setUp() {
    let fontProvider = YSFontProvider()
    fontSpecifiers = FontSpecifiers(
      text: fontProvider,
      display: fontProvider
    )
  }

  func perform(
    on renderable: UIViewRenderable,
    size: CGSize,
    name: String = #function,
    file: String = #file,
    mode: TestMode
  ) {
    let directory = URL(fileURLWithPath: file).deletingPathExtension().lastPathComponent

    let view = renderable.makeBlockView()
    view.frame = CGRect(origin: .zero, size: size)

    SnapshotTestKit.perform(
      on: view,
      name: name,
      directory: directory,
      mode: mode,
      size: size
    )
  }
}
