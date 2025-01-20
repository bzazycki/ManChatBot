// Get references to the select element and the body
const themeSelect = document.getElementById('theme');
const body = document.body;

// Load saved theme from local storage
document.addEventListener('DOMContentLoaded', () => {
  const savedTheme = localStorage.getItem('selectedTheme');
  if (savedTheme) {
    body.classList.add(savedTheme);
    themeSelect.value = savedTheme;
  }
});

// Listen for theme changes
themeSelect.addEventListener('change', (event) => {
  const selectedTheme = event.target.value;

  // Remove previous theme classes
  body.classList.remove('yellow', 'black', 'gold');

  // Add the new theme class
  body.classList.add(selectedTheme);

  // Save the selected theme to local storage
  localStorage.setItem('selectedTheme', selectedTheme);
});

// Optional: Add functionality for saving other settings like notifications
document.getElementById('theme-form').addEventListener('submit', (event) => {
  event.preventDefault();
  // You can add logic to save other preferences, e.g., notification settings
});
