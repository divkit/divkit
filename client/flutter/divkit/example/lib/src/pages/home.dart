import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../configuration/playground_custom_handler.dart';
import '../configuration/playground_root_action_handler.dart';
import '../state.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  Future<Map<String, dynamic>> loadJson(String name) async {
    final jsonData = await jsonDecode(
      await rootBundle.loadString(
        'assets/application/$name',
      ),
    );
    return jsonData;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: OrientationBuilder(builder: (context, orientation) {
          return FutureBuilder<Map<String, dynamic>>(
            key: ObjectKey(orientation),
            builder: (_, snapshot) {
              if (snapshot.hasError) {
                return Center(
                  child: Text(snapshot.error.toString()),
                );
              }
              if (snapshot.hasData) {
                return DivKitView(
                  data: DefaultDivKitData.fromJson(snapshot.requireData),
                  customHandler: PlaygroundAppCustomHandler(),
                  actionHandler: PlaygroundAppRootActionHandler(
                    navigator: navigatorKey,
                  ),
                );
              }
              return const Center(
                child: CircularProgressIndicator(),
              );
            },
            future: orientation == Orientation.portrait
                ? loadJson('menu.json')
                : loadJson('menu-land.json'),
          );
        }),
      ),
    );
  }
}
