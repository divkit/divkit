import DivKit
import DivKitSVG
import UIKit
import VGSL

final class TestImageHolderFactory: @MainActor DivImageHolderFactory {
  private var reportedUrls = Set<String>()
  private let testBundle = Bundle(for: TestImageHolderFactory.self)
  private let reportError: (String) -> Void

  init(reportError: @escaping (String) -> Void) {
    self.reportError = reportError
  }

  @MainActor
  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    guard let url, url.absoluteString != "empty://" else {
      return placeholder?.toImageHolder() ?? NilImageHolder()
    }

    if url.pathExtension == "svg",
       let asset = NSDataAsset(name: url.lastPathComponent, bundle: testBundle) {
      guard let image = SVGDecoder().decode(data: asset.data) else {
        reportError("Failed to decode SVG test asset: \(url.lastPathComponent)")
        return UIImage()
      }
      return image
    }

    if let image = UIImage(named: url.lastPathComponent, in: testBundle, compatibleWith: nil) {
      return image
    }

    let urlString = url.absoluteString
    if !reportedUrls.contains(urlString) {
      reportError(
        "Loading images from network is prohibited in tests. You need to load image from \(urlString) and add it to Images.xcassets in testing bundle"
      )
      reportedUrls.insert(urlString)
    }

    return UIImage()
  }
}
