import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/configuration.dart';

import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';

class DivErrorWidget extends StatelessWidget {
  static const errorColor = Color(0xFFFF0000);

  final String? error;
  final DivBase data;

  const DivErrorWidget({
    required this.data,
    this.error,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final showUnsupportedDivs =
        watch<DivConfiguration>(context)?.showUnsupportedDivs ?? false;

    return showUnsupportedDivs
        ? DivBaseWidget(
            data: data,
            child: Placeholder(
              color: DivErrorWidget.errorColor,
              child: Center(
                child: Material(
                  type: MaterialType.transparency,
                  child: Text(
                    error ?? '',
                    style: const TextStyle(color: DivErrorWidget.errorColor),
                  ),
                ),
              ),
            ),
          )
        : const SizedBox.shrink();
  }
}
