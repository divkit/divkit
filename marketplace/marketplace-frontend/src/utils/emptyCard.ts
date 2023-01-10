import { DivJson } from '@divkitframework/divkit/typings/common';

export const EMPTY_CARD: DivJson = {
  templates: {
    newTemplate: {
      type: 'container',
      items: [
        {
          type: 'text',
          $text: 'title',
          margins: { bottom: 16 },
          font_size: 21,
          font_weight: 'bold',
        },
        { type: 'text', $text: 'body', margins: { bottom: 16 }, font_size: 16 },
      ],
    },
  },
  card: {
    log_id: '',

    states: [
      {
        state_id: 0,
        div: {
          type: 'container',
          items: [
            {
              title: 'New DivKit template',
              body: 'You can download your new template here!',
              type: 'newTemplate',
            },
          ],
        },
      },
    ],
  },
};
