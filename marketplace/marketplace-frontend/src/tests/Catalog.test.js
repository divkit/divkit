import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event';
import { MockData, MockTemplatesApi } from '../mocks/mock';
import { renderApplication } from '../mocks/helper';
import { server } from '../mocks/server';

describe('Catalog', () => {
  afterEach(() => {
    server.resetHandlers()
  })
  beforeAll(() => {
    server.listen()
  })
  afterAll(() => server.close())
  beforeEach(() => {
    jest.setTimeout(20000);

  })
  test('Limit of templates on page is valid', async () => {
    const view = renderApplication()
    render(view)
    const elems = await screen.findAllByTestId('card')
    expect(elems.length).toBe(3)
  })

  test('Open button redirects to template page', async () => {
    const view = renderApplication()
    render(view)
    const openButton = (await screen.findAllByTestId('open-template'))[0]

    await userEvent.click(openButton)
    const form = await screen.findAllByTestId('card-form')
    expect(form).toBeTruthy()
  })
})