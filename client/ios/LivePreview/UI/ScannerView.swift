// Copyright 2022 Yandex LLC. All rights reserved.

import SwiftUI

import CommonCore

@available(iOS 13, *)
struct ScannerView: UIViewControllerRepresentable {
  private let disposePool = AutodisposePool()

  @Binding
  var result: String
  let logger: LivePreviewLogger

  func makeUIViewController(context _: Context) -> ScannerViewController {
    let controller = ScannerViewController(logger: logger)
    controller.result.currentAndNewValues.addObserver {
      result = $0
    }.dispose(in: disposePool)
    return controller
  }

  func updateUIViewController(
    _ uiViewController: ScannerViewController,
    context _: Context
  ) {
    uiViewController.result.value = result
  }
}
