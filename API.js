document.addEventListener("DOMContentLoaded", function () {
  // Fetch and Display Current GPT Model
  fetch('/API/getModel')
    .then(response => response.json())
    .then(data => {
      const currentModel = data.model || "No model available.";
      document.getElementById("currentModel").textContent = currentModel;
    })
    .catch(error => {
      console.error("Error fetching model:", error);
      document.getElementById("currentModel").textContent = "Failed to load model.";
    });

  // Fetch and Display Current Context
  fetch('/API/getContext')
    .then(response => response.json())
    .then(data => {
      const currentContext = data.context || "No context available.";
      document.getElementById("adminMessage").value = currentContext;
    })
    .catch(error => {
      console.error("Error fetching context:", error);
      document.getElementById("adminMessage").value = "Failed to load context.";
    });

  // Handle GPT Model Form Submission
  document.getElementById("modelForm").addEventListener("submit", function (event) {
    event.preventDefault();
    const newModel = document.getElementById("newModel").value;
    if (confirm("Are you sure you want to change the GPT model? This may affect performance and cost.")) {
      fetch('/API/updateModel', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ model: newModel }),
      })
        .then(response => {
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          return response.json();
        })
        .then(data => {
          if (data.message) {
            document.getElementById("currentModel").textContent = newModel;
            alert("GPT model has been updated successfully.");
          } else {
            alert("Failed to update model: " + data.error);
          }
        })
        .catch(error => {
          console.error("Error updating model:", error);
          alert("An error occurred while updating the model.");
        });
    }
  });
});