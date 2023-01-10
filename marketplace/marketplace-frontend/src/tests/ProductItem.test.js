import { fireEvent, render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event';
import { MockData, MockTemplatesApi } from '../mocks/mock';
import { renderApplication } from '../mocks/helper';
import { server } from '../mocks/server';

describe('Product Item', () => {
  let mockProduct = new MockData().templates[0]
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
    const view = renderApplication('/template/111')
    render(view)
    const elem = await screen.findByText('Title 1')
    expect(elem).toBeTruthy()
  })
  test('Correct data', async () => {
    const tagsText = mockProduct.tags.map(t => t.tag)
    const view = renderApplication('/template/111')
    render(view)
    const button = await screen.findByTestId('show-info')
    userEvent.click(button)

    const title = (await screen.findByTestId('title')).querySelector('input')
    const description = (await screen.findByTestId('description')).querySelector('textarea')
    const tags = (await screen.findAllByTestId('tag')).map(t => t.textContent)

    expect(title.value).toBe(mockProduct.title);
    expect(description.value).toBe(mockProduct.description);
    expect(tags).toStrictEqual(tagsText);
  })

  test('Fetching tag is correct', async () => {
    const view = renderApplication('/template/111')
    render(view)
    const openButton = await screen.findByTestId('show-info')
    userEvent.click(openButton)

    const addTagButton = await screen.findByTestId('add')
    userEvent.click(addTagButton)

    const autocompleteInput = (await screen.findByTestId('autocomplete-input')).querySelector('input')
    userEvent.click(autocompleteInput)

    fireEvent.focus(autocompleteInput)
    await fireEvent.change(autocompleteInput, { target: { value: 'contain' } })

    const option = await screen.findByTestId('autocomplete-option')
    await userEvent.click(option)
    expect(autocompleteInput.value).toBe('contain')
    expect(option.textContent).toBe('container')
  })

  test('Title validation is correct', async () => {
    const view = renderApplication('/template/111')
    render(view)
    const openButton = await screen.findByTestId('show-info')
    userEvent.click(openButton)

    const title = (await screen.findByTestId('title')).querySelector('input')

    await userEvent.type(title, 'a'.repeat(31))
    const warning = screen.getByText("Too long title!")
    expect(warning).toBeTruthy()
  })

  test('Can delete tag', async () => {
    const view = renderApplication('/template/111')
    render(view)
    const openButton = await screen.findByTestId('show-info')
    await userEvent.click(openButton)

    const tagsBeforeDelete = (await screen.findAllByTestId('tag'))
    // const tagsBeforeDelete = tags.map(t => t.textContent)
    const deleteFirstTagButton = tagsBeforeDelete[0].querySelector('svg')

    await userEvent.click(deleteFirstTagButton)
    const tagsAfterDelete = (await screen.findAllByTestId('tag'))

    expect(tagsAfterDelete.length).toBe(tagsBeforeDelete.length - 1)
  })

  test('Can save template after changes', async () => {
    const view = renderApplication('/template/111')
    render(view)
    const openButton = await screen.findByTestId('show-info')
    userEvent.click(openButton)

    const title = (await screen.findByTestId('title')).querySelector('input')

    await userEvent.type(title, 'a'.repeat(10))
    const saveButton = screen.getByText(/save/i)
    expect(saveButton).toBeTruthy()
  })
})