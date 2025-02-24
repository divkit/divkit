export class ScreenModel {
  id: string | null;
  path: string;
  jsonObject: string;
  version: string;
  screenName: string;
  idSystem: number;
  appScreenPatches: [];

  constructor(
    id: string | null,
    path: string,
    jsonObject: string,
    version: string,
    screenName: string,
    idSystem: number,
    appScreenPatches: [],
  ) {
    this.id = id;
    this.path = path;
    // this.jsonObject = jsonObject.substring(1, jsonObject.length - 1);
    this.jsonObject = jsonObject;
    this.version = version;
    this.screenName = screenName;
    this.idSystem = idSystem;
    this.appScreenPatches = appScreenPatches;
  }
}
