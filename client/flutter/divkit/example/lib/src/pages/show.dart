import 'package:divkit/divkit.dart';
import 'package:example/src/configuration/playground_custom_handler.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../state.dart';

class ShowPage extends ConsumerStatefulWidget {
  final Map<String, dynamic> data;

  const ShowPage(this.data, {super.key});

  @override
  ConsumerState<ShowPage> createState() => _ShowPageState();
}

class _ShowPageState extends ConsumerState<ShowPage> {
  late final DivKitData data;

  @override
  void initState() {
    super.initState();

    data = DefaultDivKitData.fromJson(widget.data);
  }

  @override
  Widget build(BuildContext context) {
    final isNightModeEnabled = ref.watch(nightModeProvider);
    final isRtlEnabled = ref.watch(isRTLProvider);
    final reloadN = ref.watch(reloadNProvider);
    final mq = MediaQuery.of(context);

    return Scaffold(
      appBar: AppBar(
        title: FittedBox(
          child:
              Text("${mq.size.width.round()}x${mq.size.height.round() - 64}"),
        ),
        actions: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: FittedBox(
              child: Row(
                children: [
                  ElevatedButton(
                    child: const Text('reload'),
                    onPressed: () {
                      ref.read(reloadNProvider.notifier).state = reloadN + 1;
                    },
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    child: Text(isNightModeEnabled ? 'dark' : 'light'),
                    onPressed: () {
                      ref.read(nightModeProvider.notifier).state =
                          !isNightModeEnabled;
                    },
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    child: Text(isRtlEnabled ? 'RTL' : 'LTR'),
                    onPressed: () {
                      ref.read(isRTLProvider.notifier).state = !isRtlEnabled;
                    },
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
      body: DivKitView(
        customHandler: PlaygroundAppCustomHandler(),
        key: ObjectKey(reloadN),
        data: data,
      ),
    );
  }
}
