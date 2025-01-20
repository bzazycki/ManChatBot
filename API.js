/**
 * This file contains the client-side JavaScript for the Admin page, allowing for the user to see and update
 * the GPT model, API key, and context sections that are then updated/sent in the database.
 * 
 * This file interacts heavily with the gpt_api.py file in the /home/ubuntu/ManChatBot/server-side directory.
 * It receives and sends data to the API and updates the front end accordingly.
 * 
 * Date Last Edited: 21/1/2025
 * Authors: Ethan Armbruster, Zach Clouse
 * Contact: armbrue2@miamioh.edu
 */

$(document).ready(function () {

  /**
   * / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
   * Function Definitions for getting the information for the Admin page.
   * These functions include:
   * - fetchCurrentModel() - Fetches and displays the current GPT model
   * - fetchAvailableModels() - Fetches and populates the available models using the OpenAI API
   * - fetchApiKey() - Fetches and displays the current API key
   * - fetchContextSections() - Fetches and displays the context sections
   * 
   * These functions are called when the page is loaded and after the user interacts with the page, updating it with the newer, relevat info. 
   * \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \
   */


  /**
   * Fetch and Display Current GPT Model
   */
  function fetchCurrentModel() {
    $.get('/API/getModel')
      .done(function (data) {
        const currentModel = data.model || "No model available.";
        $("#currentModel").text(currentModel);
      })
      .fail(function () {
        console.error("Error fetching model.");
        $("#currentModel").text("Failed to load model.");
      });
  }

  /**
   * Fetch and Populate Available Models using the OpenAi API, used in conjunction with the fetchCurrentModel() function
   */
  function fetchAvailableModels() {
    $.get('/API/getAvailableModels')
      .done(function (data) {
        const models = data.models || [];
        const modelSelect = $("#newModel");
        modelSelect.empty();

        // Add initial "Choose a Model" option
        const initialOption = $("<option>").val("").text("Choose a Model").prop("disabled", true).prop("selected", true);
        modelSelect.append(initialOption);

        // Add options for each model
        models.forEach(model => {
          const option = $("<option>").val(model).text(model);
          modelSelect.append(option);
        });
      })
      .fail(function () {
        console.error("Error fetching available models.");
      });
  }

  /**
   * Fetch and Display Current API Key
   */
  function fetchApiKey() {
    $.get('/API/getAPIKey')
      .done(function (data) {
        const apiKey = data.apiKey || "No API key available.";
        $("#currentApiKey").text(apiKey);
      })
      .fail(function () {
        console.error("Error fetching API key.");
        $("#currentApiKey").text("Failed to load API key.");
      });
  }

  /**
   * Fetch and Display Context Sections
   */
  function fetchContextSections() {
    $.get('/API/getContextSections?getAll=true')
      .done(function (data) {
        const sections = data.contextSections || [];
        const sectionsContainer = $('#sectionsContainer');
        sectionsContainer.empty(); // Clear existing sections

        // Add each section to the container
        sections.forEach(section => {
          addSection(section.id, section.active, section.contextHeader, section.contextText);
        });
      })
      .fail(function () {
        console.error("Error fetching context sections.");
      });
  }

  // Initial fetch of API key, models, and context sections
  fetchApiKey();
  fetchCurrentModel();
  fetchAvailableModels();
  fetchContextSections();

  /**
   * / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / / /
   * Function definitions for updating the front end, as well as the functionality for the context with adding, removing, and updating sections.
   * These functions include:
   * - handleFormSubmission() - Handles form submission for updating the GPT model and API key
   * - addSection() - Adds a new section or updates an existing section
   * - Listener for the update API key form submission
   * - Listener for the update model form submission
   * \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \
   */

  /**
   * Handle Form Submission
   * @param {Event} event - The form submission event
   * @param {string} url - The URL to send the request to
   * @param {Object} body - The body of the request
   * @param {string} confirmMessage - The confirmation message to display
   * @param {string} successMessage - The success message to display
   * @param {string} errorMessage - The error message to display
   * @param {string} updateElementId - The ID of the element to update
   */
  function handleFormSubmission(event, url, body, confirmMessage, successMessage, errorMessage, updateElementId) {
    event.preventDefault();
    if (confirm(confirmMessage)) {
      $.ajax({
        url: url,
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(body)
      })
        .done(function (data) {
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
        .fail(function () {
          console.error(errorMessage);
          alert("An error occurred while updating.");
        });
    }
  }

  // Handle GPT Model Form Submission
  $("#modelForm").on("submit", function (event) {
    const newModel = $("#newModel").val();
    handleFormSubmission(event, '/API/updateModel', { model: newModel }, "Are you sure you want to change the GPT model? This may affect performance and cost.", "GPT model has been updated successfully.", "Failed to update model", "currentModel");
  });

  // Handle API Key Form Submission
  const apiKeyForm = $("#apiKeyForm");
  if (apiKeyForm.length) {
    apiKeyForm.on("submit", function (event) {
      const newApiKey = $("#newApiKey").val();

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

  /**
   * Add a new section or update an existing section
   * @param {string|null} id - The ID of the section (null for new sections)
   * @param {boolean} active - Whether the section is active
   * @param {string} header - The header of the section
   * @param {string} content - The content of the section
   */
  function addSection(id = null, active = true, header = '', content = '') {
    const sectionContainer = $("<div>").addClass('section');
    if (id) {
      sectionContainer.data('id', id);
    }

    const sectionHeader = $("<div>").addClass('section-header');

    const activeLabel = $("<label>").text('Active:');
    const activeCheckbox = $("<input>").attr('type', 'checkbox').prop('checked', active);

    const headerLabel = $("<label>").text('Header:');
    const headerInput = $("<input>").attr('type', 'text').attr('placeholder', 'Header text').val(header);

    const removeButton = $("<button>").text('Remove').css({
      backgroundColor: 'red',
      color: 'white',
      border: 'none',
      padding: '5px 10px',
      marginLeft: '10px',
      cursor: 'pointer',
      borderRadius: '5px'
    }).on('click', function (event) {
      event.stopPropagation(); // Prevent form submission
      if (id) {
        $.ajax({
          url: '/API/removeContextSection',
          method: 'DELETE',
          contentType: 'application/json',
          data: JSON.stringify({ id: id })
        })
          .done(function (data) {
            if (data.message) {
              sectionContainer.remove();
              alert("Section removed successfully.");
            } else {
              alert("Failed to remove section: " + data.error);
            }
          })
          .fail(function () {
            console.error("Error removing section.");
            alert("An error occurred while removing the section.");
          });
      } else {
        sectionContainer.remove();
      }
    });

    // Creating the submit button for each section
    const submitButton = $("<button>").text('Submit').css({
      backgroundColor: '#000',
      color: '#ffc107',
      border: 'none',
      padding: '5px 10px',
      marginLeft: '10px',
      cursor: 'pointer',
      borderRadius: '5px'
    }).on('click', function (event) {
      event.preventDefault(); // Prevent form submission
      const active = activeCheckbox.prop('checked');
      const header = headerInput.val();
      const content = sectionContainer.find("textarea").val();

      $.ajax({
        url: '/API/upsertContextSection',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ id: id, contextHeader: header, active: active, contextText: content })
      })
        .done(function (data) {
          if (data.message) {
            alert("Section updated successfully.");
            fetchContextSections(); // Reload the information
          } else {
            alert("Failed to update section: " + data.error);
          }
        })
        .fail(function () {
          console.error("Error updating section.");
          alert("An error occurred while updating the section.");
        });
    });

    // Add everything to each section
    sectionHeader.append(activeLabel, activeCheckbox, headerLabel, headerInput, removeButton, submitButton);

    const sectionContent = $("<div>").addClass('section-content');
    const contentLabel = $("<label>").text('Content:');
    const contentTextarea = $("<textarea>").attr('placeholder', 'Information').val(content);

    sectionContent.append(contentLabel, contentTextarea);
    sectionContainer.append(sectionHeader, sectionContent);

    $('#sectionsContainer').append(sectionContainer);
  }

  // Add event listener for the add section button
  $("#addSectionBtn").on("click", function () {
    addSection();
  });

  // Handle Context Form Submission
  $("#contextForm").on("submit", function (event) {
    event.preventDefault();
    const sections = $(".section");
    const promises = [];

    sections.each(function () {
      const section = $(this);
      const id = section.data('id');
      const active = section.find("input[type='checkbox']").prop('checked');
      const header = section.find("input[type='text']").val();
      const content = section.find("textarea").val();

      const promise = $.ajax({
        url: '/API/upsertContextSection',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ id: id, contextHeader: header, active: active, contextText: content })
      })
        .done(function (data) {
          if (!id && data.id) {
            section.data('id', data.id); // Set the new ID for the section
          }
          if (!data.message) {
            throw new Error(data.error || 'An error occurred');
          }
        })
        .fail(function () {
          console.error('Error updating section.');
          alert('An error occurred while updating the section.');
        });

      promises.push(promise);
    });

    $.when.apply($, promises)
      .done(function () {
        alert('All sections updated successfully.');
        fetchContextSections(); // Reload the information
      })
      .fail(function () {
        console.error('Error updating sections.');
        alert('An error occurred while updating the sections.');
      });
  });
});
