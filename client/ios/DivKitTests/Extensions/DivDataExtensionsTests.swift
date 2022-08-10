@testable import DivKit
@testable import LayoutKit

import XCTest

import BaseUI
import CommonCore

final class DivDataExtensionsTests: XCTestCase {
  func test_WhenStateIsNil_TakesFirstDiv() throws {
    let block = try data.makeBlock(
      context: modified(.default) {
        $0.parentDivStatePath = nil
      }
    ).withoutStateBlock()
    let expectedBlock = try data.states[0].div.value
      .makeBlock(context: .default)
    XCTAssertTrue(block == expectedBlock)
  }

  func test_WhenStateIsPresent_TakesCorrespondingDiv() throws {
    let block = try data.makeBlock(
      context: modified(.default) {
        $0.parentDivStatePath = DivStatePath(rawValue: UIElementPath("1"))
      }
    ).withoutStateBlock()
    let expectedBlock = try data.states[1].div.value
      .makeBlock(context: .default)
    XCTAssertTrue(block == expectedBlock)
  }

  func test_WhenStateIsNotPresent_TakesFirstState() throws {
    let block = try data.makeBlock(
      context: modified(.default) {
        $0.parentDivStatePath = DivStatePath(rawValue: UIElementPath("100500"))
      }
    ).withoutStateBlock()
    let expectedBlock = try data.states[0].div.value
      .makeBlock(context: .default)
    XCTAssertTrue(block == expectedBlock)
  }

  func test_PathContainsRoot() throws {
    let block = try dataWithDivWithAction
      .makeBlock(context: .default)
      .withoutStateBlock() as? DecoratingBlock
    let expectedActionPath = UIElementPath.root + divWithAction.action!.logId
    XCTAssertEqual(block?.actions?.first.path, expectedActionPath)
  }

  func test_GalleryPathContainsRoot() throws {
    let block = try dataWithGallery
      .makeBlock(context: .default)
      .withoutStateBlock() as? DecoratingBlock
    let galleryBlock = block?.child as? GalleryBlock
    let expectedPath = UIElementPath.root + "0" + DivGallery.type
    XCTAssertEqual(galleryBlock?.model.path, expectedPath)
  }

  func test_WhenTopLevelDivIsNotGallery_DiscardsResizableInsetsParams() throws {
    let context = DivBlockModelingContext(
      galleryResizableInsets: InsetMode.Resizable(minValue: 16, maxViewportSize: 240)
    )
    let block = try dataWithGalleryInContainer
      .makeBlock(context: context)
      .withoutStateBlock() as? DecoratingBlock
    let containerBlock = block?.child as? ContainerBlock
    let galleryContainerBlock = containerBlock?.children.first?.content as? DecoratingBlock
    let innerGalleryBlock = galleryContainerBlock?.child as? GalleryBlock
    let expectedInsets = SideInsets.zero
    XCTAssertEqual(containerBlock?.gaps, expectedInsets.asArray())
    XCTAssertEqual(innerGalleryBlock?.model.metrics.axialInsetMode, .fixed(values: expectedInsets))
  }
}

private let data = makeDivData(
  logId: DivKitTests.cardLogId,
  states: [
    .init(div: .divText(DivText(text: .value("0" as CFString))), stateId: 0),
    .init(div: .divText(DivText(text: .value("1" as CFString))), stateId: 1),
    .init(div: .divText(DivText(text: .value("2" as CFString))), stateId: 2),
  ]
)

private let divWithAction = DivText(
  action: DivAction(logId: "action_log_id"),
  text: .value("0" as CFString)
)

private let dataWithDivWithAction = makeDivData(
  logId: DivKitTests.cardLogId,
  states: [.init(div: .divText(divWithAction), stateId: 0)]
)

private let gallery = makeDivGallery(
  items: [
    .divText(DivText(
      text: .value("0" as CFString),
      width: .divFixedSize(.init(value: .value(10)))
    )),
  ]
)

private let dataWithGallery = makeDivData(
  logId: DivKitTests.cardLogId,
  states: [.init(div: .divGallery(gallery), stateId: 0)]
)

private let container = makeDivContainer(
  items: [.divGallery(gallery)]
)

private let dataWithGalleryInContainer = makeDivData(
  logId: DivKitTests.cardLogId,
  states: [.init(div: .divContainer(container), stateId: 0)]
)

extension Block {
  fileprivate func withoutStateBlock() -> Block {
    if let stateBlock = self as? StateBlock {
      return stateBlock.child
    }
    return self
  }
}
