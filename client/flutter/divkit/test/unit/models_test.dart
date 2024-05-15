import 'dart:convert';
import 'dart:io';
import 'dart:ui';

import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';
import 'package:flutter_test/flutter_test.dart';

const reference = Div(
  DivContainer(
    orientation: ValueExpression(DivContainerOrientation.vertical),
    height: DivSize(DivWrapContentSize()),
    width: DivSize(DivWrapContentSize()),
    background: [
      DivBackground(
        DivSolidBackground(color: ValueExpression(Color(0xFFFFFFFF))),
      ),
    ],
    layoutMode: ValueExpression(DivContainerLayoutMode.noWrap),
    items: [
      Div(
        DivText(
          text: ValueExpression("Linear container with baseline alignment:"),
          height: DivSize(DivWrapContentSize()),
          width: DivSize(DivWrapContentSize()),
          paddings: DivEdgeInsets(
            left: ValueExpression(10),
            top: ValueExpression(10),
            bottom: ValueExpression(10),
          ),
        ),
      ),
      Div(
        DivContainer(
          border: DivBorder(
            stroke: DivStroke(
              color: ValueExpression(Color(0xFFFF0000)),
            ),
          ),
          orientation: ValueExpression(DivContainerOrientation.horizontal),
          height: DivSize(DivWrapContentSize()),
          width: DivSize(DivWrapContentSize()),
          contentAlignmentHorizontal:
              ValueExpression(DivContentAlignmentHorizontal.center),
          contentAlignmentVertical:
              ValueExpression(DivContentAlignmentVertical.center),
          layoutMode: ValueExpression(DivContainerLayoutMode.noWrap),
          items: [
            Div(
              DivText(
                text: ValueExpression("External"),
                height: DivSize(DivWrapContentSize()),
                width: DivSize(DivWrapContentSize()),
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
            Div(
              DivText(
                text: ValueExpression("container."),
                height: DivSize(DivWrapContentSize()),
                width: DivSize(DivWrapContentSize()),
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
            Div(
              DivContainer(
                border: DivBorder(
                  stroke: DivStroke(
                    color: ValueExpression(Color(0xFF0000EE)),
                  ),
                ),
                orientation:
                    ValueExpression(DivContainerOrientation.horizontal),
                height: DivSize(DivWrapContentSize()),
                width: DivSize(DivWrapContentSize()),
                alignmentVertical:
                    ValueExpression(DivAlignmentVertical.baseline),
                contentAlignmentHorizontal:
                    ValueExpression(DivContentAlignmentHorizontal.center),
                contentAlignmentVertical:
                    ValueExpression(DivContentAlignmentVertical.baseline),
                layoutMode: ValueExpression(DivContainerLayoutMode.noWrap),
                items: [
                  Div(
                    DivText(
                      text: ValueExpression("Inner"),
                      height: DivSize(DivWrapContentSize()),
                      width: DivSize(DivWrapContentSize()),
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
                  Div(
                    DivText(
                      text: ValueExpression("container."),
                      height: DivSize(DivWrapContentSize()),
                      width: DivSize(DivWrapContentSize()),
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
            Div(
              DivText(
                text: ValueExpression("Long long long text in multiline."),
                height: DivSize(DivWrapContentSize()),
                width: DivSize(DivFixedSize(value: ValueExpression(50))),
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
            Div(
              DivText(
                text: ValueExpression("Bottom alignment."),
                height: DivSize(DivWrapContentSize()),
                width: DivSize(DivWrapContentSize()),
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
      final file = File('test_data/json/div_container_with_inner_texts.json');
      final input = await file.readAsString();
      final json = jsonDecode(input);
      final divContainer = Div.fromJson(json['div']);
      expect(divContainer, reference);
    });
  });
}
