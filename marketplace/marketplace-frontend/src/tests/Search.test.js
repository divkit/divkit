import { fireEvent, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event';
import { MockData, MockTemplatesApi } from '../mocks/mock';
import { renderApplication } from '../mocks/helper';
import { server } from '../mocks/server';

describe('Search templates', () => {
  beforeEach(() => {
    jest.setTimeout(20000);
  })

  afterEach(() => {
    server.resetHandlers()
  })
  beforeAll(() => {
    server.listen()
  })
  afterAll(() => server.close())

  test('Render Catalog Item page', async () => {
    const view = renderApplication('/')
    render(view)
    const title = (await screen.findByTestId('find-by-search')).querySelector('input')
    fireEvent.change(title, { target: { value: '222222' } })
    const sendButton = await screen.findByText(/send/i)
    fireEvent.click(sendButton)
    const elem = await screen.findByTestId("nothing-found")
    expect(elem).toBeTruthy()
    const backToCatalog = await screen.findByTestId("back-to-catalog")
    expect(backToCatalog.getAttribute('href')).toBe('/')
  })
})