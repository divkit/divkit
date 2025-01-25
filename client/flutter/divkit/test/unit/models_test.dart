import 'package:divkit/divkit.dart';
import 'package:flutter_test/flutter_test.dart';

const json = {
  "div": {
    "type": "container",
    "orientation": "vertical",
    "width": {"type": "wrap_content"},
    "height": {"type": "wrap_content"},
    "background": [
      {"type": "solid", "color": "#FFFFFF"},
    ],
    "layout_mode": "no_wrap",
    "items": [
      {
        "type": "text",
        "text": "Linear container with baseline alignment:",
        "width": {"type": "wrap_content"},
        "height": {"type": "wrap_content"},
        "paddings": {"left": 10, "top": 10, "bottom": 10},
      },
      {
        "type": "container",
        "border": {
          "stroke": {"color": "#FF0000"},
        },
        "orientation": "horizontal",
        "width": {"type": "wrap_content"},
        "height": {"type": "wrap_content"},
        "content_alignment_horizontal": "center",
        "content_alignment_vertical": "center",
        "layout_mode": "no_wrap",
        "items": [
          {
            "type": "text",
            "text": "External",
            "height": {"type": "wrap_content"},
            "width": {"type": "wrap_content"},
            "alignment_vertical": "baseline",
            "paddings": {"left": 5, "top": 10, "right": 5, "bottom": 5},
            "border": {
              "stroke": {"color": "#FF0000"},
            },
            "font_size": 16,
          },
          {
            "type": "text",
            "text": "container.",
            "height": {"type": "wrap_content"},
            "width": {"type": "wrap_content"},
            "alignment_vertical": "baseline",
            "paddings": {"left": 5, "top": 5, "right": 5, "bottom": 10},
            "border": {
              "stroke": {"color": "#FF0000"},
            },
            "font_size": 10,
          },
          {
            "type": "container",
            "border": {
              "stroke": {"color": "#0000EE"},
            },
            "orientation": "horizontal",
            "width": {"type": "wrap_content"},
            "height": {"type": "wrap_content"},
            "alignment_vertical": "baseline",
            "content_alignment_horizontal": "center",
            "content_alignment_vertical": "baseline",
            "layout_mode": "no_wrap",
            "items": [
              {
                "type": "text",
                "text": "Inner",
                "height": {"type": "wrap_content"},
                "width": {"type": "wrap_content"},
                "alignment_vertical": "baseline",
                "paddings": {"left": 5, "top": 10, "right": 5, "bottom": 5},
                "border": {
                  "stroke": {"color": "#FF0000"},
                },
                "font_size": 16,
              },
              {
                "type": "text",
                "text": "container.",
                "height": {"type": "wrap_content"},
                "width": {"type": "wrap_content"},
                "alignment_vertical": "baseline",
                "paddings": {"left": 5, "top": 5, "right": 5, "bottom": 10},
                "border": {
                  "stroke": {"color": "#FF0000"},
                },
                "font_size": 10,
              }
            ],
          },
          {
            "type": "text",
            "text": "Long long long text in multiline.",
            "height": {"type": "wrap_content"},
            "width": {"type": "fixed", "value": 50},
            "alignment_vertical": "baseline",
            "paddings": {"left": 5, "top": 5, "right": 5, "bottom": 5},
            "border": {
              "stroke": {"color": "#FF0000"},
            },
            "font_size": 8,
          },
          {
            "type": "text",
            "text": "Bottom alignment.",
            "height": {"type": "wrap_content"},
            "width": {"type": "wrap_content"},
            "alignment_vertical": "bottom",
            "paddings": {"left": 5, "top": 5, "right": 5, "bottom": 10},
            "border": {
              "stroke": {"color": "#FF0000"},
            },
            "font_size": 8,
          }
        ],
      }
    ],
  },
};

const reference = Div.divContainer(
  DivContainer(
    orientation: ValueExpression(DivContainerOrientation.vertical),
    height: DivSize.divWrapContentSize(DivWrapContentSize()),
    width: DivSize.divWrapContentSize(DivWrapContentSize()),
    background: [
      DivBackground.divSolidBackground(
        DivSolidBackground(color: ValueExpression(Color(0xFFFFFFFF))),
      ),
    ],
    layoutMode: ValueExpression(DivContainerLayoutMode.noWrap),
    items: [
      Div.divText(
        DivText(
          text: ValueExpression("Linear container with baseline alignment:"),
          height: DivSize.divWrapContentSize(DivWrapContentSize()),
          width: DivSize.divWrapContentSize(DivWrapContentSize()),
          paddings: DivEdgeInsets(
            left: ValueExpression(10),
            top: ValueExpression(10),
            bottom: ValueExpression(10),
          ),
        ),
      ),
      Div.divContainer(
        DivContainer(
          border: DivBorder(
            stroke: DivStroke(
              color: ValueExpression(Color(0xFFFF0000)),
            ),
          ),
          orientation: ValueExpression(DivContainerOrientation.horizontal),
          height: DivSize.divWrapContentSize(DivWrapContentSize()),
          width: DivSize.divWrapContentSize(DivWrapContentSize()),
          contentAlignmentHorizontal:
              ValueExpression(DivContentAlignmentHorizontal.center),
          contentAlignmentVertical:
              ValueExpression(DivContentAlignmentVertical.center),
          layoutMode: ValueExpression(DivContainerLayoutMode.noWrap),
          items: [
            Div.divText(
              DivText(
                text: ValueExpression("External"),
                height: DivSize.divWrapContentSize(DivWrapContentSize()),
                width: DivSize.divWrapContentSize(DivWrapContentSize()),
                alignmentVertical:
                    ValueExpression(DivAlignmentVertical.baseline),
                paddings: DivEdgeInsets(
                  left: ValueExpression(5),
                  top: ValueExpression(10),
                  right: ValueExpression(5),
                  bottom: ValueExpression(5),
                ),
                border: DivBorder(
                  stroke: DivStroke(
                    color: ValueExpression(Color(0xFFFF0000)),
                  ),
                ),
                fontSize: ValueExpression(16),
              ),
            ),
            Div.divText(
              DivText(
                text: ValueExpression("container."),
                height: DivSize.divWrapContentSize(DivWrapContentSize()),
                width: DivSize.divWrapContentSize(DivWrapContentSize()),
                alignmentVertical:
                    ValueExpression(DivAlignmentVertical.baseline),
                paddings: DivEdgeInsets(
                  left: ValueExpression(5),
                  top: ValueExpression(5),
                  right: ValueExpression(5),
                  bottom: ValueExpression(10),
                ),
                border: DivBorder(
                  stroke: DivStroke(
                    color: ValueExpression(Color(0xFFFF0000)),
                  ),
                ),
                fontSize: ValueExpression(10),
              ),
            ),
            Div.divContainer(
              DivContainer(
                border: DivBorder(
                  stroke: DivStroke(
                    color: ValueExpression(Color(0xFF0000EE)),
                  ),
                ),
                orientation:
                    ValueExpression(DivContainerOrientation.horizontal),
                height: DivSize.divWrapContentSize(DivWrapContentSize()),
                width: DivSize.divWrapContentSize(DivWrapContentSize()),
                alignmentVertical:
                    ValueExpression(DivAlignmentVertical.baseline),
                contentAlignmentHorizontal:
                    ValueExpression(DivContentAlignmentHorizontal.center),
                contentAlignmentVertical:
                    ValueExpression(DivContentAlignmentVertical.baseline),
                layoutMode: ValueExpression(DivContainerLayoutMode.noWrap),
                items: [
                  Div.divText(
                    DivText(
                      text: ValueExpression("Inner"),
                      height: DivSize.divWrapContentSize(DivWrapContentSize()),
                      width: DivSize.divWrapContentSize(DivWrapContentSize()),
                      alignmentVertical:
                          ValueExpression(DivAlignmentVertical.baseline),
                      paddings: DivEdgeInsets(
                        left: ValueExpression(5),
                        top: ValueExpression(10),
                        right: ValueExpression(5),
                        bottom: ValueExpression(5),
                      ),
                      border: DivBorder(
                        stroke: DivStroke(
                          color: ValueExpression(Color(0xFFFF0000)),
                        ),
                      ),
                      fontSize: ValueExpression(16),
                    ),
                  ),
                  Div.divText(
                    DivText(
                      text: ValueExpression("container."),
                      height: DivSize.divWrapContentSize(DivWrapContentSize()),
                      width: DivSize.divWrapContentSize(DivWrapContentSize()),
                      alignmentVertical:
                          ValueExpression(DivAlignmentVertical.baseline),
                      paddings: DivEdgeInsets(
                        left: ValueExpression(5),
                        top: ValueExpression(5),
                        right: ValueExpression(5),
                        bottom: ValueExpression(10),
                      ),
                      border: DivBorder(
                        stroke: DivStroke(
                          color: ValueExpression(Color(0xFFFF0000)),
                        ),
                      ),
                      fontSize: ValueExpression(10),
                    ),
                  ),
                ],
              ),
            ),
            Div.divText(
              DivText(
                text: ValueExpression("Long long long text in multiline."),
                height: DivSize.divWrapContentSize(DivWrapContentSize()),
                width: DivSize.divFixedSize(
                  DivFixedSize(value: ValueExpression(50)),
                ),
                alignmentVertical:
                    ValueExpression(DivAlignmentVertical.baseline),
                paddings: DivEdgeInsets(
                  left: ValueExpression(5),
                  top: ValueExpression(5),
                  right: ValueExpression(5),
                  bottom: ValueExpression(5),
                ),
                border: DivBorder(
                  stroke: DivStroke(
                    color: ValueExpression(Color(0xFFFF0000)),
                  ),
                ),
                fontSize: ValueExpression(8),
              ),
            ),
            Div.divText(
              DivText(
                text: ValueExpression("Bottom alignment."),
                height: DivSize.divWrapContentSize(DivWrapContentSize()),
                width: DivSize.divWrapContentSize(DivWrapContentSize()),
                alignmentVertical: ValueExpression(DivAlignmentVertical.bottom),
                paddings: DivEdgeInsets(
                  left: ValueExpression(5),
                  top: ValueExpression(5),
                  right: ValueExpression(5),
                  bottom: ValueExpression(10),
                ),
                border: DivBorder(
                  stroke: DivStroke(
                    color: ValueExpression(Color(0xFFFF0000)),
                  ),
                ),
                fontSize: ValueExpression(8),
              ),
            ),
          ],
        ),
      ),
    ],
  ),
);

void main() {
  group('Generated modals test', () {
    test('Build tree using div components', () async {
      final divContainer = Div.fromJson(json['div']);
      expect(divContainer, reference);
    });
  });
}
