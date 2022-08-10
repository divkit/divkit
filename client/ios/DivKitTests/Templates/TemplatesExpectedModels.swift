@testable import DivKit

import Foundation

enum TemplatesTest {
  static let text: DivData = {
    let text = DivText(
      fontSize: .value(14),
      fontWeight: .value(.bold),
      text: .value("Lorem ipsum dolor sit amet" as CFString)
    )
    return makeDivData(
      logId: "test",
      states: [DivData.State(div: .divText(text), stateId: 0)]
    )
  }()

  static let gallery: DivData = {
    let title = DivText(
      fontSize: .value(16),
      text: .value("Заголовок карточки" as CFString)
    )
    let body = DivText(
      fontSize: .value(12),
      text: .value("Тело карточки" as CFString)
    )
    let image = DivImage(
      height: .divFixedSize(DivFixedSize(value: .value(100))),
      imageUrl: .value(URL(string: "https://alicekit.s3.yandex.net/images_for_divs/chess.png")!),
      width: .divFixedSize(DivFixedSize(value: .value(100)))
    )

    let container = makeDivContainer(
      height: .divFixedSize(DivFixedSize(value: .value(320))),
      items: [.divText(title), .divText(body), .divImage(image)],
      width: .divFixedSize(DivFixedSize(value: .value(260)))
    )

    let gallery = makeDivGallery(
      items:
      (0..<3).map { _ in Div.divContainer(container) }
    )

    return makeDivData(
      logId: "test",
      states: [DivData.State(div: .divGallery(gallery), stateId: 1)]
    )
  }()
}
