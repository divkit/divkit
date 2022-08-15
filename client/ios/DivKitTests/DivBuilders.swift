@testable import DivKit

func makeDivData(
  logId: String,
  states: [DivData.State]
) -> DivData {
  DivData(
    logId: logId,
    states: states,
    transitionAnimationSelector: nil,
    variableTriggers: nil,
    variables: nil
  )
}

func makeDivSeparator(
  id: String? = nil,
  transitionIn: DivAppearanceTransition? = nil,
  transitionTriggers: [DivTransitionTrigger]? = nil,
  visibility: Expression<DivVisibility>? = nil
) -> DivSeparator {
  DivSeparator(
    accessibility: nil,
    action: nil,
    actionAnimation: nil,
    actions: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    delimiterStyle: nil,
    doubletapActions: nil,
    extensions: nil,
    focus: nil,
    height: nil,
    id: id,
    longtapActions: nil,
    margins: nil,
    paddings: nil,
    rowSpan: nil,
    selectedActions: nil,
    tooltips: nil,
    transitionChange: nil,
    transitionIn: transitionIn,
    transitionOut: nil,
    transitionTriggers: transitionTriggers,
    visibility: visibility,
    visibilityAction: nil,
    visibilityActions: nil,
    width: nil
  )
}

func makeDivContainer(
  height: DivSize? = nil,
  items: [Div],
  width: DivSize? = nil
) -> DivContainer {
  DivContainer(
    accessibility: nil,
    action: nil,
    actionAnimation: nil,
    actions: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    contentAlignmentHorizontal: nil,
    contentAlignmentVertical: nil,
    doubletapActions: nil,
    extensions: nil,
    focus: nil,
    height: height,
    id: nil,
    items: items,
    longtapActions: nil,
    margins: nil,
    orientation: nil,
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
    width: width
  )
}

func makeDivGallery(
  items: [Div]
) -> DivGallery {
  DivGallery(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnCount: nil,
    columnSpan: nil,
    crossContentAlignment: nil,
    defaultItem: nil,
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
  )
}

func makeDivGrid(
  columnCount: Int,
  items: [Div]
) -> DivGrid {
  DivGrid(
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
  )
}

func makeDivPager(
  items: [Div],
  layoutMode: DivPagerLayoutMode
) -> DivPager {
  DivPager(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    defaultItem: nil,
    extensions: nil,
    focus: nil,
    height: nil,
    id: nil,
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
  )
}

func makeDivState(
  divId: String,
  states: [DivState.State]
) -> DivState {
  DivState(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    defaultStateId: nil,
    divId: divId,
    extensions: nil,
    focus: nil,
    height: nil,
    id: nil,
    margins: nil,
    paddings: nil,
    rowSpan: nil,
    selectedActions: nil,
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
  )
}

func makeDivStateState(
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

func makeDivTabs(
  items: [DivTabs.Item]
) -> DivTabs {
  DivTabs(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
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
  )
}

func makeDivTabsItem(
  div: Div,
  title: String
) -> DivTabs.Item {
  DivTabs.Item(
    div: div,
    title: .value(title),
    titleClickAction: nil
  )
}
