import { render, screen } from '@testing-library/react'
import { renderApplication } from '../mocks/helper';
import userEvent from '@testing-library/user-event';

describe('Create template', () => {
  beforeEach(() => {
    jest.setTimeout(20000);

  })
  test('Title is mock', async () => {
    const view = renderApplication('/')
    render(view)
    const createTemplateButton = screen.getByTestId('create-template')
    await userEvent.click(createTemplateButton)

    const baseTitle = (await screen.findByTestId('title')).querySelector('input')
    expect(baseTitle.getAttribute('value')).toBe("New Template")
  })

  test('New template doesn\'t have tags', async () => {
    const view = renderApplication('/')
    render(view)
    const createTemplateButton = screen.getByTestId('create-template')
    await userEvent.click(createTemplateButton)

    const noTagsMsg = screen.getByText("No tags yet, you can add some")
    expect(noTagsMsg).toBeTruthy()
  })

})
