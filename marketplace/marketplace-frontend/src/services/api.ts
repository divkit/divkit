import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import { ITemplate, TagType } from '../types';
import { DivJson } from '@divkitframework/divkit/typings/common';

const URL = {
  prod: 'https://brave-morse.usr.yandex-academy.ru/api/v1',
  local: 'http://localhost:8080/api/v1',
};

interface ISearchRequest {
  title: string;
  tags_ids: string;
  page: number;
  category: string;
}
export interface IUpdateTemplate {
  id?: string;
  title: string;
  description: string;
  template: DivJson;
  tags_ids: TagType[];
  file?: Blob;
  preview_filename?: string;
}
export interface ICreateTemplate {
  title: string;
  description: string;
  template: DivJson;
  tags_ids: TagType[];
  file?: Blob;
  preview_filename?: string;
}

export const templatesApi = createApi({
  reducerPath: 'templatesApi',
  baseQuery: fetchBaseQuery({
    baseUrl: URL.prod,
  }),

  endpoints: (builder) => ({
    // ------- TAGS
    getSimilarTags: builder.query<
      { tags: TagType[] },
      { text: string; page: number; size: number }
    >({
      query: ({ text, page, size }) => `tag/${text}?page=${page}`,
    }),

    createTag: builder.mutation<{ id: string }, string>({
      query: (tag) => ({
        url: 'tag',
        method: 'post',
        body: { tag },
      }),
    }),

    // -------- TEMPLATES
    getTemplatesByNameAndTags: builder.query<
      { templates: ITemplate[]; total_pages: number },
      { searchParams: string; page: number }
    >({
      query: ({ searchParams = '', page = 1 }) => `template?${searchParams}&page=${page}`,
    }),

    updateTemplate: builder.mutation<{ id: string }, IUpdateTemplate>({
      query: ({ id, description, template, title, tags_ids, file, preview_filename }) => {
        const requestData = new FormData();
        const body = {
          id,
          title,
          description,
          template,
          tag_ids: tags_ids.map((tag) => tag.id),
          preview_filename,
        };

        if (file) {
          requestData.append('file', file);
        }
        requestData.append('template', JSON.stringify(body));

        return {
          method: 'put',
          url: `template`,
          body: requestData,
        };
      },
    }),

    createTemplate: builder.mutation<{ id: string }, ICreateTemplate>({
      query: ({ template, description, title, tags_ids, file, preview_filename }) => {
        const requestData = new FormData();
        const body = {
          title,
          description,
          template,
          tag_ids: tags_ids.map((tag) => tag.id),
          preview_filename,
        };

        if (file) {
          requestData.append('file', file);
        }
        requestData.append('template', JSON.stringify(body));

        return {
          method: 'post',
          url: '/template',
          body: requestData,
        };
      },
    }),

    getTemplateById: builder.query<ITemplate, string>({
      query: (id) => `template/${id}`,
    }),

    deleteTemplate: builder.mutation<{ status: string }, string>({
      query: (id) => ({
        method: 'delete',
        url: `template/${id}`,
      }),
    }),
  }),
});

export const {
  useUpdateTemplateMutation,
  useGetTemplateByIdQuery,
  useCreateTagMutation,
  useCreateTemplateMutation,
  useDeleteTemplateMutation,
  useLazyGetSimilarTagsQuery,
  useLazyGetTemplatesByNameAndTagsQuery,
} = templatesApi;
