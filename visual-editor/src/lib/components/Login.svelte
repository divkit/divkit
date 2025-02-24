<script>
  import { toast } from 'svelte-french-toast';
  import { login } from './services/services';
  export let onLogin;
  let username = '';
  let password = '';

  function handleLogin(event) {
    toast.dismiss();
    event.preventDefault();
    if (username && password) {
      login(username, password).then((result) => {
        if (result) {
          localStorage.setItem('loggedIn', 'true');
          onLogin(true);
        } else {
          toast.error('Invalid credentials. Please try again.');
        }
      });
    } else {
      toast.error('Complete all fields.');
    }
  }
</script>

<div class="login-container">
  <form class="login-form" on:submit={handleLogin}>
    <label for="username">Username</label>
    <input
      type="text"
      id="username"
      bind:value={username}
      placeholder="Enter username"
    />

    <label for="password">Password</label>
    <input
      type="password"
      id="password"
      bind:value={password}
      placeholder="Enter password"
    />

    <button type="submit">Login</button>
  </form>
</div>

<style>
  /* Center the login container */
  .login-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100vh;
    background-color: #f5f5f5;
  }

  /* Style the login form */
  .login-form {
    width: 300px;
    padding: 2rem;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1);
  }

  /* Style labels and inputs */
  label {
    display: block;
    font-size: 0.9rem;
    margin-bottom: 0.5rem;
    color: #333;
  }

  input {
    width: 100%;
    padding: 0.5rem;
    margin-bottom: 1rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
  }

  /* Style the submit button */
  button {
    width: 100%;
    padding: 0.6rem;
    background-color: #5959e8;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }

  button:hover {
    background-color: #5959e8;
  }
</style>
