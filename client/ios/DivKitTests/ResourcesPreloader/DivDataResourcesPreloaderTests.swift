@testable import DivKit
import DivKitTestsSupport
import VGSL
import XCTest

final class DivDataResourcesPreloaderTests: XCTestCase {
  private let expressionResolver = DivBlockModelingContext.default.expressionResolver

  private let mockRequester = MockURLResourceRequester()
  private lazy var preloader = DivDataResourcesPreloader(resourceRequester: mockRequester)

  func test_WhenNoURLsToDownload_CompletesSuccessfully() {
    let divData = createEmptyDivData()

    downloadResourcesAndWait(for: divData, using: preloader)
    XCTAssertTrue(mockRequester.requestedURLs.isEmpty)
  }

  func test_WhenAllRequestsSucceed_CompletesSuccessfully() {
    mockRequester.shouldSucceed = true
    let preloader = DivDataResourcesPreloader(resourceRequester: mockRequester)
    let divData = createDivDataWithResources(preloadRequired: false)

    downloadResourcesAndWait(for: divData, using: preloader)
    XCTAssertEqual(mockRequester.requestedURLs.count, 4)
  }

  func test_WhenAllRequestsFail_CompletesWithFailure() {
    mockRequester.shouldSucceed = false
    let preloader = DivDataResourcesPreloader(resourceRequester: mockRequester)
    let divData = createDivDataWithResources(preloadRequired: false)

    downloadResourcesAndWait(for: divData, using: preloader, expectSuccess: false)
    XCTAssertEqual(mockRequester.requestedURLs.count, 4)
  }

  func test_WhenSomeRequestsSucceed_AndSomeFail_CompletesWithFailure() {
    let mockRequester = MockURLResourceRequester()
    let preloader = DivDataResourcesPreloader(resourceRequester: mockRequester)
    let divData = createDivDataWithResources(preloadRequired: false)

    var requestCount = 0
    mockRequester.customBehavior = { _ in
      requestCount += 1
      return requestCount % 2 == 1
    }

    downloadResourcesAndWait(for: divData, using: preloader, expectSuccess: false)
    XCTAssertEqual(mockRequester.requestedURLs.count, 4)
  }

  func test_FiltersURLsWithNoHost() {
    let divData = createDivDataWithInvalidURLs()

    downloadResourcesAndWait(for: divData, using: preloader)

    let expectedURLs = [
      URL(string: validImageURL)!,
      URL(string: validVideoURL)!,
    ]
    XCTAssertEqual(mockRequester.requestedURLs, expectedURLs)
  }

  func test_WhenFilterIsOnlyRequired_OnlyDownloadsRequiredResources() {
    let divData = createDivDataWithMixedResources()

    downloadResourcesAndWait(for: divData, using: preloader, filter: .onlyRequired)

    XCTAssertEqual(mockRequester.requestedURLs, requiredURLs.map { URL(string: $0)! })
  }

  func test_WhenImageUrlIsInVariableStorage_PrefetchesCorrectly() {
    let variableStorage = DivVariableStorage()
    variableStorage.put(name: "image_var", value: .url(URL(string: requiredImageURL)!))

    let context = DivBlockModelingContext(variableStorage: variableStorage)

    let divWithVariableImage = divImage(
      id: "image_from_variable",
      imageUrlExpression: "@{image_var}"
    )

    let container = divContainer(
      id: "container_with_variable",
      items: [divWithVariableImage]
    )

    let divData = divData(container)

    downloadResourcesAndWait(for: divData, using: preloader, context: context)

    XCTAssertEqual(mockRequester.requestedURLs, [URL(string: requiredImageURL)!])
  }

  func test_WhenExtensionHandlerWithURL_PrefetchesCorrectly() {
    let mockRequester = MockURLResourceRequester()
    mockRequester.shouldSucceed = true
    let preloader = DivDataResourcesPreloader(
      resourceRequester: mockRequester
    )

    let mockExtensionHandler = MockDivExtensionHandler()
    let invalidExtensionHandler = MockDivExtensionHandler()
    invalidExtensionHandler.id = "invalid_extension_id"
    let context = DivBlockModelingContext(
      extensionHandlers: [
        mockExtensionHandler,
        invalidExtensionHandler
      ]
    )

    let divWithExtension = divContainer(
      id: "container_with_extension",
      extensions: [
        DivExtension(
          id: mockExtensionHandler.id,
          params: [:]
        ),
        DivExtension(
          id: "other_invalid_extension_id",
          params: [:]
        ),
      ],
      items: []
    )

    let divData = divData(divWithExtension)

    downloadResourcesAndWait(
      for: divData, 
      using: preloader,
      context: context
    )

    XCTAssertEqual(mockRequester.requestedURLs, [mockExtensionHandler.preloadURL])
  }
  
  func test_WhenContainerHasNestedItemBuilders_WithAllFilter_PreloadsAllResources() {
    let divData = createDivDataWithNestedItemBuilders()
    
    downloadResourcesAndWait(for: divData, using: preloader)
    
    XCTAssertEqual(
      Set(mockRequester.requestedURLs.map { $0.absoluteString }),
      Set([requiredImageURL, optionalImageURL, validImageURL])
    )
  }
  
  func test_WhenItemBuilderUsesVariables_WithRequiredFilter_PreloadsOnlyRequiredResources() {
    let mockRequester = MockURLResourceRequester()
    mockRequester.shouldSucceed = true
    let preloader = DivDataResourcesPreloader(resourceRequester: mockRequester)
    
    let variableStorage = DivVariableStorage()
    variableStorage.put(name: "image_url_var", value: .url(URL(string: requiredImageURL)!))
    variableStorage.put(name: "preload_required_var", value: .bool(true))
    
    let context = DivBlockModelingContext(variableStorage: variableStorage)
    let divData = createDivDataWithVariables()
    
    downloadResourcesAndWait(for: divData, using: preloader, filter: .onlyRequired, context: context)
    
    XCTAssertEqual(
      Set(mockRequester.requestedURLs.map { $0.absoluteString }),
      Set([requiredImageURL])
    )
  }
  
  func test_WhenContainerHasItemBuilder_WithRequiredFilter_OnlyDownloadsRequiredResources() {
    let divData = createDivDataWithMixedItemBuilderResources()
    
    downloadResourcesAndWait(for: divData, using: preloader, filter: .onlyRequired)
    
    XCTAssertEqual(
      Set(mockRequester.requestedURLs.map { $0.absoluteString }),
      Set([requiredImageURL, requiredVideoURL])
    )
  }
  
  func test_WhenContainerHasItemBuilder_WithAllFilter_DownloadsAllResources() {
    let divData = createDivDataWithMixedItemBuilderResources()
    
    downloadResourcesAndWait(for: divData, using: preloader)
    
    XCTAssertEqual(
      Set(mockRequester.requestedURLs.map { $0.absoluteString }),
      Set([requiredImageURL, optionalImageURL, requiredVideoURL, optionalVideoURL])
    )
  }
  
  func test_WhenDivDataHasVariables_WithRequiredFilter_PreloadsCorrectly() {
    let divData = createDivDataWithDivVariables()
    
    downloadResourcesAndWait(for: divData, using: preloader, filter: .onlyRequired)
    
    XCTAssertEqual(
      Set(mockRequester.requestedURLs.map { $0.absoluteString }),
      Set([requiredImageURL])
    )
  }
  
  private func createDivDataWithDivVariables() -> DivData {
    let imageWithVariable = divImage(
      id: "image_with_variable",
      imageUrlExpression: "@{image_var}",
      preloadRequired: true
    )
    
    let container = divContainer(
      id: "container_with_variables",
      items: [imageWithVariable]
    )
    
    return DivData(
      functions: nil,
      logId: DivBlockModelingContext.testCardId.rawValue,
      states: [.init(div: container, stateId: 0)],
      timers: nil,
      transitionAnimationSelector: nil,
      variableTriggers: nil,
      variables: [
        .urlVariable(UrlVariable(name: "image_var", value: .value(URL(string: requiredImageURL)!)))
      ]
    )
  }
  
  private func downloadResourcesAndWait(
    for divData: DivData,
    using preloader: DivDataResourcesPreloader,
    filter: ResourcePreloadFilter = .all,
    context: DivBlockModelingContext = .default,
    expectSuccess: Bool = true
  ) {
    let expectation = XCTestExpectation(description: "Download completion")
    preloader.downloadResources(
      for: divData,
      filter: filter,
      context: context
    ) { success in
      if expectSuccess {
        XCTAssertTrue(success)
      } else {
        XCTAssertFalse(success)
      }
      expectation.fulfill()
    }
    
    wait(for: [expectation], timeout: 1.0)
  }
  
  private func createDivDataWithNestedItemBuilders() -> DivData {
    let outerData: [Any] = [
      ["type": "container", "items": [
        ["url": requiredImageURL, "preload_required": true],
        ["url": optionalImageURL, "preload_required": false]
      ]],
      ["type": "image", "url": validImageURL, "preload_required": true]
    ]
    
    let innerImagePrototype = DivCollectionItemBuilder.Prototype(
      div: divImage(
        id: "inner_image",
        imageUrlExpression: "@{getStringFromDict(item, 'url')}",
        preloadRequiredExpression: "@{getBooleanFromDict(item, 'preload_required')}"
      )
    )
    
    let innerItemBuilder = DivCollectionItemBuilder(
      data: .value(outerData.flatMap { item in
        guard let dict = item as? [String: Any],
              let items = dict["items"] as? [[String: Any]] else { return [] }
        return items
      }),
      dataElementName: "item",
      prototypes: [innerImagePrototype]
    )
    
    let containerPrototype = DivCollectionItemBuilder.Prototype(
      div: divContainer(
        id: "container_from_item_builder",
        itemBuilder: innerItemBuilder
      ),
      selector: expression("@{getStringFromDict(item, 'type') == 'container'}")
    )
    
    let imagePrototype = DivCollectionItemBuilder.Prototype(
      div: divImage(
        id: "image_from_item_builder",
        imageUrlExpression: "@{getStringFromDict(item, 'url')}",
        preloadRequiredExpression: "@{getBooleanFromDict(item, 'preload_required')}"
      ),
      selector: expression("@{getStringFromDict(item, 'type') == 'image'}")
    )
    
    let outerItemBuilder = DivCollectionItemBuilder(
      data: .value(outerData),
      dataElementName: "item",
      prototypes: [containerPrototype, imagePrototype]
    )
    
    let container = divContainer(
      id: "container_with_nested_item_builders",
      itemBuilder: outerItemBuilder
    )
    
    return divData(container)
  }
  
  private func createDivDataWithVariables() -> DivData {
    let data: [Any] = [
      ["type": "image", "use_variable": true],
      ["type": "image", "url": optionalImageURL, "preload_required": false]
    ]
    
    let variableImagePrototype = DivCollectionItemBuilder.Prototype(
      div: divImage(
        id: "image_with_variable",
        imageUrlExpression: "@{getBooleanFromDict(item, 'use_variable') ? image_url_var : getStringFromDict(item, 'url')}",
        preloadRequiredExpression: "@{getBooleanFromDict(item, 'use_variable') ? preload_required_var : getBooleanFromDict(item, 'preload_required')}"
      ),
      selector: expression("@{getStringFromDict(item, 'type') == 'image'}")
    )
    
    let itemBuilder = DivCollectionItemBuilder(
      data: .value(data),
      dataElementName: "item",
      prototypes: [variableImagePrototype]
    )
    
    let container = divContainer(
      id: "container_with_variables",
      itemBuilder: itemBuilder
    )
    
    return divData(container)
  }
  
  private func createDivDataWithMixedItemBuilderResources() -> DivData {
    let data: [[String: Any]] = [
      ["type": "image", "url": requiredImageURL, "preload_required": true],
      ["type": "image", "url": optionalImageURL, "preload_required": false],
      ["type": "video", "url": requiredVideoURL, "preload_required": true],
      ["type": "video", "url": optionalVideoURL, "preload_required": false]
    ]

    let imagePrototype = DivCollectionItemBuilder.Prototype(
      div: divImage(
        id: "image_from_item_builder",
        imageUrlExpression: "@{getStringFromDict(it, 'url')}",
        preloadRequiredExpression: "@{getBooleanFromDict(it, 'preload_required')}"
      ),
      selector: expression("@{getStringFromDict(it, 'type') == 'image'}")
    )
    
    let videoPrototype = DivCollectionItemBuilder.Prototype(
      div: divVideo(
        id: "video_from_item_builder",
        videoSources: [
          divVideoSource(
            mimeType: "video/mp4",
            urlExpression: "@{getStringFromDict(it, 'url')}"
          )
        ],
        preloadRequiredExpression: "@{getBooleanFromDict(it, 'preload_required')}"
      ),
      selector: expression("@{getStringFromDict(it, 'type') == 'video'}")
    )
    
    let itemBuilder = DivCollectionItemBuilder(
      data: .value(data),
      prototypes: [videoPrototype, imagePrototype]
    )
    
    let container = divContainer(
      id: "container_with_item_builder",
      itemBuilder: itemBuilder
    )
    
    return divData(container)
  }
}

private func createDivVideo(id: String, url: String, preloadRequired: Bool) -> Div {
  divVideo(
    id: id,
    videoSources: [
      divVideoSource(
        mimeType: "video/mp4",
        url: url
      ),
    ],
    preloadRequired: preloadRequired
  )
}

private func createDivTextWithImage(id: String, url: String, preloadRequired: Bool) -> Div {
  .divText(DivText(
    id: id,
    images: [
      divTextImage(
        url: url,
        preloadRequired: preloadRequired
      ),
    ],
    text: .value("Text with image")
  ))
}

private func createDivWithBackground(id: String, url: String, preloadRequired: Bool) -> Div {
  divContainer(
    id: id,
    items: [
      divText(id: "text_in_container", text: "Container with background"),
    ],
    background: [
      divImageBackground(
        imageUrl: url,
        preloadRequired: preloadRequired
      ),
    ]
  )
}

private func createEmptyDivData() -> DivData {
  let text = divText(id: "text", text: "No images or videos here")
  return divData(text)
}

private func createDivDataWithResources(preloadRequired: Bool = false) -> DivData {
  let image = divImage(
    id: "image",
    imageUrl: validImageURL,
    preloadRequired: preloadRequired
  )

  let video = createDivVideo(
    id: "video",
    url: validVideoURL,
    preloadRequired: preloadRequired
  )

  let textWithImage = createDivTextWithImage(
    id: "text_with_image",
    url: validTextImageURL,
    preloadRequired: preloadRequired
  )

  let containerWithBackground = createDivWithBackground(
    id: "container_with_background",
    url: validBackgroundURL,
    preloadRequired: preloadRequired
  )

  let container = divContainer(
    id: "container",
    items: [image, video, textWithImage, containerWithBackground]
  )

  return divData(container)
}

private func createDivDataWithMixedResources() -> DivData {
  let requiredImage = divImage(
    id: "required_image",
    imageUrl: requiredImageURL,
    preloadRequired: true
  )

  let optionalImage = divImage(
    id: "optional_image",
    imageUrl: optionalImageURL,
    preloadRequired: false
  )

  let requiredVideo = createDivVideo(
    id: "required_video",
    url: requiredVideoURL,
    preloadRequired: true
  )

  let optionalVideo = createDivVideo(
    id: "optional_video",
    url: optionalVideoURL,
    preloadRequired: false
  )

  let requiredTextImage = createDivTextWithImage(
    id: "required_text_image",
    url: requiredTextImageURL,
    preloadRequired: true
  )

  let optionalTextImage = createDivTextWithImage(
    id: "optional_text_image",
    url: optionalTextImageURL,
    preloadRequired: false
  )

  let requiredBackground = createDivWithBackground(
    id: "required_background",
    url: requiredBackgroundURL,
    preloadRequired: true
  )

  let optionalBackground = createDivWithBackground(
    id: "optional_background",
    url: optionalBackgroundURL,
    preloadRequired: false
  )

  let container = divContainer(
    id: "container",
    items: [
      requiredImage, optionalImage,
      requiredVideo, optionalVideo,
      requiredTextImage, optionalTextImage,
      requiredBackground, optionalBackground,
    ]
  )

  return divData(container)
}

private func createDivDataWithInvalidURLs() -> DivData {
  let validImage = divImage(
    id: "valid_image",
    imageUrl: validImageURL,
    preloadRequired: false
  )

  let invalidImage = divImage(
    id: "invalid_image",
    imageUrl: invalidLocalImageURL,
    preloadRequired: false
  )

  let container = divContainer(
    id: "container",
    items: [
      validImage,
      invalidImage,
      divVideo(
        id: "valid_video",
        videoSources: [
          divVideoSource(
            mimeType: "video/mp4",
            url: validVideoURL
          ),
        ],
        preloadRequired: false
      ),
      divVideo(
        id: "invalid_video",
        videoSources: [
          divVideoSource(
            mimeType: "video/mp4",
            url: invalidRelativeVideoURL
          ),
        ],
        preloadRequired: false
      ),
    ]
  )

  return divData(container)
}

private func testURL(_ path: String, domain: String = "example.com") -> String {
  return "https://\(domain)/\(path)"
}

private let requiredImageURL = testURL("required_image.jpg")
private let optionalImageURL = testURL("optional_image.jpg")
private let requiredVideoURL = testURL("required_video.mp4")
private let optionalVideoURL = testURL("optional_video.mp4")
private let requiredTextImageURL = testURL("required_text_image.jpg")
private let optionalTextImageURL = testURL("optional_text_image.jpg")
private let requiredBackgroundURL = testURL("required_background.jpg")
private let optionalBackgroundURL = testURL("optional_background.jpg")

private let validImageURL = testURL("image.jpg")
private let validVideoURL = testURL("video.mp4")
private let validTextImageURL = testURL("text_image.jpg")
private let validBackgroundURL = testURL("background.jpg")
private let invalidLocalImageURL = "file:///local/image.jpg"
private let invalidRelativeVideoURL = "relative/path/video.mp4"

private let requiredURLs = [
  requiredImageURL,
  requiredVideoURL,
  requiredTextImageURL,
  requiredBackgroundURL,
]

private let optionalURLs = [
  optionalImageURL,
  optionalVideoURL,
  optionalTextImageURL,
  optionalBackgroundURL,
]
