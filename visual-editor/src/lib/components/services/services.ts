import { SystemModel } from './../models/SystemModel';
import { ScreenModel } from '../models/ScreenModel';
import { systemsList } from '../../ctx/appContext';
interface LoginResponse {
  exists: boolean; // Whether the user exists
  canLogin: boolean; // Whether the user can login
  message?: string; // Optional message from the API, like error or success
}

export async function login(
  username: string,
  password: string,
): Promise<LoginResponse | null> {
  try {
    const response = await fetch('https://172.19.83.21:8443/screen/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });

    if (!response.ok) {
      throw new Error('Network response was not ok');
    }

    const data: LoginResponse = await response.json();
    return data;
  } catch (error) {
    console.error('Error during login:', error);
    return null;
  }
}

export async function saveScreen(screenModel: ScreenModel): Promise<void> {
  try {
    // console.log(JSON.stringify(screenModel))
    const response = await fetch(
      'https://172.19.83.21:8443/screen/saveScreen',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(screenModel),
      },
    );

    // Check for HTTP errors
    if (!response.ok) {
      switch (response.status) {
        case 401:
          throw new Error('Unauthorized: Please log in again.');
        case 400:
          throw new Error('Bad Request: Please check the input data.');
        case 500:
          throw new Error('Internal Server Error: Please try again later.');
        default:
          throw new Error(`Unexpected Error: ${response.statusText}`);
      }
    }

    console.log('Screen saved successfully:', response.status);
    await getSystems();
  } catch (err) {
    // Log and handle errors
    if (err instanceof Error) {
      console.error('Error while saving screen:', err.message);
      throw err; // Re-throw for caller to handle
    } else {
      console.error('Unknown error occurred:', err);
      throw new Error('An unknown error occurred while saving the screen.');
    }
  }
}

async function getSystems() {
  try {
    const response = await fetch('/screen/getSystems');

    if (!response.ok) {
      throw new Error('Failed to fetch systems');
    }

    const data: any[] = await response.json();
    const updatedSystemList = data.map(
      (item) => new SystemModel(item.id, item.systemName, item.title),
    );

    systemsList.set(updatedSystemList); // Directly update the store
  } catch (err) {
    console.error(err);
    throw new Error('Failed to fetch systems');
  }
}

export async function deleteScreen(screen: ScreenModel): Promise<void> {
  const url = `/screen/screen/${screen.id}`;
  try {
    const response = await fetch(url, {
      method: 'DELETE',
    });

    if (response.ok) {
      // Refresh the systems list
      await getSystems();
      console.log(`Screen with ID ${screen.id} deleted successfully.`);
    } else {
      const error = await response.json();
      throw new Error(`Failed to delete screen: ${error.message}`);
    }
  } catch (err) {
    console.error('Error deleting screen:', err);
    throw err;
  }
}
