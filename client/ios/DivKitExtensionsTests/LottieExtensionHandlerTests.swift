@testable import DivKit
import DivKitTestsSupport
import DivKitExtensions
import LayoutKit
import VGSL
import XCTest

final class LottieExtensionHandlerTests: XCTestCase {
  private var requester = MockURLResourceRequester()
  private var factory = MockLottieAnimationFactory()
  private var expressionResolver = DivBlockModelingContext.default.expressionResolver
  private lazy var lottieExtensionHandler = LottieExtensionHandler(
    factory: factory,
    requester: requester
  )
  
  func test_getPreloadURLs() {
    let lottieURLString = "https://example.com/DivGif9.json"
    let divWithExtension = divContainer(
      id: "container_with_extension",
      extensions: [
        DivExtension(
          id: lottieExtensionHandler.id,
          params: [
            "lottie_url": lottieURLString,
            "repeat_count": 2,
            "repeat_mode": "reverse"
          ]
        )
      ],
      items: []
    )

    let castedExtensionHandler: DivExtensionHandler = lottieExtensionHandler
    let preloadURLs = castedExtensionHandler.getPreloadURLs(
      div: divWithExtension.value,
      expressionResolver: expressionResolver
    )
    
    XCTAssertEqual(preloadURLs, [URL(string: lottieURLString)])
  }
}

private final class MockLottieAnimationFactory: AsyncSourceAnimatableViewFactory {
  var returnView = MockAnimatableView(frame: .zero)
  func createAsyncSourceAnimatableView(withMode mode: AnimationRepeatMode, repeatCount count: Float)
  -> AsyncSourceAnimatableView {
    returnView
  }
}

private final class MockAnimatableView: UIView, AsyncSourceAnimatableView {
  var playCallsCount = 0
  var receivedSources: [any DivKitExtensions.AnimationSourceType] = []
  
  func play() {
    playCallsCount += 1
  }
  
  func setSourceAsync(_ source: any DivKitExtensions.AnimationSourceType) async {
    receivedSources.append(source)
  }
  
  
}
