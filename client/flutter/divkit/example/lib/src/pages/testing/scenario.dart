import 'package:divkit/divkit.dart';
import 'package:example/src/pages/testing/data.dart';
import 'package:flutter/material.dart';

class ScenarioPage extends StatefulWidget {
  final List<Item> scenarios;
  final int index;

  const ScenarioPage({
    super.key,
    required this.scenarios,
    required this.index,
  });

  @override
  State<StatefulWidget> createState() => _ScenarioPageState();
}

class _ScenarioPageState extends State<ScenarioPage> {
  late int index = widget.index;

  @override
  Widget build(BuildContext context) {
    try {
      return Scaffold(
        appBar: AppBar(
          title: Text(widget.scenarios[index].title),
          centerTitle: true,
        ),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Expanded(
              child: SingleChildScrollView(
                child: DivKitView(
                  showUnsupportedDivs: true,
                  data: widget.scenarios[index].src,
                ),
              ),
            ),
            const Divider(),
            Padding(
              padding: const EdgeInsets.symmetric(
                vertical: 8.0,
                horizontal: 16,
              ),
              child: Row(
                children: [
                  if (index - 1 >= 0)
                    ElevatedButton(
                      onPressed: () {
                        setState(() {
                          index = index - 1;
                        });
                      },
                      child: const Text('Back'),
                    ),
                  const Spacer(),
                  if (index + 1 < widget.scenarios.length)
                    ElevatedButton(
                      onPressed: () {
                        setState(() {
                          index = index + 1;
                        });
                      },
                      child: const Text('Next'),
                    ),
                ],
              ),
            ),
          ],
        ),
      );
    } catch (e, st) {
      return Scaffold(
        appBar: AppBar(
          title: Text(widget.scenarios[index].title),
          centerTitle: true,
        ),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Expanded(child: Center(child: Text("$e\n$st"))),
            const Divider(),
            Padding(
              padding: const EdgeInsets.symmetric(
                vertical: 8.0,
                horizontal: 16,
              ),
              child: Row(
                children: [
                  if (index - 1 >= 0)
                    ElevatedButton(
                      onPressed: () {
                        setState(() {
                          index = index - 1;
                        });
                      },
                      child: const Text('Back'),
                    ),
                  const Spacer(),
                  if (index + 1 < widget.scenarios.length)
                    ElevatedButton(
                      onPressed: () {
                        setState(() {
                          index = index + 1;
                        });
                      },
                      child: const Text('Next'),
                    ),
                ],
              ),
            ),
          ],
        ),
      );
    }
  }

  @override
  void dispose() {
    super.dispose();
  }
}
