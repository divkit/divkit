import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/configuration.dart';

import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';

class DivErrorWidget extends StatefulWidget {
  static const errorColor = Color(0xFFFF0000);

  final String? error;
  final DivBase data;

  const DivErrorWidget({
    required this.data,
    this.error,
    super.key,
  });

  @override
  State<DivErrorWidget> createState() => _DivErrorWidgetState();
}

class _DivErrorWidgetState extends State<DivErrorWidget> {
  @override
  void initState() {
    super.initState();
    final divContext = read<DivContext>(context)!;
    widget.data.resolve(divContext.variables);
  }

  @override
  Widget build(BuildContext context) =>
      watch<DivConfiguration>(context)?.showUnsupportedDivs ?? false
          ? DivBaseWidget(
              data: widget.data,
              child: Placeholder(
                color: DivErrorWidget.errorColor,
                child: Center(
                  child: Material(
                    type: MaterialType.transparency,
                    child: Text(
                      widget.error ?? '',
                      style: const TextStyle(color: DivErrorWidget.errorColor),
                    ),
                  ),
                ),
              ),
            )
          : const SizedBox.shrink();
}
