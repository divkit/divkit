import { rest } from "msw";
import { MockData } from "./mock";

const api = new MockData()
export const handlers = [
  rest.get('https://brave-morse.usr.yandex-academy.ru/api/v1/template', (req, res, ctx) => {
    const title = req.url.searchParams.get('title')

    if (title === '222222') {
      return res(
        ctx.status(200),
        ctx.json({
          templates: [],
          total_pages: 0
        })
      )
    }
    return res(
      ctx.status(200),
      ctx.json({
        templates: api.templates,
        total_pages: 5
      })
    )
  }),
  rest.get('https://brave-morse.usr.yandex-academy.ru/api/v1/template/111', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json(
        api.templates[0]
      )
    )
  }),
  rest.get('https://brave-morse.usr.yandex-academy.ru/api/v1/tag/contain', (req, res, ctx) => {
    return res(
      ctx.status(200),
      ctx.json(
        { tags: [{ id: "cd8264e7-42df-4f6c-a27b-309c101235ed", tag: "container" }] }
      )
    )
  })
]