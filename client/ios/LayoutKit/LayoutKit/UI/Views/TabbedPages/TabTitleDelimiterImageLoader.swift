import UIKit
import VGSL

final class TabTitleDelimiterImageLoader {
  private var cachedImage: Image?
  private var pendingRequests: [(UIImage?) -> Void] = []
  private let imageHolder: ImageHolder

  init(imageHolder: ImageHolder) {
    self.imageHolder = imageHolder
  }

  func loadImage(completion: @escaping ((Image?) -> Void)) {
    Thread.assertIsMain()

    if let cachedImage {
      completion(cachedImage)
      return
    }

    pendingRequests.append(completion)

    guard pendingRequests.count == 1 else {
      return
    }

    imageHolder.requestImageWithCompletion { [weak self] image in
      Thread.assertIsMain()

      guard let self, let image else {
        self?.pendingRequests.forEach { $0(nil) }
        self?.pendingRequests = []
        return
      }

      self.cachedImage = image

      for request in self.pendingRequests {
        request(image)
      }
      self.pendingRequests = []
    }
  }
}
