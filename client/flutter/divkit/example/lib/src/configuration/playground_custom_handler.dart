import 'dart:async';

import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';

class PlaygroundAppCustomHandler implements DivCustomHandler {
  late final factories = {
    'new_custom_card_1': _createCustomCard,
    'new_custom_card_2': _createCustomCard,
    'new_custom_container_1': _createCustomContainer,
  };

  @override
  Widget createCustom(DivCustom div) {
    final child = factories[div.customType]?.call(div) ??
        (throw Exception(
            'Unsupported DivCustom with custom_type: ${div.customType}'));

    return child;
  }

  @override
  bool isCustomTypeSupported(String type) => factories.containsKey(type);

  Widget _createCustomCard(DivCustom div) {
    const gradientColors = [
      -0xFF0000,
      -0xFF7F00,
      -0xF00F00,
      -0x00FF00,
      -0x0000FF,
      -0x2E2B5F,
      -0x8B00FF,
    ];

    return Material(
      child: Container(
        width: double.infinity,
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.bottomLeft,
            end: Alignment.topRight,
            colors: gradientColors.map((item) => Color(item)).toList(),
          ),
        ),
        alignment: Alignment.centerLeft,
        padding: const EdgeInsets.only(
          left: 12,
          top: 12,
          right: 12,
          bottom: 12,
        ),
        child: _ChronographWidget(),
      ),
    );
  }

  Widget _createCustomContainer(DivCustom div) {
    return Column(
      children: _createItems(div.items ?? []),
    );
  }

  List<Widget> _createItems(List<Div> items) {
    return items.map((item) => DivWidget(item)).toList();
  }
}

class _ChronographWidget extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _ChronographState();
}

class _ChronographState extends State<_ChronographWidget> {
  late final timer = Timer.periodic(
    const Duration(
      seconds: 1,
    ),
    (timer) {
      if (mounted) {
        setState(() {});
      } else {
        timer.cancel();
      }
    },
  );

  @override
  Widget build(BuildContext context) => Text(
        '${(timer.tick ~/ 60).toString().padLeft(2, '0')}:${(timer.tick % 60).toString().padLeft(2, '0')}',
        style: const TextStyle(
          fontSize: 20,
          color: Colors.black,
          backgroundColor: Colors.transparent,
        ),
      );
}
