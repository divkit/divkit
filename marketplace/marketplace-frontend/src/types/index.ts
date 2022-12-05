import { DivJson } from '@divkitframework/divkit/typings/common';

export type TagType = {
  id: string;
  tag: string;
  max_use?: number;
};

export interface ITemplate {
  id: string;
  title: string;
  template: DivJson;
  dt_updated: string;
  description: string;
  tags?: TagType[];
  preview_url?: string;
  preview_filename?: string;
}
