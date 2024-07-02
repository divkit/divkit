import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../configuration/playground_action_handler.dart';
import '../state.dart';

class PlaygroundPage extends StatefulWidget {
  const PlaygroundPage({super.key});

  @override
  State<PlaygroundPage> createState() => _PlaygroundPage();
}

class _PlaygroundPage extends State<PlaygroundPage> {
  var showInfo = true;

  final variableStorage = DefaultDivVariableStorage();

  @override
  void initState() {
    super.initState();
    variableStorage.put(DivVariable(name: demoInputVariable, value: ''));
  }

  Future<Map<String, dynamic>> loadJson() async {
    final jsonData = await jsonDecode(
      await rootBundle.loadString(
        'assets/application/demo.json',
      ),
    );
    return jsonData;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color(0xffFFDCAE),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.of(context).pop();
          },
        ),
        title: const Text(
          'Playground',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.bold,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.clear),
            onPressed: () {
              variableStorage
                  .update(DivVariable(name: demoInputVariable, value: ''));
            },
          ),
        ],
      ),
      backgroundColor: const Color(0xffFFDCAE),
      body: Column(
        children: [
          if (showInfo)
            const Padding(
              padding: EdgeInsets.all(16),
              child: Text(
                'Enter the link, JSON or scan QR-code to see your result!',
                style: TextStyle(fontSize: 18),
              ),
            ),
          Expanded(
            child: Container(
              decoration: const BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(30),
                  topRight: Radius.circular(30),
                ),
              ),
              child: FutureBuilder<Map<String, dynamic>>(
                future: loadJson(),
                builder: (_, snapshot) {
                  if (snapshot.hasError) {
                    return Center(
                      child: Text(snapshot.error.toString()),
                    );
                  }
                  if (snapshot.hasData) {
                    final data = snapshot.data;
                    if (data != null) {
                      if (!data.containsKey('card')) {
                        return const Center(
                          child: Text('No card field was provided'),
                        );
                      }
                      return Listener(
                        onPointerDown: (_) {
                          if (showInfo) {
                            setState(() {
                              showInfo = false;
                            });
                          }
                        },
                        child: SafeArea(
                          child: DivKitView(
                            data: DefaultDivKitData.fromJson(data),
                            actionHandler: PlaygroundActionHandler(
                              navigator: navigatorKey,
                            ),
                            variableStorage: variableStorage,
                          ),
                        ),
                      );
                    }
                  }
                  return const Center(
                    child: CircularProgressIndicator(),
                  );
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}
