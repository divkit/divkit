export class PatchModel {
  id: string;
  jsonObject: string;
  idScreen: string;
  patchName: string;

  constructor(
    id: string,
    jsonObject: string,
    idScreen: string,
    patchName: string
  ) {
    this.id = id;
    this.jsonObject = jsonObject;
    this.idScreen = idScreen;
    this.patchName = patchName;
  }
}
