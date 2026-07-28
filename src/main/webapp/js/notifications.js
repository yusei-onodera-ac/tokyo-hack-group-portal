(function () {
  "use strict";

  var POLL_INTERVAL_MS = 30000;

  function refreshBadge() {
    var badge = document.getElementById("notification-badge");
    if (!badge) {
      return;
    }

    fetch("/notifications/unread-count")
        .then(function (response) { return response.ok ? response.text() : "0"; })
        .then(function (countText) {
          var count = parseInt(countText, 10) || 0;
          if (count > 0) {
            badge.textContent = count > 99 ? "99+" : String(count);
            badge.style.display = "inline-flex";
          } else {
            badge.style.display = "none";
          }
        })
        .catch(function () { /* ネットワーク一時断は無視する */ });
  }

  document.addEventListener("DOMContentLoaded", function () {
    if (!document.getElementById("notification-badge")) {
      return;
    }
    refreshBadge();
    setInterval(refreshBadge, POLL_INTERVAL_MS);
  });
})();
