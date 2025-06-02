import CoreGraphics
import VGSL

struct TabTitlesViewModel {
  let items: [TabTitleViewModel]
  let layoutDirection: UserInterfaceLayoutDirection
  let listPaddings: EdgeInsets
  let titlePaddings: EdgeInsets
  let selectedBackgroundColor: Color
  let backgroundColor: Color
  let cornerRadius: CornerRadii?
  let itemSpacing: CGFloat?
  var selection: CGFloat
  var offset: CGFloat?
  let tabTitleDelimiter: TabTitleDelimiterStyle?

  var totalSpacing: CGFloat {
    (itemSpacing ?? 0) + (tabTitleDelimiter?.width ?? 0)
  }
}
