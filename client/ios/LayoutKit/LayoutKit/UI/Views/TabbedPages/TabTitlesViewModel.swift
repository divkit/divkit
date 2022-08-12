// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

import CommonCore

struct TabTitlesViewModel {
  let items: [TabTitleViewModel]
  let listPaddings: EdgeInsets
  let titlePaddings: EdgeInsets
  let selectedBackgroundColor: Color
  let backgroundColor: Color
  let cornerRadius: CornerRadii?
  var selection: CGFloat
  var offset: CGFloat?
}
