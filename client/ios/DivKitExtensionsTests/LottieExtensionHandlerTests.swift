@testable import DivKit
@testable import DivKitExtensions
import DivKitTestsSupport
@testable import LayoutKit
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
            "repeat_mode": "reverse",
          ]
        ),
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

  func test_expressions() {
    let variableStorage = DivVariableStorage()
    variableStorage.put(name: "lottie_url", value: .string("https://example.com/DivGif1.json"))
    variableStorage.put(name: "repeat_count", value: .integer(2))
    variableStorage.put(name: "repeat_mode", value: .string("reverse"))
    let context = DivBlockModelingContext(
      extensionHandlers: [
        lottieExtensionHandler,
      ],
      variableStorage: variableStorage
    )

    let blockWithExpressions = makeBlock(
      divContainer(
        id: "container_with_extension",
        extensions: [
          DivExtension(
            id: lottieExtensionHandler.id,
            params: [
              "lottie_url": "@{lottie_url}",
              "repeat_count": "@{repeat_count}",
              "repeat_mode": "@{repeat_mode}",
            ]
          ),
        ],
        items: []
      ),
      context: context
    )

    let expectedBlock = makeExpectedBlockBlock(
      factory: factory,
      requester: requester,
      url: URL(string: "https://example.com/DivGif1.json")!,
      repeatCount: 2,
      repeatMode: .reverse,
      context: context
    )

    assertEqual(blockWithExpressions, expectedBlock)
  }
}

private final class MockLottieAnimationFactory: AsyncSourceAnimatableViewFactory {
  var returnView = MockAnimatableView(frame: .zero)
  func createAsyncSourceAnimatableView(withMode _: AnimationRepeatMode, repeatCount _: Float)
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

  func pause() {}

  func setSourceAsync(_ source: any DivKitExtensions.AnimationSourceType) async {
    receivedSources.append(source)
  }
}

private func makeExpectedBlockBlock(
  factory: AsyncSourceAnimatableViewFactory,
  requester: URLResourceRequesting,
  url: URL,
  repeatCount: Float,
  repeatMode: AnimationRepeatMode,
  context: DivBlockModelingContext
) -> StateBlock {
  StateBlock(
    child: LottieAnimationBlock(
      animatableView: Lazy(
        getter: {
          factory.createAsyncSourceAnimatableView(
            withMode: repeatMode,
            repeatCount: repeatCount
          )
        }
      ),
      animationHolder: RemoteAnimationHolder(
        url: url,
        animationType: .lottie,
        requester: requester,
        localDataProvider: nil
      ),
      sizeProvider: DecoratingBlock(
        child: try! ContainerBlock(
          layoutDirection: .vertical,
          children: [],
          path: context.parentPath + "0" + "container_with_extension"
        ),
        accessibilityElement: accessibility(identifier: "container_with_extension")
      ),
      scale: .fit,
      isPlaying: true
    ),
    ids: []
  )
}
