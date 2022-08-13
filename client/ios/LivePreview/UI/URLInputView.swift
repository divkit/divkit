// Copyright 2022 Yandex LLC. All rights reserved.

import SwiftUI

import CommonCore

@_implementationOnly import LayoutKit

@available(iOS 13, *)
struct URLInputView: View {
  @State
  private var urlString = ""
  @State
  private var isLivePreviewActive = false

  let onURLStringConfirmed: (String) -> Void
  let blockProvider: BlockProvider
  let sendScreenshot: ScreenshotCallback
  let onLivePreviewDisappear: Action
  let logger: LivePreviewLogger

  var body: some View {
    NavigationView {
      GeometryReader { geo in
        ZStack(alignment: .topLeading) {
          VStack(alignment: .center, spacing: 15) {
            ScannerView(result: $urlString, logger: logger)
              .frame(
                width: geo.size.width,
                height: geo.size.height * 0.75,
                alignment: .center
              )
            TextField("URL", text: $urlString)
              .textFieldStyle(.roundedBorder)
              .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 15))
            HStack(alignment: .center, spacing: 50) {
              NavigationLink(
                destination: livePreviewView
                  .navigationBarHidden(true)
                  .onDisappear(perform: onLivePreviewDisappear),
                isActive: $isLivePreviewActive
              ) {
                Button("OK") {
                  isLivePreviewActive = true
                  onURLStringConfirmed(urlString)
                }
              }
              Button("Clear") { urlString = "" }
            }
          }
        }
      }
      .navigationBarHidden(true)
    }
  }

  private var livePreviewView: some View {
    LivePreviewView(
      blockProvider: blockProvider,
      onScreenshotTaken: sendScreenshot
    )
  }
}
