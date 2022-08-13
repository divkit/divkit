// Copyright 2022 Yandex LLC. All rights reserved.

import SwiftUI
import UIKit

import CommonCore
import Serialization

public typealias LivePreviewLogger = (String) -> Void

public protocol LivePreviewGraph {}

@available(iOS 13, *)
public final class LivePreviewGraphImpl: LivePreviewGraph {
  private let rootView: URLInputView

  public var view: some View {
    rootView
  }

  public var viewController: UIViewController {
    UIHostingController(rootView: rootView)
  }

  public init(
    logger: @escaping LivePreviewLogger,
    urlOpener: @escaping UrlOpener
  ) {
    let socket = DivViewSocket(logger: logger)
    let blockProvider = BlockProvider(
      json: socket.response,
      urlOpener: urlOpener
    )
    let uiStatePayloadFactory = UIStatePayloadFactory(
      deviceInfo: DeviceInfo(),
      errors: blockProvider.$errors
    )
    rootView = URLInputView(
      onURLStringConfirmed: { [socket] in
        guard let (url, uuid) = buildWebSocketURL(from: $0) else { return }
        socket.connect(to: url, uuid: uuid)
      },
      blockProvider: blockProvider,
      sendScreenshot: { [socket] screenshotInfo in
        socket.send(
          state: uiStatePayloadFactory.makePayload(screenshotInfo: screenshotInfo)
        )
      },
      onLivePreviewDisappear: {
        socket.endConnection()
        blockProvider.reset()
      },
      logger: logger
    )
  }
}
