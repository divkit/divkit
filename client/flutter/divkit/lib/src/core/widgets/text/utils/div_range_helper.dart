import 'package:collection/collection.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/core/widgets/text/utils/div_text_range_model.dart';
import 'package:divkit/src/utils/converters.dart';
import 'package:flutter/material.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';
import 'package:divkit/src/core/action/action_converter.dart';
import 'package:divkit/src/core/action/models/action.dart';

class DivRangeHelper {
  static Future<List<DivTextRangeModel>> getRangeItems(
    String text,
    List<DivTextRange> divTextRange,
    DivVariableContext context,
    TextStyle style,
    List<DivLineStyle> linesStyleList,
  ) async {
    final inputMap = <DivTextRangeInterval, DivTextRangeOptionModel>{};
    for (var range in divTextRange) {
      final mapEntry = await _getRangeInputMapEntry(
        range,
        style,
        context,
        linesStyleList,
      );
      inputMap.addEntries([mapEntry]);
    }

    inputMap.forEach(
      (key, value) {
        if (value.isDefault) {
          inputMap.remove(key);
        }
      },
    );
    if (inputMap.isEmpty) {
      return [];
    }

    return _getListTextRange(
      text,
      inputMap,
    );
  }

  static Future<MapEntry<DivTextRangeInterval, DivTextRangeOptionModel>>
      _getRangeInputMapEntry(
    DivTextRange divTextRange,
    TextStyle style,
    DivVariableContext context,
    List<DivLineStyle> linesStyleList,
  ) async {
    final start = await divTextRange.start.resolveValue(
      context: context,
    );
    final end = await divTextRange.end.resolveValue(
      context: context,
    );

    final topOffset = await divTextRange.topOffset?.resolveValue(
      context: context,
    );

    final strike = await divTextRange.strike?.resolveValue(
          context: context,
        ) ??
        linesStyleList[1];

    final underline = await divTextRange.underline?.resolveValue(
          context: context,
        ) ??
        linesStyleList[0];

    final textColor = await divTextRange.textColor?.resolveValue(
      context: context,
    );

    Color? background;
    await divTextRange.background?.map(
      divSolidBackground: (divSolidBackground) async {
        background = await divSolidBackground.color.resolveValue(
          context: context,
        );
      },
    );

    final fontSize = await divTextRange.fontSize?.resolveValue(
      context: context,
    );

    final fontWeightValue = await divTextRange.fontWeightValue?.resolveValue(
      context: context,
    );

    FontWeight? fontWeight = FontWeight.values.firstWhereOrNull(
      (element) => element.value == fontWeightValue,
    );
    fontWeight ??= await divTextRange.fontWeight?.resolve(
      context: context,
    );

    final letterSpacing = await divTextRange.letterSpacing?.resolveValue(
      context: context,
    );

    List<DivActionModel> actions = [];
    List<DivAction>? actionsDto = [...?divTextRange.actions];
    for (final action in actionsDto) {
      final resAction = await action.resolve(
        context: context,
      );
      if (resAction.enabled) {
        actions.add(resAction);
      }
    }
    style = TextStyle(
      color: textColor,
      fontSize: fontSize?.toDouble(),
      fontWeight: fontWeight,
      backgroundColor: background,
      letterSpacing: letterSpacing,
      decoration: TextDecoration.combine(
        [
          strike.asLineThrough,
          underline.asUnderline,
        ],
      ),
    );

    return MapEntry(
      DivTextRangeInterval(
        start,
        end - 1,
      ),
      DivTextRangeOptionModel(
        style: style,
        actions: actions,
        topOffset: topOffset,
      ),
    );
  }

  /// All intervals are represented as a list of pairs (interval, text style)
  /// Then the list is sorted by intervals and we go through it in a loop
  /// When we find a new starting interval - add the corresponding style to the set of "active" styles
  /// When the interval ends, we save the resulting interval with a style that is a combination of "active" styles
  /// And remove from this set all styles that have ceased to be active (their interval has ended)
  /// We get a list of pairs (spacing, restrained text style)
  /// Then translate it into a list of pairs (part of the text, the style of this text)
  static List<DivTextRangeModel> _getListTextRange(
    String text,
    Map<DivTextRangeInterval, DivTextRangeOptionModel> inputMap,
  ) {
    Map<DivTextRangeInterval, DivTextRangeOptionModel> tempMap = {};
    Map<DivTextRangeInterval, DivTextRangeOptionModel> finalMap = {};
    List<DivTextRangeModel> finalDataTextList = [];

    List<MapEntry<DivTextRangeInterval, DivTextRangeOptionModel>> entries =
        inputMap.entries.toList();
    entries.sort(_rangeDivTextRangeInterval);
    entries.removeWhere(
      (element) => element.key.start >= text.length,
    );

    if (entries.isEmpty) {
      entries.add(
        MapEntry(
          DivTextRangeInterval(
            0,
            text.length - 1,
          ),
          const DivTextRangeOptionModel(),
        ),
      );
    }

    for (var item in entries) {
      if (item.key.end >= text.length) {
        entries[entries.indexOf(item)] = MapEntry(
          DivTextRangeInterval(
            item.key.start,
            text.length - 1,
          ),
          item.value,
        );
      }
    }

    List<MapEntry<int, bool>> boundaries = [];
    for (int i = 0; i < entries.length; i++) {
      boundaries.add(MapEntry<int, bool>(entries[i].key.start, true));
      boundaries.add(MapEntry<int, bool>(entries[i].key.end, false));
    }

    boundaries.sort(_rangePairBoundarySort);

    int leftIndex = 0;
    for (int j = 0; j < entries.length; j++) {
      if (entries[j].key.start == 0) {
        tempMap[entries[j].key] = entries[j].value;
      }
    }

    for (int i = 0; i < boundaries.length; i++) {
      if (boundaries[i].value) {
        if (boundaries[i].key != 0) {
          finalMap[DivTextRangeInterval(
            leftIndex,
            boundaries[i].key - 1,
          )] = DivTextRangeOptionModel.mergeAll(
            tempMap.values.toList(),
          );
          leftIndex = boundaries[i].key;
          for (int j = 0; j < entries.length; j++) {
            if (entries[j].key.start == boundaries[i].key) {
              tempMap[entries[j].key] = entries[j].value;
            }
          }
        }
      } else {
        if (boundaries[i].key != 0) {
          finalMap[DivTextRangeInterval(
            leftIndex,
            boundaries[i].key,
          )] = DivTextRangeOptionModel.mergeAll(
            tempMap.values.toList(),
          );
          leftIndex = boundaries[i].key + 1;
          tempMap.removeWhere(
            (key, value) => key.end <= boundaries[i].key,
          );
        }
      }
    }

    List<MapEntry<DivTextRangeInterval, DivTextRangeOptionModel>>
        finalDataList = finalMap.entries.toList();

    finalDataList.sort(_rangeDivTextRangeInterval);

    for (int i = 0; i < finalDataList.length; i++) {
      late int startIndex;
      late int endIndex;
      startIndex = finalDataList[i].key.start;
      endIndex = finalDataList[i].key.end;

      String trimmedString = text.substring(
        startIndex,
        endIndex + 1,
      );
      if (startIndex <= endIndex) {
        finalDataTextList.add(
          DivTextRangeModel(
            text: trimmedString,
            optionModel: finalDataList[i].value,
          ),
        );
      }
    }

    if (finalDataList.last.key.end + 1 < text.length) {
      String trimmedString = text.substring(
        finalDataList.last.key.end + 1,
      );

      finalDataTextList.add(
        DivTextRangeModel(
          text: trimmedString,
          optionModel: const DivTextRangeOptionModel(),
        ),
      );
    }

    return finalDataTextList;
  }

  static int _rangePairBoundarySort(
    MapEntry<int, bool> first,
    MapEntry<int, bool> second,
  ) {
    if (first.key < second.key) {
      return -1;
    } else if (first.key > second.key) {
      return 1;
    } else if (first.value == second.value) {
      return 0;
    } else if (first.value) {
      return -1;
    } else {
      return 1;
    }
  }

  static int _rangeDivTextRangeInterval(
    MapEntry<DivTextRangeInterval, DivTextRangeOptionModel> first,
    MapEntry<DivTextRangeInterval, DivTextRangeOptionModel> second,
  ) {
    if (first.key.start < second.key.start) {
      return -1;
    } else if (first.key.start > second.key.start) {
      return 1;
    } else if (first.key.end == second.key.end) {
      return 0;
    } else if (first.key.end < second.key.end) {
      return -1;
    } else {
      return 1;
    }
  }
}
