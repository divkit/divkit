@testable import DivKit
import enum DivKit.Expression
import Foundation
import LayoutKit
import VGSL

func divAction(
  isEnabled: Bool = true,
  logId: String = "test",
  payload: [String: Any]? = nil,
  scopeId: String? = nil,
  typed: DivActionTyped? = nil,
  url: String? = nil,
  urlExpression: String? = nil
) -> DivAction {
  let urlParam: Expression<URL>? = if let urlExpression {
    expression(urlExpression)
  } else if let url {
    .value(URL(string: url)!)
  } else {
    nil
  }
  return DivAction(
    isEnabled: .value(isEnabled),
    logId: .value(logId),
    payload: payload,
    scopeId: scopeId,
    typed: typed,
    url: urlParam
  )
}

func divData(
  logId: String = DivBlockModelingContext.testCardId.rawValue,
  states: [DivData.State]
) -> DivData {
  DivData(
    functions: nil,
    logId: logId,
    states: states,
    timers: nil,
    transitionAnimationSelector: nil,
    variableTriggers: nil,
    variables: nil
  )
}

func divData(
  _ div: Div,
  logId: String = UIElementPath.root.description,
  stateId: Int = 0
) -> DivData {
  divData(
    logId: logId,
    states: [.init(div: div, stateId: stateId)]
  )
}

func divGifImage(
  accessibility: DivAccessibility? = nil,
  gifUrl: String,
  height: DivSize? = nil,
  id: String? = nil,
  width: DivSize? = nil
) -> Div {
  .divGifImage(DivGifImage(
    accessibility: accessibility,
    gifUrl: .value(url(gifUrl)),
    height: height,
    id: id,
    width: width
  ))
}

func divInput(
  accessibility: DivAccessibility? = nil,
  keyboardType: DivInput.KeyboardType? = nil,
  textVariable: String
) -> Div {
  .divInput(DivInput(
    accessibility: accessibility,
    keyboardType: keyboardType.map { .value($0) },
    textVariable: textVariable
  ))
}

func divImage(
  accessibility: DivAccessibility? = nil,
  height: DivSize? = nil,
  id: String? = nil,
  imageUrl: String = "",
  imageUrlExpression: String? = nil,
  preloadRequired: Bool? = nil,
  preloadRequiredExpression: String? = nil,
  width: DivSize? = nil
) -> Div {
  let imageUrlValue: Expression<URL> = if let imageUrlExpression {
    expression(imageUrlExpression)
  } else {
    .value(url(imageUrl))
  }
  
  let preloadRequiredValue: Expression<Bool>? = if let preloadRequiredExpression {
    expression(preloadRequiredExpression)
  } else {
    preloadRequired.map { .value($0) }
  }

  return .divImage(DivImage(
    accessibility: accessibility,
    height: height,
    id: id,
    imageUrl: imageUrlValue,
    preloadRequired: preloadRequiredValue,
    width: width
  ))
}

func divText(
  accessibility: DivAccessibility? = nil,
  actions: [DivAction]? = nil,
  disappearActions: [DivDisappearAction]? = nil,
  focus: DivFocus? = nil,
  fontSize: Int? = nil,
  fontWeight: DivFontWeight? = nil,
  id: String? = nil,
  margins: DivEdgeInsets? = nil,
  paddings: DivEdgeInsets? = nil,
  text: String = "",
  textExpression: String? = nil,
  tooltips: [DivTooltip]? = nil,
  width: DivSize? = nil,
  height: DivSize? = nil,
  variables: [DivVariable]? = nil,
  visibility: Expression<DivVisibility>? = nil,
  visibilityActions: [DivVisibilityAction]? = nil
) -> Div {
  let textValue: Expression<String> = if let textExpression {
    expression(textExpression)
  } else {
    .value(text)
  }
  return .divText(DivText(
    accessibility: accessibility,
    actions: actions,
    disappearActions: disappearActions,
    focus: focus,
    fontSize: fontSize.map { .value($0) },
    fontWeight: fontWeight.map { .value($0) },
    height: height,
    id: id,
    margins: margins,
    paddings: paddings,
    text: textValue,
    tooltips: tooltips,
    variables: variables,
    visibility: visibility,
    visibilityActions: visibilityActions,
    width: width
  ))
}

func divSeparator(
  accessibility: DivAccessibility? = nil,
  actions: [DivAction]? = nil,
  alpha: CGFloat? = nil,
  background: DivBackground? = nil,
  border: DivBorder? = nil,
  delimiterStyle: DivSeparator.DelimiterStyle? = nil,
  id: String? = nil,
  margins: DivEdgeInsets? = nil,
  paddings: DivEdgeInsets? = nil,
  tooltips: [DivTooltip]? = nil,
  transitionIn: DivAppearanceTransition? = nil,
  transitionTriggers: [DivTransitionTrigger]? = nil,
  visibility: Expression<DivVisibility>? = nil,
  width: DivSize? = nil
) -> Div {
  .divSeparator(DivSeparator(
    accessibility: accessibility,
    actions: actions,
    alpha: alpha.map { .value($0) },
    background: background.map { [$0] },
    border: border,
    delimiterStyle: delimiterStyle,
    id: id,
    margins: margins,
    paddings: paddings,
    tooltips: tooltips,
    transitionIn: transitionIn,
    transitionTriggers: transitionTriggers,
    visibility: visibility,
    width: width
  ))
}

func divContainer(
  id: String? = nil,
  accessibility: DivAccessibility? = nil,
  actions: [DivAction]? = nil,
  clipToBounds: Bool = true,
  extensions: [DivExtension]? = nil,
  height: DivSize? = nil,
  itemBuilder: DivCollectionItemBuilder? = nil,
  items: [Div]? = nil,
  width: DivSize? = nil,
  background: [DivBackground]? = nil
) -> Div {
  .divContainer(DivContainer(
    accessibility: accessibility,
    action: nil,
    actionAnimation: nil,
    actions: actions,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    animators: nil,
    aspect: nil,
    background: background,
    border: nil,
    captureFocusOnAction: nil,
    clipToBounds: .value(clipToBounds),
    columnSpan: nil,
    contentAlignmentHorizontal: nil,
    contentAlignmentVertical: nil,
    disappearActions: nil,
    doubletapActions: nil,
    extensions: extensions,
    focus: nil,
    functions: nil,
    height: height,
    hoverEndActions: nil,
    hoverStartActions: nil,
    id: id,
    itemBuilder: itemBuilder,
    items: items,
    layoutMode: nil,
    layoutProvider: nil,
    lineSeparator: nil,
    longtapActions: nil,
    margins: nil,
    orientation: nil,
    paddings: nil,
    pressEndActions: nil,
    pressStartActions: nil,
    reuseId: nil,
    rowSpan: nil,
    selectedActions: nil,
    separator: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    variableTriggers: nil,
    variables: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: width
  ))
}

func divGallery(
  items: [Div],
  id: String? = nil
) -> Div {
  .divGallery(DivGallery(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    animators: nil,
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
    functions: nil,
    height: nil,
    id: id,
    itemBuilder: nil,
    itemSpacing: nil,
    items: items,
    layoutProvider: nil,
    margins: nil,
    orientation: nil,
    paddings: nil,
    restrictParentScroll: nil,
    reuseId: nil,
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
    variableTriggers: nil,
    variables: nil,
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
    animators: nil,
    background: nil,
    border: nil,
    captureFocusOnAction: nil,
    columnCount: .value(columnCount),
    columnSpan: nil,
    contentAlignmentHorizontal: nil,
    contentAlignmentVertical: nil,
    disappearActions: nil,
    doubletapActions: nil,
    extensions: nil,
    focus: nil,
    functions: nil,
    height: nil,
    hoverEndActions: nil,
    hoverStartActions: nil,
    id: nil,
    items: items,
    layoutProvider: nil,
    longtapActions: nil,
    margins: nil,
    paddings: nil,
    pressEndActions: nil,
    pressStartActions: nil,
    reuseId: nil,
    rowSpan: nil,
    selectedActions: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    variableTriggers: nil,
    variables: nil,
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
    animators: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    crossAxisAlignment: nil,
    defaultItem: nil,
    disappearActions: nil,
    extensions: nil,
    focus: nil,
    functions: nil,
    height: nil,
    id: nil,
    infiniteScroll: nil,
    itemBuilder: nil,
    itemSpacing: nil,
    items: items,
    layoutMode: layoutMode,
    layoutProvider: nil,
    margins: nil,
    orientation: nil,
    paddings: nil,
    pageTransformation: nil,
    restrictParentScroll: nil,
    reuseId: nil,
    rowSpan: nil,
    scrollAxisAlignment: nil,
    selectedActions: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    variableTriggers: nil,
    variables: nil,
    visibility: nil,
    visibilityAction: nil,
    visibilityActions: nil,
    width: nil
  ))
}

func divState(
  divId: String?,
  id: String? = nil,
  defaultStateId: Expression<String>? = nil,
  states: [DivState.State]
) -> Div {
  .divState(DivState(
    accessibility: nil,
    alignmentHorizontal: nil,
    alignmentVertical: nil,
    alpha: nil,
    animators: nil,
    background: nil,
    border: nil,
    clipToBounds: nil,
    columnSpan: nil,
    defaultStateId: defaultStateId,
    disappearActions: nil,
    divId: divId,
    extensions: nil,
    focus: nil,
    functions: nil,
    height: nil,
    id: id,
    layoutProvider: nil,
    margins: nil,
    paddings: nil,
    reuseId: nil,
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
    variableTriggers: nil,
    variables: nil,
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
    animators: nil,
    background: nil,
    border: nil,
    columnSpan: nil,
    disappearActions: nil,
    dynamicHeight: nil,
    extensions: nil,
    focus: nil,
    functions: nil,
    hasSeparator: nil,
    height: nil,
    id: nil,
    items: items,
    layoutProvider: nil,
    margins: nil,
    paddings: nil,
    restrictParentScroll: nil,
    reuseId: nil,
    rowSpan: nil,
    selectedActions: nil,
    selectedTab: nil,
    separatorColor: nil,
    separatorPaddings: nil,
    switchTabsByContentSwipeEnabled: nil,
    tabTitleDelimiter: nil,
    tabTitleStyle: nil,
    titlePaddings: nil,
    tooltips: nil,
    transform: nil,
    transitionChange: nil,
    transitionIn: nil,
    transitionOut: nil,
    transitionTriggers: nil,
    variableTriggers: nil,
    variables: nil,
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

func matchParentSize() -> DivSize {
  .divMatchParentSize(DivMatchParentSize())
}

func wrapContentSize() -> DivSize {
  .divWrapContentSize(DivWrapContentSize())
}

func point(x: Double, y: Double) -> DivPoint {
  DivPoint(
    x: DivDimension(value: .value(x)),
    y: DivDimension(value: .value(y))
  )
}

func variable(_ name: String, _ value: String) -> DivVariable {
  .stringVariable(StringVariable(name: name, value: .value(value)))
}


func solidBackground(_ color: RGBAColor) -> DivBackground {
  .divSolidBackground(DivSolidBackground(color: .value(color)))
}

func divVideo(
  id: String? = nil,
  videoSources: [DivVideoSource],
  preloadRequired: Bool? = nil,
  preloadRequiredExpression: String? = nil
) -> Div {
  let preloadRequiredValue: Expression<Bool>? = if let preloadRequiredExpression {
    expression(preloadRequiredExpression)
  } else {
    preloadRequired.map { .value($0) }
  }
  
  return .divVideo(DivVideo(
    id: id,
    preloadRequired: preloadRequiredValue,
    videoSources: videoSources
  ))
}

func divVideoSource(
  mimeType: String,
  url: String? = nil,
  urlExpression: String? = nil
) -> DivVideoSource {
  let urlValue: Expression<URL> = if let urlExpression {
    expression(urlExpression)
  } else {
    .value(URL(string: url!)!)
  }

  return DivVideoSource(
    mimeType: .value(mimeType),
    url: urlValue
  )
}

func divTextImage(
  start: Int = 0,
  url: String,
  preloadRequired: Bool? = nil
) -> DivText.Image {
  DivText.Image(
    height: nil,
    preloadRequired: preloadRequired.map { .value($0) },
    start: .value(start),
    url: .value(URL(string: url)!)
  )
}

func divImageBackground(
  imageUrl: String,
  preloadRequired: Bool? = nil
) -> DivBackground {
  .divImageBackground(DivImageBackground(
    imageUrl: .value(URL(string: imageUrl)!),
    preloadRequired: preloadRequired.map { .value($0) }
  ))
}
