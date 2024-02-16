@testable import DivKit

import Foundation

func divData(
  logId: String = DivKitTests.cardId.rawValue,
  states: [DivData.State]
) -> DivData {
  DivData(
    logId: logId,
    states: states,
    timers: nil,
    transitionAnimationSelector: nil,
    variableTriggers: nil,
    variables: nil
  )
}

func divData(_ div: Div) -> DivData {
  divData(
    states: [.init(div: div, stateId: 0)]
  )
}

func divGifImage(
  accessibility: DivAccessibility? = nil,
  gifUrl: String,
  height: DivSize? = nil,
  width: DivSize? = nil
) -> Div {
  .divGifImage(DivGifImage(
    accessibility: accessibility,
    gifUrl: .value(url(gifUrl)),
    height: height,
    width: width
  ))
}

func divInput(
  accessibility: DivAccessibility? = nil,
  textVariable: String
) -> Div {
  .divInput(DivInput(
    accessibility: accessibility,
    textVariable: textVariable
  ))
}

func divImage(
  accessibility: DivAccessibility? = nil,
  height: DivSize? = nil,
  imageUrl: String,
  width: DivSize? = nil
) -> Div {
  .divImage(DivImage(
    accessibility: accessibility,
    height: height,
    imageUrl: .value(url(imageUrl)),
    width: width
  ))
}

func divText(
  accessibility: DivAccessibility? = nil,
  actions: [DivAction]? = nil,
  fontSize: Int? = nil,
  fontWeight: DivFontWeight? = nil,
  id: String? = nil,
  margins: DivEdgeInsets? = nil,
  paddings: DivEdgeInsets? = nil,
  text: String = "",
  textExpression: String? = nil,
  width: DivSize? = nil,
  visibilityActions: [DivVisibilityAction]? = nil
) -> Div {
  let textValue: Expression<String>
  if let textExpression {
    textValue = expression(textExpression)
  } else {
    textValue = .value(text)
  }
  return .divText(DivText(
    accessibility: accessibility,
    actions: actions,
    fontSize: fontSize.map { .value($0) },
    fontWeight: fontWeight.map { .value($0) },
    id: id,
    margins: margins,
    paddings: paddings,
    text: textValue,
    visibilityActions: visibilityActions,
    width: width
  ))
}

func divSeparator(
  accessibility: DivAccessibility? = nil,
  actions: [DivAction]? = nil,
  delimiterStyle: DivSeparator.DelimiterStyle? = nil,
  id: String? = nil,
  margins: DivEdgeInsets? = nil,
  paddings: DivEdgeInsets? = nil,
  transitionIn: DivAppearanceTransition? = nil,
  transitionTriggers: [DivTransitionTrigger]? = nil,
  visibility: Expression<DivVisibility>? = nil
) -> Div {
  .divSeparator(DivSeparator(
    accessibility: accessibility,
    actions: actions,
    delimiterStyle: delimiterStyle,
    id: id,
    margins: margins,
    paddings: paddings,
    transitionIn: transitionIn,
    transitionTriggers: transitionTriggers,
    visibility: visibility
  ))
}

func divContainer(
  accessibility: DivAccessibility? = nil,
  actions: [DivAction]? = nil,
  height: DivSize? = nil,
  itemBuilder: DivCollectionItemBuilder? = nil,
  items: [Div]? = nil,
  width: DivSize? = nil
) -> Div {
  .divContainer(DivContainer(
    accessibility: accessibility,
    action: nil,
    actionAnimation: nil,
    actions: actions,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    aspect: nil,
    background: nil,
    border: nil,
    clipToBounds: nil,
    columnSpan: nil,
    contentAlignmentHorizontal: nil,
    contentAlignmentVertical: nil,
    disappearActions: nil,
    doubletapActions: nil,
    extensions: nil,
    focus: nil,
    height: height,
    id: nil,
    itemBuilder: itemBuilder,
    items: items,
    layoutMode: nil,
    lineSeparator: nil,
    longtapActions: nil,
    margins: nil,
    orientation: nil,
    paddings: nil,
    rowSpan: nil,
    selectedActions: nil,
    separator: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: width
  ))
}

func divGallery(
  items: [Div]
) -> Div {
  .divGallery(DivGallery(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnCount: nil,
    columnSpan: nil,
    crossContentAlignment: nil,
    crossSpacing: nil,
    defaultItem: nil,
    disappearActions: nil,
    extensions: nil,
    focus: nil,
    height: nil,
    id: nil,
    itemSpacing: nil,
    items: items,
    margins: nil,
    orientation: nil,
    paddings: nil,
    restrictParentScroll: nil,
    rowSpan: nil,
    scrollMode: nil,
    scrollbar: nil,
    selectedActions: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: nil
  ))
}

func divGrid(
  columnCount: Int,
  items: [Div]
) -> Div {
  .divGrid(DivGrid(
    accessibility: nil,
    action: nil,
    actionAnimation: nil,
    actions: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnCount: .value(columnCount),
    columnSpan: nil,
    contentAlignmentHorizontal: nil,
    contentAlignmentVertical: nil,
    disappearActions: nil,
    doubletapActions: nil,
    extensions: nil,
    focus: nil,
    height: nil,
    id: nil,
    items: items,
    longtapActions: nil,
    margins: nil,
    paddings: nil,
    rowSpan: nil,
    selectedActions: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: nil
  ))
}

func divPager(
  items: [Div],
  layoutMode: DivPagerLayoutMode
) -> Div {
  .divPager(DivPager(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    defaultItem: nil,
    disappearActions: nil,
    extensions: nil,
    focus: nil,
    height: nil,
    id: nil,
    infiniteScroll: nil,
    itemSpacing: nil,
    items: items,
    layoutMode: layoutMode,
    margins: nil,
    orientation: nil,
    paddings: nil,
    restrictParentScroll: nil,
    rowSpan: nil,
    selectedActions: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: nil
  ))
}

func divState(
  divId: String,
  states: [DivState.State]
) -> Div {
  .divState(DivState(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    defaultStateId: nil,
    disappearActions: nil,
    divId: divId,
    extensions: nil,
    focus: nil,
    height: nil,
    id: nil,
    margins: nil,
    paddings: nil,
    rowSpan: nil,
    selectedActions: nil,
    stateIdVariable: nil,
    states: states,
    tooltips: nil,
    transform: nil,
    transitionAnimationSelector: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: nil
  ))
}

func divStateState(
  div: Div?,
  stateId: String
) -> DivState.State {
  DivState.State(
    animationIn: nil,
    animationOut: nil,
    div: div,
    stateId: stateId,
    swipeOutActions: nil
  )
}

func divTabs(
  items: [DivTabs.Item]
) -> Div {
  .divTabs(DivTabs(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    disappearActions: nil,
    dynamicHeight: nil,
    extensions: nil,
    focus: nil,
    hasSeparator: nil,
    height: nil,
    id: nil,
    items: items,
    margins: nil,
    paddings: nil,
    restrictParentScroll: nil,
    rowSpan: nil,
    selectedActions: nil,
    selectedTab: nil,
    separatorColor: nil,
    separatorPaddings: nil,
    switchTabsByContentSwipeEnabled: nil,
    tabTitleStyle: nil,
    titlePaddings: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: nil
  ))
}

func divTabsItem(
  div: Div,
  title: String
) -> DivTabs.Item {
  DivTabs.Item(
    div: div,
    title: .value(title),
    titleClickAction: nil
  )
}

func fixedSize(_ value: Int) -> DivSize {
  .divFixedSize(DivFixedSize(value: .value(value)))
}

func wrapContentSize() -> DivSize {
  .divWrapContentSize(DivWrapContentSize())
}
