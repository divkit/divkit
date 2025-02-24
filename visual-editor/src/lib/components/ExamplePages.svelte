<script lang="ts">
  import {
    buttonValue,
    isDeleteConfirmed,
    isYesNoModalOpen,
    showAddScreenModal,
  } from "./../ctx/appContext";
  import { getContext, onDestroy, onMount } from "svelte";
  import { capitalize } from "../utils/capitalize";
  import { ScreenModel } from "./models/ScreenModel";
  import { SystemModel } from "./models/SystemModel";
  import { faTrash } from "@fortawesome/free-solid-svg-icons";
  import { FontAwesomeIcon } from "@fortawesome/svelte-fontawesome";
  import toast from "svelte-french-toast";
  import {
    APP_CTX,
    type AppContext,
    systemsList,
    selectedScreen,
    isNewScreen,
    systemScreens,
    deletingScreen,
  } from "../ctx/appContext";
  import { SetJsonCommand } from "../data/commands/setJson";

  const { state } = getContext<AppContext>(APP_CTX);

  // const {
  //   divjsonStore,
  //   selectedLeaf,
  //   highlightLeaf,
  //   highlightElem,
  //   highlightRanges,
  //   selectedRanges,
  //   highlightLoc,
  //   readOnly,
  //   themeStore,
  // } = state;

  let selectedSystem: SystemModel | null = null;
  let systemList: SystemModel[] = [];
  let pageList: ScreenModel[] = [];
  let isLoading: boolean = true;
  let error: string | null = null;

  onMount(async () => {
    try {
      console.log("Start......");
      const response = await fetch("/screen/getSystems");

      console.log(response);
      if (!response.ok) {
        throw new Error("Failed to fetch systems");
      }

      const data: any[] = await response.json();
      systemsList.set(
        data.map(
          (item) => new SystemModel(item.id, item.systemName, item.title)
        )
      );
    } catch (err) {
      error =
        err instanceof Error
          ? err.message
          : "Failed to fetch systems from server. Please try again.";
    } finally {
      isLoading = false;
    }
  });

  function handleScreenSelect(item: ScreenModel) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.delete("id");
    const newQueryString = urlParams.toString();
    const newUrl = newQueryString
      ? `${window.location.pathname}?${newQueryString}`
      : window.location.pathname;
    window.history.replaceState({}, "", newUrl);
    buttonValue.set("Update");

    selectedScreen.set(item);
    console.log(item.jsonObject);
    console.log($selectedScreen);

    state.pushCommand(new SetJsonCommand(state, item.jsonObject));
  }

  async function handleSystemSelect(system: SystemModel) {
    selectedSystem = system;
  }
</script>

<div class="scrollable-list">
  <!-- Header with title and add button -->
  <div style="display: flex; justify-content: space-between">
    <h3 class="title">Pages</h3>
    <button
      class="btn"
      type="button"
      on:click={() => {
        showAddScreenModal.set(true);
        isNewScreen.set(true);
      }}
    >
      +
    </button>
  </div>

  <!-- Loading and error states -->
  {#if isLoading}
    <p class="loading">Loading...</p>
  {:else if error}
    <p class="error">{error}</p>
  {:else}
    <div>
      <!-- Loop through systemsList -->
      {#each $systemsList as system (system.id)}
        <div
          class="list-item"
          class:list-item_selected={selectedSystem === system}
          role="button"
          tabindex="0"
          on:click={() => handleSystemSelect(system)}
          on:keydown={(event) => {
            if (event.key === "Enter" || event.key === " ") {
              handleSystemSelect(system);
            }
          }}
        >
          <h4 class="system-name-title">
            {capitalize(system.systemName)}
          </h4>

          <!-- Check if system has associated screens -->
          {#if $systemScreens[system.id] && $systemScreens[system.id].length > 0 && selectedSystem === system}
            <ul>
              {#each $systemScreens[system.id] as systemScreen (systemScreen.id)}
                <li
                  class="child-item"
                  class:child-item_selected={$selectedScreen === systemScreen}
                >
                  <button
                    class="screen-item-button"
                    type="button"
                    on:click={() => handleScreenSelect(systemScreen)}
                    on:keydown={(event) => {
                      if (event.key === "Enter" || event.key === " ") {
                        handleScreenSelect(systemScreen);
                      }
                    }}
                  >
                    <div class="text-section">
                      <h2>{capitalize(systemScreen.screenName)}</h2>
                      <!-- <p class="screen-subtext">{systemScreen.path}</p> -->
                    </div>
                  </button>
                </li>
              {/each}
            </ul>
          {/if}
        </div>
      {/each}
    </div>
  {/if}
</div>

<style>
  .scrollable-list {
    overflow-y: auto;
    padding: 0 16px;
  }

  .title {
    margin-top: 18px;
    margin-left: 8px;
    font-size: 18px;
    font-weight: 500;
  }

  .btn {
    margin-top: 16px;
    cursor: pointer;
    font-size: 30px;
    font-weight: bolder;
    background: none; /* Remove background color */
    border: none; /* Remove border */
  }

  div {
    list-style-type: none;
    padding: 0;
    margin: 0;
  }

  .list-item {
    padding: 8px;
    cursor: default;
    font-size: 14px;
    border-bottom: 1px solid var(--fill-transparent-2);
    direction: rtl; /* If you need RTL layout, this stays */
  }

  .list-item:last-child {
    border-bottom: none;
  }

  .list-item:hover:not(.list-item_selected) {
    opacity: 0.5;
    cursor: pointer;
  }

  .list-item_selected {
    cursor: default;
    background-color: var(--bg-secondary);
    transition:
      background-color 0.15s ease-in-out,
      color 0.15s ease-in-out;
  }

  .screen-item {
    display: flex; /* Use flexbox for horizontal layout */
    justify-content: space-between; /* Distribute space between child items */
    padding: 5px; /* Reduced padding for better compactness */
    background-color: #f9f9f9; /* Light background for the container */
    border-radius: 8px; /* Optional: rounded corners */
    background: none; /* Remove background color */
    border: none; /* Remove border */
  }
  .system-name-title {
    text-align: right;
    font-size: 14px;
    font-weight: 500;
  }

  .text-section {
    display: flex;
    flex-direction: column; /* Stack screenName and path vertically */
    align-items: flex-start; /* Align to the left */
    justify-content: flex-start; /* Align items to the start */
  }

  .text-section h2 {
    width: 100%;
    text-align: left;
    font-size: 12px;
    font-weight: 400;
  }

  .screen-item-button {
    background: none; /* Remove default button background */
    border: none; /* Remove border */
    cursor: pointer; /* Change cursor to pointer on hover */
    font-size: 18px; /* Adjust icon size */
    padding: 0px; /* Add some padding for click area */
    margin-left: auto; /* Push the button to the right */
    font-weight: 300;
  }

  .screen-item-button:hover {
    color: #d93636; /* Change color on hover for better UX */
  }

  .child-item {
    padding: 4px; /* Reduce padding for a more compact look */
    font-size: 12px; /* Slightly smaller font size */
    direction: rtl; /* Maintain RTL layout */
    border-bottom: 1px solid var(--fill-transparent-2);
    list-style-type: none;
  }

  .child-item:hover:not(.child-item_selected) {
    opacity: 0.7; /* Slightly reduce opacity on hover */
    cursor: pointer;
  }

  .child-item_selected {
    cursor: default;
    background-color: var(--fill-accent-2);
    transition:
      background-color 0.15s ease-in-out,
      color 0.15s ease-in-out;
  }

  .child-item:last-child {
    border-bottom: none;
  }
</style>
