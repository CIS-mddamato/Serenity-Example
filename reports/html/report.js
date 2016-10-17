$(document).ready(function() {var formatter = new CucumberHTML.DOMFormatter($('.cucumber-report'));formatter.uri("Manage_Holiday_API_Tests.feature");
formatter.feature({
  "line": 1,
  "name": "Manage_Holiday_API_Tests",
  "description": "Criteria:\r\nHoliday can be added to site,district,regional and national level\r\nOnce holiday is added, it can be visible in list of holidays. It is also visible in view schedules pertaining to the sites",
  "id": "manage-holiday-api-tests",
  "keyword": "Feature"
});
formatter.scenario({
  "line": 7,
  "name": "Manage Holiday for domestic site, get, add, edit and remove holidays",
  "description": " User Stories\r\n[ NASSIP-56 ]",
  "id": "manage-holiday-api-tests;manage-holiday-for-domestic-site,-get,-add,-edit-and-remove-holidays",
  "type": "scenario",
  "keyword": "Scenario",
  "tags": [
    {
      "line": 6,
      "name": "@API"
    },
    {
      "line": 6,
      "name": "@Manage_Holiday_API_Tests"
    }
  ]
});
});