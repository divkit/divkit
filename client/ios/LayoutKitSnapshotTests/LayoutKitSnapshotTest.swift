import CoreGraphics
import Foundation

import LayoutKit

enum LayoutKitSnapshotTest {
  static func perform(
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
