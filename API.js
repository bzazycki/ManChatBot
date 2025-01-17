document.addEventListener("DOMContentLoaded", function () {
  // Fetch and Display Current GPT Model
  function fetchCurrentModel() {
    fetch('/API/getModel')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        const currentModel = data.model || "No model available.";
        document.getElementById("currentModel").textContent = currentModel;
      })
      .catch(error => {
        console.error("Error fetching model:", error);
        document.getElementById("currentModel").textContent = "Failed to load model.";
      });
  }

  // Fetch and Populate Available Models
  function fetchAvailableModels() {
    fetch('/API/getAvailableModels')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        const models = data.models || [];
        const modelSelect = document.getElementById("newModel");
        modelSelect.innerHTML = ""; // Clear existing options

        // Add initial "Choose a Model" option
        const initialOption = document.createElement("option");
        initialOption.value = "";
        initialOption.textContent = "Choose a Model";
        initialOption.disabled = true;
        initialOption.selected = true;
        modelSelect.appendChild(initialOption);

        models.forEach(model => {
          const option = document.createElement("option");
          option.value = model;
          option.textContent = model;
          modelSelect.appendChild(option);
        });
      })
      .catch(error => {
        console.error("Error fetching available models:", error);
      });
  }

  // Fetch and Display Current Context
  fetch('/API/getContext')
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      return response.json();
    })
    .then(data => {
      const currentContext = data.context || "No context available.";
      document.getElementById("adminMessage").value = currentContext;
    })
    .catch(error => {
      console.error("Error fetching context:", error);
      document.getElementById("adminMessage").value = "Failed to load context.";
    });

  // Fetch and Display Current API Key
  function fetchApiKey() {
    fetch('/API/getAPIKey')
      .then(response => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then(data => {
        const apiKey = data.apiKey || "No API key available.";
        document.getElementById("currentApiKey").textContent = apiKey;
      })
      .catch(error => {
        console.error("Error fetching API key:", error);
        document.getElementById("currentApiKey").textContent = "Failed to load API key.";
      });
  }

  // Initial fetch of API key and models
  fetchApiKey();
  fetchCurrentModel();
  fetchAvailableModels();

  // Handle Form Submission
  function handleFormSubmission(event, url, body, confirmMessage, successMessage, errorMessage, updateElementId) {
    event.preventDefault();
    if (confirm(confirmMessage)) {
      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      })
        .then(response => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.json();
        })
        .then(data => {
          if (data.message) {
            alert(successMessage);
            if (updateElementId === "currentApiKey") {
              fetchApiKey(); // Fetch the updated API key
            } else if (updateElementId === "currentModel") {
              fetchCurrentModel(); // Fetch the updated model
            }
          } else {
            alert(errorMessage + ": " + data.error);
          }
        })
        .catch(error => {
          console.error(errorMessage, error);
          alert("An error occurred while updating.");
        });
    }
  }

  // Handle GPT Model Form Submission
  document.getElementById("modelForm").addEventListener("submit", function (event) {
    const newModel = document.getElementById("newModel").value;
    handleFormSubmission(event, '/API/updateModel', { model: newModel }, "Are you sure you want to change the GPT model? This may affect performance and cost.", "GPT model has been updated successfully.", "Failed to update model", "currentModel");
  });

  // Ensure event listeners are not added multiple times
  const apiKeyForm = document.getElementById("apiKeyForm");
  if (apiKeyForm) {
    apiKeyForm.addEventListener("submit", function (event) {
      const newApiKey = document.getElementById("newApiKey").value;

      // Validate API key
      if (!newApiKey.startsWith("sk-proj-")) {
        alert("Error: The API key must start with 'sk-proj-'.");
        return;
      }
      if (newApiKey.length !== 164) {
        alert("Error: The API key must be exactly 164 characters long.");
        return;
      }

      handleFormSubmission(
        event,
        '/API/updateAPIKey',
        { apiKey: newApiKey },
        "This will change the API Key of the program and might result in functionality issues. Are you sure you would like to proceed?",
        "API key has been updated successfully.",
        "Failed to update API key",
        "currentApiKey"
      );
    });
  }
});