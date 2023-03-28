import CoreGraphics

import CommonCorePublic

struct TabTitlesViewModel {
  let items: [TabTitleViewModel]
  let listPaddings: EdgeInsets
  let titlePaddings: EdgeInsets
  let selectedBackgroundColor: Color
  let backgroundColor: Color
  let cornerRadius: CornerRadii?
  let itemSpacing: CGFloat?
  var selection: CGFloat
  var offset: CGFloat?
}
