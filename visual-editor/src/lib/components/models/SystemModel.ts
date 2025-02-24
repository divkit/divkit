export class SystemModel {
  id: number;
  systemName: string;
  title: string;

  constructor(id: number, systemName: string, title: string) {
    this.id = id;
    this.systemName = systemName;
    this.title = title;
  }
}
