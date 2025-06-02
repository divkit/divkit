@testable import LayoutKit
import VGSL
import XCTest

final class TabTitleDelimiterImageLoaderTests: XCTestCase {
  private var imageLoader: TabDelimiterImageLoader!
  private var mockImageHolder: MockImageHolder!

  override func setUp() {
    super.setUp()
    mockImageHolder = MockImageHolder()
    imageLoader = TabDelimiterImageLoader(imageHolder: mockImageHolder)
  }

  override func tearDown() {
    imageLoader = nil
    mockImageHolder = nil
    super.tearDown()
  }

  func test_LoadImageSuccessfully_ReturnsImage() {
    let expectation = XCTestExpectation(description: "Image loaded")
    let testImage = createTestImage()

    mockImageHolder.mockImage = testImage

    var resultImage: UIImage?
    imageLoader.loadImage { image in
      resultImage = image
      expectation.fulfill()
    }

    wait(for: [expectation], timeout: 1.0)
    XCTAssertNotNil(resultImage)
    XCTAssertEqual(resultImage, testImage)
  }

  func test_LoadImageFails_ReturnsNil() {
    let expectation = XCTestExpectation(description: "Image load fails")

    mockImageHolder.mockImage = nil

    var resultImage: UIImage?
    imageLoader.loadImage { image in
      resultImage = image
      expectation.fulfill()
    }

    wait(for: [expectation], timeout: 1.0)
    XCTAssertNil(resultImage)
  }

  func test_CachedImage_ReturnedWithoutImageHolderRequest() {
    let testImage = createTestImage()
    mockImageHolder.mockImage = testImage

    let expectation1 = XCTestExpectation(description: "First load")
    imageLoader.loadImage { _ in
      expectation1.fulfill()
    }
    wait(for: [expectation1], timeout: 1.0)

    // Reset the mock to verify it's not called again
    mockImageHolder.mockImage = nil
    mockImageHolder.requestCount = 0

    let expectation2 = XCTestExpectation(description: "Second load")
    var resultImage: UIImage?
    imageLoader.loadImage { image in
      resultImage = image
      expectation2.fulfill()
    }

    wait(for: [expectation2], timeout: 1.0)
    XCTAssertNotNil(resultImage)
    XCTAssertEqual(resultImage, testImage)
    XCTAssertEqual(mockImageHolder.requestCount, 0)
  }

  func test_MultipleRequests_MakesOnlyOneImageHolderRequest() {
    let testImage = createTestImage()
    mockImageHolder.mockImage = testImage

    let expectation1 = XCTestExpectation(description: "First request")
    let expectation2 = XCTestExpectation(description: "Second request")

    imageLoader.loadImage { _ in
      expectation1.fulfill()
    }

    imageLoader.loadImage { _ in
      expectation2.fulfill()
    }

    wait(for: [expectation1, expectation2], timeout: 1.0)
    XCTAssertEqual(mockImageHolder.requestCount, 1)
  }

  private func createTestImage() -> UIImage {
    let renderer = UIGraphicsImageRenderer(size: CGSize(width: 100, height: 100))
    return renderer.image { context in
      UIColor.red.setFill()
      context.fill(CGRect(x: 0, y: 0, width: 100, height: 100))
    }
  }
}

private class MockImageHolder: ImageHolder {
  var mockImage: UIImage?
  var requestCount = 0

  var image: Image? { mockImage }
  var placeholder: ImagePlaceholder? { nil }
  var debugDescription: String { "MockImageHolder" }

  func requestImageWithCompletion(_ completion: @escaping @MainActor (Image?) -> Void)
    -> Cancellable? {
    requestCount += 1
    DispatchQueue.main.async {
      completion(self.mockImage)
    }
    return nil
  }

  func requestImageWithSource(_ completion: @escaping CompletionHandlerWithSource) -> Cancellable? {
    requestCount += 1
    DispatchQueue.main.async {
      if let image = self.mockImage {
        completion((image, .network))
      } else {
        completion(nil)
      }
    }
    return nil
  }

  func reused(with _: ImagePlaceholder?, remoteImageURL _: URL?) -> ImageHolder? {
    nil
  }

  func equals(_: ImageHolder) -> Bool {
    false
  }
}
