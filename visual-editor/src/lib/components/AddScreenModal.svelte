<script lang="ts">
  import { getContext, createEventDispatcher } from "svelte";
  import toast, { Toaster } from "svelte-french-toast";
  import { saveScreen } from "./services/services";
  import { ScreenModel } from "./models/ScreenModel";
  import type { SystemModel } from "./models/SystemModel";
  import { SetJsonCommand } from "../data/commands/setJson";

  import {
    APP_CTX,
    type AppContext,
    systemsList,
    showAddScreenModal,
    isNewScreen,
    selectedScreen,
  } from "../ctx/appContext";

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
  let isAddScreenModalOpen = false;
  let dropdownOptions = systemsList;
  let selectedOption: SystemModel | null = null;
  let screenPath = "";
  let screenName = "";
  let isLoading = false;
  const dispatch = createEventDispatcher();

  const { state } = getContext<AppContext>(APP_CTX);
  const { divjsonStore } = state;

  showAddScreenModal.subscribe((value) => {
    isAddScreenModalOpen = value;
  });

  if (selectedOption === null && $dropdownOptions.length > 0) {
    selectedOption = $dropdownOptions[0];
  }

  function openAddScreenModal(): void {
    showAddScreenModal.set(true);
  }

  async function submitAddScreen() {
    if (!(selectedOption && screenName && screenPath)) {
      toast.error("Please fill all required fields.");
      console.log("Validation failed:", {
        selectedOption,
        screenName,
        screenPath,
      });
      return;
    }
    isLoading = true; // Show loading indicator
    console.log("Calling saveScreen with:", {
      selectedOption,
      screenName,
      screenPath,
    });

    try {
      const urlParams = new URLSearchParams(window.location.search);
      let tempScreenId = urlParams.get("id");

      let json = $divjsonStore.fullString;
      if (isNewScreen && !tempScreenId) {
        json = BASE_VIEW_JSON;
      }
      const m = new ScreenModel(
        null,
        screenPath,
        json,
        new Date().toISOString(),
        screenName,
        selectedOption.id,
        []
      );
      console.log("ScreenModel:", m);

      const saveResult = await saveScreen(m);
      state.pushCommand(new SetJsonCommand(state, BASE_VIEW_JSON));
      console.dir(saveResult);
      dispatch("showToast", {
        message: "Screen created successfully!",
        type: "success",
      });
      // isNewScreen ? goToNewScreen(saveResult.id, saveResult.idSystem) : null;
      isNewScreen.set(false);
      setTimeout(() => {
        isLoading = false;
        closeAddPageModal();
      }, 1000);
    } catch (error) {
      isLoading = false;
      console.error("Save screen failed:", error);
      dispatch("showToast", { message: error, type: "error" });
    }
  }
  function closeAddPageModal(): void {
    showAddScreenModal.set(false);
    isNewScreen.set(false);
  }

  function handleBackgroundClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (target.classList.contains("modal")) {
      closeAddPageModal();
    }
  }
  // async function goToNewScreen(id: string, systemId: number) {
  //   const screenModels = await fetchScreenBySystem(systemId);

  //   if (!Array.isArray(screenModels)) {
  //     console.error("Invalid data returned, expected an array:", screenModels);
  //     return;
  //   }

  //   const screen = screenModels.find((screen) => screen.id === id);

  //   if (screen) {
  //     selectedScreen.set(screen);
  //     console.log("Selected screen:", screen);
  //   } else {
  //     console.error("Screen not found for id:", id);
  //   }
  // }

  // async function fetchScreenBySystem(id: number): Promise<ScreenModel[]> {
  //   const screenPath = `/screen/getScreenBySystem/${id}`;
  //   try {
  //     const response = await fetch(screenPath);

  //     if (!response.ok) {
  //       throw new Error(`Failed to fetch screens: ${response.statusText}`);
  //     }

  //     const data: any[] = await response.json();

  //     // Map and validate to ensure it matches ScreenModel[]
  //     const screens: ScreenModel[] = data.map((item) => ({
  //       id: item.id,
  //       screenName: item.screenName,
  //     }));

  //     return screens;
  //   } catch (error) {
  //     console.error("Error fetching screens:", error);
  //     return []; // Return an empty array if an error occurs
  //   }
  // }
</script>

{#if isAddScreenModalOpen}
  <div class="modal" on:click={handleBackgroundClick}>
    <div class="modal-content">
      {#if isLoading}
        <!-- Loading Indicator -->
        <div class="loading">
          <p>Creating Screen...</p>
          <div class="spinner"></div>
        </div>
      {:else}
        <div class="modal-header">
          <h2>Add New Screen</h2>
          <span class="close" on:click={closeAddPageModal}>&times;</span>
        </div>

        <div class="modal-body">
          <label class="label" for="screenName">Screen Name</label>
          <input
            id="screenName"
            class="inp"
            type="text"
            placeholder="Enter Screen Name"
            bind:value={screenName}
          />

          <label class="label" for="screenPath">Path</label>
          <input
            id="screenPath"
            class="inp"
            type="text"
            placeholder="Enter Path"
            bind:value={screenPath}
          />

          <label class="label" for="optionSelect">Select System</label>
          <select
            id="optionSelect"
            class="dropdown"
            bind:value={selectedOption}
            on:change={() => console.log("Selected Option:", selectedOption)}
          >
            {#each $dropdownOptions as option}
              <option value={option}>{option.systemName}</option>
            {/each}
          </select>

          <button class="button" on:click={submitAddScreen}
            >Create Screen</button
          >
        </div>
      {/if}
    </div>
    <Toaster position="top-center" />
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

  .inp {
    width: 100%;
    padding: 12px 8px;
    margin: 10px 0;
    font-size: 16px;
    border-radius: 4px;
    border: 1px solid #ccc;
    box-sizing: border-box;
  }

  .label {
    font-size: 14px;
    margin-bottom: 5px;
    display: block;
    color: #333;
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

  .dropdown {
    width: 100%;
    padding: 12px 8px;
    margin: 10px 0;
    font-size: 16px;
    border-radius: 4px;
    border: 1px solid #ccc;
    box-sizing: border-box;
  }

  .button {
    padding: 14px;
    background-color: #5959e8;
    color: white;
    border: none;
    border-radius: 4px;
    width: 100%;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }

  .button:hover {
    background-color: #5959e8;
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
