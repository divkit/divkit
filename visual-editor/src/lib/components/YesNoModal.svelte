<script lang="ts">
  import { getContext } from "svelte";
  import {
    APP_CTX,
    isYesNoModalOpen,
    type AppContext,
  } from "../ctx/appContext";
  import toast from "svelte-french-toast";
  import type { ScreenModel } from "./models/ScreenModel";
  import { deleteScreen } from "./services/services";
  import { SetJsonCommand } from "../data/commands/setJson";

  const { state } = getContext<AppContext>(APP_CTX);

  const BASE_VIEW_JSON = JSON.stringify({
    card: {
      log_id: "div2_sample_card",
      states: [
        {
          state_id: 0,
          div: {
            items: [
              {
                items: [
                  {
                    accessibility: {
                      description: "Hello Naji",
                    },
                    font_size: 32,
                    line_height: 40,
                    text: "Hello Naji",
                    font_weight: "medium",
                    text_color: "#000",
                    type: "text",
                  },
                ],
                height: {
                  type: "match_parent",
                },
                margins: {
                  bottom: 0,
                  top: 50,
                  left: 40,
                  right: 40,
                },
                type: "container",
              },
            ],
            visibility_action: {
              log_id: "visible",
            },
            background: [
              {
                color:
                  "@{getDictOptColor('#00ffffff', local_palette, 'bg_primary', theme)}",
                type: "solid",
              },
            ],
            height: {
              type: "match_parent",
            },
            orientation: "overlap",
            type: "container",
          },
        },
      ],
      variables: [
        {
          type: "dict",
          name: "local_palette",
          value: {
            bg_primary: {
              name: "Primary background",
              light: "#fff",
              dark: "#000",
            },
            color0: {
              name: "Secondary background",
              light: "#eeeeee",
              dark: "#000",
            },
          },
        },
        {
          type: "dict",
          name: "test",
          value: {
            logged: 1,
            login: "Vasya",
            mailCount: 123,
          },
        },
      ],
    },
  });

  export let screenModel: ScreenModel | null;

  let isLoading = false;

  function onConfirm() {
    console.log(screenModel);
    if (screenModel) {
      isLoading = true;
      // Call deleteScreen from ExamplePages when 'Yes' is clicked
      deleteScreenById(screenModel).finally(() => {
        isLoading = false;
        isYesNoModalOpen.set(false); // Close the modal after the action is complete
      });
    }
  }

  async function deleteScreenById(screen: ScreenModel) {
    try {
      await deleteScreen(screen); // Assuming this function deletes the screen
      toast.success("Screen deleted successfully!");
      state.pushCommand(new SetJsonCommand(state, BASE_VIEW_JSON));
      window.location.reload();

      // Only clear selectedScreen if the deleted screen was selected
      // if ($selectedScreen?.id === screen.id) {
      //   selectedScreen.set(null);
      // }
      // if (selectedSystem) {
      //   const remainingScreens = $systemScreens[selectedSystem.id] || [];
      //   if (remainingScreens.length === 0) {
      //     selectedSystem = null;
      //   } else if (
      //     remainingScreens.length > 0 &&
      //     !remainingScreens.find((s) => s.id === $selectedScreen?.id)
      //   ) {
      //     selectedScreen.set(remainingScreens[0]);
      //     isDeleteConfirmed.set(false);
      //   }
      // }
    } catch (error) {
      toast.error("Failed to delete screen");
    }
  }

  function onCancel() {
    isYesNoModalOpen.set(false);
  }

  // const dispatch = createEventDispatcher();
</script>

{#if isLoading}
  <div class="loading">
    <p>Processing...</p>
    <div class="spinner"></div>
  </div>
{:else}
  <div class="modal" on:click={onCancel}>
    <div class="modal-content" on:click|stopPropagation>
      <div class="modal-header">
        <h2>Confirm Action</h2>
        <span class="close" on:click={onCancel}>&times;</span>
      </div>

      <div class="modal-body">
        <p>Are you sure you want to proceed?</p>
      </div>

      <div class="modal-actions">
        <button class="button" on:click={onConfirm}>Yes</button>
        <button class="button" on:click={onCancel}>No</button>
      </div>
    </div>
  </div>
{/if}

<style>
  .loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    text-align: center;
  }

  .spinner {
    margin-top: 1rem;
    width: 40px;
    height: 40px;
    border: 4px solid #ddd;
    border-top: 4px solid #333;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    from {
      transform: rotate(0deg);
    }
    to {
      transform: rotate(360deg);
    }
  }

  .modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    backdrop-filter: blur(8px);
    background-color: rgba(0, 0, 0, 0.4);
    z-index: 1000;
    display: flex;
    justify-content: center;
    align-items: center;
    animation: fadeIn 0.3s ease-out;
  }

  .modal-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    max-width: 500px;
    width: 100%;
    position: relative;
    box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
    animation: slideIn 0.4s ease-out;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .modal-header h2 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin: 0;
  }

  .close {
    font-size: 30px;
    cursor: pointer;
    color: #333;
    transition: color 0.3s;
  }

  .close:hover {
    color: #f00;
  }

  .modal-body p {
    font-size: 16px;
    color: #333;
  }

  .modal-actions {
    display: flex;
    justify-content: space-between;
  }

  .button {
    padding: 12px 20px;
    background-color: #5959e8;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }

  .button:hover {
    background-color: #4747c1;
  }

  @keyframes fadeIn {
    0% {
      opacity: 0;
    }
    100% {
      opacity: 1;
    }
  }

  @keyframes slideIn {
    0% {
      transform: translateY(-50px);
      opacity: 0;
    }
    100% {
      transform: translateY(0);
      opacity: 1;
    }
  }
</style>
