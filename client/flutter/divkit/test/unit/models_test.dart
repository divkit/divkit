import 'dart:convert';
import 'dart:io';
import 'dart:ui';

import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';
import 'package:flutter_test/flutter_test.dart';

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
                    DivFixedSize(value: ValueExpression(50))),
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
      final file = File('test_data/json/div_container_with_inner_texts.json');
      final input = await file.readAsString();
      final json = jsonDecode(input);
      final divContainer = Div.fromJson(json['div']);
      expect(divContainer, reference);
    });
  });
}
